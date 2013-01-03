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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

/**
 * A default implementation of {@code ExchangeRateOperations} operating on an externally
 * provided {@link ExchangeRate}.
 */
class DefaultExchangeRateOperations implements ExchangeRateOperations {

    /**
     * The underlying exchange rate.
     */
    private ExchangeRate exchangeRate;
    /**
     * The scale to use.
     */
    private final int scale;
    /**
     * The rounding mode to use.
     */
    private final RoundingMode roundingMode;

    /**
     * Creates an instance.
     * 
     * @param exchangeRate  the exchange rate, not null
     * @param scale  the scale, zero or greater
     * @param roundingMode  the rounding mode, not null
     * @throws IllegalArgumentException if input parameters are invalid
     */
    DefaultExchangeRateOperations(ExchangeRate exchangeRate, int scale, RoundingMode roundingMode) {
        MoneyUtils.checkNotNull(roundingMode, "Exchange rate must not be null");
        if (scale < 0) {
            throw new IllegalArgumentException("Scale must be greater or equal to 0");
        }
        this.exchangeRate = exchangeRate;
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    //-----------------------------------------------------------------------
    public ExchangeRateOperations invert() {
        this.exchangeRate = ExchangeRate.of(exchangeRate.getCounter(), exchangeRate.getBase(),
                BigDecimal.ONE.divide(exchangeRate.getRate(), scale, roundingMode));
        return this;
    }

    public BigMoney exchange(BigMoney toExchange) {
        MoneyUtils.checkNotNull(toExchange, "Money to exchange cannot be null");

        BigDecimal rate = null;
        CurrencyUnit resultingCurrency = null;
        if (toExchange.getCurrencyUnit().equals(exchangeRate.getBase())) {
            resultingCurrency = exchangeRate.getCounter();
            rate = exchangeRate.getRate();
        } else if (toExchange.getCurrencyUnit().equals(exchangeRate.getCounter())) {
            resultingCurrency = exchangeRate.getBase();
            rate = invert().getExchangeRate().getRate();
        } else {
            throw new NotExchangeableException(toExchange, exchangeRate);
        }

        return BigMoney.of(resultingCurrency, toExchange.getAmount().multiply(rate));
    }

    public Money exchange(Money toExchange) {
        return Money.of(exchange(BigMoney.of(toExchange)), this.roundingMode);
    }

    public ExchangeRateOperations combine(ExchangeRate other) {
        MoneyUtils.checkNotNull(other, "Exchange rate to be combined must not be null");

        CurrencyUnit commonCurrency = null;
        Set<CurrencyUnit> currencies = new HashSet<CurrencyUnit>();
        currencies.add(other.getCounter());
        currencies.add(other.getBase());
        if (!currencies.add(exchangeRate.getBase())) {
            commonCurrency = exchangeRate.getBase();
        }
        if (!currencies.add(exchangeRate.getCounter())) {
            commonCurrency = exchangeRate.getCounter();
        }
        if (commonCurrency == null) {
            throw new NoCommonCurrencyException(exchangeRate, other);
        }

        ExchangeRate a = exchangeRate;
        ExchangeRate b = other;
        if (!a.getCounter().equals(commonCurrency)) {
            a = a.operations(scale, roundingMode).invert().getExchangeRate();
        }
        if (!b.getCounter().equals(commonCurrency)) {
            b = b.operations(scale, roundingMode).invert().getExchangeRate();
        }

        BigDecimal newRate = null;
        if (b.getBase() == a.getBase()) {
            newRate = BigDecimal.ONE;
        } else {
            newRate = a.getRate().divide(b.getRate(), scale, roundingMode).stripTrailingZeros();
        }

        this.exchangeRate = ExchangeRate.of(a.getBase(), b.getBase(), newRate);
        return this;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

}
