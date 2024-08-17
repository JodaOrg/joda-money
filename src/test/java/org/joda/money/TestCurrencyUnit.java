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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Test CurrencyUnit.
 */
class TestCurrencyUnit {

    private static final Currency JDK_GBP = Currency.getInstance("GBP");

    //-----------------------------------------------------------------------
    // registeredCurrencies()
    //-----------------------------------------------------------------------
    @Test
    void test_registeredCurrencies() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("GBP")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void test_registeredCurrencies_sorted() {
        List<CurrencyUnit> curList1 = CurrencyUnit.registeredCurrencies();
        List<CurrencyUnit> curList2 = CurrencyUnit.registeredCurrencies();
        Collections.sort(curList2);
        assertEquals(curList2, curList1);
        Collections.shuffle(curList2);
        Collections.sort(curList2);
        assertEquals(curList2, curList1);
    }

    @Test
    void test_registeredCurrency_nullCode() {
        assertThrows(NullPointerException.class, () -> {
            CurrencyUnit.registerCurrency(null, 991, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidStringCode_empty() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("", 991, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidStringCode_1letter() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("A", 991, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidStringCode_2letters() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("AB", 991, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidStringCode_4letters() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("ABCD", 991, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidStringCode_lowerCase() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("xxA", 991, 2, Arrays.asList("xx"));
        });
    }

    @Test
    void test_registeredCurrency_invalidStringCode_number() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("123", 991, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidStringCode_dash() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("A-", 991, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidNumericCode_small() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("TST", -2, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidNumericCode_big() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("TST", 1000, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidDP_small() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("TST", 991, -2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_invalidDP_big() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("TST", 991, 31, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_validDP_big() {
        CurrencyUnit.registerCurrency("XLG", -1, 30, new ArrayList<String>());

        CurrencyUnit currency = CurrencyUnit.of("XLG");
        assertEquals(30, currency.getDecimalPlaces());
    }

    @Test
    void test_registeredCurrency_nullCountry() {
        assertThrows(NullPointerException.class, () -> {
            CurrencyUnit.registerCurrency("TST", 991, 2, Arrays.asList((String) null));
        });
    }

    @Test
    void test_registeredCurrency_alreadyRegisteredCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("GBP", 991, 2, Arrays.asList("GB"));
        });
    }

    @Test
    void test_registeredCurrency_alreadyRegisteredNumericCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("TST", 826, 2, Arrays.asList("TS"));
        });
    }

    @Test
    void test_registeredCurrency_alreadyRegisteredCountry() {
        assertThrows(IllegalArgumentException.class, () -> {
            CurrencyUnit.registerCurrency("GBX", 991, 2, Arrays.asList("GB"));
        });
    }

    @Test
    void test_registeredCurrencies_crossCheck() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        for (CurrencyUnit currencyUnit : curList) {
            try {
                Currency curr = Currency.getInstance(currencyUnit.getCode());
                int dp = curr.getDefaultFractionDigits() < 0 ? 0 : curr.getDefaultFractionDigits();
                assertEquals(dp, currencyUnit.getDecimalPlaces(), curr.getCurrencyCode());
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    //-----------------------------------------------------------------------
    // registeredCountries()
    //-----------------------------------------------------------------------
    @Test
    void test_registeredCountries() {
        List<String> countryList = CurrencyUnit.registeredCountries();
        assertTrue(countryList.contains("GB"));
        assertTrue(countryList.contains("EU"));
        assertTrue(countryList.contains("US"));
    }

    @Test
    void test_registeredCountries_sorted() {
        List<String> curList1 = CurrencyUnit.registeredCountries();
        List<String> curList2 = CurrencyUnit.registeredCountries();
        Collections.sort(curList2);
        assertEquals(curList2, curList1);
        Collections.shuffle(curList2);
        Collections.sort(curList2);
        assertEquals(curList2, curList1);
    }

    //-----------------------------------------------------------------------
    // constants
    //-----------------------------------------------------------------------
    @Test
    void test_constants() {
        assertEquals(CurrencyUnit.USD, CurrencyUnit.of("USD"));
        assertEquals(CurrencyUnit.EUR, CurrencyUnit.of("EUR"));
        assertEquals(CurrencyUnit.JPY, CurrencyUnit.of("JPY"));
        assertEquals(CurrencyUnit.GBP, CurrencyUnit.of("GBP"));
        assertEquals(CurrencyUnit.CHF, CurrencyUnit.of("CHF"));
        assertEquals(CurrencyUnit.AUD, CurrencyUnit.of("AUD"));
        assertEquals(CurrencyUnit.CAD, CurrencyUnit.of("CAD"));
    }

    //-----------------------------------------------------------------------
    // constructor assert
    //-----------------------------------------------------------------------
    @Test
    void test_constructor_nullCode() {
        assertThrows(AssertionError.class, () -> {
            new CurrencyUnit(null, (short) 1, (short) 2);
        });
    }

    //-----------------------------------------------------------------------
    // of(Currency)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency() {
        CurrencyUnit test = CurrencyUnit.of(JDK_GBP);
        assertEquals("GBP", test.getCode());
    }

    @Test
    void test_factory_of_Currency_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            CurrencyUnit.of((Currency) null);
        });
    }

    //-----------------------------------------------------------------------
    // of(String)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_String() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals("GBP", test.getCode());
    }

    @Test
    void test_factory_of_String_nullString() {
        assertThrows(NullPointerException.class, () -> {
            CurrencyUnit.of((String) null);
        });
    }

    @Test
    void test_factory_of_String_unknownCurrency() {
        assertThrows(IllegalCurrencyException.class, () -> {
            try {
                CurrencyUnit.of("ABC");
            } catch (IllegalCurrencyException ex) {
                assertEquals("Unknown currency 'ABC'", ex.getMessage());
                throw ex;
            }
        });
    }

    @Test
    void test_factory_of_String_empty() {
        assertThrows(IllegalCurrencyException.class, () -> {
            CurrencyUnit.of("");
        });
    }

    @Test
    void test_factory_of_String_tooShort_unknown() {
        assertThrows(IllegalCurrencyException.class, () -> {
            CurrencyUnit.of("AB");
        });
    }

    @Test
    void test_factory_of_String_tooLong_unknown() {
        assertThrows(IllegalCurrencyException.class, () -> {
            CurrencyUnit.of("ABCD");
        });
    }

    //-----------------------------------------------------------------------
    // ofNumericCode(String)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofNumericCode_String() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("826");
        assertEquals("GBP", test.getCode());
    }

    @Test
    void test_factory_ofNumericCode_String_2char() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("051");
        assertEquals("AMD", test.getCode());
    }

