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

/**
 * Defines operations performed on exchange rates.
 * <p>
 * Object implementing this interface are contextual objects operating on an underlying {@code ExchangeRate}.
 * Some operations change the underlying rate, while others just perform operations using it.
 * The reasoning for this kind of object is to wrap the settings, such as scale and rounding,
 * allowing several operations with the same settings.
 */
public interface ExchangeRateOperations {

    /**
     * Converts the given {@link BigMoney value in currency} to a value in the other currency
     * using the underlying exchange rate.
     * <p>
     * The conversion uses the underlying exchange rate.
     * The monetary value must have either the base or counter currency in order to perform
     * the conversion.
     * 
     * @param money  the monetary value to be exchanged, not null
     * @return the equivalent in the opposite currency, not null
     * @throws NotExchangeableException if exchange rate cannot be used
     */
    BigMoney exchange(BigMoney money);

    /**
     * Converts the given {@link Money value in currency} to a value in the other currency
     * using the underlying exchange rate.
     * <p>
     * The conversion uses the underlying exchange rate.
     * The monetary value must have either the base or counter currency in order to perform
     * the conversion.
     * 
     * @param money  the monetary value to be exchanged, not null
     * @return the equivalent in the opposite currency, not null
     * @see ExchangeRateOperations#exchange(BigMoney)
     */
    Money exchange(Money money);

    /**
     * Inverts the underlying exchange rate.
     * <p>
     * This operation swaps the currencies and inverts the rate.
     * The inverted rate can be retrieved using {@link #getExchangeRate()}.
     * 
     * @return {@code this}, for method chaining, not null
     */
    ExchangeRateOperations invert();

    /**
     * Combines {@link ExchangeRate} represented by this object with the given one.
     * <p>
     * This change the underlying exchange rate by combining it with the specified rate,
     * eliminating the common currency.
     * The combined rate can be retrieved using {@link #getExchangeRate()}.
     * For example, 'USD/GBP 3' and 'GBP/EUR 2' then the result is 'USD/EUR 6'.
     * <p>
     * The common currency can be in either the base or counter currency position.
     * The non-common currency from this exchange rate will become the new base.
     * The non-common currency in the input exchange rate will become the new counter.
     * The common currency will "disappear".
     * <br>
     * For Example:
     * <pre>
     *  thisRate = ExchangeRate.parse("USD/PLN 3.50");
     *  otherRate = ExchangeRate.parse("EUR/PLN 4.00");
     * 
     *  thisRate.combine(otherRate) // 'USD/EUR 0.8750'
     *  otherRate.combine(thisRate) // 'EUR/USD 1.1429' (rounded for this example)
     * </pre>
     * <p>
     * A special case is when exchange rates for the same sets of currencies are combined
     * no matter the position. In this case, the rates are ignored.
     * Combining two such exchange rate will result in the "identity" rate for
     * {@code this} rate's target currency.
     * <br>
     * For Example:
     * <pre>
     *  rate1 = ExchangeRate.parse("EUR/PLN 3.22");
     *  rate2 = ExchangeRate.parse("PLN/EUR 3.19");
     * 
     *  rate1.combine(rate2)   // 'EUR/EUR 1'
     *  rate2.combine(rate1)   // 'PLN/PLN 1'
     * </pre>
     * <p>
     * The resulting exchange rate will have the scale and rounding mode of this instance.
     * 
     * @param other  the exchange rate to be combine with this instance
     * @return {@code this}, for method chaining, not null
     * @throws NullPointerException if the other object is null
     * @throws NoCommonCurrencyException if objects this and other have no common currency
     */
    ExchangeRateOperations combine(ExchangeRate other);

    /**
     * Gets the {@code ExchangeRate}.
     * 
     * @return the current rate, not null
     */
    ExchangeRate getExchangeRate();

}
