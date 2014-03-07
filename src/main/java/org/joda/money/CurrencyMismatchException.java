/*
 *  Copyright 2009-present, Stephen Colebourne
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
 * Exception thrown when a monetary operation fails due to mismatched currencies.
 * <p>
 * For example, this exception would be thrown when trying to add a monetary
 * value in one currency to a monetary value in a different currency.
 * <p>
 * This exception makes no guarantees about immutability or thread-safety.
 */
public class CurrencyMismatchException extends IllegalArgumentException {

    /** Serialization lock. */
    private static final long serialVersionUID = 1L;

    /** First currency. */
    private final CurrencyUnit firstCurrency;
    /** Second currency. */
    private final CurrencyUnit secondCurrency;

    /**
     * Constructor.
     * 
     * @param firstCurrency  the first currency, may be null
     * @param secondCurrency  the second currency, not null
     */
    public CurrencyMismatchException(CurrencyUnit firstCurrency, CurrencyUnit secondCurrency) {
        super("Currencies differ: " +
                (firstCurrency != null ? firstCurrency.getCode() : "null") + '/' +
                (secondCurrency != null ? secondCurrency.getCode() : "null"));
        this.firstCurrency = firstCurrency;
        this.secondCurrency = secondCurrency;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the first currency at fault.
     * 
     * @return the currency at fault, may be null
     */
    public CurrencyUnit getFirstCurrency() {
        return firstCurrency;
    }

    /**
     * Gets the second currency at fault.
     * 
     * @return the currency at fault, may be null
     */
    public CurrencyUnit getSecondCurrency() {
        return secondCurrency;
    }

}
