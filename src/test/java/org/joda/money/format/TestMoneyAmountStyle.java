package org.joda.money.format;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test MoneyAmountStyle.
 */
class TestMoneyAmountStyle {

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = Locale.of("en", "GB", "TEST");
    private static final Locale TEST_DE_LOCALE = Locale.of("de", "DE", "TEST");
    private static final Locale TEST_LV_LOCALE = Locale.of("lv", "LV", "TEST");
    private static final BigMoney MONEY = BigMoney.of(CurrencyUnit.GBP, new BigDecimal("87654321.12345678"));

    @BeforeEach
    void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
    }

    @AfterEach
    void afterMethod() {
        Locale.setDefault(cCachedLocale);
    }

    //-----------------------------------------------------------------------
    // Example: using the updated static methods
    //-----------------------------------------------------------------------
    @Test
    void test_asciiDecimalPointGroup3Comma() {
        MoneyAmountStyle style = MoneyAmountStyle.asciiDecimalPointGroup3Comma();
        assertThat(style.getZeroCharacter()).isEqualTo('0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo('+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo('-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo('.');
        assertThat(style.getGroupingCharacter()).isEqualTo(',');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo(3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo(0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_asciiDecimalPointGroup3Comma_print() {
        MoneyAmountStyle style = MoneyAmountStyle.asciiDecimalPointGroup3Comma();
        MoneyFormatter fmt = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(fmt.print(MONEY)).isEqualTo("87,654,321.123,456,78");
    }

    @Test
    void test_asciiDecimalPointGroup3Space() {
        MoneyAmountStyle style = MoneyAmountStyle.asciiDecimalPointGroup3Space();
        assertThat(style.getZeroCharacter()).isEqualTo('0');
        // ... more assertions ...
    }

    @Test
    void test_asciiDecimalPointGroup3Space_print() {
        MoneyAmountStyle style = MoneyAmountStyle.asciiDecimalPointGroup3Space();
        MoneyFormatter fmt = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(fmt.print(MONEY)).isEqualTo("87 654 321.123 456 78");
    }

    @Test
    void test_asciiDecimalPointNoGrouping() {
        MoneyAmountStyle style = MoneyAmountStyle.asciiDecimalPointNoGrouping();
        // ... more assertions ...
    }

    @Test
    void test_asciiDecimalPointNoGrouping_print() {
        MoneyAmountStyle style = MoneyAmountStyle.asciiDecimalPointNoGrouping();
        MoneyFormatter fmt = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(fmt.print(MONEY)).isEqualTo("87654321.12345678");
    }

    @Test
    void test_asciiDecimalCommaGroup3Dot() {
        MoneyAmountStyle style = MoneyAmountStyle.asciiDecimalCommaGroup3Dot();
        // ... more assertions ...
    }

    @Test
    void test_asciiDecimalCommaGroup3Dot_print() {
        MoneyAmountStyle style = MoneyAmountStyle.asciiDecimalCommaGroup3Dot();
        MoneyFormatter fmt = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(fmt.print(MONEY)).isEqualTo("87.654.321,123.456.78");
    }

    // ... and so on for asciiDecimalCommaGroup3Space(), asciiDecimalCommaNoGrouping(),
    //     localizedGrouping(), and localizedNoGrouping() ...

    //-----------------------------------------------------------------------
    // localize(Locale) tests remain the same, but ensure they reference
    // MoneyAmountStyle.localizedGrouping() or .localizedNoGrouping().
    //-----------------------------------------------------------------------
    @Test
    void test_localizedGrouping_print() {
        MoneyAmountStyle style = MoneyAmountStyle.localizedGrouping();
        MoneyFormatter fmt = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(fmt.print(MONEY)).isEqualTo("87,654,321.123,456,78");
    }

    @Test
    void test_localizedNoGrouping_print() {
        MoneyAmountStyle style = MoneyAmountStyle.localizedNoGrouping();
        MoneyFormatter fmt = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(fmt.print(MONEY)).isEqualTo("87654321.12345678");
    }

    // ... etc. ...
}
