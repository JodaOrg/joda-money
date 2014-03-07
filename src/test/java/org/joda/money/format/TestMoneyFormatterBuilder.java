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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test MoneyFormatterBuilder.
 */
@Test
public class TestMoneyFormatterBuilder {

    private static final Money GBP_2_34 = Money.parse("GBP 2.34");
    private static final Money GBP_23_45 = Money.parse("GBP 23.45");
    private static final Money GBP_234_56 = Money.parse("GBP 234.56");
    private static final Money GBP_MINUS_234_56 = Money.parse("GBP -234.56");
    private static final Money GBP_2345_67 = Money.parse("GBP 2345.67");
    private static final Money GBP_1234567_89 = Money.parse("GBP 1234567.89");
    private static final BigMoney GBP_1234_56789 = BigMoney.parse("GBP 1234.56789");
    private static final Money JPY_2345 = Money.parse("JPY 2345");

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = new Locale("fr", "FR", "TEST");
    private static final DecimalFormatSymbols FR_SYMBOLS = new DecimalFormatSymbols(Locale.FRANCE);
    private static final char FR_DECIMAL = FR_SYMBOLS.getMonetaryDecimalSeparator();
    private static final char FR_GROUP = FR_SYMBOLS.getGroupingSeparator();

    private MoneyFormatterBuilder iBuilder;

