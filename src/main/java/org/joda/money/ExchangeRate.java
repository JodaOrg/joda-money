/*
 *  Copyright 2009-2011 Stephen Colebourne
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

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an exchange rate that allows {@link BigMoneyProvider} instances to be converted between amounts in
 * different {@link CurrencyUnit}s. Exchange rate is an object that holds data about two {@link CurrencyUnit}s and a
 * conversion rate, and basically mirrors the following equation:
 * 
 * <p>
 * 1 major unit of <strong><code>target currency</code></strong> = <strong><code>rate</code></strong> major units of
 * <strong><code>source currency</code></strong>.
 * </p>
 * 
 * Exchange rates provide a few simple operations that can be performed with them through {@link ExchangeRateOperations}
 * . A default implementation can be acquired using {@link #operations(int, RoundingMode)}.
 * 
 * <p>
 * Instances of this class are immutable.
 * </p>
 * 
 * <p>
 * Two instances of this class are considered equal when both target and source currencies are the same and the rates
 * compare equal according to {@link BigDecimal#compareTo(BigDecimal)}.
 * </p>
 * 
 * Please be aware that creating an exchange rate less or equal to 0 is considered an error.
 * 
 * @author Tom Pasierb
 */
public final class ExchangeRate implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Pattern PARSE_REGEX = Pattern.compile("^1\\s+([A-Z]{3})\\s+=\\s([-+]?\\d+(.\\d+)?)\\s+([A-Z]{3})$");

    /**
     * Conversion rate between source and target currency.
     */
    private final BigDecimal rate;

    /**
     * The source currency.
     */
    private final CurrencyUnit source;

    /**
     * The target currency.
     */
    private final CurrencyUnit target;

    public static ExchangeRate parse(String exchangeRate) {
        Utils.notNull(exchangeRate, "Exchange rate cannot be null");

        String exr = exchangeRate.trim();
        Matcher matcher = PARSE_REGEX.matcher(exr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Exchange rate '%s' cannot be parsed", exchangeRate));
        }

        String source, target, rate;
        target = matcher.group(1);
        rate = matcher.group(2);
        source = matcher.groupCount() == 4 ? matcher.group(4) : matcher.group(3);
        return ExchangeRate.of(new BigDecimal(rate), CurrencyUnit.getInstance(source), CurrencyUnit.getInstance(target));
    }

    /**
     * Creates an identity rate for the given {@link CurrencyUnit}. The rate will be equal to 1.00 and both source and
     * target currency units will be the same as the given one.
     * 
     * @param currency to be set for source and target currency units.
     * @throws NullPointerException if the given parameter is <code>null</code>
     * @return identity exchange rate having rate 1 and the same source and target currencies
     */
    public static ExchangeRate identity(CurrencyUnit currency) {
        return new ExchangeRate(BigDecimal.ONE, currency, currency);
    }

    /**
     * Creates an Exchange rate with the given parameters.
     * 
     * @param rate the conversion rate
     * @param source source {@link CurrencyUnit}
     * @param target target {@link CurrencyUnit}
     * @throws NullPointerException if any of the given parameters is <code>null</code>
     * @throws IllegalArgumentException if the given rate is less or equal to 0 or when source and currency units are
     *             equal and rate is not equal to 1
     * @return an instance of exchange rate parameterized as instructed
     */
    public static ExchangeRate of(BigDecimal rate, CurrencyUnit source, CurrencyUnit target) {
        return new ExchangeRate(rate, source, target);
    }

    /**
     * Constructs an instance of ExchangeRate with the given parameters.
     * 
     * @param rate the conversion rate
     * @param source source {@link CurrencyUnit}
     * @param target target {@link CurrencyUnit}
     * @throws NullPointerException if any of the given parameters is <code>null</code>
     * @throws IllegalArgumentException if the given rate is less or equal to 0 or when source and currency units are
     *             equal and rate is not equal to 1
     * @return an instance of exchange rate parameterized as instructed
     */
    ExchangeRate(BigDecimal rate, CurrencyUnit source, CurrencyUnit target) {
        Utils.notNull(rate, "Rate must not be null");
        Utils.notNull(source, "Source currency must not be null");
        Utils.notNull(target, "Target currency must not be null");
        Utils.isTrue(rate.compareTo(BigDecimal.ZERO) > 0, "Rate must be greater than 0");
        if (source == target) {
            Utils.isTrue(rate.compareTo(BigDecimal.ONE) == 0,
                    "Rate must be 1 if source and target currencies are the same, got rate=%f, source=%s, target=%s", rate, source, target);
        }
        this.rate = rate;
        this.source = source;
        this.target = target;
    }

    /**
     * Creates an ExchangeRate like this one with the given rate.
     * 
     * @param rate the rate that the new ExchangeRate should have
     * @return a copy of this with the given rate and all other settings as this one
     * @see #of(BigDecimal, CurrencyUnit, CurrencyUnit)
     */
    public ExchangeRate withRate(BigDecimal rate) {
        return new ExchangeRate(rate, source, target);
    }

    /**
     * Returns the conversion rate.
     * 
     * @return the conversion rate between this exchange rate's source and target currencies
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Returns the source currency.
     * 
     * @return the source currency
     */
    public CurrencyUnit getSource() {
        return source;
    }

    /**
     * Returns the target currency.
     * 
     * @return the target currency
     */
    public CurrencyUnit getTarget() {
        return target;
    }

    /**
     * Creates an instance of an object that knows how to perform operations on {@link ExchangeRate}s.
     * 
     * @param scale scale that will be used for calculations
     * @param roundingMode rounding mode to use if rounding is necessary
     * @return an instance of {@link ExchangeRateOperations}
     */
    public ExchangeRateOperations operations(int scale, RoundingMode roundingMode) {
        return new DefaultExchangeRateOperations(this, scale, roundingMode);
    }

    /**
     * Creates an instance of an object that knows how to perform operations on {@link ExchangeRate}s with default
     * values for <code>scale</code> and <code>roundingMode</code> that will be used for calculations. The values used
     * as defaults are:
     * <ul>
     * <li>scale = 16</li>
     * <li>roundingMode = {@link RoundingMode#HALF_EVEN}</li>
     * </ul>
     * 
     * @return an instance of {@link ExchangeRateOperations}
     */
    public ExchangeRateOperations operations() {
        return new DefaultExchangeRateOperations(this, 16, RoundingMode.HALF_EVEN);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rate == null) ? 0 : rate.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ExchangeRate)) {
            return false;
        }
        ExchangeRate other = (ExchangeRate) obj;
        boolean equal = true;
        equal &= rate == null ? other.rate == null : rate.compareTo(other.rate) == 0;
        if (!equal) {
            return equal;
        }
        equal &= source == null ? other.source == null : source.equals(other.source);
        if (!equal) {
            return equal;
        }
        equal &= target == null ? other.target == null : target.equals(other.target);
        return equal;
    }

    @Override
    public String toString() {
        return "ExchangeRate[1 " + target + " = " + rate + " " + source + "]";
    }

    /**
     * Block malicious data streams.
     * 
     * @param ois the input stream, not null
     * @throws InvalidObjectException
     */
    private void readObject(ObjectInputStream ois) throws InvalidObjectException {
        throw new InvalidObjectException("Serialization delegate required");
    }

    /**
     * Uses a serialization delegate.
     * 
     * @return the replacing object, never null
     */
    private Object writeReplace() {
        return new Ser(Ser.EXCHANGE_RATE, this);
    }
}
