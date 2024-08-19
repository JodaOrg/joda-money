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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test Money.
 */
class TestMoney {

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
    private static final Money GBP_LONG_MAX_MAJOR_PLUS1 = Money.of(
            GBP,
            BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
    private static final Money GBP_LONG_MIN_MAJOR_MINUS1 = Money.of(
            GBP,
            BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
    private static final Money JPY_423 = Money.parse("JPY 423");
    private static final Money USD_1_23 = Money.parse("USD 1.23");
    private static final Money USD_2_34 = Money.parse("USD 2.34");
    private static final Money USD_2_35 = Money.parse("USD 2.35");

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency_BigDecimal() {
        var test = Money.of(GBP, BIGDEC_2_34);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(234);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_BigDecimal_correctScale() {
        var test = Money.of(GBP, BIGDEC_2_3);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(230);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_BigDecimal_invalidScaleGBP() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> Money.of(GBP, BIGDEC_2_345));
    }

    @Test
    void test_factory_of_Currency_BigDecimal_invalidScaleJPY() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> Money.of(JPY, BIGDEC_2_3));
    }

    @Test
    void test_factory_of_Currency_BigDecimal_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of((CurrencyUnit) null, BIGDEC_2_34));
    }

    @Test
    void test_factory_of_Currency_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of(GBP, (BigDecimal) null));
    }

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency_BigDecimal_GBP_RoundingMode_DOWN() {
        var test = Money.of(GBP, BIGDEC_2_34, RoundingMode.DOWN);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(234);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_BigDecimal_JPY_RoundingMode_DOWN() {
        var test = Money.of(JPY, BIGDEC_2_34, RoundingMode.DOWN);
        assertThat(test.getCurrencyUnit()).isEqualTo(JPY);
        assertThat(test.getAmountMinorInt()).isEqualTo(2);
        assertThat(test.getAmount().scale()).isEqualTo(0);
    }

    @Test
    void test_factory_of_Currency_BigDecimal_JPY_RoundingMode_UP() {
        var test = Money.of(JPY, BIGDEC_2_34, RoundingMode.UP);
        assertThat(test.getCurrencyUnit()).isEqualTo(JPY);
        assertThat(test.getAmountMinorInt()).isEqualTo(3);
        assertThat(test.getAmount().scale()).isEqualTo(0);
    }

    @Test
    void test_factory_of_Currency_BigDecimal_RoundingMode_UNNECESSARY() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> Money.of(JPY, BIGDEC_2_34, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_factory_of_Currency_BigDecimal_RoundingMode_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of((CurrencyUnit) null, BIGDEC_2_34, RoundingMode.DOWN));
    }

    @Test
    void test_factory_of_Currency_BigDecimal_RoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of(GBP, (BigDecimal) null, RoundingMode.DOWN));
    }

    @Test
    void test_factory_of_Currency_BigDecimal_RoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of(GBP, BIGDEC_2_34, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // of(Currency,double)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency_double() {
        var test = Money.of(GBP, 2.34d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(234);
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_double_correctScale() {
        var test = Money.of(GBP, 2.3d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(230);
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_double_trailingZero1() {
        var test = Money.of(GBP, 1.230d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(123L, 2));
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_double_trailingZero2() {
        var test = Money.of(GBP, 1.20d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(120L, 2));
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_double_medium() {
        var test = Money.of(GBP, 2000d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(200000L, 2));
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_double_big() {
        var test = Money.of(GBP, 200000000d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(20000000000L, 2));
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_double_invalidScaleGBP() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> Money.of(GBP, 2.345d));
    }

    @Test
    void test_factory_of_Currency_double_invalidScaleJPY() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> Money.of(JPY, 2.3d));
    }

    @Test
    void test_factory_of_Currency_double_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of((CurrencyUnit) null, BIGDEC_2_34));
    }

    //-----------------------------------------------------------------------
    // of(Currency,double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency_double_GBP_RoundingMode_DOWN() {
        var test = Money.of(GBP, 2.34d, RoundingMode.DOWN);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(234);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_double_JPY_RoundingMode_DOWN() {
        var test = Money.of(JPY, 2.34d, RoundingMode.DOWN);
        assertThat(test.getCurrencyUnit()).isEqualTo(JPY);
        assertThat(test.getAmountMinorInt()).isEqualTo(2);
        assertThat(test.getAmount().scale()).isEqualTo(0);
    }

    @Test
    void test_factory_of_Currency_double_JPY_RoundingMode_UP() {
        var test = Money.of(JPY, 2.34d, RoundingMode.UP);
        assertThat(test.getCurrencyUnit()).isEqualTo(JPY);
        assertThat(test.getAmountMinorInt()).isEqualTo(3);
        assertThat(test.getAmount().scale()).isEqualTo(0);
    }

    @Test
    void test_factory_of_Currency_double_RoundingMode_UNNECESSARY() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> Money.of(JPY, 2.34d, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_factory_of_Currency_double_RoundingMode_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of((CurrencyUnit) null, 2.34d, RoundingMode.DOWN));
    }

    @Test
    void test_factory_of_Currency_double_RoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of(GBP, 2.34d, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // ofMajor(Currency,long)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofMajor_Currency_long() {
        var test = Money.ofMajor(GBP, 234);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(23400);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_ofMajor_Currency_long_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.ofMajor((CurrencyUnit) null, 234));
    }

    //-----------------------------------------------------------------------
    // ofMinor(Currency,long)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofMinor_Currency_long() {
        var test = Money.ofMinor(GBP, 234);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(234);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_ofMinor_Currency_long_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.ofMinor((CurrencyUnit) null, 234));
    }

    //-----------------------------------------------------------------------
    // zero(Currency)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_zero_Currency() {
        var test = Money.zero(GBP);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(0);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_zero_Currency_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.zero((CurrencyUnit) null));
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_from_BigMoneyProvider() {
        var test = Money.of(BigMoney.parse("GBP 104.23"));
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(10423);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_from_BigMoneyProvider_fixScale() {
        var test = Money.of(BigMoney.parse("GBP 104.2"));
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(10420);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_from_BigMoneyProvider_invalidCurrencyScale() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> Money.of(BigMoney.parse("GBP 104.235")));
    }

    @Test
    void test_factory_from_BigMoneyProvider_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of((BigMoneyProvider) null));
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_from_BigMoneyProvider_RoundingMode() {
        var test = Money.of(BigMoney.parse("GBP 104.235"), RoundingMode.HALF_EVEN);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(10424);
        assertThat(test.getAmount().scale()).isEqualTo(2);
    }

    @Test
    void test_factory_from_BigMoneyProvider_RoundingMode_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of((BigMoneyProvider) null, RoundingMode.DOWN));
    }

    @Test
    void test_factory_from_BigMoneyProvider_RoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.of(BigMoney.parse("GBP 104.235"), (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // total(Money...)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_varargs_1() {
        var test = Money.total(GBP_1_23);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(123);
    }

    @Test
    void test_factory_total_array_1() {
        var array = new Money[] {GBP_1_23};
        var test = Money.total(array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(123);
    }

    @Test
    void test_factory_total_varargs_3() {
        var test = Money.total(GBP_1_23, GBP_2_33, GBP_2_36);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_array_3() {
        var array = new Money[] {GBP_1_23, GBP_2_33, GBP_2_36};
        var test = Money.total(array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_varargs_empty() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> Money.total());
    }

    @Test
    void test_factory_total_array_empty() {
        var array = new Money[0];
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> Money.total(array));
    }

    @Test
    void test_factory_total_varargs_currenciesDiffer() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Money.total(GBP_2_33, JPY_423);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_factory_total_array_currenciesDiffer() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    var array = new Money[] {GBP_2_33, JPY_423};
                    Money.total(array);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_factory_total_varargs_nullFirst() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total((Money) null, GBP_2_33, GBP_2_36));
    }

    @Test
    void test_factory_total_array_nullFirst() {
        var array = new Money[] {null, GBP_2_33, GBP_2_36};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(array));
    }

    @Test
    void test_factory_total_varargs_nullNotFirst() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(GBP_2_33, null, GBP_2_36));
    }

    @Test
    void test_factory_total_array_nullNotFirst() {
        var array = new Money[] {GBP_2_33, null, GBP_2_36};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(array));
    }

    //-----------------------------------------------------------------------
    // total(Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_Iterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_1_23, GBP_2_33, GBP_2_36);
        var test = Money.total(iterable);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_Iterable_empty() {
        Iterable<Money> iterable = Collections.emptyList();
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> Money.total(iterable));
    }

    @Test
    void test_factory_total_Iterable_currenciesDiffer() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
                    Money.total(iterable);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_factory_total_Iterable_nullFirst() {
        Iterable<Money> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(iterable));
    }

    @Test
    void test_factory_total_Iterable_nullNotFirst() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(iterable));
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,Money...)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_CurrencyUnitVarargs_1() {
        var test = Money.total(GBP, GBP_1_23);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(123);
    }

    @Test
    void test_factory_total_CurrencyUnitArray_1() {
        var array = new Money[] {GBP_1_23};
        var test = Money.total(GBP, array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(123);
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_3() {
        var test = Money.total(GBP, GBP_1_23, GBP_2_33, GBP_2_36);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_CurrencyUnitArray_3() {
        var array = new Money[] {GBP_1_23, GBP_2_33, GBP_2_36};
        var test = Money.total(GBP, array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_empty() {
        var test = Money.total(GBP);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(0);
    }

    @Test
    void test_factory_total_CurrencyUnitArray_empty() {
        var array = new Money[0];
        var test = Money.total(GBP, array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(0);
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_currenciesDiffer() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Money.total(GBP, JPY_423);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_factory_total_CurrencyUnitArray_currenciesDiffer() {
        var array = new Money[] {JPY_423};
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Money.total(GBP, array);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_currenciesDifferInArray() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Money.total(GBP, GBP_2_33, JPY_423);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_factory_total_CurrencyUnitArray_currenciesDifferInArray() {
        var array = new Money[] {GBP_2_33, JPY_423};
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Money.total(GBP, array);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_nullFirst() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(GBP, null, GBP_2_33, GBP_2_36));
    }

    @Test
    void test_factory_total_CurrencyUnitArray_nullFirst() {
        var array = new Money[] {null, GBP_2_33, GBP_2_36};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(GBP, array));
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_nullNotFirst() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(GBP, GBP_2_33, null, GBP_2_36));
    }

    @Test
    void test_factory_total_CurrencyUnitArray_nullNotFirst() {
        var array = new Money[] {GBP_2_33, null, GBP_2_36};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(GBP, array));
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_CurrencyUnitIterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_1_23, GBP_2_33, GBP_2_36);
        var test = Money.total(GBP, iterable);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_empty() {
        Iterable<Money> iterable = Collections.emptyList();
        var test = Money.total(GBP, iterable);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(0);
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_currenciesDiffer() {
        Iterable<Money> iterable = Arrays.asList(JPY_423);
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Money.total(GBP, iterable);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_currenciesDifferInIterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Money.total(GBP, iterable);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_nullFirst() {
        Iterable<Money> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(GBP, iterable));
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_nullNotFirst() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.total(GBP, iterable));
    }

    //-----------------------------------------------------------------------
    // parse(String)
    //-----------------------------------------------------------------------
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

    @ParameterizedTest
    @MethodSource("data_parse")
    void test_factory_parse(String str, CurrencyUnit currency, int amount) {
        var test = Money.parse(str);
        assertThat(test.getCurrencyUnit()).isEqualTo(currency);
        assertThat(test.getAmountMinorInt()).isEqualTo(amount);
    }

    @Test
    void test_factory_parse_String_tooShort() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> Money.parse("GBP "));
    }

    @Test
    void test_factory_parse_String_badCurrency() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> Money.parse("GBX 2.34"));
    }

    @Test
    void test_factory_parse_String_nullString() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> Money.parse((String) null));
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    @Test
    void test_constructor_null1() throws Exception {
        var con = Money.class.getDeclaredConstructor(BigMoney.class);
        assertThat(Modifier.isPublic(con.getModifiers())).isFalse();
        assertThat(Modifier.isProtected(con.getModifiers())).isFalse();
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] {null});
            fail("");
        } catch (InvocationTargetException ex) {
            assertThat(ex.getCause().getClass()).isEqualTo(AssertionError.class);
        }
    }

    @Test
    void test_constructor_scale() throws Exception {
        var con = Money.class.getDeclaredConstructor(BigMoney.class);
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] {BigMoney.of(GBP, BIGDEC_2_3)});
            fail("");
        } catch (InvocationTargetException ex) {
            assertThat(ex.getCause().getClass()).isEqualTo(AssertionError.class);
        }
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test
    void test_serialization() throws Exception {
        var a = GBP_2_34;
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(a);
            oos.close();
            var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            var input = (Money) ois.readObject();
            assertThat(input).isEqualTo(a);
        }
    }

    @Test
    void test_serialization_invalidNumericCode() throws IOException {
        var cu = new CurrencyUnit("GBP", (short) 234, (short) 2);
        var m = Money.of(cu, 123.43d);
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(m);
            oos.close();
            var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            assertThatExceptionOfType(InvalidObjectException.class)
                .isThrownBy(() -> ois.readObject());
        }
    }

    @Test
    void test_serialization_invalidDecimalPlaces() throws IOException {
        var cu = new CurrencyUnit("GBP", (short) 826, (short) 3);
        var m = Money.of(cu, 123.43d);
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(m);
            oos.close();
            var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            assertThatExceptionOfType(InvalidObjectException.class)
                .isThrownBy(() -> ois.readObject());
        }
    }

    //-----------------------------------------------------------------------
    // getCurrencyUnit()
    //-----------------------------------------------------------------------
    @Test
    void test_getCurrencyUnit_GBP() {
        assertThat(GBP_2_34.getCurrencyUnit()).isEqualTo(GBP);
    }

    @Test
    void test_getCurrencyUnit_EUR() {
        assertThat(Money.parse("EUR -5.78").getCurrencyUnit()).isEqualTo(EUR);
    }

    //-----------------------------------------------------------------------
    // withCurrencyUnit(Currency)
    //-----------------------------------------------------------------------
    @Test
    void test_withCurrencyUnit_Currency() {
        var test = GBP_2_34.withCurrencyUnit(USD);
        assertThat(test).hasToString("USD 2.34");
    }

    @Test
    void test_withCurrencyUnit_Currency_same() {
        var test = GBP_2_34.withCurrencyUnit(GBP);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_withCurrencyUnit_Currency_scaleProblem() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.withCurrencyUnit(JPY));
    }

    @Test
    void test_withCurrencyUnit_Currency_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.withCurrencyUnit((CurrencyUnit) null));
    }

    //-----------------------------------------------------------------------
    // withCurrencyUnit(Currency,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_withCurrencyUnit_CurrencyRoundingMode_DOWN() {
        var test = GBP_2_34.withCurrencyUnit(JPY, RoundingMode.DOWN);
        assertThat(test).hasToString("JPY 2");
    }

    @Test
    void test_withCurrencyUnit_CurrencyRoundingMode_UP() {
        var test = GBP_2_34.withCurrencyUnit(JPY, RoundingMode.UP);
        assertThat(test).hasToString("JPY 3");
    }

    @Test
    void test_withCurrencyUnit_CurrencyRoundingMode_same() {
        var test = GBP_2_34.withCurrencyUnit(GBP, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_withCurrencyUnit_CurrencyRoundingMode_UNECESSARY() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.withCurrencyUnit(JPY, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_withCurrencyUnit_CurrencyRoundingMode_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.withCurrencyUnit((CurrencyUnit) null, RoundingMode.UNNECESSARY));
    }

    //-----------------------------------------------------------------------
    // getScale()
    //-----------------------------------------------------------------------
    @Test
    void test_getScale_GBP() {
        assertThat(GBP_2_34.getScale()).isEqualTo(2);
    }

    @Test
    void test_getScale_JPY() {
        assertThat(JPY_423.getScale()).isEqualTo(0);
    }

    //-----------------------------------------------------------------------
    // getAmount()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmount_positive() {
        assertThat(GBP_2_34.getAmount()).isEqualTo(BIGDEC_2_34);
    }

    @Test
    void test_getAmount_negative() {
        assertThat(GBP_M5_78.getAmount()).isEqualTo(BIGDEC_M5_78);
    }

    //-----------------------------------------------------------------------
    // getAmountMajor()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMajor_positive() {
        assertThat(GBP_2_34.getAmountMajor()).isEqualTo(BigDecimal.valueOf(2));
    }

    @Test
    void test_getAmountMajor_negative() {
        assertThat(GBP_M5_78.getAmountMajor()).isEqualTo(BigDecimal.valueOf(-5));
    }

    //-----------------------------------------------------------------------
    // getAmountMajorLong()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMajorLong_positive() {
        assertThat(GBP_2_34.getAmountMajorLong()).isEqualTo(2L);
    }

    @Test
    void test_getAmountMajorLong_negative() {
        assertThat(GBP_M5_78.getAmountMajorLong()).isEqualTo(-5L);
    }

    @Test
    void test_getAmountMajorLong_tooBigPositive() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_LONG_MAX_MAJOR_PLUS1.getAmountMajorLong());
    }

    @Test
    void test_getAmountMajorLong_tooBigNegative() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_LONG_MIN_MAJOR_MINUS1.getAmountMajorLong());
    }

    //-----------------------------------------------------------------------
    // getAmountMajorInt()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMajorInt_positive() {
        assertThat(GBP_2_34.getAmountMajorInt()).isEqualTo(2);
    }

    @Test
    void test_getAmountMajorInt_negative() {
        assertThat(GBP_M5_78.getAmountMajorInt()).isEqualTo(-5);
    }

    @Test
    void test_getAmountMajorInt_tooBigPositive() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_INT_MAX_MAJOR_PLUS1.getAmountMajorInt());
    }

    @Test
    void test_getAmountMajorInt_tooBigNegative() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_INT_MIN_MAJOR_MINUS1.getAmountMajorInt());
    }

    //-----------------------------------------------------------------------
    // getAmountMinor()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMinor_positive() {
        assertThat(GBP_2_34.getAmountMinor()).isEqualTo(BigDecimal.valueOf(234));
    }

    @Test
    void test_getAmountMinor_negative() {
        assertThat(GBP_M5_78.getAmountMinor()).isEqualTo(BigDecimal.valueOf(-578));
    }

    //-----------------------------------------------------------------------
    // getAmountMinorLong()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMinorLong_positive() {
        assertThat(GBP_2_34.getAmountMinorLong()).isEqualTo(234L);
    }

    @Test
    void test_getAmountMinorLong_negative() {
        assertThat(GBP_M5_78.getAmountMinorLong()).isEqualTo(-578L);
    }

    @Test
    void test_getAmountMinorLong_tooBigPositive() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_LONG_MAX_PLUS1.getAmountMinorLong());
    }

    @Test
    void test_getAmountMinorLong_tooBigNegative() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_LONG_MIN_MINUS1.getAmountMinorLong());
    }

    //-----------------------------------------------------------------------
    // getAmountMinorInt()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMinorInt_positive() {
        assertThat(GBP_2_34.getAmountMinorInt()).isEqualTo(234);
    }

    @Test
    void test_getAmountMinorInt_negative() {
        assertThat(GBP_M5_78.getAmountMinorInt()).isEqualTo(-578);
    }

    @Test
    void test_getAmountMinorInt_tooBigPositive() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_INT_MAX_PLUS1.getAmountMinorInt());
    }

    @Test
    void test_getAmountMinorInt_tooBigNegative() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_INT_MIN_MINUS1.getAmountMinorInt());
    }

    //-----------------------------------------------------------------------
    // getMinorPart()
    //-----------------------------------------------------------------------
    @Test
    void test_getMinorPart_positive() {
        assertThat(GBP_2_34.getMinorPart()).isEqualTo(34);
    }

    @Test
    void test_getMinorPart_negative() {
        assertThat(GBP_M5_78.getMinorPart()).isEqualTo(-78);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    @Test
    void test_isZero() {
        assertThat(GBP_0_00.isZero()).isTrue();
        assertThat(GBP_2_34.isZero()).isFalse();
        assertThat(GBP_M5_78.isZero()).isFalse();
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    @Test
    void test_isPositive() {
        assertThat(GBP_0_00.isPositive()).isFalse();
        assertThat(GBP_2_34.isPositive()).isTrue();
        assertThat(GBP_M5_78.isPositive()).isFalse();
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero()
    //-----------------------------------------------------------------------
    @Test
    void test_isPositiveOrZero() {
        assertThat(GBP_0_00.isPositiveOrZero()).isTrue();
        assertThat(GBP_2_34.isPositiveOrZero()).isTrue();
        assertThat(GBP_M5_78.isPositiveOrZero()).isFalse();
    }

    //-----------------------------------------------------------------------
    // isNegative()
    //-----------------------------------------------------------------------
    @Test
    void test_isNegative() {
        assertThat(GBP_0_00.isNegative()).isFalse();
        assertThat(GBP_2_34.isNegative()).isFalse();
        assertThat(GBP_M5_78.isNegative()).isTrue();
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero()
    //-----------------------------------------------------------------------
    @Test
    void test_isNegativeOrZero() {
        assertThat(GBP_0_00.isNegativeOrZero()).isTrue();
        assertThat(GBP_2_34.isNegativeOrZero()).isFalse();
        assertThat(GBP_M5_78.isNegativeOrZero()).isTrue();
    }

    //-----------------------------------------------------------------------
    // withAmount(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_withAmount_BigDecimal() {
        var test = GBP_2_34.withAmount(BIGDEC_M5_78);
        assertThat(test).hasToString("GBP -5.78");
    }

    @Test
    void test_withAmount_BigDecimal_same() {
        var test = GBP_2_34.withAmount(BIGDEC_2_34);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_withAmount_BigDecimal_invalidScale() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.withAmount(new BigDecimal("2.345")));
    }

    @Test
    void test_withAmount_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.withAmount((BigDecimal) null));
    }

    //-----------------------------------------------------------------------
    // withAmount(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_withAmount_BigDecimalRoundingMode() {
        var test = GBP_2_34.withAmount(BIGDEC_M5_78, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP -5.78");
    }

    @Test
    void test_withAmount_BigDecimalRoundingMode_same() {
        var test = GBP_2_34.withAmount(BIGDEC_2_34, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_withAmount_BigDecimalRoundingMode_roundDown() {
        var test = GBP_2_34.withAmount(new BigDecimal("2.355"), RoundingMode.DOWN);
        assertThat(test).isEqualTo(GBP_2_35);
    }

    @Test
    void test_withAmount_BigDecimalRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.withAmount(new BigDecimal("2.345"), RoundingMode.UNNECESSARY));
    }

    @Test
    void test_withAmount_BigDecimalRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.withAmount((BigDecimal) null, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_withAmount_BigDecimalRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.withAmount(BIGDEC_2_34, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // withAmount(double)
    //-----------------------------------------------------------------------
    @Test
    void test_withAmount_double() {
        var test = GBP_2_34.withAmount(-5.78d);
        assertThat(test).hasToString("GBP -5.78");
    }

    @Test
    void test_withAmount_double_same() {
        var test = GBP_2_34.withAmount(2.34d);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_withAmount_double_invalidScale() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.withAmount(2.345d));
    }

    //-----------------------------------------------------------------------
    // withAmount(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_withAmount_doubleRoundingMode() {
        var test = GBP_2_34.withAmount(-5.78d, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP -5.78");
    }

    @Test
    void test_withAmount_doubleRoundingMode_same() {
        var test = GBP_2_34.withAmount(2.34d, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_withAmount_doubleRoundingMode_roundDown() {
        var test = GBP_2_34.withAmount(2.355d, RoundingMode.DOWN);
        assertThat(test).isEqualTo(GBP_2_35);
    }

    @Test
    void test_withAmount_doubleRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.withAmount(2.345d, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_withAmount_doubleRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.withAmount(BIGDEC_2_34, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // plus(Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_Iterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, GBP_1_23);
        var test = GBP_2_34.plus(iterable);
        assertThat(test).hasToString("GBP 5.90");
    }

    @Test
    void test_plus_Iterable_zero() {
        Iterable<Money> iterable = Arrays.asList(GBP_0_00);
        var test = GBP_2_34.plus(iterable);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plus_Iterable_currencyMismatch() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
                    GBP_M5_78.plus(iterable);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_plus_Iterable_nullEntry() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus(iterable));
    }

    @Test
    void test_plus_Iterable_nullIterable() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus((Iterable<Money>) null));
    }

    //-----------------------------------------------------------------------
    // plus(Money)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_Money_zero() {
        var test = GBP_2_34.plus(GBP_0_00);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plus_Money_positive() {
        var test = GBP_2_34.plus(GBP_1_23);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plus_Money_negative() {
        var test = GBP_2_34.plus(GBP_M1_23);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_plus_Money_currencyMismatch() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    GBP_M5_78.plus(USD_1_23);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(USD, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_plus_Money_nullMoney() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus((Money) null));
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_BigDecimal_zero() {
        var test = GBP_2_34.plus(BigDecimal.ZERO);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plus_BigDecimal_positive() {
        var test = GBP_2_34.plus(new BigDecimal("1.23"));
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plus_BigDecimal_negative() {
        var test = GBP_2_34.plus(new BigDecimal("-1.23"));
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_plus_BigDecimal_invalidScale() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.plus(new BigDecimal("1.235")));
    }

    @Test
    void test_plus_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus((BigDecimal) null));
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_BigDecimalRoundingMode_zero() {
        var test = GBP_2_34.plus(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plus_BigDecimalRoundingMode_positive() {
        var test = GBP_2_34.plus(new BigDecimal("1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plus_BigDecimalRoundingMode_negative() {
        var test = GBP_2_34.plus(new BigDecimal("-1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_plus_BigDecimalRoundingMode_roundDown() {
        var test = GBP_2_34.plus(new BigDecimal("1.235"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plus_BigDecimalRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.plus(new BigDecimal("1.235"), RoundingMode.UNNECESSARY));
    }

    @Test
    void test_plus_BigDecimalRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus((BigDecimal) null, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_plus_BigDecimalRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus(BIGDEC_2_34, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // plus(double)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_double_zero() {
        var test = GBP_2_34.plus(0d);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plus_double_positive() {
        var test = GBP_2_34.plus(1.23d);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plus_double_negative() {
        var test = GBP_2_34.plus(-1.23d);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_plus_double_invalidScale() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.plus(1.235d));
    }

    //-----------------------------------------------------------------------
    // plus(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_doubleRoundingMode_zero() {
        var test = GBP_2_34.plus(0d, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plus_doubleRoundingMode_positive() {
        var test = GBP_2_34.plus(1.23d, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plus_doubleRoundingMode_negative() {
        var test = GBP_2_34.plus(-1.23d, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_plus_doubleRoundingMode_roundDown() {
        var test = GBP_2_34.plus(1.235d, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plus_doubleRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.plus(1.235d, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_plus_doubleRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus(2.34d, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // plusMajor(long)
    //-----------------------------------------------------------------------
    @Test
    void test_plusMajor_zero() {
        var test = GBP_2_34.plusMajor(0);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plusMajor_positive() {
        var test = GBP_2_34.plusMajor(123);
        assertThat(test).hasToString("GBP 125.34");
    }

    @Test
    void test_plusMajor_negative() {
        var test = GBP_2_34.plusMajor(-123);
        assertThat(test).hasToString("GBP -120.66");
    }

    //-----------------------------------------------------------------------
    // plusMinor(long)
    //-----------------------------------------------------------------------
    @Test
    void test_plusMinor_zero() {
        var test = GBP_2_34.plusMinor(0);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plusMinor_positive() {
        var test = GBP_2_34.plusMinor(123);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plusMinor_negative() {
        var test = GBP_2_34.plusMinor(-123);
        assertThat(test).hasToString("GBP 1.11");
    }

    //-----------------------------------------------------------------------
    // minus(Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_Iterable() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, GBP_1_23);
        var test = GBP_2_34.minus(iterable);
        assertThat(test).hasToString("GBP -1.22");
    }

    @Test
    void test_minus_Iterable_zero() {
        Iterable<Money> iterable = Arrays.asList(GBP_0_00);
        var test = GBP_2_34.minus(iterable);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minus_Iterable_currencyMismatch() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    Iterable<Money> iterable = Arrays.asList(GBP_2_33, JPY_423);
                    GBP_M5_78.minus(iterable);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(JPY, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_minus_Iterable_nullEntry() {
        Iterable<Money> iterable = Arrays.asList(GBP_2_33, null);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus(iterable));
    }

    @Test
    void test_minus_Iterable_nullIterable() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus((Iterable<Money>) null));
    }

    //-----------------------------------------------------------------------
    // minus(Money)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_Money_zero() {
        var test = GBP_2_34.minus(GBP_0_00);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minus_Money_positive() {
        var test = GBP_2_34.minus(GBP_1_23);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_minus_Money_negative() {
        var test = GBP_2_34.minus(GBP_M1_23);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_minus_Money_currencyMismatch() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> {
                try {
                    GBP_M5_78.minus(USD_1_23);
                } catch (CurrencyMismatchException ex) {
                    assertEquals(GBP, ex.getFirstCurrency());
                    assertEquals(USD, ex.getSecondCurrency());
                    throw ex;
                }
            });
    }

    @Test
    void test_minus_Money_nullMoney() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus((Money) null));
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_BigDecimal_zero() {
        var test = GBP_2_34.minus(BigDecimal.ZERO);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minus_BigDecimal_positive() {
        var test = GBP_2_34.minus(new BigDecimal("1.23"));
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_minus_BigDecimal_negative() {
        var test = GBP_2_34.minus(new BigDecimal("-1.23"));
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_minus_BigDecimal_invalidScale() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.minus(new BigDecimal("1.235")));
    }

    @Test
    void test_minus_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus((BigDecimal) null));
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_BigDecimalRoundingMode_zero() {
        var test = GBP_2_34.minus(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minus_BigDecimalRoundingMode_positive() {
        var test = GBP_2_34.minus(new BigDecimal("1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_minus_BigDecimalRoundingMode_negative() {
        var test = GBP_2_34.minus(new BigDecimal("-1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_minus_BigDecimalRoundingMode_roundDown() {
        var test = GBP_2_34.minus(new BigDecimal("1.235"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 1.10");
    }

    @Test
    void test_minus_BigDecimalRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.minus(new BigDecimal("1.235"), RoundingMode.UNNECESSARY));
    }

    @Test
    void test_minus_BigDecimalRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus((BigDecimal) null, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_minus_BigDecimalRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus(BIGDEC_2_34, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // minus(double)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_double_zero() {
        var test = GBP_2_34.minus(0d);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minus_double_positive() {
        var test = GBP_2_34.minus(1.23d);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_minus_double_negative() {
        var test = GBP_2_34.minus(-1.23d);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_minus_double_invalidScale() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.minus(1.235d));
    }

    //-----------------------------------------------------------------------
    // minus(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_doubleRoundingMode_zero() {
        var test = GBP_2_34.minus(0d, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minus_doubleRoundingMode_positive() {
        var test = GBP_2_34.minus(1.23d, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_minus_doubleRoundingMode_negative() {
        var test = GBP_2_34.minus(-1.23d, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_minus_doubleRoundingMode_roundDown() {
        var test = GBP_2_34.minus(1.235d, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 1.10");
    }

    @Test
    void test_minus_doubleRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.minus(1.235d, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_minus_doubleRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus(2.34d, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // minusMajor(long)
    //-----------------------------------------------------------------------
    @Test
    void test_minusMajor_zero() {
        var test = GBP_2_34.minusMajor(0);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minusMajor_positive() {
        var test = GBP_2_34.minusMajor(123);
        assertThat(test).hasToString("GBP -120.66");
    }

    @Test
    void test_minusMajor_negative() {
        var test = GBP_2_34.minusMajor(-123);
        assertThat(test).hasToString("GBP 125.34");
    }

    //-----------------------------------------------------------------------
    // minusMinor(long)
    //-----------------------------------------------------------------------
    @Test
    void test_minusMinor_zero() {
        var test = GBP_2_34.minusMinor(0);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minusMinor_positive() {
        var test = GBP_2_34.minusMinor(123);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_minusMinor_negative() {
        var test = GBP_2_34.minusMinor(-123);
        assertThat(test).hasToString("GBP 3.57");
    }

    //-----------------------------------------------------------------------
    // multipliedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_multipliedBy_BigDecimalRoundingMode_one() {
        var test = GBP_2_34.multipliedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_multipliedBy_BigDecimalRoundingMode_positive() {
        var test = GBP_2_33.multipliedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 5.82");
    }

    @Test
    void test_multipliedBy_BigDecimalRoundingMode_positive_halfUp() {
        var test = GBP_2_33.multipliedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertThat(test).hasToString("GBP 5.83");
    }

    @Test
    void test_multipliedBy_BigDecimalRoundingMode_negative() {
        var test = GBP_2_33.multipliedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
        assertThat(test).hasToString("GBP -5.83");
    }

    @Test
    void test_multipliedBy_BigDecimalRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.multipliedBy((BigDecimal) null, RoundingMode.DOWN));
    }

    @Test
    void test_multipliedBy_BigDecimalRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.multipliedBy(new BigDecimal("2.5"), (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // multipliedBy(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_multipliedBy_doubleRoundingMode_one() {
        var test = GBP_2_34.multipliedBy(1d, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_multipliedBy_doubleRoundingMode_positive() {
        var test = GBP_2_33.multipliedBy(2.5d, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 5.82");
    }

    @Test
    void test_multipliedBy_doubleRoundingMode_positive_halfUp() {
        var test = GBP_2_33.multipliedBy(2.5d, RoundingMode.HALF_UP);
        assertThat(test).hasToString("GBP 5.83");
    }

    @Test
    void test_multipliedBy_doubleRoundingMode_negative() {
        var test = GBP_2_33.multipliedBy(-2.5d, RoundingMode.FLOOR);
        assertThat(test).hasToString("GBP -5.83");
    }

    @Test
    void test_multipliedBy_doubleRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.multipliedBy(2.5d, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // multipliedBy(long)
    //-----------------------------------------------------------------------
    @Test
    void test_multipliedBy_long_one() {
        var test = GBP_2_34.multipliedBy(1);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_multipliedBy_long_positive() {
        var test = GBP_2_34.multipliedBy(3);
        assertThat(test).hasToString("GBP 7.02");
    }

    @Test
    void test_multipliedBy_long_negative() {
        var test = GBP_2_34.multipliedBy(-3);
        assertThat(test).hasToString("GBP -7.02");
    }

    //-----------------------------------------------------------------------
    // dividedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_dividedBy_BigDecimalRoundingMode_one() {
        var test = GBP_2_34.dividedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_positive() {
        var test = GBP_2_34.dividedBy(new BigDecimal("2.5"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 0.93");
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_positive_halfUp() {
        var test = GBP_2_34.dividedBy(new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertThat(test).hasToString("GBP 0.94");
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_negative() {
        var test = GBP_2_34.dividedBy(new BigDecimal("-2.5"), RoundingMode.FLOOR);
        assertThat(test).hasToString("GBP -0.94");
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.dividedBy((BigDecimal) null, RoundingMode.DOWN));
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.dividedBy(new BigDecimal("2.5"), (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // dividedBy(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_dividedBy_doubleRoundingMode_one() {
        var test = GBP_2_34.dividedBy(1d, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_dividedBy_doubleRoundingMode_positive() {
        var test = GBP_2_34.dividedBy(2.5d, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 0.93");
    }

    @Test
    void test_dividedBy_doubleRoundingMode_positive_halfUp() {
        var test = GBP_2_34.dividedBy(2.5d, RoundingMode.HALF_UP);
        assertThat(test).hasToString("GBP 0.94");
    }

    @Test
    void test_dividedBy_doubleRoundingMode_negative() {
        var test = GBP_2_34.dividedBy(-2.5d, RoundingMode.FLOOR);
        assertThat(test).hasToString("GBP -0.94");
    }

    @Test
    void test_dividedBy_doubleRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.dividedBy(2.5d, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // dividedBy(long,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_dividedBy_long_one() {
        var test = GBP_2_34.dividedBy(1, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_dividedBy_long_positive() {
        var test = GBP_2_34.dividedBy(3, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 0.78");
    }

    @Test
    void test_dividedBy_long_positive_roundDown() {
        var test = GBP_2_35.dividedBy(3, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 0.78");
    }

    @Test
    void test_dividedBy_long_positive_roundUp() {
        var test = GBP_2_35.dividedBy(3, RoundingMode.UP);
        assertThat(test).hasToString("GBP 0.79");
    }

    @Test
    void test_dividedBy_long_negative() {
        var test = GBP_2_34.dividedBy(-3, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP -0.78");
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    @Test
    void test_negated_positive() {
        var test = GBP_2_34.negated();
        assertThat(test).hasToString("GBP -2.34");
    }

    @Test
    void test_negated_negative() {
        var test = Money.parse("GBP -2.34").negated();
        assertThat(test).hasToString("GBP 2.34");
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    @Test
    void test_abs_positive() {
        var test = GBP_2_34.abs();
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_abs_negative() {
        var test = Money.parse("GBP -2.34").abs();
        assertThat(test).hasToString("GBP 2.34");
    }

    //-----------------------------------------------------------------------
    // rounded()
    //-----------------------------------------------------------------------
    @Test
    void test_round_2down() {
        var test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_round_2up() {
        var test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_round_1down() {
        var test = GBP_2_34.rounded(1, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 2.30");
    }

    @Test
    void test_round_1up() {
        var test = GBP_2_34.rounded(1, RoundingMode.UP);
        assertThat(test).hasToString("GBP 2.40");
    }

    @Test
    void test_round_0down() {
        var test = GBP_2_34.rounded(0, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 2.00");
    }

    @Test
    void test_round_0up() {
        var test = GBP_2_34.rounded(0, RoundingMode.UP);
        assertThat(test).hasToString("GBP 3.00");
    }

    @Test
    void test_round_M1down() {
        var test = Money.parse("GBP 432.34").rounded(-1, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 430.00");
    }

    @Test
    void test_round_M1up() {
        var test = Money.parse("GBP 432.34").rounded(-1, RoundingMode.UP);
        assertThat(test).hasToString("GBP 440.00");
    }

    @Test
    void test_round_3() {
        var test = GBP_2_34.rounded(3, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    //-----------------------------------------------------------------------
    // convertedTo(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_convertedTo_BigDecimalRoundingMode_positive() {
        var test = GBP_2_33.convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.DOWN);
        assertThat(test).hasToString("EUR 5.82");
    }

    @Test
    void test_convertedTo_BigDecimalRoundingMode_positive_halfUp() {
        var test = GBP_2_33.convertedTo(EUR, new BigDecimal("2.5"), RoundingMode.HALF_UP);
        assertThat(test).hasToString("EUR 5.83");
    }

    @Test
    void test_convertedTo_BigDecimalRoundingMode_negative() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> GBP_2_33.convertedTo(EUR, new BigDecimal("-2.5"), RoundingMode.FLOOR));
    }

    @Test
    void test_convertedTo_BigDecimalRoundingMode_sameCurrency() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> GBP_2_33.convertedTo(GBP, new BigDecimal("2.5"), RoundingMode.DOWN));
    }

    @Test
    void test_convertedTo_BigDecimalRoundingMode_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.convertedTo((CurrencyUnit) null, new BigDecimal("2"), RoundingMode.DOWN));
    }

    @Test
    void test_convertedTo_BigDecimalRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.convertedTo(EUR, (BigDecimal) null, RoundingMode.DOWN));
    }

    @Test
    void test_convertedTo_BigDecimalRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.convertedTo(EUR, new BigDecimal("2.5"), (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    @Test
    void test_toBigMoney() {
        assertThat(GBP_2_34.toBigMoney()).isEqualTo(BigMoney.ofMinor(GBP, 234));
    }

    //-----------------------------------------------------------------------
    // isSameCurrency(Money)
    //-----------------------------------------------------------------------
    @Test
    void test_isSameCurrency_Money_same() {
        assertThat(GBP_2_34.isSameCurrency(GBP_2_35)).isTrue();
    }

    @Test
    void test_isSameCurrency_Money_different() {
        assertThat(GBP_2_34.isSameCurrency(USD_2_34)).isFalse();
    }

    @Test
    void test_isSameCurrency_BigMoney_same() {
        assertThat(GBP_2_34.isSameCurrency(BigMoney.parse("GBP 2"))).isTrue();
    }

    @Test
    void test_isSameCurrency_BigMoney_different() {
        assertThat(GBP_2_34.isSameCurrency(BigMoney.parse("USD 2"))).isFalse();
    }

    @Test
    void test_isSameCurrency_Money_nullMoney() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.isSameCurrency((Money) null));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    void test_compareTo_Money() {
        var a = GBP_2_34;
        var b = GBP_2_35;
        var c = GBP_2_36;
        assertThat(a.compareTo(a)).isEqualTo(0);
        assertThat(b.compareTo(b)).isEqualTo(0);
        assertThat(c.compareTo(c)).isEqualTo(0);

        assertThat(a.compareTo(b)).isEqualTo(-1);
        assertThat(b.compareTo(a)).isEqualTo(1);

        assertThat(a.compareTo(c)).isEqualTo(-1);
        assertThat(c.compareTo(a)).isEqualTo(1);

        assertThat(b.compareTo(c)).isEqualTo(-1);
        assertThat(c.compareTo(b)).isEqualTo(1);
    }

    @Test
    void test_compareTo_BigMoney() {
        var t = GBP_2_35;
        var a = BigMoney.ofMinor(GBP, 234);
        var b = BigMoney.ofMinor(GBP, 235);
        var c = BigMoney.ofMinor(GBP, 236);
        assertThat(t.compareTo(a)).isEqualTo(1);
        assertThat(t.compareTo(b)).isEqualTo(0);
        assertThat(t.compareTo(c)).isEqualTo(-1);
    }

    @Test
    void test_compareTo_currenciesDiffer() {
        var a = GBP_2_34;
        var b = USD_2_35;
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> a.compareTo(b));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void test_compareTo_wrongType() {
        Comparable a = GBP_2_34;
        assertThatExceptionOfType(ClassCastException.class)
            .isThrownBy(() -> a.compareTo("NotRightType"));
    }

    //-----------------------------------------------------------------------
    // isEqual()
    //-----------------------------------------------------------------------
    @Test
    void test_isEqual() {
        var a = GBP_2_34;
        var b = GBP_2_35;
        var c = GBP_2_36;
        assertThat(a.isEqual(a)).isTrue();
        assertThat(b.isEqual(b)).isTrue();
        assertThat(c.isEqual(c)).isTrue();

        assertThat(a.isEqual(b)).isFalse();
        assertThat(b.isEqual(a)).isFalse();

        assertThat(a.isEqual(c)).isFalse();
        assertThat(c.isEqual(a)).isFalse();

        assertThat(b.isEqual(c)).isFalse();
        assertThat(c.isEqual(b)).isFalse();
    }

    @Test
    void test_isEqual_Money() {
        var a = GBP_2_34;
        var b = BigMoney.ofMinor(GBP, 234);
        assertThat(a.isEqual(b)).isTrue();
    }

    @Test
    void test_isEqual_currenciesDiffer() {
        var a = GBP_2_34;
        var b = USD_2_35;
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> a.isEqual(b));
    }

    //-----------------------------------------------------------------------
    // isGreaterThan()
    //-----------------------------------------------------------------------
    @Test
    void test_isGreaterThan() {
        var a = GBP_2_34;
        var b = GBP_2_35;
        var c = GBP_2_36;
        assertThat(a.isGreaterThan(a)).isFalse();
        assertThat(a.isGreaterThan(b)).isFalse();
        assertThat(a.isGreaterThan(c)).isFalse();

        assertThat(b.isGreaterThan(a)).isTrue();
        assertThat(b.isGreaterThan(b)).isFalse();
        assertThat(b.isGreaterThan(c)).isFalse();

        assertThat(c.isGreaterThan(a)).isTrue();
        assertThat(c.isGreaterThan(b)).isTrue();
        assertThat(c.isGreaterThan(c)).isFalse();
    }

    @Test
    void test_isGreaterThan_currenciesDiffer() {
        var a = GBP_2_34;
        var b = USD_2_35;
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> a.isGreaterThan(b));
    }

    //-----------------------------------------------------------------------
    // isGreaterThanOrEqual()
    //-----------------------------------------------------------------------
    @Test
    void test_isGreaterThanOrEqual() {
        var a = GBP_2_34;
        var b = GBP_2_35;
        var c = GBP_2_36;
        assertThat(a.isGreaterThanOrEqual(a)).isTrue();
        assertThat(a.isGreaterThanOrEqual(b)).isFalse();
        assertThat(a.isGreaterThanOrEqual(c)).isFalse();

        assertThat(b.isGreaterThanOrEqual(a)).isTrue();
        assertThat(b.isGreaterThanOrEqual(b)).isTrue();
        assertThat(b.isGreaterThanOrEqual(c)).isFalse();

        assertThat(c.isGreaterThanOrEqual(a)).isTrue();
        assertThat(c.isGreaterThanOrEqual(b)).isTrue();
        assertThat(c.isGreaterThanOrEqual(c)).isTrue();
    }

    @Test
    void test_isGreaterThanOrEqual_currenciesDiffer() {
        var a = GBP_2_34;
        var b = USD_2_35;
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> a.isGreaterThanOrEqual(b));
    }

    //-----------------------------------------------------------------------
    // isLessThan()
    //-----------------------------------------------------------------------
    @Test
    void test_isLessThan() {
        var a = GBP_2_34;
        var b = GBP_2_35;
        var c = GBP_2_36;
        assertThat(a.isLessThan(a)).isFalse();
        assertThat(a.isLessThan(b)).isTrue();
        assertThat(a.isLessThan(c)).isTrue();

        assertThat(b.isLessThan(a)).isFalse();
        assertThat(b.isLessThan(b)).isFalse();
        assertThat(b.isLessThan(c)).isTrue();

        assertThat(c.isLessThan(a)).isFalse();
        assertThat(c.isLessThan(b)).isFalse();
        assertThat(c.isLessThan(c)).isFalse();
    }

    @Test
    void test_isLessThan_currenciesDiffer() {
        var a = GBP_2_34;
        var b = USD_2_35;
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> a.isLessThan(b));
    }

    //-----------------------------------------------------------------------
    // isLessThanOrEqual()
    //-----------------------------------------------------------------------
    @Test
    void test_isLessThanOrEqual() {
        var a = GBP_2_34;
        var b = GBP_2_35;
        var c = GBP_2_36;
        assertThat(a.isLessThanOrEqual(a)).isTrue();
        assertThat(a.isLessThanOrEqual(b)).isTrue();
        assertThat(a.isLessThanOrEqual(c)).isTrue();

        assertThat(b.isLessThanOrEqual(a)).isFalse();
        assertThat(b.isLessThanOrEqual(b)).isTrue();
        assertThat(b.isLessThanOrEqual(c)).isTrue();

        assertThat(c.isLessThanOrEqual(a)).isFalse();
        assertThat(c.isLessThanOrEqual(b)).isFalse();
        assertThat(c.isLessThanOrEqual(c)).isTrue();
    }

    @Test
    void test_isLessThanOrEqual_currenciesDiffer() {
        var a = GBP_2_34;
        var b = USD_2_35;
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> a.isLessThanOrEqual(b));
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    @Test
    void test_equals_hashCode_positive() {
        var a = GBP_2_34;
        var b = GBP_2_34;
        var c = GBP_2_35;
        assertThat(a).isEqualTo(a);
        assertThat(b).isEqualTo(b);
        assertThat(c).isEqualTo(c);

        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(b.hashCode()).isEqualTo(a.hashCode());

        assertThat(c).isNotEqualTo(a);
        assertThat(c).isNotEqualTo(b);
    }

    @Test
    void test_equals_false() {
        var a = GBP_2_34;
        assertThat(a).isNotEqualTo(null);
        assertThat(new Object()).isNotEqualTo(a);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    void test_toString_positive() {
        var test = Money.of(GBP, BIGDEC_2_34);
        assertThat(test).hasToString("GBP 2.34");
    }

    @Test
    void test_toString_negative() {
        var test = Money.of(EUR, BIGDEC_M5_78);
        assertThat(test).hasToString("EUR -5.78");
    }

}