    @Test
    void test_factory_ofNumericCode_String_2charNoPad() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("51");
        assertEquals("AMD", test.getCode());
    }

    @Test
    void test_factory_ofNumericCode_String_1char() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("008");
        assertEquals("ALL", test.getCode());
    }

    @Test
    void test_factory_ofNumericCode_String_1charNoPad() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("8");
        assertEquals("ALL", test.getCode());
    }

    @Test
    void test_factory_ofNumericCode_String_nullString() {
        assertThrows(NullPointerException.class, () -> {
            CurrencyUnit.ofNumericCode((String) null);
        });
    }

    @Test
    void test_factory_ofNumericCode_String_unknownCurrency() {
        assertThrows(IllegalCurrencyException.class, () -> {
            try {
                CurrencyUnit.ofNumericCode("111");
            } catch (IllegalCurrencyException ex) {
                assertEquals("Unknown currency '111'", ex.getMessage());
                throw ex;
            }
        });
    }

    @Test
    void test_factory_ofNumericCode_String_negative() {
        assertThrows(IllegalCurrencyException.class, () -> {
            CurrencyUnit.ofNumericCode("-1");
        });
    }

    @Test
    void test_factory_ofNumericCode_String_empty() {
        assertThrows(IllegalCurrencyException.class, () -> {
            try {
                CurrencyUnit.ofNumericCode("");
            } catch (IllegalCurrencyException ex) {
                assertEquals("Unknown currency ''", ex.getMessage());
                throw ex;
            }
        });
    }

    @Test
    void test_factory_ofNumericCode_String_tooLong() {
        assertThrows(IllegalCurrencyException.class, () -> {
            try {
                CurrencyUnit.ofNumericCode("1234");
            } catch (IllegalCurrencyException ex) {
                assertEquals("Unknown currency '1234'", ex.getMessage());
                throw ex;
            }
        });
    }

    //-----------------------------------------------------------------------
    // ofNumericCode(int)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofNumericCode_int() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode(826);
        assertEquals("GBP", test.getCode());
    }

    @Test
    void test_factory_ofNumericCode_int_2char() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode(51);
        assertEquals("AMD", test.getCode());
    }

    @Test
    void test_factory_ofNumericCode_int_1char() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode(8);
        assertEquals("ALL", test.getCode());
    }

    @Test
    void test_factory_ofNumericCode_int_unknownCurrency() {
        assertThrows(IllegalCurrencyException.class, () -> {
            try {
                CurrencyUnit.ofNumericCode(111);
            } catch (IllegalCurrencyException ex) {
                assertEquals("Unknown currency '111'", ex.getMessage());
                throw ex;
            }
        });
    }

    @Test
    void test_factory_ofNumericCode_int_negative() {
        assertThrows(IllegalCurrencyException.class, () -> {
            try {
                CurrencyUnit.ofNumericCode(-1);
            } catch (IllegalCurrencyException ex) {
                assertEquals("Unknown currency '-1'", ex.getMessage());
                throw ex;
            }
        });
    }

    @Test
    void test_factory_ofNumericCode_int_tooLong() {
        assertThrows(IllegalCurrencyException.class, () -> {
            try {
                CurrencyUnit.ofNumericCode(1234);
            } catch (IllegalCurrencyException ex) {
                assertEquals("Unknown currency '1234'", ex.getMessage());
                throw ex;
            }
        });
    }

    //-----------------------------------------------------------------------
    // of(Locale)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_LocaleUK() {
        CurrencyUnit test = CurrencyUnit.of(Locale.UK);
        assertEquals("GBP", test.getCode());
    }

    @Test
    void test_factory_of_LocaleUS() {
        CurrencyUnit test = CurrencyUnit.of(Locale.US);
        assertEquals("USD", test.getCode());
    }

    @Test
    void test_factory_of_Locale_nullLocale() {
        assertThrows(NullPointerException.class, () -> {
            CurrencyUnit.of((Locale) null);
        });
    }

    @Test
    void test_factory_of_Locale_unknownCurrency() {
        assertThrows(IllegalCurrencyException.class, () -> {
            try {
                CurrencyUnit.of(new Locale("en", "XY"));
            } catch (IllegalCurrencyException ex) {
                assertEquals("No currency found for locale 'en_XY'", ex.getMessage());
                throw ex;
            }
        });
    }

    //-----------------------------------------------------------------------
    // ofCountry(String)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofCountry_String() {
        CurrencyUnit test = CurrencyUnit.ofCountry("GB");
        assertEquals("GBP", test.getCode());
    }

    @Test
    void test_factory_ofCountry_String_nullString() {
        assertThrows(NullPointerException.class, () -> {
            CurrencyUnit.ofCountry((String) null);
        });
    }

    @Test
    void test_factory_ofCountry_String_unknownCurrency() {
        assertThrows(IllegalCurrencyException.class, () -> {
            try {
                CurrencyUnit.ofCountry("gb");
            } catch (IllegalCurrencyException ex) {
                assertEquals("No currency found for country 'gb'", ex.getMessage());
                throw ex;
            }
        });
    }

    //-----------------------------------------------------------------------
    // Serialisation
    //-----------------------------------------------------------------------
    @Test
    void test_serialization() throws Exception {
        CurrencyUnit cu = CurrencyUnit.of("GBP");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(cu);
            oos.close();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            CurrencyUnit input = (CurrencyUnit) ois.readObject();
            assertEquals(cu, input);
        }
    }

    @Test
    void test_serialization_invalidNumericCode() throws IOException {
        CurrencyUnit cu = new CurrencyUnit("GBP", (short) 234, (short) 2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(cu);
            oos.close();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            assertThrows(InvalidObjectException.class, () -> {
                try {
                    ois.readObject();
                } catch (InvalidObjectException ex) {
                    // expected
                    assertTrue(ex.getMessage().contains("numeric code"));
                    assertTrue(ex.getMessage().contains("currency GBP"));
                    throw ex;
                }
            });
        }
    }

    @Test
    void test_serialization_invalidDecimalPlaces() throws IOException {
        CurrencyUnit cu = new CurrencyUnit("GBP", (short) 826, (short) 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(cu);
            oos.close();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            assertThrows(InvalidObjectException.class, () -> {
                try {
                    ois.readObject();
                } catch (InvalidObjectException ex) {
                    // expected
                    assertTrue(ex.getMessage().contains("decimal places"));
                    assertTrue(ex.getMessage().contains("currency GBP"));
                    throw ex;
                }
            });
        }
    }

    //-----------------------------------------------------------------------
    // getNumeric3Code()
    //-----------------------------------------------------------------------
    @Test
    void test_getNumeric3Code_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals("826", test.getNumeric3Code());
    }

    @Test
    void test_getNumeric3Code_ALL() {
        CurrencyUnit test = CurrencyUnit.of("ALL");
        assertEquals("008", test.getNumeric3Code());
    }

    @Test
    void test_getNumeric3Code_AMD() {
        CurrencyUnit test = CurrencyUnit.of("AMD");
        assertEquals("051", test.getNumeric3Code());
    }

    @Test
    void test_getNumeric3Code_XFU() {
        CurrencyUnit test = CurrencyUnit.of("XFU");
        assertEquals("", test.getNumeric3Code());
    }

    //-----------------------------------------------------------------------
    // getNumericCode()
    //-----------------------------------------------------------------------
    @Test
    void test_getNumericCode_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(826, test.getNumericCode());
    }

    //-----------------------------------------------------------------------
    // getCurrencyCodes()
    //-----------------------------------------------------------------------
    @Test
    void test_getCurrencyCodes_GBP() {
        Set<String> test = CurrencyUnit.of("GBP").getCountryCodes();
        assertTrue(test.contains("GB"));
        assertTrue(test.contains("IM"));
        assertTrue(test.contains("JE"));
        assertTrue(test.contains("GG"));
    }

    //-----------------------------------------------------------------------
    // getDecimalPlaces()
    //-----------------------------------------------------------------------
    @Test
    void test_getDecimalPlaces_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(2, test.getDecimalPlaces());
    }

    @Test
    void test_getDecimalPlaces_JPY() {
        CurrencyUnit test = CurrencyUnit.of("JPY");
        assertEquals(0, test.getDecimalPlaces());
    }

    @Test
    void test_getDecimalPlaces_XXX() {
        CurrencyUnit test = CurrencyUnit.of("XXX");
        assertEquals(0, test.getDecimalPlaces());
    }

    //-----------------------------------------------------------------------
    // isPseudoCurrency()
    //-----------------------------------------------------------------------
    @Test
    void test_isPseudoCurrency_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertFalse(test.isPseudoCurrency());
    }

    @Test
    void test_isPseudoCurrency_JPY() {
        CurrencyUnit test = CurrencyUnit.of("JPY");
        assertFalse(test.isPseudoCurrency());
    }

    @Test
    void test_isPseudoCurrency_XXX() {
        CurrencyUnit test = CurrencyUnit.of("XXX");
        assertTrue(test.isPseudoCurrency());
    }

    //-----------------------------------------------------------------------
    // getSymbol()
    //-----------------------------------------------------------------------
    @Test
    void test_getSymbol_GBP() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("GBP");
            assertEquals("\u00A3", test.getSymbol());
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_JPY() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("JPY");
            assertTrue(test.getSymbol().contains("JP"));
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_TMT() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("TMT");
            assertEquals("TMT", test.getSymbol());
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_XXX() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("XXX");
            assertEquals("XXX", test.getSymbol());
        } finally {
            Locale.setDefault(loc);
        }
    }

    //-----------------------------------------------------------------------
    // getSymbol()
    //-----------------------------------------------------------------------
    @Test
    void test_getSymbol_Locale_GBP_UK() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("GBP");
            assertEquals("\u00A3", test.getSymbol(Locale.UK));
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_GBP_France() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("GBP");
            assertTrue(test.getSymbol(Locale.FRANCE).contains("GB"));
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_USD_UK() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("USD");
            assertEquals("$", test.getSymbol(Locale.US));
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_USD_France() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("USD");
            assertTrue(test.getSymbol(Locale.FRANCE).contains("US"));
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_JPY_Japan() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("JPY");
            assertEquals("\uFFE5", test.getSymbol(Locale.JAPAN));
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_TMT_UK() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("TMT");
            assertEquals("TMT", test.getSymbol(Locale.UK));
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_XXX() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("XXX");
            assertEquals("XXX", test.getSymbol(Locale.FRANCE));
        } finally {
            Locale.setDefault(loc);
        }
    }

    //-----------------------------------------------------------------------
    // toCurrency()
    //-----------------------------------------------------------------------
    @Test
    void test_toCurrency() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(JDK_GBP, test.toCurrency());
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    void test_compareTo() {
        CurrencyUnit a = CurrencyUnit.of("EUR");
        CurrencyUnit b = CurrencyUnit.of("GBP");
        CurrencyUnit c = CurrencyUnit.of("JPY");
        assertEquals(0, a.compareTo(a));
        assertEquals(0, b.compareTo(b));
        assertEquals(0, c.compareTo(c));

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);

        assertTrue(a.compareTo(c) < 0);
        assertTrue(c.compareTo(a) > 0);

        assertTrue(b.compareTo(c) < 0);
        assertTrue(c.compareTo(b) > 0);
    }

    @Test
    void test_compareTo_null() {
        assertThrows(NullPointerException.class, () -> {
            CurrencyUnit.of("EUR").compareTo(null);
        });
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    @Test
    void test_equals_hashCode() {
        CurrencyUnit a = CurrencyUnit.of("GBP");
        CurrencyUnit b = CurrencyUnit.of("GBP");
        CurrencyUnit c = CurrencyUnit.of("EUR");
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));
        assertTrue(c.equals(c));

        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertTrue(a.hashCode() == b.hashCode());

        assertFalse(a.equals(c));
        assertFalse(b.equals(c));
    }

    @Test
    void test_equals_false() {
        CurrencyUnit a = CurrencyUnit.of("GBP");
        assertFalse(a.equals(null));
        Object obj = "String";  // avoid warning in Eclipse
        assertFalse(a.equals(obj));
        assertFalse(a.equals(new Object()));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    void test_toString() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals("GBP", test.toString());
    }

}
