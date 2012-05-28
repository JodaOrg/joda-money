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
package org.joda.money.format;

import java.math.BigDecimal;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

/**
 * Context used when parsing money.
 * <p>
 * This class is mutable and intended for use by a single thread.
 * A new instance is created for each parse.
 */
public final class MoneyParseContext extends ParseContext {

    /**
     * The parsed currency.
     */
    private CurrencyUnit currency;
    /**
     * The parsed amount.
     */
    private BigDecimal amount;

    /**
     * Constructor.
     * 
     * @param locale  the locale, not null
     * @param text  the text to parse, not null
     * @param index  the current text index
     */
    MoneyParseContext(Locale locale, CharSequence text, int index) {
    	super(locale, text, index);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the parsed currency.
     * 
     * @return the parsed currency, null if not parsed yet
     */
    public CurrencyUnit getCurrency() {
        return currency;
    }

    /**
     * Sets the parsed currency.
     * 
     * @param currency  the parsed currency, may be null
     */
    public void setCurrency(CurrencyUnit currency) {
        this.currency = currency;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the parsed amount.
     * 
     * @return the parsed amount, null if not parsed yet
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the parsed currency.
     * 
     * @param amount  the parsed amount, may be null
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Checks if the context contains a currency and amount suitable for creating
     * a monetary value.
     * 
     * @return true if able to create a monetary value
     */
    public boolean isComplete() {
        return currency != null && amount != null;
    }

    /**
     * Converts the context to a {@code BigMoney}.
     * 
     * @return the monetary value, never null
     * @throws MoneyFormatException if either the currency or amount is missing
     */
    public BigMoney toBigMoney() {
        if (currency == null) {
            throw new MoneyFormatException("Cannot convert to BigMoney as no currency found");
        }
        if (amount == null) {
            throw new MoneyFormatException("Cannot convert to BigMoney as no amount found");
        }
        return BigMoney.of(currency, amount);
    }

}
