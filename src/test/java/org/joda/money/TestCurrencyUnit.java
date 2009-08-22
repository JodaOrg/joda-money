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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

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
            if (currencyUnit.getCurrencyCode().equals("GBP")) {
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
    // of(Currency)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency() {
        CurrencyUnit test = CurrencyUnit.of(JDK_GBP);
        assertEquals(test.getCurrencyCode(), "GBP");
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
        assertEquals(test.getCurrencyCode(), "GBP");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_String_nullString() {
        CurrencyUnit.of((String) null);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_of_String_unknownCurrency() {
        CurrencyUnit.of("ABC");
    }

    //-----------------------------------------------------------------------
    // getInstance(String)
    //-----------------------------------------------------------------------
    public void test_factory_getInstance_String() {
        CurrencyUnit test = CurrencyUnit.getInstance("GBP");
        assertEquals(test.getCurrencyCode(), "GBP");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_getInstance_String_nullString() {
        CurrencyUnit.getInstance((String) null);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_getInstance_String_unknownCurrency() {
        CurrencyUnit.getInstance("ABC");
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

    //-----------------------------------------------------------------------
    // getCurrencyCode()
    //-----------------------------------------------------------------------
    public void test_getCurrencyCode_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.getCurrencyCode(), "GBP");
    }

    //-----------------------------------------------------------------------
    // getNumericCode()
    //-----------------------------------------------------------------------
    public void test_getNumericCode_GBP() {
        CurrencyUnit test = CurrencyUnit.of("GBP");
        assertEquals(test.getNumericCode(), 826);
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
