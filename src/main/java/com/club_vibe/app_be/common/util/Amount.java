package com.club_vibe.app_be.common.util;

import com.club_vibe.app_be.common.embedable.MoneyAmount;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.stripe.model.PaymentIntent;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import static com.club_vibe.app_be.common.util.DefaultPlatformValues.PLATFORM_CURRENCY;

/**
 * Represents an amount in a specific currency, handling formatting and calculations.
 *
 * TODO test methods
 */
public class Amount {
    @Getter
    private final BigDecimal value;
    private final Currency currency;
    private final int decimals;

    private Amount(BigDecimal value, Currency currency) {
        this.currency = currency;
        this.decimals = currency.getDefaultFractionDigits();
        this.value = value.setScale(decimals, RoundingMode.HALF_UP);
    }

    public static Amount fromAuthRequest(AuthorizePaymentRequest request) {
        return new Amount(BigDecimal.valueOf(request.amount()), Currency.getInstance(request.currency()));
    }

    /**
     * Creates an Amount from a BigDecimal and Currency.
     * Assumes the value is expressed in major units (dollars, euros, etc.).
     *
     * @param value     the payment amount as {@link BigDecimal}.
     * @param currency  of the payment as {@link Currency} in format ISO 4217.
     */
    public static Amount of(BigDecimal value, Currency currency) {
        return new Amount(value, currency);
    }

    /**
     * Creates an Amount from a double and a currency code
     * The {@link Currency} class handles the initialization of its object and the validation of the data.
     * The expected currency string is expected to be in format ISO 4217
     *
     * @param value the payment amount as {@link Double}.
     * @param currencyCode {@link String} value of the currency code.
     */
    public static Amount of(Double value, String currencyCode) {
        return new Amount(BigDecimal.valueOf(value), Currency.getInstance(currencyCode));
    }

    /**
     * Creates an Amount from a double and a currency code
     * The {@link Currency} class handles the initialization of its object and the validation of the data.
     * The expected currency string is expected to be in format ISO 4217
     *
     * @param value the payment amount as {@link BigDecimal}.
     * @param currencyCode {@link String} value of the currency code.
     */
    public static Amount of(BigDecimal value, String currencyCode) {
        return new Amount(value, Currency.getInstance(currencyCode));
    }

    /**
     * Creates an Amount from the smallest currency unit (e.g., cents).
     * Converts cents to major unit by moving the decimal point left.
     * The {@link Currency} class handles the initialization of its object and the validation of the data.
     * The expected currency string is expected to be in format ISO-4217.
     *
     * @param cents         {@link Long} the payment amount in cents.
     * @param currencyStr   The currency of the payment as {@link String}.
     */
    public static Amount fromCents(Long cents, String currencyStr) {
        Currency currency = Currency.getInstance(currencyStr.toUpperCase());
        BigDecimal majorValue = BigDecimal.valueOf(cents).movePointLeft(currency.getDefaultFractionDigits());
        return new Amount(majorValue, currency);
    }

    /**
     * Creates an Amount from a {@link PaymentIntent}.
     * Assumes that the Stripe function PaymentIntent.getAmount() returns the amount in cents.
     * The {@link Currency} class handles the initialization of its object and the validation of the data.
     * The expected currency string is expected to be in format ISO-4217.
     *
     * @param paymentIntent {@link PaymentIntent} object returned by Stripe after initializing a payment.
     */
    public static Amount fromPaymentIntent(PaymentIntent paymentIntent) {
        long cents = paymentIntent.getAmount();
        return fromCents(cents, paymentIntent.getCurrency());
    }

    /**
     * Converts the amount to cents (or the smallest currency unit).
     *
     * @return The amount in cents.
     */
    public Long toCents() {
        return this.value.movePointRight(this.decimals).longValueExact();
    }

    /**
     * Returns the string currency code of the amount.
     *
     * @return {@link String}
     */
    public String getCurrency() {
        return currency.getCurrencyCode();
    }

    /**
     * Formats the amount according to the currency and locale.
     */
    public String format(Locale locale) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setCurrency(currency);
        return formatter.format(value);
    }

    /**
     * Splits the amount based on a percentage.
     *
     * @param percentage The percentage to calculate.
     * @return The split amount.
     */
    public Amount calculatePercentage(BigDecimal percentage) {
        BigDecimal percentageValue = value.multiply(percentage).divide(BigDecimal.valueOf(100), decimals, RoundingMode.HALF_UP);
        return new Amount(percentageValue, currency);
    }

    /**
     * Converts the amount to another currency using an exchange rate.
     *
     * @param targetCurrency The target currency.
     * @param exchangeRate   The exchange rate (1 unit of current currency to target currency).
     * @return Converted amount.
     */
    public Amount convertTo(Currency targetCurrency, BigDecimal exchangeRate) {
        BigDecimal convertedValue = value.multiply(exchangeRate);
        return new Amount(convertedValue, targetCurrency);
    }

    public MoneyAmount toMoneyAmount() {
        return MoneyAmount.builder()
                .amount(this.value)
                .currency(this.currency.getCurrencyCode())
                .build();
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + value.setScale(decimals, RoundingMode.HALF_UP);
    }
}
