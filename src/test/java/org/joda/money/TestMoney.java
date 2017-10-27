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
package org.joda.money;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

/**
 * Test Money.
 */
@RunWith(DataProviderRunner.class)
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
    @Test
    public void test_factory_of_Currency_BigDecimal() {
        Money test = Money.of(GBP, BIGDEC_2_34);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(234, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test
    public void test_factory_of_Currency_BigDecimal_correctScale() {
        Money test = Money.of(GBP, BIGDEC_2_3);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(230, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test(expected = ArithmeticException.class)
    public void test_factory_of_Currency_BigDecimal_invalidScaleGBP() {
        Money.of(GBP, BIGDEC_2_345);
    }

    @Test(expected = ArithmeticException.class)
    public void test_factory_of_Currency_BigDecimal_invalidScaleJPY() {
        Money.of(JPY, BIGDEC_2_3);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullCurrency() {
        Money.of((CurrencyUnit) null, BIGDEC_2_34);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullBigDecimal() {
        Money.of(GBP, (BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_of_Currency_BigDecimal_GBP_RoundingMode_DOWN() {
        Money test = Money.of(GBP, BIGDEC_2_34, RoundingMode.DOWN);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(234, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test
    public void test_factory_of_Currency_BigDecimal_JPY_RoundingMode_DOWN() {
        Money test = Money.of(JPY, BIGDEC_2_34, RoundingMode.DOWN);
        assertEquals(JPY, test.getCurrencyUnit());
        assertEquals(2, test.getAmountMinorInt());
        assertEquals(0, test.getAmount().scale());
    }

    @Test
    public void test_factory_of_Currency_BigDecimal_JPY_RoundingMode_UP() {
        Money test = Money.of(JPY, BIGDEC_2_34, RoundingMode.UP);
        assertEquals(JPY, test.getCurrencyUnit());
        assertEquals(3, test.getAmountMinorInt());
        assertEquals(0, test.getAmount().scale());
    }

    @Test(expected = ArithmeticException.class)
    public void test_factory_of_Currency_BigDecimal_RoundingMode_UNNECESSARY() {
        Money.of(JPY, BIGDEC_2_34, RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_RoundingMode_nullCurrency() {
        Money.of((CurrencyUnit) null, BIGDEC_2_34, RoundingMode.DOWN);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_RoundingMode_nullBigDecimal() {
        Money.of(GBP, (BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_RoundingMode_nullRoundingMode() {
        Money.of(GBP, BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // of(Currency,double)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_of_Currency_double() {
        Money test = Money.of(GBP, 2.34d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(234, test.getAmountMinorInt());
        assertEquals(2, test.getScale());
    }

    @Test
    public void test_factory_of_Currency_double_correctScale() {
        Money test = Money.of(GBP, 2.3d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(230, test.getAmountMinorInt());
        assertEquals(2, test.getScale());
    }

    @Test
    public void test_factory_of_Currency_double_trailingZero1() {
        Money test = Money.of(GBP, 1.230d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(123L, 2), test.getAmount());
        assertEquals(2, test.getScale());
    }

    @Test
    public void test_factory_of_Currency_double_trailingZero2() {
        Money test = Money.of(GBP, 1.20d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(120L, 2), test.getAmount());
        assertEquals(2, test.getScale());
    }

    @Test
    public void test_factory_of_Currency_double_medium() {
        Money test = Money.of(GBP, 2000d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(200000L, 2), test.getAmount());
        assertEquals(2, test.getScale());
    }

    @Test
    public void test_factory_of_Currency_double_big() {
        Money test = Money.of(GBP, 200000000d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(20000000000L, 2), test.getAmount());
        assertEquals(2, test.getScale());
    }

    @Test(expected = ArithmeticException.class)
    public void test_factory_of_Currency_double_invalidScaleGBP() {
        Money.of(GBP, 2.345d);
    }

    @Test(expected = ArithmeticException.class)
    public void test_factory_of_Currency_double_invalidScaleJPY() {
        Money.of(JPY, 2.3d);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_of_Currency_double_nullCurrency() {
        Money.of((CurrencyUnit) null, BIGDEC_2_34);
    }

    //-----------------------------------------------------------------------
    // of(Currency,double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_of_Currency_double_GBP_RoundingMode_DOWN() {
        Money test = Money.of(GBP, 2.34d, RoundingMode.DOWN);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(234, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test
    public void test_factory_of_Currency_double_JPY_RoundingMode_DOWN() {
        Money test = Money.of(JPY, 2.34d, RoundingMode.DOWN);
        assertEquals(JPY, test.getCurrencyUnit());
        assertEquals(2, test.getAmountMinorInt());
        assertEquals(0, test.getAmount().scale());
    }

    @Test
    public void test_factory_of_Currency_double_JPY_RoundingMode_UP() {
        Money test = Money.of(JPY, 2.34d, RoundingMode.UP);
        assertEquals(JPY, test.getCurrencyUnit());
        assertEquals(3, test.getAmountMinorInt());
        assertEquals(0, test.getAmount().scale());
    }

    @Test(expected = ArithmeticException.class)
    public void test_factory_of_Currency_double_RoundingMode_UNNECESSARY() {
        Money.of(JPY, 2.34d, RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_of_Currency_double_RoundingMode_nullCurrency() {
        Money.of((CurrencyUnit) null, 2.34d, RoundingMode.DOWN);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_of_Currency_double_RoundingMode_nullRoundingMode() {
        Money.of(GBP, 2.34d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // ofMajor(Currency,long)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_ofMajor_Currency_long() {
        Money test = Money.ofMajor(GBP, 234);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(23400, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_ofMajor_Currency_long_nullCurrency() {
        Money.ofMajor((CurrencyUnit) null, 234);
    }

    //-----------------------------------------------------------------------
    // ofMinor(Currency,long)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_ofMinor_Currency_long() {
        Money test = Money.ofMinor(GBP, 234);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(234, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_ofMinor_Currency_long_nullCurrency() {
        Money.ofMinor((CurrencyUnit) null, 234);
    }

    //-----------------------------------------------------------------------
    // zero(Currency)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_zero_Currency() {
        Money test = Money.zero(GBP);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(0, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_zero_Currency_nullCurrency() {
        Money.zero((CurrencyUnit) null);
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_from_BigMoneyProvider() {
        Money test = Money.of(BigMoney.parse("GBP 104.23"));
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(10423, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test
    public void test_factory_from_BigMoneyProvider_fixScale() {
        Money test = Money.of(BigMoney.parse("GBP 104.2"));
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(10420, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test(expected = ArithmeticException.class)
    public void test_factory_from_BigMoneyProvider_invalidCurrencyScale() {
        Money.of(BigMoney.parse("GBP 104.235"));
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_nullBigMoneyProvider() {
        Money.of((BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_from_BigMoneyProvider_RoundingMode() {
        Money test = Money.of(BigMoney.parse("GBP 104.235"), RoundingMode.HALF_EVEN);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(10424, test.getAmountMinorInt());
        assertEquals(2, test.getAmount().scale());
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_RoundingMode_nullBigMoneyProvider() {
        Money.of((BigMoneyProvider) null, RoundingMode.DOWN);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_RoundingMode_nullRoundingMode() {
        Money.of(BigMoney.parse("GBP 104.235"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // total(Money...)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_total_varargs_1() {
        Money test = Money.total(GBP_1_23);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(123, test.getAmountMinorInt());
    }

    @Test
    public void test_factory_total_array_1() {
        Money[] array = new Money[] {GBP_1_23};
        Money test = Money.total(array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(123, test.getAmountMinorInt());
    }

    @Test
    public void test_factory_total_varargs_3() {
        Money test = Money.total(GBP_1_23, GBP_2_33, GBP_2_36);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    public void test_factory_total_array_3() {
        Money[] array = new Money[] {GBP_1_23, GBP_2_33, GBP_2_36};
        Money test = Money.total(array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_factory_total_varargs_empty() {
        Money.total();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_factory_total_array_empty() {
        Money[] array = new Money[0];
        Money.total(array);
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_factory_total_varargs_currenciesDiffer() {
        try {
            Money.total(GBP_2_33, JPY_423);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_factory_total_array_currenciesDiffer() {
        try {
            Money[] array = new Money[] {GBP_2_33, JPY_423};
            Money.total(array);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_varargs_nullFirst() {
        Money.total((Money) null, GBP_2_33, GBP_2_36);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_array_nullFirst() {
        Money[] array = new Money[] {null, GBP_2_33, GBP_2_36};
        Money.total(array);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_varargs_nullNotFirst() {
        Money.total(GBP_2_33, null, GBP_2_36);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_array_nullNotFirst() {
        Money[] array = new Money[] {GBP_2_33, null, GBP_2_36};
        Money.total(array);
    }

    //-----------------------------------------------------------------------
    // total(Iterable)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_total_Iterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_1_23, GBP_2_33, GBP_2_36);
        Money test = Money.total(iterable);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_factory_total_Iterable_empty() {
        Iterable<Money> iterable = Collections.emptyList();
        Money.total(iterable);
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_factory_total_Iterable_currenciesDiffer() {
        try {
            Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
            Money.total(iterable);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_Iterable_nullFirst() {
        Iterable<Money> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        Money.total(iterable);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_Iterable_nullNotFirst() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        Money.total(iterable);
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,Money...)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_total_CurrencyUnitVarargs_1() {
        Money test = Money.total(GBP, GBP_1_23);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(123, test.getAmountMinorInt());
    }

    @Test
    public void test_factory_total_CurrencyUnitArray_1() {
        Money[] array = new Money[] {GBP_1_23};
        Money test = Money.total(GBP, array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(123, test.getAmountMinorInt());
    }

    @Test
    public void test_factory_total_CurrencyUnitVarargs_3() {
        Money test = Money.total(GBP, GBP_1_23, GBP_2_33, GBP_2_36);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    public void test_factory_total_CurrencyUnitArray_3() {
        Money[] array = new Money[] {GBP_1_23, GBP_2_33, GBP_2_36};
        Money test = Money.total(GBP, array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    public void test_factory_total_CurrencyUnitVarargs_empty() {
        Money test = Money.total(GBP);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(0, test.getAmountMinorInt());
    }

    @Test
    public void test_factory_total_CurrencyUnitArray_empty() {
        Money[] array = new Money[0];
        Money test = Money.total(GBP, array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(0, test.getAmountMinorInt());
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_factory_total_CurrencyUnitVarargs_currenciesDiffer() {
        try {
            Money.total(GBP, JPY_423);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_factory_total_CurrencyUnitArray_currenciesDiffer() {
        try {
            Money[] array = new Money[] {JPY_423};
            Money.total(GBP, array);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_factory_total_CurrencyUnitVarargs_currenciesDifferInArray() {
        try {
            Money.total(GBP, GBP_2_33, JPY_423);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_factory_total_CurrencyUnitArray_currenciesDifferInArray() {
        try {
            Money[] array = new Money[] {GBP_2_33, JPY_423};
            Money.total(GBP, array);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_CurrencyUnitVarargs_nullFirst() {
        Money.total(GBP, null, GBP_2_33, GBP_2_36);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_CurrencyUnitArray_nullFirst() {
        Money[] array = new Money[] {null, GBP_2_33, GBP_2_36};
        Money.total(GBP, array);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_CurrencyUnitVarargs_nullNotFirst() {
        Money.total(GBP, GBP_2_33, null, GBP_2_36);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_CurrencyUnitArray_nullNotFirst() {
        Money[] array = new Money[] {GBP_2_33, null, GBP_2_36};
        Money.total(GBP, array);
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,Iterable)
    //-----------------------------------------------------------------------
    @Test
    public void test_factory_total_CurrencyUnitIterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_1_23, GBP_2_33, GBP_2_36);
        Money test = Money.total(GBP, iterable);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    public void test_factory_total_CurrencyUnitIterable_empty() {
        Iterable<Money> iterable = Collections.emptyList();
        Money test = Money.total(GBP, iterable);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(0, test.getAmountMinorInt());
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_factory_total_CurrencyUnitIterable_currenciesDiffer() {
        try {
            Iterable<Money> iterable = Arrays.asList(JPY_423);
            Money.total(GBP, iterable);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_factory_total_CurrencyUnitIterable_currenciesDifferInIterable() {
        try {
            Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
            Money.total(GBP, iterable);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_CurrencyUnitIterable_nullFirst() {
        Iterable<Money> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        Money.total(GBP, iterable);
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_total_CurrencyUnitIterable_nullNotFirst() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        Money.total(GBP, iterable);
    }

    //-----------------------------------------------------------------------
    // parse(String)
    //-----------------------------------------------------------------------
    @DataProvider
    public static Object[][] data_parse() {
        return new Object[][] {
            {"GBP 2.43", GBP, 243},
            {"GBP +12.57", GBP, 1257},
            {"GBP -5.87", GBP, -587},
            {"GBP 0.99", GBP, 99},
            {"GBP .99", GBP, 99},
            {"GBP +.99", GBP, 99},
            {"GBP +0.99", GBP, 99},
            {"GBP -.99", GBP, -99},
            {"GBP -0.99", GBP, -99},
            {"GBP 0", GBP, 0},
            {"GBP 2", GBP, 200},
            {"GBP 123.", GBP, 12300},
            {"GBP3", GBP, 300},
            {"GBP3.10", GBP, 310},
            {"GBP  3.10", GBP, 310},
            {"GBP   3.10", GBP, 310},
            {"GBP                           3.10", GBP, 310},
        };
    }

    @Test
    @UseDataProvider("data_parse")
    public void test_factory_parse(String str, CurrencyUnit currency, int amount) {
        Money test = Money.parse(str);
        assertEquals(currency, test.getCurrencyUnit());
        assertEquals(amount, test.getAmountMinorInt());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_factory_parse_String_tooShort() {
        Money.parse("GBP ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_factory_parse_String_badCurrency() {
        Money.parse("GBX 2.34");
    }

    @Test(expected = NullPointerException.class)
    public void test_factory_parse_String_nullString() {
        Money.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // nonNull(Money,CurrencyUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_nonNull_MoneyCurrencyUnit_nonNull() {
        Money test = Money.nonNull(GBP_1_23, GBP);
        assertSame(GBP_1_23, test);
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_nonNull_MoneyCurrencyUnit_nonNullCurrencyMismatch() {
        try {
            Money.nonNull(GBP_1_23, JPY);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_nonNull_MoneyCurrencyUnit_nonNull_nullCurrency() {
        Money.nonNull(GBP_1_23, null);
    }

    @Test
    public void test_nonNull_MoneyCurrencyUnit_null() {
        Money test = Money.nonNull(null, GBP);
        assertEquals(GBP_0_00, test);
    }

    @Test(expected = NullPointerException.class)
    public void test_nonNull_MoneyCurrencyUnit_null_nullCurrency() {
        Money.nonNull(null, null);
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    @Test
    public void test_constructor_null1() throws Exception {
        Constructor<Money> con = Money.class.getDeclaredConstructor(BigMoney.class);
        assertEquals(false, Modifier.isPublic(con.getModifiers()));
        assertEquals(false, Modifier.isProtected(con.getModifiers()));
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] { null });
            fail();
        } catch (InvocationTargetException ex) {
            assertEquals(AssertionError.class, ex.getCause().getClass());
        }
    }

    @Test
    public void test_constructor_scale() throws Exception {
        Constructor<Money> con = Money.class.getDeclaredConstructor(BigMoney.class);
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] { BigMoney.of(GBP, BIGDEC_2_3) });
            fail();
        } catch (InvocationTargetException ex) {
            assertEquals(AssertionError.class, ex.getCause().getClass());
        }
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test
    public void test_serialization() throws Exception {
        Money a = GBP_2_34;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(a);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Money input = (Money) ois.readObject();
        assertEquals(a, input);
    }

    @Test(expected = InvalidObjectException.class)
    public void test_serialization_invalidNumericCode() throws Exception {
        CurrencyUnit cu = new CurrencyUnit("GBP", (short) 234, (short) 2);
        Money m = Money.of(cu, 123.43d);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(m);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        ois.readObject();
    }

    @Test(expected = InvalidObjectException.class)
    public void test_serialization_invalidDecimalPlaces() throws Exception {
        CurrencyUnit cu = new CurrencyUnit("GBP", (short) 826, (short) 3);
        Money m = Money.of(cu, 123.43d);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(m);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        ois.readObject();
    }

    //-----------------------------------------------------------------------
    // getCurrencyUnit()
    //-----------------------------------------------------------------------
    @Test
    public void test_getCurrencyUnit_GBP() {
        assertEquals(GBP, GBP_2_34.getCurrencyUnit());
    }

    @Test
    public void test_getCurrencyUnit_EUR() {
        assertEquals(EUR, Money.parse("EUR -5.78").getCurrencyUnit());
    }

    //-----------------------------------------------------------------------
    // withCurrencyUnit(Currency)
    //-----------------------------------------------------------------------
    @Test
    public void test_withCurrencyUnit_Currency() {
        Money test = GBP_2_34.withCurrencyUnit(USD);
        assertEquals("USD 2.34", test.toString());
    }

    @Test
    public void test_withCurrencyUnit_Currency_same() {
        Money test = GBP_2_34.withCurrencyUnit(GBP);
        assertSame(GBP_2_34, test);
    }

    @Test(expected = ArithmeticException.class)
    public void test_withCurrencyUnit_Currency_scaleProblem() {
        GBP_2_34.withCurrencyUnit(JPY);
    }

    @Test(expected = NullPointerException.class)
    public void test_withCurrencyUnit_Currency_nullCurrency() {
        GBP_2_34.withCurrencyUnit((CurrencyUnit) null);
    }

    //-----------------------------------------------------------------------
    // withCurrencyUnit(Currency,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_withCurrencyUnit_CurrencyRoundingMode_DOWN() {
        Money test = GBP_2_34.withCurrencyUnit(JPY, RoundingMode.DOWN);
        assertEquals("JPY 2", test.toString());
    }

    @Test
    public void test_withCurrencyUnit_CurrencyRoundingMode_UP() {
        Money test = GBP_2_34.withCurrencyUnit(JPY, RoundingMode.UP);
        assertEquals("JPY 3", test.toString());
    }

    @Test
    public void test_withCurrencyUnit_CurrencyRoundingMode_same() {
        Money test = GBP_2_34.withCurrencyUnit(GBP, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test(expected = ArithmeticException.class)
    public void test_withCurrencyUnit_CurrencyRoundingMode_UNECESSARY() {
        GBP_2_34.withCurrencyUnit(JPY, RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_withCurrencyUnit_CurrencyRoundingMode_nullCurrency() {
        GBP_2_34.withCurrencyUnit((CurrencyUnit) null, RoundingMode.UNNECESSARY);
    }

    //-----------------------------------------------------------------------
    // getScale()
    //-----------------------------------------------------------------------
    @Test
    public void test_getScale_GBP() {
        assertEquals(2, GBP_2_34.getScale());
    }

    @Test
    public void test_getScale_JPY() {
        assertEquals(0, JPY_423.getScale());
    }

    //-----------------------------------------------------------------------
    // getAmount()
    //-----------------------------------------------------------------------
    @Test
    public void test_getAmount_positive() {
        assertEquals(BIGDEC_2_34, GBP_2_34.getAmount());
    }

    @Test
    public void test_getAmount_negative() {
        assertEquals(BIGDEC_M5_78, GBP_M5_78.getAmount());
    }

    //-----------------------------------------------------------------------
    // getAmountMajor()
    //-----------------------------------------------------------------------
    @Test
    public void test_getAmountMajor_positive() {
        assertEquals(BigDecimal.valueOf(2), GBP_2_34.getAmountMajor());
    }

    @Test
    public void test_getAmountMajor_negative() {
        assertEquals(BigDecimal.valueOf(-5), GBP_M5_78.getAmountMajor());
    }

    //-----------------------------------------------------------------------
    // getAmountMajorLong()
    //-----------------------------------------------------------------------
    @Test
    public void test_getAmountMajorLong_positive() {
        assertEquals(2L, GBP_2_34.getAmountMajorLong());
    }

    @Test
    public void test_getAmountMajorLong_negative() {
        assertEquals(-5L, GBP_M5_78.getAmountMajorLong());
    }

    @Test(expected = ArithmeticException.class)
    public void test_getAmountMajorLong_tooBigPositive() {
        GBP_LONG_MAX_MAJOR_PLUS1.getAmountMajorLong();
    }

    @Test(expected = ArithmeticException.class)
    public void test_getAmountMajorLong_tooBigNegative() {
        GBP_LONG_MIN_MAJOR_MINUS1.getAmountMajorLong();
    }

    //-----------------------------------------------------------------------
    // getAmountMajorInt()
    //-----------------------------------------------------------------------
    @Test
    public void test_getAmountMajorInt_positive() {
        assertEquals(2, GBP_2_34.getAmountMajorInt());
    }

    @Test
    public void test_getAmountMajorInt_negative() {
        assertEquals(-5, GBP_M5_78.getAmountMajorInt());
    }

    @Test(expected = ArithmeticException.class)
    public void test_getAmountMajorInt_tooBigPositive() {
        GBP_INT_MAX_MAJOR_PLUS1.getAmountMajorInt();
    }

    @Test(expected = ArithmeticException.class)
    public void test_getAmountMajorInt_tooBigNegative() {
        GBP_INT_MIN_MAJOR_MINUS1.getAmountMajorInt();
    }

    //-----------------------------------------------------------------------
    // getAmountMinor()
    //-----------------------------------------------------------------------
    @Test
    public void test_getAmountMinor_positive() {
        assertEquals(BigDecimal.valueOf(234), GBP_2_34.getAmountMinor());
    }

    @Test
    public void test_getAmountMinor_negative() {
        assertEquals(BigDecimal.valueOf(-578), GBP_M5_78.getAmountMinor());
    }

    //-----------------------------------------------------------------------
    // getAmountMinorLong()
    //-----------------------------------------------------------------------
    @Test
    public void test_getAmountMinorLong_positive() {
        assertEquals(234L, GBP_2_34.getAmountMinorLong());
    }

    @Test
    public void test_getAmountMinorLong_negative() {
        assertEquals(-578L, GBP_M5_78.getAmountMinorLong());
    }

    @Test(expected = ArithmeticException.class)
    public void test_getAmountMinorLong_tooBigPositive() {
        GBP_LONG_MAX_PLUS1.getAmountMinorLong();
    }

    @Test(expected = ArithmeticException.class)
    public void test_getAmountMinorLong_tooBigNegative() {
        GBP_LONG_MIN_MINUS1.getAmountMinorLong();
    }

    //-----------------------------------------------------------------------
    // getAmountMinorInt()
    //-----------------------------------------------------------------------
    @Test
    public void test_getAmountMinorInt_positive() {
        assertEquals(234, GBP_2_34.getAmountMinorInt());
    }

    @Test
    public void test_getAmountMinorInt_negative() {
        assertEquals(-578, GBP_M5_78.getAmountMinorInt());
    }

    @Test(expected = ArithmeticException.class)
    public void test_getAmountMinorInt_tooBigPositive() {
        GBP_INT_MAX_PLUS1.getAmountMinorInt();
    }

    @Test(expected = ArithmeticException.class)
    public void test_getAmountMinorInt_tooBigNegative() {
        GBP_INT_MIN_MINUS1.getAmountMinorInt();
    }

    //-----------------------------------------------------------------------
    // getMinorPart()
    //-----------------------------------------------------------------------
    @Test
    public void test_getMinorPart_positive() {
        assertEquals(34, GBP_2_34.getMinorPart());
    }

    @Test
    public void test_getMinorPart_negative() {
        assertEquals(-78, GBP_M5_78.getMinorPart());
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    @Test
    public void test_isZero() {
        assertEquals(true, GBP_0_00.isZero());
        assertEquals(false, GBP_2_34.isZero());
        assertEquals(false, GBP_M5_78.isZero());
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    @Test
    public void test_isPositive() {
        assertEquals(false, GBP_0_00.isPositive());
        assertEquals(true, GBP_2_34.isPositive());
        assertEquals(false, GBP_M5_78.isPositive());
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero()
    //-----------------------------------------------------------------------
    @Test
    public void test_isPositiveOrZero() {
        assertEquals(true, GBP_0_00.isPositiveOrZero());
        assertEquals(true, GBP_2_34.isPositiveOrZero());
        assertEquals(false, GBP_M5_78.isPositiveOrZero());
    }

    //-----------------------------------------------------------------------
    // isNegative()
    //-----------------------------------------------------------------------
    @Test
    public void test_isNegative() {
        assertEquals(false, GBP_0_00.isNegative());
        assertEquals(false, GBP_2_34.isNegative());
        assertEquals(true, GBP_M5_78.isNegative());
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero()
    //-----------------------------------------------------------------------
    @Test
    public void test_isNegativeOrZero() {
        assertEquals(true, GBP_0_00.isNegativeOrZero());
        assertEquals(false, GBP_2_34.isNegativeOrZero());
        assertEquals(true, GBP_M5_78.isNegativeOrZero());
    }

    //-----------------------------------------------------------------------
    // withAmount(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    public void test_withAmount_BigDecimal() {
        Money test = GBP_2_34.withAmount(BIGDEC_M5_78);
        assertEquals("GBP -5.78", test.toString());
    }

    @Test
    public void test_withAmount_BigDecimal_same() {
        Money test = GBP_2_34.withAmount(BIGDEC_2_34);
        assertSame(GBP_2_34, test);
    }

    @Test(expected = ArithmeticException.class)
    public void test_withAmount_BigDecimal_invalidScale() {
        GBP_2_34.withAmount(new BigDecimal("2.345"));
    }

    @Test(expected = NullPointerException.class)
    public void test_withAmount_BigDecimal_nullBigDecimal() {
        GBP_2_34.withAmount((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // withAmount(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_withAmount_BigDecimalRoundingMode() {
        Money test = GBP_2_34.withAmount(BIGDEC_M5_78, RoundingMode.UNNECESSARY);
        assertEquals("GBP -5.78", test.toString());
    }

    @Test
    public void test_withAmount_BigDecimalRoundingMode_same() {
        Money test = GBP_2_34.withAmount(BIGDEC_2_34, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_withAmount_BigDecimalRoundingMode_roundDown() {
        Money test = GBP_2_34.withAmount(new BigDecimal("2.355"), RoundingMode.DOWN);
        assertEquals(GBP_2_35, test);
    }

    @Test(expected = ArithmeticException.class)
    public void test_withAmount_BigDecimalRoundingMode_roundUnecessary() {
        GBP_2_34.withAmount(new BigDecimal("2.345"), RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_withAmount_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_2_34.withAmount((BigDecimal) null, RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_withAmount_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_2_34.withAmount(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // withAmount(double)
    //-----------------------------------------------------------------------
    @Test
    public void test_withAmount_double() {
        Money test = GBP_2_34.withAmount(-5.78d);
        assertEquals("GBP -5.78", test.toString());
    }

    @Test
    public void test_withAmount_double_same() {
        Money test = GBP_2_34.withAmount(2.34d);
        assertSame(GBP_2_34, test);
    }

    @Test(expected = ArithmeticException.class)
    public void test_withAmount_double_invalidScale() {
        GBP_2_34.withAmount(2.345d);
    }

    //-----------------------------------------------------------------------
    // withAmount(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_withAmount_doubleRoundingMode() {
        Money test = GBP_2_34.withAmount(-5.78d, RoundingMode.UNNECESSARY);
        assertEquals("GBP -5.78", test.toString());
    }

    @Test
    public void test_withAmount_doubleRoundingMode_same() {
        Money test = GBP_2_34.withAmount(2.34d, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_withAmount_doubleRoundingMode_roundDown() {
        Money test = GBP_2_34.withAmount(2.355d, RoundingMode.DOWN);
        assertEquals(GBP_2_35, test);
    }

    @Test(expected = ArithmeticException.class)
    public void test_withAmount_doubleRoundingMode_roundUnecessary() {
        GBP_2_34.withAmount(2.345d, RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_withAmount_doubleRoundingMode_nullRoundingMode() {
        GBP_2_34.withAmount(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // plus(Iterable)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Iterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, GBP_1_23);
        Money test = GBP_2_34.plus(iterable);
        assertEquals("GBP 5.90", test.toString());
    }

    @Test
    public void test_plus_Iterable_zero() {
        Iterable<Money> iterable = Arrays.asList(GBP_0_00);
        Money test = GBP_2_34.plus(iterable);
        assertSame(GBP_2_34, test);
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_plus_Iterable_currencyMismatch() {
        try {
            Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
            GBP_M5_78.plus(iterable);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_plus_Iterable_nullEntry() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null);
        GBP_M5_78.plus(iterable);
    }

    @Test(expected = NullPointerException.class)
    public void test_plus_Iterable_nullIterable() {
        GBP_M5_78.plus((Iterable<Money>) null);
    }

    //-----------------------------------------------------------------------
    // plus(Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_Money_zero() {
        Money test = GBP_2_34.plus(GBP_0_00);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_plus_Money_positive() {
        Money test = GBP_2_34.plus(GBP_1_23);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    public void test_plus_Money_negative() {
        Money test = GBP_2_34.plus(GBP_M1_23);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_plus_Money_currencyMismatch() {
        try {
            GBP_M5_78.plus(USD_1_23);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(USD, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_plus_Money_nullMoney() {
        GBP_M5_78.plus((Money) null);
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_BigDecimal_zero() {
        Money test = GBP_2_34.plus(BigDecimal.ZERO);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_plus_BigDecimal_positive() {
        Money test = GBP_2_34.plus(new BigDecimal("1.23"));
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    public void test_plus_BigDecimal_negative() {
        Money test = GBP_2_34.plus(new BigDecimal("-1.23"));
        assertEquals("GBP 1.11", test.toString());
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_BigDecimal_invalidScale() {
        GBP_2_34.plus(new BigDecimal("1.235"));
    }

    @Test(expected = NullPointerException.class)
    public void test_plus_BigDecimal_nullBigDecimal() {
        GBP_M5_78.plus((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_BigDecimalRoundingMode_zero() {
        Money test = GBP_2_34.plus(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_plus_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_34.plus(new BigDecimal("1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    public void test_plus_BigDecimalRoundingMode_negative() {
        Money test = GBP_2_34.plus(new BigDecimal("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    public void test_plus_BigDecimalRoundingMode_roundDown() {
        Money test = GBP_2_34.plus(new BigDecimal("1.235"), RoundingMode.DOWN);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_BigDecimalRoundingMode_roundUnecessary() {
        GBP_2_34.plus(new BigDecimal("1.235"), RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_plus_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_M5_78.plus((BigDecimal) null, RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_plus_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_M5_78.plus(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // plus(double)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_double_zero() {
        Money test = GBP_2_34.plus(0d);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_plus_double_positive() {
        Money test = GBP_2_34.plus(1.23d);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    public void test_plus_double_negative() {
        Money test = GBP_2_34.plus(-1.23d);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_double_invalidScale() {
        GBP_2_34.plus(1.235d);
    }

    //-----------------------------------------------------------------------
    // plus(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_plus_doubleRoundingMode_zero() {
        Money test = GBP_2_34.plus(0d, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_plus_doubleRoundingMode_positive() {
        Money test = GBP_2_34.plus(1.23d, RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    public void test_plus_doubleRoundingMode_negative() {
        Money test = GBP_2_34.plus(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    public void test_plus_doubleRoundingMode_roundDown() {
        Money test = GBP_2_34.plus(1.235d, RoundingMode.DOWN);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test(expected = ArithmeticException.class)
    public void test_plus_doubleRoundingMode_roundUnecessary() {
        GBP_2_34.plus(1.235d, RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_plus_doubleRoundingMode_nullRoundingMode() {
        GBP_M5_78.plus(2.34d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // plusMajor(long)
    //-----------------------------------------------------------------------
    @Test
    public void test_plusMajor_zero() {
        Money test = GBP_2_34.plusMajor(0);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_plusMajor_positive() {
        Money test = GBP_2_34.plusMajor(123);
        assertEquals("GBP 125.34", test.toString());
    }

    @Test
    public void test_plusMajor_negative() {
        Money test = GBP_2_34.plusMajor(-123);
        assertEquals("GBP -120.66", test.toString());
    }

    //-----------------------------------------------------------------------
    // plusMinor(long)
    //-----------------------------------------------------------------------
    @Test
    public void test_plusMinor_zero() {
        Money test = GBP_2_34.plusMinor(0);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_plusMinor_positive() {
        Money test = GBP_2_34.plusMinor(123);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    public void test_plusMinor_negative() {
        Money test = GBP_2_34.plusMinor(-123);
        assertEquals("GBP 1.11", test.toString());
    }

    //-----------------------------------------------------------------------
    // minus(Iterable)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_Iterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, GBP_1_23);
        Money test = GBP_2_34.minus(iterable);
        assertEquals("GBP -1.22", test.toString());
    }

    @Test
    public void test_minus_Iterable_zero() {
        Iterable<Money> iterable = Arrays.asList(GBP_0_00);
        Money test = GBP_2_34.minus(iterable);
        assertSame(GBP_2_34, test);
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_minus_Iterable_currencyMismatch() {
        try {
            Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
            GBP_M5_78.minus(iterable);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(JPY, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_minus_Iterable_nullEntry() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null);
        GBP_M5_78.minus(iterable);
    }

    @Test(expected = NullPointerException.class)
    public void test_minus_Iterable_nullIterable() {
        GBP_M5_78.minus((Iterable<Money>) null);
    }

    //-----------------------------------------------------------------------
    // minus(Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_Money_zero() {
        Money test = GBP_2_34.minus(GBP_0_00);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_minus_Money_positive() {
        Money test = GBP_2_34.minus(GBP_1_23);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    public void test_minus_Money_negative() {
        Money test = GBP_2_34.minus(GBP_M1_23);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_minus_Money_currencyMismatch() {
        try {
            GBP_M5_78.minus(USD_1_23);
        } catch (CurrencyMismatchException ex) {
            assertEquals(GBP, ex.getFirstCurrency());
            assertEquals(USD, ex.getSecondCurrency());
            throw ex;
        }
    }

    @Test(expected = NullPointerException.class)
    public void test_minus_Money_nullMoney() {
        GBP_M5_78.minus((Money) null);
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_BigDecimal_zero() {
        Money test = GBP_2_34.minus(BigDecimal.ZERO);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_minus_BigDecimal_positive() {
        Money test = GBP_2_34.minus(new BigDecimal("1.23"));
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    public void test_minus_BigDecimal_negative() {
        Money test = GBP_2_34.minus(new BigDecimal("-1.23"));
        assertEquals("GBP 3.57", test.toString());
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_BigDecimal_invalidScale() {
        GBP_2_34.minus(new BigDecimal("1.235"));
    }

    @Test(expected = NullPointerException.class)
    public void test_minus_BigDecimal_nullBigDecimal() {
        GBP_M5_78.minus((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_BigDecimalRoundingMode_zero() {
        Money test = GBP_2_34.minus(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_minus_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_34.minus(new BigDecimal("1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    public void test_minus_BigDecimalRoundingMode_negative() {
        Money test = GBP_2_34.minus(new BigDecimal("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    public void test_minus_BigDecimalRoundingMode_roundDown() {
        Money test = GBP_2_34.minus(new BigDecimal("1.235"), RoundingMode.DOWN);
        assertEquals("GBP 1.10", test.toString());
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_BigDecimalRoundingMode_roundUnecessary() {
        GBP_2_34.minus(new BigDecimal("1.235"), RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_minus_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_M5_78.minus((BigDecimal) null, RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_minus_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_M5_78.minus(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // minus(double)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_double_zero() {
        Money test = GBP_2_34.minus(0d);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_minus_double_positive() {
        Money test = GBP_2_34.minus(1.23d);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    public void test_minus_double_negative() {
        Money test = GBP_2_34.minus(-1.23d);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_double_invalidScale() {
        GBP_2_34.minus(1.235d);
    }

    //-----------------------------------------------------------------------
    // minus(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_minus_doubleRoundingMode_zero() {
        Money test = GBP_2_34.minus(0d, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_minus_doubleRoundingMode_positive() {
        Money test = GBP_2_34.minus(1.23d, RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    public void test_minus_doubleRoundingMode_negative() {
        Money test = GBP_2_34.minus(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    public void test_minus_doubleRoundingMode_roundDown() {
        Money test = GBP_2_34.minus(1.235d, RoundingMode.DOWN);
        assertEquals("GBP 1.10", test.toString());
    }

    @Test(expected = ArithmeticException.class)
    public void test_minus_doubleRoundingMode_roundUnecessary() {
        GBP_2_34.minus(1.235d, RoundingMode.UNNECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public void test_minus_doubleRoundingMode_nullRoundingMode() {
        GBP_M5_78.minus(2.34d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // minusMajor(long)
    //-----------------------------------------------------------------------
    @Test
    public void test_minusMajor_zero() {
        Money test = GBP_2_34.minusMajor(0);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_minusMajor_positive() {
        Money test = GBP_2_34.minusMajor(123);
        assertEquals("GBP -120.66", test.toString());
    }

    @Test
    public void test_minusMajor_negative() {
        Money test = GBP_2_34.minusMajor(-123);
        assertEquals("GBP 125.34", test.toString());
    }

    //-----------------------------------------------------------------------
    // minusMinor(long)
    //-----------------------------------------------------------------------
    @Test
    public void test_minusMinor_zero() {
        Money test = GBP_2_34.minusMinor(0);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_minusMinor_positive() {
        Money test = GBP_2_34.minusMinor(123);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    public void test_minusMinor_negative() {
        Money test = GBP_2_34.minusMinor(-123);
        assertEquals("GBP 3.57", test.toString());
    }

    //-----------------------------------------------------------------------
    // multipliedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy_BigDecimalRoundingMode_one() {
        Money test = GBP_2_34.multipliedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_multipliedBy_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_33.multipliedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals("GBP 5.82", test.toString());
    }

    @Test
    public void test_multipliedBy_BigDecimalRoundingMode_positive_halfUp() {
        Money test = GBP_2_33.multipliedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals("GBP 5.83", test.toString());
    }

    @Test
    public void test_multipliedBy_BigDecimalRoundingMode_negative() {
        Money test = GBP_2_33.multipliedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
        assertEquals("GBP -5.83", test.toString());
    }

    @Test(expected = NullPointerException.class)
    public void test_multipliedBy_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_5_78.multipliedBy((BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expected = NullPointerException.class)
    public void test_multipliedBy_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_5_78.multipliedBy(new BigDecimal("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy_doubleRoundingMode_one() {
        Money test = GBP_2_34.multipliedBy(1d, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_multipliedBy_doubleRoundingMode_positive() {
        Money test = GBP_2_33.multipliedBy(2.5d, RoundingMode.DOWN);
        assertEquals("GBP 5.82", test.toString());
    }

    @Test
    public void test_multipliedBy_doubleRoundingMode_positive_halfUp() {
        Money test = GBP_2_33.multipliedBy(2.5d, RoundingMode.HALF_UP);
        assertEquals("GBP 5.83", test.toString());
    }

    @Test
    public void test_multipliedBy_doubleRoundingMode_negative() {
        Money test = GBP_2_33.multipliedBy(-2.5d, RoundingMode.FLOOR);
        assertEquals("GBP -5.83", test.toString());
    }

    @Test(expected = NullPointerException.class)
    public void test_multipliedBy_doubleRoundingMode_nullRoundingMode() {
        GBP_5_78.multipliedBy(2.5d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(long)
    //-----------------------------------------------------------------------
    @Test
    public void test_multipliedBy_long_one() {
        Money test = GBP_2_34.multipliedBy(1);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_multipliedBy_long_positive() {
        Money test = GBP_2_34.multipliedBy(3);
        assertEquals("GBP 7.02", test.toString());
    }

    @Test
    public void test_multipliedBy_long_negative() {
        Money test = GBP_2_34.multipliedBy(-3);
        assertEquals("GBP -7.02", test.toString());
    }

    //-----------------------------------------------------------------------
    // dividedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_dividedBy_BigDecimalRoundingMode_one() {
        Money test = GBP_2_34.dividedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_dividedBy_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_34.dividedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals("GBP 0.93", test.toString());
    }

    @Test
    public void test_dividedBy_BigDecimalRoundingMode_positive_halfUp() {
        Money test = GBP_2_34.dividedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals("GBP 0.94", test.toString());
    }

    @Test
    public void test_dividedBy_BigDecimalRoundingMode_negative() {
        Money test = GBP_2_34.dividedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
        assertEquals("GBP -0.94", test.toString());
    }

    @Test(expected = NullPointerException.class)
    public void test_dividedBy_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_5_78.dividedBy((BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expected = NullPointerException.class)
    public void test_dividedBy_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_5_78.dividedBy(new BigDecimal("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // dividedBy(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_dividedBy_doubleRoundingMode_one() {
        Money test = GBP_2_34.dividedBy(1d, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_dividedBy_doubleRoundingMode_positive() {
        Money test = GBP_2_34.dividedBy(2.5d, RoundingMode.DOWN);
        assertEquals("GBP 0.93", test.toString());
    }

    @Test
    public void test_dividedBy_doubleRoundingMode_positive_halfUp() {
        Money test = GBP_2_34.dividedBy(2.5d, RoundingMode.HALF_UP);
        assertEquals("GBP 0.94", test.toString());
    }

    @Test
    public void test_dividedBy_doubleRoundingMode_negative() {
        Money test = GBP_2_34.dividedBy(-2.5d, RoundingMode.FLOOR);
        assertEquals("GBP -0.94", test.toString());
    }

    @Test(expected = NullPointerException.class)
    public void test_dividedBy_doubleRoundingMode_nullRoundingMode() {
        GBP_5_78.dividedBy(2.5d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // dividedBy(long,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_dividedBy_long_one() {
        Money test = GBP_2_34.dividedBy(1, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_dividedBy_long_positive() {
        Money test = GBP_2_34.dividedBy(3, RoundingMode.DOWN);
        assertEquals("GBP 0.78", test.toString());
    }

    @Test
    public void test_dividedBy_long_positive_roundDown() {
        Money test = GBP_2_35.dividedBy(3, RoundingMode.DOWN);
        assertEquals("GBP 0.78", test.toString());
    }

    @Test
    public void test_dividedBy_long_positive_roundUp() {
        Money test = GBP_2_35.dividedBy(3, RoundingMode.UP);
        assertEquals("GBP 0.79", test.toString());
    }

    @Test
    public void test_dividedBy_long_negative() {
        Money test = GBP_2_34.dividedBy(-3, RoundingMode.DOWN);
        assertEquals("GBP -0.78", test.toString());
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    @Test
    public void test_negated_positive() {
        Money test = GBP_2_34.negated();
        assertEquals("GBP -2.34", test.toString());
    }

    @Test
    public void test_negated_negative() {
        Money test = Money.parse("GBP -2.34").negated();
        assertEquals("GBP 2.34", test.toString());
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    @Test
    public void test_abs_positive() {
        Money test = GBP_2_34.abs();
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_abs_negative() {
        Money test = Money.parse("GBP -2.34").abs();
        assertEquals("GBP 2.34", test.toString());
    }

    //-----------------------------------------------------------------------
    // rounded()
    //-----------------------------------------------------------------------
    @Test
    public void test_round_2down() {
        Money test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_round_2up() {
        Money test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    public void test_round_1down() {
        Money test = GBP_2_34.rounded(1, RoundingMode.DOWN);
        assertEquals("GBP 2.30", test.toString());
    }

    @Test
    public void test_round_1up() {
        Money test = GBP_2_34.rounded(1, RoundingMode.UP);
        assertEquals("GBP 2.40", test.toString());
    }

    @Test
    public void test_round_0down() {
        Money test = GBP_2_34.rounded(0, RoundingMode.DOWN);
        assertEquals("GBP 2.00", test.toString());
    }

    @Test
    public void test_round_0up() {
        Money test = GBP_2_34.rounded(0, RoundingMode.UP);
        assertEquals("GBP 3.00", test.toString());
    }

    @Test
    public void test_round_M1down() {
        Money test = Money.parse("GBP 432.34").rounded(-1, RoundingMode.DOWN);
        assertEquals("GBP 430.00", test.toString());
    }

    @Test
    public void test_round_M1up() {
        Money test = Money.parse("GBP 432.34").rounded(-1, RoundingMode.UP);
        assertEquals("GBP 440.00", test.toString());
    }

    @Test
    public void test_round_3() {
        Money test = GBP_2_34.rounded(3, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    //-----------------------------------------------------------------------
    // convertedTo(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    public void test_convertedTo_BigDecimalRoundingMode_positive() {
        Money test = GBP_2_33.convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.DOWN);
        assertEquals("EUR 5.82", test.toString());
    }

    @Test
    public void test_convertedTo_BigDecimalRoundingMode_positive_halfUp() {
        Money test = GBP_2_33.convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertEquals("EUR 5.83", test.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_convertedTo_BigDecimalRoundingMode_negative() {
        GBP_2_33.convertedTo(EUR, new BigDecimal("-2.5"), RoundingMode.FLOOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_convertedTo_BigDecimalRoundingMode_sameCurrency() {
        GBP_2_33.convertedTo(GBP, new BigDecimal("2.5"), RoundingMode.DOWN);
    }

    @Test(expected = NullPointerException.class)
    public void test_convertedTo_BigDecimalRoundingMode_nullCurrency() {
        GBP_5_78.convertedTo((CurrencyUnit) null, new BigDecimal("2"), RoundingMode.DOWN);
    }

    @Test(expected = NullPointerException.class)
    public void test_convertedTo_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_5_78.convertedTo(EUR, (BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expected = NullPointerException.class)
    public void test_convertedTo_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_5_78.convertedTo(EUR, new BigDecimal("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    @Test
    public void test_toBigMoney() {
        assertEquals(BigMoney.ofMinor(GBP, 234), GBP_2_34.toBigMoney());
    }

    //-----------------------------------------------------------------------
    // isSameCurrency(Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_isSameCurrency_Money_same() {
        assertEquals(true, GBP_2_34.isSameCurrency(GBP_2_35));
    }

    @Test
    public void test_isSameCurrency_Money_different() {
        assertEquals(false, GBP_2_34.isSameCurrency(USD_2_34));
    }

    @Test
    public void test_isSameCurrency_BigMoney_same() {
        assertEquals(true, GBP_2_34.isSameCurrency(BigMoney.parse("GBP 2")));
    }

    @Test
    public void test_isSameCurrency_BigMoney_different() {
        assertEquals(false, GBP_2_34.isSameCurrency(BigMoney.parse("USD 2")));
    }

    @Test(expected = NullPointerException.class)
    public void test_isSameCurrency_Money_nullMoney() {
        GBP_2_34.isSameCurrency((Money) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    public void test_compareTo_Money() {
        Money a = GBP_2_34;
        Money b = GBP_2_35;
        Money c = GBP_2_36;
        assertEquals(0, a.compareTo(a));
        assertEquals(0, b.compareTo(b));
        assertEquals(0, c.compareTo(c));
        
        assertEquals(-1, a.compareTo(b));
        assertEquals(1, b.compareTo(a));
        
        assertEquals(-1, a.compareTo(c));
        assertEquals(1, c.compareTo(a));
        
        assertEquals(-1, b.compareTo(c));
        assertEquals(1, c.compareTo(b));
    }

    @Test
    public void test_compareTo_BigMoney() {
        Money t = GBP_2_35;
        BigMoney a = BigMoney.ofMinor(GBP, 234);
        BigMoney b = BigMoney.ofMinor(GBP, 235);
        BigMoney c = BigMoney.ofMinor(GBP, 236);
        assertEquals(1, t.compareTo(a));
        assertEquals(0, t.compareTo(b));
        assertEquals(-1, t.compareTo(c));
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_compareTo_currenciesDiffer() {
        Money a = GBP_2_34;
        Money b = USD_2_35;
        a.compareTo(b);
    }

    @Test(expected = ClassCastException.class)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void test_compareTo_wrongType() {
        Comparable a = GBP_2_34;
        a.compareTo("NotRightType");
    }

    //-----------------------------------------------------------------------
    // isEqual()
    //-----------------------------------------------------------------------
    @Test
    public void test_isEqual() {
        Money a = GBP_2_34;
        Money b = GBP_2_35;
        Money c = GBP_2_36;
        assertEquals(true, a.isEqual(a));
        assertEquals(true, b.isEqual(b));
        assertEquals(true, c.isEqual(c));
        
        assertEquals(false, a.isEqual(b));
        assertEquals(false, b.isEqual(a));
        
        assertEquals(false, a.isEqual(c));
        assertEquals(false, c.isEqual(a));
        
        assertEquals(false, b.isEqual(c));
        assertEquals(false, c.isEqual(b));
    }

    @Test
    public void test_isEqual_Money() {
        Money a = GBP_2_34;
        BigMoney b = BigMoney.ofMinor(GBP, 234);
        assertEquals(true, a.isEqual(b));
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_isEqual_currenciesDiffer() {
        Money a = GBP_2_34;
        Money b = USD_2_35;
        a.isEqual(b);
    }

    //-----------------------------------------------------------------------
    // isGreaterThan()
    //-----------------------------------------------------------------------
    @Test
    public void test_isGreaterThan() {
        Money a = GBP_2_34;
        Money b = GBP_2_35;
        Money c = GBP_2_36;
        assertEquals(false, a.isGreaterThan(a));
        assertEquals(false, b.isGreaterThan(b));
        assertEquals(false, c.isGreaterThan(c));
        
        assertEquals(false, a.isGreaterThan(b));
        assertEquals(true, b.isGreaterThan(a));
        
        assertEquals(false, a.isGreaterThan(c));
        assertEquals(true, c.isGreaterThan(a));
        
        assertEquals(false, b.isGreaterThan(c));
        assertEquals(true, c.isGreaterThan(b));
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_isGreaterThan_currenciesDiffer() {
        Money a = GBP_2_34;
        Money b = USD_2_35;
        a.isGreaterThan(b);
    }

    //-----------------------------------------------------------------------
    // isLessThan()
    //-----------------------------------------------------------------------
    @Test
    public void test_isLessThan() {
        Money a = GBP_2_34;
        Money b = GBP_2_35;
        Money c = GBP_2_36;
        assertEquals(false, a.isLessThan(a));
        assertEquals(false, b.isLessThan(b));
        assertEquals(false, c.isLessThan(c));
        
        assertEquals(true, a.isLessThan(b));
        assertEquals(false, b.isLessThan(a));
        
        assertEquals(true, a.isLessThan(c));
        assertEquals(false, c.isLessThan(a));
        
        assertEquals(true, b.isLessThan(c));
        assertEquals(false, c.isLessThan(b));
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_isLessThan_currenciesDiffer() {
        Money a = GBP_2_34;
        Money b = USD_2_35;
        a.isLessThan(b);
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_hashCode_positive() {
        Money a = GBP_2_34;
        Money b = GBP_2_34;
        Money c = GBP_2_35;
        assertEquals(true, a.equals(a));
        assertEquals(true, b.equals(b));
        assertEquals(true, c.equals(c));
        
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(true, a.hashCode() == b.hashCode());
        
        assertEquals(false, a.equals(c));
        assertEquals(false, b.equals(c));
    }

    @Test
    public void test_equals_false() {
        Money a = GBP_2_34;
        assertEquals(false, a.equals(null));
        assertEquals(false, a.equals("String"));
        assertEquals(false, a.equals(new Object()));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void test_toString_positive() {
        Money test = Money.of(GBP, BIGDEC_2_34);
        assertEquals("GBP 2.34", test.toString());
    }

    @Test
    public void test_toString_negative() {
        Money test = Money.of(EUR, BIGDEC_M5_78);
        assertEquals("EUR -5.78", test.toString());
    }

}
