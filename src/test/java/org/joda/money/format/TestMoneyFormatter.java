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

/**
 * Test MoneyFormatter.
 */
class TestMoneyFormatter {

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = Locale.of("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = Locale.of("fr", "FR", "TEST");

    private static final Money MONEY_GBP_12_34 = Money.parse("GBP 12.34");

    // Formatters used in various tests
    private MoneyFormatter iPrintTest;
    private MoneyFormatter iCannotPrint;
    private MoneyFormatter iParseTest;
    private MoneyFormatter iCannotParse;

    @BeforeEach
    void beforeMethod() {
        // Set default locale for consistent tests
        Locale.setDefault(TEST_GB_LOCALE);

        // A formatter that can print but not parse currency amounts
        iPrintTest = new MoneyFormatterBuilder()
                .appendCurrencyCode()
                .appendLiteral(" hello")
                .toFormatter();

        // A formatter that cannot print (null printer) but can parse
        iCannotPrint = new MoneyFormatterBuilder()
                .append(null, (context) -> {})
                .toFormatter();

        // A formatter that can parse localized amounts plus currency code
        iParseTest = new MoneyFormatterBuilder()
                .appendAmountLocalized()
                .appendLiteral(" ")
                .appendCurrencyCode()
                .toFormatter();

        // A formatter that cannot parse (null parser)
        iCannotParse = new MoneyFormatterBuilder()
                .append((context, appendable, money) -> {}, null)
                .toFormatter();
    }

    @AfterEach
    void afterMethod() {
        Locale.setDefault(cCachedLocale);
        iPrintTest = null;
        iCannotPrint = null;
        iParseTest = null;
        iCannotParse = null;
    }

    //-----------------------------------------------------------------------
    // Basic printing tests
    //-----------------------------------------------------------------------
    @Test
    void test_print_BigMoneyProvider() {
        // iPrintTest can print
        assertThat(iPrintTest.print(MONEY_GBP_12_34))
                .isEqualTo("GBP hello");
    }

    @Test
    void test_print_BigMoneyProvider_cannotPrint() {
        // iCannotPrint has a null printer
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> iCannotPrint.print(MONEY_GBP_12_34));
    }

    @Test
    void test_print_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> iPrintTest.print((BigMoneyProvider) null));
    }

    //-----------------------------------------------------------------------
    // Basic parsing tests
    //-----------------------------------------------------------------------
    @Test
    void test_parseMoney_valid() {
        // iParseTest can parse localized amounts + currency code
        CharSequence input = "12.34 GBP";
        Money parsed = iParseTest.parseMoney(input);
        assertThat(parsed).isEqualTo(MONEY_GBP_12_34);
    }

    @Test
    void test_parseMoney_invalidCurrency() {
        // Show how to parse with a custom style:
        // replaced old public constant with new static method
        MoneyFormatter f = new MoneyFormatterBuilder()
                .appendAmount(MoneyAmountStyle.asciiDecimalPointGroup3Comma())
                .toFormatter();

        assertThatExceptionOfType(MoneyFormatException.class)
                .isThrownBy(() -> f.parseMoney("12.34 GBX")); // invalid currency code
    }

    @Test
    void test_parseMoney_notFullyParsed() {
        // iParseTest expects "amount currency", no extra text
        assertThatExceptionOfType(MoneyFormatException.class)
                .isThrownBy(() -> iParseTest.parseMoney("12.34 GBP X"));
    }

    @Test
    void test_parseMoney_incomplete() {
        // iCannotPrint has no parser for amounts
        // Actually it has a parser but let's say it's not enough
        assertThatExceptionOfType(MoneyFormatException.class)
                .isThrownBy(() -> iCannotPrint.parseMoney("12.34 GBP"));
    }

    @Test
    void test_parseMoney_cannotParse() {
        // iCannotParse has a null parser
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> iCannotParse.parseMoney("12.34 GBP"));
    }

    @Test
    void test_parseMoney_nullCharSequence() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> iParseTest.parseMoney((CharSequence) null));
    }

    //-----------------------------------------------------------------------
    // Additional tests for parseBigMoney, print to Appendable, etc.
    // (Same approach: no references to old MoneyAmountStyle constants)
    //-----------------------------------------------------------------------
    @Test
    void test_parseBigMoney_valid() {
        CharSequence input = "12.34 GBP";
        BigMoney big = iParseTest.parseBigMoney(input);
        assertThat(big.toMoney()).isEqualTo(MONEY_GBP_12_34);
    }

    @Test
    void test_parseBigMoney_invalid() {
        MoneyFormatter f = new MoneyFormatterBuilder()
                .appendAmount(MoneyAmountStyle.asciiDecimalPointGroup3Comma())
                .toFormatter();

        assertThatExceptionOfType(MoneyFormatException.class)
                .isThrownBy(() -> f.parseBigMoney("12.34 GBX"));
    }

    //-----------------------------------------------------------------------
    // Example of serialization test, if needed
    //-----------------------------------------------------------------------
    @Test
    void test_serialization() throws Exception {
        MoneyFormatter original = iPrintTest;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray()))) {
            MoneyFormatter deserialized = (MoneyFormatter) ois.readObject();
            assertThat(deserialized.print(MONEY_GBP_12_34))
                    .isEqualTo(original.print(MONEY_GBP_12_34));
        }
    }
}
