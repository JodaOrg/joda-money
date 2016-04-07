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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.testng.annotations.Test;

/**
 * Test CurrencyUnit.
 */
@Test
public class TestCurrencyUnit {

    private static final Currency JDK_GBP = Currency.getInstance("GBP");

    //-----------------------------------------------------------------------
    // registeredCurrencies()
    //-----------------------------------------------------------------------
    public void test_registeredCurrencies() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("GBP")) {
                found = true;
                break;
            }
        }
        assertEquals(found, true);
    }

    public void test_registeredCurrencies_sorted() {
        List<CurrencyUnit> curList1 = CurrencyUnit.registeredCurrencies();
        List<CurrencyUnit> curList2 = CurrencyUnit.registeredCurrencies();
        Collections.sort(curList2);
        assertEquals(curList1, curList2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_registeredCurrency_nullCode() {
        CurrencyUnit.registerCurrency(null, 991, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidStringCode_empty() {
        CurrencyUnit.registerCurrency("", 991, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidStringCode_1letter() {
        CurrencyUnit.registerCurrency("A", 991, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidStringCode_2letters() {
        CurrencyUnit.registerCurrency("AB", 991, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidStringCode_4letters() {
        CurrencyUnit.registerCurrency("ABCD", 991, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidStringCode_lowerCase() {
        CurrencyUnit.registerCurrency("xxA", 991, 2, Arrays.asList("xx"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidStringCode_number() {
        CurrencyUnit.registerCurrency("123", 991, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidStringCode_dash() {
        CurrencyUnit.registerCurrency("A-", 991, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidNumericCode_small() {
        CurrencyUnit.registerCurrency("TST", -2, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidNumericCode_big() {
        CurrencyUnit.registerCurrency("TST", 1000, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidDP_small() {
        CurrencyUnit.registerCurrency("TST", 991, -2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_invalidDP_big() {
        CurrencyUnit.registerCurrency("TST", 991, 10, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_registeredCurrency_nullCountry() {
        CurrencyUnit.registerCurrency("TST", 991, 2, Arrays.asList((String) null));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_alreadyRegisteredCode() {
        CurrencyUnit.registerCurrency("GBP", 991, 2, Arrays.asList("GB"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_alreadyRegisteredNumericCode() {
        CurrencyUnit.registerCurrency("TST", 826, 2, Arrays.asList("TS"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registeredCurrency_alreadyRegisteredCountry() {
        CurrencyUnit.registerCurrency("GBX", 991, 2, Arrays.asList("GB"));
    }

//    public void test_registeredCurrencies_crossCheck() {
//        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
//        for (CurrencyUnit currencyUnit : curList) {
//            try {
//                Currency curr = Currency.getInstance(currencyUnit.getCode());
//                assertEquals(currencyUnit.getDefaultFractionDigits(), curr.getDefaultFractionDigits(), curr.getCurrencyCode());
//            } catch (IllegalArgumentException ex) {
//                System.out. println(currencyUnit);
//            }
//        }
//    }

//    {
//        System.out. println(curList1);
//        for (char a = 'A'; a <= 'Z'; a++) {
//            for (char b = 'A'; b <= 'Z'; b++) {
//                for (char c = 'A'; c <= 'Z'; c++) {
//                    String code = "" + a + b + c;
//                    try {
//                        Currency curr = Currency.getInstance(code);
//                        System.out. println(curr);
//                    } catch (Exception ex) {
//                        // continue
//                    }
//                }
//            }
//        }
//    }

    //-----------------------------------------------------------------------
    // constants
    //-----------------------------------------------------------------------
    public void test_constants() {
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
    @Test(expectedExceptions = AssertionError.class)
    public void test_constructor_nullCode() {
        new CurrencyUnit(null, (short) 1, (short) 2);
    }

    //-----------------------------------------------------------------------
    // of(Currency)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency() {
        CurrencyUnit test = CurrencyUnit.of(JDK_GBP);
        assertEquals(test.getCode(), "GBP");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_nullCurrency() {
        CurrencyUnit.of((Currency) null);
    }

    //-----------------------------------------------------------------------
    // of(String)
    //-----------------------------------------------------------------------
    public void test_factory_of_String() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.getCode(), "GBP");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_String_nullString() {
        CurrencyUnit.of((String) null);
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_of_String_unknownCurrency() {
        try {
            CurrencyUnit.of("ABC");
        } catch (IllegalCurrencyException ex) {
            assertEquals(ex.getMessage(), "Unknown currency 'ABC'");
            throw ex;
        }
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_of_String_empty() {
        CurrencyUnit.of("");
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_of_String_tooShort_unknown() {
        CurrencyUnit.of("AB");
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_of_String_tooLong_unknown() {
        CurrencyUnit.of("ABCD");
    }

    //-----------------------------------------------------------------------
    // ofNumericCode(String)
    //-----------------------------------------------------------------------
    public void test_factory_ofNumericCode_String() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("826");
        assertEquals(test.getCode(), "GBP");
    }

    public void test_factory_ofNumericCode_String_2char() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("051");
        assertEquals(test.getCode(), "AMD");
    }

    public void test_factory_ofNumericCode_String_2charNoPad() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("51");
        assertEquals(test.getCode(), "AMD");
    }

    public void test_factory_ofNumericCode_String_1char() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("008");
        assertEquals(test.getCode(), "ALL");
    }

    public void test_factory_ofNumericCode_String_1charNoPad() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode("8");
        assertEquals(test.getCode(), "ALL");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofNumericCode_String_nullString() {
        CurrencyUnit.ofNumericCode((String) null);
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_ofNumericCode_String_unknownCurrency() {
        try {
            CurrencyUnit.ofNumericCode("111");
        } catch (IllegalCurrencyException ex) {
            assertEquals(ex.getMessage(), "Unknown currency '111'");
            throw ex;
        }
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_ofNumericCode_String_negative() {
        CurrencyUnit.ofNumericCode("-1");
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_ofNumericCode_String_empty() {
        try {
            CurrencyUnit.ofNumericCode("");
        } catch (IllegalCurrencyException ex) {
            assertEquals(ex.getMessage(), "Unknown currency ''");
            throw ex;
        }
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_ofNumericCode_String_tooLong() {
        try {
            CurrencyUnit.ofNumericCode("1234");
        } catch (IllegalCurrencyException ex) {
            assertEquals(ex.getMessage(), "Unknown currency '1234'");
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // ofNumericCode(int)
    //-----------------------------------------------------------------------
    public void test_factory_ofNumericCode_int() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode(826);
        assertEquals(test.getCode(), "GBP");
    }

    public void test_factory_ofNumericCode_int_2char() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode(51);
        assertEquals(test.getCode(), "AMD");
    }

    public void test_factory_ofNumericCode_int_1char() {
        CurrencyUnit test = CurrencyUnit.ofNumericCode(8);
        assertEquals(test.getCode(), "ALL");
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_ofNumericCode_int_unknownCurrency() {
        try {
            CurrencyUnit.ofNumericCode(111);
        } catch (IllegalCurrencyException ex) {
            assertEquals(ex.getMessage(), "Unknown currency '111'");
            throw ex;
        }
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_ofNumericCode_int_negative() {
        try {
            CurrencyUnit.ofNumericCode(-1);
        } catch (IllegalCurrencyException ex) {
            assertEquals(ex.getMessage(), "Unknown currency '-1'");
            throw ex;
        }
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_ofNumericCode_int_tooLong() {
        try {
            CurrencyUnit.ofNumericCode(1234);
        } catch (IllegalCurrencyException ex) {
            assertEquals(ex.getMessage(), "Unknown currency '1234'");
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // of(Locale)
    //-----------------------------------------------------------------------
    public void test_factory_of_LocaleUK() {
        CurrencyUnit test = CurrencyUnit.of(Locale.UK);
        assertEquals(test.getCode(), "GBP");
    }

    public void test_factory_of_LocaleUS() {
        CurrencyUnit test = CurrencyUnit.of(Locale.US);
        assertEquals(test.getCode(), "USD");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Locale_nullLocale() {
        CurrencyUnit.of((Locale) null);
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_of_Locale_unknownCurrency() {
        try {
            CurrencyUnit.of(new Locale("en", "XY"));
        } catch (IllegalCurrencyException ex) {
            assertEquals(ex.getMessage(), "Unknown currency for locale 'en_XY'");
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // ofCountry(String)
    //-----------------------------------------------------------------------
    public void test_factory_ofCountry_String() {
        CurrencyUnit test = CurrencyUnit.ofCountry("GB");
        assertEquals(test.getCode(), "GBP");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofCountry_String_nullString() {
        CurrencyUnit.ofCountry((String) null);
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_ofCountry_String_unknownCurrency() {
        try {
            CurrencyUnit.ofCountry("gb");
        } catch (IllegalCurrencyException ex) {
            assertEquals(ex.getMessage(), "Unknown currency for country 'gb'");
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // getInstance(String)
    //-----------------------------------------------------------------------
    public void test_factory_getInstance_String() {
        CurrencyUnit test = CurrencyUnit.getInstance("GBP");
        assertEquals(test.getCode(), "GBP");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_getInstance_String_nullString() {
        CurrencyUnit.getInstance((String) null);
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_getInstance_String_unknownCurrency() {
        CurrencyUnit.getInstance("ABC");
    }

    //-----------------------------------------------------------------------
    // getInstance(Locale)
    //-----------------------------------------------------------------------
    public void test_factory_getInstance_Locale() {
        CurrencyUnit test = CurrencyUnit.getInstance(Locale.UK);
        assertEquals(test.getCode(), "GBP");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_getInstance_Locale_nullString() {
        CurrencyUnit.getInstance((Locale) null);
    }

    @Test(expectedExceptions = IllegalCurrencyException.class)
    public void test_factory_getInstance_Locale_unknownCurrency() {
        CurrencyUnit.getInstance(new Locale("en", "XY"));
    }

    //-----------------------------------------------------------------------
    // Serialisation
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        CurrencyUnit cu = CurrencyUnit.of("GBP");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(cu);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        CurrencyUnit input = (CurrencyUnit) ois.readObject();
        assertEquals(input, cu);
    }

    @Test(expectedExceptions = InvalidObjectException.class)
    public void test_serialization_invalidNumericCode() throws Exception {
        CurrencyUnit cu = new CurrencyUnit("GBP", (short) 234, (short) 2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(cu);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        try {
            ois.readObject();
        } catch (InvalidObjectException ex) {
            // expected
            assertTrue(ex.getMessage().contains("numeric code"));
            assertTrue(ex.getMessage().contains("currency GBP"));
            throw ex;
        }
    }

    @Test(expectedExceptions = InvalidObjectException.class)
    public void test_serialization_invalidDecimalPlaces() throws Exception {
        CurrencyUnit cu = new CurrencyUnit("GBP", (short) 826, (short) 1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(cu);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        try {
            ois.readObject();
        } catch (InvalidObjectException ex) {
            // expected
            assertTrue(ex.getMessage().contains("decimal places"));
            assertTrue(ex.getMessage().contains("currency GBP"));
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // getCurrencyCode()
    //-----------------------------------------------------------------------
    public void test_getCurrencyCode_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.getCode(), "GBP");
        assertEquals(test.getCurrencyCode(), "GBP");
    }

    //-----------------------------------------------------------------------
    // getNumeric3Code()
    //-----------------------------------------------------------------------
    public void test_getNumeric3Code_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.getNumeric3Code(), "826");
    }

    public void test_getNumeric3Code_ALL() {
        CurrencyUnit test = CurrencyUnit.of("ALL");
        assertEquals(test.getNumeric3Code(), "008");
    }

    public void test_getNumeric3Code_AMD() {
        CurrencyUnit test = CurrencyUnit.of("AMD");
        assertEquals(test.getNumeric3Code(), "051");
    }

    public void test_getNumeric3Code_XFU() {
        CurrencyUnit test = CurrencyUnit.of("XFU");
        assertEquals(test.getNumeric3Code(), "");
    }

    //-----------------------------------------------------------------------
    // getNumericCode()
    //-----------------------------------------------------------------------
    public void test_getNumericCode_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.getNumericCode(), 826);
    }

    //-----------------------------------------------------------------------
    // getCurrencyCodes()
    //-----------------------------------------------------------------------
    public void test_getCurrencyCodes_GBP() {
        Set<String> test = CurrencyUnit.of("GBP").getCountryCodes();
        assertEquals(test.contains("GB"), true);
        assertEquals(test.contains("IM"), true);
        assertEquals(test.contains("JE"), true);
        assertEquals(test.contains("GG"), true);
        assertEquals(test.contains("GS"), true);
    }

    //-----------------------------------------------------------------------
    // getDecimalPlaces()
    //-----------------------------------------------------------------------
    public void test_getDecimalPlaces_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.getDecimalPlaces(), 2);
    }

    public void test_getDecimalPlaces_JPY() {
        CurrencyUnit test = CurrencyUnit.of("JPY");
        assertEquals(test.getDecimalPlaces(), 0);
    }

    public void test_getDecimalPlaces_XXX() {
        CurrencyUnit test = CurrencyUnit.of("XXX");
        assertEquals(test.getDecimalPlaces(), 0);
    }

    //-----------------------------------------------------------------------
    // isPseudoCurrency()
    //-----------------------------------------------------------------------
    public void test_isPseudoCurrency_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.isPseudoCurrency(), false);
    }

    public void test_isPseudoCurrency_JPY() {
        CurrencyUnit test = CurrencyUnit.of("JPY");
        assertEquals(test.isPseudoCurrency(), false);
    }

    public void test_isPseudoCurrency_XXX() {
        CurrencyUnit test = CurrencyUnit.of("XXX");
        assertEquals(test.isPseudoCurrency(), true);
    }

    //-----------------------------------------------------------------------
    // getDefaultFractionDigits()
    //-----------------------------------------------------------------------
    public void test_getDefaultFractionDigits_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.getDefaultFractionDigits(), 2);
    }

    public void test_getDefaultFractionDigits_JPY() {
        CurrencyUnit test = CurrencyUnit.of("JPY");
        assertEquals(test.getDefaultFractionDigits(), 0);
    }

    public void test_getDefaultFractionDigits_XXX() {
        CurrencyUnit test = CurrencyUnit.of("XXX");
        assertEquals(test.getDefaultFractionDigits(), -1);
    }

    //-----------------------------------------------------------------------
    // getSymbol()
    //-----------------------------------------------------------------------
    public void test_getSymbol_GBP() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("GBP");
            assertEquals(test.getSymbol(), "\u00A3");
        } finally {
            Locale.setDefault(loc);
        }
    }

    public void test_getSymbol_JPY() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("JPY");
            assertEquals(test.getSymbol(), "JPY");
        } finally {
            Locale.setDefault(loc);
        }
    }

    public void test_getSymbol_TMT() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("TMT");
            assertEquals(test.getSymbol(), "TMT");
        } finally {
            Locale.setDefault(loc);
        }
    }

    public void test_getSymbol_XXX() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("XXX");
            assertEquals(test.getSymbol(), "XXX");
        } finally {
            Locale.setDefault(loc);
        }
    }

    //-----------------------------------------------------------------------
    // getSymbol()
    //-----------------------------------------------------------------------
    public void test_getSymbol_Locale_GBP_UK() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("GBP");
            assertEquals(test.getSymbol(Locale.UK), "\u00A3");
        } finally {
            Locale.setDefault(loc);
        }
    }

    public void test_getSymbol_Locale_GBP_France() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("GBP");
            assertEquals(test.getSymbol(Locale.FRANCE), "GBP");
        } finally {
            Locale.setDefault(loc);
        }
    }

    public void test_getSymbol_Locale_USD_UK() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("USD");
            assertEquals(test.getSymbol(Locale.US), "$");
        } finally {
            Locale.setDefault(loc);
        }
    }

    public void test_getSymbol_Locale_USD_France() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("USD");
            assertEquals(test.getSymbol(Locale.FRANCE), "USD");
        } finally {
            Locale.setDefault(loc);
        }
    }

    public void test_getSymbol_Locale_JPY_Japan() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("JPY");
            assertEquals(test.getSymbol(Locale.JAPAN), "\uFFE5");
        } finally {
            Locale.setDefault(loc);
        }
    }

    public void test_getSymbol_TMT_UK() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("TMT");
            assertEquals(test.getSymbol(Locale.UK), "TMT");
        } finally {
            Locale.setDefault(loc);
        }
    }

    public void test_getSymbol_Locale_XXX() {
        Locale loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            CurrencyUnit test = CurrencyUnit.of("XXX");
            assertEquals(test.getSymbol(Locale.FRANCE), "XXX");
        } finally {
            Locale.setDefault(loc);
        }
    }

    //-----------------------------------------------------------------------
    // toCurrency()
    //-----------------------------------------------------------------------
    public void test_toCurrency() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.toCurrency(), JDK_GBP);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        CurrencyUnit a = CurrencyUnit.of("EUR");
        CurrencyUnit b = CurrencyUnit.of("GBP");
        CurrencyUnit c = CurrencyUnit.of("JPY");
        assertEquals(a.compareTo(a), 0);
        assertEquals(b.compareTo(b), 0);
        assertEquals(c.compareTo(c), 0);
        
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        
        assertTrue(a.compareTo(c) < 0);
        assertTrue(c.compareTo(a) > 0);
        
        assertTrue(b.compareTo(c) < 0);
        assertTrue(c.compareTo(b) > 0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compareTo_null() {
        CurrencyUnit.of("EUR").compareTo(null);
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    public void test_equals_hashCode() {
        CurrencyUnit a = CurrencyUnit.of("GBP");
        CurrencyUnit b = CurrencyUnit.of("GBP");
        CurrencyUnit c = CurrencyUnit.of("EUR");
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
        assertEquals(c.equals(c), true);
        
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        
        assertEquals(a.equals(c), false);
        assertEquals(b.equals(c), false);
    }

    public void test_equals_false() {
        CurrencyUnit a = CurrencyUnit.of("GBP");
        assertEquals(a.equals(null), false);
        assertEquals(a.equals("String"), false);
        assertEquals(a.equals(new Object()), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.toString(), "GBP");
    }

}
