package com.club_vibe.app_be.integration.payment;

import com.club_vibe.app_be.common.embedable.MoneyAmount;
import com.club_vibe.app_be.common.enums.Country;
import com.club_vibe.app_be.common.enums.PayoutStatus;
import com.club_vibe.app_be.stripe.payout.entity.PayoutEntity;
import com.club_vibe.app_be.stripe.payout.repository.PayoutRepository;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.club.entity.ClubAddress;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import com.club_vibe.app_be.users.staff.entity.StripeDetails;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/*
- Configures an in-memory database
- Auto-configures JPA repositories
- Sets up TestEntityManager
- Disables full auto-configuration
 */
@DataJpaTest
public class PayoutRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PayoutRepository payoutRepository;

    private StaffEntity artist;
    private StaffEntity club;
    private PayoutEntity artistPayout1;
    private PayoutEntity artistPayout2;
    private PayoutEntity clubPayout1;
    private PayoutEntity clubPayout2;

    @BeforeEach
    public void setup() {
        // Create test staff
        artist = ArtistEntity.builder()
                .email("artist@example.com")
                .password("password")
                .role(StaffRole.ARTIST)
                .country(Country.BG)
                .stageName("artist")
                .stripeDetails(
                        StripeDetails.builder()
                                .accountId("stripe_acc_artist")
                                .kycStatus(KycStatus.VERIFIED)
                                .build())
                .build();
        entityManager.persist(artist);

        club = ClubEntity.builder()
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

        entityManager.persist(club);

        // Create test payouts
        artistPayout1 = new PayoutEntity();
        artistPayout1.setMoneyAmount(new MoneyAmount(new BigDecimal("100.00"), "USD"));
        artistPayout1.setStripePayoutId("po_stripe_id_1");
        artistPayout1.setStatus(PayoutStatus.PENDING);
        artistPayout1.setStaff(artist);
        entityManager.persist(artistPayout1);

        artistPayout2 = new PayoutEntity();
        artistPayout2.setMoneyAmount(new MoneyAmount(new BigDecimal("200.00"), "USD"));
        artistPayout2.setStripePayoutId("po_stripe_id_2");
        artistPayout2.setStatus(PayoutStatus.COMPLETED);
        artistPayout2.setStaff(artist);
        entityManager.persist(artistPayout2);

        clubPayout1 = new PayoutEntity();
        clubPayout1.setMoneyAmount(new MoneyAmount(new BigDecimal("300.00"), "EUR"));
        clubPayout1.setStripePayoutId("po_stripe_id_3");
        clubPayout1.setStatus(PayoutStatus.FAILED);
        clubPayout1.setStaff(club);
        entityManager.persist(clubPayout1);

        clubPayout2 = new PayoutEntity();
        clubPayout2.setMoneyAmount(new MoneyAmount(new BigDecimal("400.00"), "GBP"));
        clubPayout2.setStripePayoutId("po_stripe_id_4");
        clubPayout2.setStatus(PayoutStatus.PENDING);
        clubPayout2.setStaff(club);
        entityManager.persist(clubPayout2);

        entityManager.flush();
    }

    @Test
    public void testFindByStaffIdOrderByIdDesc() {
        // Test for artist
        List<PayoutEntity> artistPayouts = payoutRepository.findByStaffIdOrderByIdDesc(artist.getId());

        assertThat(artistPayouts).hasSize(2);
        assertThat(artistPayouts.get(0).getId()).isGreaterThan(artistPayouts.get(1).getId()); // Verify descending order
        assertThat(artistPayouts).extracting(PayoutEntity::getStaff).extracting(StaffEntity::getId)
                .containsOnly(artist.getId());

        // Test for club
        List<PayoutEntity> staff2Payouts = payoutRepository.findByStaffIdOrderByIdDesc(club.getId());

        assertThat(staff2Payouts).hasSize(2);
        assertThat(staff2Payouts.get(0).getId()).isGreaterThan(staff2Payouts.get(1).getId()); // Verify descending order
        assertThat(staff2Payouts).extracting(PayoutEntity::getStaff).extracting(StaffEntity::getId)
                .containsOnly(club.getId());
    }

    @Test
    public void testFindByStatus() {
        // Test for PENDING status
        List<PayoutEntity> pendingPayouts = payoutRepository.findByStatus(PayoutStatus.PENDING);

        assertThat(pendingPayouts).hasSize(2);
        assertThat(pendingPayouts).extracting(PayoutEntity::getStatus)
                .containsOnly(PayoutStatus.PENDING);
        assertThat(pendingPayouts).extracting(PayoutEntity::getStripePayoutId)
                .containsExactlyInAnyOrder("po_stripe_id_1", "po_stripe_id_4");

        // Test for COMPLETED status
        List<PayoutEntity> paidPayouts = payoutRepository.findByStatus(PayoutStatus.COMPLETED);

        assertThat(paidPayouts).hasSize(1);
        assertThat(paidPayouts).extracting(PayoutEntity::getStatus)
                .containsOnly(PayoutStatus.COMPLETED);
        assertThat(paidPayouts).extracting(PayoutEntity::getStripePayoutId)
                .containsExactly("po_stripe_id_2");

        // Test for FAILED status
        List<PayoutEntity> failedPayouts = payoutRepository.findByStatus(PayoutStatus.FAILED);

        assertThat(failedPayouts).hasSize(1);
        assertThat(failedPayouts).extracting(PayoutEntity::getStatus)
                .containsOnly(PayoutStatus.FAILED);
        assertThat(failedPayouts).extracting(PayoutEntity::getStripePayoutId)
                .containsExactly("po_stripe_id_3");

        // Test for non-existent status
        List<PayoutEntity> canceledPayouts = payoutRepository.findByStatus(PayoutStatus.CANCELED);

        assertThat(canceledPayouts).isEmpty();
    }

    @Test
    public void testFindByStripePayoutId() {
        // Test for existing Stripe payout ID
        Optional<PayoutEntity> foundPayout = payoutRepository.findByStripePayoutId("po_stripe_id_1");

        assertThat(foundPayout).isPresent();
        assertThat(foundPayout.get().getStripePayoutId()).isEqualTo("po_stripe_id_1");
        assertThat(foundPayout.get().getStatus()).isEqualTo(PayoutStatus.PENDING);
        assertThat(foundPayout.get().getMoneyAmount().getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(foundPayout.get().getMoneyAmount().getCurrency()).isEqualTo("USD");

        // Test for non-existent Stripe payout ID
        Optional<PayoutEntity> notFoundPayout = payoutRepository.findByStripePayoutId("non_existent_id");

        assertThat(notFoundPayout).isEmpty();
    }

    @Test
    public void testBasicCrudOperations() {
        // Test count
        long count = payoutRepository.count();
        assertThat(count).isEqualTo(4);

        // Test findById
        Optional<PayoutEntity> foundPayout = payoutRepository.findById(artistPayout1.getId());
        assertThat(foundPayout).isPresent();
        assertThat(foundPayout.get().getId()).isEqualTo(artistPayout1.getId());

        // Test save (update)
        PayoutEntity payoutToUpdate = foundPayout.get();
        payoutToUpdate.setStatus(PayoutStatus.COMPLETED);
        payoutRepository.save(payoutToUpdate);

        PayoutEntity updatedPayout = entityManager.find(PayoutEntity.class, artistPayout1.getId());
        assertThat(updatedPayout.getStatus()).isEqualTo(PayoutStatus.COMPLETED);

        // Test delete
        payoutRepository.delete(payoutToUpdate);
        Optional<PayoutEntity> deletedPayout = payoutRepository.findById(artistPayout1.getId());
        assertThat(deletedPayout).isEmpty();

        // Verify count after deletion
        long newCount = payoutRepository.count();
        assertThat(newCount).isEqualTo(3);
    }

    @Test
    public void testFindAll() {
        List<PayoutEntity> allPayouts = payoutRepository.findAll();

        assertThat(allPayouts).hasSize(4);
        assertThat(allPayouts).extracting(PayoutEntity::getStripePayoutId)
                .containsExactlyInAnyOrder("po_stripe_id_1", "po_stripe_id_2", "po_stripe_id_3", "po_stripe_id_4");
    }
}