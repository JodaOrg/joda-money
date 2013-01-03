/*
 *  Copyright 2009-2013 Stephen Colebourne
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
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * An exchange rate defining the way to convert between amounts in two currencies.
 * <p>
 * This represents the exchange rate between two currencies.
 * <p>
 * 1 major unit of <strong><code>base currency</code></strong> = <strong><code>rate</code></strong> major units of
 * <strong><code>counter currency</code></strong>.
 * <p>
 * Operations on an exchange rate are provided via {@link ExchangeRateOperations}.
 * A default implementation can be acquired using {@link #operations(int, RoundingMode)}.
 * <p>
 * This class is immutable and thread-safe.
 * 
 * @author tpasierb
 */
public final class ExchangeRate implements Serializable {

    /**
     * The serialisation version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Regular expression for parsing.
     */
    private static final Pattern PARSE_REGEX = Pattern.compile("([A-Z]{3})[/]([A-Z]{3}) ([+-]?[0-9]*[.]?[0-9]*)");

    /**
     * The base currency.
     */
    private final CurrencyUnit base;
    /**
     * The counter currency.
     */
    private final CurrencyUnit counter;
    /**
     * Conversion rate, 1 base = rate x counter.
     */
    private final BigDecimal rate;

    //-----------------------------------------------------------------------
    /**
     * Obtains the identity rate for the specified currency.
     * <p>
     * This is the exchange rate between a currency and itself, which is one.
     * 
     * @param currency  the single currency, not null
     * @return the identity exchange rate, not null
     */
    public static ExchangeRate identity(CurrencyUnit currency) {
        return new ExchangeRate(currency, currency, BigDecimal.ONE);
    }

    /**
     * Obtains an {@code ExchangeRate} from the base, counter and conversion rate.
     * <p>
     * The exchange rate can be expressed as '1 AAA = 1.25 BBB' or 'AAA/BBB 1.25'.
     * The base currency is 'AAA' and the counter currency is 'BBB'.
     * 
     * @param base  the base currency, not null
     * @param counter  the counter currency, not null
     * @param rate  the conversion rate, not null
     * @return an instance of exchange rate, not null
     * @throws IllegalArgumentException if the given rate is less or equal to 0
     * @throws IllegalArgumentException if the rate is not 1 when the currencies are the same
     */
    public static ExchangeRate of(CurrencyUnit base, CurrencyUnit counter, BigDecimal rate) {
        return new ExchangeRate(base, counter, rate);
    }

    //-----------------------------------------------------------------------
    /**
     * Parses an instance of {@code ExchangeRate} from a string.
     * <p>
     * The string format is '${base}/${counter} ${rate}'.
     * The base and counter are three letter currency codes.
     * The rate must match the regular expression {@code [+-]?[0-9]*[.]?[0-9]*}.
     * This matches the output from {@link #toString()}.
     *
     * @param rateStr  the rate string to parse, not null
     * @return the parsed instance, never null
     * @throws IllegalArgumentException if the string is malformed
     */
    @FromString
    public static ExchangeRate parse(String rateStr) {
        MoneyUtils.checkNotNull(rateStr, "Exchange rate cannot be null");
        Matcher matcher = PARSE_REGEX.matcher(rateStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Exchange rate '%s' cannot be parsed", rateStr));
        }
        String base = matcher.group(1);
        String counter = matcher.group(2);
        String rate = matcher.group(3);
        return ExchangeRate.of(CurrencyUnit.getInstance(base), CurrencyUnit.getInstance(counter), new BigDecimal(rate));
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance of ExchangeRate with the given parameters.
     * 
     * @param base  the base currency, not null
     * @param counter  the counter currency, not null
     * @param rate  the conversion rate, not null
     * @throws IllegalArgumentException if the given rate is less or equal to 0
     * @throws IllegalArgumentException if the rate is not 1 when the currencies are the same
     */
    ExchangeRate(CurrencyUnit base, CurrencyUnit counter, BigDecimal rate) {
        MoneyUtils.checkNotNull(rate, "Rate must not be null");
        MoneyUtils.checkNotNull(counter, "Source currency must not be null");
        MoneyUtils.checkNotNull(base, "Target currency must not be null");
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rate must be greater than 0");
        }
        if (counter == base && rate.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalArgumentException("Rate must be 1 if base and counter currencies are the same");
        }
        this.rate = rate;
        this.counter = counter;
        this.base = base;
    }

    //-----------------------------------------------------------------------
    /**
     * Block malicious data streams.
     * 
     * @return never
     * @throws InvalidObjectException
     */
    private Object readResolve() throws ObjectStreamException {
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

    //-----------------------------------------------------------------------
    /**
     * Gets the conversion rate between the base and counter currencies.
     * <p>
     * The rate is the amount of the counter currency obtained for each unit of the base currency.
     * 
     * @return the exchange rate, not null
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Gets the base currency.
     * <p>
     * The exchange rate can be expressed as '1 AAA = 1.25 BBB' or 'AAA/BBB 1.25'.
     * The base currency is 'AAA'.
     * 
     * @return the base currency, not null
     */
    public CurrencyUnit getBase() {
        return base;
    }

    /**
     * Gets the counter currency.
     * <p>
     * The exchange rate can be expressed as '1 AAA = 1.25 BBB' or 'AAA/BBB 1.25'.
     * The counter currency is 'BBB'.
     * 
     * @return the source currency
     */
    public CurrencyUnit getCounter() {
        return counter;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ExchangeRate} with the specified rate changed.
     * <p>
     * This method returns an exchange rate with the same base and counter
     * currencies, but with the specified rate.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param rate  the rate to set, positive
     * @return an {@code ExchangeRate} based on this object with the specified rate, never null
     * @throws IllegalArgumentException if the rate is zero or negative
     */
    public ExchangeRate withRate(BigDecimal rate) {
        return new ExchangeRate(base, counter, rate);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance of an object that knows how to perform operations
     * on this exchange rate using default scale and rounding.
     * <p>
     * This method will perform operations using a scale of 16 and a rounding
     * mode or {@link RoundingMode#HALF_EVEN EVEN}.
     * 
     * @return the exchange rate operations, not null
     */
    public ExchangeRateOperations operations() {
        return operations(16, RoundingMode.HALF_EVEN);
    }

    /**
     * Creates an instance of an object that knows how to perform operations
     * on this exchange rate.
     * 
     * @param scale  the scale that will be used for calculations
     * @param roundingMode  the rounding mode to use if rounding is necessary, not null
     * @return the exchange rate operations, not null
     */
    public ExchangeRateOperations operations(int scale, RoundingMode roundingMode) {
        return new DefaultExchangeRateOperations(this, scale, roundingMode);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this exchange rate equals another.
     * <p>
     * The comparison checks that all three elements, the two currencies and
     * the conversion rate are equal, taking into account the scale of the rate.
     * 
     * @return true if this instance equals the other instance
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ExchangeRate) {
            ExchangeRate other = (ExchangeRate) obj;
            return rate.equals(other.rate) && base.equals(other.base) && counter.equals(other.counter); 
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
        int result = 1;
        result = 31 * result + rate.hashCode();
        result = 31 * result + base.hashCode();
        result = 31 * result + counter.hashCode();
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the exchange rate as a string.
     * <p>
     * The string format is '${base}/${counter} ${rate}'.
     * The currencies are formatted as 3 letter ISO currency codes
     * The rate is formatted as per {@link BigDecimal#toPlainString()}.
     * 
     * @return the string representation of this monetary value, never null
     */
    @Override
    @ToString
    public String toString() {
        return base + "/" + counter + ' ' + rate.toPlainString();
    }

}