    @BeforeMethod
    public void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
        iBuilder = new MoneyFormatterBuilder();
    }

    @AfterMethod
    public void afterMethod() {
        Locale.setDefault(cCachedLocale);
        iBuilder = null;
    }

    //-----------------------------------------------------------------------
    public void test_empty() {
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "");
        assertEquals(test.toString(), "");
    }

    //-----------------------------------------------------------------------
    public void test_appendCurrencyCode_print() {
        iBuilder.appendCurrencyCode();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "GBP");
        assertEquals(test.toString(), "${code}");
    }

    public void test_appendCurrencyCode_parse_ok() {
        iBuilder.appendCurrencyCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("GBP", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 3);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), CurrencyUnit.GBP);
    }

    public void test_appendCurrencyCode_parse_tooShort() {
        iBuilder.appendCurrencyCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("GB", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyCode_parse_empty() {
        iBuilder.appendCurrencyCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    //-----------------------------------------------------------------------
    public void test_appendCurrencyNumeric3Code_print() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "826");
        assertEquals(test.toString(), "${numeric3Code}");
    }

    public void test_appendCurrencyNumeric3Code_parse_ok() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("826A", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 3);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), CurrencyUnit.GBP);
    }

    public void test_appendCurrencyNumeric3Code_parse_tooShort() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("82", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyNumeric3Code_parse_badCurrency() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("991A", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyNumeric3Code_parse_empty() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    //-----------------------------------------------------------------------
    public void test_appendCurrencyNumericCode_print() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "826");
        assertEquals(test.toString(), "${numericCode}");
    }

    public void test_appendCurrencyNumericCode_parse_ok() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("826A", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 3);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), CurrencyUnit.GBP);
    }

    public void test_appendCurrencyNumericCode_parse_ok_padded() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("008A", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 3);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency().getCode(), "ALL");
    }

    public void test_appendCurrencyNumericCode_parse_ok_notPadded1() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("8A", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 1);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency().getCode(), "ALL");
    }

    public void test_appendCurrencyNumericCode_parse_ok_notPadded2() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("51 ", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 2);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency().getCode(), "AMD");
    }

    public void test_appendCurrencyNumericCode_parse_tooShort() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyNumericCode_parse_badCurrency() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("991A", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyNumericCode_parse_empty() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    //-----------------------------------------------------------------------
    public void test_appendCurrencySymbolLocalized_print() {
        iBuilder.appendCurrencySymbolLocalized();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "\u00a3");
        assertEquals(test.toString(), "${symbolLocalized}");
    }

    public void test_appendCurrencySymbolLocalized_parse() {
        iBuilder.appendCurrencySymbolLocalized();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.isParser(), false);
    }

    //-----------------------------------------------------------------------
    public void test_appendLiteral_print() {
        iBuilder.appendLiteral("Hello");
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "Hello");
        assertEquals(test.toString(), "'Hello'");
    }

    public void test_appendLiteral_print_empty() {
        iBuilder.appendLiteral("");
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "");
        assertEquals(test.toString(), "");
    }

    public void test_appendLiteral_print_null() {
        iBuilder.appendLiteral((CharSequence) null);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "");
        assertEquals(test.toString(), "");
    }

    public void test_appendLiteral_parse_ok() {
        iBuilder.appendLiteral("Hello");
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("HelloWorld", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 5);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendLiteral_parse_tooShort() {
        iBuilder.appendLiteral("Hello");
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("Hell", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendLiteral_parse_noMatch() {
        iBuilder.appendLiteral("Hello");
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("Helol", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    //-----------------------------------------------------------------------
    public void test_appendAmount_GBP_2_34() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "2.34");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_23_45() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_23_45), "23.45");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_234_56() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_234_56), "234.56");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_2345_67() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2345_67), "2,345.67");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_1234567_89() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_1234567_89), "1,234,567.89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_1234_56789() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_1234_56789), "1,234.567,89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_1234_56789_France() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_1234_56789), "1,234.567,89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_JPY_2345() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(JPY_2345), "2,345");
        assertEquals(test.toString(), "${amount}");
    }

    //-----------------------------------------------------------------------
    public void test_appendAmountLocalized_GBP_2_34() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_2_34), "2" + FR_DECIMAL + "34");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_23_45() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_23_45), "23" + FR_DECIMAL + "45");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_234_56() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_234_56), "234" + FR_DECIMAL + "56");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_MINUS_234_56() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_MINUS_234_56), "-234" + FR_DECIMAL + "56");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_2345_67() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_2345_67), "2" + FR_GROUP + "345" + FR_DECIMAL + "67");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_1234567_89() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_1234567_89), "1" + FR_GROUP + "234" + FR_GROUP + "567" + FR_DECIMAL + "89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_1234_56789() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_1234_56789), "1" + FR_GROUP + "234" + FR_DECIMAL + "567" + FR_GROUP + "89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_1234_56789_US() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.US);
        assertEquals(test.print(GBP_1234_56789), "1,234.567,89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_JPY_2345() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(JPY_2345), "2" + FR_GROUP + "345");
        assertEquals(test.toString(), "${amount}");
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions = NullPointerException.class)
    public void test_appendAmount_MoneyAmountStyle_null() {
        iBuilder.appendAmount((MoneyAmountStyle) null);
    }

    public void test_appendAmount_MoneyAmountStyle_noGrouping_GBP_2_34() {
        iBuilder.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "2.34");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_MoneyAmountStyle_noGrouping_GBP_23_45() {
        iBuilder.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_23_45), "23.45");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_MoneyAmountStyle_noGrouping_GBP_234_56() {
        iBuilder.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_234_56), "234.56");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_MoneyAmountStyle_noGrouping_GBP_2345_67() {
        iBuilder.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2345_67), "2345.67");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_MoneyAmountStyle_noGrouping_GBP_1234567_89() {
        iBuilder.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_1234567_89), "1234567.89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_MoneyAmountStyle_noGrouping_JPY_2345() {
        iBuilder.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(JPY_2345), "2345");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_MoneyAmountStyle_groupingForceDecimal_JPY_2345() {
        iBuilder.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withForcedDecimalPoint(true));
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(JPY_2345), "2,345.");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_MoneyAmountStyle_noGroupingForceDecimal_JPY_2345() {
        iBuilder.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING.withForcedDecimalPoint(true));
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(JPY_2345), "2345.");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_MoneyAmountStyle_noGroupingZeroCharacter_GBP_2345_67() {
        iBuilder.appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING.withZeroCharacter('A'));
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2345_67), "CDEF.GH");
        assertEquals(test.toString(), "${amount}");
    }

    //-----------------------------------------------------------------------
    public void test_append_MoneyPrinterMoneyParser_printer() {
        MoneyPrinter printer = new MoneyPrinter() {
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                appendable.append("HELLO");
            }
        };
        iBuilder.append(printer, null);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.isPrinter(), true);
        assertEquals(test.isParser(), false);
        assertEquals(test.print(JPY_2345), "HELLO");
        assertEquals(test.toString().startsWith("org.joda.money.format.TestMoneyFormatterBuilder$"), true);
    }

    public void test_append_MoneyPrinterMoneyParser_parser() {
        MoneyParser parser = new MoneyParser() {
            public void parse(MoneyParseContext context) {
                context.setAmount(JPY_2345.getAmount());
                context.setCurrency(JPY_2345.getCurrencyUnit());
            }
        };
        iBuilder.append(null, parser);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.isPrinter(), false);
        assertEquals(test.isParser(), true);
        assertEquals(test.parseMoney(""), JPY_2345);
        assertEquals(test.toString().startsWith("org.joda.money.format.TestMoneyFormatterBuilder$"), true);
    }

    public void test_append_MoneyPrinter_nullMoneyPrinter_nullMoneyParser() {
        iBuilder.append((MoneyPrinter) null, (MoneyParser) null);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.isPrinter(), false);
        assertEquals(test.isParser(), false);
        assertEquals(test.toString(), "");
    }

    //-----------------------------------------------------------------------
    public void test_append_MoneyFormatter() {
        MoneyFormatter f1 = new MoneyFormatterBuilder().appendAmount().toFormatter();
        MoneyFormatter f2 = new MoneyFormatterBuilder().appendCurrencyCode().appendLiteral(" ").append(f1).toFormatter();
        assertEquals(f2.print(GBP_2345_67), "GBP 2,345.67");
    }

    //-----------------------------------------------------------------------
    public void test_toFormatter_defaultLocale() {
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.getLocale(), TEST_GB_LOCALE);
    }

    public void test_toFormatter_Locale() {
        MoneyFormatter test = iBuilder.toFormatter(TEST_FR_LOCALE);
        assertEquals(test.getLocale(), TEST_FR_LOCALE);
    }

}
