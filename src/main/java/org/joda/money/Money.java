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
 * An amount of money in a specific currency.
 * <p>
 * An instance of money holds an amount in a currency.
 * <p>
 * Every currency has a certain number of decimal places.
 * This is typically 2 (Euro, British Pound, US Dollar) but might be
 * 0 (Japanese Yen), 1 (Vietnamese Dong) or 3 (Bahrain Dinar).
 * <p>
 * This class is limited to storing and manipulating the amount at the
 * precision of the number of decimal places and the capacity of a long.
 * For example, on US dollars no calculations can be performed on fractions of a cent.
 * <p>
 * Money is immutable and thread-safe.
 */
public final class Money implements Comparable<Money>, Serializable {

    /**
     * The serialisation version.
     */
    private static final long serialVersionUID = 723473581L;
    /**
     * The currency, not null.
     */
    private final CurrencyUnit iCurrency;
    /**
     * The amount in minor units.
     */
    private final long iAmount;

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of Money in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * No rounding is performed on the amount, so it must be valid.
     *
     * @param currency  the currency, not null
     * @param amount  the amount of money, not null
     * @return the new instance, never null
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public static Money of(CurrencyUnit currency, BigDecimal amount) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        MoneyUtils.checkNotNull(amount, "Amount must not be null");
        return ofMinor(currency, decimalToMinor(currency, amount));
    }

    /**
     * Gets an instance of Money in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * No rounding is performed on the amount, so it must be valid.
     *
     * @param currencyCode  the currency code, not null
     * @param amount  the amount of money, not null
     * @return the new instance, never null
     * @throws IllegalArgumentException if the currency is unknown
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public static Money of(String currencyCode, BigDecimal amount) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        return of(CurrencyUnit.of(currencyCode), amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of Money in the specified currency, rounding as necessary.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * If the amount has excess fractional digits, they are rounded using the rounding mode.
     *
     * @param currency  the currency, not null
     * @param amount  the amount of money, not null
     * @param roundingMode  the rounding mode to use, not null
     * @return the new instance, never null
     * @throws ArithmeticException if the rounding fails
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public static Money of(CurrencyUnit currency, BigDecimal amount, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        MoneyUtils.checkNotNull(amount, "Amount must not be null");
        amount = amount.setScale(currency.getDecimalPlaces(), roundingMode);
        return ofMinor(currency, decimalToMinor(currency, amount));
    }

    /**
     * Gets an instance of Money in the specified currency, rounding as necessary.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * If the amount has excess fractional digits, they are rounded using the rounding mode.
     *
     * @param currencyCode  the currency code, not null
     * @param amount  the amount of money, not null
     * @param roundingMode  the rounding mode to use, not null
     * @return the new instance, never null
     * @throws ArithmeticException if the rounding fails
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public static Money of(String currencyCode, BigDecimal amount, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        return of(CurrencyUnit.of(currencyCode), amount, roundingMode);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of Money in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * The amount is a whole number only. Thus you can initialise the value
     * 'USD 20', but not the value 'USD 20.32'.
     * <p>
     * For example, <code>ofMajor(USD, 25)</code> creates the instance <code>USD 25.00</code>.
     *
     * @param currency  the currency, not null
     * @param amountMajor  the amount of money in the major division of the currency
     * @return the new instance, never null
     * @throws ArithmeticException if the amount is too large
     */
    public static Money ofMajor(CurrencyUnit currency, long amountMajor) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        return ofMinor(currency, majorToMinor(currency, amountMajor));
    }

    /**
     * Gets an instance of Money in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     * The amount is a whole number only. Thus you can initialise the value
     * 'USD 20', but not the value 'USD 20.32'.
     * <p>
     * For example, <code>ofMajor("USD", 25)</code> creates the instance <code>USD 25.00</code>.
     *
     * @param currencyCode  the currency code, not null
     * @param amountMajor  the amount of money in the major division of the currency
     * @return the new instance, never null
     * @throws IllegalArgumentException if the currency is unknown
     * @throws ArithmeticException if the amount is too large
     */
    public static Money ofMajor(String currencyCode, long amountMajor) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        return ofMajor(CurrencyUnit.of(currencyCode), amountMajor);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of Money in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount
     * expressed in terms of the minor unit.
     * For example, if constructing US Dollars, the input to this method represents cents.
     * Note that when a currency has zero decimal places, the major and minor units are the same.
     * <p>
     * For example, <code>ofMajor(USD, 2595)</code> creates the instance <code>USD 25.95</code>.
     *
     * @param currency  the currency, not null
     * @param amountMinor  the amount of money in the minor division of the currency
     * @return the new instance, never null
     */
    public static Money ofMinor(CurrencyUnit currency, long amountMinor) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        return new Money(currency, amountMinor);
    }

    /**
     * Gets an instance of Money in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount
     * expressed in terms of the minor unit.
     * For example, if constructing US Dollars, the input to this method represents cents.
     * Note that when a currency has zero decimal places, the major and minor units are the same.
     * <p>
     * For example, <code>ofMajor("USD", 2595)</code> creates the instance <code>USD 25.95</code>.
     *
     * @param currencyCode  the currency code, not null
     * @param amountMinor  the amount of money in the minor division of the currency
     * @return the new instance, never null
     * @throws IllegalArgumentException if the currency is unknown
     */
    public static Money ofMinor(String currencyCode, long amountMinor) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        return ofMinor(CurrencyUnit.of(currencyCode), amountMinor);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of the Money representing zero in the specified currency.
     * <p>
     * For example, <code>zero(USD)</code> creates the instance <code>USD 0.00</code>.
     *
     * @param currency  the currency, not null
     * @return the instance representing zero, never null
     */
    public static Money zero(CurrencyUnit currency) {
        return ofMinor(currency, 0);
    }

    /**
     * Gets an instance of the Money representing zero in the specified currency.
     * <p>
     * For example, <code>zero("USD")</code> creates the instance <code>USD 0.00</code>.
     *
     * @param currencyCode  the currency code, not null
     * @return the instance representing zero, never null
     * @throws IllegalArgumentException if the currency is unknown
     */
    public static Money zero(String currencyCode) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        return zero(CurrencyUnit.of(currencyCode));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of the Money from a string.
     * <p>
     * The string format is '<currencyCode> <amount>'.
     * The currency code must be three letters, and the amount must be a number.
     * This matches the output from {@link #toString()}.
     * <p>
     * For example, <code>of("USD 25")</code> creates the instance <code>USD 25.00</code>
     * while <code>of("USD 25.95")</code> creates the instance <code>USD 25.95</code>.
     *
     * @param moneyStr  the money string to parse, not null
     * @return the parsed instance, never null
     * @throws IllegalArgumentException if the string is malformed
     * @throws ArithmeticException if the amount is too large
     */
    public static Money parse(String moneyStr) {
        MoneyUtils.checkNotNull(moneyStr, "Money must not be null");
        if (moneyStr.length() < 5 || moneyStr.charAt(3) != ' ') {
            throw new IllegalArgumentException("Money '" + moneyStr + "' cannot be parsed");
        }
        String currStr = moneyStr.substring(0, 3);
        String amountStr = moneyStr.substring(4);
        return of(CurrencyUnit.of(currStr), new BigDecimal(amountStr));
    }

    /**
     * Converts an amount in decimal to an amount in minor units safely.
     * 
     * @param currency  the currency, validated not null
     * @param amount  the amount to convert, validated not null
     * @return the converted amount
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    private static long decimalToMinor(CurrencyUnit currency, BigDecimal amount) {
        return amount.movePointRight(currency.getDecimalPlaces()).longValueExact();
    }

    /**
     * Converts an amount in major units to an amount in minor units safely.
     * 
     * @param currency  the currency, validated not null
     * @param amountMajor  the amount to convert
     * @return the converted amount
     */
    private static long majorToMinor(CurrencyUnit currency, long amountMajor) {
        long mult = factor(currency.getDecimalPlaces());
        long result = amountMajor * mult;
        if (result / mult != amountMajor) {
            throw new ArithmeticException("Monetary value is too large: " + currency.getCurrencyCode() + " " + amountMajor);
        }
        return result;
    }

    /**
     * Gets the factors to divide or multiply by between major and minor units.
     * 
     * @param dp  the decimal places
     * @return the factor
     */
    private static int factor(int dp) {
        return (dp == 3 ? 1000 : (dp == 2 ? 100 : (dp == 1 ? 10 : 1)));
    }

    /**
     * Safely converts a <code>long</code> to an <code>int</code>.
     * 
     * @param amount  the amount to convert
     * @return the value as an <code>int</code>
     * @throws ArithmeticException if the amount is too large
     */
    private static int safeToInt(long amount) {
        if (amount > Integer.MAX_VALUE || amount < Integer.MIN_VALUE) {
            throw new ArithmeticException("Amount is too large to represent in an int: " + amount);
        }
        return (int) amount;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param currency  the currency to use, not null
     * @param amount  the amount of money
     */
    private Money(CurrencyUnit currency, long amount) {
        iCurrency = currency;
        iAmount = amount;
    }

    /**
     * Resolves singletons.
     * 
     * @return the singleton instance
     */
    private Object readResolve() {
        return Money.ofMinor(iCurrency, iAmount);
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

    /**
     * Gets the number of decimal places used by the currency and this monetary value.
     * 
     * @return the number of decimal places in use, typically 2 but could be 0, 1 and 3
     */
    public int getDecimalPlaces() {
        return iCurrency.getDecimalPlaces();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount.
     * 
     * @return the amount, never null
     */
    public BigDecimal getAmount() {
        return BigDecimal.valueOf(iAmount, iCurrency.getDecimalPlaces());
    }

    /**
     * Gets the amount in major units.
     * <p>
     * This extracts the whole number part of the monetary amount by dropping
     * any amount beyond the decimal place, effectively rounding down.
     * For example, 'EUR 2.35' will return 2, and 'BHD -1.345' will return -1.
     * 
     * @return the whole number part of the amount
     */
    public long getAmountMajor() {
        long mult = factor(getDecimalPlaces());
        return iAmount / mult;
    }

    /**
     * Gets the amount in major units as an <code>int</code>.
     * <p>
     * This extracts the whole number part of the monetary amount by dropping
     * any amount beyond the decimal place, effectively rounding down.
     * For example, 'EUR 2.35' will return 2, and 'BHD -1.345' will return -1.
     * 
     * @return the whole number part of the amount
     * @throws ArithmeticException if the amount is too large
     */
    public int getAmountMajorInt() {
        return safeToInt(getAmountMajor());
    }

    /**
     * Gets the amount in minor units.
     * <p>
     * This return the whole of the monetary amount as a long in terms of the minor units.
     * For example, 'EUR 2.35' will return 235, and 'BHD -1.345' will return -1345.
     * 
     * @return the whole number part of the amount
     */
    public long getAmountMinor() {
        return iAmount;
    }

    /**
     * Gets the amount in minor units as an <code>int</code>.
     * <p>
     * This return the whole of the monetary amount as a long in terms of the minor units.
     * For example, 'EUR 2.35' will return 235, and 'BHD -1.345' will return -1345.
     * 
     * @return the whole number part of the amount
     * @throws ArithmeticException if the amount is too large
     */
    public int getAmountMinorInt() {
        return safeToInt(iAmount);
    }

    /**
     * Gets the minor part of the amount.
     * <p>
     * This return the whole of the monetary amount as a long in terms of the minor units.
     * For example, 'EUR 2.35' will return 35, and 'BHD -1.345' will return -345.
     * 
     * @return the minor part of the amount, negative if the amount is negative
     */
    public int getMinorPart() {
        long mult = factor(getDecimalPlaces());
        return (int) (iAmount % mult);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the amount is zero.
     * 
     * @return true if the amount is zero
     */
    public boolean isZero() {
        return iAmount == 0;
    }

    /**
     * Checks if the amount is greater than zero.
     * 
     * @return true if the amount is greater than zero
     */
    public boolean isPositive() {
        return iAmount > 0;
    }

    /**
     * Checks if the amount is zero or greater.
     * 
     * @return true if the amount is zero or greater
     */
    public boolean isPositiveOrZero() {
        return iAmount >= 0;
    }

    /**
     * Checks if the amount is less than zero.
     * 
     * @return true if the amount is less than zero
     */
    public boolean isNegative() {
        return iAmount < 0;
    }

    /**
     * Checks if the amount is zero or less.
     * 
     * @return true if the amount is zero or less
     */
    public boolean isNegativeOrZero() {
        return iAmount <= 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instance and the specified instance have the same currency.
     * 
     * @param money  the money to check, not null
     * @return true if they have the same currency
     */
    public boolean isSameCurrency(Money money) {
        MoneyUtils.checkNotNull(money, "Money must not be null");
        return (iCurrency.equals(money.getCurrencyUnit()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instance with the specified currency.
     * <p>
     * The returned instance will have the specified currency and the amount
     * from this instance. The amount will be rounded down if necessary.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param currency  the currency to use, not null
     * @return the new instance with the input currency set, never null
     */
    public Money withCurrency(CurrencyUnit currency) {
        return withCurrency(currency, RoundingMode.DOWN);
    }

    /**
     * Returns a copy of this instance with the specified currency.
     * <p>
     * The returned instance will have the specified currency and the amount
     * from this instance. The amount will be rounded down if necessary.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param currency  the currency to use, not null
     * @param roundingMode  the rounding mode to use to bring the decimal places back in line, not null
     * @return the new instance with the input currency set, never null
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public Money withCurrency(CurrencyUnit currency, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        if (iCurrency == currency) {
            return this;
        }
        return Money.of(currency, getAmount(), roundingMode);
    }

    /**
     * Returns a copy of this instance with the specified amount.
     * <p>
     * The returned instance will have this currency and the new amount.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amount  the monetary value to use, never null
     * @return the new instance with the input amount set, never null
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public Money withAmount(BigDecimal amount) {
        MoneyUtils.checkNotNull(amount, "Amount must not be null");
        if (amount.equals(getAmount())) {
            return this;
        }
        return Money.of(iCurrency, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param moneyToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     * @throws MoneyException if the currencies differ
     * @throws ArithmeticException if the amount is too large
     */
    public Money plus(Money moneyToAdd) {
        MoneyUtils.checkNotNull(moneyToAdd, "Money must not be null");
        if (isSameCurrency(moneyToAdd) == false) {
            throw new MoneyException("Cannot add " + moneyToAdd + " to " + this + " as the currencies differ");
        }
        return plusMinor(moneyToAdd.getAmountMinor());
    }

    /**
     * Returns a copy of this monetary value with the amount added.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public Money plus(BigDecimal amountToAdd) {
        MoneyUtils.checkNotNull(amountToAdd, "Amount must not be null");
        return plusMinor(decimalToMinor(iCurrency, amountToAdd));
    }

    /**
     * Returns a copy of this monetary value with the amount in major units added.
     * <p>
     * This adds an amount in major units, leaving the minor units untouched.
     * For example, USD 23.45 plus 138 gives USD 161.45.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     * @throws ArithmeticException if the amount is too large
     */
    public Money plusMajor(long amountToAdd) {
        return plusMinor(majorToMinor(iCurrency, amountToAdd));
    }

    /**
     * Returns a copy of this monetary value with the amount in minor units added.
     * <p>
     * This adds an amount in minor units.
     * For example, USD 23.45 plus 138 gives USD 24.83.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToAdd  the monetary value to add, not null
     * @return the new instance with the input amount added, never null
     * @throws ArithmeticException if the amount is too large
     */
    public Money plusMinor(long amountToAdd) {
        if (amountToAdd == 0) {
            return this;
        }
        long amountMinor = iAmount + amountToAdd;
        if ((iAmount ^ amountMinor) < 0 && (iAmount ^ amountToAdd) >= 0) {
            throw new ArithmeticException("Monetary value is too large");
        }
        return Money.ofMinor(iCurrency, amountMinor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param moneyToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     * @throws MoneyException if the currencies differ
     * @throws ArithmeticException if the amount is too large
     */
    public Money minus(Money moneyToSubtract) {
        MoneyUtils.checkNotNull(moneyToSubtract, "Money must not be null");
        if (isSameCurrency(moneyToSubtract) == false) {
            throw new MoneyException("Cannot subtract " + moneyToSubtract + " from " + this + " as the currencies differ");
        }
        return minusMinor(moneyToSubtract.getAmountMinor());
    }

    /**
     * Returns a copy of this monetary value with the amount subtracted.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     * @throws ArithmeticException if the amount is too large or exceeds the fractional capacity
     */
    public Money minus(BigDecimal amountToSubtract) {
        MoneyUtils.checkNotNull(amountToSubtract, "Amount must not be null");
        return minusMinor(decimalToMinor(iCurrency, amountToSubtract));
    }

    /**
     * Returns a copy of this monetary value with the amount in major units subtracted.
     * <p>
     * This subtracts an amount in major units, leaving the minor units untouched.
     * For example, USD 23.45 minus 138 gives USD -114.55.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     * @throws ArithmeticException if the amount is too large
     */
    public Money minusMajor(long amountToSubtract) {
        return minusMinor(majorToMinor(iCurrency, amountToSubtract));
    }

    /**
     * Returns a copy of this monetary value with the amount in minor units subtracted.
     * <p>
     * This subtracts an amount in minor units.
     * For example, USD 23.45 minus 138 gives USD 22.07.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param amountToSubtract  the monetary value to subtract, not null
     * @return the new instance with the input amount subtracted, never null
     * @throws ArithmeticException if the amount is too large
     */
    public Money minusMinor(long amountToSubtract) {
        if (amountToSubtract == 0) {
            return this;
        }
        long amountMinor = iAmount - amountToSubtract;
        if ((iAmount ^ amountMinor) < 0 && (iAmount ^ amountToSubtract) < 0) {
            throw new ArithmeticException("Monetary value is too large");
        }
        return Money.ofMinor(iCurrency, amountMinor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value multiplied by the specified value
     * rounding down if necessary.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToMultiplyBy  the scalar value to multiply by, not null
     * @return the new multiplied instance, never null
     * @throws ArithmeticException if the amount is too large
     */
    public Money multipliedBy(BigDecimal valueToMultiplyBy) {
        return multipliedBy(valueToMultiplyBy, RoundingMode.DOWN);
    }

    /**
     * Returns a copy of this monetary value multiplied by the specified value
     * using the specified rounding mode to adjust the decimal places in the result.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToMultiplyBy  the scalar value to multiply by, not null
     * @param roundingMode  the rounding mode to use to bring the decimal places back in line, not null
     * @return the new multiplied instance, never null
     * @throws ArithmeticException if the rounding fails
     * @throws ArithmeticException if the amount is too large
     */
    public Money multipliedBy(BigDecimal valueToMultiplyBy, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(valueToMultiplyBy, "Multiplier must not be null");
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        BigDecimal amount = getAmount().multiply(valueToMultiplyBy);
        return Money.of(iCurrency, amount, roundingMode);
    }

    /**
     * Returns a copy of this monetary value multiplied by the specified value.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToMultiplyBy  the scalar value to multiply by, not null
     * @return the new multiplied instance, never null
     * @throws ArithmeticException if the amount is too large
     */
    public Money multipliedBy(long valueToMultiplyBy) {
        if (valueToMultiplyBy == 1) {
            return this;
        }
        long total = iAmount * valueToMultiplyBy;
        if (total / valueToMultiplyBy != iAmount) {
            throw new ArithmeticException("Monetary value is too large");
        }
        return Money.ofMinor(iCurrency, total);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value divided by the specified value
     * rounding down if necessary.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToDivideBy  the scalar value to multiply by, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     */
    public Money dividedBy(BigDecimal valueToDivideBy) {
        return dividedBy(valueToDivideBy, RoundingMode.DOWN);
    }

    /**
     * Returns a copy of this monetary value divided by the specified value
     * using the specified rounding mode to adjust the decimal places in the result.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToDivideBy  the scalar value to divide by, not null
     * @param roundingMode  the rounding mode to use, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     * @throws ArithmeticException if the rounding fails
     */
    public Money dividedBy(BigDecimal valueToDivideBy, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(valueToDivideBy, "Divisor must not be null");
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        BigDecimal amount = getAmount().divide(valueToDivideBy, roundingMode);
        return Money.of(iCurrency, amount);
    }

    /**
     * Returns a copy of this monetary value divided by the specified value
     * rounding down if necessary.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param valueToDivideBy  the scalar value to divide by, not null
     * @return the new divided instance, never null
     * @throws ArithmeticException if dividing by zero
     */
    public Money dividedBy(long valueToDivideBy) {
        if (valueToDivideBy == 1) {
            return this;
        }
        return Money.ofMinor(iCurrency, iAmount / valueToDivideBy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value with the amount negated.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @return the new instance with the amount negated, never null
     */
    public Money negated() {
        if (iAmount == Long.MIN_VALUE) {
            throw new ArithmeticException("Monetary value is too large");
        }
        return Money.ofMinor(iCurrency, -iAmount);
    }

    /**
     * Returns a copy of this monetary value with a positive amount.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @return the new instance with the amount converted to be positive, never null
     */
    public Money abs() {
        return (iAmount < 0 ? negated() : this);
    }

    /**
     * Returns a copy of this monetary value rounded to the specified scale.
     * <p>
     * Scale is described in {@link BigDecimal} and represents the point below which
     * the monetary value is zero.
     * Negative scales round increasingly large numbers.
     * <ul>
     * <li>Rounding EUR 45.23 to a scale of 2 has no effect (it already has that scale).
     * <li>Rounding EUR 45.23 to a scale of 1 returns 45.20 or 45.30 depending on the rounding mode.
     * <li>Rounding EUR 45.23 to a scale of 0 returns 45.00 or 46.00 depending on the rounding mode.
     * <li>Rounding EUR 45.23 to a scale of -1 returns 40.00 or 50.00 depending on the rounding mode.
     * </ul>
     * This instance is immutable and unaffected by this method.
     * 
     * @return the new instance with the amount converted to be positive, never null
     * @throws MoneyException if the scale is invalid
     * @throws ArithmeticException if the rounding fails
     * @throws ArithmeticException if the amount is too large
     */
    public Money rounded(int scale, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        if (scale == getDecimalPlaces()) {
            return this;
        }
        if (scale > getDecimalPlaces()) {
            throw new MoneyException("Scale cannot be less than the number of allowed decimal places");
        }
        BigDecimal amount = getAmount().setScale(scale, roundingMode);
        return Money.of(iCurrency, amount, roundingMode);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this monetary value converted into another currency
     * using the specified conversion rate, truncating the value if rounding is required.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param currency  the new currency, not null
     * @param conversionMultipler  the conversion factor between the currencies, not null
     * @return the new multiplied instance, never null
     * @throws MoneyException if the currency is the same as this currency
     * @throws MoneyException if the conversion multiplier is negative
     * @throws ArithmeticException if the rounding fails
     * @throws ArithmeticException if the amount is too large
     */
    public Money convertedTo(CurrencyUnit currency, BigDecimal conversionMultipler) {
        return convertedTo(currency, conversionMultipler, RoundingMode.DOWN);
    }

    /**
     * Returns a copy of this monetary value converted into another currency
     * using the specified conversion rate, with a rounding mode used to adjust
     * the decimal places in the result.
     * <p>
     * This instance is immutable and unaffected by this method.
     * 
     * @param currency  the new currency, not null
     * @param conversionMultipler  the conversion factor between the currencies, not null
     * @param roundingMode  the rounding mode to use to bring the decimal places back in line, not null
     * @return the new multiplied instance, never null
     * @throws MoneyException if the currency is the same as this currency
     * @throws MoneyException if the conversion multiplier is negative
     * @throws ArithmeticException if the rounding fails
     * @throws ArithmeticException if the amount is too large
     */
    public Money convertedTo(CurrencyUnit currency, BigDecimal conversionMultipler, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(currency, "CurrencyUnit must not be null");
        MoneyUtils.checkNotNull(conversionMultipler, "Multiplier must not be null");
        MoneyUtils.checkNotNull(roundingMode, "RoundingMode must not be null");
        if (currency == iCurrency) {
            throw new MoneyException("Cannot convert to the same currency");
        }
        if (conversionMultipler.compareTo(BigDecimal.ZERO) < 0) {
            throw new MoneyException("Cannot convert using a negative conversion multiplier");
        }
        BigDecimal amount = getAmount().multiply(conversionMultipler);
        return Money.of(currency, amount, roundingMode);
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
    public int compareTo(Money other) {
        if (isSameCurrency(other) == false) {
            throw new MoneyException("Cannot compare " + other + " to " + this + " as the currencies differ");
        }
        if (iAmount < other.getAmountMinor()) {
            return -1;
        }
        if (iAmount > other.getAmountMinor()) {
            return 1;
        }
        return 0;
    }

    /**
     * Checks if this monetary value is greater than another.
     * The compared values must be in the same currency.
     * 
     * @param other  the other monetary value, not null
     * @return true is this is greater than the specified monetary value
     * @throws MoneyException if the currencies differ
     */
    public boolean isGreaterThan(Money other) {
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
    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this monetary value equals another.
     * The compared values must be in the same currency.
     * 
     * @return true if this instance equals the other instance
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Money) {
            Money otherMoney = (Money) other;
            return iCurrency.equals(otherMoney.getCurrencyUnit()) &&
                    iAmount == otherMoney.getAmountMinor();
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
        return iCurrency.hashCode() ^ ((int) (iAmount ^ (iAmount >>> 32)));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the monetary value as a string.
     * <p>
     * The format is the 3 letter ISO currency code, followed by a space,
     * followed by the amount with decimal places as per the currency.
     * 
     * @return the monetary value, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        long factor = factor(getDecimalPlaces());
        buf.append(iCurrency.getCurrencyCode()).append(' ');
        long amount = iAmount;
        long minor;
        if (amount < 0) {
            buf.append('-').append(-(amount / factor));
            minor = -(amount % factor);
        } else {
            buf.append(amount / factor);
            minor = amount % factor;
        }
        if (getDecimalPlaces() > 0) {
            int index = buf.length();
            buf.append(minor + factor).setCharAt(index, '.');
        }
        return buf.toString();
    }

}
