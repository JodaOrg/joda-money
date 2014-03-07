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

import java.util.List;

/**
 * Provider for available currencies.
 */
public abstract class CurrencyUnitDataProvider {

    /**
     * Registers all the currencies known by this provider.
     * 
     * @throws Exception if an error occurs
     */
    protected abstract void registerCurrencies() throws Exception;

    /**
     * Registers a currency allowing it to be used.
     * <p>
     * This method is called by {@link #registerCurrencies()} to perform the
     * actual creation of a currency.
     *
     * @param currencyCode  the currency code, not null
     * @param numericCurrencyCode  the numeric currency code, -1 if none
     * @param decimalPlaces  the number of decimal places that the currency
     *  normally has, from 0 to 3, or -1 for a pseudo-currency
     * @param countryCodes  the country codes to register the currency under, not null
     */
    protected final void registerCurrency(String currencyCode, int numericCurrencyCode, int decimalPlaces, List<String> countryCodes) {
        CurrencyUnit.registerCurrency(currencyCode, numericCurrencyCode, decimalPlaces, countryCodes, true);
    }

}
