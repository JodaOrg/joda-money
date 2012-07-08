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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

/**
 * A default implementation of {@link ExchangeRateOperations} operating on a externally provided {@link ExchangeRate}.
 * 
 * @author tpasierb
 */
class DefaultExchangeRateOperations implements ExchangeRateOperations {

    private ExchangeRate exchangeRate;
    private final int scale;
    private final RoundingMode roundingMode;

    DefaultExchangeRateOperations(ExchangeRate exchangeRate, int scale, RoundingMode roundingMode) {
        Utils.notNull(roundingMode, "Exchange rate must not be null");
        Utils.isTrue(scale >= 0, "Scale must be greater or equal to 0");
        this.exchangeRate = exchangeRate;
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.joda.money.ExchangeRateOperations#invert()
     */
    public ExchangeRateOperations invert() {
        this.exchangeRate = ExchangeRate.of(BigDecimal.ONE.divide(exchangeRate.getRate(), scale, roundingMode), exchangeRate.getTarget(),
                exchangeRate.getSource());
        return this;
    }

    /**
     * Converts the given {@link BigMoney} to an equivalent in the other currency.
     * 
     * This object's rate is used directly if the given Money's currency is equal to this exchange rate's target
     * currency. Otherwise this exchange rate is inverted before the conversion is made.
     * 
     * The following formula is used for calculation:<br>
     * 
     * <p>
     * 1 major unit of <strong><code>target currency</code></strong> = <strong><code>rate</code></strong> major units of
     * <strong><code>source currency</code></strong>.
     * </p>
     * 
     * For the following example exchange rate: <code>1 USD = 2.3428 PLN</code><br/>
     * passing {@link Money 100 USD} as an argument the method will return the equivalent in PLN. On the other hand when
     * the method receives {@link Money 20 PLN} it will return an equivalent in USD.
     * 
     * @see ExchangeRateOperations
     * @param toExchange the value in currency to be exchanged
     * @return the equivalent in other currency
     * @throws NullPointerException if the given parameter is <code>null</code>
     * @throws NotExchangeableException if this exchange rate cannot be used for conversion of the given {@link Money}
     */
    public BigMoney exchange(BigMoney toExchange) {
        Utils.notNull(toExchange, "Money to exchange cannot be null");

        BigDecimal rate = null;
        CurrencyUnit resultingCurrency = null;

        if (toExchange.getCurrencyUnit().equals(exchangeRate.getTarget())) {
            resultingCurrency = exchangeRate.getSource();
            rate = exchangeRate.getRate();
        } else if (toExchange.getCurrencyUnit().equals(exchangeRate.getSource())) {
            resultingCurrency = exchangeRate.getTarget();
            rate = invert().getExchangeRate().getRate();
        } else {
            throw new NotExchangeableException(toExchange, exchangeRate);
        }

        return BigMoney.of(resultingCurrency, toExchange.getAmount().multiply(rate));
    }

    /**
     * Uses this ExchangeRate's rounding mode for creating the resulting {@link Money} instance.
     * 
     * @param toExchange the value in currency to be exchanged
     * @return the equivalent in other currency
     * @see ExchangeRate#exchange(BigMoney)
     */
    public Money exchange(Money toExchange) {
        return Money.of(exchange(BigMoney.of(toExchange)), this.roundingMode);
    }

    /**
     * Combines this object with the given one. This {@link ExchangeRate} and the given one have to have a common
     * currency no matter the position "source" or "target".
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
     * @return the combined exchange rate
     * @throws NullPointerException if the other object is null
     * @throws NoCommonCurrencyException if objects this and other have no common currency which means that it is
     *             impossible to create a combination of the two exchange rates
     */
    public ExchangeRateOperations combine(ExchangeRate other) {
        Utils.notNull(other, "Exchange rate to be combined must not be null");

        CurrencyUnit commonCurrency = null;

        Set<CurrencyUnit> currencies = new HashSet<CurrencyUnit>();

        currencies.add(other.getSource());
        currencies.add(other.getTarget());
        if (!currencies.add(exchangeRate.getTarget())) {
            commonCurrency = exchangeRate.getTarget();
        }
        if (!currencies.add(exchangeRate.getSource())) {
            commonCurrency = exchangeRate.getSource();
        }

        if (commonCurrency == null) {
            throw new NoCommonCurrencyException(exchangeRate, other);
        }

        ExchangeRate a = exchangeRate;
        ExchangeRate b = other;

        if (!a.getSource().equals(commonCurrency)) {
            a = a.operations(scale, roundingMode).invert().getExchangeRate();
        }
        if (!b.getSource().equals(commonCurrency)) {
            b = b.operations(scale, roundingMode).invert().getExchangeRate();
        }

        BigDecimal newRate = null;

        if (b.getTarget() == a.getTarget()) {
            newRate = BigDecimal.ONE;
        } else {
            newRate = a.getRate().divide(b.getRate(), scale, roundingMode).stripTrailingZeros();
        }

        this.exchangeRate = ExchangeRate.of(newRate, b.getTarget(), a.getTarget());
        return this;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

}
