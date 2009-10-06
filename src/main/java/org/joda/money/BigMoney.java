/*
 *  Copyright 2009 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An amount of money in a specific currency with a controllable scale.
 * <p>
 * An instance of money holds an amount in a currency.
 * <p>
 * Every currency has a certain number of decimal places.
 * This is typically 2 (Euro, British Pound, US Dollar) but might be
 * 0 (Japanese Yen), 1 (Vietnamese Dong) or 3 (Bahrain Dinar).
 * This becomes the scale of the created money, unless an alternate precision
 * is chosen.
 * <p>
 * This class can store and manipulate the amount at any precision by using
 * a {@link BigDecimal} internally.
 * <p>
 * BigMoney is immutable and thread-safe.
 */
public final class BigMoney implements BigMoneyProvider, Comparable<BigMoneyProvider>, Serializable {

    /**
     * The serialisation version.
     */
    private static final long serialVersionUID = 723473581L;
    /**
     * The currency, not null.
     */
    private final CurrencyUnit iCurrency;
    /**
     * The amount, not null.
     */
    private final BigDecimal iAmount;

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>BigMoney</code> in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * The scale of the money will be that of the BigDecimal.
     *
     * @param currency  the currency, not null
     * @param amount  the amount of money, not null
     * @return the new instance, never null
     */
    public static BigMoney of(CurrencyUnit currency, BigDecimal amount) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        MoneyUtils.checkNotNull(amount, "Amount must not be null");
        if (amount.getClass() != BigDecimal.class) {
            amount = new BigDecimal(amount.unscaledValue(), amount.scale());
        }
        return new BigMoney(currency, amount);
    }

    /**
     * Gets an instance of <code>BigMoney</code> in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * The scale of the money will be that of the BigDecimal.
     *
     * @param currencyCode  the currency code, not null
     * @param amount  the amount of money, not null
     * @return the new instance, never null
     * @throws IllegalArgumentException if the currency is unknown
     */
    public static BigMoney of(String currencyCode, BigDecimal amount) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        MoneyUtils.checkNotNull(amount, "Amount must not be null");
        return BigMoney.of(CurrencyUnit.of(currencyCode), amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>BigMoney</code> in the specified currency,
     * using a well-defined conversion from a <code>double</code>.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * <p>
     * The amount is converted via {@link BigDecimal#valueOf(double)} which yields
     * the most expected answer for most programming scenarios.
     * Any <code>double</code> literal in code will be converted to
     * exactly the same BigDecimal with the same scale.
     * For example, the literal '1.425d' will be converted to '1.425'.
     * The scale of the money will be that of the BigDecimal produced.
     *
     * @param currency  the currency, not null
     * @param amount  the amount of money, not null
     * @return the new instance, never null
     */
    public static BigMoney of(CurrencyUnit currency, double amount) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        return BigMoney.of(currency, BigDecimal.valueOf(amount));
    }

    /**
     * Gets an instance of <code>BigMoney</code> in the specified currency,
     * using a well-defined conversion from a <code>double</code>.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * <p>
     * The amount is converted via {@link BigDecimal#valueOf(double)} which yields
     * the most expected answer for most programming scenarios.
     * Any <code>double</code> literal in code will be converted to
     * exactly the same BigDecimal with the same scale.
     * For example, the literal '1.425d' will be converted to '1.425'.
     * The scale of the money will be that of the BigDecimal produced.
     *
     * @param currencyCode  the currency code, not null
     * @param amount  the amount of money, not null
     * @return the new instance, never null
     * @throws IllegalArgumentException if the currency is unknown
     */
    public static BigMoney of(String currencyCode, double amount) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        return BigMoney.of(CurrencyUnit.of(currencyCode), amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>BigMoney</code> in the specified currency,
     * using the scale of the currency rounding as necessary.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * The scale of the money will be that of the currency, such as 2 for USD or 0 for JPY.
     * If the BigDecimal has excess fractional digits, they are rounded using the rounding mode.
     *
     * @param currency  the currency, not null
     * @param amount  the amount of money, not null
     * @param roundingMode  the rounding mode to use, not null
     * @return the new instance, never null
     * @throws ArithmeticException if the rounding fails
     */
    public static BigMoney ofCurrencyScale(CurrencyUnit currency, BigDecimal amount, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        MoneyUtils.checkNotNull(amount, "Amount must not be null");
        amount = amount.setScale(currency.getDecimalPlaces(), roundingMode);
        return BigMoney.of(currency, amount);
    }

    /**
     * Gets an instance of <code>BigMoney</code> in the specified currency,
     * using the scale of the currency rounding as necessary.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * The scale of the money will be that of the currency, such as 2 for USD or 0 for JPY.
     * If the BigDecimal has excess fractional digits, they are rounded using the rounding mode.
     *
     * @param currencyCode  the currency code, not null
     * @param amount  the amount of money, not null
     * @param roundingMode  the rounding mode to use, not null
     * @return the new instance, never null
     * @throws ArithmeticException if the rounding fails
     */
    public static BigMoney ofCurrencyScale(String currencyCode, BigDecimal amount, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        MoneyUtils.checkNotNull(amount, "Amount must not be null");
        return BigMoney.ofCurrencyScale(CurrencyUnit.of(currencyCode), amount, roundingMode);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>BigMoney</code> specifying the amount in major units.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * The scale of the money will be zero.
     * <p>
     * The amount is a whole number only. Thus you can initialise the value
     * 'USD 20', but not the value 'USD 20.32'.
     * For example, <code>ofMajor(USD, 25)</code> creates the instance <code>USD 25</code>.
     *
     * @param currency  the currency, not null
     * @param amountMajor  the amount of money in the major division of the currency
     * @return the new instance, never null
     */
    public static BigMoney ofMajor(CurrencyUnit currency, long amountMajor) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        return BigMoney.of(currency, BigDecimal.valueOf(amountMajor));
    }

    /**
     * Gets an instance of <code>BigMoney</code> specifying the amount in major units.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * The scale of the money will be zero.
     * <p>
     * The amount is a whole number only. Thus you can initialise the value
     * 'USD 20', but not the value 'USD 20.32'.
     * For example, <code>ofMajor(USD, 25)</code> creates the instance <code>USD 25</code>.
     *
     * @param currencyCode  the currency code, not null
     * @param amountMajor  the amount of money in the major division of the currency
     * @return the new instance, never null
     * @throws IllegalArgumentException if the currency is unknown
     * @throws ArithmeticException if the amount is too large
     */
    public static BigMoney ofMajor(String currencyCode, long amountMajor) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        return BigMoney.ofMajor(CurrencyUnit.of(currencyCode), amountMajor);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>BigMoney</code> in the specifying the amount in minor units.
     * <p>
     * This allows you to create an instance with a specific currency and amount
     * expressed in terms of the minor unit.
     * The scale of the money will be that of the currency, such as 2 for USD or 0 for JPY.
     * <p>
     * For example, if constructing US Dollars, the input to this method represents cents.
     * Note that when a currency has zero decimal places, the major and minor units are the same.
     * For example, <code>ofMajor(USD, 2595)</code> creates the instance <code>USD 25.95</code>.
     *
     * @param currency  the currency, not null
     * @param amountMinor  the amount of money in the minor division of the currency
     * @return the new instance, never null
     */
    public static BigMoney ofMinor(CurrencyUnit currency, long amountMinor) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        return BigMoney.of(currency, BigDecimal.valueOf(amountMinor, currency.getDecimalPlaces()));
    }

    /**
     * Gets an instance of <code>BigMoney</code> in the specifying the amount in minor units.
     * <p>
     * This allows you to create an instance with a specific currency and amount
     * expressed in terms of the minor unit.
     * The scale of the money will be that of the currency, such as 2 for USD or 0 for JPY.
     * <p>
     * For example, if constructing US Dollars, the input to this method represents cents.
     * Note that when a currency has zero decimal places, the major and minor units are the same.
     * For example, <code>ofMajor(USD, 2595)</code> creates the instance <code>USD 25.95</code>.
     *
     * @param currencyCode  the currency code, not null
     * @param amountMinor  the amount of money in the minor division of the currency
     * @return the new instance, never null
     * @throws IllegalArgumentException if the currency is unknown
     */
    public static BigMoney ofMinor(String currencyCode, long amountMinor) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        return BigMoney.ofMinor(CurrencyUnit.of(currencyCode), amountMinor);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>BigMoney</code> representing zero in the specified currency.
     * <p>
     * The scale of the money will be zero.
     * For example, <code>zero(USD)</code> creates the instance <code>USD 0</code>.
     *
     * @param currency  the currency, not null
     * @return the instance representing zero, never null
     */
    public static BigMoney zero(CurrencyUnit currency) {
        return BigMoney.of(currency, BigDecimal.ZERO);
    }

    /**
     * Gets an instance of <code>BigMoney</code> representing zero in the specified currency.
     * <p>
     * The scale of the money will be zero.
     * For example, <code>zero(USD)</code> creates the instance <code>USD 0</code>.
     *
     * @param currencyCode  the currency code, not null
     * @return the instance representing zero, never null
     * @throws IllegalArgumentException if the currency is unknown
     */
    public static BigMoney zero(String currencyCode) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        return BigMoney.zero(CurrencyUnit.of(currencyCode));
    }

    //-----------------------------------------------------------------------
    /**
     * Parses an instance of <code>BigMoney</code> from a string.
     * <p>
     * The string format is '<currencyCode> <amount>'.
     * The currency code must be three letters, and the amount must be a number.
     * This matches the output from {@link #toString()}.
     * <p>
     * For example, <code>of("USD 25")</code> creates the instance <code>USD 25</code>
     * while <code>of("USD 25.95")</code> creates the instance <code>USD 25.95</code>.
     *
     * @param moneyStr  the money string to parse, not null
     * @return the parsed instance, never null
     * @throws IllegalArgumentException if the string is malformed
     * @throws ArithmeticException if the amount is too large
     */
    public static BigMoney parse(String moneyStr) {
        MoneyUtils.checkNotNull(moneyStr, "Money must not be null");
        if (moneyStr.length() < 5 || moneyStr.charAt(3) != ' ') {
            throw new IllegalArgumentException("Money '" + moneyStr + "' cannot be parsed");
        }
        String currStr = moneyStr.substring(0, 3);
        String amountStr = moneyStr.substring(4);
        return BigMoney.of(CurrencyUnit.of(currStr), new BigDecimal(amountStr));
    }

