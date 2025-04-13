package com.club_vibe.app_be.integration.payment;

import com.club_vibe.app_be.common.enums.Country;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.events.repository.EventRepository;
import com.club_vibe.app_be.helpers.EventTestHelper;
import com.club_vibe.app_be.integration.config.TestPaymentData;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.artist.repository.ArtistRepository;
import com.club_vibe.app_be.users.club.entity.ClubAddress;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.club.repository.ClubRepository;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.entity.StripeDetails;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.club_vibe.app_be.stripe.payments.controller.PaymentProcessingController.BASE_URL;

/**
 * Integration test class for Stripe payment processing.
 * This uses Stripe's test environment and test cards.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class StripePaymentIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private EventEntity savedEvent;
    private ClubEntity savedClub;
    private ArtistEntity savedArtist;
    @LocalServerPort
    private int port;

    String baseUrl = "http://localhost:" + port + "/api/payments/";
    private static final String STRIPE_TEST_SECRET_KEY = "sk_test_51NyGEjA25s7vmUeUpE7YvRKU6oKqFTddp2ZYVpIU5PwCF7gouYZJtayEYWThmzR5NvUMKZTokc9KvQcd576T0JsB00FWzrkn7r";

    @BeforeAll
    public static void setup() {
        // Initialize Stripe with test API key
        Stripe.apiKey = STRIPE_TEST_SECRET_KEY;
    }

    @BeforeEach
    void setUp() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            // Create StripeDetails for entities
            StripeDetails clubStripeDetails = new StripeDetails();
            clubStripeDetails.setAccountId(TestPaymentData.TEST_CLUB_ACCOUNT_ID);
            clubStripeDetails.setKycStatus(KycStatus.VERIFIED);

            StripeDetails artistStripeDetails = new StripeDetails();
            artistStripeDetails.setAccountId(TestPaymentData.TEST_ARTIST_ACCOUNT_ID);
            artistStripeDetails.setKycStatus(KycStatus.VERIFIED);

            // Create and save Club
            ClubEntity club = new ClubEntity();
            club.setEmail("club@example.com");
            club.setPassword("password");
            club.setRole(StaffRole.CLUB);
            club.setStripeDetails(clubStripeDetails);
            club.setCountry(Country.BG);
            club.setName("Test Club");

            ClubAddress address = new ClubAddress();
            address.setStreet("123 Test St");
            address.setCity("Test City");
            club.setAddress(address);

            entityManager.persist(club);
            savedClub = club;

            // Create and save Artist
            ArtistEntity artist = new ArtistEntity();
            artist.setEmail("artist@example.com");
            artist.setPassword("password");
            artist.setRole(StaffRole.ARTIST);
            artist.setStripeDetails(artistStripeDetails);
            artist.setCountry(Country.BG);
            artist.setStageName("Test Artist");

            entityManager.persist(artist);
            savedArtist = artist;

            // Create and save Event
            EventEntity event = new EventEntity();
            event.setStartTime(LocalDateTime.now().minusHours(1));
            event.setEndTime(LocalDateTime.now().plusHours(2));
            event.setConditions(EventTestHelper.createDefaultEventConditionsEntity());
            event.setActive(true);
            event.setClub(savedClub);
            event.setArtist(savedArtist);

            entityManager.persist(event);
            savedEvent = event;
            return null;
        });
    }


    @Test
    public void testFullPaymentFlow() throws Exception {
        // 1. Create a test payment method
        String paymentMethodId = TestPaymentData.createTestPaymentMethod(TestPaymentData.TEST_CARD_SUCCESS);

        System.out.println("Created payment method: " + paymentMethodId);  // Debug log

        // 2. Create the authorize request
        AuthorizePaymentRequest authRequest = TestPaymentData.createTestAuthorizeRequest(paymentMethodId, savedEvent.getId());

        // 3. Set up headers with idempotency key
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String idempotencyKey = UUID.randomUUID().toString();
        headers.set("Idempotency-Key", idempotencyKey);

        // 4. Make the authorize request
        HttpEntity<AuthorizePaymentRequest> authEntity = new HttpEntity<>(authRequest, headers);
        String fullUrl = "http://localhost:" + port + "/api/payments/authorize";

        ResponseEntity<AuthorizePaymentResponse> authResponse = restTemplate.exchange(
                fullUrl,
                HttpMethod.POST,
                authEntity,
                AuthorizePaymentResponse.class
        );

        // 5. Verify the auth response
        assert authResponse.getStatusCode() == HttpStatus.OK;
        assert authResponse.getBody().paymentIntentId() != null;

        // 6. Set up headers for capture with new idempotency key
        HttpHeaders captureHeaders = new HttpHeaders();
        captureHeaders.setContentType(MediaType.APPLICATION_JSON);
        captureHeaders.set("Idempotency-Key", UUID.randomUUID().toString());

        // 7. Make the capture request
        HttpEntity<Void> captureEntity = new HttpEntity<>(null, captureHeaders);
        ResponseEntity<Void> captureResponse = restTemplate.exchange(
                BASE_URL + "/" + authResponse.getBody().paymentIntentId() + "/capture",
                HttpMethod.POST,
                captureEntity,
                Void.class
        );

        // 8. Verify the capture response
        assert captureResponse.getStatusCode() == HttpStatus.OK;

        // 9. Verify the payment in Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(authResponse.getBody().paymentIntentId());
        assert "succeeded".equals(paymentIntent.getStatus());
    }

    @Test
    public void testAuthenticationRequiredFlow() throws Exception {
        // Similar to above but with TEST_CARD_REQUIRES_AUTH
        String paymentMethodId = TestPaymentData.createTestPaymentMethod(TestPaymentData.TEST_CARD_REQUIRES_AUTH);
        // Continue test implementation...
    }

    @Test
    public void testDeclinedCardFlow() throws Exception {
        // Test with a card that will be declined
        String paymentMethodId = TestPaymentData.createTestPaymentMethod(TestPaymentData.TEST_CARD_DECLINE);
        // Continue test implementation expecting an error response...
    }

    @Test
    public void testDuplicatePaymentWithSameIdempotencyKey() throws Exception {
        // Test that sending the same request twice with the same idempotency key
        // only results in one payment
        String idempotencyKey = UUID.randomUUID().toString();
        // Continue test implementation...
    }

    @Test
    public void testCaptureAlreadyCapturedPayment() throws Exception {
        // Test attempting to capture a payment that's already been captured
        // Should return the appropriate error
    }
}