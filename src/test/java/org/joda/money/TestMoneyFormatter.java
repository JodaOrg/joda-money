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

import java.util.Locale;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Test MoneyFormatter.
 */
@Test
public class TestMoneyFormatter {

//    private static final CurrencyUnit GBP = CurrencyUnit.of("GBP");
//    private static final CurrencyUnit EUR = CurrencyUnit.of("EUR");
//    private static final CurrencyUnit USD = CurrencyUnit.of("USD");
//    private static final CurrencyUnit JPY = CurrencyUnit.of("JPY");
//    private static final BigDecimal BIGDEC_2_34 = new BigDecimal("2.34");
//    private static final BigDecimal BIGDEC_M5_78 = new BigDecimal("-5.78");
//
//    private static final Money GBP_0_00 = Money.parse("GBP 0.00");
//    private static final Money GBP_1_23 = Money.parse("GBP 1.23");
//    private static final Money GBP_2_33 = Money.parse("GBP 2.33");
    private static final Money GBP_2_34 = Money.parse("GBP 2.34");
//    private static final Money GBP_2_35 = Money.parse("GBP 2.35");
//    private static final Money GBP_2_36 = Money.parse("GBP 2.36");
//    private static final Money GBP_5_78 = Money.parse("GBP 5.78");
//    private static final Money GBP_M1_23 = Money.parse("GBP -1.23");
//    private static final Money GBP_M5_78 = Money.parse("GBP -5.78");
//    private static final Money GBP_INT_MAX_PLUS1 = Money.ofMinor("GBP", ((long) Integer.MAX_VALUE) + 1);
//    private static final Money GBP_INT_MIN_MINUS1 = Money.ofMinor("GBP", ((long) Integer.MIN_VALUE) - 1);
//    private static final Money GBP_INT_MAX_MAJOR_PLUS1 = Money.ofMinor("GBP", (((long) Integer.MAX_VALUE) + 1) * 100);
//    private static final Money GBP_INT_MIN_MAJOR_MINUS1 = Money.ofMinor("GBP", (((long) Integer.MIN_VALUE) - 1) * 100);
//    private static final Money JPY_423 = Money.parse("JPY 423");
//    private static final Money USD_1_23 = Money.parse("USD 1.23");
//    private static final Money USD_2_34 = Money.parse("USD 2.34");
//    private static final Money USD_2_35 = Money.parse("USD 2.35");

    /** The cached locale. */
    private static final Locale cCachedLocale = Locale.getDefault();

    @BeforeTest
    public void beforeTest() {
        Locale.setDefault(Locale.UK);
    }

    @AfterTest
    public void afterTest() {
        Locale.setDefault(cCachedLocale);
    }

    //-----------------------------------------------------------------------
    // of(Locale)
    //-----------------------------------------------------------------------
    public void test_factory_of_Locale() {
        MoneyFormatter test = MoneyFormatter.of(Locale.UK);
        assertEquals(test.getLocale(), Locale.UK);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Locale_nullLocale() {
        MoneyFormatter.of((Locale) null);
    }

    //-----------------------------------------------------------------------
    // print(Money)
    //-----------------------------------------------------------------------
    public void test_print_Money() {
        MoneyFormatter test = MoneyFormatter.of(Locale.UK);
        assertEquals(test.print(GBP_2_34), "\u00A32.34");
    }

    public void test_print_Money_grouping() {
        MoneyFormatter test = MoneyFormatter.of(Locale.UK);
        assertEquals(test.print(Money.parse("GBP 123456.78")), "\u00A3123,456.78");
    }

    public void test_print_Money_groupingOff() {
        MoneyFormatter test = MoneyFormatter.of(Locale.UK);
        test = test.withGrouping(false);
        assertEquals(test.print(Money.parse("GBP 123456.78")), "\u00A3123456.78");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_print_Money_nullMoney() {
        MoneyFormatter test = MoneyFormatter.of(Locale.UK);
        test.print((Money) null);
    }

//    //-----------------------------------------------------------------------
//    // equals() hashCode()
//    //-----------------------------------------------------------------------
//    public void test_equals_hashCode_positive() {
//        Money a = GBP_2_34;
//        Money b = GBP_2_34;
//        Money c = GBP_2_35;
//        assertEquals(a.equals(a), true);
//        assertEquals(b.equals(b), true);
//        assertEquals(c.equals(c), true);
//        
//        assertEquals(a.equals(b), true);
//        assertEquals(b.equals(a), true);
//        assertEquals(a.hashCode() == b.hashCode(), true);
//        
//        assertEquals(a.equals(c), false);
//        assertEquals(b.equals(c), false);
//    }
//
//    public void test_equals_false() {
//        Money a = GBP_2_34;
//        assertEquals(a.equals(null), false);
//        assertEquals(a.equals("String"), false);
//        assertEquals(a.equals(new Object()), false);
//    }
//
//    //-----------------------------------------------------------------------
//    // toString()
//    //-----------------------------------------------------------------------
//    public void test_toString_positive() {
//        Money test = Money.of(GBP, BIGDEC_2_34);
//        assertEquals(test.toString(), "GBP 2.34");
//    }
//
//    public void test_toString_negative() {
//        Money test = Money.of(EUR, BIGDEC_M5_78);
//        assertEquals(test.toString(), "EUR -5.78");
//    }

}