//    /**
//     * Converts an amount in decimal to an amount in minor units safely.
//     * 
//     * @param currency  the currency, validated not null
//     * @param amount  the amount to convert, validated not null
//     * @return the converted amount, never null
//     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
//     */
//    private static BigDecimal decimalToMinor(CurrencyUnit currency, BigDecimal amount) {
//        return amount.movePointRight(currency.getDecimalPlaces());
//    }
//
//    /**
//     * Converts an amount in major units to an amount in minor units safely.
//     * 
//     * @param currency  the currency, validated not null
//     * @param amountMajor  the amount to convert
//     * @return the converted amount
//     */
//    private static long majorToMinor(CurrencyUnit currency, long amountMajor) {
//        long mult = factor(currency.getDecimalPlaces());
//        long result = amountMajor * mult;
//        if (result / mult != amountMajor) {
//            throw new ArithmeticException("Monetary value is too large: " + currency.getCurrencyCode() + " " + amountMajor);
//        }
//        return result;
//    }
//
//    /**
//     * Gets the factors to divide or multiply by between major and minor units.
//     * 
//     * @param dp  the decimal places
//     * @return the factor
//     */
//    private static int factor(int dp) {
//        return (dp == 3 ? 1000 : (dp == 2 ? 100 : (dp == 1 ? 10 : 1)));
//    }
//
//    /**
//     * Safely converts a <code>long</code> to an <code>int</code>.
//     * 
//     * @param amount  the amount to convert
//     * @return the value as an <code>int</code>
//     * @throws ArithmeticException if the amount is too large
//     */
//    private static int safeToInt(long amount) {
//        if (amount > Integer.MAX_VALUE || amount < Integer.MIN_VALUE) {
//            throw new ArithmeticException("Amount is too large to represent in an int: " + amount);
//        }
//        return (int) amount;
//    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param currency  the currency to use, not null
     * @param amount  the amount of money, not null
     */
    private BigMoney(CurrencyUnit currency, BigDecimal amount) {
        iCurrency = currency;
        iAmount = amount;
    }

    /**
     * Resolves singletons.
     * 
     * @return the singleton instance
     */
    private Object readResolve() {
        return BigMoney.of(iCurrency, iAmount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the currency.
     * 
     * @return the currency, never null
     */
    public CurrencyUnit getCurrencyUnit() {
        return iCurrency;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instance with the specified currency.
     * <p>
     * The returned instance will have the specified currency and the amount
     * from this instance. No currency conversion or alteration to the scale occurs.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param currency  the currency to use, not null
     * @return the new instance with the input currency set, never null
     */
    public BigMoney withCurrencyUnit(CurrencyUnit currency) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        if (iCurrency == currency) {
            return this;
        }
        return new BigMoney(currency, iAmount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the scale of the <code>BigDecimal</code> amount.
     * <p>
     * The scale has the same meaning as in {@link BigDecimal}.
     * Positive values represent the number of decimal places in use.
     * Negative numbers represent the opposite.
     * For example, a scale of 2 means that the money will have two decimal places
     * such as 'USD 43.25'. Whereas, a scale of -3 means that only thousands can be
     * represented, such as 'GBP 124000'.
     * 
     * @return the scale in use
     * @see #withScale
     */
    public int getScale() {
        return iAmount.scale();
    }

    /**
     * Checks if this money has the scale of the currency.
     * <p>
     * Each currency has a default scale, such as 2 for USD and 0 for JPY.
     * This method checks if the current scale matches the default scale.
     * 
     * @return true if the scale equals the current default scale
     */
    public boolean isCurrencyScale() {
        return iAmount.scale() == iCurrency.getDecimalPlaces();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instance with the specified scale,
     * truncating the amount if necessary.
     * <p>
     * The returned instance will have this currency and the new scaled amount.
     * For example, scaling 'USD 43.271' to a scale of 1 will yield 'USD 43.2'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amount  the monetary value to use, never null
     * @return the new instance with the input amount set, never null
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public BigMoney withScale(int scale) {
        return withScale(scale, RoundingMode.DOWN);
    }

    /**
     * Returns a copy of this instance with the specified scale,
     * using the specified rounding mode if necessary.
     * <p>
     * The returned instance will have this currency and the new scaled amount.
     * For example, scaling 'USD 43.271' to a scale of 1 with HALF_EVEN rounding
     * will yield 'USD 43.3'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amount  the monetary value to use, never null
     * @return the new instance with the input amount set, never null
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public BigMoney withScale(int scale, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        if (scale == iAmount.scale()) {
            return this;
        }
        return BigMoney.of(iCurrency, iAmount.setScale(scale, roundingMode));
    }

    /**
     * Returns a copy of this instance with the scale of the currency,
     * truncating the amount if necessary.
     * <p>
     * The returned instance will have this currency and the new scaled amount.
     * For example, scaling 'USD 43.271' will yield 'USD 43.27' as USD has a scale of 2.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amount  the monetary value to use, never null
     * @return the new instance with the input amount set, never null
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public BigMoney withCurrencyScale() {
        return withScale(iCurrency.getDecimalPlaces(), RoundingMode.DOWN);
    }

    /**
     * Returns a copy of this instance with the scale of the currency,
     * using the specified rounding mode if necessary.
     * <p>
     * The returned instance will have this currency and the new scaled amount.
     * For example, scaling 'USD 43.271' will yield 'USD 43.27' as USD has a scale of 2.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amount  the monetary value to use, never null
     * @return the new instance with the input amount set, never null
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public BigMoney withCurrencyScale(RoundingMode roundingMode) {
        return withScale(iCurrency.getDecimalPlaces(), roundingMode);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount.
     * <p>
     * This returns the value of the money as a <code>BigDecimal</code>.
     * The scale will be the scale of this money.
     * 
     * @return the amount, never null
     */
    public BigDecimal getAmount() {
        return iAmount;
    }

    /**
     * Gets the amount in major units as a <code>BigDecimal</code> with scale 0.
     * <p>
     * This returns the monetary amount in terms of the major units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 2, and 'BHD -1.345' will return -1.
     * <p>
     * This is returned as a <code>BigDecimal</code> rather than a <code>BigInteger</code>.
     * This is to allow further calculations to be performed on the result.
     * Should you need a <code>BigInteger</code>, simply call {@link BigDecimal#toBigInteger()}.
     * 
     * @return the major units part of the amount, never null
     */
    public BigDecimal getAmountMajor() {
        return iAmount.setScale(0, RoundingMode.DOWN);
    }

    /**
     * Gets the amount in major units as a <code>long</code>.
     * <p>
     * This returns the monetary amount in terms of the major units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 2, and 'BHD -1.345' will return -1.
     * 
     * @return the major units part of the amount
     * @throws ArithmeticException if the amount is too large for a <code>long</code>
     */
    public long getAmountMajorLong() {
        return getAmountMajor().longValueExact();
    }

    /**
     * Gets the amount in major units as an <code>int</code>.
     * <p>
     * This returns the monetary amount in terms of the major units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 2, and 'BHD -1.345' will return -1.
     * 
     * @return the major units part of the amount
     * @throws ArithmeticException if the amount is too large for an <code>int</code>
     */
    public int getAmountMajorInt() {
        return getAmountMajor().intValueExact();
    }

    /**
     * Gets the amount in minor units as a <code>BigDecimal</code> with scale 0.
     * <p>
     * This returns the monetary amount in terms of the minor units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 235, and 'BHD -1.345' will return -1345.
     * <p>
     * This is returned as a <code>BigDecimal</code> rather than a <code>BigInteger</code>.
     * This is to allow further calculations to be performed on the result.
     * Should you need a <code>BigInteger</code>, simply call {@link BigDecimal#toBigInteger()}.
     * 
     * @return the minor units part of the amount, never null
     */
    public BigDecimal getAmountMinor() {
        int cdp = getCurrencyUnit().getDecimalPlaces();
        return iAmount.setScale(cdp, RoundingMode.DOWN).movePointRight(cdp);
    }

    /**
     * Gets the amount in minor units as a <code>long</code>.
     * <p>
     * This returns the monetary amount in terms of the minor units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 235, and 'BHD -1.345' will return -1345.
     * 
     * @return the minor units part of the amount
     * @throws ArithmeticException if the amount is too large for a <code>long</code>
     */
    public long getAmountMinorLong() {
        return getAmountMinor().longValueExact();
    }

    /**
     * Gets the amount in minor units as an <code>int</code>.
     * <p>
     * This returns the monetary amount in terms of the minor units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 235, and 'BHD -1.345' will return -1345.
     * 
     * @return the minor units part of the amount
     * @throws ArithmeticException if the amount is too large for an <code>int</code>
     */
    public int getAmountMinorInt() {
        return getAmountMinor().intValueExact();
    }

    /**
     * Gets the minor part of the amount.
     * <p>
     * This return the minor unit part of the monetary amount.
     * This is defined as the amount in minor units excluding major units.
     * <p>
     * For example, EUR has a scale of 2, so the minor part is always between 0 and 99
     * for positive amounts, and 0 and -99 for negative amounts.
     * Thus 'EUR 2.35' will return 35, and 'EUR -1.34' will return -34.
     * 
     * @return the minor part of the amount, negative if the amount is negative
     */
    public int getMinorPart() {
        int cdp = getCurrencyUnit().getDecimalPlaces();
        return iAmount.setScale(cdp, RoundingMode.DOWN)
                    .remainder(BigDecimal.ONE)
                    .movePointRight(cdp).intValueExact();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the amount is zero.
     * 
     * @return true if the amount is zero
     */
    public boolean isZero() {
        return iAmount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Checks if the amount is greater than zero.
     * 
     * @return true if the amount is greater than zero
     */
    public boolean isPositive() {
        return iAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if the amount is zero or greater.
     * 
     * @return true if the amount is zero or greater
     */
    public boolean isPositiveOrZero() {
        return iAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Checks if the amount is less than zero.
     * 
     * @return true if the amount is less than zero
     */
    public boolean isNegative() {
        return iAmount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Checks if the amount is zero or less.
     * 
     * @return true if the amount is zero or less
     */
    public boolean isNegativeOrZero() {
        return iAmount.compareTo(BigDecimal.ZERO) <= 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instance with the specified amount.
     * <p>
     * The returned instance will have this currency and the new amount.
     * The scale of the returned instance will be that of the specified BigDecimal.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amount  the monetary amount to set in the returned instance, not null
     * @return the new instance with the input amount set, never null
     */
    public BigMoney withAmount(BigDecimal amount) {
        MoneyUtils.checkNotNull(amount, "Amount must not be null");
        if (iAmount.equals(amount)) {
            return this;
        }
        return BigMoney.of(iCurrency, amount);
    }

    /**
     * Returns a copy of this instance with the specified amount using a well-defined
     * conversion from a <code>double</code>.
     * <p>
     * The returned instance will have this currency and the new amount.
     * <p>
     * The amount is converted via {@link BigDecimal#valueOf(double)} which yields
     * the most expected answer for most programming scenarios.
     * Any <code>double</code> literal in code will be converted to
     * exactly the same BigDecimal with the same scale.
     * For example, the literal '1.425d' will be converted to '1.425'.
     * The scale of the money will be that of the BigDecimal produced.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amount  the monetary amount to set in the returned instance
     * @return the new instance with the input amount set, never null
     */
    public BigMoney withAmount(double amount) {
        return withAmount(BigDecimal.valueOf(amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the currency of this money and the specified money match.
     * 
     * @param money  the money to check, not null
     * @throws MoneyException if the currencies differ
     */
    private BigMoney checkCurrencyEqual(BigMoneyProvider money) {
        BigMoney m = money.toBigMoney();
        if (isSameCurrency(m) == false) {
            throw new MoneyException("Currencies differ: " + this + " : " + money);
        }
        return m;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * No precision is lost in the result.
     * The scale of the result will be the maximum of the two scales.
     * For example,'USD 25.95' plus 'USD 3.021' will 'USD 28.971.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param moneyToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     * @throws MoneyException if the currencies differ
     */
    public BigMoney plus(BigMoneyProvider moneyToAdd) {
        BigMoney toAdd = checkCurrencyEqual(moneyToAdd);
        return plus(toAdd.getAmount());
    }

    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * No precision is lost in the result.
     * The scale of the result will be the maximum of the two scales.
     * For example,'USD 25.95' plus 'USD 3.021' will 'USD 28.971.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     */
    public BigMoney plus(BigDecimal amountToAdd) {
        MoneyUtils.checkNotNull(amountToAdd, "Amount must not be null");
        if (amountToAdd.compareTo(BigDecimal.ZERO) == 0) {
            return this;
        }
        BigDecimal amount = iAmount.add(amountToAdd);
        return BigMoney.of(iCurrency, amount);
    }

    /**
     * Returns a copy of this monetary value with the amount in major units added.
     * <p>
     * This adds an amount in major units, leaving the minor units untouched.
     * No precision is lost in the result.
     * The scale of the result will be the maximum of the current scale and 0.
     * For example, 'USD 23.45' plus 138 gives 'USD 161.45'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     */
    public BigMoney plusMajor(long amountToAdd) {
        if (amountToAdd == 0) {
            return this;
        }
        BigDecimal amount = iAmount.add(BigDecimal.valueOf(amountToAdd));
        return BigMoney.of(iCurrency, amount);
    }

    /**
     * Returns a copy of this monetary value with the amount in minor units added.
     * <p>
     * This adds an amount in minor units.
     * No precision is lost in the result.
     * The scale of the result will be the maximum of the current scale and the default currency scale.
     * For example, 'USD 23.45' plus 138 gives 'USD 24.83'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     */
    public BigMoney plusMinor(long amountToAdd) {
        if (amountToAdd == 0) {
            return this;
        }
        BigDecimal amount = iAmount.add(BigDecimal.valueOf(amountToAdd, iCurrency.getDecimalPlaces()));
        return BigMoney.of(iCurrency, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * No precision is lost in the result.
     * The scale of the result will be the maximum of the two scales.
     * For example,'USD 25.95' minus 'USD 3.021' will 'USD 22.929.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param moneyToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     * @throws MoneyException if the currencies differ
     */
    public BigMoney minus(BigMoneyProvider moneyToSubtract) {
        BigMoney toSubtract = checkCurrencyEqual(moneyToSubtract);
        return minus(toSubtract.getAmount());
    }

    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * No precision is lost in the result.
     * The scale of the result will be the maximum of the two scales.
     * For example,'USD 25.95' minus 'USD 3.021' will 'USD 22.929.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     */
    public BigMoney minus(BigDecimal amountToSubtract) {
        MoneyUtils.checkNotNull(amountToSubtract, "Amount must not be null");
        if (amountToSubtract.compareTo(BigDecimal.ZERO) == 0) {
            return this;
        }
        BigDecimal amount = iAmount.subtract(amountToSubtract);
        return BigMoney.of(iCurrency, amount);
    }

    /**
     * Returns a copy of this monetary value with the amount in major units subtracted.
     * <p>
     * No precision is lost in the result.
     * This subtracts an amount in major units, leaving the minor units untouched.
     * The scale of the result will be the maximum of the current scale and 0.
     * For example, 'USD 23.45' minus 138 gives 'USD -114.55'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     */
    public BigMoney minusMajor(long amountToSubtract) {
        if (amountToSubtract == 0) {
            return this;
        }
        BigDecimal amount = iAmount.subtract(BigDecimal.valueOf(amountToSubtract));
        return BigMoney.of(iCurrency, amount);
    }

    /**
     * Returns a copy of this monetary value with the amount in minor units subtracted.
     * <p>
     * No precision is lost in the result.
     * This subtracts an amount in minor units.
     * The scale of the result will be the maximum of the current scale and the default currency scale.
     * For example, USD 23.45 minus 138 gives USD 22.07.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     */
    public BigMoney minusMinor(long amountToSubtract) {
        if (amountToSubtract == 0) {
            return this;
        }
        BigDecimal amount = iAmount.subtract(BigDecimal.valueOf(amountToSubtract, iCurrency.getDecimalPlaces()));
        return BigMoney.of(iCurrency, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value multiplied by the specified value.
     * <p>
     * No precision is lost in the result.
     * The result has a scale equal to the sum of the two scales.
     * For example, 'USD 1.13' multiplied by 2.5 yields 'USD 2.825'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToMultiplyBy  the scalar value to multiply by, not null
     * @return the new multiplied instance, never null
     */
    public BigMoney multipliedBy(BigDecimal valueToMultiplyBy) {
        MoneyUtils.checkNotNull(valueToMultiplyBy, "Multiplier must not be null");
        if (valueToMultiplyBy.compareTo(BigDecimal.ONE) == 0) {
            return this;
        }
        BigDecimal amount = iAmount.multiply(valueToMultiplyBy);
        return BigMoney.of(iCurrency, amount);
    }

    /**
     * Returns a copy of this monetary value multiplied by the specified value.
     * <p>
     * No precision is lost in the result.
     * The result has a scale equal to the scale of this money.
     * For example, 'USD 1.13' multiplied by 2 yields 'USD 2.26'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToMultiplyBy  the scalar value to multiply by, not null
     * @return the new multiplied instance, never null
     */
    public BigMoney multipliedBy(long valueToMultiplyBy) {
        if (valueToMultiplyBy == 1) {
            return this;
        }
        BigDecimal amount = iAmount.multiply(BigDecimal.valueOf(valueToMultiplyBy));
        return BigMoney.of(iCurrency, amount);
    }

    /**
     * Returns a copy of this monetary value multiplied by the specified value
     * using the specified rounding mode to adjust the scale of the result.
     * <p>
     * This multiplies this money by the specified value, retaining the scale of this money.
     * This will frequently lose precision, hence the need for a rounding mode.
     * For example, 'USD 1.13' multiplied by 2.5 and rounding down yields 'USD 2.82'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToMultiplyBy  the scalar value to multiply by, not null
     * @param roundingMode  the rounding mode to use to bring the decimal places back in line, not null
     * @return the new multiplied instance, never null
     * @throws ArithmeticException if the rounding fails
     */
    public BigMoney multiplyRetainScale(BigDecimal valueToMultiplyBy, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(valueToMultiplyBy, "Multiplier must not be null");
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        if (valueToMultiplyBy.compareTo(BigDecimal.ONE) == 0) {
            return this;
        }
        BigDecimal amount = iAmount.multiply(valueToMultiplyBy);
        amount = amount.setScale(getScale(), roundingMode);
        return BigMoney.of(iCurrency, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value divided by the specified value,
     * rounding down the result to have the same scale as this money.
     * <p>
     * The result has the same scale as this instance.
     * For example, 'USD 1.13' divided by 2.5 yields 'USD 0.45'
     * (amount rounded down from 0.452).
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToDivideBy  the scalar value to multiply by, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     */
    public BigMoney dividedBy(BigDecimal valueToDivideBy) {
        return dividedBy(valueToDivideBy, RoundingMode.DOWN);
    }

    /**
     * Returns a copy of this monetary value divided by the specified value
     * using the specified rounding mode to adjust the scale.
     * <p>
     * The result has the same scale as this instance.
     * For example, 'USD 1.13' divided by 2.5 and rounding down yields 'USD 0.45'
     * (amount rounded down from 0.452).
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToDivideBy  the scalar value to divide by, not null
     * @param roundingMode  the rounding mode to use, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     * @throws ArithmeticException if the rounding fails
     */
    public BigMoney dividedBy(BigDecimal valueToDivideBy, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(valueToDivideBy, "Divisor must not be null");
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        if (valueToDivideBy.compareTo(BigDecimal.ONE) == 0) {
            return this;
        }
        BigDecimal amount = iAmount.divide(valueToDivideBy, roundingMode);
        return BigMoney.of(iCurrency, amount);
    }

    /**
     * Returns a copy of this monetary value divided by the specified value
     * rounding down if necessary.
     * <p>
     * The result has the same scale as this instance.
     * For example, 'USD 1.13' divided by 2 yields 'USD 0.56'
     * (amount rounded down from 0.565).
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToDivideBy  the scalar value to divide by, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     */
    public BigMoney dividedBy(long valueToDivideBy) {
        return dividedBy(valueToDivideBy, RoundingMode.DOWN);
    }

    /**
     * Returns a copy of this monetary value divided by the specified value
     * using the specified rounding mode to adjust the decimal places in the result.
     * <p>
     * The result has the same scale as this instance.
     * For example, 'USD 1.13' divided by 2 and rounding down yields 'USD 0.56'
     * (amount rounded down from 0.565).
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToDivideBy  the scalar value to divide by, not null
     * @param roundingMode  the rounding mode to use, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     */
    public BigMoney dividedBy(long valueToDivideBy, RoundingMode roundingMode) {
        if (valueToDivideBy == 1) {
            return this;
        }
        BigDecimal amount = iAmount.divide(BigDecimal.valueOf(valueToDivideBy), roundingMode);
        return BigMoney.of(iCurrency, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the amount negated.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @return the new instance with the amount negated, never null
     */
    public BigMoney negated() {
        if (isZero()) {
            return this;
        }
        return BigMoney.of(iCurrency, iAmount.negate());
    }

    /**
     * Returns a copy of this monetary value with a positive amount.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @return the new instance with the amount converted to be positive, never null
     */
    public BigMoney abs() {
        return (isNegative() ? negated() : this);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value rounded to the specified scale without
     * changing the current scale.
     * <p>
     * Scale is described in {@link BigDecimal} and represents the point below which
     * the monetary value is zero. Negative scales round increasingly large numbers.
     * Unlike {@link #withScale(int)}, this scale of the result is unchanged.
     * <ul>
     * <li>Rounding 'EUR 45.23' to a scale of 2 has no effect (it already has that scale).
     * <li>Rounding 'EUR 45.23' to a scale of 1 returns 45.20 or 45.30 depending on the rounding mode.
     * <li>Rounding 'EUR 45.23' to a scale of 0 returns 45.00 or 46.00 depending on the rounding mode.
     * <li>Rounding 'EUR 45.23' to a scale of -1 returns 40.00 or 50.00 depending on the rounding mode.
     * </ul>
     * This instance is immutable and unaffected by this method.
     * 
     * @return the new instance with the amount converted to be positive, never null
     * @throws ArithmeticException if the rounding fails
     */
    public BigMoney rounded(int scale, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        if (scale >= getScale()) {
            return this;
        }
        int currentScale = iAmount.scale();
        BigDecimal amount = iAmount.setScale(scale, roundingMode).setScale(currentScale);
        return BigMoney.of(iCurrency, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value converted into another currency
     * using the specified conversion rate.
     * <p>
     * The scale of the result will be the sum of the scale of this money and the multiplier.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param currency  the new currency, not null
     * @param conversionMultipler  the conversion factor between the currencies, not null
     * @return the new multiplied instance, never null
     * @throws MoneyException if the currency is the same as this currency
     * @throws MoneyException if the conversion multiplier is negative
     */
    public BigMoney convertedTo(CurrencyUnit currency, BigDecimal conversionMultipler) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        MoneyUtils.checkNotNull(conversionMultipler, "Multiplier must not be null");
        if (currency == iCurrency) {
            throw new MoneyException("Cannot convert to the same currency");
        }
        if (conversionMultipler.compareTo(BigDecimal.ZERO) < 0) {
            throw new MoneyException("Cannot convert using a negative conversion multiplier");
        }
        BigDecimal amount = iAmount.multiply(conversionMultipler);
        return BigMoney.of(currency, amount);
    }

//    /**
//     * Returns a copy of this monetary value converted into another currency
//     * using the specified conversion rate, with a rounding mode used to adjust
//     * the decimal places in the result.
//     * <p>
//     * This instance is immutable and unaffected by this method.
//     * 
//     * @param currency  the new currency, not null
//     * @param conversionMultipler  the conversion factor between the currencies, not null
//     * @param roundingMode  the rounding mode to use to bring the decimal places back in line, not null
//     * @return the new multiplied instance, never null
//     * @throws MoneyException if the currency is the same as this currency
//     * @throws MoneyException if the conversion multiplier is negative
//     * @throws ArithmeticException if the rounding fails
//     * @throws ArithmeticException if the amount is too large
//     */
//    public BigMoney convertedTo(CurrencyUnit currency, BigDecimal conversionMultipler, RoundingMode roundingMode) {
//        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
//        MoneyUtils.checkNotNull(conversionMultipler, "Multiplier must not be null");
//        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
//        if (currency == iCurrency) {
//            throw new MoneyException("Cannot convert to the same currency");
//        }
//        if (conversionMultipler.compareTo(BigDecimal.ZERO) < 0) {
//            throw new MoneyException("Cannot convert using a negative conversion multiplier");
//        }
//        BigDecimal amount = getAmount().multiply(conversionMultipler);
//        return BigMoney.of(currency, amount, roundingMode);
//    }

    //-----------------------------------------------------------------------
    /**
     * Implements the <code>BigMoneyProvider</code> interface, trivially
     * returning <code>this</code>.
     * 
     * @return the money instance, never null
     * @throws MoneyException if conversion is not possible
     */
    public BigMoney toBigMoney() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instance and the specified instance have the same currency.
     * 
     * @param money  the money to check, not null
     * @return true if they have the same currency
     */
    public boolean isSameCurrency(BigMoneyProvider money) {
        MoneyUtils.checkNotNull(money, "Money must not be null");
        return (iCurrency.equals(money.toBigMoney().getCurrencyUnit()));
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this monetary value to another.
     * The compared values must be in the same currency.
     * 
     * @param other  the other monetary value, not null
     * @return -1 if this is less than , 0 if equal, 1 if greater than
     * @throws MoneyException if the currencies differ
     */
    public int compareTo(BigMoneyProvider other) {
        if (isSameCurrency(other) == false) {
            throw new MoneyException("Cannot compare " + other + " to " + this + " as the currencies differ");
        }
        return iAmount.compareTo(other.toBigMoney().iAmount);
    }

    /**
     * Checks if this monetary value is equal to another.
     * <p>
     * This ignores the scale of the amount.
     * Thus, 'USD 30.00' and 'USD 30' are equal.
     * <p>
     * The compared values must be in the same currency.
     * 
     * @param other  the other monetary value, not null
     * @return true is this is greater than the specified monetary value
     * @throws MoneyException if the currencies differ
     * @see #equals(Object)
     */
    public boolean isEqual(BigMoneyProvider other) {
        return compareTo(other) == 0;
    }

    /**
     * Checks if this monetary value is greater than another.
     * The compared values must be in the same currency.
     * 
     * @param other  the other monetary value, not null
     * @return true is this is greater than the specified monetary value
     * @throws MoneyException if the currencies differ
     */
    public boolean isGreaterThan(BigMoneyProvider other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if this monetary value is less than another.
     * The compared values must be in the same currency.
     * 
     * @param other  the other monetary value, not null
     * @return true is this is less than the specified monetary value
     * @throws MoneyException if the currencies differ
     */
    public boolean isLessThan(BigMoneyProvider other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this monetary value equals another.
     * <p>
     * Like BigDecimal, this method compares the scale of the amount.
     * Thus, 'USD 30.00' and 'USD 30' are not equal.
     * <p>
     * The compared values must be in the same currency.
     * 
     * @return true if this instance equals the other instance
     * @see #isEqual(BigMoney)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof BigMoney) {
            BigMoney otherMoney = (BigMoney) other;
            return iCurrency.equals(otherMoney.getCurrencyUnit()) &&
                    iAmount.equals(otherMoney.iAmount);
        }
        return false;
    }

    /**
     * Returns a hash code for this instance.
     * 
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return iCurrency.hashCode() ^ iAmount.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the monetary value as a string.
     * <p>
     * The format is the 3 letter ISO currency code, followed by a space,
     * followed by the amount as per {@link BigDecimal#toPlainString()}.
     * 
     * @return the monetary value, never null
     */
    @Override
    public String toString() {
        return new StringBuilder()
            .append(iCurrency.getCurrencyCode())
            .append(' ')
            .append(iAmount.toPlainString())
            .toString();
    }

}
