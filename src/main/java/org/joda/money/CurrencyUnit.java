/*
 *  Copyright 2009 Stephen Colebourne
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A unit of currency.
 * <p>
 * This class represents a unit of currency such as the British Pound, Euro
 * or US Dollar.
 *
 * @author Stephen Colebourne
 */
public final class CurrencyUnit implements Comparable<CurrencyUnit>, Serializable {

    /**
     * The serialisation version.
     */
    private static final long serialVersionUID = 327835287287L;
    /**
     * Map of registered currencies.
     */
    private static final ConcurrentMap<String, CurrencyUnit> cCurrencies = new ConcurrentHashMap<String, CurrencyUnit>();
    static {
        registerCurrency("USD", 2);
        registerCurrency("CAD", 2);
        registerCurrency("GBP", 2);
        registerCurrency("EUR", 2);
        registerCurrency("JPY", 0);
        registerCurrency("XXX", -1);
    }
    /**
     * The currency, not null.
     */
    private final String iCode;
    /**
     * The number of decimal places.
     */
    private final transient int iDecimalPlaces;

    //-----------------------------------------------------------------------
    /**
     * Registers a currency allowing it to be used.
     * <p>
     * This class only permits known currencies to be returned.
     * To achieve this, all currencies have to be registered in advance, at
     * application startup.
     *
     * @param currency  the currency, not null
     * @param decimalPlaces  the number of decimal places that the currency
     *  normally has, from 0 to 3, or -1 for a pseudo-currency
     * @return the new instance, never null
     */
    public static CurrencyUnit registerCurrency(String currencyCode, int decimalPlaces) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        if (decimalPlaces < -1 || decimalPlaces > 3) {
            throw new IllegalArgumentException("Invalid number of decimal places");
        }
        CurrencyUnit currency = new CurrencyUnit(currencyCode, decimalPlaces);
        cCurrencies.putIfAbsent(currencyCode, currency);
        return cCurrencies.get(currencyCode);
    }

    /**
     * Registers a currency allowing it to be used.
     * <p>
     * This class only permits known currencies to be returned.
     * To achieve this, all currencies have to be registered in advance, at
     * application startup.
     *
     * @return the sorted, independent, list of all registered currencies, never null
     */
    public static List<CurrencyUnit> registeredCurrencies() {
        ArrayList<CurrencyUnit> list = new ArrayList<CurrencyUnit>(cCurrencies.values());
        Collections.sort(list);
        return list;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of Money in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     *
     * @param currency  the currency, not null
     * @param amount  the amount of money, not null
     * @return the singleton instance, never null
     */
    public static CurrencyUnit of(Currency currency) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        return of(currency.getCurrencyCode());
    }

    /**
     * Gets an instance of Money in the specified currency.
     * <p>
     * This allows you to create an instance with a specific currency and amount.
     *
     * @param currency  the currency, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit of(String currencyCode) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        CurrencyUnit currency = cCurrencies.get(currencyCode);
        if (currency == null) {
            throw new MoneyException("Unknown currency: " + currencyCode);
        }
        return currency;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param code  the currency code, not null
     * @param decimalPlaces  the decimal places, not null
     */
    private CurrencyUnit(String code, int decimalPlaces) {
        iCode = code;
        iDecimalPlaces = decimalPlaces;
    }

    /**
     * Resolves singletons.
     * 
     * @return the singleton instance
     */
    private Object readResolve() {
        return CurrencyUnit.of(iCode);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ISO-4217 currency code.
     * 
     * @return the currency, never null
     */
    public String getCurrencyCode() {
        return iCode;
    }

    /**
     * Gets the number of decimal places typically used by this currency.
     * <p>
     * This method returns 0 for pseudo-currencies.
     * 
     * @return the decimal places, from 0 to 3
     */
    public int getDecimalPlaces() {
        return iDecimalPlaces < 0 ? 0 : iDecimalPlaces;
    }

    /**
     * Checks if this is a pseudo-currency.
     * 
     * @return true if this is a pseudo-currency
     */
    public boolean isPseudoCurrency() {
        return iDecimalPlaces < 0;
    }

    /**
     * Gets the default number of fractional digits for the currency.
     * <p>
     * This method matches the return value of the similarly named JDK method
     * where pseudo-currencies return -1.
     * 
     * @return the fractional digits, from 0 to 3, or -1 for pseudo-currencies
     */
    public int getDefaultFractionDigits() {
        return iDecimalPlaces;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the JDK currency instance equivalent to this currency.
     * 
     * @return the JDK currency instance, never null
     */
    public Currency toCurrency() {
        return Currency.getInstance(iCode);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this currency to another by alphabetical comparison of the code.
     * 
     * @param other  the other currency, not null
     * @return negative if earlier alphabetically, 0 if equal, positive if greater alphabetically
     */
    public int compareTo(CurrencyUnit other) {
        return iCode.compareTo(other.iCode);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the currency code as a string.
     * 
     * @return the currency code, never null
     */
    public String toString() {
        return iCode;
    }

}
