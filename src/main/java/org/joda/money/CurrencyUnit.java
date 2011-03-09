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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * A unit of currency.
 * <p>
 * This class represents a unit of currency such as the British Pound, Euro
 * or US Dollar.
 * <p>
 * The set of loaded currencies is provided by an instance of {@link CurrencyUnitDataProvider}.
 * The provider used is determined by the system property {@code org.joda.money.CurrencyUnitDataProvider}
 * which should be the fully qualified class name of the provider. The default provider loads the first
 * resource named {@code /org/joda/money/MoneyData.csv} on the classpath.
 * <p>
 * This class is immutable and thread-safe.
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
        // load one data provider by system property
        try {
            try {
                String clsName = System.getProperty(
                        "org.joda.money.CurrencyUnitDataProvider", "org.joda.money.DefaultCurrencyUnitDataProvider");
                Class<? extends CurrencyUnitDataProvider> cls =
                        CurrencyUnit.class.getClassLoader().loadClass(clsName).asSubclass(CurrencyUnitDataProvider.class);
                cls.newInstance().registerCurrencies();
            } catch (SecurityException ex) {
                new DefaultCurrencyUnitDataProvider().registerCurrencies();
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex.toString(), ex);
        }
    }

    // a selection of commonly traded, stable currencies
    /**
     * The currency 'USD' - United States Dollar.
     */
    public static final CurrencyUnit USD = of("USD");
    /**
     * The currency 'EUR' - Euro.
     */
    public static final CurrencyUnit EUR = of("EUR");
    /**
     * The currency 'JPY' - Japanese Yen.
     */
    public static final CurrencyUnit JPY = of("JPY");
    /**
     * The currency 'GBP' - British pound.
     */
    public static final CurrencyUnit GBP = of("GBP");
    /**
     * The currency 'EUR' - Swiss Franc.
     */
    public static final CurrencyUnit CHF = of("CHF");
    /**
     * The currency 'AUD' - Australian Dollar.
     */
    public static final CurrencyUnit AUD = of("AUD");
    /**
     * The currency 'CAD' - Canadian Dollar.
     */
    public static final CurrencyUnit CAD = of("CAD");

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
     * Obtains an instance of {@code CurrencyUnit} matching the specified JDK currency.
     * <p>
     * This converts the JDK currency instance to a currency unit using the code.
     *
     * @param currency  the currency, not null
     * @return the singleton instance, never null
     */
    public static CurrencyUnit of(Currency currency) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        return of(currency.getCurrencyCode());
    }

    /**
     * Obtains an instance of {@code CurrencyUnit} for the specified ISO-4217 three letter currency code.
     * <p>
     * A currency is uniquely identified by ISO-4217 three letter code.
     *
     * @param currencyCode  the currency code, not null
     * @return the singleton instance, never null
     * @throws IllegalCurrencyException if the currency is unknown
     */
    @FromString
    public static CurrencyUnit of(String currencyCode) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        CurrencyUnit currency = cCurrenciesByCode.get(currencyCode);
        if (currency == null) {
            throw new IllegalCurrencyException("Unknown currency '" + currencyCode + '\'');
        }
        return currency;
    }

    /**
     * Obtains an instance of {@code CurrencyUnit} for the specified ISO-4217 numeric currency code.
     * <p>
     * The numeric code is an alternative to the three letter code.
     * This method is lenient and does not require the string to be left padded with zeroes.
     *
     * @param numericCurrencyCode  the currency code, not null
     * @return the singleton instance, never null
     * @throws IllegalCurrencyException if the currency is unknown
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
                throw new IllegalCurrencyException("Unknown currency '" + numericCurrencyCode + '\'');
        }
    }

    /**
     * Obtains an instance of {@code CurrencyUnit} for the specified ISO-4217 numeric currency code.
     * <p>
     * The numeric code is an alternative to the three letter code.
     *
     * @param numericCurrencyCode  the numeric currency code, not null
     * @return the singleton instance, never null
     * @throws IllegalCurrencyException if the currency is unknown
     */
    public static CurrencyUnit ofNumericCode(int numericCurrencyCode) {
        CurrencyUnit currency = cCurrenciesByNumericCode.get(numericCurrencyCode);
        if (currency == null) {
            throw new IllegalCurrencyException("Unknown currency '" + numericCurrencyCode + '\'');
        }
        return currency;
    }

    /**
     * Obtains an instance of {@code CurrencyUnit} for the specified locale.
     * <p>
     * Only the country is used from the locale.
     *
     * @param locale  the locale, not null
     * @return the singleton instance, never null
     * @throws IllegalCurrencyException if the currency is unknown
     */
    public static CurrencyUnit of(Locale locale) {
        MoneyUtils.checkNotNull(locale, "Locale must not be null");
        CurrencyUnit currency = cCurrenciesByCountry.get(locale.getCountry());
        if (currency == null) {
            throw new IllegalCurrencyException("Unknown currency for locale '" + locale + '\'');
        }
        return currency;
    }

    /**
     * Obtains an instance of {@code CurrencyUnit} for the specified country code.
     * <p>
     * Country codes should generally be in upper case.
     * This method is case sensitive.
     *
     * @param countryCode  the country code, not null
     * @return the singleton instance, never null
     * @throws IllegalCurrencyException if the currency is unknown
     */
    public static CurrencyUnit ofCountry(String countryCode) {
        MoneyUtils.checkNotNull(countryCode, "Country code must not be null");
        CurrencyUnit currency = cCurrenciesByCountry.get(countryCode);
        if (currency == null) {
            throw new IllegalCurrencyException("Unknown currency for country '" + countryCode + '\'');
        }
        return currency;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code CurrencyUnit} for the specified currency code.
     * <p>
     * This method exists to match the API of {@link Currency}.
     *
     * @param currencyCode  the currency code, not null
     * @return the singleton instance, never null
     * @throws IllegalCurrencyException if the currency is unknown
     */
    public static CurrencyUnit getInstance(String currencyCode) {
        return CurrencyUnit.of(currencyCode);
    }

    /**
     * Obtains an instance of {@code CurrencyUnit} for the specified locale.
     * <p>
     * This method exists to match the API of {@link Currency}.
     *
     * @param locale  the locale, not null
     * @return the singleton instance, never null
     * @throws IllegalCurrencyException if the currency is unknown
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
    CurrencyUnit(String code, short numericCurrencyCode, short decimalPlaces) {
        assert code != null : "Joda-Money bug: Currency code must not be null";
        iCode = code;
        iNumericCode = numericCurrencyCode;
        iDecimalPlaces = decimalPlaces;
    }

    /**
     * Uses a serialization delegate.
     * 
     * @return the replacing object, never null
     */
    private Object writeReplace() {
        return new Ser(Ser.CURRENCY_UNIT, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ISO-4217 three letter currency code.
     * <p>
     * Each currency is uniquely identified by a three-letter code.
     * 
     * @return the three letter ISO-4217 currency code, never null
     */
    public String getCode() {
        return iCode;
    }

    /**
     * Gets the ISO-4217 numeric currency code.
     * <p>
     * The numeric code is an alternative to the standard three letter code.
     * 
     * @return the numeric currency code
     */
    public int getNumericCode() {
        return iNumericCode;
    }

    /**
     * Gets the ISO-4217 numeric currency code as a three digit string.
     * <p>
     * This formats the numeric code as a three digit string prefixed by zeroes if necessary.
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
     * Gets the number of decimal places typically used by this currency.
     * <p>
     * Different currencies have different numbers of decimal places by default.
     * For example, 'GBP' has 2 decimal places, but 'JPY' has zero.
     * Pseudo-currencies will return zero.
     * <p>
     * See also {@link #getDefaultFractionDigits()}.
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

    //-----------------------------------------------------------------------
    /**
     * Gets the ISO-4217 three-letter currency code.
     * <p>
     * This method matches the API of {@link Currency}.
     * 
     * @return the currency code, never null
     */
    public String getCurrencyCode() {
        return iCode;
    }

    /**
     * Gets the number of fractional digits typically used by this currency.
     * <p>
     * Different currencies have different numbers of fractional digits by default.
     * For example, 'GBP' has 2 fractional digits, but 'JPY' has zero.
     * Pseudo-currencies are indicated by -1.
     * <p>
     * This method matches the API of {@link Currency}.
     * The alternative method {@link #getDecimalPlaces()} may be more useful.
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
     * <p>
     * This method matches the API of {@link Currency}.
     * 
     * @return the JDK currency instance, never null
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
     * <p>
     * This method matches the API of {@link Currency}.
     * 
     * @param locale  the locale to get the symbol for, not null
     * @return the JDK currency instance, never null
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
     * <p>
     * This attempts to convert a {@code CurrencyUnit} to a JDK {@code Currency}.
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

    /**
     * Checks if this currency equals another currency.
     * <p>
     * The comparison checks the 3 letter currency code.
     * 
     * @param obj  the other currency, null returns false
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CurrencyUnit) {
            return iCode.equals(((CurrencyUnit) obj).iCode);
        }
        return false;
    }

    /**
     * Returns a suitable hash code for the currency.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return iCode.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the currency code as a string.
     * 
     * @return the currency code, never null
     */
    @Override
    @ToString
    public String toString() {
        return iCode;
    }

}
