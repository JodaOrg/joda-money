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
package org.joda.money.format;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.BigMoneyProvider;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Test MoneyFormatter.
 */
@RunWith(DataProviderRunner.class)
public class TestMoneyFormatter {

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = new Locale("fr", "FR", "TEST");
    private static final Money MONEY_GBP_12_34 = Money.parse("GBP 12.34");
    private MoneyFormatter iPrintTest;
    private MoneyFormatter iCannotPrint;
    private MoneyFormatter iParseTest;
    private MoneyFormatter iCannotParse;

    @Before
    public void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
        iPrintTest = new MoneyFormatterBuilder()
            .appendCurrencyCode()
            .appendLiteral(" hello")
            .toFormatter();
        iCannotPrint = new MoneyFormatterBuilder()
            .append(null, new MoneyParser() {
                @Override
                public void parse(MoneyParseContext context) {
                }
            })
            .toFormatter();
        iParseTest = new MoneyFormatterBuilder()
            .appendAmountLocalized()
            .appendLiteral(" ")
            .appendCurrencyCode()
            .toFormatter();
        iCannotParse = new MoneyFormatterBuilder()
            .append(new MoneyPrinter() {
                @Override
                public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                }
            }, null)
            .toFormatter();
    }

    @After
    public void afterMethod() {
        Locale.setDefault(cCachedLocale);
        iPrintTest = null;
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test
    public void test_serialization() throws Exception {
        MoneyFormatter a = iPrintTest;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(a);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        MoneyFormatter input = (MoneyFormatter) ois.readObject();
        Money value = MONEY_GBP_12_34;
        assertEquals(a.print(value), input.print(value));
    }

    //-----------------------------------------------------------------------
    // getLocale() withLocale(Locale)
    //-----------------------------------------------------------------------
    @Test
    public void test_getLocale() {
        assertEquals(TEST_GB_LOCALE, iPrintTest.getLocale());
    }

    @Test
    public void test_withLocale() {
        MoneyFormatter test = iPrintTest.withLocale(TEST_FR_LOCALE);
        assertEquals(TEST_GB_LOCALE, iPrintTest.getLocale());
        assertEquals(TEST_FR_LOCALE, test.getLocale());
    }

    @Test(expected = NullPointerException.class)
    public void test_withLocale_nullLocale() {
        iPrintTest.withLocale((Locale) null);
    }

    //-----------------------------------------------------------------------
    // print(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    public void test_print_BigMoneyProvider() {
        assertEquals("GBP hello", iPrintTest.print(MONEY_GBP_12_34));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_print_BigMoneyProvider_cannotPrint() {
        iCannotPrint.print(MONEY_GBP_12_34);
    }

    @Test(expected = NullPointerException.class)
    public void test_print_BigMoneyProvider_nullBigMoneyProvider() {
        iPrintTest.print((BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // print(Appendable,BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    public void test_print_AppendableBigMoneyProvider() {
        StringBuilder buf = new StringBuilder();
        iPrintTest.print(buf, MONEY_GBP_12_34);
        assertEquals("GBP hello", buf.toString());
    }

    @Test(expected = MoneyFormatException.class)
    public void test_print_AppendableBigMoneyProvider_IOException() {
        Appendable appendable = new IOAppendable();
        try {
            iPrintTest.print(appendable, MONEY_GBP_12_34);
        } catch (MoneyFormatException ex) {
            assertEquals(IOException.class, ex.getCause().getClass());
            throw ex;
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_print_AppendableBigMoneyProvider_cannotPrint() {
        iCannotPrint.print(new StringBuilder(), MONEY_GBP_12_34);
    }

    @Test(expected = NullPointerException.class)
    public void test_print_AppendableBigMoneyProvider_nullAppendable() {
        iPrintTest.print((Appendable) null, MONEY_GBP_12_34);
    }

    @Test(expected = NullPointerException.class)
    public void test_print_AppendableBigMoneyProvider_nullBigMoneyProvider() {
        iPrintTest.print(new StringBuilder(), (BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // printIO(Appendable,BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    public void test_printIO_AppendableBigMoneyProvider() throws IOException {
        StringBuilder buf = new StringBuilder();
        iPrintTest.printIO(buf, MONEY_GBP_12_34);
        assertEquals("GBP hello", buf.toString());
    }

    @Test(expected = IOException.class)
    public void test_printIO_AppendableBigMoneyProvider_IOException() throws IOException {
        Appendable appendable = new IOAppendable();
        iPrintTest.printIO(appendable, MONEY_GBP_12_34);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_printIO_AppendableBigMoneyProvider_cannotPrint() throws IOException {
        iCannotPrint.printIO(new StringBuilder(), MONEY_GBP_12_34);
    }

    @Test(expected = NullPointerException.class)
    public void test_printIO_AppendableBigMoneyProvider_nullAppendable() throws IOException {
        iPrintTest.printIO((Appendable) null, MONEY_GBP_12_34);
    }

    @Test(expected = NullPointerException.class)
    public void test_printIO_AppendableBigMoneyProvider_nullBigMoneyProvider() throws IOException {
        iPrintTest.printIO(new StringBuilder(), (BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // parseBigMoney(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    public void test_parseBigMoney_CharSequence() {
        CharSequence input = new StringBuilder("12.34 GBP");
        BigMoney test = iParseTest.parseBigMoney(input);
        assertEquals(MONEY_GBP_12_34.toBigMoney(), test);
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseBigMoney_CharSequence_invalidCurrency() {
        iParseTest.parseBigMoney("12.34 GBX");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseBigMoney_CharSequence_notFullyParsed() {
        iParseTest.parseBigMoney("12.34 GBP X");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseBigMoney_CharSequence_incomplete() {
        iParseTest.parseBigMoney("12.34 GBP ");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseBigMoney_CharSequence_incompleteLongText() {
        iParseTest.parseBigMoney("12.34 GBP ABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABAB");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseBigMoney_CharSequence_incompleteEmptyParser() {
        iCannotPrint.parseBigMoney("12.34 GBP");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseBigMoney_CharSequence_missingCurrency() {
        MoneyFormatter f = new MoneyFormatterBuilder().appendAmount().toFormatter();
        f.parseBigMoney("12.34");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_parseBigMoney_CharSequence_cannotParse() {
        iCannotParse.parseBigMoney(new StringBuilder());
    }

    @Test(expected = NullPointerException.class)
    public void test_parseBigMoney_CharSequence_nullCharSequence() {
        iParseTest.parseBigMoney((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    // parseMoney(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    public void test_parseMoney_CharSequence() {
        CharSequence input = new StringBuilder("12.34 GBP");
        Money test = iParseTest.parseMoney(input);
        assertEquals(MONEY_GBP_12_34, test);
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseMoney_CharSequence_invalidCurrency() {
        iParseTest.parseMoney("12.34 GBX");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseMoney_CharSequence_notFullyParsed() {
        iParseTest.parseMoney("12.34 GBP X");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseMoney_CharSequence_incomplete() {
        iCannotPrint.parseMoney("12.34 GBP");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_parseMoney_CharSequence_cannotParse() {
        iCannotParse.parseMoney(new StringBuilder());
    }

    @Test(expected = NullPointerException.class)
    public void test_parseMoney_CharSequence_nullCharSequence() {
        iParseTest.parseMoney((CharSequence) null);
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,int)
    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_parse() {
        return new Object[][] {
            new Object[] {"12.34 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 9, -1, false, true, true},
            new Object[] {"1,2.34 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 10, -1, false, true, true},
            new Object[] {"12,.34 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 10, -1, false, true, true},
            new Object[] {"12.,34 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 10, -1, false, true, true},
            new Object[] {"12.3,4 GBP", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 10, -1, false, true, true},
            new Object[] {".12 GBP", BigDecimal.valueOf(12, 2), MONEY_GBP_12_34.getCurrencyUnit(), 7, -1, false, true, true},
            new Object[] {"12. GBP", BigDecimal.valueOf(12), MONEY_GBP_12_34.getCurrencyUnit(), 7, -1, false, true, true},
            new Object[] {"12,34 GBP", BigDecimal.valueOf(1234), MONEY_GBP_12_34.getCurrencyUnit(), 9, -1, false, true, true},
            
            new Object[] {"-12.34 GBP", BigDecimal.valueOf(-1234, 2), CurrencyUnit.GBP, 10, -1, false, true, true},
            new Object[] {"+12.34 GBP", BigDecimal.valueOf(1234, 2), CurrencyUnit.GBP, 10, -1, false, true, true},
            
            new Object[] {"12.34 GB", BigDecimal.valueOf(1234, 2), null, 6, 6, true, false, false},
            new Object[] {",12.34 GBP", null, null, 0, 0, true, false, false},
            new Object[] {"12..34 GBP", BigDecimal.valueOf(12), null, 3, 3, true, false, false},
            new Object[] {"12,,34 GBP", BigDecimal.valueOf(12), null, 2, 2, true, false, false},
            new Object[] {"12.34 GBX", MONEY_GBP_12_34.getAmount(), null, 6, 6, true, false, false},
            new Object[] {"12.34 GBPX", MONEY_GBP_12_34.getAmount(), MONEY_GBP_12_34.getCurrencyUnit(), 9, -1, false, false, true},
        };
    }

    @Test
    @UseDataProvider("data_parse")
    public void test_parse_CharSequenceInt(String str, BigDecimal amount, CurrencyUnit currency,
                    int index, int errorIndex, boolean error, boolean fullyParsed, boolean complete) {
        CharSequence input = new StringBuilder(str);
        MoneyParseContext test = iParseTest.parse(input, 0);
        assertEquals(amount, test.getAmount());
        assertEquals(currency, test.getCurrency());
        assertEquals(index, test.getIndex());
        assertEquals(errorIndex, test.getErrorIndex());
        assertEquals(str, test.getText().toString());
        assertEquals(str.length(), test.getTextLength());
        assertEquals(error, test.isError());
        assertEquals(fullyParsed, test.isFullyParsed());
        assertEquals(complete, test.isComplete());
        ParsePosition pp = new ParsePosition(index);
        pp.setErrorIndex(errorIndex);
        assertEquals(pp, test.toParsePosition());
    }

    @Test
    public void test_parse_CharSequenceInt_incomplete() {
        // this parser does nothing
        MoneyParseContext test = iCannotPrint.parse("12.34 GBP", 0);
        assertEquals(null, test.getAmount());
        assertEquals(null, test.getCurrency());
        assertEquals(0, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        assertEquals("12.34 GBP", test.getText().toString());
        assertEquals(9, test.getTextLength());
        assertEquals(false, test.isError());
        assertEquals(false, test.isFullyParsed());
        assertEquals(false, test.isComplete());
    }

    @Test
    public void test_parse_CharSequenceInt_continueAfterDoubleDecimal() {
        MoneyFormatter f = new MoneyFormatterBuilder()
            .appendAmountLocalized().appendLiteral(".").appendCurrencyCode().toFormatter();
        MoneyParseContext test = f.parse("12..GBP", 0);
        assertEquals(BigDecimal.valueOf(12), test.getAmount());
        assertEquals(CurrencyUnit.of("GBP"), test.getCurrency());
        assertEquals(7, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        assertEquals("12..GBP", test.getText().toString());
        assertEquals(7, test.getTextLength());
        assertEquals(false, test.isError());
        assertEquals(true, test.isFullyParsed());
        assertEquals(true, test.isComplete());
    }

    @Test
    public void test_parse_CharSequenceInt_continueAfterSingleComma() {
        MoneyFormatter f = new MoneyFormatterBuilder()
            .appendAmountLocalized().appendLiteral(",").appendCurrencyCode().toFormatter();
        MoneyParseContext test = f.parse("12,GBP", 0);
        assertEquals(BigDecimal.valueOf(12), test.getAmount());
        assertEquals(CurrencyUnit.of("GBP"), test.getCurrency());
        assertEquals(6, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        assertEquals("12,GBP", test.getText().toString());
        assertEquals(6, test.getTextLength());
        assertEquals(false, test.isError());
        assertEquals(true, test.isFullyParsed());
        assertEquals(true, test.isComplete());
    }

    @Test
    public void test_parse_CharSequenceInt_continueAfterDoubleComma() {
        MoneyFormatter f = new MoneyFormatterBuilder()
            .appendAmountLocalized().appendLiteral(",,").appendCurrencyCode().toFormatter();
        MoneyParseContext test = f.parse("12,,GBP", 0);
        assertEquals(BigDecimal.valueOf(12), test.getAmount());
        assertEquals(CurrencyUnit.of("GBP"), test.getCurrency());
        assertEquals(7, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        assertEquals("12,,GBP", test.getText().toString());
        assertEquals(7, test.getTextLength());
        assertEquals(false, test.isError());
        assertEquals(true, test.isFullyParsed());
        assertEquals(true, test.isComplete());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_parse_CharSequenceInt_cannotParse() {
        iCannotParse.parse(new StringBuilder(), 0);
    }

    @Test(expected = NullPointerException.class)
    public void test_parse_CharSequenceInt_nullCharSequence() {
        iParseTest.parse((CharSequence) null, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_parse_CharSequenceInt_startIndexTooSmall() {
        iParseTest.parse("", -1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_parse_CharSequenceInt_startIndexTooBig() {
        iParseTest.parse("", 1);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_printParse_zeroChar() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withZeroCharacter('A');
        MoneyFormatter f = new MoneyFormatterBuilder().appendCurrencyCode().appendLiteral(" ").appendAmount(style).toFormatter();
        assertEquals("GBP BC.DE", f.print(MONEY_GBP_12_34));
        assertEquals(MONEY_GBP_12_34, f.parseMoney("GBP BC.DE"));
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseMoney_notFullyParsed() {
        iParseTest.parseMoney("GBP hello notfullyparsed");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseMoney_noAmount() {
        iParseTest.parseMoney("GBP hello");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseBigMoney_notFullyParsed() {
        iParseTest.parseBigMoney("GBP hello notfullyparsed");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parseBigMoney_noAmount() {
        iParseTest.parseBigMoney("GBP hello");
    }

    @Test(expected = MoneyFormatException.class)
    public void test_parse_notFullyParsed() {
        MoneyParseContext context = iParseTest.parse("GBP hello notfullyparsed", 1);
        context.toBigMoney();
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        assertEquals("${code}' hello'", iPrintTest.toString());
    }

    @Test
    public void test_toString_differentPrinterParser() {
        MoneyPrinter printer = new MoneyPrinter() {
            @Override
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
            }
            @Override
            public String toString() {
                return "A";
            }
        };
        MoneyParser parser = new MoneyParser() {
            @Override
            public void parse(MoneyParseContext context) {
            }
            @Override
            public String toString() {
                return "B";
            }
        };
        MoneyFormatter f = new MoneyFormatterBuilder().append(printer, parser).toFormatter();
        assertEquals("A:B", f.toString());
    }

    //-----------------------------------------------------------------------
    private static final class IOAppendable implements Appendable {
        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            throw new IOException();
        }
        @Override
        public Appendable append(char c) throws IOException {
            throw new IOException();
        }
        @Override
        public Appendable append(CharSequence csq) throws IOException {
            throw new IOException();
        }
    }

}
