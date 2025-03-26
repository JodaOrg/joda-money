package org.joda.money;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Pattern;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

/**
 * A unit of currency.
 */
public final class CurrencyUnit implements Comparable<CurrencyUnit>, Serializable {

    private static final long serialVersionUID = 327835287287L;

    private static final Pattern CODE = Pattern.compile("[A-Z][A-Z][A-Z]");

    private static final ConcurrentMap<String, CurrencyUnit> currenciesByCode = new ConcurrentSkipListMap<>();
    private static final ConcurrentMap<Integer, CurrencyUnit> currenciesByNumericCode = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, CurrencyUnit> currenciesByCountry = new ConcurrentSkipListMap<>();

    // Magic number extracted
    private static final int ISO_CURRENCY_CODE_LENGTH = 3;

    static {
        try {
            try {
                var clsName = System.getProperty(
                        "org.joda.money.CurrencyUnitDataProvider",
                        "org.joda.money.DefaultCurrencyUnitDataProvider");
                Class<? extends CurrencyUnitDataProvider> cls =
                        CurrencyUnit.class.getClassLoader().loadClass(clsName).asSubclass(CurrencyUnitDataProvider.class);
                cls.getDeclaredConstructor().newInstance().registerCurrencies();
            } catch (SecurityException ex) {
                new DefaultCurrencyUnitDataProvider().registerCurrencies();
            }
        } catch (RuntimeException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(ex.toString(), ex);
        }
    }

    // Kept public to prevent breaking existing test cases
    public static final CurrencyUnit USD = of("USD");
    public static final CurrencyUnit EUR = of("EUR");
    public static final CurrencyUnit JPY = of("JPY");
    public static final CurrencyUnit GBP = of("GBP");
    public static final CurrencyUnit CHF = of("CHF");
    public static final CurrencyUnit AUD = of("AUD");
    public static final CurrencyUnit CAD = of("CAD");

    private final String code;
    private final short numericCode;
    private final short decimalPlaces;

    public static synchronized CurrencyUnit registerCurrency(
            String currencyCode,
            int numericCurrencyCode,
            int decimalPlaces,
            List<String> countryCodes) {
        return registerCurrency(currencyCode, numericCurrencyCode, decimalPlaces, countryCodes, false);
    }

    public static synchronized CurrencyUnit registerCurrency(
            String currencyCode,
            int numericCurrencyCode,
            int decimalPlaces,
            List<String> countryCodes,
            boolean force) {

        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        if (currencyCode.length() != ISO_CURRENCY_CODE_LENGTH) {
            throw new IllegalArgumentException("Invalid string code, must be length " + ISO_CURRENCY_CODE_LENGTH);
        }
        if (!CODE.matcher(currencyCode).matches()) {
            throw new IllegalArgumentException("Invalid string code, must be ASCII upper-case letters");
        }
        if (numericCurrencyCode < -1 || numericCurrencyCode > 999) {
            throw new IllegalArgumentException("Invalid numeric code");
        }
        if (decimalPlaces < -1 || decimalPlaces > 30) {
            throw new IllegalArgumentException("Invalid number of decimal places");
        }
        MoneyUtils.checkNotNull(countryCodes, "Country codes must not be null");

        var currency = new CurrencyUnit(currencyCode, (short) numericCurrencyCode, (short) decimalPlaces);
        if (force) {
            currenciesByCode.remove(currencyCode);
            currenciesByNumericCode.remove(numericCurrencyCode);
            for (String countryCode : countryCodes) {
                currenciesByCountry.remove(countryCode);
            }
        } else {
            if (currenciesByCode.containsKey(currencyCode) || currenciesByNumericCode.containsKey(numericCurrencyCode)) {
                throw new IllegalArgumentException("Currency already registered: " + currencyCode);
            }
            for (String countryCode : countryCodes) {
                if (currenciesByCountry.containsKey(countryCode)) {
                    throw new IllegalArgumentException("Currency already registered for country: " + countryCode);
                }
            }
        }
        currenciesByCode.putIfAbsent(currencyCode, currency);
        if (numericCurrencyCode >= 0) {
            currenciesByNumericCode.putIfAbsent(numericCurrencyCode, currency);
        }
        for (String countryCode : countryCodes) {
            registerCountry(countryCode, currency);
        }
        return currenciesByCode.get(currencyCode);
    }

    public static synchronized CurrencyUnit registerCurrency(
            String currencyCode,
            int numericCurrencyCode,
            int decimalPlaces,
            boolean force) {
        List<String> countryCodes = Collections.emptyList();
        return registerCurrency(currencyCode, numericCurrencyCode, decimalPlaces, countryCodes, force);
    }

    public static synchronized void registerCountry(String countryCode, CurrencyUnit currency) {
        currenciesByCountry.put(countryCode, currency);
    }

