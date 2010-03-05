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
package org.joda.money.format;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.BigMoneyProvider;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test MoneyFormatter.
 */
@Test
public class TestMoneyFormatter {

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = new Locale("fr", "FR", "TEST");
    private static final Money MONEY_GBP_12_34 = Money.parse("GBP 12.34");
    private MoneyFormatter iPrintTest;
    private MoneyFormatter iCannotPrint;
    private MoneyFormatter iParseTest;
    private MoneyFormatter iCannotParse;

    @BeforeMethod
    public void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
        iPrintTest = new MoneyFormatterBuilder()
            .appendCurrencyCode()
            .appendLiteral(" hello")
            .toFormatter();
        iCannotPrint = new MoneyFormatterBuilder()
            .append(null, new MoneyParser() {
                public void parse(MoneyParseContext context) {
                }
            })
            .toFormatter();
        iParseTest = new MoneyFormatterBuilder()
            .appendAmount()
            .appendLiteral(" ")
            .appendCurrencyCode()
            .toFormatter();
        iCannotParse = new MoneyFormatterBuilder()
            .append(new MoneyPrinter() {
                public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                }
            }, null)
            .toFormatter();
    }

    @AfterMethod
    public void afterMethod() {
        Locale.setDefault(cCachedLocale);
        iPrintTest = null;
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        MoneyFormatter a = iPrintTest;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(a);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        MoneyFormatter input = (MoneyFormatter) ois.readObject();
        Money value = MONEY_GBP_12_34;
        assertEquals(input.print(value), a.print(value));
    }

    //-----------------------------------------------------------------------
    // getLocale() withLocale(Locale)
    //-----------------------------------------------------------------------
    public void test_getLocale() {
        assertEquals(iPrintTest.getLocale(), TEST_GB_LOCALE);
    }

    public void test_withLocale() {
        MoneyFormatter test = iPrintTest.withLocale(TEST_FR_LOCALE);
        assertEquals(iPrintTest.getLocale(), TEST_GB_LOCALE);
        assertEquals(test.getLocale(), TEST_FR_LOCALE);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withLocale_nullLocale() {
        iPrintTest.withLocale((Locale) null);
    }

    //-----------------------------------------------------------------------
    // print(BigMoneyProvider)
    //-----------------------------------------------------------------------
    public void test_print_BigMoneyProvider() {
        assertEquals(iPrintTest.print(MONEY_GBP_12_34), "GBP hello");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void test_print_BigMoneyProvider_cannotPrint() {
        iCannotPrint.print(MONEY_GBP_12_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_print_BigMoneyProvider_nullBigMoneyProvider() {
        iPrintTest.print((BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // print(Appendable,BigMoneyProvider)
    //-----------------------------------------------------------------------
    public void test_print_AppendableBigMoneyProvider() {
        StringBuilder buf = new StringBuilder();
        iPrintTest.print(buf, MONEY_GBP_12_34);
        assertEquals(buf.toString(), "GBP hello");
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_print_AppendableBigMoneyProvider_IOException() {
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
        try {
            iPrintTest.print(appendable, MONEY_GBP_12_34);
        } catch (MoneyFormatException ex) {
            assertEquals(ex.getCause().getClass(), IOException.class);
            throw ex;
        }
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void test_print_AppendableBigMoneyProvider_cannotPrint() {
        iCannotPrint.print(new StringBuilder(), MONEY_GBP_12_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_print_AppendableBigMoneyProvider_nullAppendable() {
        iPrintTest.print((Appendable) null, MONEY_GBP_12_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_print_AppendableBigMoneyProvider_nullBigMoneyProvider() {
        iPrintTest.print(new StringBuilder(), (BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // printIO(Appendable,BigMoneyProvider)
    //-----------------------------------------------------------------------
    public void test_printIO_AppendableBigMoneyProvider() throws IOException {
        StringBuilder buf = new StringBuilder();
        iPrintTest.printIO(buf, MONEY_GBP_12_34);
        assertEquals(buf.toString(), "GBP hello");
    }

    @Test(expectedExceptions = IOException.class)
    public void test_printIO_AppendableBigMoneyProvider_IOException() throws IOException {
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
        iPrintTest.printIO(appendable, MONEY_GBP_12_34);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void test_printIO_AppendableBigMoneyProvider_cannotPrint() throws IOException {
        iCannotPrint.printIO(new StringBuilder(), MONEY_GBP_12_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_printIO_AppendableBigMoneyProvider_nullAppendable() throws IOException {
        iPrintTest.printIO((Appendable) null, MONEY_GBP_12_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_printIO_AppendableBigMoneyProvider_nullBigMoneyProvider() throws IOException {
        iPrintTest.printIO(new StringBuilder(), (BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // parseBigMoney(CharSequence)
    //-----------------------------------------------------------------------
    public void test_parseBigMoney_CharSequence() {
        CharSequence input = new StringBuilder("12.34 GBP");
        BigMoney test = iParseTest.parseBigMoney(input);
        assertEquals(test, MONEY_GBP_12_34.toBigMoney());
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_parseBigMoney_CharSequence_invalidCurrency() {
        iParseTest.parseBigMoney("12.34 GBX");
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_parseBigMoney_CharSequence_notFullyParsed() {
        iParseTest.parseBigMoney("12.34 GBP X");
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_parseBigMoney_CharSequence_incomplete() {
        iCannotPrint.parseBigMoney("12.34 GBP");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void test_parseBigMoney_CharSequence_cannotParse() {
        iCannotParse.parseBigMoney(new StringBuilder());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parseBigMoney_CharSequence_nullCharSequence() {
        iParseTest.parseBigMoney((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    // parseMoney(CharSequence)
    //-----------------------------------------------------------------------
    public void test_parseMoney_CharSequence() {
        CharSequence input = new StringBuilder("12.34 GBP");
        Money test = iParseTest.parseMoney(input);
        assertEquals(test, MONEY_GBP_12_34);
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_parseMoney_CharSequence_invalidCurrency() {
        iParseTest.parseMoney("12.34 GBX");
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_parseMoney_CharSequence_notFullyParsed() {
        iParseTest.parseMoney("12.34 GBP X");
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_parseMoney_CharSequence_incomplete() {
        iCannotPrint.parseMoney("12.34 GBP");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void test_parseMoney_CharSequence_cannotParse() {
        iCannotParse.parseMoney(new StringBuilder());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parseMoney_CharSequence_nullCharSequence() {
        iParseTest.parseMoney((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,int)
    //-----------------------------------------------------------------------
    @DataProvider(name = "parse")
    Object[][] data_parse() {
        return new Object[][] {
            new Object[] {"12.34 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 9, -1, false, true, true},
            new Object[] {"1,2.34 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 10, -1, false, true, true},
            new Object[] {"12,.34 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 10, -1, false, true, true},
            new Object[] {"12.,34 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 10, -1, false, true, true},
            new Object[] {"12.3,4 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 10, -1, false, true, true},
            new Object[] {".12 GBP", BigDecimal.valueOf(12, 2), MONEY_GBP_12_34.getCurrencyUnit(), 7, -1, false, true, true},
            new Object[] {"12. GBP", BigDecimal.valueOf(12), MONEY_GBP_12_34.getCurrencyUnit(), 7, -1, false, true, true},
            new Object[] {"12,34 GBP", BigDecimal.valueOf(1234), MONEY_GBP_12_34.getCurrencyUnit(), 9, -1, false, true, true},
            
            new Object[] {",12.34 GBP", null, null, 0, 0, true, false, false},
            new Object[] {"12..34 GBP", BigDecimal.valueOf(12), null, 3, 3, true, false, false},
            new Object[] {"12,,34 GBP", BigDecimal.valueOf(12), null, 2, 2, true, false, false},
            new Object[] {"12.34 GBX", MONEY_GBP_12_34.getAmount(), null, 6, 6, true, false, false},
            new Object[] {"12.34 GBPX", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 9, -1, false, false, true},
        };
    }

    @Test(dataProvider = "parse")
    public void test_parse_CharSequenceInt(String str, BigDecimal amount, CurrencyUnit currency,
                    int index, int errorIndex, boolean error, boolean fullyParsed, boolean complete) {
        CharSequence input = new StringBuilder(str);
        MoneyParseContext test = iParseTest.parse(input, 0);
        assertEquals(test.getAmount(), amount);
        assertEquals(test.getCurrency(), currency);
        assertEquals(test.getIndex(), index);
        assertEquals(test.getErrorIndex(), errorIndex);
        assertEquals(test.getText().toString(), str);
        assertEquals(test.getTextLength(), str.length());
        assertEquals(test.isError(), error);
        assertEquals(test.isFullyParsed(), fullyParsed);
        assertEquals(test.isComplete(), complete);
    }

    public void test_parse_CharSequenceInt_incomplete() {
        // this parser does nothing
        MoneyParseContext test = iCannotPrint.parse("12.34 GBP", 0);
        assertEquals(test.getAmount(), null);
        assertEquals(test.getCurrency(), null);
        assertEquals(test.getIndex(), 0);
        assertEquals(test.getErrorIndex(), -1);
        assertEquals(test.getText().toString(), "12.34 GBP");
        assertEquals(test.getTextLength(), 9);
        assertEquals(test.isError(), false);
        assertEquals(test.isFullyParsed(), false);
        assertEquals(test.isComplete(), false);
    }

    public void test_parse_CharSequenceInt_continueAfterDoubleDecimal() {
        MoneyFormatter f = new MoneyFormatterBuilder()
            .appendAmount().appendLiteral(".").appendCurrencyCode().toFormatter();
        MoneyParseContext test = f.parse("12..GBP", 0);
        assertEquals(test.getAmount(), BigDecimal.valueOf(12));
        assertEquals(test.getCurrency(), CurrencyUnit.of("GBP"));
        assertEquals(test.getIndex(), 7);
        assertEquals(test.getErrorIndex(), -1);
        assertEquals(test.getText().toString(), "12..GBP");
        assertEquals(test.getTextLength(), 7);
        assertEquals(test.isError(), false);
        assertEquals(test.isFullyParsed(), true);
        assertEquals(test.isComplete(), true);
    }

    public void test_parse_CharSequenceInt_continueAfterSingleComma() {
        MoneyFormatter f = new MoneyFormatterBuilder()
            .appendAmount().appendLiteral(",").appendCurrencyCode().toFormatter();
        MoneyParseContext test = f.parse("12,GBP", 0);
        assertEquals(test.getAmount(), BigDecimal.valueOf(12));
        assertEquals(test.getCurrency(), CurrencyUnit.of("GBP"));
        assertEquals(test.getIndex(), 6);
        assertEquals(test.getErrorIndex(), -1);
        assertEquals(test.getText().toString(), "12,GBP");
        assertEquals(test.getTextLength(), 6);
        assertEquals(test.isError(), false);
        assertEquals(test.isFullyParsed(), true);
        assertEquals(test.isComplete(), true);
    }

    public void test_parse_CharSequenceInt_continueAfterDoubleComma() {
        MoneyFormatter f = new MoneyFormatterBuilder()
            .appendAmount().appendLiteral(",,").appendCurrencyCode().toFormatter();
        MoneyParseContext test = f.parse("12,,GBP", 0);
        assertEquals(test.getAmount(), BigDecimal.valueOf(12));
        assertEquals(test.getCurrency(), CurrencyUnit.of("GBP"));
        assertEquals(test.getIndex(), 7);
        assertEquals(test.getErrorIndex(), -1);
        assertEquals(test.getText().toString(), "12,,GBP");
        assertEquals(test.getTextLength(), 7);
        assertEquals(test.isError(), false);
        assertEquals(test.isFullyParsed(), true);
        assertEquals(test.isComplete(), true);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void test_parse_CharSequenceInt_cannotParse() {
        iCannotParse.parse(new StringBuilder(), 0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_CharSequenceInt_nullCharSequence() {
        iParseTest.parse((CharSequence) null, 0);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(iPrintTest.toString(), "${code}' hello'");
    }

}
