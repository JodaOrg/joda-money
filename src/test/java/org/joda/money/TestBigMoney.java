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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test BigMoney.
 */
class TestBigMoney {

    private static final CurrencyUnit GBP = CurrencyUnit.of("GBP");
    private static final CurrencyUnit EUR = CurrencyUnit.of("EUR");
    private static final CurrencyUnit USD = CurrencyUnit.of("USD");
    private static final CurrencyUnit JPY = CurrencyUnit.of("JPY");
    private static final BigDecimal BIGDEC_2_34 = new BigDecimal("2.34");
    private static final BigDecimal BIGDEC_2_345 = new BigDecimal("2.345");
    private static final BigDecimal BIGDEC_M5_78 = new BigDecimal("-5.78");

    private static final BigMoney GBP_0_00 = BigMoney.parse("GBP 0.00");
    private static final BigMoney GBP_1_23 = BigMoney.parse("GBP 1.23");
    private static final BigMoney GBP_2_33 = BigMoney.parse("GBP 2.33");
    private static final BigMoney GBP_2_34 = BigMoney.parse("GBP 2.34");
    private static final BigMoney GBP_2_35 = BigMoney.parse("GBP 2.35");
    private static final BigMoney GBP_2_36 = BigMoney.parse("GBP 2.36");
    private static final BigMoney GBP_5_78 = BigMoney.parse("GBP 5.78");
    private static final BigMoney GBP_M1_23 = BigMoney.parse("GBP -1.23");
    private static final BigMoney GBP_M5_78 = BigMoney.parse("GBP -5.78");
    private static final BigMoney GBP_INT_MAX_PLUS1 = BigMoney.ofMinor(GBP, ((long) Integer.MAX_VALUE) + 1);
    private static final BigMoney GBP_INT_MIN_MINUS1 = BigMoney.ofMinor(GBP, ((long) Integer.MIN_VALUE) - 1);
    private static final BigMoney GBP_INT_MAX_MAJOR_PLUS1 = BigMoney.ofMinor(GBP, (((long) Integer.MAX_VALUE) + 1) * 100);
    private static final BigMoney GBP_INT_MIN_MAJOR_MINUS1 = BigMoney.ofMinor(GBP, (((long) Integer.MIN_VALUE) - 1) * 100);
    private static final BigMoney GBP_LONG_MAX_PLUS1 = BigMoney.of(GBP, BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE));
    private static final BigMoney GBP_LONG_MIN_MINUS1 = BigMoney.of(GBP, BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE));
    private static final BigMoney GBP_LONG_MAX_MAJOR_PLUS1 = BigMoney.of(
            GBP,
            BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
    private static final BigMoney GBP_LONG_MIN_MAJOR_MINUS1 = BigMoney.of(
            GBP,
            BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
    private static final BigMoney JPY_423 = BigMoney.parse("JPY 423");
    private static final BigMoney USD_1_23 = BigMoney.parse("USD 1.23");
    private static final BigMoney USD_2_34 = BigMoney.parse("USD 2.34");
    private static final BigMoney USD_2_35 = BigMoney.parse("USD 2.35");
    private static final BigMoneyProvider BAD_PROVIDER = () -> null;

    private static BigDecimal bd(String str) {
        return new BigDecimal(str);
    }

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency_BigDecimal() {
        var test = BigMoney.of(GBP, BIGDEC_2_345);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BIGDEC_2_345);
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_factory_of_Currency_BigDecimal_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.of((CurrencyUnit) null, BIGDEC_2_345));
    }

    @Test
    void test_factory_of_Currency_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.of(GBP, (BigDecimal) null));
    }

    @Test
    void test_factory_of_Currency_subClass1() {
        class BadDecimal extends BigDecimal {
            private static final long serialVersionUID = 1L;

            BadDecimal() {
                super(432);
            }

            @Override
            public BigInteger unscaledValue() {
                return null;
            }

            @Override
            public int scale() {
                return 1;
            }
        }
        BigDecimal sub = new BadDecimal();
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> BigMoney.of(GBP, sub));
    }

    @Test
    void test_factory_of_Currency_subClass2() {
        class BadInteger extends BigInteger {
            private static final long serialVersionUID = 1L;

            public BadInteger() {
                super("123");
            }
        }
        class BadDecimal extends BigDecimal {
            private static final long serialVersionUID = 1L;

            BadDecimal() {
                super(432);
            }

            @Override
            public BigInteger unscaledValue() {
                return new BadInteger();
            }

            @Override
            public int scale() {
                return 1;
            }
        }
        BigDecimal sub = new BadDecimal();
        var test = BigMoney.of(GBP, sub);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(bd("12.3"));
        assertThat(test.getScale()).isEqualTo(1);
        assertThat(test.getAmount().getClass()).isEqualTo(BigDecimal.class);
    }

    //-----------------------------------------------------------------------
    // of(Currency,double)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency_double() {
        var test = BigMoney.of(GBP, 2.345d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BIGDEC_2_345);
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_factory_of_Currency_double_trailingZero1() {
        var test = BigMoney.of(GBP, 1.230d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(123L, 2));
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_factory_of_Currency_double_trailingZero2() {
        var test = BigMoney.of(GBP, 1.20d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(12L, 1));
        assertThat(test.getScale()).isEqualTo(1);
    }

    @Test
    void test_factory_of_Currency_double_zero() {
        assertThat(BigMoney.of(GBP, 0d)).isEqualTo(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)));
        assertThat(BigMoney.of(GBP, -0d)).isEqualTo(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)));
        assertThat(BigMoney.of(GBP, 0.0d)).isEqualTo(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)));
        assertThat(BigMoney.of(GBP, 0.00d)).isEqualTo(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)));
        assertThat(BigMoney.of(GBP, -0.0d)).isEqualTo(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)));
    }

    @Test
    void test_factory_of_Currency_double_medium() {
        var test = BigMoney.of(GBP, 2000d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(2000L, 0));
        assertThat(test.getScale()).isEqualTo(0);
    }

    @Test
    void test_factory_of_Currency_double_big() {
        var test = BigMoney.of(GBP, 200000000d);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(200000000L, 0));
        assertThat(test.getScale()).isEqualTo(0);
    }

    @Test
    void test_factory_of_Currency_double_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.of((CurrencyUnit) null, 2.345d));
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,BigDecimal, int)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofScale_Currency_BigDecimal_int() {
        var test = BigMoney.ofScale(GBP, BIGDEC_2_34, 4);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(23400, 4));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_negativeScale() {
        var test = BigMoney.ofScale(GBP, BigDecimal.valueOf(23400), -2);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(23400L, 0));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_invalidScale() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> BigMoney.ofScale(GBP, BIGDEC_2_345, 2));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.ofScale((CurrencyUnit) null, BIGDEC_2_34, 2));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.ofScale(GBP, (BigDecimal) null, 2));
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,BigDecimal,int,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_DOWN() {
        var test = BigMoney.ofScale(GBP, BIGDEC_2_34, 1, RoundingMode.DOWN);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(23, 1));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_JPY_RoundingMode_UP() {
        var test = BigMoney.ofScale(JPY, BIGDEC_2_34, 0, RoundingMode.UP);
        assertThat(test.getCurrencyUnit()).isEqualTo(JPY);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(3, 0));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_negativeScale() {
        var test = BigMoney.ofScale(GBP, BigDecimal.valueOf(23400), -2, RoundingMode.DOWN);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(23400L, 0));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_UNNECESSARY() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> BigMoney.ofScale(JPY, BIGDEC_2_34, 1, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.ofScale((CurrencyUnit) null, BIGDEC_2_34, 2, RoundingMode.DOWN));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.ofScale(GBP, (BigDecimal) null, 2, RoundingMode.DOWN));
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.ofScale(GBP, BIGDEC_2_34, 2, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,long, int)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofScale_Currency_long_int() {
        var test = BigMoney.ofScale(GBP, 234, 4);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(234, 4));
    }

    @Test
    void test_factory_ofScale_Currency_long_int_negativeScale() {
        var test = BigMoney.ofScale(GBP, 234, -4);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(2340000, 0));
    }

    @Test
    void test_factory_ofScale_Currency_long_int_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.ofScale((CurrencyUnit) null, 234, 2));
    }

    //-----------------------------------------------------------------------
    // ofMajor(Currency,long)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofMajor_Currency_long() {
        var test = BigMoney.ofMajor(GBP, 234);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(bd("234"));
        assertThat(test.getScale()).isEqualTo(0);
    }

    @Test
    void test_factory_ofMajor_Currency_long_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.ofMajor((CurrencyUnit) null, 234));
    }

    //-----------------------------------------------------------------------
    // ofMinor(Currency,long)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofMinor_Currency_long() {
        var test = BigMoney.ofMinor(GBP, 234);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(bd("2.34"));
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_factory_ofMinor_Currency_long_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.ofMinor((CurrencyUnit) null, 234));
    }

    //-----------------------------------------------------------------------
    // zero(Currency)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_zero_Currency() {
        var test = BigMoney.zero(GBP);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(test.getScale()).isEqualTo(0);
    }

    @Test
    void test_factory_zero_Currency_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.zero((CurrencyUnit) null));
    }

    //-----------------------------------------------------------------------
    // zero(Currency, int)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_zero_Currency_int() {
        var test = BigMoney.zero(GBP, 3);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(0, 3));
    }

    @Test
    void test_factory_zero_Currency_int_negativeScale() {
        var test = BigMoney.zero(GBP, -3);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(0, 0));
    }

    @Test
    void test_factory_zero_Currency_int_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.zero((CurrencyUnit) null, 3));
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_from_BigMoneyProvider() {
        var test = BigMoney.of(BigMoney.parse("GBP 104.23"));
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(10423);
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_factory_from_BigMoneyProvider_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.of((BigMoneyProvider) null));
    }

    @Test
    void test_factory_from_BigMoneyProvider_badProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.of(BAD_PROVIDER));
    }

    //-----------------------------------------------------------------------
    // total(BigMoneyProvider...)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_varargs_1BigMoney() {
        var test = BigMoney.total(GBP_1_23);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(123);
    }

    @Test
    void test_factory_total_array_1BigMoney() {
        var array = new BigMoneyProvider[] {GBP_1_23};
        var test = BigMoney.total(array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(123);
    }

    @Test
    void test_factory_total_varargs_3Mixed() {
        var test = BigMoney.total(GBP_1_23, GBP_2_33.toMoney(), GBP_2_36);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_array_3Mixed() {
        var array = new BigMoneyProvider[] {GBP_1_23, GBP_2_33.toMoney(), GBP_2_36};
        var test = BigMoney.total(array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_array_3Money() {
        var array = new Money[] {GBP_1_23.toMoney(), GBP_2_33.toMoney(), GBP_2_36.toMoney()};
        var test = BigMoney.total(array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_varargs_empty() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> BigMoney.total());
    }

    @Test
    void test_factory_total_array_empty() {
        var array = new BigMoneyProvider[0];
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> BigMoney.total(array));
    }

    @Test
    void test_factory_total_varargs_currenciesDiffer() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> BigMoney.total(GBP_2_33, JPY_423));
    }

    @Test
    void test_factory_total_array_currenciesDiffer() {
        var array = new BigMoneyProvider[] {GBP_2_33, JPY_423};
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> BigMoney.total(array));
    }

    @Test
    void test_factory_total_varargs_nullFirst() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total((BigMoney) null, GBP_2_33, GBP_2_36));
    }

    @Test
    void test_factory_total_array_nullFirst() {
        var array = new BigMoneyProvider[] {null, GBP_2_33, GBP_2_36};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(array));
    }

    @Test
    void test_factory_total_varargs_nullNotFirst() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP_2_33, null, GBP_2_36));
    }

    @Test
    void test_factory_total_array_nullNotFirst() {
        var array = new BigMoneyProvider[] {GBP_2_33, null, GBP_2_36};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(array));
    }

    @Test
    void test_factory_total_varargs_badProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(BAD_PROVIDER));
    }

    @Test
    void test_factory_total_array_badProvider() {
        var array = new BigMoneyProvider[] {BAD_PROVIDER};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(array));
    }

    //-----------------------------------------------------------------------
    // total(Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_Iterable() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_1_23, GBP_2_33, BigMoney.of(GBP, 2.361d));
        var test = BigMoney.total(iterable);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(5921, 3));
    }

    @Test
    void test_factory_total_Iterable_Mixed() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_1_23.toMoney(), GBP_2_33);
        var test = BigMoney.total(iterable);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(356, 2));
    }

    @Test
    void test_factory_total_Iterable_empty() {
        Iterable<BigMoney> iterable = Collections.emptyList();
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> BigMoney.total(iterable));
    }

    @Test
    void test_factory_total_Iterable_currenciesDiffer() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, JPY_423);
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> BigMoney.total(iterable));
    }

    @Test
    void test_factory_total_Iterable_nullFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(iterable));
    }

    @Test
    void test_factory_total_Iterable_nullNotFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(iterable));
    }

    @Test
    void test_factory_total_Iterable_badProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(BAD_PROVIDER);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(iterable));
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,BigMoneyProvider...)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_CurrencyUnitVarargs_1() {
        var test = BigMoney.total(GBP, GBP_1_23);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(123);
    }

    @Test
    void test_factory_total_CurrencyUnitArray_1() {
        var array = new BigMoney[] {GBP_1_23};
        var test = BigMoney.total(GBP, array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(123);
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_3() {
        var test = BigMoney.total(GBP, GBP_1_23, GBP_2_33, GBP_2_36);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_CurrencyUnitArray_3() {
        var array = new BigMoney[] {GBP_1_23, GBP_2_33, GBP_2_36};
        var test = BigMoney.total(GBP, array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_3Mixed() {
        var test = BigMoney.total(GBP, GBP_1_23, GBP_2_33.toMoney(), GBP_2_36);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_CurrencyUnitArray_3Mixed() {
        var array = new BigMoneyProvider[] {GBP_1_23, GBP_2_33.toMoney(), GBP_2_36};
        var test = BigMoney.total(GBP, array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_CurrencyUnitArray_3Money() {
        var array = new Money[] {GBP_1_23.toMoney(), GBP_2_33.toMoney(), GBP_2_36.toMoney()};
        var test = BigMoney.total(GBP, array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(592);
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_empty() {
        var test = BigMoney.total(GBP);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(0);
    }

    @Test
    void test_factory_total_CurrencyUnitArray_empty() {
        var array = new BigMoney[0];
        var test = BigMoney.total(GBP, array);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(0);
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_currenciesDiffer() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> BigMoney.total(GBP, JPY_423));
    }

    @Test
    void test_factory_total_CurrencyUnitArray_currenciesDiffer() {
        var array = new BigMoney[] {JPY_423};
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> BigMoney.total(GBP, array));
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_currenciesDifferInArray() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> BigMoney.total(GBP, GBP_2_33, JPY_423));
    }

    @Test
    void test_factory_total_CurrencyUnitArray_currenciesDifferInArray() {
        var array = new BigMoney[] {GBP_2_33, JPY_423};
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> BigMoney.total(GBP, array));
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_nullFirst() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP, null, GBP_2_33, GBP_2_36));
    }

    @Test
    void test_factory_total_CurrencyUnitArray_nullFirst() {
        var array = new BigMoney[] {null, GBP_2_33, GBP_2_36};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP, array));
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_nullNotFirst() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP, GBP_2_33, null, GBP_2_36));
    }

    @Test
    void test_factory_total_CurrencyUnitArray_nullNotFirst() {
        var array = new BigMoney[] {GBP_2_33, null, GBP_2_36};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP, array));
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_badProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP, BAD_PROVIDER));
    }

    @Test
    void test_factory_total_CurrencyUnitArray_badProvider() {
        var array = new BigMoneyProvider[] {BAD_PROVIDER};
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP, array));
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_CurrencyUnitIterable() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_1_23, GBP_2_33, BigMoney.of(GBP, 2.361d));
        var test = BigMoney.total(GBP, iterable);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(5921, 3));
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_Mixed() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_1_23.toMoney(), GBP_2_33);
        var test = BigMoney.total(GBP, iterable);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmount()).isEqualTo(BigDecimal.valueOf(356, 2));
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_empty() {
        Iterable<BigMoney> iterable = Collections.emptyList();
        var test = BigMoney.total(GBP, iterable);
        assertThat(test.getCurrencyUnit()).isEqualTo(GBP);
        assertThat(test.getAmountMinorInt()).isEqualTo(0);
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_currenciesDiffer() {
        Iterable<BigMoney> iterable = Arrays.asList(JPY_423);
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> BigMoney.total(GBP, iterable));
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_currenciesDifferInIterable() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, JPY_423);
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> BigMoney.total(GBP, iterable));
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_nullFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP, iterable));
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_nullNotFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP, iterable));
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_badProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(BAD_PROVIDER);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.total(GBP, iterable));
    }

    //-----------------------------------------------------------------------
    // parse(String)
    //-----------------------------------------------------------------------
    public static Object[][] data_parse() {
        return new Object[][] {
            {"GBP 2.43", GBP, "2.43", 2},
            {"GBP +12.57", GBP, "12.57", 2},
            {"GBP -5.87", GBP, "-5.87", 2},
            {"GBP 0.99", GBP, "0.99", 2},
            {"GBP .99", GBP, "0.99", 2},
            {"GBP +.99", GBP, "0.99", 2},
            {"GBP +0.99", GBP, "0.99", 2},
            {"GBP -.99", GBP, "-0.99", 2},
            {"GBP -0.99", GBP, "-0.99", 2},
            {"GBP 0", GBP, "0", 0},
            {"GBP 2", GBP, "2", 0},
            {"GBP 123.", GBP, "123", 0},
            {"GBP3", GBP, "3", 0},
            {"GBP3.10", GBP, "3.10", 2},
            {"GBP  3.10", GBP, "3.10", 2},
            {"GBP   3.10", GBP, "3.10", 2},
            {"GBP                           3.10", GBP, "3.10", 2},
            {"GBP 123.456789", GBP, "123.456789", 6},
        };
    }

    @ParameterizedTest
    @MethodSource("data_parse")
    void test_factory_parse(String str, CurrencyUnit currency, String amountStr, int scale) {
        var test = BigMoney.parse(str);
        assertThat(test.getCurrencyUnit()).isEqualTo(currency);
        assertThat(test.getAmount()).isEqualTo(bd(amountStr));
        assertThat(test.getScale()).isEqualTo(scale);
    }

    @Test
    void test_factory_parse_String_tooShort() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> BigMoney.parse("GBP"));
    }

    @Test
    void test_factory_parse_String_exponent() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> BigMoney.parse("GBP 234E2"));
    }

    @Test
    void test_factory_parse_String_badCurrency() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> BigMoney.parse("GBX 2.34"));
    }

    @Test
    void test_factory_parse_String_nullString() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> BigMoney.parse((String) null));
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    @Test
    void test_constructor_null1() throws Exception {
        var con = BigMoney.class.getDeclaredConstructor(CurrencyUnit.class, BigDecimal.class);
        assertThat(Modifier.isPublic(con.getModifiers())).isFalse();
        assertThat(Modifier.isProtected(con.getModifiers())).isFalse();
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] {null, BIGDEC_2_34});
            fail("");
        } catch (InvocationTargetException ex) {
            assertThat(ex.getCause().getClass()).isEqualTo(AssertionError.class);
        }
    }

    @Test
    void test_constructor_null2() throws Exception {
        var con = BigMoney.class.getDeclaredConstructor(CurrencyUnit.class, BigDecimal.class);
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] {GBP, null});
            fail("");
        } catch (InvocationTargetException ex) {
            assertThat(ex.getCause().getClass()).isEqualTo(AssertionError.class);
        }
    }

    //-----------------------------------------------------------------------
    @Test
    void test_scaleNormalization1() {
        var a = BigMoney.ofScale(GBP, 100, 0);
        var b = BigMoney.ofScale(GBP, 1, -2);
        assertThat(a.toString()).isEqualTo("GBP 100");
        assertThat(b.toString()).isEqualTo("GBP 100");
        assertThat(a).isEqualTo(a);
        assertThat(b).isEqualTo(b);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(b.hashCode()).isEqualTo(a.hashCode());
    }

    @Test
    void test_scaleNormalization2() {
        var a = BigMoney.ofScale(GBP, 1, 1);
        var b = BigMoney.ofScale(GBP, 10, 2);
        assertThat(a.toString()).isEqualTo("GBP 0.1");
        assertThat(b.toString()).isEqualTo("GBP 0.10");
        assertThat(a).isEqualTo(a);
        assertThat(b).isEqualTo(b);
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
        assertThat(a.hashCode() == b.hashCode()).isFalse();
    }

    @Test
    void test_scaleNormalization3() {
        var a = BigMoney.of(GBP, new BigDecimal("100"));
        var b = BigMoney.of(GBP, new BigDecimal("1E+2"));
        assertThat(a.toString()).isEqualTo("GBP 100");
        assertThat(b.toString()).isEqualTo("GBP 100");
        assertThat(a).isEqualTo(a);
        assertThat(b).isEqualTo(b);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(b.hashCode()).isEqualTo(a.hashCode());
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test
    void test_serialization() throws Exception {
        var a = BigMoney.parse("GBP 2.34");
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(a);
            oos.close();
            var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            var input = (BigMoney) ois.readObject();
            assertThat(input).isEqualTo(a);
        }
    }

    @Test
    void test_serialization_invalidNumericCode() throws IOException {
        var cu = new CurrencyUnit("GBP", (short) 234, (short) 2);
        var m = BigMoney.of(cu, 123.43d);
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
        var cu = new CurrencyUnit("GBP", (short) 826, (short) 1);
        var m = BigMoney.of(cu, 123.43d);
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
        assertThat(BigMoney.parse("EUR -5.78").getCurrencyUnit()).isEqualTo(EUR);
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
    void test_withCurrencyUnit_Currency_differentCurrencyScale() {
        var test = GBP_2_34.withCurrencyUnit(JPY);
        assertThat(test).hasToString("JPY 2.34");
    }

    @Test
    void test_withCurrencyUnit_Currency_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.withCurrencyUnit((CurrencyUnit) null));
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
    // isCurrencyScale()
    //-----------------------------------------------------------------------
    @Test
    void test_isCurrencyScale_GBP() {
        assertThat(BigMoney.parse("GBP 2").isCurrencyScale()).isFalse();
        assertThat(BigMoney.parse("GBP 2.3").isCurrencyScale()).isFalse();
        assertThat(BigMoney.parse("GBP 2.34").isCurrencyScale()).isTrue();
        assertThat(BigMoney.parse("GBP 2.345").isCurrencyScale()).isFalse();
    }

    @Test
    void test_isCurrencyScale_JPY() {
        assertThat(BigMoney.parse("JPY 2").isCurrencyScale()).isTrue();
        assertThat(BigMoney.parse("JPY 2.3").isCurrencyScale()).isFalse();
        assertThat(BigMoney.parse("JPY 2.34").isCurrencyScale()).isFalse();
        assertThat(BigMoney.parse("JPY 2.345").isCurrencyScale()).isFalse();
    }

    //-----------------------------------------------------------------------
    // withScale(int)
    //-----------------------------------------------------------------------
    @Test
    void test_withScale_int_same() {
        var test = GBP_2_34.withScale(2);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_withScale_int_more() {
        var test = GBP_2_34.withScale(3);
        assertThat(test.getAmount()).isEqualTo(bd("2.340"));
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_withScale_int_less() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> BigMoney.parse("GBP 2.345").withScale(2));
    }

    //-----------------------------------------------------------------------
    // withScale(int,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_withScale_intRoundingMode_less() {
        var test = GBP_2_34.withScale(1, RoundingMode.UP);
        assertThat(test.getAmount()).isEqualTo(bd("2.4"));
        assertThat(test.getScale()).isEqualTo(1);
    }

    @Test
    void test_withScale_intRoundingMode_more() {
        var test = GBP_2_34.withScale(3, RoundingMode.UP);
        assertThat(test.getAmount()).isEqualTo(bd("2.340"));
        assertThat(test.getScale()).isEqualTo(3);
    }

    //-----------------------------------------------------------------------
    // withCurrencyScale()
    //-----------------------------------------------------------------------
    @Test
    void test_withCurrencyScale_int_same() {
        var test = GBP_2_34.withCurrencyScale();
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_withCurrencyScale_int_more() {
        var test = BigMoney.parse("GBP 2.3").withCurrencyScale();
        assertThat(test.getAmount()).isEqualTo(bd("2.30"));
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_withCurrencyScale_int_less() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> BigMoney.parse("GBP 2.345").withCurrencyScale());
    }

    //-----------------------------------------------------------------------
    // withCurrencyScale(RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_withCurrencyScale_intRoundingMode_less() {
        var test = BigMoney.parse("GBP 2.345").withCurrencyScale(RoundingMode.UP);
        assertThat(test.getAmount()).isEqualTo(bd("2.35"));
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_withCurrencyScale_intRoundingMode_more() {
        var test = BigMoney.parse("GBP 2.3").withCurrencyScale(RoundingMode.UP);
        assertThat(test.getAmount()).isEqualTo(bd("2.30"));
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_withCurrencyScale_intRoundingMode_lessJPY() {
        var test = BigMoney.parse("JPY 2.345").withCurrencyScale(RoundingMode.UP);
        assertThat(test.getAmount()).isEqualTo(bd("3"));
        assertThat(test.getScale()).isEqualTo(0);
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
        var test = GBP_2_34.withAmount(BIGDEC_2_345);
        assertThat(test.getAmount()).isEqualTo(bd("2.345"));
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_withAmount_BigDecimal_same() {
        var test = GBP_2_34.withAmount(BIGDEC_2_34);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_withAmount_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.withAmount((BigDecimal) null));
    }

    //-----------------------------------------------------------------------
    // withAmount(double)
    //-----------------------------------------------------------------------
    @Test
    void test_withAmount_double() {
        var test = GBP_2_34.withAmount(2.345d);
        assertThat(test.getAmount()).isEqualTo(bd("2.345"));
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_withAmount_double_same() {
        var test = GBP_2_34.withAmount(2.34d);
        assertThat(test).isSameAs(GBP_2_34);
    }

    //-----------------------------------------------------------------------
    // plus(Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_Iterable_BigMoneyProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, GBP_1_23);
        var test = GBP_2_34.plus(iterable);
        assertThat(test).hasToString("GBP 5.90");
    }

    @Test
    void test_plus_Iterable_BigMoney() {
        Iterable<BigMoney> iterable = Arrays.<BigMoney>asList(GBP_2_33, GBP_1_23);
        var test = GBP_2_34.plus(iterable);
        assertThat(test).hasToString("GBP 5.90");
    }

    @Test
    void test_plus_Iterable_Money() {
        Iterable<Money> iterable = Arrays.<Money>asList(GBP_2_33.toMoney(), GBP_1_23.toMoney());
        var test = GBP_2_34.plus(iterable);
        assertThat(test).hasToString("GBP 5.90");
    }

    @Test
    void test_plus_Iterable_Mixed() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33.toMoney(), () -> GBP_1_23);
        var test = GBP_2_34.plus(iterable);
        assertThat(test).hasToString("GBP 5.90");
    }

    @Test
    void test_plus_Iterable_zero() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_0_00);
        var test = GBP_2_34.plus(iterable);
        assertThat(test).isEqualTo(GBP_2_34);
    }

    @Test
    void test_plus_Iterable_currencyMismatch() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, JPY_423);
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> GBP_M5_78.plus(iterable));
    }

    @Test
    void test_plus_Iterable_nullEntry() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, null);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus(iterable));
    }

    @Test
    void test_plus_Iterable_nullIterable() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus((Iterable<BigMoneyProvider>) null));
    }

    @Test
    void test_plus_Iterable_badProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(() -> null);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus(iterable));
    }

    //-----------------------------------------------------------------------
    // plus(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_BigMoneyProvider_zero() {
        var test = GBP_2_34.plus(GBP_0_00);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plus_BigMoneyProvider_positive() {
        var test = GBP_2_34.plus(GBP_1_23);
        assertThat(test).hasToString("GBP 3.57");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plus_BigMoneyProvider_negative() {
        var test = GBP_2_34.plus(GBP_M1_23);
        assertThat(test).hasToString("GBP 1.11");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plus_BigMoneyProvider_scale() {
        var test = GBP_2_34.plus(BigMoney.parse("GBP 1.111"));
        assertThat(test).hasToString("GBP 3.451");
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_plus_BigMoneyProvider_Money() {
        var test = GBP_2_34.plus(BigMoney.ofMinor(GBP, 1));
        assertThat(test).hasToString("GBP 2.35");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plus_BigMoneyProvider_currencyMismatch() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> GBP_M5_78.plus(USD_1_23));
    }

    @Test
    void test_plus_BigMoneyProvider_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus((BigMoneyProvider) null));
    }

    @Test
    void test_plus_BigMoneyProvider_badProvider() {
        BigMoneyProvider provider = () -> null;
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus(provider));
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
        var test = GBP_2_34.plus(bd("1.23"));
        assertThat(test).hasToString("GBP 3.57");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plus_BigDecimal_negative() {
        var test = GBP_2_34.plus(bd("-1.23"));
        assertThat(test).hasToString("GBP 1.11");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plus_BigDecimal_scale() {
        var test = GBP_2_34.plus(bd("1.235"));
        assertThat(test).hasToString("GBP 3.575");
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_plus_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plus((BigDecimal) null));
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
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plus_double_negative() {
        var test = GBP_2_34.plus(-1.23d);
        assertThat(test).hasToString("GBP 1.11");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plus_double_scale() {
        var test = GBP_2_34.plus(1.234d);
        assertThat(test).hasToString("GBP 3.574");
        assertThat(test.getScale()).isEqualTo(3);
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
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plusMajor_negative() {
        var test = GBP_2_34.plusMajor(-123);
        assertThat(test).hasToString("GBP -120.66");
        assertThat(test.getScale()).isEqualTo(2);
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
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plusMinor_negative() {
        var test = GBP_2_34.plusMinor(-123);
        assertThat(test).hasToString("GBP 1.11");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_plusMinor_scale() {
        var test = BigMoney.parse("GBP 12").plusMinor(123);
        assertThat(test).hasToString("GBP 13.23");
        assertThat(test.getScale()).isEqualTo(2);
    }

    //-----------------------------------------------------------------------
    // plusRetainScale(BigMoneyProvider,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_zero() {
        var test = GBP_2_34.plusRetainScale(BigMoney.zero(GBP), RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_positive() {
        var test = GBP_2_34.plusRetainScale(BigMoney.parse("GBP 1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_negative() {
        var test = GBP_2_34.plusRetainScale(BigMoney.parse("GBP -1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_roundDown() {
        var test = GBP_2_34.plusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.plusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.UNNECESSARY));
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plusRetainScale((BigDecimal) null, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plusRetainScale(BigMoney.parse("GBP 1.23"), (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // plusRetainScale(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_zero() {
        var test = GBP_2_34.plusRetainScale(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_positive() {
        var test = GBP_2_34.plusRetainScale(bd("1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_negative() {
        var test = GBP_2_34.plusRetainScale(bd("-1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_roundDown() {
        var test = GBP_2_34.plusRetainScale(bd("1.235"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.plusRetainScale(bd("1.235"), RoundingMode.UNNECESSARY));
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plusRetainScale((BigDecimal) null, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plusRetainScale(BIGDEC_2_34, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // plusRetainScale(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_plusRetainScale_doubleRoundingMode_zero() {
        var test = GBP_2_34.plusRetainScale(0d, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_positive() {
        var test = GBP_2_34.plusRetainScale(1.23d, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_negative() {
        var test = GBP_2_34.plusRetainScale(-1.23d, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_roundDown() {
        var test = GBP_2_34.plusRetainScale(1.235d, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.plusRetainScale(1.235d, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.plusRetainScale(2.34d, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // minus(Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_Iterable_BigMoneyProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, GBP_1_23);
        var test = GBP_2_34.minus(iterable);
        assertThat(test).hasToString("GBP -1.22");
    }

    @Test
    void test_minus_Iterable_BigMoney() {
        Iterable<BigMoney> iterable = Arrays.<BigMoney>asList(GBP_2_33, GBP_1_23);
        var test = GBP_2_34.minus(iterable);
        assertThat(test).hasToString("GBP -1.22");
    }

    @Test
    void test_minus_Iterable_Money() {
        Iterable<Money> iterable = Arrays.<Money>asList(GBP_2_33.toMoney(), GBP_1_23.toMoney());
        var test = GBP_2_34.minus(iterable);
        assertThat(test).hasToString("GBP -1.22");
    }

    @Test
    void test_minus_Iterable_Mixed() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33.toMoney(), () -> GBP_1_23);
        var test = GBP_2_34.minus(iterable);
        assertThat(test).hasToString("GBP -1.22");
    }

    @Test
    void test_minus_Iterable_zero() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_0_00);
        var test = GBP_2_34.minus(iterable);
        assertThat(test).isEqualTo(GBP_2_34);
    }

    @Test
    void test_minus_Iterable_currencyMismatch() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, JPY_423);
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> GBP_M5_78.minus(iterable));
    }

    @Test
    void test_minus_Iterable_nullEntry() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, null);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus(iterable));
    }

    @Test
    void test_minus_Iterable_nullIterable() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus((Iterable<BigMoneyProvider>) null));
    }

    @Test
    void test_minus_Iterable_badProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(() -> null);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus(iterable));
    }

    //-----------------------------------------------------------------------
    // minus(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_BigMoneyProvider_zero() {
        var test = GBP_2_34.minus(GBP_0_00);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minus_BigMoneyProvider_positive() {
        var test = GBP_2_34.minus(GBP_1_23);
        assertThat(test).hasToString("GBP 1.11");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minus_BigMoneyProvider_negative() {
        var test = GBP_2_34.minus(GBP_M1_23);
        assertThat(test).hasToString("GBP 3.57");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minus_BigMoneyProvider_scale() {
        var test = GBP_2_34.minus(BigMoney.parse("GBP 1.111"));
        assertThat(test).hasToString("GBP 1.229");
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_minus_BigMoneyProvider_Money() {
        var test = GBP_2_34.minus(BigMoney.ofMinor(GBP, 1));
        assertThat(test).hasToString("GBP 2.33");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minus_BigMoneyProvider_currencyMismatch() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> GBP_M5_78.minus(USD_1_23));
    }

    @Test
    void test_minus_BigMoneyProvider_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus((BigMoneyProvider) null));
    }

    @Test
    void test_minus_BigMoneyProvider_badProvider() {
        BigMoneyProvider provider = () -> null;
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus(provider));
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
        var test = GBP_2_34.minus(bd("1.23"));
        assertThat(test).hasToString("GBP 1.11");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minus_BigDecimal_negative() {
        var test = GBP_2_34.minus(bd("-1.23"));
        assertThat(test).hasToString("GBP 3.57");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minus_BigDecimal_scale() {
        var test = GBP_2_34.minus(bd("1.235"));
        assertThat(test).hasToString("GBP 1.105");
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_minus_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minus((BigDecimal) null));
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
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minus_double_negative() {
        var test = GBP_2_34.minus(-1.23d);
        assertThat(test).hasToString("GBP 3.57");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minus_double_scale() {
        var test = GBP_2_34.minus(1.235d);
        assertThat(test).hasToString("GBP 1.105");
        assertThat(test.getScale()).isEqualTo(3);
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
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minusMajor_negative() {
        var test = GBP_2_34.minusMajor(-123);
        assertThat(test).hasToString("GBP 125.34");
        assertThat(test.getScale()).isEqualTo(2);
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
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minusMinor_negative() {
        var test = GBP_2_34.minusMinor(-123);
        assertThat(test).hasToString("GBP 3.57");
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_minusMinor_scale() {
        var test = BigMoney.parse("GBP 12").minusMinor(123);
        assertThat(test).hasToString("GBP 10.77");
        assertThat(test.getScale()).isEqualTo(2);
    }

    //-----------------------------------------------------------------------
    // minusRetainScale(BigMoneyProvider,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_zero() {
        var test = GBP_2_34.minusRetainScale(BigMoney.zero(GBP), RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_positive() {
        var test = GBP_2_34.minusRetainScale(BigMoney.parse("GBP 1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_negative() {
        var test = GBP_2_34.minusRetainScale(BigMoney.parse("GBP -1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_roundDown() {
        var test = GBP_2_34.minusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 1.10");
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.minusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.UNNECESSARY));
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_nullBigMoneyProvider() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minusRetainScale((BigMoneyProvider) null, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minusRetainScale(BigMoney.parse("GBP 123"), (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // minusRetainScale(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_zero() {
        var test = GBP_2_34.minusRetainScale(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_positive() {
        var test = GBP_2_34.minusRetainScale(bd("1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_negative() {
        var test = GBP_2_34.minusRetainScale(bd("-1.23"), RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_roundDown() {
        var test = GBP_2_34.minusRetainScale(bd("1.235"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 1.10");
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.minusRetainScale(bd("1.235"), RoundingMode.UNNECESSARY));
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minusRetainScale((BigDecimal) null, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minusRetainScale(BIGDEC_2_34, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // minusRetainScale(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_minusRetainScale_doubleRoundingMode_zero() {
        var test = GBP_2_34.minusRetainScale(0d, RoundingMode.UNNECESSARY);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_positive() {
        var test = GBP_2_34.minusRetainScale(1.23d, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 1.11");
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_negative() {
        var test = GBP_2_34.minusRetainScale(-1.23d, RoundingMode.UNNECESSARY);
        assertThat(test).hasToString("GBP 3.57");
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_roundDown() {
        var test = GBP_2_34.minusRetainScale(1.235d, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 1.10");
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_roundUnecessary() {
        assertThatExceptionOfType(ArithmeticException.class)
            .isThrownBy(() -> GBP_2_34.minusRetainScale(1.235d, RoundingMode.UNNECESSARY));
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_M5_78.minusRetainScale(2.34d, (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // multipliedBy(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_multipliedBy_BigDecimal_one() {
        var test = GBP_2_34.multipliedBy(BigDecimal.ONE);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_multipliedBy_BigDecimal_positive() {
        var test = GBP_2_33.multipliedBy(bd("2.5"));
        assertThat(test).hasToString("GBP 5.825");
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_multipliedBy_BigDecimal_negative() {
        var test = GBP_2_33.multipliedBy(bd("-2.5"));
        assertThat(test).hasToString("GBP -5.825");
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_multipliedBy_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.multipliedBy((BigDecimal) null));
    }

    //-----------------------------------------------------------------------
    // multipliedBy(double)
    //-----------------------------------------------------------------------
    @Test
    void test_multipliedBy_doubleRoundingMode_one() {
        var test = GBP_2_34.multipliedBy(1d);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_multipliedBy_doubleRoundingMode_positive() {
        var test = GBP_2_33.multipliedBy(2.5d);
        assertThat(test).hasToString("GBP 5.825");
        assertThat(test.getScale()).isEqualTo(3);
    }

    @Test
    void test_multipliedBy_doubleRoundingMode_negative() {
        var test = GBP_2_33.multipliedBy(-2.5d);
        assertThat(test).hasToString("GBP -5.825");
        assertThat(test.getScale()).isEqualTo(3);
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
        assertThat(test.getScale()).isEqualTo(2);
    }

    @Test
    void test_multipliedBy_long_negative() {
        var test = GBP_2_34.multipliedBy(-3);
        assertThat(test).hasToString("GBP -7.02");
        assertThat(test.getScale()).isEqualTo(2);
    }

    //-----------------------------------------------------------------------
    // multiplyRetainScale(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_one() {
        var test = GBP_2_34.multiplyRetainScale(BigDecimal.ONE, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_positive() {
        var test = GBP_2_33.multiplyRetainScale(bd("2.5"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 5.82");
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_positive_halfUp() {
        var test = GBP_2_33.multiplyRetainScale(bd("2.5"), RoundingMode.HALF_UP);
        assertThat(test).hasToString("GBP 5.83");
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_negative() {
        var test = GBP_2_33.multiplyRetainScale(bd("-2.5"), RoundingMode.FLOOR);
        assertThat(test).hasToString("GBP -5.83");
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.multiplyRetainScale((BigDecimal) null, RoundingMode.DOWN));
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.multiplyRetainScale(bd("2.5"), (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // multiplyRetainScale(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_multiplyRetainScale_doubleRoundingMode_one() {
        var test = GBP_2_34.multiplyRetainScale(1d, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    @Test
    void test_multiplyRetainScale_doubleRoundingMode_positive() {
        var test = GBP_2_33.multiplyRetainScale(2.5d, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 5.82");
    }

    @Test
    void test_multiplyRetainScale_doubleRoundingMode_positive_halfUp() {
        var test = GBP_2_33.multiplyRetainScale(2.5d, RoundingMode.HALF_UP);
        assertThat(test).hasToString("GBP 5.83");
    }

    @Test
    void test_multiplyRetainScale_doubleRoundingMode_negative() {
        var test = GBP_2_33.multiplyRetainScale(-2.5d, RoundingMode.FLOOR);
        assertThat(test).hasToString("GBP -5.83");
    }

    @Test
    void test_multiplyRetainScale_doubleRoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.multiplyRetainScale(2.5d, (RoundingMode) null));
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
        var test = GBP_2_34.dividedBy(bd("2.5"), RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 0.93");
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_positive_halfUp() {
        var test = GBP_2_34.dividedBy(bd("2.5"), RoundingMode.HALF_UP);
        assertThat(test).hasToString("GBP 0.94");
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_negative() {
        var test = GBP_2_34.dividedBy(bd("-2.5"), RoundingMode.FLOOR);
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
            .isThrownBy(() -> GBP_5_78.dividedBy(bd("2.5"), (RoundingMode) null));
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
    void test_negated_zero() {
        var test = GBP_0_00.negated();
        assertThat(test).isSameAs(GBP_0_00);
    }

    @Test
    void test_negated_positive() {
        var test = GBP_2_34.negated();
        assertThat(test).hasToString("GBP -2.34");
    }

    @Test
    void test_negated_negative() {
        var test = BigMoney.parse("GBP -2.34").negated();
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
        var test = BigMoney.parse("GBP -2.34").abs();
        assertThat(test).hasToString("GBP 2.34");
    }

    //-----------------------------------------------------------------------
    // rounded(int,RoundingMode)
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
        var test = BigMoney.parse("GBP 432.34").rounded(-1, RoundingMode.DOWN);
        assertThat(test).hasToString("GBP 430.00");
    }

    @Test
    void test_round_M1up() {
        var test = BigMoney.parse("GBP 432.34").rounded(-1, RoundingMode.UP);
        assertThat(test).hasToString("GBP 440.00");
    }

    @Test
    void test_round_3() {
        var test = GBP_2_34.rounded(3, RoundingMode.DOWN);
        assertThat(test).isSameAs(GBP_2_34);
    }

    //-----------------------------------------------------------------------
    // convertedTo(CurrencyUnit,BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_positive() {
        var test = GBP_2_33.convertedTo(EUR, bd("2.5"));
        assertThat(test).hasToString("EUR 5.825");
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_sameCurrencyCorrectFactor() {
        var test = GBP_2_33.convertedTo(GBP, bd("1.00000"));
        assertThat(test).isEqualTo(GBP_2_33);
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_negative() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> GBP_2_33.convertedTo(EUR, bd("-2.5")));
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_sameCurrencyWrongFactor() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> GBP_2_33.convertedTo(GBP, bd("2.5")));
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.convertedTo((CurrencyUnit) null, bd("2")));
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.convertedTo(EUR, (BigDecimal) null));
    }

    //-----------------------------------------------------------------------
    // convertRetainScale(CurrencyUnit,BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_positive() {
        var test = BigMoney.parse("GBP 2.2").convertRetainScale(EUR, bd("2.5"), RoundingMode.DOWN);
        assertThat(test).hasToString("EUR 5.5");
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_roundHalfUp() {
        var test = BigMoney.parse("GBP 2.21").convertRetainScale(EUR, bd("2.5"), RoundingMode.HALF_UP);
        assertThat(test).hasToString("EUR 5.53");
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_negative() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> GBP_2_33.convertRetainScale(EUR, bd("-2.5"), RoundingMode.DOWN));
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_sameCurrency() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> GBP_2_33.convertRetainScale(GBP, bd("2.5"), RoundingMode.DOWN));
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.convertRetainScale((CurrencyUnit) null, bd("2"), RoundingMode.DOWN));
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_nullBigDecimal() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.convertRetainScale(EUR, (BigDecimal) null, RoundingMode.DOWN));
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_nullRoundingMode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_5_78.convertRetainScale(EUR, bd("2"), (RoundingMode) null));
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    @Test
    void test_toBigMoney() {
        assertThat(GBP_2_34.toBigMoney()).isSameAs(GBP_2_34);
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    @Test
    void test_toMoney() {
        assertThat(GBP_2_34.toMoney()).isEqualTo(Money.of(GBP, BIGDEC_2_34));
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    @Test
    void test_toMoney_RoundingMode() {
        assertThat(GBP_2_34.toMoney(RoundingMode.HALF_EVEN)).isEqualTo(Money.parse("GBP 2.34"));
    }

    @Test
    void test_toMoney_RoundingMode_round() {
        var money = BigMoney.parse("GBP 2.355");
        assertThat(money.toMoney(RoundingMode.HALF_EVEN)).isEqualTo(Money.parse("GBP 2.36"));
    }

    //-----------------------------------------------------------------------
    // isSameCurrency(Money)
    //-----------------------------------------------------------------------
    @Test
    void test_isSameCurrency_BigMoney_same() {
        assertThat(GBP_2_34.isSameCurrency(GBP_2_35)).isTrue();
    }

    @Test
    void test_isSameCurrency_BigMoney_different() {
        assertThat(GBP_2_34.isSameCurrency(USD_2_34)).isFalse();
    }

    @Test
    void test_isSameCurrency_Money_same() {
        assertThat(GBP_2_34.isSameCurrency(Money.parse("GBP 2"))).isTrue();
    }

    @Test
    void test_isSameCurrency_Money_different() {
        assertThat(GBP_2_34.isSameCurrency(Money.parse("USD 2"))).isFalse();
    }

    @Test
    void test_isSameCurrency_Money_nullMoney() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> GBP_2_34.isSameCurrency((BigMoney) null));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    void test_compareTo_BigMoney() {
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
    void test_compareTo_Money() {
        var t = GBP_2_35;
        var a = Money.ofMinor(GBP, 234);
        var b = Money.ofMinor(GBP, 235);
        var c = Money.ofMinor(GBP, 236);
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
        var b = Money.ofMinor(GBP, 234);
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
        var test = BigMoney.of(GBP, BIGDEC_2_34);
        assertThat(test).hasToString("GBP 2.34");
    }

    @Test
    void test_toString_negative() {
        var test = BigMoney.of(EUR, BIGDEC_M5_78);
        assertThat(test).hasToString("EUR -5.78");
    }

}
