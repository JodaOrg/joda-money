/*
 *  Copyright 2009-2010 Stephen Colebourne
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

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
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
     * Map of registered currencies by text code.
     */
    private static final ConcurrentMap<String, CurrencyUnit> cCurrenciesByCode = new ConcurrentHashMap<String, CurrencyUnit>();
    /**
     * Map of registered currencies by numeric code.
     */
    private static final ConcurrentMap<Integer, CurrencyUnit> cCurrenciesByNumericCode = new ConcurrentHashMap<Integer, CurrencyUnit>();
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
     * The currency code, not null.
     */
    private final String iCode;
    /**
     * The numeric currency code.
     */
    private final short iNumericCode;
    /**
     * The number of decimal places.
     */
    private final short iDecimalPlaces;

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
    static synchronized CurrencyUnit registerCurrency(
            String currencyCode, int numericCurrencyCode, int decimalPlaces, List<String> countryCodes) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        if (numericCurrencyCode < -1 || numericCurrencyCode > 999) {
            throw new IllegalArgumentException("Invalid numeric code");
        }
        if (decimalPlaces < -1 || decimalPlaces > 3) {
            throw new IllegalArgumentException("Invalid number of decimal places");
        }
        MoneyUtils.checkNotNull(countryCodes, "Country codes must not be null");
        
        CurrencyUnit currency = new CurrencyUnit(currencyCode, (short) numericCurrencyCode, (short) decimalPlaces);
        if (cCurrenciesByCode.containsKey(currencyCode) || cCurrenciesByNumericCode.containsKey(numericCurrencyCode)) {
            throw new IllegalArgumentException("Currency already registered: " + currencyCode);
        }
        for (String countryCode : countryCodes) {
            if (cCurrenciesByCountry.containsKey(countryCode)) {
                throw new IllegalArgumentException("Currency already registered for country: " + countryCode);
            }
        }
        cCurrenciesByCode.putIfAbsent(currencyCode, currency);
        if (numericCurrencyCode >= 0) {
            cCurrenciesByNumericCode.putIfAbsent(numericCurrencyCode, currency);
        }
        for (String countryCode : countryCodes) {
            cCurrenciesByCountry.put(countryCode, currency);
        }
        return cCurrenciesByCode.get(currencyCode);
    }

    /**
     * Gets the list of all registered currencies.
     * <p>
     * This class only permits known currencies to be returned, thus this list is
     * the complete list of valid singleton currencies. The list may change after
     * application startup, however this isn't recommended.
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
     * Gets the {@code CurrencyUnit} instance matching the specified currency.
     *
     * @param currency  the currency, not null
     * @return the singleton instance, never null
     */
    public static CurrencyUnit of(Currency currency) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        return of(currency.getCurrencyCode());
    }

    /**
     * Gets the {@code CurrencyUnit} instance for the specified currency code.
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
     * Gets the {@code CurrencyUnit} instance for the specified ISO-4217
     * numeric currency code formatted as a string.
     * <p>
     * This method is lenient and does not require the string to be left padded with zeroes.
     *
     * @param currencyCode  the currency code, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit ofNumericCode(String numericCurrencyCode) {
        MoneyUtils.checkNotNull(numericCurrencyCode, "Currency code must not be null");
        switch (numericCurrencyCode.length()) {
            case 1:
                return ofNumericCode(numericCurrencyCode.charAt(0) - '0');
            case 2:
                return ofNumericCode((numericCurrencyCode.charAt(0) - '0') * 10 +
                                      numericCurrencyCode.charAt(1) - '0');
            case 3:
                return ofNumericCode((numericCurrencyCode.charAt(0) - '0') * 100 +
                                     (numericCurrencyCode.charAt(1) - '0') * 10 +
                                      numericCurrencyCode.charAt(2) - '0');
            default:
                throw new MoneyException("Unknown currency: " + numericCurrencyCode);
        }
    }

    /**
     * Gets the {@code CurrencyUnit} instance for the specified ISO-4217
     * numeric currency code.
     *
     * @param numericCurrencyCode  the numeric currency code, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit ofNumericCode(int numericCurrencyCode) {
        CurrencyUnit currency = cCurrenciesByNumericCode.get(numericCurrencyCode);
        if (currency == null) {
            throw new MoneyException("Unknown currency: " + numericCurrencyCode);
        }
        return currency;
    }

    /**
     * Gets the {@code CurrencyUnit} instance for the specified locale.
     * <p>
     * Only the country is used from the locale.
     *
     * @param locale  the locale, not null
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
     * Gets the {@code CurrencyUnit} instance for the specified country code.
     * <p>
     * Country codes should generally be in upper case.
     * This method is case sensitive.
     *
     * @param countryCode  the country code, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit ofCountry(String countryCode) {
        MoneyUtils.checkNotNull(countryCode, "Country code must not be null");
        CurrencyUnit currency = cCurrenciesByCountry.get(countryCode);
        if (currency == null) {
            throw new MoneyException("Unknown currency for country code: " + countryCode);
        }
        return currency;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the {@code CurrencyUnit} instance for the specified currency code.
     * <p>
     * This method exists to match the API of {@link Currency}.
     *
     * @param currencyCode  the currency code, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit getInstance(String currencyCode) {
        return CurrencyUnit.of(currencyCode);
    }

    /**
     * Gets the {@code CurrencyUnit} instance for the specified locale.
     * <p>
     * This method exists to match the API of {@link Currency}.
     *
     * @param locale  the locale, not null
     * @return the singleton instance, never null
     * @throws MoneyException if the currency is unknown
     */
    public static CurrencyUnit getInstance(Locale locale) {
        return CurrencyUnit.of(locale);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new currency instance.
     * 
     * @param code  the currency code, not null
     * @param numericCurrencyCode  the numeric currency code, -1 if none
     * @param decimalPlaces  the decimal places, not null
     */
    private CurrencyUnit(String code, short numericCurrencyCode, short decimalPlaces) {
        assert code != null : "Joda-Money bug: Currency code must not be null";
        iCode = code;
        iNumericCode = numericCurrencyCode;
        iDecimalPlaces = decimalPlaces;
    }

    /**
     * Resolves singletons, validating that the incoming currency has the same definition
     * as the local currency.
     * 
     * @return the singleton instance, never null
     */
    private Object readResolve() throws ObjectStreamException {
        CurrencyUnit singletonCurrency = CurrencyUnit.of(iCode);
        if (iNumericCode != singletonCurrency.iNumericCode) {
            throw new InvalidObjectException("Deserialization found a mismatch in the numeric code for currency " + iCode);
        }
        if (iDecimalPlaces != singletonCurrency.iDecimalPlaces) {
            throw new InvalidObjectException("Deserialization found a mismatch in the decimal places for currency " + iCode);
        }
        return singletonCurrency;
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
