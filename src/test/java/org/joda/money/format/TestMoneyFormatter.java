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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test MoneyFormatter.
 */
class TestMoneyFormatter {

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = new Locale("fr", "FR", "TEST");
    private static final Money MONEY_GBP_12_34 = Money.parse("GBP 12.34");
    private MoneyFormatter iPrintTest;
    private MoneyFormatter iCannotPrint;
    private MoneyFormatter iParseTest;
    private MoneyFormatter iCannotParse;

    @BeforeEach
    void beforeMethod() {
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

    @AfterEach
    void afterMethod() {
        Locale.setDefault(cCachedLocale);
        iPrintTest = null;
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test
    void test_serialization() throws Exception {
        MoneyFormatter a = iPrintTest;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(a);
            oos.close();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            MoneyFormatter input = (MoneyFormatter) ois.readObject();
            Money value = MONEY_GBP_12_34;
            assertEquals(a.print(value), input.print(value));
        }
    }

    //-----------------------------------------------------------------------
    // getLocale() withLocale(Locale)
    //-----------------------------------------------------------------------
    @Test
    void test_getLocale() {
        assertEquals(TEST_GB_LOCALE, iPrintTest.getLocale());
    }

    @Test
    void test_withLocale() {
        MoneyFormatter test = iPrintTest.withLocale(TEST_FR_LOCALE);
        assertEquals(TEST_GB_LOCALE, iPrintTest.getLocale());
        assertEquals(TEST_FR_LOCALE, test.getLocale());
    }

    @Test
    void test_withLocale_nullLocale() {
        assertThrows(NullPointerException.class, () -> {
            iPrintTest.withLocale((Locale) null);
        });
    }

    //-----------------------------------------------------------------------
    // print(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_print_BigMoneyProvider() {
        assertEquals("GBP hello", iPrintTest.print(MONEY_GBP_12_34));
    }

    @Test
    void test_print_BigMoneyProvider_cannotPrint() {
        assertThrows(UnsupportedOperationException.class, () -> {
            iCannotPrint.print(MONEY_GBP_12_34);
        });
    }

    @Test
    void test_print_BigMoneyProvider_nullBigMoneyProvider() {
        assertThrows(NullPointerException.class, () -> {
            iPrintTest.print((BigMoneyProvider) null);
        });
    }

