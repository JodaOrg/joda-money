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
 * Indicates that conversion cannot be performed.
 * <p>
 * This exception makes no guarantees about immutability or thread-safety.
 */
public class NotExchangeableException extends IllegalArgumentException {

    /** Serialization lock. */
    private static final long serialVersionUID = 1L;

    /** The money that could not be converted. */
    private final BigMoneyProvider money;
    /** The rate that could not be used. */
    private final ExchangeRate exchangeRate;

    /**
     * Creates an instance.
     * 
     * @param money  the monetary amount that could not be converted, may be null
     * @param exchangeRate  the rate that could not be used, may be null
     */
    public NotExchangeableException(BigMoneyProvider money, ExchangeRate exchangeRate) {
        super(String.format("%s is not exchangeable using %s", money != null ? money : "Money <null>", exchangeRate != null ? exchangeRate
                : "ExchangeRate <null>"));
        this.money = money;
        this.exchangeRate = exchangeRate;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the monetary amount that could not be converted.
     * 
     * @return the money at fault, may be null
     */
    public BigMoneyProvider getMoney() {
        return money;
    }

    /**
     * Gets the exchange rate that could not be used.
     * 
     * @return the rate at fault, may be null
     */
    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

}
