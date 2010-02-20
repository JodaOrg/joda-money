/*
 *  Copyright 2009-2010 Stephen Colebourne
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
 * An amount of money with a fixed number of decimal places.
 * <p>
 * This class represents a quantity of money, stored as an amount in a
 * single {@link CurrencyUnit currency}.
 * <p>
 * This class models a fixed scale amount where the scale is zero or positive.
 * See {@link BigDecimal} for a discussion of scale.
 * <p>
 * For example, if the application must store and manipulate all monetary values to
 * a scale of 9, then the scale is simply passed to the factory method and
 * all methods will operate based on that scale.
 * <p>
 * FixedMoney is immutable and thread-safe.
 */
public final class FixedMoney implements BigMoneyProvider, Comparable<BigMoneyProvider>, Serializable {

    /**
     * The serialisation version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The money, not null.
     */
    private final BigMoney iMoney;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code FixedMoney} from a {@code BigDecimal} at a specific scale.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * No rounding is performed on the amount, so it must have a
     * scale less than or equal to the new scale.
     *
     * @param currency  the currency, not null
     * @param amount  the amount of money, not null
     * @param scale  the scale to use, zero or positive
     * @return the new instance, never null
     * @throws ArithmeticException if the scale exceeds the currency scale
     */
    public static FixedMoney of(CurrencyUnit currency, BigDecimal amount, int scale) {
        return FixedMoney.of(currency, amount, scale, RoundingMode.UNNECESSARY);
    }

    /**
     * Obtains an instance of {@code FixedMoney} from a {@code double} using a
     * well-defined conversion, rounding as necessary.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * If the amount has a scale in excess of the scale of the currency then the excess
     * fractional digits are rounded using the rounding mode.
     *
     * @param currency  the currency, not null
     * @param amount  the amount of money, not null
     * @param scale  the scale to use, zero or positive
     * @param roundingMode  the rounding mode to use, not null
     * @return the new instance, never null
     * @throws IllegalArgumentException if the scale is negative
     * @throws ArithmeticException if the rounding fails
     */
    public static FixedMoney of(CurrencyUnit currency, BigDecimal amount, int scale, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        MoneyUtils.checkNotNull(amount, "Amount must not be null");
        checkScale(scale);
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        amount = amount.setScale(scale, roundingMode);
        return new FixedMoney(BigMoney.of(currency, amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code FixedMoney} from an scaled amount.
     * <p>
     * This allows you to create an instance with a specific currency, amount and scale.
     * The amount is defined in terms of the specified scale.
     * <p>
     * For example, {@code ofScaled(USD, 234, 2)} creates the instance {@code USD 2.34}.
     *
     * @param currency  the currency, not null
     * @param amountMajor  the amount of money in the major division of the currency
     * @param scale  the scale to use, zero or positive
     * @return the new instance, never null
     * @throws IllegalArgumentException if the scale is negative
     */
    public static FixedMoney ofScaled(CurrencyUnit currency, long amountInScale, int scale) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        checkScale(scale);
        BigDecimal amount = BigDecimal.valueOf(amountInScale, scale);
        return new FixedMoney(BigMoney.of(currency, amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code FixedMoney} from an amount in major units at a scale of zero.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * The amount is a whole number only. Thus you can initialise the value
     * 'USD 20', but not the value 'USD 20.32'.
     * <p>
     * For example, {@code ofMajor(USD, 25)} creates the instance {@code USD 25}.
     *
     * @param currency  the currency, not null
     * @param amountMajor  the amount of money in the major division of the currency
     * @return the new instance, never null
     */
    public static FixedMoney ofMajor(CurrencyUnit currency, long amountMajor) {
        return ofMajor(currency, amountMajor, 0);
    }

    /**
     * Obtains an instance of {@code FixedMoney} from an amount in major units at a specific scale.
     * <p>
     * This allows you to create an instance with a specific currency, amount and scale.
     * The amount is a whole number only. Thus you can initialise the value
     * 'USD 20', but not the value 'USD 20.32'.
     * <p>
     * For example, {@code ofMajor(USD, 25, 2)} creates the instance {@code USD 25.00}.
     *
     * @param currency  the currency, not null
     * @param amountMajor  the amount of money in the major division of the currency
     * @param scale  the scale to use, zero or positive
     * @return the new instance, never null
     * @throws IllegalArgumentException if the scale is negative
     */
    public static FixedMoney ofMajor(CurrencyUnit currency, long amountMajor, int scale) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        checkScale(scale);
        BigDecimal amount = BigDecimal.valueOf(amountMajor).setScale(scale);
        return new FixedMoney(BigMoney.of(currency, amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code FixedMoney} representing zero at a scale of zero.
     * <p>
     * For example, {@code zero(USD)} creates the instance {@code USD 0}.
     *
     * @param currency  the currency, not null
     * @return the instance representing zero, never null
     */
    public static FixedMoney zero(CurrencyUnit currency) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        return new FixedMoney(BigMoney.of(currency, BigDecimal.ZERO));
    }

    /**
     * Obtains an instance of {@code FixedMoney} representing zero at a specific scale.
     * <p>
     * For example, {@code zero(USD, 2)} creates the instance {@code USD 0.00}.
     *
     * @param currency  the currency, not null
     * @param scale  the scale to use, zero or positive
     * @return the instance representing zero, never null
     * @throws IllegalArgumentException if the scale is negative
     */
    public static FixedMoney zero(CurrencyUnit currency, int scale) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        checkScale(scale);
        return new FixedMoney(BigMoney.of(currency, BigDecimal.valueOf(0, scale)));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code FixedMoney} from a provider.
     * <p>
     * This allows you to create an instance from any class that implements the
     * provider, such as {@code BigMoney}.
     * <p>
     * If the scale of the provided money is negative, the result will have a scale of zero.
     * Otherwise, the scale of the result will equal that of the provided money.
     *
     * @param moneyProvider  the money to convert, not null
     * @return the new instance, never null
     */
    public static FixedMoney from(BigMoneyProvider moneyProvider) {
        BigMoney money = BigMoney.from(moneyProvider);
        if (money.getScale() < 0) {
            money = money.withScale(0);
        }
        return new FixedMoney(money);
    }

    /**
     * Obtains an instance of {@code FixedMoney} from a provider, rounding as necessary.
     * <p>
     * This allows you to create an instance from any class that implements the
     * provider, such as {@code BigMoney}.
     * The rounding mode is used to adjust the scale to the scale of the currency.
     *
     * @param moneyProvider  the money to convert, not null
     * @param scale  the scale to use, zero or positive
     * @param roundingMode  the rounding mode to use, not null
     * @return the new instance, never null
     * @throws IllegalArgumentException if the scale is negative
     * @throws ArithmeticException if the rounding fails
     */
    public static FixedMoney from(BigMoneyProvider moneyProvider, int scale, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(moneyProvider, "BigMoneyProvider must not be null");
        checkScale(scale);
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        return new FixedMoney(BigMoney.from(moneyProvider).withScale(scale, roundingMode));
    }

    //-----------------------------------------------------------------------
    /**
     * Parses an instance of {@code FixedMoney} from a string.
     * <p>
     * The string format is '<currencyCode> <amount>'.
     * The currency code must be three letters, and the amount must be a number.
     * This matches the output from {@link #toString()}.
     * <p>
     * For example, {@code of("USD 25")} creates the instance {@code USD 25.00}
     * while {@code of("USD 25.95")} creates the instance {@code USD 25.95}.
     * <p>
     * If the scale of the provided money is negative, the result will have a scale of zero.
     * Otherwise, the scale of the result will equal that of the provided money.
     *
     * @param moneyStr  the money string to parse, not null
     * @return the parsed instance, never null
     * @throws IllegalArgumentException if the string is malformed
     * @throws ArithmeticException if the amount is too large
     */
    public static FixedMoney parse(String moneyStr) {
        MoneyUtils.checkNotNull(moneyStr, "Money must not be null");
        if (moneyStr.length() < 5 || moneyStr.charAt(3) != ' ') {
            throw new IllegalArgumentException("Money '" + moneyStr + "' cannot be parsed");
        }
        String currStr = moneyStr.substring(0, 3);
        CurrencyUnit curr = CurrencyUnit.of(currStr);
        String amountStr = moneyStr.substring(4);
        BigDecimal amount = new BigDecimal(amountStr);
        amount = amount.setScale(Math.max(amount.scale(), 0));
        return new FixedMoney(BigMoney.of(curr, amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Ensures that a {@code FixedMoney} is not {@code null}.
     * <p>
     * If the input money is not {@code null}, then it is returned, providing
     * that the currency and scale match those specified.
     * If the input money is {@code null}, then zero money in the currency and
     * scale is returned.
     * 
     * @param money  the monetary value to check, may be null
     * @param currency  the currency to use, not null
     * @param scale  the scale to use, zero or positive
     * @return the input money or zero in the specified currency and scale, never null
     * @throws MoneyException if the input money is non-null and the currencies differ
     */
    public static FixedMoney nonNull(FixedMoney money, CurrencyUnit currency, int scale) {
        if (money == null) {
            return zero(currency, scale);
        }
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        checkScale(scale);
        if (money.getCurrencyUnit().equals(currency) == false) {
            throw new MoneyException("FixedMoney does not match specified currency");
        }
        if (money.getScale() != scale) {
            throw new MoneyException("FixedMoney does not match specified scale");
        }
        return money;
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the scale.
     * 
     * @param scale  the scale to validate
     * @throws IllegalArgumentException if the scale is invalid
     */
    private static void checkScale(int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("Invalid scale " + scale);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param money  the underlying money, not null
     */
    private FixedMoney(BigMoney money) {
        assert money != null : "Joda-Money bug: BigMoney must not be null";
        iMoney = money;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new {@code FixedMoney}, returning {@code this} if possible.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param currency  the currency to use, not null
     * @return the new instance with the input currency set, never null
     */
    private FixedMoney with(BigMoney newInstance) {
        if (newInstance == iMoney) {
            return this;
        }
        return new FixedMoney(newInstance);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the currency.
     * 
     * @return the currency, never null
     */
    public CurrencyUnit getCurrencyUnit() {
        return iMoney.getCurrencyUnit();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the specified currency.
     * <p>
     * The returned instance will have the specified currency and the amount
     * from this instance.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param currency  the currency to use, not null
     * @return the new instance with the input currency set, never null
     */
    public FixedMoney withCurrencyUnit(CurrencyUnit currency) {
        return with(iMoney.withCurrencyUnit(currency));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the scale of the {@code BigDecimal} amount.
     * <p>
     * The scale has the same meaning as in {@link BigDecimal}.
     * Positive values represent the number of decimal places in use.
     * For example, a scale of 2 means that the money will have two decimal places
     * such as 'USD 43.25'.
     * <p>
     * For {@code FixedMoney}, the scale is fixed by the factory method and doesn't
     * change from the calculation methods.
     * 
     * @return the scale in use, zero or greater
     */
    public int getScale() {
        return iMoney.getScale();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the specified scale.
     * <p>
     * The returned instance will have this currency and the new scaled amount.
     * For example, scaling 'USD 43.271' to a scale of 1 will yield 'USD 43.2'.
     * No rounding is performed on the amount, so it must have a
     * scale less than or equal to the new scale.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param scale  the scale to use, zero or greater
     * @return the new instance with the input amount set, never null
     * @throws IllegalArgumentException if the scale is negative
     * @throws ArithmeticException if the scale of the amount is too large
     */
    public FixedMoney withScale(int scale) {
        return withScale(scale, RoundingMode.UNNECESSARY);
    }

    /**
     * Returns a copy of this monetary value with the specified scale,
     * using the specified rounding mode if necessary.
     * <p>
     * The returned instance will have this currency and the new scaled amount.
     * For example, scaling 'USD 43.271' to a scale of 1 with HALF_EVEN rounding
     * will yield 'USD 43.3'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param scale  the scale to use, zero or greater
     * @param roundingMode  the rounding mode to use, not null
     * @return the new instance with the input amount set, never null
     * @throws IllegalArgumentException if the scale is negative
     * @throws ArithmeticException if the rounding fails
     */
    public FixedMoney withScale(int scale, RoundingMode roundingMode) {
        checkScale(scale);
        return with(iMoney.withScale(scale, roundingMode));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount.
     * <p>
     * This returns the value of the money as a {@code BigDecimal}.
     * The scale will be the scale of this money.
     * 
     * @return the amount, never null
     */
    public BigDecimal getAmount() {
        return iMoney.getAmount();
    }

    /**
     * Gets the amount in major units as a {@code BigDecimal} with scale 0.
     * <p>
     * This returns the monetary amount in terms of the major units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 2, and 'BHD -1.345' will return -1.
     * <p>
     * This is returned as a {@code BigDecimal} rather than a {@code BigInteger}.
     * This is to allow further calculations to be performed on the result.
     * Should you need a {@code BigInteger}, simply call {@link BigDecimal#toBigInteger()}.
     * 
     * @return the major units part of the amount, never null
     */
    public BigDecimal getAmountMajor() {
        return iMoney.getAmountMajor();
    }

    /**
     * Gets the amount in major units as a {@code long}.
     * <p>
     * This returns the monetary amount in terms of the major units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 2, and 'BHD -1.345' will return -1.
     * 
     * @return the major units part of the amount
     * @throws ArithmeticException if the amount is too large for a {@code long}
     */
    public long getAmountMajorLong() {
        return iMoney.getAmountMajorLong();
    }

    /**
     * Gets the amount in major units as an {@code int}.
     * <p>
     * This returns the monetary amount in terms of the major units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 2, and 'BHD -1.345' will return -1.
     * 
     * @return the major units part of the amount
     * @throws ArithmeticException if the amount is too large for an {@code int}
     */
    public int getAmountMajorInt() {
        return iMoney.getAmountMajorInt();
    }

    /**
     * Gets the amount in minor units as a {@code BigDecimal} with scale 0.
     * <p>
     * This returns the monetary amount in terms of the minor units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 235, and 'BHD -1.345' will return -1345.
     * <p>
     * This is returned as a {@code BigDecimal} rather than a {@code BigInteger}.
     * This is to allow further calculations to be performed on the result.
     * Should you need a {@code BigInteger}, simply call {@link BigDecimal#toBigInteger()}.
     * 
     * @return the minor units part of the amount, never null
     */
    public BigDecimal getAmountMinor() {
        return iMoney.getAmountMinor();
    }

    /**
     * Gets the amount in minor units as a {@code long}.
     * <p>
     * This returns the monetary amount in terms of the minor units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 235, and 'BHD -1.345' will return -1345.
     * 
     * @return the minor units part of the amount
     * @throws ArithmeticException if the amount is too large for a {@code long}
     */
    public long getAmountMinorLong() {
        return iMoney.getAmountMinorLong();
    }

    /**
     * Gets the amount in minor units as an {@code int}.
     * <p>
     * This returns the monetary amount in terms of the minor units of the currency,
     * truncating the amount if necessary.
     * For example, 'EUR 2.35' will return 235, and 'BHD -1.345' will return -1345.
     * 
     * @return the minor units part of the amount
     * @throws ArithmeticException if the amount is too large for an {@code int}
     */
    public int getAmountMinorInt() {
        return iMoney.getAmountMinorInt();
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
        return iMoney.getMinorPart();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the amount is zero.
     * 
     * @return true if the amount is zero
     */
    public boolean isZero() {
        return iMoney.isZero();
    }

    /**
     * Checks if the amount is greater than zero.
     * 
     * @return true if the amount is greater than zero
     */
    public boolean isPositive() {
        return iMoney.isPositive();
    }

    /**
     * Checks if the amount is zero or greater.
     * 
     * @return true if the amount is zero or greater
     */
    public boolean isPositiveOrZero() {
        return iMoney.isPositiveOrZero();
    }

    /**
     * Checks if the amount is less than zero.
     * 
     * @return true if the amount is less than zero
     */
    public boolean isNegative() {
        return iMoney.isNegative();
    }

    /**
     * Checks if the amount is zero or less.
     * 
     * @return true if the amount is zero or less
     */
    public boolean isNegativeOrZero() {
        return iMoney.isNegativeOrZero();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * This adds the specified amount to this monetary amount, returning a new object.
     * The amount added must be in the same currency.
     * <p>
     * No rounding is performed on the amount to be added, so it must have a
     * scale less than or equal to the scale of this monetary value.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param moneyToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     * @throws MoneyException if the currencies differ
     * @throws ArithmeticException if the scale of the money is too large
     */
    public FixedMoney plus(BigMoneyProvider moneyToAdd) {
        return plus(moneyToAdd, RoundingMode.UNNECESSARY);
    }

    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * This adds the specified amount to this monetary amount, returning a new object.
     * The amount added must be in the same currency.
     * <p>
     * No rounding is performed on the amount to be added, so it must have a
     * scale less than or equal to the scale of this monetary value.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param moneyToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     * @throws MoneyException if the currencies differ
     */
    public FixedMoney plus(BigMoneyProvider moneyToAdd, RoundingMode roundingMode) {
        return with(iMoney.plusRetainScale(moneyToAdd, roundingMode));
    }

    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * This adds the specified amount to this monetary amount, returning a new object.
     * No rounding is performed on the amount to be added, so it must have a
     * scale less than or equal to the scale of this monetary value.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     * @throws ArithmeticException if the scale of the amount is too large
     */
    public FixedMoney plus(BigDecimal amountToAdd) {
        return plus(amountToAdd, RoundingMode.UNNECESSARY);
    }

    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * This adds the specified amount to this monetary amount, returning a new object.
     * If the amount to add exceeds the scale of the currency, then the
     * rounding mode will be used to adjust the result.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     */
    public FixedMoney plus(BigDecimal amountToAdd, RoundingMode roundingMode) {
        return with(iMoney.plusRetainScale(amountToAdd, roundingMode));
    }

    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * This adds the specified amount to this monetary amount, returning a new object.
     * No rounding is performed on the amount to be added, so it must have a
     * scale less than or equal to the scale of this monetary value.
     * <p>
     * The amount is converted via {@link BigDecimal#valueOf(double)} which yields
     * the most expected answer for most programming scenarios.
     * Any {@code double} literal in code will be converted to
     * exactly the same BigDecimal with the same scale.
     * For example, the literal '1.45d' will be converted to '1.45'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     * @throws ArithmeticException if the scale of the amount is too large
     */
    public FixedMoney plus(double amountToAdd) {
        return plus(amountToAdd, RoundingMode.UNNECESSARY);
    }

    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * This adds the specified amount to this monetary amount, returning a new object.
     * If the amount to add exceeds the scale of the currency, then the
     * rounding mode will be used to adjust the result.
     * <p>
     * The amount is converted via {@link BigDecimal#valueOf(double)} which yields
     * the most expected answer for most programming scenarios.
     * Any {@code double} literal in code will be converted to
     * exactly the same BigDecimal with the same scale.
     * For example, the literal '1.45d' will be converted to '1.45'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     */
    public FixedMoney plus(double amountToAdd, RoundingMode roundingMode) {
        return with(iMoney.plusRetainScale(amountToAdd, roundingMode));
    }

    /**
     * Returns a copy of this monetary value with the amount in major units added.
     * <p>
     * This adds an amount in major units, leaving the minor units untouched.
     * For example, USD 23.45 plus 138 gives USD 161.45.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     */
    public FixedMoney plusMajor(long amountToAdd) {
        return with(iMoney.plusMajor(amountToAdd));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * This subtracts the specified amount from this monetary amount, returning a new object.
     * The amount subtracted must be in the same currency.
     * <p>
     * No rounding is performed on the amount to be subtracted, so it must have a
     * scale less than or equal to the scale of this monetary value.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param moneyToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     * @throws MoneyException if the currencies differ
     * @throws ArithmeticException if the scale of the money is too large
     */
    public FixedMoney minus(BigMoneyProvider moneyToSubtract) {
        return minus(moneyToSubtract, RoundingMode.UNNECESSARY);
    }

    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * This subtracts the specified amount from this monetary amount, returning a new object.
     * The amount subtracted must be in the same currency.
     * <p>
     * No rounding is performed on the amount to be subtracted, so it must have a
     * scale less than or equal to the scale of this monetary value.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param moneyToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     * @throws MoneyException if the currencies differ
     */
    public FixedMoney minus(BigMoneyProvider moneyToSubtract, RoundingMode roundingMode) {
        return with(iMoney.minusRetainScale(moneyToSubtract, roundingMode));
    }

    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * This subtracts the specified amount from this monetary amount, returning a new object.
     * No rounding is performed on the amount to be subtracted, so it must have a
     * scale less than or equal to the scale of this monetary value.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     * @throws ArithmeticException if the scale of the amount is too large
     */
    public FixedMoney minus(BigDecimal amountToSubtract) {
        return minus(amountToSubtract, RoundingMode.UNNECESSARY);
    }

    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * This subtracts the specified amount from this monetary amount, returning a new object.
     * If the amount to subtract exceeds the scale of the currency, then the
     * rounding mode will be used to adjust the result.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     */
    public FixedMoney minus(BigDecimal amountToSubtract, RoundingMode roundingMode) {
        return with(iMoney.minusRetainScale(amountToSubtract, roundingMode));
    }

    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * This subtracts the specified amount from this monetary amount, returning a new object.
     * No rounding is performed on the amount to be subtracted, so it must have a
     * scale less than or equal to the scale of this monetary value.
     * <p>
     * The amount is converted via {@link BigDecimal#valueOf(double)} which yields
     * the most expected answer for most programming scenarios.
     * Any {@code double} literal in code will be converted to
     * exactly the same BigDecimal with the same scale.
     * For example, the literal '1.45d' will be converted to '1.45'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     * @throws ArithmeticException if the scale of the amount is too large
     */
    public FixedMoney minus(double amountToSubtract) {
        return minus(amountToSubtract, RoundingMode.UNNECESSARY);
    }

    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * This subtracts the specified amount from this monetary amount, returning a new object.
     * If the amount to subtract exceeds the scale of the currency, then the
     * rounding mode will be used to adjust the result.
     * <p>
     * The amount is converted via {@link BigDecimal#valueOf(double)} which yields
     * the most expected answer for most programming scenarios.
     * Any {@code double} literal in code will be converted to
     * exactly the same BigDecimal with the same scale.
     * For example, the literal '1.45d' will be converted to '1.45'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     */
    public FixedMoney minus(double amountToSubtract, RoundingMode roundingMode) {
        return with(iMoney.minusRetainScale(amountToSubtract, roundingMode));
    }

    /**
     * Returns a copy of this monetary value with the amount in major units subtracted.
     * <p>
     * This subtracts an amount in major units, leaving the minor units untouched.
     * For example, USD 23.45 minus 138 gives USD -114.55.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     */
    public FixedMoney minusMajor(long amountToSubtract) {
        return with(iMoney.minusMajor(amountToSubtract));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value multiplied by the specified value.
     * <p>
     * This takes this amount and multiplies it by the specified value, rounding
     * the result is rounded as specified.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param valueToMultiplyBy  the scalar value to multiply by, not null
     * @param roundingMode  the rounding mode to use to bring the decimal places back in line, not null
     * @return the new multiplied instance, never null
     * @throws ArithmeticException if the rounding fails
     */
    public FixedMoney multipliedBy(BigDecimal valueToMultiplyBy, RoundingMode roundingMode) {
        return with(iMoney.multiplyRetainScale(valueToMultiplyBy, roundingMode));
    }

    /**
     * Returns a copy of this monetary value multiplied by the specified value.
     * <p>
     * This takes this amount and multiplies it by the specified value, rounding
     * the result is rounded as specified.
     * <p>
     * The amount is converted via {@link BigDecimal#valueOf(double)} which yields
     * the most expected answer for most programming scenarios.
     * Any {@code double} literal in code will be converted to
     * exactly the same BigDecimal with the same scale.
     * For example, the literal '1.45d' will be converted to '1.45'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param valueToMultiplyBy  the scalar value to multiply by, not null
     * @param roundingMode  the rounding mode to use to bring the decimal places back in line, not null
     * @return the new multiplied instance, never null
     * @throws ArithmeticException if the rounding fails
     */
    public FixedMoney multipliedBy(double valueToMultiplyBy, RoundingMode roundingMode) {
        return with(iMoney.multiplyRetainScale(valueToMultiplyBy, roundingMode));
    }

    /**
     * Returns a copy of this monetary value multiplied by the specified value.
     * <p>
     * This takes this amount and multiplies it by the specified value.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param valueToMultiplyBy  the scalar value to multiply by, not null
     * @return the new multiplied instance, never null
     */
    public FixedMoney multipliedBy(long valueToMultiplyBy) {
        return with(iMoney.multipliedBy(valueToMultiplyBy));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value divided by the specified value.
     * <p>
     * This takes this amount and divides it by the specified value, rounding
     * the result is rounded as specified.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param valueToDivideBy  the scalar value to divide by, not null
     * @param roundingMode  the rounding mode to use, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     * @throws ArithmeticException if the rounding fails
     */
    public FixedMoney dividedBy(BigDecimal valueToDivideBy, RoundingMode roundingMode) {
        return with(iMoney.dividedBy(valueToDivideBy, roundingMode));
    }

    /**
     * Returns a copy of this monetary value divided by the specified value.
     * <p>
     * This takes this amount and divides it by the specified value, rounding
     * the result is rounded as specified.
     * <p>
     * The amount is converted via {@link BigDecimal#valueOf(double)} which yields
     * the most expected answer for most programming scenarios.
     * Any {@code double} literal in code will be converted to
     * exactly the same BigDecimal with the same scale.
     * For example, the literal '1.45d' will be converted to '1.45'.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param valueToDivideBy  the scalar value to divide by, not null
     * @param roundingMode  the rounding mode to use, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     * @throws ArithmeticException if the rounding fails
     */
    public FixedMoney dividedBy(double valueToDivideBy, RoundingMode roundingMode) {
        return with(iMoney.dividedBy(valueToDivideBy, roundingMode));
    }

    /**
     * Returns a copy of this monetary value divided by the specified value.
     * <p>
     * This takes this amount and divides it by the specified value, rounding
     * the result is rounded as specified.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param valueToDivideBy  the scalar value to divide by, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     * @throws ArithmeticException if the rounding fails
     */
    public FixedMoney dividedBy(long valueToDivideBy, RoundingMode roundingMode) {
        return with(iMoney.dividedBy(valueToDivideBy, roundingMode));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the amount negated.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @return the new instance with the amount negated, never null
     */
    public FixedMoney negated() {
        return with(iMoney.negated());
    }

    /**
     * Returns a copy of this monetary value with a positive amount.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @return the new instance with the amount converted to be positive, never null
     */
    public FixedMoney abs() {
        return (isNegative() ? negated() : this);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value rounded to the specified scale without
     * changing the current scale.
     * <p>
     * Scale has the same meaning as in {@link BigDecimal}.
     * A scale of 2 means round to 2 decimal places.
     * <ul>
     * <li>Rounding 'EUR 45.23' to a scale of -1 returns 40.00 or 50.00 depending on the rounding mode.
     * <li>Rounding 'EUR 45.23' to a scale of 0 returns 45.00 or 46.00 depending on the rounding mode.
     * <li>Rounding 'EUR 45.23' to a scale of 1 returns 45.20 or 45.30 depending on the rounding mode.
     * <li>Rounding 'EUR 45.23' to a scale of 2 has no effect (it already has that scale).
     * <li>Rounding 'EUR 45.23' to a scale of 3 has no effect (the scale is not increased).
     * </ul>
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param scale  the new scale
     * @param roundingMode  the rounding mode to use, not null
     * @return the new instance with the amount converted to be positive, never null
     * @throws ArithmeticException if the rounding fails
     */
    public FixedMoney rounded(int scale, RoundingMode roundingMode) {
        return with(iMoney.rounded(scale, roundingMode));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value converted into another currency
     * using the specified conversion rate, with a rounding mode used to adjust
     * the decimal places in the result.
     * <p>
     * This instance is immutable and unaffected by this method.
     * The result will have the same scale as this monetary value.
     * 
     * @param currency  the new currency, not null
     * @param conversionMultipler  the conversion factor between the currencies, not null
     * @param roundingMode  the rounding mode to use to bring the decimal places back in line, not null
     * @return the new multiplied instance, never null
     * @throws MoneyException if the currency is the same as this currency
     * @throws MoneyException if the conversion multiplier is negative
     * @throws ArithmeticException if the rounding fails
     */
    public FixedMoney convertedTo(CurrencyUnit currency, BigDecimal conversionMultipler, RoundingMode roundingMode) {
        return with(iMoney.convertedTo(currency, conversionMultipler).withCurrencyScale(roundingMode));
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the {@code BigMoneyProvider} interface, returning a
     * {@code BigMoney} instance with the same currency, amount and scale.
     * 
     * @return the money instance, never null
     */
    public BigMoney toBigMoney() {
        return iMoney;
    }

    /**
     * Converts this money to an instance of {@code Money} without rounding.
     * If the scale of this money exceeds the currency scale an exception will be thrown.
     * 
     * @return the money instance, never null
     * @throws ArithmeticException if the rounding fails
     */
    public Money toMoney() {
        return Money.from(this);
    }

    /**
     * Converts this money to an instance of {@code Money}.
     * 
     * @param roundingMode  the rounding mode to use, not null
     * @return the money instance, never null
     * @throws ArithmeticException if the rounding fails
     */
    public Money toMoney(RoundingMode roundingMode) {
        return Money.from(this, roundingMode);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instance and the specified instance have the same currency.
     * 
     * @param money  the money to check, not null
     * @return true if they have the same currency
     */
    public boolean isSameCurrency(BigMoneyProvider money) {
        return iMoney.isSameCurrency(money);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this monetary value to another.
     * <p>
     * This allows {@code FixedMoney} to be compared to any {@code BigMoneyProvider}.
     * Scale is ignored in the comparison.
     * The compared values must be in the same currency.
     * 
     * @param other  the other monetary value, not null
     * @return -1 if this is less than , 0 if equal, 1 if greater than
     * @throws MoneyException if the currencies differ
     */
    public int compareTo(BigMoneyProvider other) {
        return iMoney.compareTo(other);
    }

    /**
     * Checks if this monetary value is equal to another.
     * <p>
     * This allows {@code FixedMoney} to be compared to any {@code BigMoneyProvider}.
     * Scale is ignored, so 'USD 30.00' and 'USD 30' are equal.
     * The compared values must be in the same currency.
     * 
     * @param other  the other monetary value, not null
     * @return true is this is greater than the specified monetary value
     * @throws MoneyException if the currencies differ
     * @see #equals(Object)
     */
    public boolean isEqual(BigMoneyProvider other) {
        return iMoney.isEqual(other);
    }

    /**
     * Checks if this monetary value is greater than another.
     * <p>
     * This allows {@code FixedMoney} to be compared to any {@code BigMoneyProvider}.
     * Scale is ignored in the comparison.
     * The compared values must be in the same currency.
     * 
     * @param other  the other monetary value, not null
     * @return true is this is greater than the specified monetary value
     * @throws MoneyException if the currencies differ
     */
    public boolean isGreaterThan(BigMoneyProvider other) {
        return iMoney.isGreaterThan(other);
    }

    /**
     * Checks if this monetary value is less than another.
     * <p>
     * This allows {@code FixedMoney} to be compared to any {@code BigMoneyProvider}.
     * Scale is ignored in the comparison.
     * The compared values must be in the same currency.
     * 
     * @param other  the other monetary value, not null
     * @return true is this is less than the specified monetary value
     * @throws MoneyException if the currencies differ
     */
    public boolean isLessThan(BigMoneyProvider other) {
        return iMoney.isLessThan(other);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this monetary value equals another.
     * <p>
     * The comparison takes into account the scale.
     * The compared values must be in the same currency.
     * 
     * @return true if this instance equals the other instance
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof FixedMoney) {
            FixedMoney otherMoney = (FixedMoney) other;
            return iMoney.equals(otherMoney.iMoney);
        }
        return false;
    }

    /**
     * Returns a hash code for this monetary value.
     * 
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return iMoney.hashCode() + 3;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the monetary value as a string.
     * <p>
     * The format is the 3 letter ISO currency code, followed by a space,
     * followed by the amount as per {@link BigDecimal#toPlainString()}.
     * The scale is represented by the number of printed decimal places.
     * 
     * @return the string representation of this monetary value, never null
     */
    @Override
    public String toString() {
        return iMoney.toString();
    }

}
