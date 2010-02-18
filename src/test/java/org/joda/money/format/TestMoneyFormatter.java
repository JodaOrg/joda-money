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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

import org.joda.money.MoneyProvider;
import org.joda.money.StandardMoney;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test MoneyFormatter.
 */
@Test
public class TestMoneyFormatter {

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = new Locale("fr", "FR", "TEST");
    private static final StandardMoney MONEY_GBP_12_34 = StandardMoney.parse("GBP 12.34");
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
    // serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        MoneyFormatter a = iTest;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(a);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        MoneyFormatter input = (MoneyFormatter) ois.readObject();
        StandardMoney value = MONEY_GBP_12_34;
        assertEquals(input.print(value), a.print(value));
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
    // print(MoneyProvider)
    //-----------------------------------------------------------------------
    public void test_print_MoneyProvider() {
        assertEquals(iTest.print(MONEY_GBP_12_34), "GBP hello");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_print_MoneyProvider_nullMoneyProvider() {
        iTest.print((MoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // print(Appendable,MoneyProvider)
    //-----------------------------------------------------------------------
    public void test_print_AppendableMoneyProvider() {
        StringBuilder buf = new StringBuilder();
        iTest.print(buf, MONEY_GBP_12_34);
        assertEquals(buf.toString(), "GBP hello");
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_print_AppendableMoneyProvider_IOException() {
        Appendable appendable = new Appendable() {
            public Appendable append(CharSequence csq, int start, int end) throws IOException {
                throw new IOException();
            }
            public Appendable append(char c) throws IOException {
                throw new IOException();
            }
            public Appendable append(CharSequence csq) throws IOException {
                throw new IOException();
            }
        };
        iTest.print(appendable, MONEY_GBP_12_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_print_AppendableMoneyProvider_nullAppendable() {
        iTest.print((Appendable) null, MONEY_GBP_12_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_print_AppendableMoneyProvider_nullMoneyProvider() {
        iTest.print(new StringBuilder(), (MoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(iTest.toString(), "${code}' hello'");
    }

}