    public static List<CurrencyUnit> registeredCurrencies() {
        return new ArrayList<>(currenciesByCode.values());
    }

    public static List<String> registeredCountries() {
        return new ArrayList<>(currenciesByCountry.keySet());
    }

    public static CurrencyUnit of(Currency currency) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        return of(currency.getCurrencyCode());
    }

    @FromString
    public static CurrencyUnit of(String currencyCode) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        var currency = currenciesByCode.get(currencyCode);
        if (currency == null) {
            throw new IllegalCurrencyException("Unknown currency '" + currencyCode + '\'' );
        }
        return currency;
    }

    public static CurrencyUnit ofNumericCode(String numericCurrencyCode) {
        MoneyUtils.checkNotNull(numericCurrencyCode, "Currency code must not be null");
        return switch (numericCurrencyCode.length()) {
            case 1 -> ofNumericCode(numericCurrencyCode.charAt(0) - '0');
            case 2 -> ofNumericCode((numericCurrencyCode.charAt(0) - '0') * 10 + numericCurrencyCode.charAt(1) - '0');
            case 3 -> ofNumericCode((numericCurrencyCode.charAt(0) - '0') * 100 +
                    (numericCurrencyCode.charAt(1) - '0') * 10 +
                    numericCurrencyCode.charAt(2) - '0');
            default -> throw new IllegalCurrencyException("Unknown currency '" + numericCurrencyCode + '\'' );
        };
    }

    public static CurrencyUnit ofNumericCode(int numericCurrencyCode) {
        var currency = currenciesByNumericCode.get(numericCurrencyCode);
        if (currency == null) {
            throw new IllegalCurrencyException("Unknown currency '" + numericCurrencyCode + '\'' );
        }
        return currency;
    }

    public static CurrencyUnit of(Locale locale) {
        MoneyUtils.checkNotNull(locale, "Locale must not be null");
        var currency = currenciesByCountry.get(locale.getCountry());
        if (currency == null) {
            throw new IllegalCurrencyException("No currency found for locale '" + locale + '\'' );
        }
        return currency;
    }

    public static CurrencyUnit ofCountry(String countryCode) {
        MoneyUtils.checkNotNull(countryCode, "Country code must not be null");
        var currency = currenciesByCountry.get(countryCode);
        if (currency == null) {
            throw new IllegalCurrencyException("No currency found for country '" + countryCode + '\'' );
        }
        return currency;
    }

    CurrencyUnit(String code, short numericCode, short decimalPlaces) {
        assert code != null : "Joda-Money bug: Currency code must not be null";
        this.code = code;
        this.numericCode = numericCode;
        this.decimalPlaces = decimalPlaces;
    }

    private void readObject(ObjectInputStream ois) throws InvalidObjectException {
        throw new InvalidObjectException("Serialization delegate required");
    }

    private Object writeReplace() {
        return new Ser(Ser.CURRENCY_UNIT, this);
    }

    public String getCode() {
        return code;
    }

    public int getNumericCode() {
        return numericCode;
    }

    public String getNumeric3Code() {
        if (numericCode < 0) {
            return "";
        }
        var str = Integer.toString(numericCode);
        if (str.length() == 1) {
            return "00" + str;
        }
        if (str.length() == 2) {
            return "0" + str;
        }
        return str;
    }

    public Set<String> getCountryCodes() {
        Set<String> countryCodes = new HashSet<>();
        for (Entry<String, CurrencyUnit> entry : currenciesByCountry.entrySet()) {
            if (this.equals(entry.getValue())) {
                countryCodes.add(entry.getKey());
            }
        }
        return countryCodes;
    }

    public int getDecimalPlaces() {
        return decimalPlaces < 0 ? 0 : decimalPlaces;
    }

    public boolean isPseudoCurrency() {
        return decimalPlaces < 0;
    }

    public String getSymbol() {
        if ("XXX".equals(code)) {
            return code;
        }
        try {
            return Currency.getInstance(code).getSymbol();
        } catch (IllegalArgumentException ex) {
            return code;
        }
    }

    public String getSymbol(Locale locale) {
        MoneyUtils.checkNotNull(locale, "Locale must not be null");
        if ("XXX".equals(code)) {
            return code;
        }
        try {
            return Currency.getInstance(code).getSymbol(locale);
        } catch (IllegalArgumentException ex) {
            return code;
        }
    }

    public Currency toCurrency() {
        return Currency.getInstance(code);
    }

    @Override
    public int compareTo(CurrencyUnit other) {
        return code.compareTo(other.code);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CurrencyUnit) {
            return code.equals(((CurrencyUnit) obj).code);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    @ToString
    public String toString() {
        return code;
    }
}
