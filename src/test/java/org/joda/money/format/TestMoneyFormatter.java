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
package org.joda.money.format;

import static org.testng.Assert.assertEquals;

import java.util.Locale;

import org.joda.money.Money;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
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

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = new Locale("fr", "FR", "TEST");
    private MoneyFormatter iTest;

    @BeforeMethod
    public void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
        iTest = new MoneyFormatterBuilder()
            .appendCurrencyCode()
            .appendLiteral(" hello")
            .toFormatter();
    }

    @AfterMethod
    public void afterMethod() {
        Locale.setDefault(cCachedLocale);
        iTest = null;
    }

    //-----------------------------------------------------------------------
    // getLocale() withLocale(Locale)
    //-----------------------------------------------------------------------
    public void test_getLocale() {
        assertEquals(iTest.getLocale(), TEST_GB_LOCALE);
    }

    public void test_withLocale() {
        MoneyFormatter test = iTest.withLocale(TEST_FR_LOCALE);
        assertEquals(iTest.getLocale(), TEST_GB_LOCALE);
        assertEquals(test.getLocale(), TEST_FR_LOCALE);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withLocale_nullLocale() {
        iTest.withLocale((Locale) null);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(iTest.toString(), "${code}' hello'");
    }

}
