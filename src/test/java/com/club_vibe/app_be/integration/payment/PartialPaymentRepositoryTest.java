package com.club_vibe.app_be.integration.payment;

import com.club_vibe.app_be.common.enums.Country;
import com.club_vibe.app_be.events.entity.EventConditionsEntity;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.events.entity.RequestSettings;
import com.club_vibe.app_be.events.repository.EventRepository;
import com.club_vibe.app_be.helpers.EventTestHelper;
import com.club_vibe.app_be.request.entity.RequestEntity;
import com.club_vibe.app_be.request.entity.RequestType;
import com.club_vibe.app_be.request.repository.RequestRepository;
import com.club_vibe.app_be.stripe.balance.dto.artist.ArtistPaymentDto;
import com.club_vibe.app_be.stripe.balance.dto.club.ClubEarningDto;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;
import com.club_vibe.app_be.stripe.payments.entity.partial.PartialPaymentEntity;
import com.club_vibe.app_be.stripe.payments.entity.partial.PaymentAllocationType;
import com.club_vibe.app_be.stripe.payments.repository.PartialPaymentRepository;
import com.club_vibe.app_be.stripe.payments.repository.PaymentRepository;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.artist.repository.ArtistRepository;
import com.club_vibe.app_be.users.club.entity.ClubAddress;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.club.repository.ClubRepository;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.entity.StripeDetails;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class PartialPaymentRepositoryTest {

    @Autowired
    private PartialPaymentRepository partialPaymentRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    private ArtistEntity artist;
    private ClubEntity club;
    private EventEntity event;
    private RequestEntity request;
    private PaymentEntity payment;


    @BeforeEach
    void setUp() {
        setupTestData();
    }

    @Test
    void findArtistPaymentsByMonth_shouldReturnCorrectPayments() {
        // Given
        LocalDateTime startOfMonth = LocalDateTime.now().minusDays(10).withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Create artist payment allocation
        PartialPaymentEntity artistPayment = createPartialPayment(
                PaymentAllocationType.ARTIST,
                BigDecimal.valueOf(50.00),
                "USD"
        );
        partialPaymentRepository.save(artistPayment);

        // When
        List<ArtistPaymentDto> result = partialPaymentRepository.findArtistPaymentsByMonth(
                artist.getStripeDetails().getAccountId(),
                PaymentAllocationType.ARTIST,
                startOfMonth
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        ArtistPaymentDto dto = result.getFirst();
        assertThat(dto.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(dto.getCurrency()).isEqualTo("USD");
        assertThat(dto.getStatus()).isEqualTo(payment.getStatus());
    }

    @Test
    void findClubEarningsByMonth_shouldReturnCorrectEarnings() {
        // Given
        LocalDateTime startOfMonth = LocalDateTime.now().minusDays(10).withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Create club payment allocations
        PartialPaymentEntity clubPayment1 = createPartialPayment(
                PaymentAllocationType.CLUB,
                BigDecimal.valueOf(30.00),
                "USD"
        );
        PartialPaymentEntity clubPayment2 = createPartialPayment(
                PaymentAllocationType.CLUB,
                BigDecimal.valueOf(20.00),
                "USD"
        );
        partialPaymentRepository.save(clubPayment1);
        partialPaymentRepository.save(clubPayment2);

        // When
        List<ClubEarningDto> result = partialPaymentRepository.findClubEarningsByMonth(
                club.getStripeDetails().getAccountId(),
                PaymentAllocationType.CLUB,
                startOfMonth
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        ClubEarningDto dto = result.getFirst();
        assertThat(dto.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50.00)); // 30 + 20
        assertThat(dto.getCurrencyCode()).isEqualTo("USD");
        assertThat(dto.getPaymentCount()).isEqualTo(2L);
    }

    private void setupTestData() {
        // Create artist
        StripeDetails artistStripeDetails = StripeDetails.builder()
                .accountId("artist_" + UUID.randomUUID().toString())
                .kycStatus(KycStatus.VERIFIED)
                .build();

        artist = ArtistEntity.builder()
                .email("artist@test.com")
                .password("password")
                .role(StaffRole.ARTIST)
                .stripeDetails(artistStripeDetails)
                .country(Country.BG)
                .stageName("Test Artist")
                .build();
        artistRepository.save(artist);

        // Create club
        ClubAddress clubAddress = ClubAddress.builder()
                .street("123 Test St")
                .city("Test City")
                .build();

        StripeDetails clubStripeDetails = StripeDetails.builder()
                .accountId("club_" + UUID.randomUUID().toString())
                .kycStatus(KycStatus.VERIFIED)
                .build();

        club = ClubEntity.builder()
                .email("club@test.com")
                .password("password")
                .role(StaffRole.CLUB)
                .stripeDetails(clubStripeDetails)
                .country(Country.BG)
                .name("Test Club")
                .address(clubAddress)
                .build();
        clubRepository.save(club);

        // Create event
        event = new EventEntity();
        event.setStartTime(LocalDateTime.now().plusDays(7));
        event.setEndTime(LocalDateTime.now().plusDays(7).plusHours(3));
        event.setConditions(EventTestHelper.createDefaultEventConditionsEntity());
        event.setActive(true);
        event.setClub(club);
        event.setArtist(artist);
        eventRepository.save(event);

        // Create request
        request = new RequestEntity();
        request.setType(RequestType.SONG);
        request.setTitle("Test Request");
        request.setMessage("This is a test request");
        request.setGuestEmail("guest@test.com");
        request.setEvent(event);
        requestRepository.save(request);

        // Create payment
        payment = new PaymentEntity();
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setCurrency("USD");
        payment.setStripePaymentIntentId("pi_" + UUID.randomUUID());
        payment.setTimestamp(LocalDateTime.now());
        payment.setStatus(StripePaymentStatus.AUTHENTICATED);
        payment.setRequest(request);
        paymentRepository.save(payment);
    }

    private PartialPaymentEntity createPartialPayment(PaymentAllocationType type, BigDecimal amount, String currency) {
        return PartialPaymentEntity.builder()
                .type(type)
                .amount(amount)
                .currency(currency)
                .allocationDate(LocalDateTime.now())
                .payment(payment)
                .description("Test payment allocation")
                .build();
    }
}