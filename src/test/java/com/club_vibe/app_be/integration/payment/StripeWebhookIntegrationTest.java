package com.club_vibe.app_be.integration.payment;
import com.club_vibe.app_be.common.embedable.MoneyAmount;
import com.club_vibe.app_be.common.enums.Country;
import com.club_vibe.app_be.common.enums.PayoutStatus;
import com.club_vibe.app_be.stripe.payout.entity.PayoutEntity;
import com.club_vibe.app_be.stripe.payout.repository.PayoutRepository;
import com.club_vibe.app_be.stripe.webhook.service.StripeWebhookService;
import com.club_vibe.app_be.users.club.entity.ClubAddress;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.entity.StripeDetails;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.stripe.model.Payout;
import com.stripe.net.ApiResource;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class StripeWebhookIntegrationTest {

    @Autowired
    private MockMvc mockMvc; //TODO use TestRestTemplate
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PayoutRepository payoutRepository;
    @Autowired
    private StripeWebhookService stripeWebhookService;
    @Autowired
    private EntityManager entityManager;

    private ClubEntity testClub;
    private PayoutEntity testPayout;
    private String stripePayoutId = "po_test123456789";

    @BeforeEach
    public void setup() {
        // Create test data
        testClub = ClubEntity.builder()
                .email("club@example.com")
                .password("password")
                .role(StaffRole.CLUB)
                .country(Country.BG)
                .address(
                        ClubAddress.builder()
                                .city("City")
                                .street("Street 1")
                                .build())
                .name("club")
                .stripeDetails(
                        StripeDetails.builder()
                                .accountId("stripe_acc_club")
                                .kycStatus(KycStatus.VERIFIED)
                                .build())
                .build();

        // Save the staff
        entityManager.persist(testClub);

        // Create a test payout in PENDING status
        testPayout = new PayoutEntity();
        testPayout.setStaff(testClub);
        testPayout.setStripePayoutId(stripePayoutId);
        testPayout.setStatus(PayoutStatus.PENDING);
        testPayout.setMoneyAmount(new MoneyAmount(new BigDecimal("100.00"), "USD"));

        // Save the payout
        entityManager.persist(testPayout);
        entityManager.flush();
    }

    @Test
    public void testPayoutPaidWebhook() throws Exception {
        // Create a real Payout object using Stripe's library
        Payout payout = new Payout();
        payout.setId(stripePayoutId);
        payout.setObject("payout");
        payout.setStatus("paid");
        payout.setAmount(10000L); // $100.00 in cents
        payout.setCurrency("usd");

        // Create a real Event object using Stripe's library
        String eventJson = createStripeEventJson("payout.paid", payout);

        // Send a mock webhook request
        mockMvc.perform(post("/webhook/stripe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "t=123456,v1=mock_signature")
                        .content(eventJson))
                .andExpect(status().isOk());

        // Verify the payout status was updated in the database
        PayoutEntity updatedPayout = entityManager.find(PayoutEntity.class, testPayout.getId());
        assertThat(updatedPayout.getStatus()).isEqualTo(PayoutStatus.COMPLETED);
    }

    @Test
    public void testPayoutFailedWebhook() throws Exception {
        // Create a real Payout object using Stripe's library
        Payout payout = new Payout();
        payout.setId(stripePayoutId);
        payout.setObject("payout");
        payout.setStatus("failed");
        payout.setAmount(10000L);
        payout.setCurrency("usd");
        payout.setFailureCode("insufficient_funds");
        payout.setFailureMessage("The account has insufficient funds to make this payout");

        // Create a real Event object using Stripe's library
        String eventJson = createStripeEventJson("payout.failed", payout);

        // Send a mock webhook request
        mockMvc.perform(post("/webhook/stripe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "t=123457,v1=mock_signature")
                        .content(eventJson))
                .andExpect(status().isOk());

        // Verify the payout status was updated in the database
        PayoutEntity updatedPayout = entityManager.find(PayoutEntity.class, testPayout.getId());
        assertThat(updatedPayout.getStatus()).isEqualTo(PayoutStatus.FAILED);
    }

    @Test
    public void testMultipleStatusChanges() throws Exception {
        // First change to in_transit
        Payout transitPayout = new Payout();
        transitPayout.setId(stripePayoutId);
        transitPayout.setObject("payout");
        transitPayout.setStatus("in_transit");
        transitPayout.setAmount(10000L);
        transitPayout.setCurrency("usd");

        String transitEventJson = createStripeEventJson("payout.updated", transitPayout);

        mockMvc.perform(post("/webhook/stripe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "t=123458,v1=mock_signature")
                        .content(transitEventJson))
                .andExpect(status().isOk());

        // Verify intermediate status
        entityManager.clear(); // Clear persistence context to force a fresh load
        PayoutEntity midPayout = entityManager.find(PayoutEntity.class, testPayout.getId());
        assertThat(midPayout.getStatus()).isEqualTo(PayoutStatus.IN_TRANSIT);

        // Then change to paid
        Payout paidPayout = new Payout();
        paidPayout.setId(stripePayoutId);
        paidPayout.setObject("payout");
        paidPayout.setStatus("paid");
        paidPayout.setAmount(10000L);
        paidPayout.setCurrency("usd");

        String paidEventJson = createStripeEventJson("payout.paid", paidPayout);

        mockMvc.perform(post("/webhook/stripe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "t=123459,v1=mock_signature")
                        .content(paidEventJson))
                .andExpect(status().isOk());

        // Verify final status
        entityManager.clear(); // Clear persistence context to force a fresh load
        PayoutEntity finalPayout = entityManager.find(PayoutEntity.class, testPayout.getId());
        assertThat(finalPayout.getStatus()).isEqualTo(PayoutStatus.COMPLETED);
    }

    /**
     * Creates a JSON string for a Stripe event using real Stripe objects
     * This ensures the JSON structure matches what Stripe would actually send
     */
    private String createStripeEventJson(String eventType, ApiResource stripeObject) {
        // Convert the Stripe object to its JSON representation
        String objectJson = ApiResource.GSON.toJson(stripeObject);

        // Create the event JSON structure
        JsonObject eventJson = new JsonObject();
        eventJson.addProperty("id", "evt_test" + System.currentTimeMillis());
        eventJson.addProperty("object", "event");
        eventJson.addProperty("type", eventType);
        eventJson.addProperty("api_version", "2022-11-15");

        // Add the data object
        JsonObject dataJson = new JsonObject();
        dataJson.add("object", ApiResource.GSON.fromJson(objectJson, JsonObject.class));
        eventJson.add("data", dataJson);

        return eventJson.toString();
    }
}