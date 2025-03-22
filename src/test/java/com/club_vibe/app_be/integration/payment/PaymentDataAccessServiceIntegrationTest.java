package com.club_vibe.app_be.integration.payment;

import com.club_vibe.app_be.common.enums.Country;
import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.integration.config.TestPaymentData;
import com.club_vibe.app_be.request.entity.RequestEntity;
import com.club_vibe.app_be.request.entity.RequestType;
import com.club_vibe.app_be.stripe.payments.dto.CreatePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;
import com.club_vibe.app_be.stripe.payments.repository.PaymentRepository;
import com.club_vibe.app_be.stripe.payments.service.PaymentDataAccessService;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.club.entity.ClubAddress;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.entity.StripeDetails;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class PaymentDataAccessServiceIntegrationTest {

    @Autowired
    private PaymentDataAccessService paymentDataAccessService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private EntityManager entityManager;

    private RequestEntity savedRequest;
    private EventEntity savedEvent;
    private ClubEntity savedClub;
    private ArtistEntity savedArtist;

    @BeforeEach
    void setUp() {
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
        event.setArtistPercentage(new BigDecimal("60.00"));
        event.setClubPercentage(new BigDecimal("20.00"));
        event.setActive(true);
        event.setClub(savedClub);
        event.setArtist(savedArtist);

        entityManager.persist(event);
        savedEvent = event;

        // Create and save Request
        RequestEntity request = new RequestEntity();
        request.setType(RequestType.SONG);
        request.setTitle("Test Song");
        request.setMessage("Please play this song");
        request.setGuestEmail("guest@example.com");
        request.setEvent(savedEvent);

        entityManager.persist(request);
        savedRequest = request;

        entityManager.flush();
    }

    @Test
    void createPayment_ShouldCreateAndSavePayment() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        Currency currency = Currency.getInstance("BGN");
        Amount paymentAmount = Amount.of(amount, currency);
        CreatePaymentRequest request = new CreatePaymentRequest(paymentAmount, savedRequest.getId(), currency);

        // Act
        PaymentEntity result = paymentDataAccessService.createPayment(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(amount, result.getAmount());
        assertEquals(currency.getCurrencyCode(), result.getCurrency());
        assertEquals(StripePaymentStatus.CREATED, result.getStatus());
        assertNotNull(result.getTimestamp());
        assertEquals(savedRequest.getId(), result.getRequest().getId());
        assertNull(result.getStripePaymentIntentId());

        // Verify it was saved to the database
        Optional<PaymentEntity> savedEntity = paymentRepository.findById(result.getId());
        assertTrue(savedEntity.isPresent());
    }

    @Test
    void createPayment_WithNullRequestId_ShouldCreatePaymentWithoutRequest() {
        // Arrange
        BigDecimal amount = new BigDecimal("50.00");
        Currency currency = Currency.getInstance("BGN");
        Amount paymentAmount = Amount.of(amount, currency);
        CreatePaymentRequest request = new CreatePaymentRequest(paymentAmount, null, currency);

        // Act
        PaymentEntity result = paymentDataAccessService.createPayment(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(amount, result.getAmount());
        assertEquals(currency.getCurrencyCode(), result.getCurrency());
        assertEquals(StripePaymentStatus.CREATED, result.getStatus());
        assertNull(result.getRequest());
    }

    @Test
    void updatePaymentAfterAuthentication_ShouldUpdatePaymentDetails() {
        // Arrange
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(new BigDecimal("200.00"));
        payment.setCurrency("BGN");
        payment.setStatus(StripePaymentStatus.CREATED);
        entityManager.persist(payment);
        entityManager.flush();

        String paymentIntentId = "pi_test_123456789";
        StripePaymentStatus newStatus = StripePaymentStatus.AUTHENTICATED;
        AuthorizePaymentResponse authResponse = new AuthorizePaymentResponse(paymentIntentId, null, false, newStatus);

        // Act
        paymentDataAccessService.updatePaymentAfterAuthentication(payment, authResponse);

        // Assert
        PaymentEntity updatedPayment = paymentRepository.findById(payment.getId()).orElse(null);
        assertNotNull(updatedPayment);
        assertEquals(paymentIntentId, updatedPayment.getStripePaymentIntentId());
        assertEquals(newStatus, updatedPayment.getStatus());
    }

    @Test
    void getPaymentSplitData_WithValidPaymentIntent_ShouldReturnValidSplitDetails() {
        // Arrange - Create a payment with all necessary relationships
        PaymentEntity payment = new PaymentEntity();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("BGN");
        payment.setStatus(StripePaymentStatus.AUTHENTICATED);
        payment.setStripePaymentIntentId("pi_test_valid_split");
        payment.setRequest(savedRequest);

        entityManager.persist(payment);
        entityManager.flush();

        // Act
        try {
            PaymentSplitDetails result = paymentDataAccessService.getPaymentSplitData("pi_test_valid_split");

            // Assert
            assertNotNull(result);
            assertEquals(TestPaymentData.TEST_CLUB_ACCOUNT_ID, result.clubConnectedAccountId());
            assertEquals(TestPaymentData.TEST_ARTIST_ACCOUNT_ID, result.artistConnectedAccountId());
            assertEquals(new BigDecimal("20.00"), result.clubPercentage());
            assertEquals(new BigDecimal("60.00"), result.artistPercentage());
        } catch (Exception e) {
            fail("Test setup for getPaymentSplitData needs to be adjusted: " + e.getMessage());
        }
    }

    @Test
    void getPaymentSplitData_WithInvalidPaymentIntent_ShouldThrowItemNotFoundException() {
        String nonExistentPaymentIntentId = "pi_non_existent";

        assertThrows(ItemNotFoundException.class, () -> {
            paymentDataAccessService.getPaymentSplitData(nonExistentPaymentIntentId);
        });
    }

    @Test
    void getPaymentSplitData_WithMissingClubAccountId_ShouldThrowIllegalStateException() {
        // Arrange
        String paymentIntentId = "pi_test_missing_club";

        // Create the club without a connected account ID
        ClubEntity clubWithoutAccount = new ClubEntity();
        clubWithoutAccount.setEmail("club_no_account@example.com");
        clubWithoutAccount.setPassword("password");
        clubWithoutAccount.setRole(StaffRole.CLUB);
        clubWithoutAccount.setStripeDetails(new StripeDetails()); // Empty stripe details
        clubWithoutAccount.setCountry(Country.BG);
        clubWithoutAccount.setName("Club Without Account");

        ClubAddress address = new ClubAddress();
        address.setStreet("123 Empty St");
        address.setCity("Test City");
        clubWithoutAccount.setAddress(address);

        entityManager.persist(clubWithoutAccount);

        // Create an event with this club
        EventEntity eventWithClubNoAccount = new EventEntity();
        eventWithClubNoAccount.setStartTime(LocalDateTime.now());
        eventWithClubNoAccount.setEndTime(LocalDateTime.now().plusHours(2));
        eventWithClubNoAccount.setArtistPercentage(new BigDecimal("60.00"));
        eventWithClubNoAccount.setClubPercentage(new BigDecimal("20.00"));
        eventWithClubNoAccount.setActive(true);
        eventWithClubNoAccount.setClub(clubWithoutAccount);
        eventWithClubNoAccount.setArtist(savedArtist);

        entityManager.persist(eventWithClubNoAccount);

        // Create a request for this event
        RequestEntity requestWithClubNoAccount = new RequestEntity();
        requestWithClubNoAccount.setType(RequestType.SONG);
        requestWithClubNoAccount.setTitle("Test Song");
        requestWithClubNoAccount.setMessage("Please play this song");
        requestWithClubNoAccount.setGuestEmail("guest@example.com");
        requestWithClubNoAccount.setEvent(eventWithClubNoAccount);

        entityManager.persist(requestWithClubNoAccount);

        // Create a payment with this request
        PaymentEntity paymentWithClubNoAccount = new PaymentEntity();
        paymentWithClubNoAccount.setAmount(new BigDecimal("100.00"));
        paymentWithClubNoAccount.setCurrency("BGN");
        paymentWithClubNoAccount.setStatus(StripePaymentStatus.AUTHENTICATED);
        paymentWithClubNoAccount.setStripePaymentIntentId(paymentIntentId);
        paymentWithClubNoAccount.setRequest(requestWithClubNoAccount);

        entityManager.persist(paymentWithClubNoAccount);
        entityManager.flush();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentDataAccessService.getPaymentSplitData(paymentIntentId);
        });

        assertTrue(exception.getMessage().contains("Club connected account ID is missing"));
    }

    @Test
    void getPaymentSplitData_WithMissingArtistAccountId_ShouldThrowIllegalStateException() {
        // Arrange
        String paymentIntentId = "pi_test_missing_artist";

        // Create the artist without a connected account ID
        ArtistEntity artistWithoutAccount = new ArtistEntity();
        artistWithoutAccount.setEmail("artist_no_account@example.com");
        artistWithoutAccount.setPassword("password");
        artistWithoutAccount.setRole(StaffRole.ARTIST);
        artistWithoutAccount.setStripeDetails(new StripeDetails()); // Empty stripe details
        artistWithoutAccount.setCountry(Country.BG);
        artistWithoutAccount.setStageName("Artist Without Account");

        entityManager.persist(artistWithoutAccount);

        // Create an event with this artist
        EventEntity eventWithArtistNoAccount = new EventEntity();
        eventWithArtistNoAccount.setStartTime(LocalDateTime.now());
        eventWithArtistNoAccount.setEndTime(LocalDateTime.now().plusHours(2));
        eventWithArtistNoAccount.setArtistPercentage(new BigDecimal("30.00"));
        eventWithArtistNoAccount.setClubPercentage(new BigDecimal("70.00"));
        eventWithArtistNoAccount.setActive(true);
        eventWithArtistNoAccount.setClub(savedClub); // Use existing club
        eventWithArtistNoAccount.setArtist(artistWithoutAccount);

        entityManager.persist(eventWithArtistNoAccount);

        // Create a request for this event
        RequestEntity requestWithArtistNoAccount = new RequestEntity();
        requestWithArtistNoAccount.setType(RequestType.SONG);
        requestWithArtistNoAccount.setTitle("Test Song");
        requestWithArtistNoAccount.setMessage("Please play this song");
        requestWithArtistNoAccount.setGuestEmail("guest@example.com");
        requestWithArtistNoAccount.setEvent(eventWithArtistNoAccount);

        entityManager.persist(requestWithArtistNoAccount);

        // Create a payment with this request
        PaymentEntity paymentWithArtistNoAccount = new PaymentEntity();
        paymentWithArtistNoAccount.setAmount(new BigDecimal("100.00"));
        paymentWithArtistNoAccount.setCurrency("BGN");
        paymentWithArtistNoAccount.setStatus(StripePaymentStatus.AUTHENTICATED);
        paymentWithArtistNoAccount.setStripePaymentIntentId(paymentIntentId);
        paymentWithArtistNoAccount.setRequest(requestWithArtistNoAccount);

        entityManager.persist(paymentWithArtistNoAccount);
        entityManager.flush();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentDataAccessService.getPaymentSplitData(paymentIntentId);
        });

        assertTrue(exception.getMessage().contains("Artist connected account ID is missing"));
    }

    @Test
    void getPaymentSplitData_WithInvalidPercentages_ShouldThrowValidationException() {
        // Arrange
        String paymentIntentId = "pi_test_invalid_percentages";

        // Create an event with invalid percentages (sum > 100%)
        EventEntity eventWithInvalidPercentages = new EventEntity();
        eventWithInvalidPercentages.setStartTime(LocalDateTime.now());
        eventWithInvalidPercentages.setEndTime(LocalDateTime.now().plusHours(2));
        eventWithInvalidPercentages.setArtistPercentage(new BigDecimal("60.00"));
        eventWithInvalidPercentages.setClubPercentage(new BigDecimal("50.00"));
        eventWithInvalidPercentages.setActive(true);
        eventWithInvalidPercentages.setClub(savedClub);
        eventWithInvalidPercentages.setArtist(savedArtist);

        entityManager.persist(eventWithInvalidPercentages);

        RequestEntity requestWithInvalidPercentages = new RequestEntity();
        requestWithInvalidPercentages.setType(RequestType.SONG);
        requestWithInvalidPercentages.setTitle("Test Song");
        requestWithInvalidPercentages.setMessage("Please play this song");
        requestWithInvalidPercentages.setGuestEmail("guest@example.com");
        requestWithInvalidPercentages.setEvent(eventWithInvalidPercentages);

        entityManager.persist(requestWithInvalidPercentages);

        PaymentEntity paymentWithInvalidPercentages = new PaymentEntity();
        paymentWithInvalidPercentages.setAmount(new BigDecimal("100.00"));
        paymentWithInvalidPercentages.setCurrency("BGN");
        paymentWithInvalidPercentages.setStatus(StripePaymentStatus.AUTHENTICATED);
        paymentWithInvalidPercentages.setStripePaymentIntentId(paymentIntentId);
        paymentWithInvalidPercentages.setRequest(requestWithInvalidPercentages);

        entityManager.persist(paymentWithInvalidPercentages);
        entityManager.flush();

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            paymentDataAccessService.getPaymentSplitData(paymentIntentId);
        });

        assertTrue(exception.getMessage().contains("percentage") ||
                exception.getMessage().contains("Percentage"));
    }
}