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

/**
 * Defines operations performed on {@link ExchangeRate}s. Object implementing this interface are contextual objects
 * operating on "current" ExchangeRate. Some operations modify the underlying ExchageRate and others just perform
 * operations using the underlying ExchangeRate. The reasoning behind having this can of object is that one can create
 * such an object with the given settings and then perform serveral operations with the same settings.
 * 
 * @author tpasierb
 */
public interface ExchangeRateOperations {

    /**
     * Converts the given {@link BigMoney value in currency} to value in the other currency using the underlying
     * ExchangeRate. The conversion is possible if either source or target currency of the exchange rate represented by
     * this object matches the currency of the given {@link BigMoney}.
     * 
     * @param toExchange the value in currency to be converted
     * @return the equivalent in other currency
     * @throws NullPointerException if the given parameter is <code>null</code>
     * @throws NotExchangeableException if exchange rate represented by this object cannot be used for conversion of the
     *             given {@link Money}
     */
    BigMoney exchange(BigMoney toExchange);

    /**
     * Converts the given {@link Money value in currency} to value in the other currency returning an instance of
     * {@link Money}.
     * 
     * @param toExchange the value in currency to be exchanged
     * @return the equivalent in other currency
     * @see ExchangeRateOperations#exchange(BigMoney)
     */
    Money exchange(Money toExchange);

    /**
     * Inverts the underlying ExchangeRate. The operation involves swapping currencies and inverting the rate. The
     * inverted rate can be retrieved using {@link #getExchangeRate()}.
     * 
     * @return ExchangeRateOperations this contextual operations object allowing fluent calls
     */
    ExchangeRateOperations invert();

    /**
     * Combines {@link ExchangeRate} represented by this object with the given one. This {@link ExchangeRate} and the
     * given one have to have a common currency no matter the position "source" or "target".
     * 
     * This object's non common currency will become the target currency and the other object's non common currency will
     * become the source currency of the returned object. The common currency will "disappear".
     * 
     * <br>
     * Example:
     * 
     * <pre>
     * this rate:  1 USD = 3.50 PLN
     * other rate: 1 EUR = 4.00 PLN
     * 
     * this.combine(other) results in 1 USD = 0.8750 EUR
     * other.combine(this) results in 1 EUR = 1.1429 USD (rounded to 4 decimal places *)
     * </pre>
     * 
     * A special case is when exchange rates for the same sets of currencies are combined no matter the position. In
     * this case they may or may not differ on the rate field. Combining two such exchange rate will result in
     * "identity" rate for <code>this</code> rate's target currency.
     * 
     * <br>
     * Example:
     * 
     * <pre>
     * this rate:  1 EUR = 3.22 PLN
     * other rate: 1 EUR = 3.19 PLN
     * 
     * this.combine(other) results in 1 EUR = 1 EUR.
     * </pre>
     * 
     * The resulting ExchangeRate will have the scale and roundingMode of this instance.
     * 
     * <pre>
     * * rounding for this example only, internally scale may be greater
     * </pre>
     * 
     * @param other the exchange rate to be combine with this instance
     * @return this contextual operations object allowing fluent calls
     * @throws NullPointerException if the other object is null
     * @throws NoCommonCurrencyException if objects this and other have no common currency which means that it is
     *             impossible to create a combination of the two exchange rates
     */
    ExchangeRateOperations combine(ExchangeRate other);

    /**
     * Returns the current ExchangeRate.
     * 
     * @return the current ExchangeRate
     */
    ExchangeRate getExchangeRate();
}
