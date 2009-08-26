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
import java.util.Locale;
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
     * Map of registered currencies by code.
     */
    private static final ConcurrentMap<String, CurrencyUnit> cCurrenciesByCode = new ConcurrentHashMap<String, CurrencyUnit>();
    /**
     * Map of registered currencies by country.
     */
    private static final ConcurrentMap<String, CurrencyUnit> cCurrenciesByCountry = new ConcurrentHashMap<String, CurrencyUnit>();
    static {
        try {
            String clsName = System.getProperty(
                    "org.joda.money.CurrencyUnitDataProvider", "org.joda.money.CurrencyUnitDataProvider$DefaultProvider");
            Class<? extends CurrencyUnitDataProvider> cls =
                    CurrencyUnit.class.getClassLoader().loadClass(clsName).asSubclass(CurrencyUnitDataProvider.class);
            cls.newInstance().registerCurrencies();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex.toString(), ex);
        }
    }
    /**
     * The currency, not null.
     */
    private final String iCode;
    /**
     * The number of decimal places.
     */
    private final transient int iNumericCode;
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
     * @param currencyCode  the currency code, not null
     * @param numericCurrencyCode  the numeric currency code, -1 if none
     * @param decimalPlaces  the number of decimal places that the currency
     *  normally has, from 0 to 3, or -1 for a pseudo-currency
     * @param countryCodes  the country codes to register the currency under, not null
     * @return the new instance, never null
     */
    static CurrencyUnit registerCurrency(String currencyCode, int numericCurrencyCode, int decimalPlaces, List<String> countryCodes) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        if (numericCurrencyCode < -1 || numericCurrencyCode > 999) {
            throw new IllegalArgumentException("Invalid numeric code");
        }
        if (decimalPlaces < -1 || decimalPlaces > 3) {
            throw new IllegalArgumentException("Invalid number of decimal places");
        }
        MoneyUtils.checkNotNull(countryCodes, "Country codes must not be null");
        
        CurrencyUnit currency = new CurrencyUnit(currencyCode, numericCurrencyCode, decimalPlaces);
        if (cCurrenciesByCode.putIfAbsent(currencyCode, currency) != null) {
            throw new IllegalArgumentException("Currency already registered: " + currencyCode);
        }
        for (String countryCode : countryCodes) {
            if (cCurrenciesByCountry.putIfAbsent(countryCode, currency) != null) {
                throw new IllegalArgumentException("Currency already registered for country: " + countryCode);
            }
        }
        return cCurrenciesByCode.get(currencyCode);
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
        ArrayList<CurrencyUnit> list = new ArrayList<CurrencyUnit>(cCurrenciesByCode.values());
        Collections.sort(list);
        return list;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the Currency instance matching the specified currency.
     *
     * @param currency  the currency, not null
     * @return the singleton instance, never null
     */
    public static CurrencyUnit of(Currency currency) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        return of(currency.getCurrencyCode());
    }

    /**
     * Gets the Currency instance for the specified currency code.
     *
     * @param currencyCode  the currency code, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit of(String currencyCode) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        CurrencyUnit currency = cCurrenciesByCode.get(currencyCode);
        if (currency == null) {
            throw new MoneyException("Unknown currency: " + currencyCode);
        }
        return currency;
    }

    /**
     * Gets the Currency instance for the specified currency code.
     * <p>
     * This method exists to match the API of {@link Currency}.
     *
     * @param currencyCode  the currency code, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit getInstance(String currencyCode) {
        return of(currencyCode);
    }

    /**
     * Gets the Currency instance for the specified currency code.
     *
     * @param currencyCode  the currency code, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit of(Locale locale) {
        MoneyUtils.checkNotNull(locale, "Locale must not be null");
        CurrencyUnit currency = cCurrenciesByCountry.get(locale.getCountry());
        if (currency == null) {
            throw new MoneyException("Unknown currency for locale: " + locale);
        }
        return currency;
    }

    /**
     * Gets the Currency instance for the specified currency code.
     * <p>
     * This method exists to match the API of {@link Currency}.
     *
     * @param currencyCode  the currency code, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit getInstance(Locale locale) {
        return of(locale);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param code  the currency code, not null
     * @param numericCurrencyCode  the numeric currency code, -1 if none
     * @param decimalPlaces  the decimal places, not null
     */
    private CurrencyUnit(String code, int numericCurrencyCode, int decimalPlaces) {
        iCode = code;
        iNumericCode = numericCurrencyCode;
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
     * Gets the three-letter ISO-4217 currency code.
     * <p>
     * This method matches the API of {@link Currency}.
     * 
     * @return the currency code, never null
     */
    public String getCurrencyCode() {
        return iCode;
    }

    /**
     * Gets the numeric ISO-4217 currency code as a three digit string.
     * <p>
     * If there is no valid code, then an empty string is returned.
     * 
     * @return the three digit numeric currency code, empty is no code, never null
     */
    public String getNumeric3Code() {
        if (iNumericCode < 0) {
            return "";
        }
        String str = Integer.toString(iNumericCode);
        if (str.length() == 1) {
            return "00" + str;
        }
        if (str.length() == 2) {
            return "0" + str;
        }
        return str;
    }

    /**
     * Gets the numeric ISO-4217 currency code.
     * 
     * @return the numeric currency code
     */
    public int getNumericCode() {
        return iNumericCode;
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
     * <p>
     * This method matches the API of {@link Currency}.
     * 
     * @return the fractional digits, from 0 to 3, or -1 for pseudo-currencies
     */
    public int getDefaultFractionDigits() {
        return iDecimalPlaces;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the symbol for this locale from the JDK.
     * <p>
     * If this currency doesn't have a JDK equivalent, then the currency code
     * is returned.
     * 
     * @return the JDK currency instance, never null
     * @throws IllegalArgumentException if no matching currency exists in the JDK
     */
    public String getSymbol() {
        try {
            return Currency.getInstance(iCode).getSymbol();
        } catch (IllegalArgumentException ex) {
            return iCode;
        }
    }

    /**
     * Gets the symbol for this locale from the JDK.
     * <p>
     * If this currency doesn't have a JDK equivalent, then the currency code
     * is returned.
     * 
     * @return the JDK currency instance, never null
     * @throws IllegalArgumentException if no matching currency exists in the JDK
     */
    public String getSymbol(Locale locale) {
        MoneyUtils.checkNotNull(locale, "Locale must not be null");
        try {
            return Currency.getInstance(iCode).getSymbol(locale);
        } catch (IllegalArgumentException ex) {
            return iCode;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the JDK currency instance equivalent to this currency.
     * 
     * @return the JDK currency instance, never null
     * @throws IllegalArgumentException if no matching currency exists in the JDK
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