    //-----------------------------------------------------------------------
    // print(Appendable,BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_print_AppendableBigMoneyProvider() {
        StringBuilder buf = new StringBuilder();
        iPrintTest.print(buf, MONEY_GBP_12_34);
        assertEquals("GBP hello", buf.toString());
    }

    @Test
    void test_print_AppendableBigMoneyProvider_IOException() {
        assertThrows(MoneyFormatException.class, () -> {
            Appendable appendable = new IOAppendable();
            try {
                iPrintTest.print(appendable, MONEY_GBP_12_34);
            } catch (MoneyFormatException ex) {
                assertEquals(IOException.class, ex.getCause().getClass());
                throw ex;
            }
        });
    }

    @Test
    void test_print_AppendableBigMoneyProvider_cannotPrint() {
        assertThrows(UnsupportedOperationException.class, () -> {
            iCannotPrint.print(new StringBuilder(), MONEY_GBP_12_34);
        });
    }

    @Test
    void test_print_AppendableBigMoneyProvider_nullAppendable() {
        assertThrows(NullPointerException.class, () -> {
            iPrintTest.print((Appendable) null, MONEY_GBP_12_34);
        });
    }

    @Test
    void test_print_AppendableBigMoneyProvider_nullBigMoneyProvider() {
        assertThrows(NullPointerException.class, () -> {
            iPrintTest.print(new StringBuilder(), (BigMoneyProvider) null);
        });
    }

    //-----------------------------------------------------------------------
    // printIO(Appendable,BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_printIO_AppendableBigMoneyProvider() throws IOException {
        StringBuilder buf = new StringBuilder();
        iPrintTest.printIO(buf, MONEY_GBP_12_34);
        assertEquals("GBP hello", buf.toString());
    }

    @Test
    void test_printIO_AppendableBigMoneyProvider_IOException() {
        Appendable appendable = new IOAppendable();
        assertThrows(IOException.class, () -> {
            iPrintTest.printIO(appendable, MONEY_GBP_12_34);
        });
    }

    @Test
    void test_printIO_AppendableBigMoneyProvider_cannotPrint() {
        assertThrows(UnsupportedOperationException.class, () -> {
            iCannotPrint.printIO(new StringBuilder(), MONEY_GBP_12_34);
        });
    }

    @Test
    void test_printIO_AppendableBigMoneyProvider_nullAppendable() {
        assertThrows(NullPointerException.class, () -> {
            iPrintTest.printIO((Appendable) null, MONEY_GBP_12_34);
        });
    }

    @Test
    void test_printIO_AppendableBigMoneyProvider_nullBigMoneyProvider() {
        assertThrows(NullPointerException.class, () -> {
            iPrintTest.printIO(new StringBuilder(), (BigMoneyProvider) null);
        });
    }

    //-----------------------------------------------------------------------
    // parseBigMoney(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    void test_parseBigMoney_CharSequence() {
        CharSequence input = new StringBuilder("12.34 GBP");
        BigMoney test = iParseTest.parseBigMoney(input);
        assertEquals(MONEY_GBP_12_34.toBigMoney(), test);
    }

    @Test
    void test_parseBigMoney_CharSequence_invalidCurrency() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseBigMoney("12.34 GBX");
        });
    }

    @Test
    void test_parseBigMoney_CharSequence_notFullyParsed() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseBigMoney("12.34 GBP X");
        });
    }

    @Test
    void test_parseBigMoney_CharSequence_incomplete() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseBigMoney("12.34 GBP ");
        });
    }

    @Test
    void test_parseBigMoney_CharSequence_incompleteLongText() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseBigMoney("12.34 GBP ABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABAB");
        });
    }

    @Test
    void test_parseBigMoney_CharSequence_incompleteEmptyParser() {
        assertThrows(MoneyFormatException.class, () -> {
            iCannotPrint.parseBigMoney("12.34 GBP");
        });
    }

    @Test
    void test_parseBigMoney_CharSequence_missingCurrency() {
        MoneyFormatter f = new MoneyFormatterBuilder().appendAmount().toFormatter();
        assertThrows(MoneyFormatException.class, () -> {
            f.parseBigMoney("12.34");
        });
    }

    @Test
    void test_parseBigMoney_CharSequence_cannotParse() {
        assertThrows(UnsupportedOperationException.class, () -> {
            iCannotParse.parseBigMoney(new StringBuilder());
        });
    }

    @Test
    void test_parseBigMoney_CharSequence_nullCharSequence() {
        assertThrows(NullPointerException.class, () -> {
            iParseTest.parseBigMoney((CharSequence) null);
        });
    }

    //-----------------------------------------------------------------------
    // parseMoney(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    void test_parseMoney_CharSequence() {
        CharSequence input = new StringBuilder("12.34 GBP");
        Money test = iParseTest.parseMoney(input);
        assertEquals(MONEY_GBP_12_34, test);
    }

    @Test
    void test_parseMoney_CharSequence_invalidCurrency() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseMoney("12.34 GBX");
        });
    }

    @Test
    void test_parseMoney_CharSequence_notFullyParsed() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseMoney("12.34 GBP X");
        });
    }

    @Test
    void test_parseMoney_CharSequence_incomplete() {
        assertThrows(MoneyFormatException.class, () -> {
            iCannotPrint.parseMoney("12.34 GBP");
        });
    }

    @Test
    void test_parseMoney_CharSequence_cannotParse() {
        assertThrows(UnsupportedOperationException.class, () -> {
            iCannotParse.parseMoney(new StringBuilder());
        });
    }

    @Test
    void test_parseMoney_CharSequence_nullCharSequence() {
        assertThrows(NullPointerException.class, () -> {
            iParseTest.parseMoney((CharSequence) null);
        });
    }

    //-----------------------------------------------------------------------
    // parse(CharSequence,int)
    //-----------------------------------------------------------------------
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

    @ParameterizedTest
    @MethodSource("data_parse")
    void test_parse_CharSequenceInt(
            String str,
            BigDecimal amount,
            CurrencyUnit currency,
            int index,
            int errorIndex,
            boolean error,
            boolean fullyParsed,
            boolean complete) {

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
    void test_parse_CharSequenceInt_incomplete() {
        // this parser does nothing
        MoneyParseContext test = iCannotPrint.parse("12.34 GBP", 0);
        assertNull(test.getAmount());
        assertNull(test.getCurrency());
        assertEquals(0, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        assertEquals("12.34 GBP", test.getText().toString());
        assertEquals(9, test.getTextLength());
        assertFalse(test.isError());
        assertFalse(test.isFullyParsed());
        assertFalse(test.isComplete());
    }

    @Test
    void test_parse_CharSequenceInt_continueAfterDoubleDecimal() {
        MoneyFormatter f = new MoneyFormatterBuilder()
            .appendAmountLocalized().appendLiteral(".").appendCurrencyCode().toFormatter();
        MoneyParseContext test = f.parse("12..GBP", 0);
        assertEquals(BigDecimal.valueOf(12), test.getAmount());
        assertEquals(CurrencyUnit.of("GBP"), test.getCurrency());
        assertEquals(7, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        assertEquals("12..GBP", test.getText().toString());
        assertEquals(7, test.getTextLength());
        assertFalse(test.isError());
        assertTrue(test.isFullyParsed());
        assertTrue(test.isComplete());
    }

    @Test
    void test_parse_CharSequenceInt_continueAfterSingleComma() {
        MoneyFormatter f = new MoneyFormatterBuilder()
            .appendAmountLocalized().appendLiteral(",").appendCurrencyCode().toFormatter();
        MoneyParseContext test = f.parse("12,GBP", 0);
        assertEquals(BigDecimal.valueOf(12), test.getAmount());
        assertEquals(CurrencyUnit.of("GBP"), test.getCurrency());
        assertEquals(6, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        assertEquals("12,GBP", test.getText().toString());
        assertEquals(6, test.getTextLength());
        assertFalse(test.isError());
        assertTrue(test.isFullyParsed());
        assertTrue(test.isComplete());
    }

    @Test
    void test_parse_CharSequenceInt_continueAfterDoubleComma() {
        MoneyFormatter f = new MoneyFormatterBuilder()
            .appendAmountLocalized().appendLiteral(",,").appendCurrencyCode().toFormatter();
        MoneyParseContext test = f.parse("12,,GBP", 0);
        assertEquals(BigDecimal.valueOf(12), test.getAmount());
        assertEquals(CurrencyUnit.of("GBP"), test.getCurrency());
        assertEquals(7, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        assertEquals("12,,GBP", test.getText().toString());
        assertEquals(7, test.getTextLength());
        assertFalse(test.isError());
        assertTrue(test.isFullyParsed());
        assertTrue(test.isComplete());
    }

    @Test
    void test_parse_CharSequenceInt_cannotParse() {
        assertThrows(UnsupportedOperationException.class, () -> {
            iCannotParse.parse(new StringBuilder(), 0);
        });
    }

    @Test
    void test_parse_CharSequenceInt_nullCharSequence() {
        assertThrows(NullPointerException.class, () -> {
            iParseTest.parse((CharSequence) null, 0);
        });
    }

    @Test
    void test_parse_CharSequenceInt_startIndexTooSmall() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            iParseTest.parse("", -1);
        });
    }

    @Test
    void test_parse_CharSequenceInt_startIndexTooBig() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            iParseTest.parse("", 1);
        });
    }

    //-----------------------------------------------------------------------
    @Test
    void test_printParse_zeroChar() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withZeroCharacter('A');
        MoneyFormatter f = new MoneyFormatterBuilder().appendCurrencyCode().appendLiteral(" ").appendAmount(style).toFormatter();
        assertEquals("GBP BC.DE", f.print(MONEY_GBP_12_34));
        assertEquals(MONEY_GBP_12_34, f.parseMoney("GBP BC.DE"));
    }

    @Test
    void test_parseMoney_notFullyParsed() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseMoney("GBP hello notfullyparsed");
        });
    }

    @Test
    void test_parseMoney_noAmount() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseMoney("GBP hello");
        });
    }

    @Test
    void test_parseBigMoney_notFullyParsed() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseBigMoney("GBP hello notfullyparsed");
        });
    }

    @Test
    void test_parseBigMoney_noAmount() {
        assertThrows(MoneyFormatException.class, () -> {
            iParseTest.parseBigMoney("GBP hello");
        });
    }

    @Test
    void test_parse_notFullyParsed() {
        assertThrows(MoneyFormatException.class, () -> {
            MoneyParseContext context = iParseTest.parse("GBP hello notfullyparsed", 1);
            context.toBigMoney();
        });
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    void test_toString() {
        assertEquals("${code}' hello'", iPrintTest.toString());
    }

    @Test
    void test_toString_differentPrinterParser() {
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
