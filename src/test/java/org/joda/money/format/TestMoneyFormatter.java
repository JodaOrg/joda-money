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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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
    private static final Locale TEST_GB_LOCALE = Locale.of("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = Locale.of("fr", "FR", "TEST");
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
            .append(null, context -> {})
            .toFormatter();
        iParseTest = new MoneyFormatterBuilder()
            .appendAmountLocalized()
            .appendLiteral(" ")
            .appendCurrencyCode()
            .toFormatter();
        iCannotParse = new MoneyFormatterBuilder()
            .append((context, appendable, money) -> {}, null)
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
        var a = iPrintTest;
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(a);
            oos.close();
            var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            var input = (MoneyFormatter) ois.readObject();
            var value = MONEY_GBP_12_34;
            assertThat(input.print(value)).isEqualTo(a.print(value));
        }
    }

    //-----------------------------------------------------------------------
    // getLocale() withLocale(Locale)
    //-----------------------------------------------------------------------
    @Test
    void test_getLocale() {
        assertThat(iPrintTest.getLocale()).isEqualTo(TEST_GB_LOCALE);
    }

    @Test
    void test_withLocale() {
        var test = iPrintTest.withLocale(TEST_FR_LOCALE);
        assertThat(iPrintTest.getLocale()).isEqualTo(TEST_GB_LOCALE);
        assertThat(test.getLocale()).isEqualTo(TEST_FR_LOCALE);
    }

    @Test
    void test_withLocale_nullLocale() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iPrintTest.withLocale((Locale) null));
    }

    //-----------------------------------------------------------------------
    // print(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_print_BigMoneyProvider() {
        assertThat(iPrintTest.print(MONEY_GBP_12_34)).isEqualTo("GBP hello");
    }

    @Test
    void test_print_BigMoneyProvider_cannotPrint() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> iCannotPrint.print(MONEY_GBP_12_34));
    }

    @Test
    void test_print_BigMoneyProvider_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iPrintTest.print((BigMoneyProvider) null));
    }

    //-----------------------------------------------------------------------
    // print(Appendable,BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_print_AppendableBigMoneyProvider() {
        var buf = new StringBuilder();
        iPrintTest.print(buf, MONEY_GBP_12_34);
        assertThat(buf).hasToString("GBP hello");
    }

    @Test
    void test_print_AppendableBigMoneyProvider_IOException() {
        Appendable appendable = new IOAppendable();
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iPrintTest.print(appendable, MONEY_GBP_12_34))
            .withCauseInstanceOf(IOException.class);
    }

    @Test
    void test_print_AppendableBigMoneyProvider_cannotPrint() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> iCannotPrint.print(new StringBuilder(), MONEY_GBP_12_34));
    }

    @Test
    void test_print_AppendableBigMoneyProvider_nullAppendable() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iPrintTest.print((Appendable) null, MONEY_GBP_12_34));
    }

    @Test
    void test_print_AppendableBigMoneyProvider_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iPrintTest.print(new StringBuilder(), (BigMoneyProvider) null));
    }

    //-----------------------------------------------------------------------
    // printIO(Appendable,BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_printIO_AppendableBigMoneyProvider() throws IOException {
        var buf = new StringBuilder();
        iPrintTest.printIO(buf, MONEY_GBP_12_34);
        assertThat(buf).hasToString("GBP hello");
    }

    @Test
    void test_printIO_AppendableBigMoneyProvider_IOException() {
        Appendable appendable = new IOAppendable();
        assertThatExceptionOfType(IOException.class)
            .isThrownBy(() -> iPrintTest.printIO(appendable, MONEY_GBP_12_34));
    }

    @Test
    void test_printIO_AppendableBigMoneyProvider_cannotPrint() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> iCannotPrint.printIO(new StringBuilder(), MONEY_GBP_12_34));
    }

    @Test
    void test_printIO_AppendableBigMoneyProvider_nullAppendable() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iPrintTest.printIO((Appendable) null, MONEY_GBP_12_34));
    }

    @Test
    void test_printIO_AppendableBigMoneyProvider_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iPrintTest.printIO(new StringBuilder(), (BigMoneyProvider) null));
    }

    //-----------------------------------------------------------------------
    // parseBigMoney(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    void test_parseBigMoney_CharSequence() {
        CharSequence input = new StringBuilder("12.34 GBP");
        var test = iParseTest.parseBigMoney(input);
        assertThat(test).isEqualTo(MONEY_GBP_12_34.toBigMoney());
    }

    @Test
    void test_parseBigMoney_CharSequence_invalidCurrency() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseBigMoney("12.34 GBX"));
    }

    @Test
    void test_parseBigMoney_CharSequence_notFullyParsed() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseBigMoney("12.34 GBP X"));
    }

    @Test
    void test_parseBigMoney_CharSequence_incomplete() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseBigMoney("12.34 GBP "));
    }

    @Test
    void test_parseBigMoney_CharSequence_incompleteLongText() {
        var str = "12.34 GBP ABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABABAB";
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseBigMoney(str));
    }

    @Test
    void test_parseBigMoney_CharSequence_incompleteEmptyParser() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iCannotPrint.parseBigMoney("12.34 GBP"));
    }

    @Test
    void test_parseBigMoney_CharSequence_missingCurrency() {
        var f = new MoneyFormatterBuilder().appendAmount().toFormatter();
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> f.parseBigMoney("12.34"));
    }

    @Test
    void test_parseBigMoney_CharSequence_cannotParse() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> iCannotParse.parseBigMoney(new StringBuilder()));
    }

    @Test
    void test_parseBigMoney_CharSequence_nullCharSequence() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iParseTest.parseBigMoney((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    // parseMoney(CharSequence)
    //-----------------------------------------------------------------------
    @Test
    void test_parseMoney_CharSequence() {
        CharSequence input = new StringBuilder("12.34 GBP");
        var test = iParseTest.parseMoney(input);
        assertThat(test).isEqualTo(MONEY_GBP_12_34);
    }

    @Test
    void test_parseMoney_CharSequence_invalidCurrency() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseMoney("12.34 GBX"));
    }

    @Test
    void test_parseMoney_CharSequence_notFullyParsed() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseMoney("12.34 GBP X"));
    }

    @Test
    void test_parseMoney_CharSequence_incomplete() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iCannotPrint.parseMoney("12.34 GBP"));
    }

    @Test
    void test_parseMoney_CharSequence_cannotParse() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> iCannotParse.parseMoney(new StringBuilder()));
    }

    @Test
    void test_parseMoney_CharSequence_nullCharSequence() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iParseTest.parseMoney((CharSequence) null));
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
        var test = iParseTest.parse(input, 0);
        assertThat(test.getAmount()).isEqualTo(amount);
        assertThat(test.getCurrency()).isEqualTo(currency);
        assertThat(test.getIndex()).isEqualTo(index);
        assertThat(test.getErrorIndex()).isEqualTo(errorIndex);
        assertThat(test.getText()).hasToString(str);
        assertThat(test.getTextLength()).isEqualTo(str.length());
        assertThat(test.isError()).isEqualTo(error);
        assertThat(test.isFullyParsed()).isEqualTo(fullyParsed);
        assertThat(test.isComplete()).isEqualTo(complete);
        var pp = new ParsePosition(index);
        pp.setErrorIndex(errorIndex);
        assertThat(test.toParsePosition()).isEqualTo(pp);
    }

    @Test
    void test_parse_CharSequenceInt_incomplete() {
        // this parser does nothing
        var test = iCannotPrint.parse("12.34 GBP", 0);
        assertThat(test.getAmount()).isNull();
        assertThat(test.getCurrency()).isNull();
        assertThat(test.getIndex()).isEqualTo(0);
        assertThat(test.getErrorIndex()).isEqualTo(-1);
        assertThat(test.getText()).hasToString("12.34 GBP");
        assertThat(test.getTextLength()).isEqualTo(9);
        assertThat(test.isError()).isFalse();
        assertThat(test.isFullyParsed()).isFalse();
        assertThat(test.isComplete()).isFalse();
    }

    @Test
    void test_parse_CharSequenceInt_continueAfterDoubleDecimal() {
        var f = new MoneyFormatterBuilder()
            .appendAmountLocalized().appendLiteral(".").appendCurrencyCode().toFormatter();
        var test = f.parse("12..GBP", 0);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(12));
        assertThat(test.getCurrency()).isEqualTo(CurrencyUnit.of("GBP"));
        assertThat(test.getIndex()).isEqualTo(7);
        assertThat(test.getErrorIndex()).isEqualTo(-1);
        assertThat(test.getText()).hasToString("12..GBP");
        assertThat(test.getTextLength()).isEqualTo(7);
        assertThat(test.isError()).isFalse();
        assertThat(test.isFullyParsed()).isTrue();
        assertThat(test.isComplete()).isTrue();
    }

    @Test
    void test_parse_CharSequenceInt_continueAfterSingleComma() {
        var f = new MoneyFormatterBuilder()
            .appendAmountLocalized().appendLiteral(",").appendCurrencyCode().toFormatter();
        var test = f.parse("12,GBP", 0);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(12));
        assertThat(test.getCurrency()).isEqualTo(CurrencyUnit.of("GBP"));
        assertThat(test.getIndex()).isEqualTo(6);
        assertThat(test.getErrorIndex()).isEqualTo(-1);
        assertThat(test.getText()).hasToString("12,GBP");
        assertThat(test.getTextLength()).isEqualTo(6);
        assertThat(test.isError()).isFalse();
        assertThat(test.isFullyParsed()).isTrue();
        assertThat(test.isComplete()).isTrue();
    }

    @Test
    void test_parse_CharSequenceInt_continueAfterDoubleComma() {
        var f = new MoneyFormatterBuilder()
            .appendAmountLocalized().appendLiteral(",,").appendCurrencyCode().toFormatter();
        var test = f.parse("12,,GBP", 0);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(12));
        assertThat(test.getCurrency()).isEqualTo(CurrencyUnit.of("GBP"));
        assertThat(test.getIndex()).isEqualTo(7);
        assertThat(test.getErrorIndex()).isEqualTo(-1);
        assertThat(test.getText()).hasToString("12,,GBP");
        assertThat(test.getTextLength()).isEqualTo(7);
        assertThat(test.isError()).isFalse();
        assertThat(test.isFullyParsed()).isTrue();
        assertThat(test.isComplete()).isTrue();
    }

    @Test
    void test_parse_CharSequenceInt_cannotParse() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> iCannotParse.parse(new StringBuilder(), 0));
    }

    @Test
    void test_parse_CharSequenceInt_nullCharSequence() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iParseTest.parse((CharSequence) null, 0));
    }

    @Test
    void test_parse_CharSequenceInt_startIndexTooSmall() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
            .isThrownBy(() -> iParseTest.parse("", -1));
    }

    @Test
    void test_parse_CharSequenceInt_startIndexTooBig() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
            .isThrownBy(() -> iParseTest.parse("", 1));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_printParse_zeroChar() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withZeroCharacter('A');
        var f = new MoneyFormatterBuilder().appendCurrencyCode().appendLiteral(" ").appendAmount(style).toFormatter();
        assertThat(f.print(MONEY_GBP_12_34)).isEqualTo("GBP BC.DE");
        assertThat(f.parseMoney("GBP BC.DE")).isEqualTo(MONEY_GBP_12_34);
    }

    @Test
    void test_parseMoney_notFullyParsed() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseMoney("GBP hello notfullyparsed"));
    }

    @Test
    void test_parseMoney_noAmount() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseMoney("GBP hello"));
    }

    @Test
    void test_parseBigMoney_notFullyParsed() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseBigMoney("GBP hello notfullyparsed"));
    }

    @Test
    void test_parseBigMoney_noAmount() {
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> iParseTest.parseBigMoney("GBP hello"));
    }

    @Test
    void test_parse_notFullyParsed() {
        var context = iParseTest.parse("GBP hello notfullyparsed", 1);
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> context.toBigMoney());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    void test_toString() {
        assertThat(iPrintTest.toString()).isEqualTo("${code}' hello'");
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
        var f = new MoneyFormatterBuilder().append(printer, parser).toFormatter();
        assertThat(f).hasToString("A:B");
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
