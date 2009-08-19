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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.testng.annotations.Test;

/**
 * Test Money.
 */
@Test
public class TestMoney {

    private static final Currency GBP = Currency.getInstance("GBP");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency XXX = Currency.getInstance("XXX");
    private static final BigDecimal BIGDEC_2_34 = new BigDecimal("2.34");
    private static final BigDecimal BIGDEC_M5_78 = new BigDecimal("-5.78");

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency_BigDecimal() {
        Money test = Money.of(GBP, BIGDEC_2_34);
        assertEquals(test.getCurrency(), GBP);
        assertEquals(test.getAmountMinor(), 234);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullCurrency() {
        Money.of((Currency) null, BIGDEC_2_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullBigDecimal() {
        Money.of(GBP, (BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // of(String,BigDecimal)
    //-----------------------------------------------------------------------
    public void test_factory_of_String_BigDecimal() {
        Money test = Money.of("GBP", BIGDEC_2_34);
        assertEquals(test.getCurrency(), GBP);
        assertEquals(test.getAmountMinor(), 234);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_of_String_BigDecimal_badCurrency() {
        Money.of("GBX", BIGDEC_2_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_String_BigDecimal_nullCurrency() {
        Money.of((String) null, BIGDEC_2_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_String_BigDecimal_nullBigDecimal() {
        Money.of("GBP", (BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // ofMajor(Currency,long)
    //-----------------------------------------------------------------------
    public void test_factory_ofMajor_Currency_long() {
        Money test = Money.ofMajor(GBP, 234);
        assertEquals(test.getCurrency(), GBP);
        assertEquals(test.getAmountMinor(), 23400);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofMajor_Currency_long_nullCurrency() {
        Money.ofMajor((Currency) null, 234);
    }

    //-----------------------------------------------------------------------
    // ofMajor(String,long)
    //-----------------------------------------------------------------------
    public void test_factory_ofMajor_String_long() {
        Money test = Money.ofMajor("GBP", 234);
        assertEquals(test.getCurrency(), GBP);
        assertEquals(test.getAmountMinor(), 23400);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_ofMajor_String_long_badCurrency() {
        Money.ofMajor("GBX", 234);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofMajor_String_long_nullCurrency() {
        Money.ofMajor((String) null, 234);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_ofMajor_String_long_tooBig() {
        Money.ofMajor("GBP", Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    // zero(Currency)
    //-----------------------------------------------------------------------
    public void test_factory_zero_Currency() {
        Money test = Money.zero(GBP);
        assertEquals(test.getCurrency(), GBP);
        assertEquals(test.getAmountMinor(), 0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_zero_Currency_nullCurrency() {
        Money.zero((Currency) null);
    }

    //-----------------------------------------------------------------------
    // zero(String)
    //-----------------------------------------------------------------------
    public void test_factory_zero_String() {
        Money test = Money.zero("GBP");
        assertEquals(test.getCurrency(), GBP);
        assertEquals(test.getAmountMinor(), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_zero_String_badCurrency() {
        Money.zero("GBX");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_zero_String_nullString() {
        Money.zero((String) null);
    }

    //-----------------------------------------------------------------------
    // parse(String)
    //-----------------------------------------------------------------------
    public void test_factory_parse_String_positive() {
        Money test = Money.parse("GBP 2.34");
        assertEquals(test.getCurrency(), GBP);
        assertEquals(test.getAmountMinor(), 234);
    }

    public void test_factory_parse_String_negative() {
        Money test = Money.parse("GBP -5.78");
        assertEquals(test.getCurrency(), GBP);
        assertEquals(test.getAmountMinor(), -578);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_tooShort() {
        Money.parse("GBP ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_noSpace() {
        Money.parse("GBP2.34");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_badCurrency() {
        Money.parse("GBX 2.34");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_parse_String_nullString() {
        Money.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        Money a = Money.parse("GBP 2.34");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(a);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Money input = (Money) ois.readObject();
        assertEquals(input, a);
    }

    //-----------------------------------------------------------------------
    // getAmount()
    //-----------------------------------------------------------------------
    public void test_getAmount_positive() {
        Money test = Money.parse("GBP 2.34");
        assertEquals(test.getAmount(), BIGDEC_2_34);
    }

    public void test_getAmount_negative() {
        Money test = Money.parse("GBP -5.78");
        assertEquals(test.getAmount(), BIGDEC_M5_78);
    }

    //-----------------------------------------------------------------------
    // getAmountMajor()
    //-----------------------------------------------------------------------
    public void test_getAmountMajor_positive() {
        Money test = Money.parse("GBP 2.34");
        assertEquals(test.getAmountMajor(), 2);
    }

    public void test_getAmountMajor_negative() {
        Money test = Money.parse("GBP -5.78");
        assertEquals(test.getAmountMajor(), -5);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMajor_tooBig() {
        Money test = Money.parse("GBP 12345678912345678912");
        test.getAmountMajor();
    }

    //-----------------------------------------------------------------------
    // getAmountMinor()
    //-----------------------------------------------------------------------
    public void test_getAmountMinor_positive() {
        Money test = Money.parse("GBP 2.34");
        assertEquals(test.getAmountMinor(), 234);
    }

    public void test_getAmountMinor_negative() {
        Money test = Money.parse("GBP -5.78");
        assertEquals(test.getAmountMinor(), -578);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMinor_tooBig() {
        Money test = Money.parse("GBP 123456789123456789");
        test.getAmountMinor();
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertEquals(Money.parse("GBP 0.00").isZero(), true);
        assertEquals(Money.parse("GBP 2.34").isZero(), false);
        assertEquals(Money.parse("GBP -2.34").isZero(), false);
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    public void test_isPositive() {
        assertEquals(Money.parse("GBP 0.00").isPositive(), false);
        assertEquals(Money.parse("GBP 2.34").isPositive(), true);
        assertEquals(Money.parse("GBP -2.34").isPositive(), false);
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero()
    //-----------------------------------------------------------------------
    public void test_isPositiveOrZero() {
        assertEquals(Money.parse("GBP 0.00").isPositiveOrZero(), true);
        assertEquals(Money.parse("GBP 2.34").isPositiveOrZero(), true);
        assertEquals(Money.parse("GBP -2.34").isPositiveOrZero(), false);
    }

    //-----------------------------------------------------------------------
    // isNegative()
    //-----------------------------------------------------------------------
    public void test_isNegative() {
        assertEquals(Money.parse("GBP 0.00").isNegative(), false);
        assertEquals(Money.parse("GBP 2.34").isNegative(), false);
        assertEquals(Money.parse("GBP -2.34").isNegative(), true);
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero()
    //-----------------------------------------------------------------------
    public void test_isNegativeOrZero() {
        assertEquals(Money.parse("GBP 0.00").isNegativeOrZero(), true);
        assertEquals(Money.parse("GBP 2.34").isNegativeOrZero(), false);
        assertEquals(Money.parse("GBP -2.34").isNegativeOrZero(), true);
    }

    //-----------------------------------------------------------------------
    // isSameCurrency(Money)
    //-----------------------------------------------------------------------
    public void test_isSameCurrency_Money_same() {
        Money test = Money.parse("GBP 2.34");
        assertEquals(test.isSameCurrency(Money.parse("GBP 4.56")), true);
    }

    public void test_isSameCurrency_Money_different() {
        Money test = Money.parse("GBP 2.34");
        assertEquals(test.isSameCurrency(Money.parse("EUR 4.56")), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_isSameCurrency_Money_nullMoney() {
        Money.parse("GBP 2.34").isSameCurrency((Money) null);
    }

    //-----------------------------------------------------------------------
    // plus(Money)
    //-----------------------------------------------------------------------
    public void test_plus_Money_positive() {
        Money test = Money.parse("GBP 2.34").plus(Money.parse("GBP 1.23"));
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_Money_negative() {
        Money test = Money.parse("GBP 2.34").plus(Money.parse("GBP -1.23"));
        assertEquals(test.toString(), "GBP 1.11");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_plus_Money_currencyMismatch() {
        Money.parse("GBP -5.78").plus(Money.parse("USD 1.23"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_Money_nullMoney() {
        Money.parse("GBP -5.78").plus((Money) null);
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_plus_BigDecimal_positive() {
        Money test = Money.parse("GBP 2.34").plus(new BigDecimal("1.23"));
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_BigDecimal_negative() {
        Money test = Money.parse("GBP 2.34").plus(new BigDecimal("-1.23"));
        assertEquals(test.toString(), "GBP 1.11");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_BigDecimal_nullBigDecimal() {
        Money.parse("GBP -5.78").plus((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // plusMajor(long)
    //-----------------------------------------------------------------------
    public void test_plusMajor_positive() {
        Money test = Money.parse("GBP 2.34").plusMajor(123);
        assertEquals(test.toString(), "GBP 125.34");
    }

    public void test_plusMajor_negative() {
        Money test = Money.parse("GBP 2.34").plusMajor(-123);
        assertEquals(test.toString(), "GBP -120.66");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plusMajor_overflowBig() {
        Money.ofMinor("GBP", Long.MAX_VALUE).plusMajor(1);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plusMajor_overflowSmall() {
        Money.ofMinor("GBP", Long.MIN_VALUE).plusMajor(-1);
    }

    //-----------------------------------------------------------------------
    // plusMinor(long)
    //-----------------------------------------------------------------------
    public void test_plusMinor_zero() {
        Money test = Money.parse("GBP 2.34").plusMinor(0);
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_plusMinor_positive() {
        Money test = Money.parse("GBP 2.34").plusMinor(123);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plusMinor_negative() {
        Money test = Money.parse("GBP 2.34").plusMinor(-123);
        assertEquals(test.toString(), "GBP 1.11");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plusMinor_overflowBig() {
        Money.ofMinor("GBP", Long.MAX_VALUE).plusMinor(1);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plusMinor_overflowSmall() {
        Money.ofMinor("GBP", Long.MIN_VALUE).plusMinor(-1);
    }

    //-----------------------------------------------------------------------
    // minus(Money)
    //-----------------------------------------------------------------------
    public void test_minus_Money_positive() {
        Money test = Money.parse("GBP 2.34").minus(Money.parse("GBP 1.23"));
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_Money_negative() {
        Money test = Money.parse("GBP 2.34").minus(Money.parse("GBP -1.23"));
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_minus_Money_currencyMismatch() {
        Money.parse("GBP -5.78").minus(Money.parse("USD 1.23"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_Money_nullMoney() {
        Money.parse("GBP -5.78").minus((Money) null);
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_minus_BigDecimal_positive() {
        Money test = Money.parse("GBP 2.34").minus(new BigDecimal("1.23"));
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_BigDecimal_negative() {
        Money test = Money.parse("GBP 2.34").minus(new BigDecimal("-1.23"));
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_BigDecimal_nullBigDecimal() {
        Money.parse("GBP -5.78").minus((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // minusMajor(long)
    //-----------------------------------------------------------------------
    public void test_minusMajor_positive() {
        Money test = Money.parse("GBP 2.34").minusMajor(123);
        assertEquals(test.toString(), "GBP -120.66");
    }

    public void test_minusMajor_negative() {
        Money test = Money.parse("GBP 2.34").minusMajor(-123);
        assertEquals(test.toString(), "GBP 125.34");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minusMajor_overflowBig() {
        Money.ofMinor("GBP", Long.MAX_VALUE).minusMajor(-1);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minusMajor_overflowSmall() {
        Money.ofMinor("GBP", Long.MIN_VALUE).minusMajor(1);
    }

    //-----------------------------------------------------------------------
    // minusMinor(long)
    //-----------------------------------------------------------------------
    public void test_minusMinor_zero() {
        Money test = Money.parse("GBP 2.34").minusMinor(0);
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_minusMinor_positive() {
        Money test = Money.parse("GBP 2.34").minusMinor(123);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minusMinor_negative() {
        Money test = Money.parse("GBP 2.34").minusMinor(-123);
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minusMinor_overflowBig() {
        Money.ofMinor("GBP", Long.MAX_VALUE).minusMinor(-1);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minusMinor_overflowSmall() {
        Money.ofMinor("GBP", Long.MIN_VALUE).minusMinor(1);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_BigDecimal_positive() {
        Money test = Money.parse("GBP 2.33").multipliedBy(new BigDecimal("2.5"));
        assertEquals(test.toString(), "GBP 5.82");
    }

    public void test_multipliedBy_BigDecimal_negative() {
        Money test = Money.parse("GBP 2.33").multipliedBy(new BigDecimal("-2.5"));
        assertEquals(test.toString(), "GBP -5.82");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_BigDecimal_overflowBig() {
        Money.ofMinor("GBP", Long.MAX_VALUE / 2 + 1).multipliedBy(new BigDecimal("2"));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_BigDecimal_overflowSmall() {
        Money.ofMinor("GBP", Long.MIN_VALUE / 2 - 1).multipliedBy(new BigDecimal("2"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multipliedBy_BigDecimal_nullBigDecimal() {
        Money.parse("GBP 5.78").multipliedBy((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_BigDecimalRoundingMode_positive() {
        Money test = Money.parse("GBP 2.33").multipliedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 5.82");
    }

    public void test_multipliedBy_BigDecimalRoundingMode_positive_halfUp() {
        Money test = Money.parse("GBP 2.33").multipliedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 5.83");
    }

    public void test_multipliedBy_BigDecimalRoundingMode_negative() {
        Money test = Money.parse("GBP 2.33").multipliedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
        assertEquals(test.toString(), "GBP -5.83");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_BigDecimalRoundingMode_overflowBig() {
        Money.ofMinor("GBP", Long.MAX_VALUE / 2 + 1).multipliedBy(new BigDecimal("2"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_BigDecimalRoundingMode_overflowSmall() {
        Money.ofMinor("GBP", Long.MIN_VALUE / 2 - 1).multipliedBy(new BigDecimal("2"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multipliedBy_BigDecimalRoundingMode_nullBigDecimal() {
        Money.parse("GBP 5.78").multipliedBy((BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multipliedBy_BigDecimalRoundingMode_nullRoundingMode() {
        Money.parse("GBP 5.78").multipliedBy(new BigDecimal("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(long)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_long_one() {
        Money test = Money.parse("GBP 2.34").multipliedBy(1);
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_multipliedBy_long_positive() {
        Money test = Money.parse("GBP 2.34").multipliedBy(3);
        assertEquals(test.toString(), "GBP 7.02");
    }

    public void test_multipliedBy_long_negative() {
        Money test = Money.parse("GBP 2.34").multipliedBy(-3);
        assertEquals(test.toString(), "GBP -7.02");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_long_overflowBig() {
        Money.ofMinor("GBP", Long.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_multipliedBy_long_overflowSmall() {
        Money.ofMinor("GBP", Long.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    // dividedBy(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_dividedBy_BigDecimal_positive() {
        Money test = Money.parse("GBP 2.34").dividedBy(new BigDecimal("2.5"));
        assertEquals(test.toString(), "GBP 0.93");
    }

    public void test_dividedBy_BigDecimal_negative() {
        Money test = Money.parse("GBP 2.34").dividedBy(new BigDecimal("-2.5"));
        assertEquals(test.toString(), "GBP -0.93");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_dividedBy_BigDecimal_nullBigDecimal() {
        Money.parse("GBP 5.78").dividedBy((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // dividedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_dividedBy_BigDecimalRoundingMode_positive() {
        Money test = Money.parse("GBP 2.34").dividedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.93");
    }

    public void test_dividedBy_BigDecimalRoundingMode_positive_halfUp() {
        Money test = Money.parse("GBP 2.34").dividedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 0.94");
    }

    public void test_dividedBy_BigDecimalRoundingMode_negative() {
        Money test = Money.parse("GBP 2.34").dividedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
        assertEquals(test.toString(), "GBP -0.94");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_dividedBy_BigDecimalRoundingMode_nullBigDecimal() {
        Money.parse("GBP 5.78").dividedBy((BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_dividedBy_BigDecimalRoundingMode_nullRoundingMode() {
        Money.parse("GBP 5.78").dividedBy(new BigDecimal("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // dividedBy(long)
    //-----------------------------------------------------------------------
    public void test_dividedBy_long_one() {
        Money test = Money.parse("GBP 2.34").dividedBy(1);
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_dividedBy_long_positive() {
        Money test = Money.parse("GBP 2.34").dividedBy(3);
        assertEquals(test.toString(), "GBP 0.78");
    }

    public void test_dividedBy_long_positive_roundDown() {
        Money test = Money.parse("GBP 2.35").dividedBy(3);
        assertEquals(test.toString(), "GBP 0.78");
    }

    public void test_dividedBy_long_negative() {
        Money test = Money.parse("GBP 2.34").dividedBy(-3);
        assertEquals(test.toString(), "GBP -0.78");
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    public void test_negated_positive() {
        Money test = Money.parse("GBP 2.34").negated();
        assertEquals(test.toString(), "GBP -2.34");
    }

    public void test_negated_negative() {
        Money test = Money.parse("GBP -2.34").negated();
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_negated_big() {
        Money test = Money.ofMinor("GBP", Long.MAX_VALUE).negated();
        assertEquals(test.getAmountMinor(), -Long.MAX_VALUE);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_negated_overflow() {
        Money.ofMinor("GBP", Long.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    public void test_abs_positive() {
        Money test = Money.parse("GBP 2.34").abs();
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_abs_negative() {
        Money test = Money.parse("GBP -2.34").abs();
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_abs_big() {
        Money test = Money.ofMinor("GBP", Long.MAX_VALUE).abs();
        assertEquals(test.getAmountMinor(), Long.MAX_VALUE);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_abs_overflow() {
        Money.ofMinor("GBP", Long.MIN_VALUE).abs();
    }

    //-----------------------------------------------------------------------
    // rounded()
    //-----------------------------------------------------------------------
    public void test_round_2down() {
        Money test = Money.parse("GBP 2.34").rounded(2, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_round_2up() {
        Money test = Money.parse("GBP 2.34").rounded(2, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_round_1down() {
        Money test = Money.parse("GBP 2.34").rounded(1, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.30");
    }

    public void test_round_1up() {
        Money test = Money.parse("GBP 2.34").rounded(1, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 2.40");
    }

    public void test_round_0down() {
        Money test = Money.parse("GBP 2.34").rounded(0, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.00");
    }

    public void test_round_0up() {
        Money test = Money.parse("GBP 2.34").rounded(0, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 3.00");
    }

    public void test_round_M1down() {
        Money test = Money.parse("GBP 432.34").rounded(-1, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 430.00");
    }

    public void test_round_M1up() {
        Money test = Money.parse("GBP 432.34").rounded(-1, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 440.00");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_round_3() {
        Money.parse("GBP 2.34").rounded(3, RoundingMode.DOWN);  // 3 exceeds scale of 2
    }

    //-----------------------------------------------------------------------
    // convertedTo(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_convertedTo_BigDecimal_positive() {
        Money test = Money.parse("GBP 2.33").convertedTo(EUR, new BigDecimal("2.5"));
        assertEquals(test.toString(), "EUR 5.82");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertedTo_BigDecimal_negative() {
        Money.parse("GBP 2.33").convertedTo(EUR, new BigDecimal("-2.5"));
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertedTo_BigDecimal_sameCurrency() {
        Money.parse("GBP 2.33").convertedTo(GBP, new BigDecimal("2.5"));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_convertedTo_BigDecimal_overflowBig() {
        Money.ofMinor("GBP", Long.MAX_VALUE / 2 + 1).convertedTo(EUR, new BigDecimal("2"));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_convertedTo_BigDecimal_overflowSmall() {
        Money.ofMinor("GBP", Long.MIN_VALUE / 2 - 1).convertedTo(EUR, new BigDecimal("2"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_BigDecimal_nullCurrency() {
        Money.parse("GBP 5.78").convertedTo((Currency) null, new BigDecimal("2"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_BigDecimal_nullBigDecimal() {
        Money.parse("GBP 5.78").convertedTo(EUR, (BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // convertedTo(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_convertedTo_BigDecimalRoundingMode_positive() {
        Money test = Money.parse("GBP 2.33").convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "EUR 5.82");
    }

    public void test_convertedTo_BigDecimalRoundingMode_positive_halfUp() {
        Money test = Money.parse("GBP 2.33").convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "EUR 5.83");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertedTo_BigDecimalRoundingMode_negative() {
        Money.parse("GBP 2.33").convertedTo(EUR, new BigDecimal("-2.5"), RoundingMode.FLOOR);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertedTo_BigDecimalRoundingMode_sameCurrency() {
        Money.parse("GBP 2.33").convertedTo(GBP, new BigDecimal("2.5"));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_convertedTo_BigDecimalRoundingMode_overflowBig() {
        Money.ofMinor("GBP", Long.MAX_VALUE / 2 + 1).convertedTo(EUR, new BigDecimal("2"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_convertedTo_BigDecimalRoundingMode_overflowSmall() {
        Money.ofMinor("GBP", Long.MIN_VALUE / 2 - 1).convertedTo(EUR, new BigDecimal("2"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_BigDecimalRoundingMode_nullCurrency() {
        Money.parse("GBP 5.78").convertedTo((Currency) null, new BigDecimal("2"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_BigDecimalRoundingMode_nullBigDecimal() {
        Money.parse("GBP 5.78").convertedTo(EUR, (BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_BigDecimalRoundingMode_nullRoundingMode() {
        Money.parse("GBP 5.78").convertedTo(EUR, new BigDecimal("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Money a = Money.parse("GBP 2.34");
        Money b = Money.parse("GBP 2.35");
        Money c = Money.parse("GBP 2.36");
        assertEquals(a.compareTo(a), 0);
        assertEquals(b.compareTo(b), 0);
        assertEquals(c.compareTo(c), 0);
        
        assertEquals(a.compareTo(b), -1);
        assertEquals(b.compareTo(a), 1);
        
        assertEquals(a.compareTo(c), -1);
        assertEquals(c.compareTo(a), 1);
        
        assertEquals(b.compareTo(c), -1);
        assertEquals(c.compareTo(b), 1);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_compareTo_currenciesDiffer() {
        Money a = Money.parse("GBP 2.34");
        Money b = Money.parse("USD 2.35");
        a.compareTo(b);
    }

    //-----------------------------------------------------------------------
    // isGreaterThan()
    //-----------------------------------------------------------------------
    public void test_isGreaterThan() {
        Money a = Money.parse("GBP 2.34");
        Money b = Money.parse("GBP 2.35");
        Money c = Money.parse("GBP 2.36");
        assertEquals(a.isGreaterThan(a), false);
        assertEquals(b.isGreaterThan(b), false);
        assertEquals(c.isGreaterThan(c), false);
        
        assertEquals(a.isGreaterThan(b), false);
        assertEquals(b.isGreaterThan(a), true);
        
        assertEquals(a.isGreaterThan(c), false);
        assertEquals(c.isGreaterThan(a), true);
        
        assertEquals(b.isGreaterThan(c), false);
        assertEquals(c.isGreaterThan(b), true);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_isGreaterThan_currenciesDiffer() {
        Money a = Money.parse("GBP 2.34");
        Money b = Money.parse("USD 2.35");
        a.isGreaterThan(b);
    }

    //-----------------------------------------------------------------------
    // isLessThan()
    //-----------------------------------------------------------------------
    public void test_isLessThan() {
        Money a = Money.parse("GBP 2.34");
        Money b = Money.parse("GBP 2.35");
        Money c = Money.parse("GBP 2.36");
        assertEquals(a.isLessThan(a), false);
        assertEquals(b.isLessThan(b), false);
        assertEquals(c.isLessThan(c), false);
        
        assertEquals(a.isLessThan(b), true);
        assertEquals(b.isLessThan(a), false);
        
        assertEquals(a.isLessThan(c), true);
        assertEquals(c.isLessThan(a), false);
        
        assertEquals(b.isLessThan(c), true);
        assertEquals(c.isLessThan(b), false);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_isLessThan_currenciesDiffer() {
        Money a = Money.parse("GBP 2.34");
        Money b = Money.parse("USD 2.35");
        a.isLessThan(b);
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    public void test_equals_hashCode_positive() {
        Money a = Money.parse("GBP 2.34");
        Money b = Money.parse("GBP 2.34");
        Money c = Money.parse("GBP 2.35");
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
        assertEquals(c.equals(c), true);
        
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        
        assertEquals(a.equals(c), false);
        assertEquals(b.equals(c), false);
    }

    public void test_equals_false() {
        Money a = Money.parse("GBP 2.34");
        assertEquals(a.equals(null), false);
        assertEquals(a.equals("String"), false);
        assertEquals(a.equals(new Object()), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString_positive() {
        Money test = Money.of(GBP, BIGDEC_2_34);
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_toString_negative() {
        Money test = Money.of(EUR, BIGDEC_M5_78);
        assertEquals(test.toString(), "EUR -5.78");
    }

}
