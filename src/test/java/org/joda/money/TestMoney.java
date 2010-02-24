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
import java.util.Arrays;
import java.util.Collections;

import org.testng.annotations.Test;

/**
 * Test Money.
 */
@Test
public class TestMoney {

    private static final CurrencyUnit GBP = CurrencyUnit.of("GBP");
    private static final CurrencyUnit EUR = CurrencyUnit.of("EUR");
    private static final CurrencyUnit USD = CurrencyUnit.of("USD");
    private static final CurrencyUnit JPY = CurrencyUnit.of("JPY");
    private static final BigDecimal BIGDEC_2_3 = new BigDecimal("2.3");
    private static final BigDecimal BIGDEC_2_34 = new BigDecimal("2.34");
    private static final BigDecimal BIGDEC_2_345 = new BigDecimal("2.345");
    private static final BigDecimal BIGDEC_M5_78 = new BigDecimal("-5.78");

    private static final Money GBP_0_00 = Money.parse("GBP 0.00");
    private static final Money GBP_1_23 = Money.parse("GBP 1.23");
    private static final Money GBP_2_33 = Money.parse("GBP 2.33");
    private static final Money GBP_2_34 = Money.parse("GBP 2.34");
    private static final Money GBP_2_35 = Money.parse("GBP 2.35");
    private static final Money GBP_2_36 = Money.parse("GBP 2.36");
    private static final Money GBP_5_78 = Money.parse("GBP 5.78");
    private static final Money GBP_M1_23 = Money.parse("GBP -1.23");
    private static final Money GBP_M5_78 = Money.parse("GBP -5.78");
    private static final Money GBP_INT_MAX_PLUS1 = Money.ofMinor(GBP, ((long) Integer.MAX_VALUE) + 1);
    private static final Money GBP_INT_MIN_MINUS1 = Money.ofMinor(GBP, ((long) Integer.MIN_VALUE) - 1);
    private static final Money GBP_INT_MAX_MAJOR_PLUS1 = Money.ofMinor(GBP, (((long) Integer.MAX_VALUE) + 1) * 100);
    private static final Money GBP_INT_MIN_MAJOR_MINUS1 = Money.ofMinor(GBP, (((long) Integer.MIN_VALUE) - 1) * 100);
    private static final Money GBP_LONG_MAX_PLUS1 = Money.of(GBP, BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE));
    private static final Money GBP_LONG_MIN_MINUS1 =
        Money.of(GBP, BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE));
    private static final Money GBP_LONG_MAX_MAJOR_PLUS1 = Money.of(GBP,
            BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
    private static final Money GBP_LONG_MIN_MAJOR_MINUS1 = Money.of(GBP,
            BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
    private static final Money JPY_423 = Money.parse("JPY 423");
    private static final Money USD_1_23 = Money.parse("USD 1.23");
    private static final Money USD_2_34 = Money.parse("USD 2.34");
    private static final Money USD_2_35 = Money.parse("USD 2.35");

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency_BigDecimal() {
        Money test = Money.of(GBP, BIGDEC_2_34);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 234);
        assertEquals(test.getAmount().scale(), 2);
    }

    public void test_factory_of_Currency_BigDecimal_correctScale() {
        Money test = Money.of(GBP, BIGDEC_2_3);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 230);
        assertEquals(test.getAmount().scale(), 2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_of_Currency_BigDecimal_invalidScaleGBP() {
        Money.of(GBP, BIGDEC_2_345);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_of_Currency_BigDecimal_invalidScaleJPY() {
        Money.of(JPY, BIGDEC_2_3);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullCurrency() {
        Money.of((CurrencyUnit) null, BIGDEC_2_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullBigDecimal() {
        Money.of(GBP, (BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency_BigDecimal_GBP_RoundingMode_DOWN() {
        Money test = Money.of(GBP, BIGDEC_2_34, RoundingMode.DOWN);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 234);
        assertEquals(test.getAmount().scale(), 2);
    }

    public void test_factory_of_Currency_BigDecimal_JPY_RoundingMode_DOWN() {
        Money test = Money.of(JPY, BIGDEC_2_34, RoundingMode.DOWN);
        assertEquals(test.getCurrencyUnit(), JPY);
        assertEquals(test.getAmountMinorInt(), 2);
        assertEquals(test.getAmount().scale(), 0);
    }

    public void test_factory_of_Currency_BigDecimal_JPY_RoundingMode_UP() {
        Money test = Money.of(JPY, BIGDEC_2_34, RoundingMode.UP);
        assertEquals(test.getCurrencyUnit(), JPY);
        assertEquals(test.getAmountMinorInt(), 3);
        assertEquals(test.getAmount().scale(), 0);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_of_Currency_BigDecimal_RoundingMode_UNNECESSARY() {
        Money.of(JPY, BIGDEC_2_34, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_RoundingMode_nullCurrency() {
        Money.of((CurrencyUnit) null, BIGDEC_2_34, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_RoundingMode_nullBigDecimal() {
        Money.of(GBP, (BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_RoundingMode_nullRoundingMode() {
        Money.of(GBP, BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // of(Currency,double)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency_double() {
        Money test = Money.of(GBP, 2.34d);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 234);
        assertEquals(test.getAmount().scale(), 2);
    }

    public void test_factory_of_Currency_double_correctScale() {
        Money test = Money.of(GBP, 2.3d);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 230);
        assertEquals(test.getAmount().scale(), 2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_of_Currency_double_invalidScaleGBP() {
        Money.of(GBP, 2.345d);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_of_Currency_double_invalidScaleJPY() {
        Money.of(JPY, 2.3d);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_double_nullCurrency() {
        Money.of((CurrencyUnit) null, BIGDEC_2_34);
    }

    //-----------------------------------------------------------------------
    // of(Currency,double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency_double_GBP_RoundingMode_DOWN() {
        Money test = Money.of(GBP, 2.34d, RoundingMode.DOWN);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 234);
        assertEquals(test.getAmount().scale(), 2);
    }

    public void test_factory_of_Currency_double_JPY_RoundingMode_DOWN() {
        Money test = Money.of(JPY, 2.34d, RoundingMode.DOWN);
        assertEquals(test.getCurrencyUnit(), JPY);
        assertEquals(test.getAmountMinorInt(), 2);
        assertEquals(test.getAmount().scale(), 0);
    }

    public void test_factory_of_Currency_double_JPY_RoundingMode_UP() {
        Money test = Money.of(JPY, 2.34d, RoundingMode.UP);
        assertEquals(test.getCurrencyUnit(), JPY);
        assertEquals(test.getAmountMinorInt(), 3);
        assertEquals(test.getAmount().scale(), 0);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_of_Currency_double_RoundingMode_UNNECESSARY() {
        Money.of(JPY, 2.34d, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_double_RoundingMode_nullCurrency() {
        Money.of((CurrencyUnit) null, 2.34d, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_double_RoundingMode_nullRoundingMode() {
        Money.of(GBP, 2.34d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // ofMajor(Currency,long)
    //-----------------------------------------------------------------------
    public void test_factory_ofMajor_Currency_long() {
        Money test = Money.ofMajor(GBP, 234);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 23400);
        assertEquals(test.getAmount().scale(), 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofMajor_Currency_long_nullCurrency() {
        Money.ofMajor((CurrencyUnit) null, 234);
    }

    //-----------------------------------------------------------------------
    // ofMinor(Currency,long)
    //-----------------------------------------------------------------------
    public void test_factory_ofMinor_Currency_long() {
        Money test = Money.ofMinor(GBP, 234);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 234);
        assertEquals(test.getAmount().scale(), 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofMinor_Currency_long_nullCurrency() {
        Money.ofMinor((CurrencyUnit) null, 234);
    }

    //-----------------------------------------------------------------------
    // zero(Currency)
    //-----------------------------------------------------------------------
    public void test_factory_zero_Currency() {
        Money test = Money.zero(GBP);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 0);
        assertEquals(test.getAmount().scale(), 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_zero_Currency_nullCurrency() {
        Money.zero((CurrencyUnit) null);
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider)
    //-----------------------------------------------------------------------
    public void test_factory_from_BigMoneyProvider() {
        Money test = Money.from(BigMoney.parse("GBP 104.23"));
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 10423);
        assertEquals(test.getAmount().scale(), 2);
    }

    public void test_factory_from_BigMoneyProvider_fixScale() {
        Money test = Money.from(BigMoney.parse("GBP 104.2"));
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 10420);
        assertEquals(test.getAmount().scale(), 2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_from_BigMoneyProvider_invalidCurrencyScale() {
        Money.from(BigMoney.parse("GBP 104.235"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_nullBigMoneyProvider() {
        Money.from((BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_factory_from_BigMoneyProvider_RoundingMode() {
        Money test = Money.from(BigMoney.parse("GBP 104.235"), RoundingMode.HALF_EVEN);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 10424);
        assertEquals(test.getAmount().scale(), 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_RoundingMode_nullBigMoneyProvider() {
        Money.from((BigMoneyProvider) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_RoundingMode_nullRoundingMode() {
        Money.from(BigMoney.parse("GBP 104.235"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // total(Money...)
    //-----------------------------------------------------------------------
    public void test_factory_total_varargs_1() {
        Money test = Money.total(GBP_1_23);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 123);
    }

    public void test_factory_total_array_1() {
        Money[] array = new Money[] {GBP_1_23};
        Money test = Money.total(array);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 123);
    }

    public void test_factory_total_varargs_3() {
        Money test = Money.total(GBP_1_23, GBP_2_33, GBP_2_36);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 592);
    }

    public void test_factory_total_array_3() {
        Money[] array = new Money[] {GBP_1_23, GBP_2_33, GBP_2_36};
        Money test = Money.total(array);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 592);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_total_varargs_empty() {
        Money.total();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_total_array_empty() {
        Money[] array = new Money[0];
        Money.total(array);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_varargs_currenciesDiffer() {
        Money.total(GBP_2_33, JPY_423);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_array_currenciesDiffer() {
        Money[] array = new Money[] {GBP_2_33, JPY_423};
        Money.total(array);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_varargs_nullFirst() {
        Money.total((Money) null, GBP_2_33, GBP_2_36);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_array_nullFirst() {
        Money[] array = new Money[] {null, GBP_2_33, GBP_2_36};
        Money.total(array);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_varargs_nullNotFirst() {
        Money.total(GBP_2_33, null, GBP_2_36);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_array_nullNotFirst() {
        Money[] array = new Money[] {GBP_2_33, null, GBP_2_36};
        Money.total(array);
    }

    //-----------------------------------------------------------------------
    // total(Iterable)
    //-----------------------------------------------------------------------
    public void test_factory_total_Iterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_1_23, GBP_2_33, GBP_2_36);
        Money test = Money.total(iterable);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 592);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_total_Iterable_empty() {
        Iterable<Money> iterable = Collections.emptyList();
        Money.total(iterable);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_Iterable_currenciesDiffer() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
        Money.total(iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_Iterable_nullFirst() {
        Iterable<Money> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        Money.total(iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_Iterable_nullNotFirst() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        Money.total(iterable);
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,Money...)
    //-----------------------------------------------------------------------
    public void test_factory_total_CurrencyUnitVarargs_1() {
        Money test = Money.total(GBP, GBP_1_23);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 123);
    }

    public void test_factory_total_CurrencyUnitArray_1() {
        Money[] array = new Money[] {GBP_1_23};
        Money test = Money.total(GBP, array);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 123);
    }

    public void test_factory_total_CurrencyUnitVarargs_3() {
        Money test = Money.total(GBP, GBP_1_23, GBP_2_33, GBP_2_36);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 592);
    }

    public void test_factory_total_CurrencyUnitArray_3() {
        Money[] array = new Money[] {GBP_1_23, GBP_2_33, GBP_2_36};
        Money test = Money.total(GBP, array);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 592);
    }

    public void test_factory_total_CurrencyUnitVarargs_empty() {
        Money test = Money.total(GBP);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 0);
    }

    public void test_factory_total_CurrencyUnitArray_empty() {
        Money[] array = new Money[0];
        Money test = Money.total(GBP, array);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 0);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_CurrencyUnitVarargs_currenciesDiffer() {
        Money.total(GBP, JPY_423);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_CurrencyUnitArray_currenciesDiffer() {
        Money[] array = new Money[] {JPY_423};
        Money.total(GBP, array);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_CurrencyUnitVarargs_currenciesDifferInArray() {
        Money.total(GBP, GBP_2_33, JPY_423);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_CurrencyUnitArray_currenciesDifferInArray() {
        Money[] array = new Money[] {GBP_2_33, JPY_423};
        Money.total(GBP, array);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_CurrencyUnitVarargs_nullFirst() {
        Money.total(GBP, null, GBP_2_33, GBP_2_36);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_CurrencyUnitArray_nullFirst() {
        Money[] array = new Money[] {null, GBP_2_33, GBP_2_36};
        Money.total(GBP, array);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_CurrencyUnitVarargs_nullNotFirst() {
        Money.total(GBP, GBP_2_33, null, GBP_2_36);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_CurrencyUnitArray_nullNotFirst() {
        Money[] array = new Money[] {GBP_2_33, null, GBP_2_36};
        Money.total(GBP, array);
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,Iterable)
    //-----------------------------------------------------------------------
    public void test_factory_total_CurrencyUnitIterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_1_23, GBP_2_33, GBP_2_36);
        Money test = Money.total(GBP, iterable);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 592);
    }

    public void test_factory_total_CurrencyUnitIterable_empty() {
        Iterable<Money> iterable = Collections.emptyList();
        Money test = Money.total(GBP, iterable);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 0);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_CurrencyUnitIterable_currenciesDiffer() {
        Iterable<Money> iterable = Arrays.asList(JPY_423);
        Money.total(GBP, iterable);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_CurrencyUnitIterable_currenciesDifferInIterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
        Money.total(GBP, iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_CurrencyUnitIterable_nullFirst() {
        Iterable<Money> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        Money.total(GBP, iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_CurrencyUnitIterable_nullNotFirst() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        Money.total(GBP, iterable);
    }

    //-----------------------------------------------------------------------
    // parse(String)
    //-----------------------------------------------------------------------
    public void test_factory_parse_String_positive() {
        Money test = Money.parse("GBP 2.43");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 243);
    }

    public void test_factory_parse_String_negative() {
        Money test = Money.parse("GBP -5.87");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), -587);
    }

    public void test_factory_parse_String_fixScale_0() {
        Money test = Money.parse("GBP 0");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 0);
        assertEquals(test.getAmount().scale(), 2);
    }

    public void test_factory_parse_String_fixScale_2() {
        Money test = Money.parse("GBP 2");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 200);
        assertEquals(test.getAmount().scale(), 2);
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
    // nonNull(Money,CurrencyUnit)
    //-----------------------------------------------------------------------
    public void test_nonNull_MoneyCurrencyUnit_nonNull() {
        Money test = Money.nonNull(GBP_1_23, GBP);
        assertSame(test, GBP_1_23);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_nonNull_MoneyCurrencyUnit_nonNullCurrencyMismatch() {
        Money.nonNull(GBP_1_23, JPY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_nonNull_MoneyCurrencyUnit_nonNull_nullCurrency() {
        Money.nonNull(GBP_1_23, null);
    }

    public void test_nonNull_MoneyCurrencyUnit_null() {
        Money test = Money.nonNull(null, GBP);
        assertEquals(test, GBP_0_00);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_nonNull_MoneyCurrencyUnit_null_nullCurrency() {
        Money.nonNull(null, null);
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    public void test_constructor_null1() throws Exception {
        Constructor<Money> con = Money.class.getDeclaredConstructor(BigMoney.class);
        assertEquals(Modifier.isPublic(con.getModifiers()), false);
        assertEquals(Modifier.isProtected(con.getModifiers()), false);
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] { null });
            fail();
        } catch (InvocationTargetException ex) {
            assertEquals(ex.getCause().getClass(), AssertionError.class);
        }
    }

    public void test_constructor_scale() throws Exception {
        Constructor<Money> con = Money.class.getDeclaredConstructor(BigMoney.class);
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] { BigMoney.of(GBP, BIGDEC_2_3) });
            fail();
        } catch (InvocationTargetException ex) {
            assertEquals(ex.getCause().getClass(), AssertionError.class);
        }
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        Money a = GBP_2_34;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(a);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Money input = (Money) ois.readObject();
        assertEquals(input, a);
    }

    //-----------------------------------------------------------------------
    // getCurrencyUnit()
    //-----------------------------------------------------------------------
    public void test_getCurrencyUnit_GBP() {
        assertEquals(GBP_2_34.getCurrencyUnit(), GBP);
    }

    public void test_getCurrencyUnit_EUR() {
        assertEquals(Money.parse("EUR -5.78").getCurrencyUnit(), EUR);
    }

    //-----------------------------------------------------------------------
    // withCurrencyUnit(Currency)
    //-----------------------------------------------------------------------
    public void test_withCurrencyUnit_Currency() {
        Money test = GBP_2_34.withCurrencyUnit(USD);
        assertEquals(test.toString(), "USD 2.34");
    }

    public void test_withCurrencyUnit_Currency_same() {
        Money test = GBP_2_34.withCurrencyUnit(GBP);
        assertSame(test, GBP_2_34);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_withCurrencyUnit_Currency_scaleProblem() {
        GBP_2_34.withCurrencyUnit(JPY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withCurrencyUnit_Currency_nullCurrency() {
        GBP_2_34.withCurrencyUnit((CurrencyUnit) null);
    }

    //-----------------------------------------------------------------------
    // withCurrencyUnit(Currency,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_withCurrencyUnit_CurrencyRoundingMode_DOWN() {
        Money test = GBP_2_34.withCurrencyUnit(JPY, RoundingMode.DOWN);
        assertEquals(test.toString(), "JPY 2");
    }

    public void test_withCurrencyUnit_CurrencyRoundingMode_UP() {
        Money test = GBP_2_34.withCurrencyUnit(JPY, RoundingMode.UP);
        assertEquals(test.toString(), "JPY 3");
    }

    public void test_withCurrencyUnit_CurrencyRoundingMode_same() {
        Money test = GBP_2_34.withCurrencyUnit(GBP, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_withCurrencyUnit_CurrencyRoundingMode_UNECESSARY() {
        GBP_2_34.withCurrencyUnit(JPY, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withCurrencyUnit_CurrencyRoundingMode_nullCurrency() {
        GBP_2_34.withCurrencyUnit((CurrencyUnit) null, RoundingMode.UNNECESSARY);
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
    // withAmount(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_withAmount_BigDecimal() {
        Money test = GBP_2_34.withAmount(BIGDEC_M5_78);
        assertEquals(test.toString(), "GBP -5.78");
    }

    public void test_withAmount_BigDecimal_same() {
        Money test = GBP_2_34.withAmount(BIGDEC_2_34);
        assertSame(test, GBP_2_34);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_withAmount_BigDecimal_invalidScale() {
        GBP_2_34.withAmount(new BigDecimal("2.345"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withAmount_BigDecimal_nullBigDecimal() {
        GBP_2_34.withAmount((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // withAmount(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_withAmount_BigDecimalRoundingMode() {
        Money test = GBP_2_34.withAmount(BIGDEC_M5_78, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP -5.78");
    }

    public void test_withAmount_BigDecimalRoundingMode_same() {
        Money test = GBP_2_34.withAmount(BIGDEC_2_34, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_withAmount_BigDecimalRoundingMode_roundDown() {
        Money test = GBP_2_34.withAmount(new BigDecimal("2.355"), RoundingMode.DOWN);
        assertEquals(test, GBP_2_35);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_withAmount_BigDecimalRoundingMode_roundUnecessary() {
        GBP_2_34.withAmount(new BigDecimal("2.345"), RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withAmount_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_2_34.withAmount((BigDecimal) null, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withAmount_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_2_34.withAmount(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // withAmount(double)
    //-----------------------------------------------------------------------
    public void test_withAmount_double() {
        Money test = GBP_2_34.withAmount(-5.78d);
        assertEquals(test.toString(), "GBP -5.78");
    }

    public void test_withAmount_double_same() {
        Money test = GBP_2_34.withAmount(2.34d);
        assertSame(test, GBP_2_34);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_withAmount_double_invalidScale() {
        GBP_2_34.withAmount(2.345d);
    }

    //-----------------------------------------------------------------------
    // withAmount(double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_withAmount_doubleRoundingMode() {
        Money test = GBP_2_34.withAmount(-5.78d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP -5.78");
    }

    public void test_withAmount_doubleRoundingMode_same() {
        Money test = GBP_2_34.withAmount(2.34d, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_withAmount_doubleRoundingMode_roundDown() {
        Money test = GBP_2_34.withAmount(2.355d, RoundingMode.DOWN);
        assertEquals(test, GBP_2_35);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_withAmount_doubleRoundingMode_roundUnecessary() {
        GBP_2_34.withAmount(2.345d, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withAmount_doubleRoundingMode_nullRoundingMode() {
        GBP_2_34.withAmount(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // plus(Iterable)
    //-----------------------------------------------------------------------
    public void test_plus_Iterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, GBP_1_23);
        Money test = GBP_2_34.plus(iterable);
        assertEquals(test.toString(), "GBP 5.90");
    }

    public void test_plus_Iterable_zero() {
        Iterable<Money> iterable = Arrays.asList(GBP_0_00);
        Money test = GBP_2_34.plus(iterable);
        assertSame(test, GBP_2_34);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_plus_Iterable_currencyMismatch() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
        GBP_M5_78.plus(iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_Iterable_nullEntry() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null);
        GBP_M5_78.plus(iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_Iterable_nullIterable() {
        GBP_M5_78.plus((Iterable<Money>) null);
    }

    //-----------------------------------------------------------------------
    // plus(Money)
    //-----------------------------------------------------------------------
    public void test_plus_Money_zero() {
        Money test = GBP_2_34.plus(GBP_0_00);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_Money_positive() {
        Money test = GBP_2_34.plus(GBP_1_23);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_Money_negative() {
        Money test = GBP_2_34.plus(GBP_M1_23);
        assertEquals(test.toString(), "GBP 1.11");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_plus_Money_currencyMismatch() {
        GBP_M5_78.plus(USD_1_23);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_Money_nullMoney() {
        GBP_M5_78.plus((Money) null);
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_plus_BigDecimal_zero() {
        Money test = GBP_2_34.plus(BigDecimal.ZERO);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_BigDecimal_positive() {
        Money test = GBP_2_34.plus(new BigDecimal("1.23"));
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_BigDecimal_negative() {
        Money test = GBP_2_34.plus(new BigDecimal("-1.23"));
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
        Money test = GBP_2_34.plus(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_34.plus(new BigDecimal("1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_BigDecimalRoundingMode_negative() {
        Money test = GBP_2_34.plus(new BigDecimal("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_plus_BigDecimalRoundingMode_roundDown() {
        Money test = GBP_2_34.plus(new BigDecimal("1.235"), RoundingMode.DOWN);
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
        Money test = GBP_2_34.plus(0d);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_double_positive() {
        Money test = GBP_2_34.plus(1.23d);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_double_negative() {
        Money test = GBP_2_34.plus(-1.23d);
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
        Money test = GBP_2_34.plus(0d, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_doubleRoundingMode_positive() {
        Money test = GBP_2_34.plus(1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plus_doubleRoundingMode_negative() {
        Money test = GBP_2_34.plus(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_plus_doubleRoundingMode_roundDown() {
        Money test = GBP_2_34.plus(1.235d, RoundingMode.DOWN);
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
        Money test = GBP_2_34.plusMajor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_plusMajor_positive() {
        Money test = GBP_2_34.plusMajor(123);
        assertEquals(test.toString(), "GBP 125.34");
    }

    public void test_plusMajor_negative() {
        Money test = GBP_2_34.plusMajor(-123);
        assertEquals(test.toString(), "GBP -120.66");
    }

    //-----------------------------------------------------------------------
    // plusMinor(long)
    //-----------------------------------------------------------------------
    public void test_plusMinor_zero() {
        Money test = GBP_2_34.plusMinor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_plusMinor_positive() {
        Money test = GBP_2_34.plusMinor(123);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plusMinor_negative() {
        Money test = GBP_2_34.plusMinor(-123);
        assertEquals(test.toString(), "GBP 1.11");
    }

    //-----------------------------------------------------------------------
    // minus(Iterable)
    //-----------------------------------------------------------------------
    public void test_minus_Iterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, GBP_1_23);
        Money test = GBP_2_34.minus(iterable);
        assertEquals(test.toString(), "GBP -1.22");
    }

    public void test_minus_Iterable_zero() {
        Iterable<Money> iterable = Arrays.asList(GBP_0_00);
        Money test = GBP_2_34.minus(iterable);
        assertSame(test, GBP_2_34);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_minus_Iterable_currencyMismatch() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
        GBP_M5_78.minus(iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_Iterable_nullEntry() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null);
        GBP_M5_78.minus(iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_Iterable_nullIterable() {
        GBP_M5_78.minus((Iterable<Money>) null);
    }

    //-----------------------------------------------------------------------
    // minus(Money)
    //-----------------------------------------------------------------------
    public void test_minus_Money_zero() {
        Money test = GBP_2_34.minus(GBP_0_00);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_Money_positive() {
        Money test = GBP_2_34.minus(GBP_1_23);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_Money_negative() {
        Money test = GBP_2_34.minus(GBP_M1_23);
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_minus_Money_currencyMismatch() {
        GBP_M5_78.minus(USD_1_23);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_Money_nullMoney() {
        GBP_M5_78.minus((Money) null);
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_minus_BigDecimal_zero() {
        Money test = GBP_2_34.minus(BigDecimal.ZERO);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_BigDecimal_positive() {
        Money test = GBP_2_34.minus(new BigDecimal("1.23"));
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_BigDecimal_negative() {
        Money test = GBP_2_34.minus(new BigDecimal("-1.23"));
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
        Money test = GBP_2_34.minus(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_34.minus(new BigDecimal("1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_BigDecimalRoundingMode_negative() {
        Money test = GBP_2_34.minus(new BigDecimal("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_minus_BigDecimalRoundingMode_roundDown() {
        Money test = GBP_2_34.minus(new BigDecimal("1.235"), RoundingMode.DOWN);
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
        Money test = GBP_2_34.minus(0d);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_double_positive() {
        Money test = GBP_2_34.minus(1.23d);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_double_negative() {
        Money test = GBP_2_34.minus(-1.23d);
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
        Money test = GBP_2_34.minus(0d, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_doubleRoundingMode_positive() {
        Money test = GBP_2_34.minus(1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minus_doubleRoundingMode_negative() {
        Money test = GBP_2_34.minus(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_minus_doubleRoundingMode_roundDown() {
        Money test = GBP_2_34.minus(1.235d, RoundingMode.DOWN);
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
        Money test = GBP_2_34.minusMajor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_minusMajor_positive() {
        Money test = GBP_2_34.minusMajor(123);
        assertEquals(test.toString(), "GBP -120.66");
    }

    public void test_minusMajor_negative() {
        Money test = GBP_2_34.minusMajor(-123);
        assertEquals(test.toString(), "GBP 125.34");
    }

    //-----------------------------------------------------------------------
    // minusMinor(long)
    //-----------------------------------------------------------------------
    public void test_minusMinor_zero() {
        Money test = GBP_2_34.minusMinor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_minusMinor_positive() {
        Money test = GBP_2_34.minusMinor(123);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minusMinor_negative() {
        Money test = GBP_2_34.minusMinor(-123);
        assertEquals(test.toString(), "GBP 3.57");
    }

    //-----------------------------------------------------------------------
    // multipliedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_BigDecimalRoundingMode_one() {
        Money test = GBP_2_34.multipliedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_multipliedBy_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_33.multipliedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 5.82");
    }

    public void test_multipliedBy_BigDecimalRoundingMode_positive_halfUp() {
        Money test = GBP_2_33.multipliedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 5.83");
    }

    public void test_multipliedBy_BigDecimalRoundingMode_negative() {
        Money test = GBP_2_33.multipliedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
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
        Money test = GBP_2_34.multipliedBy(1d, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_multipliedBy_doubleRoundingMode_positive() {
        Money test = GBP_2_33.multipliedBy(2.5d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 5.82");
    }

    public void test_multipliedBy_doubleRoundingMode_positive_halfUp() {
        Money test = GBP_2_33.multipliedBy(2.5d, RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 5.83");
    }

    public void test_multipliedBy_doubleRoundingMode_negative() {
        Money test = GBP_2_33.multipliedBy(-2.5d, RoundingMode.FLOOR);
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
        Money test = GBP_2_34.multipliedBy(1);
        assertSame(test, GBP_2_34);
    }

    public void test_multipliedBy_long_positive() {
        Money test = GBP_2_34.multipliedBy(3);
        assertEquals(test.toString(), "GBP 7.02");
    }

    public void test_multipliedBy_long_negative() {
        Money test = GBP_2_34.multipliedBy(-3);
        assertEquals(test.toString(), "GBP -7.02");
    }

    //-----------------------------------------------------------------------
    // dividedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_dividedBy_BigDecimalRoundingMode_one() {
        Money test = GBP_2_34.dividedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_dividedBy_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_34.dividedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.93");
    }

    public void test_dividedBy_BigDecimalRoundingMode_positive_halfUp() {
        Money test = GBP_2_34.dividedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 0.94");
    }

    public void test_dividedBy_BigDecimalRoundingMode_negative() {
        Money test = GBP_2_34.dividedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
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
        Money test = GBP_2_34.dividedBy(1d, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_dividedBy_doubleRoundingMode_positive() {
        Money test = GBP_2_34.dividedBy(2.5d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.93");
    }

    public void test_dividedBy_doubleRoundingMode_positive_halfUp() {
        Money test = GBP_2_34.dividedBy(2.5d, RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 0.94");
    }

    public void test_dividedBy_doubleRoundingMode_negative() {
        Money test = GBP_2_34.dividedBy(-2.5d, RoundingMode.FLOOR);
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
        Money test = GBP_2_34.dividedBy(1, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_dividedBy_long_positive() {
        Money test = GBP_2_34.dividedBy(3, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.78");
    }

    public void test_dividedBy_long_positive_roundDown() {
        Money test = GBP_2_35.dividedBy(3, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.78");
    }

    public void test_dividedBy_long_positive_roundUp() {
        Money test = GBP_2_35.dividedBy(3, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 0.79");
    }

    public void test_dividedBy_long_negative() {
        Money test = GBP_2_34.dividedBy(-3, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP -0.78");
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    public void test_negated_positive() {
        Money test = GBP_2_34.negated();
        assertEquals(test.toString(), "GBP -2.34");
    }

    public void test_negated_negative() {
        Money test = Money.parse("GBP -2.34").negated();
        assertEquals(test.toString(), "GBP 2.34");
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    public void test_abs_positive() {
        Money test = GBP_2_34.abs();
        assertSame(test, GBP_2_34);
    }

    public void test_abs_negative() {
        Money test = Money.parse("GBP -2.34").abs();
        assertEquals(test.toString(), "GBP 2.34");
    }

    //-----------------------------------------------------------------------
    // rounded()
    //-----------------------------------------------------------------------
    public void test_round_2down() {
        Money test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_round_2up() {
        Money test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_round_1down() {
        Money test = GBP_2_34.rounded(1, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.30");
    }

    public void test_round_1up() {
        Money test = GBP_2_34.rounded(1, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 2.40");
    }

    public void test_round_0down() {
        Money test = GBP_2_34.rounded(0, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.00");
    }

    public void test_round_0up() {
        Money test = GBP_2_34.rounded(0, RoundingMode.UP);
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

    public void test_round_3() {
        Money test = GBP_2_34.rounded(3, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    //-----------------------------------------------------------------------
    // convertedTo(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_convertedTo_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_33.convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "EUR 5.82");
    }

    public void test_convertedTo_BigDecimalRoundingMode_positive_halfUp() {
        Money test = GBP_2_33.convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.HALF_UP);
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

//    //-----------------------------------------------------------------------
//    // toFixedMoney()
//    //-----------------------------------------------------------------------
//    public void test_toFixedMoney() {
//        assertEquals(GBP_2_34.toFixedMoney(), FixedMoney.of(GBP, BIGDEC_2_34, 2));
//    }

    //-----------------------------------------------------------------------
    // isSameCurrency(Money)
    //-----------------------------------------------------------------------
    public void test_isSameCurrency_Money_same() {
        assertEquals(GBP_2_34.isSameCurrency(GBP_2_35), true);
    }

    public void test_isSameCurrency_Money_different() {
        assertEquals(GBP_2_34.isSameCurrency(USD_2_34), false);
    }

    public void test_isSameCurrency_BigMoney_same() {
        assertEquals(GBP_2_34.isSameCurrency(BigMoney.parse("GBP 2")), true);
    }

    public void test_isSameCurrency_BigMoney_different() {
        assertEquals(GBP_2_34.isSameCurrency(BigMoney.parse("USD 2")), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_isSameCurrency_Money_nullMoney() {
        GBP_2_34.isSameCurrency((Money) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_Money() {
        Money a = GBP_2_34;
        Money b = GBP_2_35;
        Money c = GBP_2_36;
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

    public void test_compareTo_BigMoney() {
        Money t = GBP_2_35;
        BigMoney a = BigMoney.ofMinor(GBP, 234);
        BigMoney b = BigMoney.ofMinor(GBP, 235);
        BigMoney c = BigMoney.ofMinor(GBP, 236);
        assertEquals(t.compareTo(a), 1);
        assertEquals(t.compareTo(b), 0);
        assertEquals(t.compareTo(c), -1);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_compareTo_currenciesDiffer() {
        Money a = GBP_2_34;
        Money b = USD_2_35;
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
        Money a = GBP_2_34;
        Money b = GBP_2_35;
        Money c = GBP_2_36;
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
        Money a = GBP_2_34;
        BigMoney b = BigMoney.ofMinor(GBP, 234);
        assertEquals(a.isEqual(b), true);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_isEqual_currenciesDiffer() {
        Money a = GBP_2_34;
        Money b = USD_2_35;
        a.isEqual(b);
    }

    //-----------------------------------------------------------------------
    // isGreaterThan()
    //-----------------------------------------------------------------------
    public void test_isGreaterThan() {
        Money a = GBP_2_34;
        Money b = GBP_2_35;
        Money c = GBP_2_36;
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
        Money a = GBP_2_34;
        Money b = USD_2_35;
        a.isGreaterThan(b);
    }

    //-----------------------------------------------------------------------
    // isLessThan()
    //-----------------------------------------------------------------------
    public void test_isLessThan() {
        Money a = GBP_2_34;
        Money b = GBP_2_35;
        Money c = GBP_2_36;
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
        Money a = GBP_2_34;
        Money b = USD_2_35;
        a.isLessThan(b);
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    public void test_equals_hashCode_positive() {
        Money a = GBP_2_34;
        Money b = GBP_2_34;
        Money c = GBP_2_35;
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
        Money a = GBP_2_34;
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
