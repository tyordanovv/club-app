package com.club_vibe.app_be.stripe.mapper;

import com.club_vibe.app_be.stripe.balance.dto.artist.BalanceFunds;
import com.stripe.model.Balance;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class StripeBalanceMapper {
    /**
     * Maps a Stripe balance fund to our DTO
     * This avoids using reflection and handles specific Stripe DTO types
     */
    public BalanceFunds mapAvailableToBalanceFunds(Balance.Available balanceAmount) {
        Long amountInCents = balanceAmount.getAmount();
        String currency = balanceAmount.getCurrency();

        BigDecimal amountFormatted = BigDecimal.valueOf(amountInCents)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return new BalanceFunds(currency, amountInCents, amountFormatted);
    }

    /**
     * Maps a Stripe balance fund to our DTO
     * This avoids using reflection and handles specific Stripe DTO types
     */
    public BalanceFunds mapPendingToBalanceFunds(Balance.Pending balanceAmount) {
        Long amountInCents = balanceAmount.getAmount();
        String currency = balanceAmount.getCurrency();

        BigDecimal amountFormatted = BigDecimal.valueOf(amountInCents)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return new BalanceFunds(currency, amountInCents, amountFormatted);
    }
}
