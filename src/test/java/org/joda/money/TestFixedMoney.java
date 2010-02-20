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
package org.joda.money;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.testng.annotations.Test;

/**
 * Test FixedMoney.
 */
@Test
public class TestFixedMoney {

    private static final CurrencyUnit GBP = CurrencyUnit.of("GBP");
    private static final CurrencyUnit EUR = CurrencyUnit.of("EUR");
    private static final CurrencyUnit USD = CurrencyUnit.of("USD");
    private static final CurrencyUnit JPY = CurrencyUnit.of("JPY");
    private static final BigDecimal BIGDEC_2_34 = new BigDecimal("2.34");
    private static final BigDecimal BIGDEC_2_345 = new BigDecimal("2.345");
    private static final BigDecimal BIGDEC_M5_78 = new BigDecimal("-5.78");

    private static final FixedMoney GBP_0_00 = FixedMoney.parse("GBP 0.00");
    private static final FixedMoney GBP_1_23 = FixedMoney.parse("GBP 1.23");
    private static final FixedMoney GBP_2_33 = FixedMoney.parse("GBP 2.33");
    private static final FixedMoney GBP_2_34 = FixedMoney.parse("GBP 2.34");
    private static final FixedMoney GBP_2_35 = FixedMoney.parse("GBP 2.35");
    private static final FixedMoney GBP_2_36 = FixedMoney.parse("GBP 2.36");
    private static final FixedMoney GBP_5_78 = FixedMoney.parse("GBP 5.78");
    private static final FixedMoney GBP_M1_23 = FixedMoney.parse("GBP -1.23");
    private static final FixedMoney GBP_M5_78 = FixedMoney.parse("GBP -5.78");
    private static final FixedMoney GBP_INT_MAX_PLUS1 = FixedMoney.ofScale(GBP, ((long) Integer.MAX_VALUE) + 1, 2);
    private static final FixedMoney GBP_INT_MIN_MINUS1 = FixedMoney.ofScale(GBP, ((long) Integer.MIN_VALUE) - 1, 2);
    private static final FixedMoney GBP_INT_MAX_MAJOR_PLUS1 = FixedMoney.ofScale(GBP, (((long) Integer.MAX_VALUE) + 1) * 100, 2);
    private static final FixedMoney GBP_INT_MIN_MAJOR_MINUS1 = FixedMoney.ofScale(GBP, (((long) Integer.MIN_VALUE) - 1) * 100, 2);
    private static final FixedMoney GBP_LONG_MAX_PLUS1 = FixedMoney.of(GBP, BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE), 2);
    private static final FixedMoney GBP_LONG_MIN_MINUS1 =
        FixedMoney.of(GBP, BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE), 2);
    private static final FixedMoney GBP_LONG_MAX_MAJOR_PLUS1 = FixedMoney.of(GBP,
            BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)), 2);
    private static final FixedMoney GBP_LONG_MIN_MAJOR_MINUS1 = FixedMoney.of(GBP,
            BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)), 2);
    private static final FixedMoney JPY_423 = FixedMoney.parse("JPY 423");
    private static final FixedMoney USD_1_23 = FixedMoney.parse("USD 1.23");
    private static final FixedMoney USD_2_34 = FixedMoney.parse("USD 2.34");
    private static final FixedMoney USD_2_35 = FixedMoney.parse("USD 2.35");

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal, int)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency_BigDecimal_int() {
        FixedMoney test = FixedMoney.of(GBP, BIGDEC_2_34, 4);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(23400, 4));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_of_Currency_BigDecimal_invalidScale() {
        FixedMoney.of(GBP, BIGDEC_2_345, 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullCurrency() {
        FixedMoney.of((CurrencyUnit) null, BIGDEC_2_34, 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullBigDecimal() {
        FixedMoney.of(GBP, (BigDecimal) null, 2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_of_Currency_BigDecimal_negativeScale() {
        FixedMoney.of(GBP, BIGDEC_2_34, -2);
    }

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal,int,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency_BigDecimal_int_RoundingMode_DOWN() {
        FixedMoney test = FixedMoney.of(GBP, BIGDEC_2_34, 1, RoundingMode.DOWN);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(23, 1));
    }

    public void test_factory_of_Currency_BigDecimal_int_JPY_RoundingMode_UP() {
        FixedMoney test = FixedMoney.of(JPY, BIGDEC_2_34, 0, RoundingMode.UP);
        assertEquals(test.getCurrencyUnit(), JPY);
        assertEquals(test.getAmount(), BigDecimal.valueOf(3, 0));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_of_Currency_BigDecimal_int_RoundingMode_UNNECESSARY() {
        FixedMoney.of(JPY, BIGDEC_2_34, 1, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_int_RoundingMode_nullCurrency() {
        FixedMoney.of((CurrencyUnit) null, BIGDEC_2_34, 2, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_int_RoundingMode_nullBigDecimal() {
        FixedMoney.of(GBP, (BigDecimal) null, 2, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_of_Currency_BigDecimal_int_RoundingMode_negativeScale() {
        FixedMoney.of(GBP, BIGDEC_2_34, -2, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_int_RoundingMode_nullRoundingMode() {
        FixedMoney.of(GBP, BIGDEC_2_34, 2, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,long, int)
    //-----------------------------------------------------------------------
    public void test_factory_ofScale_Currency_long_int() {
        FixedMoney test = FixedMoney.ofScale(GBP, 234, 4);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(234, 4));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofScale_Currency_long_int_nullCurrency() {
        FixedMoney.ofScale((CurrencyUnit) null, 234, 2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_ofScale_Currency_long_int_negativeScale() {
        FixedMoney.ofScale(GBP, 234, -1);
    }

    //-----------------------------------------------------------------------
    // ofMajor(Currency,long)
    //-----------------------------------------------------------------------
    public void test_factory_ofMajor_Currency_long() {
        FixedMoney test = FixedMoney.ofMajor(GBP, 234);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(234, 0));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofMajor_Currency_long_nullCurrency() {
        FixedMoney.ofMajor((CurrencyUnit) null, 234);
    }

    //-----------------------------------------------------------------------
    // ofMajor(Currency,long, int)
    //-----------------------------------------------------------------------
    public void test_factory_ofMajor_Currency_long_int() {
        FixedMoney test = FixedMoney.ofMajor(GBP, 234, 4);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(2340000, 4));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofMajor_Currency_long_int_nullCurrency() {
        FixedMoney.ofMajor((CurrencyUnit) null, 234, 2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_ofMajor_Currency_long_int_negativeScale() {
        FixedMoney.ofMajor(GBP, 234, -1);
    }

    //-----------------------------------------------------------------------
    // zero(Currency)
    //-----------------------------------------------------------------------
    public void test_factory_zero_Currency() {
        FixedMoney test = FixedMoney.zero(GBP);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(0, 0));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_zero_Currency_nullCurrency() {
        FixedMoney.zero((CurrencyUnit) null);
    }

    //-----------------------------------------------------------------------
    // zero(Currency, int)
    //-----------------------------------------------------------------------
    public void test_factory_zero_Currency_int() {
        FixedMoney test = FixedMoney.zero(GBP, 3);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(0, 3));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_zero_Currency_int_nullCurrency() {
        FixedMoney.zero((CurrencyUnit) null, 3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_zero_Currency_int_negativeScale() {
        FixedMoney.zero(GBP, -1);
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider)
    //-----------------------------------------------------------------------
    public void test_factory_from_BigMoneyProvider() {
        FixedMoney test = FixedMoney.from(BigMoney.parse("GBP 104.235"));
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(104235, 3));
    }

    public void test_factory_from_BigMoneyProvider_fixScale() {
        FixedMoney test = FixedMoney.from(BigMoney.of(GBP, BigDecimal.valueOf(12, -1)));
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(120, 0));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_nullBigMoneyProvider() {
        FixedMoney.from((BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider,int,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_factory_from_BigMoneyProvider_int_RoundingMode() {
        FixedMoney test = FixedMoney.from(BigMoney.parse("GBP 104.235"), 2, RoundingMode.HALF_EVEN);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(10424, 2));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_int_RoundingMode_nullBigMoneyProvider() {
        FixedMoney.from((BigMoneyProvider) null, 2, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_from_BigMoneyProvider_int_RoundingMode_negativeScale() {
        FixedMoney.from(BigMoney.parse("GBP 104.235"), -1, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_int_RoundingMode_nullRoundingMode() {
        FixedMoney.from(BigMoney.parse("GBP 104.235"), 2, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // parse(String)
    //-----------------------------------------------------------------------
    public void test_factory_parse_String_positive() {
        FixedMoney test = FixedMoney.parse("GBP 2.43");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(243, 2));
    }

    public void test_factory_parse_String_negative() {
        FixedMoney test = FixedMoney.parse("GBP -5.87");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(-587, 2));
    }

    public void test_factory_parse_String_scale0() {
        FixedMoney test = FixedMoney.parse("GBP 0");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(0, 0));
    }

    public void test_factory_parse_String_fixScaleNegative() {
        FixedMoney test = FixedMoney.parse("GBP 200");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(200, 0));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_tooShort() {
        FixedMoney.parse("GBP ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_noSpace() {
        FixedMoney.parse("GBP2.34");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_badCurrency() {
        FixedMoney.parse("GBX 2.34");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_parse_String_nullString() {
        FixedMoney.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // nonNull(FixedMoney,CurrencyUnit,int)
    //-----------------------------------------------------------------------
    public void test_nonNull_MoneyCurrencyUnitint_nonNull() {
        FixedMoney test = FixedMoney.nonNull(GBP_1_23, GBP, 2);
        assertSame(test, GBP_1_23);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_nonNull_MoneyCurrencyUnitint_nonNullCurrencyMismatch() {
        FixedMoney.nonNull(GBP_1_23, JPY, 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_nonNull_MoneyCurrencyUnitint_nonNull_nullCurrency() {
        FixedMoney.nonNull(GBP_1_23, null, 2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_nonNull_MoneyCurrencyUnitint_notNull_invalidScale() {
        FixedMoney.nonNull(GBP_1_23, GBP, -1);
    }

    public void test_nonNull_MoneyCurrencyUnitint_null() {
        FixedMoney test = FixedMoney.nonNull(null, GBP, 2);
        assertEquals(test, GBP_0_00);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_nonNull_MoneyCurrencyUnitint_null_nullCurrency() {
        FixedMoney.nonNull(null, null, 2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_nonNull_MoneyCurrencyUnitint_null_invalidScale() {
        FixedMoney.nonNull(null, GBP, -1);
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    public void test_constructor() throws Exception {
        Constructor<FixedMoney> con = FixedMoney.class.getDeclaredConstructor(BigMoney.class);
        assertEquals(Modifier.isPrivate(con.getModifiers()), true);
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] { null });
            fail();
        } catch (InvocationTargetException ex) {
            assertEquals(ex.getCause().getClass(), AssertionError.class);
        }
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        FixedMoney a = GBP_2_34;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(a);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        FixedMoney input = (FixedMoney) ois.readObject();
        assertEquals(input, a);
    }

    //-----------------------------------------------------------------------
    // getCurrencyUnit()
    //-----------------------------------------------------------------------
    public void test_getCurrencyUnit_GBP() {
        assertEquals(GBP_2_34.getCurrencyUnit(), GBP);
    }

    public void test_getCurrencyUnit_EUR() {
        assertEquals(FixedMoney.parse("EUR -5.78").getCurrencyUnit(), EUR);
    }

    //-----------------------------------------------------------------------
    // withCurrencyUnit(Currency)
    //-----------------------------------------------------------------------
    public void test_withCurrencyUnit_Currency() {
        FixedMoney test = GBP_2_34.withCurrencyUnit(USD);
        assertEquals(test.toString(), "USD 2.34");
    }

    public void test_withCurrencyUnit_Currency_same() {
        FixedMoney test = GBP_2_34.withCurrencyUnit(GBP);
        assertSame(test, GBP_2_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withCurrencyUnit_Currency_nullCurrency() {
        GBP_2_34.withCurrencyUnit((CurrencyUnit) null);
    }

    //-----------------------------------------------------------------------
    // getScale()
    //-----------------------------------------------------------------------
    public void test_getScale_GBP() {
        assertEquals(GBP_2_34.getScale(), 2);
    }

    public void test_getScale_JPY() {
        assertEquals(JPY_423.getScale(), 0);
    }

    //-----------------------------------------------------------------------
    // withScale(int)
    //-----------------------------------------------------------------------
    public void test_withScale_int_same() {
        FixedMoney test = GBP_2_34.withScale(2);
        assertSame(test, GBP_2_34);
    }

    public void test_withScale_int_more() {
        FixedMoney test = GBP_2_34.withScale(3);
        assertEquals(test.getAmount(), BigDecimal.valueOf(2340, 3));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_withScale_int_less() {
        FixedMoney.parse("GBP 2.345").withScale(2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_withScale_int_negativeScale() {
        GBP_2_34.withScale(-1);
    }

    //-----------------------------------------------------------------------
    // withScale(int,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_withScale_intRoundingMode_less() {
        FixedMoney test = GBP_2_34.withScale(1, RoundingMode.UP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(24, 1));
        assertEquals(test.getScale(), 1);
    }

    public void test_withScale_intRoundingMode_more() {
        FixedMoney test = GBP_2_34.withScale(3, RoundingMode.UP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(2340, 3));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_withScale_intRoundingMode_negativeScale() {
        GBP_2_34.withScale(-1, RoundingMode.UP);
    }

    //-----------------------------------------------------------------------
    // getAmount()
    //-----------------------------------------------------------------------
    public void test_getAmount_positive() {
        assertEquals(GBP_2_34.getAmount(), BIGDEC_2_34);
    }

    public void test_getAmount_negative() {
        assertEquals(GBP_M5_78.getAmount(), BIGDEC_M5_78);
    }

    //-----------------------------------------------------------------------
    // getAmountMajor()
    //-----------------------------------------------------------------------
    public void test_getAmountMajor_positive() {
        assertEquals(GBP_2_34.getAmountMajor(), BigDecimal.valueOf(2));
    }

    public void test_getAmountMajor_negative() {
        assertEquals(GBP_M5_78.getAmountMajor(), BigDecimal.valueOf(-5));
    }

    //-----------------------------------------------------------------------
    // getAmountMajorLong()
    //-----------------------------------------------------------------------
    public void test_getAmountMajorLong_positive() {
        assertEquals(GBP_2_34.getAmountMajorLong(), 2L);
    }

    public void test_getAmountMajorLong_negative() {
        assertEquals(GBP_M5_78.getAmountMajorLong(), -5L);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMajorLong_tooBigPositive() {
        GBP_LONG_MAX_MAJOR_PLUS1.getAmountMajorLong();
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMajorLong_tooBigNegative() {
        GBP_LONG_MIN_MAJOR_MINUS1.getAmountMajorLong();
    }

    //-----------------------------------------------------------------------
    // getAmountMajorInt()
    //-----------------------------------------------------------------------
    public void test_getAmountMajorInt_positive() {
        assertEquals(GBP_2_34.getAmountMajorInt(), 2);
    }

    public void test_getAmountMajorInt_negative() {
        assertEquals(GBP_M5_78.getAmountMajorInt(), -5);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMajorInt_tooBigPositive() {
        GBP_INT_MAX_MAJOR_PLUS1.getAmountMajorInt();
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMajorInt_tooBigNegative() {
        GBP_INT_MIN_MAJOR_MINUS1.getAmountMajorInt();
    }

    //-----------------------------------------------------------------------
    // getAmountMinor()
    //-----------------------------------------------------------------------
    public void test_getAmountMinor_positive() {
        assertEquals(GBP_2_34.getAmountMinor(), BigDecimal.valueOf(234));
    }

    public void test_getAmountMinor_negative() {
        assertEquals(GBP_M5_78.getAmountMinor(), BigDecimal.valueOf(-578));
    }

    //-----------------------------------------------------------------------
    // getAmountMinorLong()
    //-----------------------------------------------------------------------
    public void test_getAmountMinorLong_positive() {
        assertEquals(GBP_2_34.getAmountMinorLong(), 234L);
    }

    public void test_getAmountMinorLong_negative() {
        assertEquals(GBP_M5_78.getAmountMinorLong(), -578L);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMinorLong_tooBigPositive() {
        GBP_LONG_MAX_PLUS1.getAmountMinorLong();
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMinorLong_tooBigNegative() {
        GBP_LONG_MIN_MINUS1.getAmountMinorLong();
    }

    //-----------------------------------------------------------------------
    // getAmountMinorInt()
    //-----------------------------------------------------------------------
    public void test_getAmountMinorInt_positive() {
        assertEquals(GBP_2_34.getAmountMinorInt(), 234);
    }

    public void test_getAmountMinorInt_negative() {
        assertEquals(GBP_M5_78.getAmountMinorInt(), -578);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMinorInt_tooBigPositive() {
        GBP_INT_MAX_PLUS1.getAmountMinorInt();
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_getAmountMinorInt_tooBigNegative() {
        GBP_INT_MIN_MINUS1.getAmountMinorInt();
    }

    //-----------------------------------------------------------------------
    // getMinorPart()
    //-----------------------------------------------------------------------
    public void test_getMinorPart_positive() {
        assertEquals(GBP_2_34.getMinorPart(), 34);
    }

    public void test_getMinorPart_negative() {
        assertEquals(GBP_M5_78.getMinorPart(), -78);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertEquals(GBP_0_00.isZero(), true);
        assertEquals(GBP_2_34.isZero(), false);
        assertEquals(GBP_M5_78.isZero(), false);
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    public void test_isPositive() {
        assertEquals(GBP_0_00.isPositive(), false);
        assertEquals(GBP_2_34.isPositive(), true);
        assertEquals(GBP_M5_78.isPositive(), false);
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero()
    //-----------------------------------------------------------------------
    public void test_isPositiveOrZero() {
        assertEquals(GBP_0_00.isPositiveOrZero(), true);
        assertEquals(GBP_2_34.isPositiveOrZero(), true);
        assertEquals(GBP_M5_78.isPositiveOrZero(), false);
    }

    //-----------------------------------------------------------------------
    // isNegative()
    //-----------------------------------------------------------------------
    public void test_isNegative() {
        assertEquals(GBP_0_00.isNegative(), false);
        assertEquals(GBP_2_34.isNegative(), false);
        assertEquals(GBP_M5_78.isNegative(), true);
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero()
    //-----------------------------------------------------------------------
    public void test_isNegativeOrZero() {
        assertEquals(GBP_0_00.isNegativeOrZero(), true);
        assertEquals(GBP_2_34.isNegativeOrZero(), false);
        assertEquals(GBP_M5_78.isNegativeOrZero(), true);
    }

    //-----------------------------------------------------------------------
    // plus(Money)
    //-----------------------------------------------------------------------
    public void test_plus_Money_zero() {
        FixedMoney test = GBP_2_34.plus(GBP_0_00);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_Money_positive() {
        FixedMoney test = GBP_2_34.plus(GBP_1_23);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_Money_negative() {
        FixedMoney test = GBP_2_34.plus(GBP_M1_23);
        assertEquals(test.toString(), "GBP 1.11");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_plus_Money_currencyMismatch() {
        GBP_M5_78.plus(USD_1_23);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_Money_nullMoney() {
        GBP_M5_78.plus((FixedMoney) null);
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_plus_BigDecimal_zero() {
        FixedMoney test = GBP_2_34.plus(BigDecimal.ZERO);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_BigDecimal_positive() {
        FixedMoney test = GBP_2_34.plus(new BigDecimal("1.23"));
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_BigDecimal_negative() {
        FixedMoney test = GBP_2_34.plus(new BigDecimal("-1.23"));
        assertEquals(test.toString(), "GBP 1.11");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_BigDecimal_invalidScale() {
        GBP_2_34.plus(new BigDecimal("1.235"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_BigDecimal_nullBigDecimal() {
        GBP_M5_78.plus((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_plus_BigDecimalRoundingMode_zero() {
        FixedMoney test = GBP_2_34.plus(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_BigDecimalRoundingMode_positive() {
        FixedMoney test = GBP_2_34.plus(new BigDecimal("1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_BigDecimalRoundingMode_negative() {
        FixedMoney test = GBP_2_34.plus(new BigDecimal("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_plus_BigDecimalRoundingMode_roundDown() {
        FixedMoney test = GBP_2_34.plus(new BigDecimal("1.235"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_BigDecimalRoundingMode_roundUnecessary() {
        GBP_2_34.plus(new BigDecimal("1.235"), RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_M5_78.plus((BigDecimal) null, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_M5_78.plus(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // plus(double)
    //-----------------------------------------------------------------------
    public void test_plus_double_zero() {
        FixedMoney test = GBP_2_34.plus(0d);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_double_positive() {
        FixedMoney test = GBP_2_34.plus(1.23d);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_double_negative() {
        FixedMoney test = GBP_2_34.plus(-1.23d);
        assertEquals(test.toString(), "GBP 1.11");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_double_invalidScale() {
        GBP_2_34.plus(1.235d);
    }

    //-----------------------------------------------------------------------
    // plus(double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_plus_doubleRoundingMode_zero() {
        FixedMoney test = GBP_2_34.plus(0d, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_doubleRoundingMode_positive() {
        FixedMoney test = GBP_2_34.plus(1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_doubleRoundingMode_negative() {
        FixedMoney test = GBP_2_34.plus(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_plus_doubleRoundingMode_roundDown() {
        FixedMoney test = GBP_2_34.plus(1.235d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plus_doubleRoundingMode_roundUnecessary() {
        GBP_2_34.plus(1.235d, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_doubleRoundingMode_nullRoundingMode() {
        GBP_M5_78.plus(2.34d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // plusMajor(long)
    //-----------------------------------------------------------------------
    public void test_plusMajor_zero() {
        FixedMoney test = GBP_2_34.plusMajor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_plusMajor_positive() {
        FixedMoney test = GBP_2_34.plusMajor(123);
        assertEquals(test.toString(), "GBP 125.34");
    }

    public void test_plusMajor_negative() {
        FixedMoney test = GBP_2_34.plusMajor(-123);
        assertEquals(test.toString(), "GBP -120.66");
    }

    //-----------------------------------------------------------------------
    // minus(Money)
    //-----------------------------------------------------------------------
    public void test_minus_Money_zero() {
        FixedMoney test = GBP_2_34.minus(GBP_0_00);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_Money_positive() {
        FixedMoney test = GBP_2_34.minus(GBP_1_23);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_Money_negative() {
        FixedMoney test = GBP_2_34.minus(GBP_M1_23);
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_minus_Money_currencyMismatch() {
        GBP_M5_78.minus(USD_1_23);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_Money_nullMoney() {
        GBP_M5_78.minus((FixedMoney) null);
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_minus_BigDecimal_zero() {
        FixedMoney test = GBP_2_34.minus(BigDecimal.ZERO);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_BigDecimal_positive() {
        FixedMoney test = GBP_2_34.minus(new BigDecimal("1.23"));
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_BigDecimal_negative() {
        FixedMoney test = GBP_2_34.minus(new BigDecimal("-1.23"));
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_BigDecimal_invalidScale() {
        GBP_2_34.minus(new BigDecimal("1.235"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_BigDecimal_nullBigDecimal() {
        GBP_M5_78.minus((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_minus_BigDecimalRoundingMode_zero() {
        FixedMoney test = GBP_2_34.minus(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_BigDecimalRoundingMode_positive() {
        FixedMoney test = GBP_2_34.minus(new BigDecimal("1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_BigDecimalRoundingMode_negative() {
        FixedMoney test = GBP_2_34.minus(new BigDecimal("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_minus_BigDecimalRoundingMode_roundDown() {
        FixedMoney test = GBP_2_34.minus(new BigDecimal("1.235"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 1.10");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_BigDecimalRoundingMode_roundUnecessary() {
        GBP_2_34.minus(new BigDecimal("1.235"), RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_M5_78.minus((BigDecimal) null, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_M5_78.minus(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // minus(double)
    //-----------------------------------------------------------------------
    public void test_minus_double_zero() {
        FixedMoney test = GBP_2_34.minus(0d);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_double_positive() {
        FixedMoney test = GBP_2_34.minus(1.23d);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_double_negative() {
        FixedMoney test = GBP_2_34.minus(-1.23d);
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_double_invalidScale() {
        GBP_2_34.minus(1.235d);
    }

    //-----------------------------------------------------------------------
    // minus(double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_minus_doubleRoundingMode_zero() {
        FixedMoney test = GBP_2_34.minus(0d, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_doubleRoundingMode_positive() {
        FixedMoney test = GBP_2_34.minus(1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_doubleRoundingMode_negative() {
        FixedMoney test = GBP_2_34.minus(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_minus_doubleRoundingMode_roundDown() {
        FixedMoney test = GBP_2_34.minus(1.235d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 1.10");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minus_doubleRoundingMode_roundUnecessary() {
        GBP_2_34.minus(1.235d, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_doubleRoundingMode_nullRoundingMode() {
        GBP_M5_78.minus(2.34d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // minusMajor(long)
    //-----------------------------------------------------------------------
    public void test_minusMajor_zero() {
        FixedMoney test = GBP_2_34.minusMajor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_minusMajor_positive() {
        FixedMoney test = GBP_2_34.minusMajor(123);
        assertEquals(test.toString(), "GBP -120.66");
    }

    public void test_minusMajor_negative() {
        FixedMoney test = GBP_2_34.minusMajor(-123);
        assertEquals(test.toString(), "GBP 125.34");
    }

    //-----------------------------------------------------------------------
    // multipliedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_BigDecimalRoundingMode_one() {
        FixedMoney test = GBP_2_34.multipliedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_multipliedBy_BigDecimalRoundingMode_positive() {
        FixedMoney test = GBP_2_33.multipliedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 5.82");
    }

    public void test_multipliedBy_BigDecimalRoundingMode_positive_halfUp() {
        FixedMoney test = GBP_2_33.multipliedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 5.83");
    }

    public void test_multipliedBy_BigDecimalRoundingMode_negative() {
        FixedMoney test = GBP_2_33.multipliedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
        assertEquals(test.toString(), "GBP -5.83");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multipliedBy_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_5_78.multipliedBy((BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multipliedBy_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_5_78.multipliedBy(new BigDecimal("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_doubleRoundingMode_one() {
        FixedMoney test = GBP_2_34.multipliedBy(1d, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_multipliedBy_doubleRoundingMode_positive() {
        FixedMoney test = GBP_2_33.multipliedBy(2.5d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 5.82");
    }

    public void test_multipliedBy_doubleRoundingMode_positive_halfUp() {
        FixedMoney test = GBP_2_33.multipliedBy(2.5d, RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 5.83");
    }

    public void test_multipliedBy_doubleRoundingMode_negative() {
        FixedMoney test = GBP_2_33.multipliedBy(-2.5d, RoundingMode.FLOOR);
        assertEquals(test.toString(), "GBP -5.83");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multipliedBy_doubleRoundingMode_nullRoundingMode() {
        GBP_5_78.multipliedBy(2.5d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(long)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_long_one() {
        FixedMoney test = GBP_2_34.multipliedBy(1);
        assertSame(test, GBP_2_34);
    }

    public void test_multipliedBy_long_positive() {
        FixedMoney test = GBP_2_34.multipliedBy(3);
        assertEquals(test.toString(), "GBP 7.02");
    }

    public void test_multipliedBy_long_negative() {
        FixedMoney test = GBP_2_34.multipliedBy(-3);
        assertEquals(test.toString(), "GBP -7.02");
    }

    //-----------------------------------------------------------------------
    // dividedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_dividedBy_BigDecimalRoundingMode_one() {
        FixedMoney test = GBP_2_34.dividedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_dividedBy_BigDecimalRoundingMode_positive() {
        FixedMoney test = GBP_2_34.dividedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.93");
    }

    public void test_dividedBy_BigDecimalRoundingMode_positive_halfUp() {
        FixedMoney test = GBP_2_34.dividedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 0.94");
    }

    public void test_dividedBy_BigDecimalRoundingMode_negative() {
        FixedMoney test = GBP_2_34.dividedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
        assertEquals(test.toString(), "GBP -0.94");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_dividedBy_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_5_78.dividedBy((BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_dividedBy_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_5_78.dividedBy(new BigDecimal("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // dividedBy(double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_dividedBy_doubleRoundingMode_one() {
        FixedMoney test = GBP_2_34.dividedBy(1d, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_dividedBy_doubleRoundingMode_positive() {
        FixedMoney test = GBP_2_34.dividedBy(2.5d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.93");
    }

    public void test_dividedBy_doubleRoundingMode_positive_halfUp() {
        FixedMoney test = GBP_2_34.dividedBy(2.5d, RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 0.94");
    }

    public void test_dividedBy_doubleRoundingMode_negative() {
        FixedMoney test = GBP_2_34.dividedBy(-2.5d, RoundingMode.FLOOR);
        assertEquals(test.toString(), "GBP -0.94");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_dividedBy_doubleRoundingMode_nullRoundingMode() {
        GBP_5_78.dividedBy(2.5d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // dividedBy(long,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_dividedBy_long_one() {
        FixedMoney test = GBP_2_34.dividedBy(1, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_dividedBy_long_positive() {
        FixedMoney test = GBP_2_34.dividedBy(3, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.78");
    }

    public void test_dividedBy_long_positive_roundDown() {
        FixedMoney test = GBP_2_35.dividedBy(3, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.78");
    }

    public void test_dividedBy_long_positive_roundUp() {
        FixedMoney test = GBP_2_35.dividedBy(3, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 0.79");
    }

    public void test_dividedBy_long_negative() {
        FixedMoney test = GBP_2_34.dividedBy(-3, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP -0.78");
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    public void test_negated_positive() {
        FixedMoney test = GBP_2_34.negated();
        assertEquals(test.toString(), "GBP -2.34");
    }

    public void test_negated_negative() {
        FixedMoney test = FixedMoney.parse("GBP -2.34").negated();
        assertEquals(test.toString(), "GBP 2.34");
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    public void test_abs_positive() {
        FixedMoney test = GBP_2_34.abs();
        assertSame(test, GBP_2_34);
    }

    public void test_abs_negative() {
        FixedMoney test = FixedMoney.parse("GBP -2.34").abs();
        assertEquals(test.toString(), "GBP 2.34");
    }

    //-----------------------------------------------------------------------
    // rounded()
    //-----------------------------------------------------------------------
    public void test_round_2down() {
        FixedMoney test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_round_2up() {
        FixedMoney test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_round_1down() {
        FixedMoney test = GBP_2_34.rounded(1, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.30");
    }

    public void test_round_1up() {
        FixedMoney test = GBP_2_34.rounded(1, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 2.40");
    }

    public void test_round_0down() {
        FixedMoney test = GBP_2_34.rounded(0, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.00");
    }

    public void test_round_0up() {
        FixedMoney test = GBP_2_34.rounded(0, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 3.00");
    }

    public void test_round_M1down() {
        FixedMoney test = FixedMoney.parse("GBP 432.34").rounded(-1, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 430.00");
    }

    public void test_round_M1up() {
        FixedMoney test = FixedMoney.parse("GBP 432.34").rounded(-1, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 440.00");
    }

    public void test_round_3() {
        FixedMoney test = GBP_2_34.rounded(3, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    //-----------------------------------------------------------------------
    // convertedTo(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_convertedTo_BigDecimalRoundingMode_positive() {
        FixedMoney test = GBP_2_33.convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "EUR 5.82");
    }

    public void test_convertedTo_BigDecimalRoundingMode_positive_halfUp() {
        FixedMoney test = GBP_2_33.convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "EUR 5.83");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertedTo_BigDecimalRoundingMode_negative() {
        GBP_2_33.convertedTo(EUR, new BigDecimal("-2.5"), RoundingMode.FLOOR);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertedTo_BigDecimalRoundingMode_sameCurrency() {
        GBP_2_33.convertedTo(GBP, new BigDecimal("2.5"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_BigDecimalRoundingMode_nullCurrency() {
        GBP_5_78.convertedTo((CurrencyUnit) null, new BigDecimal("2"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_5_78.convertedTo(EUR, (BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_5_78.convertedTo(EUR, new BigDecimal("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    public void test_toBigMoney() {
        assertEquals(GBP_2_34.toBigMoney(), BigMoney.ofMinor(GBP, 234));
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    public void test_toMoney() {
        assertEquals(GBP_2_34.toMoney(), Money.of(GBP, BIGDEC_2_34));
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    public void test_toMoney_RoundingMode() {
        assertEquals(GBP_2_34.toMoney(RoundingMode.HALF_EVEN), Money.parse("GBP 2.34"));
    }

    public void test_toMoney_RoundingMode_round() {
        BigMoney money = BigMoney.parse("GBP 2.355");
        assertEquals(money.toMoney(RoundingMode.HALF_EVEN), Money.parse("GBP 2.36"));
    }

    //-----------------------------------------------------------------------
    // isSameCurrency(Money)
    //-----------------------------------------------------------------------
    public void test_isSameCurrency_FixedMoney_same() {
        assertEquals(GBP_2_34.isSameCurrency(GBP_2_35), true);
    }

    public void test_isSameCurrency_FixedMoney_different() {
        assertEquals(GBP_2_34.isSameCurrency(USD_2_34), false);
    }

    public void test_isSameCurrency_Money_same() {
        assertEquals(GBP_2_34.isSameCurrency(BigMoney.parse("GBP 2")), true);
    }

    public void test_isSameCurrency_Money_different() {
        assertEquals(GBP_2_34.isSameCurrency(BigMoney.parse("USD 2")), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_isSameCurrency_Money_nullMoney() {
        GBP_2_34.isSameCurrency((FixedMoney) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_FixedMoney() {
        FixedMoney a = GBP_2_34;
        FixedMoney b = GBP_2_35;
        FixedMoney c = GBP_2_36;
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

    public void test_compareTo_Money() {
        FixedMoney t = GBP_2_35;
        BigMoney a = BigMoney.ofMinor(GBP, 234);
        BigMoney b = BigMoney.ofMinor(GBP, 235);
        BigMoney c = BigMoney.ofMinor(GBP, 236);
        assertEquals(t.compareTo(a), 1);
        assertEquals(t.compareTo(b), 0);
        assertEquals(t.compareTo(c), -1);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_compareTo_currenciesDiffer() {
        FixedMoney a = GBP_2_34;
        FixedMoney b = USD_2_35;
        a.compareTo(b);
    }

    @Test(expectedExceptions = ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void test_compareTo_wrongType() {
        Comparable a = GBP_2_34;
        a.compareTo("NotRightType");
    }

    //-----------------------------------------------------------------------
    // isEqual()
    //-----------------------------------------------------------------------
    public void test_isEqual() {
        FixedMoney a = GBP_2_34;
        FixedMoney b = GBP_2_35;
        FixedMoney c = GBP_2_36;
        assertEquals(a.isEqual(a), true);
        assertEquals(b.isEqual(b), true);
        assertEquals(c.isEqual(c), true);
        
        assertEquals(a.isEqual(b), false);
        assertEquals(b.isEqual(a), false);
        
        assertEquals(a.isEqual(c), false);
        assertEquals(c.isEqual(a), false);
        
        assertEquals(b.isEqual(c), false);
        assertEquals(c.isEqual(b), false);
    }

    public void test_isEqual_Money() {
        FixedMoney a = GBP_2_34;
        BigMoney b = BigMoney.ofMinor(GBP, 234);
        assertEquals(a.isEqual(b), true);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_isEqual_currenciesDiffer() {
        FixedMoney a = GBP_2_34;
        FixedMoney b = USD_2_35;
        a.isEqual(b);
    }

    //-----------------------------------------------------------------------
    // isGreaterThan()
    //-----------------------------------------------------------------------
    public void test_isGreaterThan() {
        FixedMoney a = GBP_2_34;
        FixedMoney b = GBP_2_35;
        FixedMoney c = GBP_2_36;
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
        FixedMoney a = GBP_2_34;
        FixedMoney b = USD_2_35;
        a.isGreaterThan(b);
    }

    //-----------------------------------------------------------------------
    // isLessThan()
    //-----------------------------------------------------------------------
    public void test_isLessThan() {
        FixedMoney a = GBP_2_34;
        FixedMoney b = GBP_2_35;
        FixedMoney c = GBP_2_36;
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
        FixedMoney a = GBP_2_34;
        FixedMoney b = USD_2_35;
        a.isLessThan(b);
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    public void test_equals_hashCode_positive() {
        FixedMoney a = GBP_2_34;
        FixedMoney b = GBP_2_34;
        FixedMoney c = GBP_2_35;
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
        FixedMoney a = GBP_2_34;
        assertEquals(a.equals(null), false);
        assertEquals(a.equals("String"), false);
        assertEquals(a.equals(new Object()), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString_positive() {
        FixedMoney test = FixedMoney.of(GBP, BIGDEC_2_34, 2);
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_toString_negative() {
        FixedMoney test = FixedMoney.of(EUR, BIGDEC_M5_78, 2);
        assertEquals(test.toString(), "EUR -5.78");
    }

    public void test_toString_scaled() {
        FixedMoney test = FixedMoney.ofScale(GBP, 1234567, 5);
        assertEquals(test.toString(), "GBP 12.34567");
    }

}
