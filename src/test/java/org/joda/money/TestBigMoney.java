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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
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
    private static final BigMoneyProvider BAD_PROVIDER = new BigMoneyProvider() {
        @Override
        public BigMoney toBigMoney() {
            return null;  // shouldn't return null
        }
    };

    private static BigDecimal bd(String str) {
        return new BigDecimal(str);
    }

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency_BigDecimal() {
        BigMoney test = BigMoney.of(GBP, BIGDEC_2_345);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BIGDEC_2_345, test.getAmount());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_factory_of_Currency_BigDecimal_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.of((CurrencyUnit) null, BIGDEC_2_345);
        });
    }

    @Test
    void test_factory_of_Currency_BigDecimal_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.of(GBP, (BigDecimal) null);
        });
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
        assertThrows(IllegalArgumentException.class, () -> {
            BigMoney.of(GBP, sub);
        });
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
        BigMoney test = BigMoney.of(GBP, sub);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(bd("12.3"), test.getAmount());
        assertEquals(1, test.getScale());
        assertEquals(BigDecimal.class, test.getAmount().getClass());
    }

    //-----------------------------------------------------------------------
    // of(Currency,double)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency_double() {
        BigMoney test = BigMoney.of(GBP, 2.345d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BIGDEC_2_345, test.getAmount());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_factory_of_Currency_double_trailingZero1() {
        BigMoney test = BigMoney.of(GBP, 1.230d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(123L, 2), test.getAmount());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_factory_of_Currency_double_trailingZero2() {
        BigMoney test = BigMoney.of(GBP, 1.20d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(12L, 1), test.getAmount());
        assertEquals(1, test.getScale());
    }

    @Test
    void test_factory_of_Currency_double_zero() {
        assertEquals(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)), BigMoney.of(GBP, 0d));
        assertEquals(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)), BigMoney.of(GBP, -0d));
        assertEquals(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)), BigMoney.of(GBP, 0.0d));
        assertEquals(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)), BigMoney.of(GBP, 0.00d));
        assertEquals(BigMoney.of(GBP, BigDecimal.valueOf(0L, 0)), BigMoney.of(GBP, -0.0d));
    }

    @Test
    void test_factory_of_Currency_double_medium() {
        BigMoney test = BigMoney.of(GBP, 2000d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(2000L, 0), test.getAmount());
        assertEquals(0, test.getScale());
    }

    @Test
    void test_factory_of_Currency_double_big() {
        BigMoney test = BigMoney.of(GBP, 200000000d);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(200000000L, 0), test.getAmount());
        assertEquals(0, test.getScale());
    }

    @Test
    void test_factory_of_Currency_double_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.of((CurrencyUnit) null, 2.345d);
        });
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,BigDecimal, int)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofScale_Currency_BigDecimal_int() {
        BigMoney test = BigMoney.ofScale(GBP, BIGDEC_2_34, 4);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(23400, 4), test.getAmount());
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_negativeScale() {
        BigMoney test = BigMoney.ofScale(GBP, BigDecimal.valueOf(23400), -2);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(23400L, 0), test.getAmount());
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_invalidScale() {
        assertThrows(ArithmeticException.class, () -> {
            BigMoney.ofScale(GBP, BIGDEC_2_345, 2);
        });
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.ofScale((CurrencyUnit) null, BIGDEC_2_34, 2);
        });
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.ofScale(GBP, (BigDecimal) null, 2);
        });
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,BigDecimal,int,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_DOWN() {
        BigMoney test = BigMoney.ofScale(GBP, BIGDEC_2_34, 1, RoundingMode.DOWN);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(23, 1), test.getAmount());
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_JPY_RoundingMode_UP() {
        BigMoney test = BigMoney.ofScale(JPY, BIGDEC_2_34, 0, RoundingMode.UP);
        assertEquals(JPY, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(3, 0), test.getAmount());
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_negativeScale() {
        BigMoney test = BigMoney.ofScale(GBP, BigDecimal.valueOf(23400), -2, RoundingMode.DOWN);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(23400L, 0), test.getAmount());
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_UNNECESSARY() {
        assertThrows(ArithmeticException.class, () -> {
            BigMoney.ofScale(JPY, BIGDEC_2_34, 1, RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.ofScale((CurrencyUnit) null, BIGDEC_2_34, 2, RoundingMode.DOWN);
        });
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.ofScale(GBP, (BigDecimal) null, 2, RoundingMode.DOWN);
        });
    }

    @Test
    void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.ofScale(GBP, BIGDEC_2_34, 2, (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,long, int)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofScale_Currency_long_int() {
        BigMoney test = BigMoney.ofScale(GBP, 234, 4);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(234, 4), test.getAmount());
    }

    @Test
    void test_factory_ofScale_Currency_long_int_negativeScale() {
        BigMoney test = BigMoney.ofScale(GBP, 234, -4);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(2340000, 0), test.getAmount());
    }

    @Test
    void test_factory_ofScale_Currency_long_int_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.ofScale((CurrencyUnit) null, 234, 2);
        });
    }

    //-----------------------------------------------------------------------
    // ofMajor(Currency,long)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofMajor_Currency_long() {
        BigMoney test = BigMoney.ofMajor(GBP, 234);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(bd("234"), test.getAmount());
        assertEquals(0, test.getScale());
    }

    @Test
    void test_factory_ofMajor_Currency_long_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.ofMajor((CurrencyUnit) null, 234);
        });
    }

    //-----------------------------------------------------------------------
    // ofMinor(Currency,long)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofMinor_Currency_long() {
        BigMoney test = BigMoney.ofMinor(GBP, 234);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(bd("2.34"), test.getAmount());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_factory_ofMinor_Currency_long_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.ofMinor((CurrencyUnit) null, 234);
        });
    }

    //-----------------------------------------------------------------------
    // zero(Currency)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_zero_Currency() {
        BigMoney test = BigMoney.zero(GBP);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.ZERO, test.getAmount());
        assertEquals(0, test.getScale());
    }

    @Test
    void test_factory_zero_Currency_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.zero((CurrencyUnit) null);
        });
    }

    //-----------------------------------------------------------------------
    // zero(Currency, int)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_zero_Currency_int() {
        BigMoney test = BigMoney.zero(GBP, 3);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(0, 3), test.getAmount());
    }

    @Test
    void test_factory_zero_Currency_int_negativeScale() {
        BigMoney test = BigMoney.zero(GBP, -3);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(0, 0), test.getAmount());
    }

    @Test
    void test_factory_zero_Currency_int_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.zero((CurrencyUnit) null, 3);
        });
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_from_BigMoneyProvider() {
        BigMoney test = BigMoney.of(BigMoney.parse("GBP 104.23"));
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(10423, test.getAmountMinorInt());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_factory_from_BigMoneyProvider_nullBigMoneyProvider() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.of((BigMoneyProvider) null);
        });
    }

    @Test
    void test_factory_from_BigMoneyProvider_badProvider() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.of(BAD_PROVIDER);
        });
    }

    //-----------------------------------------------------------------------
    // total(BigMoneyProvider...)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_varargs_1BigMoney() {
        BigMoney test = BigMoney.total(GBP_1_23);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(123, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_array_1BigMoney() {
        BigMoneyProvider[] array = new BigMoneyProvider[] {GBP_1_23};
        BigMoney test = BigMoney.total(array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(123, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_varargs_3Mixed() {
        BigMoney test = BigMoney.total(GBP_1_23, GBP_2_33.toMoney(), GBP_2_36);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_array_3Mixed() {
        BigMoneyProvider[] array = new BigMoneyProvider[] {GBP_1_23, GBP_2_33.toMoney(), GBP_2_36};
        BigMoney test = BigMoney.total(array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_array_3Money() {
        Money[] array = new Money[] {GBP_1_23.toMoney(), GBP_2_33.toMoney(), GBP_2_36.toMoney()};
        BigMoney test = BigMoney.total(array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_varargs_empty() {
        assertThrows(IllegalArgumentException.class, () -> {
            BigMoney.total();
        });
    }

    @Test
    void test_factory_total_array_empty() {
        BigMoneyProvider[] array = new BigMoneyProvider[0];
        assertThrows(IllegalArgumentException.class, () -> {
            BigMoney.total(array);
        });
    }

    @Test
    void test_factory_total_varargs_currenciesDiffer() {
        assertThrows(CurrencyMismatchException.class, () -> {
            BigMoney.total(GBP_2_33, JPY_423);
        });
    }

    @Test
    void test_factory_total_array_currenciesDiffer() {
        BigMoneyProvider[] array = new BigMoneyProvider[] {GBP_2_33, JPY_423};
        assertThrows(CurrencyMismatchException.class, () -> {
            BigMoney.total(array);
        });
    }

    @Test
    void test_factory_total_varargs_nullFirst() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total((BigMoney) null, GBP_2_33, GBP_2_36);
        });
    }

    @Test
    void test_factory_total_array_nullFirst() {
        BigMoneyProvider[] array = new BigMoneyProvider[] {null, GBP_2_33, GBP_2_36};
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(array);
        });
    }

    @Test
    void test_factory_total_varargs_nullNotFirst() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP_2_33, null, GBP_2_36);
        });
    }

    @Test
    void test_factory_total_array_nullNotFirst() {
        BigMoneyProvider[] array = new BigMoneyProvider[] {GBP_2_33, null, GBP_2_36};
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(array);
        });
    }

    @Test
    void test_factory_total_varargs_badProvider() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(BAD_PROVIDER);
        });
    }

    @Test
    void test_factory_total_array_badProvider() {
        BigMoneyProvider[] array = new BigMoneyProvider[] {BAD_PROVIDER};
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(array);
        });
    }

    //-----------------------------------------------------------------------
    // total(Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_Iterable() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_1_23, GBP_2_33, BigMoney.of(GBP, 2.361d));
        BigMoney test = BigMoney.total(iterable);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(5921, 3), test.getAmount());
    }

    @Test
    void test_factory_total_Iterable_Mixed() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_1_23.toMoney(), GBP_2_33);
        BigMoney test = BigMoney.total(iterable);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(356, 2), test.getAmount());
    }

    @Test
    void test_factory_total_Iterable_empty() {
        Iterable<BigMoney> iterable = Collections.emptyList();
        assertThrows(IllegalArgumentException.class, () -> {
            BigMoney.total(iterable);
        });
    }

    @Test
    void test_factory_total_Iterable_currenciesDiffer() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, JPY_423);
        assertThrows(CurrencyMismatchException.class, () -> {
            BigMoney.total(iterable);
        });
    }

    @Test
    void test_factory_total_Iterable_nullFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(iterable);
        });
    }

    @Test
    void test_factory_total_Iterable_nullNotFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(iterable);
        });
    }

    @Test
    void test_factory_total_Iterable_badProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(BAD_PROVIDER);
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(iterable);
        });
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,BigMoneyProvider...)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_CurrencyUnitVarargs_1() {
        BigMoney test = BigMoney.total(GBP, GBP_1_23);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(123, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitArray_1() {
        BigMoney[] array = new BigMoney[] {GBP_1_23};
        BigMoney test = BigMoney.total(GBP, array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(123, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_3() {
        BigMoney test = BigMoney.total(GBP, GBP_1_23, GBP_2_33, GBP_2_36);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitArray_3() {
        BigMoney[] array = new BigMoney[] {GBP_1_23, GBP_2_33, GBP_2_36};
        BigMoney test = BigMoney.total(GBP, array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_3Mixed() {
        BigMoney test = BigMoney.total(GBP, GBP_1_23, GBP_2_33.toMoney(), GBP_2_36);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitArray_3Mixed() {
        BigMoneyProvider[] array = new BigMoneyProvider[] {GBP_1_23, GBP_2_33.toMoney(), GBP_2_36};
        BigMoney test = BigMoney.total(GBP, array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitArray_3Money() {
        Money[] array = new Money[] {GBP_1_23.toMoney(), GBP_2_33.toMoney(), GBP_2_36.toMoney()};
        BigMoney test = BigMoney.total(GBP, array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(592, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_empty() {
        BigMoney test = BigMoney.total(GBP);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(0, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitArray_empty() {
        BigMoney[] array = new BigMoney[0];
        BigMoney test = BigMoney.total(GBP, array);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(0, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_currenciesDiffer() {
        assertThrows(CurrencyMismatchException.class, () -> {
            BigMoney.total(GBP, JPY_423);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitArray_currenciesDiffer() {
        BigMoney[] array = new BigMoney[] {JPY_423};
        assertThrows(CurrencyMismatchException.class, () -> {
            BigMoney.total(GBP, array);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_currenciesDifferInArray() {
        assertThrows(CurrencyMismatchException.class, () -> {
            BigMoney.total(GBP, GBP_2_33, JPY_423);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitArray_currenciesDifferInArray() {
        BigMoney[] array = new BigMoney[] {GBP_2_33, JPY_423};
        assertThrows(CurrencyMismatchException.class, () -> {
            BigMoney.total(GBP, array);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_nullFirst() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP, null, GBP_2_33, GBP_2_36);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitArray_nullFirst() {
        BigMoney[] array = new BigMoney[] {null, GBP_2_33, GBP_2_36};
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP, array);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_nullNotFirst() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP, GBP_2_33, null, GBP_2_36);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitArray_nullNotFirst() {
        BigMoney[] array = new BigMoney[] {GBP_2_33, null, GBP_2_36};
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP, array);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitVarargs_badProvider() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP, BAD_PROVIDER);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitArray_badProvider() {
        BigMoneyProvider[] array = new BigMoneyProvider[] {BAD_PROVIDER};
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP, array);
        });
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_total_CurrencyUnitIterable() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_1_23, GBP_2_33, BigMoney.of(GBP, 2.361d));
        BigMoney test = BigMoney.total(GBP, iterable);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(5921, 3), test.getAmount());
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_Mixed() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_1_23.toMoney(), GBP_2_33);
        BigMoney test = BigMoney.total(GBP, iterable);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(BigDecimal.valueOf(356, 2), test.getAmount());
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_empty() {
        Iterable<BigMoney> iterable = Collections.emptyList();
        BigMoney test = BigMoney.total(GBP, iterable);
        assertEquals(GBP, test.getCurrencyUnit());
        assertEquals(0, test.getAmountMinorInt());
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_currenciesDiffer() {
        Iterable<BigMoney> iterable = Arrays.asList(JPY_423);
        assertThrows(CurrencyMismatchException.class, () -> {
            BigMoney.total(GBP, iterable);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_currenciesDifferInIterable() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, JPY_423);
        assertThrows(CurrencyMismatchException.class, () -> {
            BigMoney.total(GBP, iterable);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_nullFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP, iterable);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_nullNotFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP, iterable);
        });
    }

    @Test
    void test_factory_total_CurrencyUnitIterable_badProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(BAD_PROVIDER);
        assertThrows(NullPointerException.class, () -> {
            BigMoney.total(GBP, iterable);
        });
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
        BigMoney test = BigMoney.parse(str);
        assertEquals(currency, test.getCurrencyUnit());
        assertEquals(bd(amountStr), test.getAmount());
        assertEquals(scale, test.getScale());
    }

    @Test
    void test_factory_parse_String_tooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            BigMoney.parse("GBP");
        });
    }

    @Test
    void test_factory_parse_String_exponent() {
        assertThrows(IllegalArgumentException.class, () -> {
            BigMoney.parse("GBP 234E2");
        });
    }

    @Test
    void test_factory_parse_String_badCurrency() {
        assertThrows(IllegalArgumentException.class, () -> {
            BigMoney.parse("GBX 2.34");
        });
    }

    @Test
    void test_factory_parse_String_nullString() {
        assertThrows(NullPointerException.class, () -> {
            BigMoney.parse((String) null);
        });
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    @Test
    void test_constructor_null1() throws Exception {
        Constructor<BigMoney> con = BigMoney.class.getDeclaredConstructor(CurrencyUnit.class, BigDecimal.class);
        assertFalse(Modifier.isPublic(con.getModifiers()));
        assertFalse(Modifier.isProtected(con.getModifiers()));
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] {null, BIGDEC_2_34});
            fail();
        } catch (InvocationTargetException ex) {
            assertEquals(AssertionError.class, ex.getCause().getClass());
        }
    }

    @Test
    void test_constructor_null2() throws Exception {
        Constructor<BigMoney> con = BigMoney.class.getDeclaredConstructor(CurrencyUnit.class, BigDecimal.class);
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] {GBP, null});
            fail();
        } catch (InvocationTargetException ex) {
            assertEquals(AssertionError.class, ex.getCause().getClass());
        }
    }

    //-----------------------------------------------------------------------
    @Test
    void test_scaleNormalization1() {
        BigMoney a = BigMoney.ofScale(GBP, 100, 0);
        BigMoney b = BigMoney.ofScale(GBP, 1, -2);
        assertEquals("GBP 100", a.toString());
        assertEquals("GBP 100", b.toString());
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    void test_scaleNormalization2() {
        BigMoney a = BigMoney.ofScale(GBP, 1, 1);
        BigMoney b = BigMoney.ofScale(GBP, 10, 2);
        assertEquals("GBP 0.1", a.toString());
        assertEquals("GBP 0.10", b.toString());
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
        assertFalse(a.hashCode() == b.hashCode());
    }

    @Test
    void test_scaleNormalization3() {
        BigMoney a = BigMoney.of(GBP, new BigDecimal("100"));
        BigMoney b = BigMoney.of(GBP, new BigDecimal("1E+2"));
        assertEquals("GBP 100", a.toString());
        assertEquals("GBP 100", b.toString());
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertTrue(a.hashCode() == b.hashCode());
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test
    void test_serialization() throws Exception {
        BigMoney a = BigMoney.parse("GBP 2.34");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(a);
            oos.close();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            BigMoney input = (BigMoney) ois.readObject();
            assertEquals(a, input);
        }
    }

    @Test
    void test_serialization_invalidNumericCode() throws IOException {
        CurrencyUnit cu = new CurrencyUnit("GBP", (short) 234, (short) 2);
        BigMoney m = BigMoney.of(cu, 123.43d);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(m);
            oos.close();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            assertThrows(InvalidObjectException.class, () -> {
                ois.readObject();
            });
        }
    }

    @Test
    void test_serialization_invalidDecimalPlaces() throws IOException {
        CurrencyUnit cu = new CurrencyUnit("GBP", (short) 826, (short) 1);
        BigMoney m = BigMoney.of(cu, 123.43d);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(m);
            oos.close();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            assertThrows(InvalidObjectException.class, () -> {
                ois.readObject();
            });
        }
    }

    //-----------------------------------------------------------------------
    // getCurrencyUnit()
    //-----------------------------------------------------------------------
    @Test
    void test_getCurrencyUnit_GBP() {
        assertEquals(GBP, GBP_2_34.getCurrencyUnit());
    }

    @Test
    void test_getCurrencyUnit_EUR() {
        assertEquals(EUR, BigMoney.parse("EUR -5.78").getCurrencyUnit());
    }

    //-----------------------------------------------------------------------
    // withCurrencyUnit(Currency)
    //-----------------------------------------------------------------------
    @Test
    void test_withCurrencyUnit_Currency() {
        BigMoney test = GBP_2_34.withCurrencyUnit(USD);
        assertEquals("USD 2.34", test.toString());
    }

    @Test
    void test_withCurrencyUnit_Currency_same() {
        BigMoney test = GBP_2_34.withCurrencyUnit(GBP);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_withCurrencyUnit_Currency_differentCurrencyScale() {
        BigMoney test = GBP_2_34.withCurrencyUnit(JPY);
        assertEquals("JPY 2.34", test.toString());
    }

    @Test
    void test_withCurrencyUnit_Currency_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            GBP_2_34.withCurrencyUnit((CurrencyUnit) null);
        });
    }

    //-----------------------------------------------------------------------
    // getScale()
    //-----------------------------------------------------------------------
    @Test
    void test_getScale_GBP() {
        assertEquals(2, GBP_2_34.getScale());
    }

    @Test
    void test_getScale_JPY() {
        assertEquals(0, JPY_423.getScale());
    }

    //-----------------------------------------------------------------------
    // isCurrencyScale()
    //-----------------------------------------------------------------------
    @Test
    void test_isCurrencyScale_GBP() {
        assertFalse(BigMoney.parse("GBP 2").isCurrencyScale());
        assertFalse(BigMoney.parse("GBP 2.3").isCurrencyScale());
        assertTrue(BigMoney.parse("GBP 2.34").isCurrencyScale());
        assertFalse(BigMoney.parse("GBP 2.345").isCurrencyScale());
    }

    @Test
    void test_isCurrencyScale_JPY() {
        assertTrue(BigMoney.parse("JPY 2").isCurrencyScale());
        assertFalse(BigMoney.parse("JPY 2.3").isCurrencyScale());
        assertFalse(BigMoney.parse("JPY 2.34").isCurrencyScale());
        assertFalse(BigMoney.parse("JPY 2.345").isCurrencyScale());
    }

    //-----------------------------------------------------------------------
    // withScale(int)
    //-----------------------------------------------------------------------
    @Test
    void test_withScale_int_same() {
        BigMoney test = GBP_2_34.withScale(2);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_withScale_int_more() {
        BigMoney test = GBP_2_34.withScale(3);
        assertEquals(bd("2.340"), test.getAmount());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_withScale_int_less() {
        assertThrows(ArithmeticException.class, () -> {
            BigMoney.parse("GBP 2.345").withScale(2);
        });
    }

    //-----------------------------------------------------------------------
    // withScale(int,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_withScale_intRoundingMode_less() {
        BigMoney test = GBP_2_34.withScale(1, RoundingMode.UP);
        assertEquals(bd("2.4"), test.getAmount());
        assertEquals(1, test.getScale());
    }

    @Test
    void test_withScale_intRoundingMode_more() {
        BigMoney test = GBP_2_34.withScale(3, RoundingMode.UP);
        assertEquals(bd("2.340"), test.getAmount());
        assertEquals(3, test.getScale());
    }

    //-----------------------------------------------------------------------
    // withCurrencyScale()
    //-----------------------------------------------------------------------
    @Test
    void test_withCurrencyScale_int_same() {
        BigMoney test = GBP_2_34.withCurrencyScale();
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_withCurrencyScale_int_more() {
        BigMoney test = BigMoney.parse("GBP 2.3").withCurrencyScale();
        assertEquals(bd("2.30"), test.getAmount());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_withCurrencyScale_int_less() {
        assertThrows(ArithmeticException.class, () -> {
            BigMoney.parse("GBP 2.345").withCurrencyScale();
        });
    }

    //-----------------------------------------------------------------------
    // withCurrencyScale(RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_withCurrencyScale_intRoundingMode_less() {
        BigMoney test = BigMoney.parse("GBP 2.345").withCurrencyScale(RoundingMode.UP);
        assertEquals(bd("2.35"), test.getAmount());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_withCurrencyScale_intRoundingMode_more() {
        BigMoney test = BigMoney.parse("GBP 2.3").withCurrencyScale(RoundingMode.UP);
        assertEquals(bd("2.30"), test.getAmount());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_withCurrencyScale_intRoundingMode_lessJPY() {
        BigMoney test = BigMoney.parse("JPY 2.345").withCurrencyScale(RoundingMode.UP);
        assertEquals(bd("3"), test.getAmount());
        assertEquals(0, test.getScale());
    }

    //-----------------------------------------------------------------------
    // getAmount()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmount_positive() {
        assertEquals(BIGDEC_2_34, GBP_2_34.getAmount());
    }

    @Test
    void test_getAmount_negative() {
        assertEquals(BIGDEC_M5_78, GBP_M5_78.getAmount());
    }

    //-----------------------------------------------------------------------
    // getAmountMajor()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMajor_positive() {
        assertEquals(BigDecimal.valueOf(2), GBP_2_34.getAmountMajor());
    }

    @Test
    void test_getAmountMajor_negative() {
        assertEquals(BigDecimal.valueOf(-5), GBP_M5_78.getAmountMajor());
    }

    //-----------------------------------------------------------------------
    // getAmountMajorLong()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMajorLong_positive() {
        assertEquals(2L, GBP_2_34.getAmountMajorLong());
    }

    @Test
    void test_getAmountMajorLong_negative() {
        assertEquals(-5L, GBP_M5_78.getAmountMajorLong());
    }

    @Test
    void test_getAmountMajorLong_tooBigPositive() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_LONG_MAX_MAJOR_PLUS1.getAmountMajorLong();
        });
    }

    @Test
    void test_getAmountMajorLong_tooBigNegative() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_LONG_MIN_MAJOR_MINUS1.getAmountMajorLong();
        });
    }

    //-----------------------------------------------------------------------
    // getAmountMajorInt()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMajorInt_positive() {
        assertEquals(2, GBP_2_34.getAmountMajorInt());
    }

    @Test
    void test_getAmountMajorInt_negative() {
        assertEquals(-5, GBP_M5_78.getAmountMajorInt());
    }

    @Test
    void test_getAmountMajorInt_tooBigPositive() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_INT_MAX_MAJOR_PLUS1.getAmountMajorInt();
        });
    }

    @Test
    void test_getAmountMajorInt_tooBigNegative() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_INT_MIN_MAJOR_MINUS1.getAmountMajorInt();
        });
    }

    //-----------------------------------------------------------------------
    // getAmountMinor()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMinor_positive() {
        assertEquals(BigDecimal.valueOf(234), GBP_2_34.getAmountMinor());
    }

    @Test
    void test_getAmountMinor_negative() {
        assertEquals(BigDecimal.valueOf(-578), GBP_M5_78.getAmountMinor());
    }

    //-----------------------------------------------------------------------
    // getAmountMinorLong()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMinorLong_positive() {
        assertEquals(234L, GBP_2_34.getAmountMinorLong());
    }

    @Test
    void test_getAmountMinorLong_negative() {
        assertEquals(-578L, GBP_M5_78.getAmountMinorLong());
    }

    @Test
    void test_getAmountMinorLong_tooBigPositive() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_LONG_MAX_PLUS1.getAmountMinorLong();
        });
    }

    @Test
    void test_getAmountMinorLong_tooBigNegative() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_LONG_MIN_MINUS1.getAmountMinorLong();
        });
    }

    //-----------------------------------------------------------------------
    // getAmountMinorInt()
    //-----------------------------------------------------------------------
    @Test
    void test_getAmountMinorInt_positive() {
        assertEquals(234, GBP_2_34.getAmountMinorInt());
    }

    @Test
    void test_getAmountMinorInt_negative() {
        assertEquals(-578, GBP_M5_78.getAmountMinorInt());
    }

    @Test
    void test_getAmountMinorInt_tooBigPositive() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_INT_MAX_PLUS1.getAmountMinorInt();
        });
    }

    @Test
    void test_getAmountMinorInt_tooBigNegative() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_INT_MIN_MINUS1.getAmountMinorInt();
        });
    }

    //-----------------------------------------------------------------------
    // getMinorPart()
    //-----------------------------------------------------------------------
    @Test
    void test_getMinorPart_positive() {
        assertEquals(34, GBP_2_34.getMinorPart());
    }

    @Test
    void test_getMinorPart_negative() {
        assertEquals(-78, GBP_M5_78.getMinorPart());
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    @Test
    void test_isZero() {
        assertTrue(GBP_0_00.isZero());
        assertFalse(GBP_2_34.isZero());
        assertFalse(GBP_M5_78.isZero());
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    @Test
    void test_isPositive() {
        assertFalse(GBP_0_00.isPositive());
        assertTrue(GBP_2_34.isPositive());
        assertFalse(GBP_M5_78.isPositive());
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero()
    //-----------------------------------------------------------------------
    @Test
    void test_isPositiveOrZero() {
        assertTrue(GBP_0_00.isPositiveOrZero());
        assertTrue(GBP_2_34.isPositiveOrZero());
        assertFalse(GBP_M5_78.isPositiveOrZero());
    }

    //-----------------------------------------------------------------------
    // isNegative()
    //-----------------------------------------------------------------------
    @Test
    void test_isNegative() {
        assertFalse(GBP_0_00.isNegative());
        assertFalse(GBP_2_34.isNegative());
        assertTrue(GBP_M5_78.isNegative());
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero()
    //-----------------------------------------------------------------------
    @Test
    void test_isNegativeOrZero() {
        assertTrue(GBP_0_00.isNegativeOrZero());
        assertFalse(GBP_2_34.isNegativeOrZero());
        assertTrue(GBP_M5_78.isNegativeOrZero());
    }

    //-----------------------------------------------------------------------
    // withAmount(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_withAmount_BigDecimal() {
        BigMoney test = GBP_2_34.withAmount(BIGDEC_2_345);
        assertEquals(bd("2.345"), test.getAmount());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_withAmount_BigDecimal_same() {
        BigMoney test = GBP_2_34.withAmount(BIGDEC_2_34);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_withAmount_BigDecimal_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_2_34.withAmount((BigDecimal) null);
        });
    }

    //-----------------------------------------------------------------------
    // withAmount(double)
    //-----------------------------------------------------------------------
    @Test
    void test_withAmount_double() {
        BigMoney test = GBP_2_34.withAmount(2.345d);
        assertEquals(bd("2.345"), test.getAmount());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_withAmount_double_same() {
        BigMoney test = GBP_2_34.withAmount(2.34d);
        assertSame(GBP_2_34, test);
    }

    //-----------------------------------------------------------------------
    // plus(Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_Iterable_BigMoneyProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, GBP_1_23);
        BigMoney test = GBP_2_34.plus(iterable);
        assertEquals("GBP 5.90", test.toString());
    }

    @Test
    void test_plus_Iterable_BigMoney() {
        Iterable<BigMoney> iterable = Arrays.<BigMoney>asList(GBP_2_33, GBP_1_23);
        BigMoney test = GBP_2_34.plus(iterable);
        assertEquals("GBP 5.90", test.toString());
    }

    @Test
    void test_plus_Iterable_Money() {
        Iterable<Money> iterable = Arrays.<Money>asList(GBP_2_33.toMoney(), GBP_1_23.toMoney());
        BigMoney test = GBP_2_34.plus(iterable);
        assertEquals("GBP 5.90", test.toString());
    }

    @Test
    void test_plus_Iterable_Mixed() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33.toMoney(), new BigMoneyProvider() {
            @Override
            public BigMoney toBigMoney() {
                return GBP_1_23;
            }
        });
        BigMoney test = GBP_2_34.plus(iterable);
        assertEquals("GBP 5.90", test.toString());
    }

    @Test
    void test_plus_Iterable_zero() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_0_00);
        BigMoney test = GBP_2_34.plus(iterable);
        assertEquals(GBP_2_34, test);
    }

    @Test
    void test_plus_Iterable_currencyMismatch() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, JPY_423);
        assertThrows(CurrencyMismatchException.class, () -> {
            GBP_M5_78.plus(iterable);
        });
    }

    @Test
    void test_plus_Iterable_nullEntry() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, null);
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plus(iterable);
        });
    }

    @Test
    void test_plus_Iterable_nullIterable() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plus((Iterable<BigMoneyProvider>) null);
        });
    }

    @Test
    void test_plus_Iterable_badProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(new BigMoneyProvider() {
            @Override
            public BigMoney toBigMoney() {
                return null;
            }
        });
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plus(iterable);
        });
    }

    //-----------------------------------------------------------------------
    // plus(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_BigMoneyProvider_zero() {
        BigMoney test = GBP_2_34.plus(GBP_0_00);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_plus_BigMoneyProvider_positive() {
        BigMoney test = GBP_2_34.plus(GBP_1_23);
        assertEquals("GBP 3.57", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plus_BigMoneyProvider_negative() {
        BigMoney test = GBP_2_34.plus(GBP_M1_23);
        assertEquals("GBP 1.11", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plus_BigMoneyProvider_scale() {
        BigMoney test = GBP_2_34.plus(BigMoney.parse("GBP 1.111"));
        assertEquals("GBP 3.451", test.toString());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_plus_BigMoneyProvider_Money() {
        BigMoney test = GBP_2_34.plus(BigMoney.ofMinor(GBP, 1));
        assertEquals("GBP 2.35", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plus_BigMoneyProvider_currencyMismatch() {
        assertThrows(CurrencyMismatchException.class, () -> {
            GBP_M5_78.plus(USD_1_23);
        });
    }

    @Test
    void test_plus_BigMoneyProvider_nullBigMoneyProvider() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plus((BigMoneyProvider) null);
        });
    }

    @Test
    void test_plus_BigMoneyProvider_badProvider() {
        BigMoneyProvider provider = new BigMoneyProvider() {
            @Override
            public BigMoney toBigMoney() {
                return null;
            }
        };
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plus(provider);
        });
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_BigDecimal_zero() {
        BigMoney test = GBP_2_34.plus(BigDecimal.ZERO);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_plus_BigDecimal_positive() {
        BigMoney test = GBP_2_34.plus(bd("1.23"));
        assertEquals("GBP 3.57", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plus_BigDecimal_negative() {
        BigMoney test = GBP_2_34.plus(bd("-1.23"));
        assertEquals("GBP 1.11", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plus_BigDecimal_scale() {
        BigMoney test = GBP_2_34.plus(bd("1.235"));
        assertEquals("GBP 3.575", test.toString());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_plus_BigDecimal_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plus((BigDecimal) null);
        });
    }

    //-----------------------------------------------------------------------
    // plus(double)
    //-----------------------------------------------------------------------
    @Test
    void test_plus_double_zero() {
        BigMoney test = GBP_2_34.plus(0d);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_plus_double_positive() {
        BigMoney test = GBP_2_34.plus(1.23d);
        assertEquals("GBP 3.57", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plus_double_negative() {
        BigMoney test = GBP_2_34.plus(-1.23d);
        assertEquals("GBP 1.11", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plus_double_scale() {
        BigMoney test = GBP_2_34.plus(1.234d);
        assertEquals("GBP 3.574", test.toString());
        assertEquals(3, test.getScale());
    }

    //-----------------------------------------------------------------------
    // plusMajor(long)
    //-----------------------------------------------------------------------
    @Test
    void test_plusMajor_zero() {
        BigMoney test = GBP_2_34.plusMajor(0);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_plusMajor_positive() {
        BigMoney test = GBP_2_34.plusMajor(123);
        assertEquals("GBP 125.34", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plusMajor_negative() {
        BigMoney test = GBP_2_34.plusMajor(-123);
        assertEquals("GBP -120.66", test.toString());
        assertEquals(2, test.getScale());
    }

    //-----------------------------------------------------------------------
    // plusMinor(long)
    //-----------------------------------------------------------------------
    @Test
    void test_plusMinor_zero() {
        BigMoney test = GBP_2_34.plusMinor(0);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_plusMinor_positive() {
        BigMoney test = GBP_2_34.plusMinor(123);
        assertEquals("GBP 3.57", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plusMinor_negative() {
        BigMoney test = GBP_2_34.plusMinor(-123);
        assertEquals("GBP 1.11", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_plusMinor_scale() {
        BigMoney test = BigMoney.parse("GBP 12").plusMinor(123);
        assertEquals("GBP 13.23", test.toString());
        assertEquals(2, test.getScale());
    }

    //-----------------------------------------------------------------------
    // plusRetainScale(BigMoneyProvider,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_zero() {
        BigMoney test = GBP_2_34.plusRetainScale(BigMoney.zero(GBP), RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_positive() {
        BigMoney test = GBP_2_34.plusRetainScale(BigMoney.parse("GBP 1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_negative() {
        BigMoney test = GBP_2_34.plusRetainScale(BigMoney.parse("GBP -1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.plusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.DOWN);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_roundUnecessary() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_2_34.plusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plusRetainScale((BigDecimal) null, RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_plusRetainScale_BigMoneyProviderRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plusRetainScale(BigMoney.parse("GBP 1.23"), (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // plusRetainScale(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_zero() {
        BigMoney test = GBP_2_34.plusRetainScale(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_positive() {
        BigMoney test = GBP_2_34.plusRetainScale(bd("1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_negative() {
        BigMoney test = GBP_2_34.plusRetainScale(bd("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.plusRetainScale(bd("1.235"), RoundingMode.DOWN);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_roundUnecessary() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_2_34.plusRetainScale(bd("1.235"), RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plusRetainScale((BigDecimal) null, RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_plusRetainScale_BigDecimalRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plusRetainScale(BIGDEC_2_34, (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // plusRetainScale(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_plusRetainScale_doubleRoundingMode_zero() {
        BigMoney test = GBP_2_34.plusRetainScale(0d, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_34.plusRetainScale(1.23d, RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_34.plusRetainScale(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.plusRetainScale(1.235d, RoundingMode.DOWN);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_roundUnecessary() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_2_34.plusRetainScale(1.235d, RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_plusRetainScale_doubleRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.plusRetainScale(2.34d, (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // minus(Iterable)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_Iterable_BigMoneyProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, GBP_1_23);
        BigMoney test = GBP_2_34.minus(iterable);
        assertEquals("GBP -1.22", test.toString());
    }

    @Test
    void test_minus_Iterable_BigMoney() {
        Iterable<BigMoney> iterable = Arrays.<BigMoney>asList(GBP_2_33, GBP_1_23);
        BigMoney test = GBP_2_34.minus(iterable);
        assertEquals("GBP -1.22", test.toString());
    }

    @Test
    void test_minus_Iterable_Money() {
        Iterable<Money> iterable = Arrays.<Money>asList(GBP_2_33.toMoney(), GBP_1_23.toMoney());
        BigMoney test = GBP_2_34.minus(iterable);
        assertEquals("GBP -1.22", test.toString());
    }

    @Test
    void test_minus_Iterable_Mixed() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33.toMoney(), new BigMoneyProvider() {
            @Override
            public BigMoney toBigMoney() {
                return GBP_1_23;
            }
        });
        BigMoney test = GBP_2_34.minus(iterable);
        assertEquals("GBP -1.22", test.toString());
    }

    @Test
    void test_minus_Iterable_zero() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_0_00);
        BigMoney test = GBP_2_34.minus(iterable);
        assertEquals(GBP_2_34, test);
    }

    @Test
    void test_minus_Iterable_currencyMismatch() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, JPY_423);
        assertThrows(CurrencyMismatchException.class, () -> {
            GBP_M5_78.minus(iterable);
        });
    }

    @Test
    void test_minus_Iterable_nullEntry() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(GBP_2_33, null);
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minus(iterable);
        });
    }

    @Test
    void test_minus_Iterable_nullIterable() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minus((Iterable<BigMoneyProvider>) null);
        });
    }

    @Test
    void test_minus_Iterable_badProvider() {
        Iterable<BigMoneyProvider> iterable = Arrays.<BigMoneyProvider>asList(new BigMoneyProvider() {
            @Override
            public BigMoney toBigMoney() {
                return null;
            }
        });
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minus(iterable);
        });
    }

    //-----------------------------------------------------------------------
    // minus(BigMoneyProvider)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_BigMoneyProvider_zero() {
        BigMoney test = GBP_2_34.minus(GBP_0_00);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_minus_BigMoneyProvider_positive() {
        BigMoney test = GBP_2_34.minus(GBP_1_23);
        assertEquals("GBP 1.11", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minus_BigMoneyProvider_negative() {
        BigMoney test = GBP_2_34.minus(GBP_M1_23);
        assertEquals("GBP 3.57", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minus_BigMoneyProvider_scale() {
        BigMoney test = GBP_2_34.minus(BigMoney.parse("GBP 1.111"));
        assertEquals("GBP 1.229", test.toString());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_minus_BigMoneyProvider_Money() {
        BigMoney test = GBP_2_34.minus(BigMoney.ofMinor(GBP, 1));
        assertEquals("GBP 2.33", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minus_BigMoneyProvider_currencyMismatch() {
        assertThrows(CurrencyMismatchException.class, () -> {
            GBP_M5_78.minus(USD_1_23);
        });
    }

    @Test
    void test_minus_BigMoneyProvider_nullBigMoneyProvider() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minus((BigMoneyProvider) null);
        });
    }

    @Test
    void test_minus_BigMoneyProvider_badProvider() {
        BigMoneyProvider provider = new BigMoneyProvider() {
            @Override
            public BigMoney toBigMoney() {
                return null;
            }
        };
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minus(provider);
        });
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_BigDecimal_zero() {
        BigMoney test = GBP_2_34.minus(BigDecimal.ZERO);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_minus_BigDecimal_positive() {
        BigMoney test = GBP_2_34.minus(bd("1.23"));
        assertEquals("GBP 1.11", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minus_BigDecimal_negative() {
        BigMoney test = GBP_2_34.minus(bd("-1.23"));
        assertEquals("GBP 3.57", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minus_BigDecimal_scale() {
        BigMoney test = GBP_2_34.minus(bd("1.235"));
        assertEquals("GBP 1.105", test.toString());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_minus_BigDecimal_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minus((BigDecimal) null);
        });
    }

    //-----------------------------------------------------------------------
    // minus(double)
    //-----------------------------------------------------------------------
    @Test
    void test_minus_double_zero() {
        BigMoney test = GBP_2_34.minus(0d);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_minus_double_positive() {
        BigMoney test = GBP_2_34.minus(1.23d);
        assertEquals("GBP 1.11", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minus_double_negative() {
        BigMoney test = GBP_2_34.minus(-1.23d);
        assertEquals("GBP 3.57", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minus_double_scale() {
        BigMoney test = GBP_2_34.minus(1.235d);
        assertEquals("GBP 1.105", test.toString());
        assertEquals(3, test.getScale());
    }

    //-----------------------------------------------------------------------
    // minusMajor(long)
    //-----------------------------------------------------------------------
    @Test
    void test_minusMajor_zero() {
        BigMoney test = GBP_2_34.minusMajor(0);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_minusMajor_positive() {
        BigMoney test = GBP_2_34.minusMajor(123);
        assertEquals("GBP -120.66", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minusMajor_negative() {
        BigMoney test = GBP_2_34.minusMajor(-123);
        assertEquals("GBP 125.34", test.toString());
        assertEquals(2, test.getScale());
    }

    //-----------------------------------------------------------------------
    // minusMinor(long)
    //-----------------------------------------------------------------------
    @Test
    void test_minusMinor_zero() {
        BigMoney test = GBP_2_34.minusMinor(0);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_minusMinor_positive() {
        BigMoney test = GBP_2_34.minusMinor(123);
        assertEquals("GBP 1.11", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minusMinor_negative() {
        BigMoney test = GBP_2_34.minusMinor(-123);
        assertEquals("GBP 3.57", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_minusMinor_scale() {
        BigMoney test = BigMoney.parse("GBP 12").minusMinor(123);
        assertEquals("GBP 10.77", test.toString());
        assertEquals(2, test.getScale());
    }

    //-----------------------------------------------------------------------
    // minusRetainScale(BigMoneyProvider,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_zero() {
        BigMoney test = GBP_2_34.minusRetainScale(BigMoney.zero(GBP), RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_positive() {
        BigMoney test = GBP_2_34.minusRetainScale(BigMoney.parse("GBP 1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_negative() {
        BigMoney test = GBP_2_34.minusRetainScale(BigMoney.parse("GBP -1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.minusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.DOWN);
        assertEquals("GBP 1.10", test.toString());
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_roundUnecessary() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_2_34.minusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_nullBigMoneyProvider() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minusRetainScale((BigMoneyProvider) null, RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_minusRetainScale_BigMoneyProviderRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minusRetainScale(BigMoney.parse("GBP 123"), (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // minusRetainScale(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_zero() {
        BigMoney test = GBP_2_34.minusRetainScale(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_positive() {
        BigMoney test = GBP_2_34.minusRetainScale(bd("1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_negative() {
        BigMoney test = GBP_2_34.minusRetainScale(bd("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.minusRetainScale(bd("1.235"), RoundingMode.DOWN);
        assertEquals("GBP 1.10", test.toString());
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_roundUnecessary() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_2_34.minusRetainScale(bd("1.235"), RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minusRetainScale((BigDecimal) null, RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_minusRetainScale_BigDecimalRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minusRetainScale(BIGDEC_2_34, (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // minusRetainScale(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_minusRetainScale_doubleRoundingMode_zero() {
        BigMoney test = GBP_2_34.minusRetainScale(0d, RoundingMode.UNNECESSARY);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_34.minusRetainScale(1.23d, RoundingMode.UNNECESSARY);
        assertEquals("GBP 1.11", test.toString());
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_34.minusRetainScale(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals("GBP 3.57", test.toString());
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.minusRetainScale(1.235d, RoundingMode.DOWN);
        assertEquals("GBP 1.10", test.toString());
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_roundUnecessary() {
        assertThrows(ArithmeticException.class, () -> {
            GBP_2_34.minusRetainScale(1.235d, RoundingMode.UNNECESSARY);
        });
    }

    @Test
    void test_minusRetainScale_doubleRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_M5_78.minusRetainScale(2.34d, (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // multipliedBy(BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_multipliedBy_BigDecimal_one() {
        BigMoney test = GBP_2_34.multipliedBy(BigDecimal.ONE);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_multipliedBy_BigDecimal_positive() {
        BigMoney test = GBP_2_33.multipliedBy(bd("2.5"));
        assertEquals("GBP 5.825", test.toString());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_multipliedBy_BigDecimal_negative() {
        BigMoney test = GBP_2_33.multipliedBy(bd("-2.5"));
        assertEquals("GBP -5.825", test.toString());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_multipliedBy_BigDecimal_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.multipliedBy((BigDecimal) null);
        });
    }

    //-----------------------------------------------------------------------
    // multipliedBy(double)
    //-----------------------------------------------------------------------
    @Test
    void test_multipliedBy_doubleRoundingMode_one() {
        BigMoney test = GBP_2_34.multipliedBy(1d);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_multipliedBy_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_33.multipliedBy(2.5d);
        assertEquals("GBP 5.825", test.toString());
        assertEquals(3, test.getScale());
    }

    @Test
    void test_multipliedBy_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_33.multipliedBy(-2.5d);
        assertEquals("GBP -5.825", test.toString());
        assertEquals(3, test.getScale());
    }

    //-----------------------------------------------------------------------
    // multipliedBy(long)
    //-----------------------------------------------------------------------
    @Test
    void test_multipliedBy_long_one() {
        BigMoney test = GBP_2_34.multipliedBy(1);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_multipliedBy_long_positive() {
        BigMoney test = GBP_2_34.multipliedBy(3);
        assertEquals("GBP 7.02", test.toString());
        assertEquals(2, test.getScale());
    }

    @Test
    void test_multipliedBy_long_negative() {
        BigMoney test = GBP_2_34.multipliedBy(-3);
        assertEquals("GBP -7.02", test.toString());
        assertEquals(2, test.getScale());
    }

    //-----------------------------------------------------------------------
    // multiplyRetainScale(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_one() {
        BigMoney test = GBP_2_34.multiplyRetainScale(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_positive() {
        BigMoney test = GBP_2_33.multiplyRetainScale(bd("2.5"), RoundingMode.DOWN);
        assertEquals("GBP 5.82", test.toString());
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_positive_halfUp() {
        BigMoney test = GBP_2_33.multiplyRetainScale(bd("2.5"), RoundingMode.HALF_UP);
        assertEquals("GBP 5.83", test.toString());
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_negative() {
        BigMoney test = GBP_2_33.multiplyRetainScale(bd("-2.5"), RoundingMode.FLOOR);
        assertEquals("GBP -5.83", test.toString());
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.multiplyRetainScale((BigDecimal) null, RoundingMode.DOWN);
        });
    }

    @Test
    void test_multiplyRetainScale_BigDecimalRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.multiplyRetainScale(bd("2.5"), (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // multiplyRetainScale(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_multiplyRetainScale_doubleRoundingMode_one() {
        BigMoney test = GBP_2_34.multiplyRetainScale(1d, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_multiplyRetainScale_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_33.multiplyRetainScale(2.5d, RoundingMode.DOWN);
        assertEquals("GBP 5.82", test.toString());
    }

    @Test
    void test_multiplyRetainScale_doubleRoundingMode_positive_halfUp() {
        BigMoney test = GBP_2_33.multiplyRetainScale(2.5d, RoundingMode.HALF_UP);
        assertEquals("GBP 5.83", test.toString());
    }

    @Test
    void test_multiplyRetainScale_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_33.multiplyRetainScale(-2.5d, RoundingMode.FLOOR);
        assertEquals("GBP -5.83", test.toString());
    }

    @Test
    void test_multiplyRetainScale_doubleRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.multiplyRetainScale(2.5d, (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // dividedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_dividedBy_BigDecimalRoundingMode_one() {
        BigMoney test = GBP_2_34.dividedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_positive() {
        BigMoney test = GBP_2_34.dividedBy(bd("2.5"), RoundingMode.DOWN);
        assertEquals("GBP 0.93", test.toString());
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_positive_halfUp() {
        BigMoney test = GBP_2_34.dividedBy(bd("2.5"), RoundingMode.HALF_UP);
        assertEquals("GBP 0.94", test.toString());
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_negative() {
        BigMoney test = GBP_2_34.dividedBy(bd("-2.5"), RoundingMode.FLOOR);
        assertEquals("GBP -0.94", test.toString());
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.dividedBy((BigDecimal) null, RoundingMode.DOWN);
        });
    }

    @Test
    void test_dividedBy_BigDecimalRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.dividedBy(bd("2.5"), (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // dividedBy(double,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_dividedBy_doubleRoundingMode_one() {
        BigMoney test = GBP_2_34.dividedBy(1d, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_dividedBy_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_34.dividedBy(2.5d, RoundingMode.DOWN);
        assertEquals("GBP 0.93", test.toString());
    }

    @Test
    void test_dividedBy_doubleRoundingMode_positive_halfUp() {
        BigMoney test = GBP_2_34.dividedBy(2.5d, RoundingMode.HALF_UP);
        assertEquals("GBP 0.94", test.toString());
    }

    @Test
    void test_dividedBy_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_34.dividedBy(-2.5d, RoundingMode.FLOOR);
        assertEquals("GBP -0.94", test.toString());
    }

    @Test
    void test_dividedBy_doubleRoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.dividedBy(2.5d, (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // dividedBy(long,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_dividedBy_long_one() {
        BigMoney test = GBP_2_34.dividedBy(1, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_dividedBy_long_positive() {
        BigMoney test = GBP_2_34.dividedBy(3, RoundingMode.DOWN);
        assertEquals("GBP 0.78", test.toString());
    }

    @Test
    void test_dividedBy_long_positive_roundDown() {
        BigMoney test = GBP_2_35.dividedBy(3, RoundingMode.DOWN);
        assertEquals("GBP 0.78", test.toString());
    }

    @Test
    void test_dividedBy_long_positive_roundUp() {
        BigMoney test = GBP_2_35.dividedBy(3, RoundingMode.UP);
        assertEquals("GBP 0.79", test.toString());
    }

    @Test
    void test_dividedBy_long_negative() {
        BigMoney test = GBP_2_34.dividedBy(-3, RoundingMode.DOWN);
        assertEquals("GBP -0.78", test.toString());
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    @Test
    void test_negated_zero() {
        BigMoney test = GBP_0_00.negated();
        assertSame(GBP_0_00, test);
    }

    @Test
    void test_negated_positive() {
        BigMoney test = GBP_2_34.negated();
        assertEquals("GBP -2.34", test.toString());
    }

    @Test
    void test_negated_negative() {
        BigMoney test = BigMoney.parse("GBP -2.34").negated();
        assertEquals("GBP 2.34", test.toString());
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    @Test
    void test_abs_positive() {
        BigMoney test = GBP_2_34.abs();
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_abs_negative() {
        BigMoney test = BigMoney.parse("GBP -2.34").abs();
        assertEquals("GBP 2.34", test.toString());
    }

    //-----------------------------------------------------------------------
    // rounded(int,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_round_2down() {
        BigMoney test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_round_2up() {
        BigMoney test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    @Test
    void test_round_1down() {
        BigMoney test = GBP_2_34.rounded(1, RoundingMode.DOWN);
        assertEquals("GBP 2.30", test.toString());
    }

    @Test
    void test_round_1up() {
        BigMoney test = GBP_2_34.rounded(1, RoundingMode.UP);
        assertEquals("GBP 2.40", test.toString());
    }

    @Test
    void test_round_0down() {
        BigMoney test = GBP_2_34.rounded(0, RoundingMode.DOWN);
        assertEquals("GBP 2.00", test.toString());
    }

    @Test
    void test_round_0up() {
        BigMoney test = GBP_2_34.rounded(0, RoundingMode.UP);
        assertEquals("GBP 3.00", test.toString());
    }

    @Test
    void test_round_M1down() {
        BigMoney test = BigMoney.parse("GBP 432.34").rounded(-1, RoundingMode.DOWN);
        assertEquals("GBP 430.00", test.toString());
    }

    @Test
    void test_round_M1up() {
        BigMoney test = BigMoney.parse("GBP 432.34").rounded(-1, RoundingMode.UP);
        assertEquals("GBP 440.00", test.toString());
    }

    @Test
    void test_round_3() {
        BigMoney test = GBP_2_34.rounded(3, RoundingMode.DOWN);
        assertSame(GBP_2_34, test);
    }

    //-----------------------------------------------------------------------
    // convertedTo(CurrencyUnit,BigDecimal)
    //-----------------------------------------------------------------------
    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_positive() {
        BigMoney test = GBP_2_33.convertedTo(EUR, bd("2.5"));
        assertEquals("EUR 5.825", test.toString());
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_sameCurrencyCorrectFactor() {
        BigMoney test = GBP_2_33.convertedTo(GBP, bd("1.00000"));
        assertEquals(GBP_2_33, test);
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_negative() {
        assertThrows(IllegalArgumentException.class, () -> {
            GBP_2_33.convertedTo(EUR, bd("-2.5"));
        });
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_sameCurrencyWrongFactor() {
        assertThrows(IllegalArgumentException.class, () -> {
            GBP_2_33.convertedTo(GBP, bd("2.5"));
        });
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.convertedTo((CurrencyUnit) null, bd("2"));
        });
    }

    @Test
    void test_convertedTo_CurrencyUnit_BigDecimal_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.convertedTo(EUR, (BigDecimal) null);
        });
    }

    //-----------------------------------------------------------------------
    // convertRetainScale(CurrencyUnit,BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_positive() {
        BigMoney test = BigMoney.parse("GBP 2.2").convertRetainScale(EUR, bd("2.5"), RoundingMode.DOWN);
        assertEquals("EUR 5.5", test.toString());
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_roundHalfUp() {
        BigMoney test = BigMoney.parse("GBP 2.21").convertRetainScale(EUR, bd("2.5"), RoundingMode.HALF_UP);
        assertEquals("EUR 5.53", test.toString());
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_negative() {
        assertThrows(IllegalArgumentException.class, () -> {
            GBP_2_33.convertRetainScale(EUR, bd("-2.5"), RoundingMode.DOWN);
        });
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_sameCurrency() {
        assertThrows(IllegalArgumentException.class, () -> {
            GBP_2_33.convertRetainScale(GBP, bd("2.5"), RoundingMode.DOWN);
        });
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_nullCurrency() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.convertRetainScale((CurrencyUnit) null, bd("2"), RoundingMode.DOWN);
        });
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_nullBigDecimal() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.convertRetainScale(EUR, (BigDecimal) null, RoundingMode.DOWN);
        });
    }

    @Test
    void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_nullRoundingMode() {
        assertThrows(NullPointerException.class, () -> {
            GBP_5_78.convertRetainScale(EUR, bd("2"), (RoundingMode) null);
        });
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    @Test
    void test_toBigMoney() {
        assertSame(GBP_2_34, GBP_2_34.toBigMoney());
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    @Test
    void test_toMoney() {
        assertEquals(Money.of(GBP, BIGDEC_2_34), GBP_2_34.toMoney());
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    @Test
    void test_toMoney_RoundingMode() {
        assertEquals(Money.parse("GBP 2.34"), GBP_2_34.toMoney(RoundingMode.HALF_EVEN));
    }

    @Test
    void test_toMoney_RoundingMode_round() {
        BigMoney money = BigMoney.parse("GBP 2.355");
        assertEquals(Money.parse("GBP 2.36"), money.toMoney(RoundingMode.HALF_EVEN));
    }

    //-----------------------------------------------------------------------
    // isSameCurrency(Money)
    //-----------------------------------------------------------------------
    @Test
    void test_isSameCurrency_BigMoney_same() {
        assertTrue(GBP_2_34.isSameCurrency(GBP_2_35));
    }

    @Test
    void test_isSameCurrency_BigMoney_different() {
        assertFalse(GBP_2_34.isSameCurrency(USD_2_34));
    }

    @Test
    void test_isSameCurrency_Money_same() {
        assertTrue(GBP_2_34.isSameCurrency(Money.parse("GBP 2")));
    }

    @Test
    void test_isSameCurrency_Money_different() {
        assertFalse(GBP_2_34.isSameCurrency(Money.parse("USD 2")));
    }

    @Test
    void test_isSameCurrency_Money_nullMoney() {
        assertThrows(NullPointerException.class, () -> {
            GBP_2_34.isSameCurrency((BigMoney) null);
        });
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    void test_compareTo_BigMoney() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
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
    void test_compareTo_Money() {
        BigMoney t = GBP_2_35;
        Money a = Money.ofMinor(GBP, 234);
        Money b = Money.ofMinor(GBP, 235);
        Money c = Money.ofMinor(GBP, 236);
        assertEquals(1, t.compareTo(a));
        assertEquals(0, t.compareTo(b));
        assertEquals(-1, t.compareTo(c));
    }

    @Test
    void test_compareTo_currenciesDiffer() {
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
        assertThrows(CurrencyMismatchException.class, () -> {
            a.compareTo(b);
        });
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void test_compareTo_wrongType() {
        Comparable a = GBP_2_34;
        assertThrows(ClassCastException.class, () -> {
            a.compareTo("NotRightType");
        });
    }

    //-----------------------------------------------------------------------
    // isEqual()
    //-----------------------------------------------------------------------
    @Test
    void test_isEqual() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
        assertTrue(a.isEqual(a));
        assertTrue(b.isEqual(b));
        assertTrue(c.isEqual(c));

        assertFalse(a.isEqual(b));
        assertFalse(b.isEqual(a));

        assertFalse(a.isEqual(c));
        assertFalse(c.isEqual(a));

        assertFalse(b.isEqual(c));
        assertFalse(c.isEqual(b));
    }

    @Test
    void test_isEqual_Money() {
        BigMoney a = GBP_2_34;
        Money b = Money.ofMinor(GBP, 234);
        assertTrue(a.isEqual(b));
    }

    @Test
    void test_isEqual_currenciesDiffer() {
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
        assertThrows(CurrencyMismatchException.class, () -> {
            a.isEqual(b);
        });
    }

    //-----------------------------------------------------------------------
    // isGreaterThan()
    //-----------------------------------------------------------------------
    @Test
    void test_isGreaterThan() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
        assertFalse(a.isGreaterThan(a));
        assertFalse(a.isGreaterThan(b));
        assertFalse(a.isGreaterThan(c));

        assertTrue(b.isGreaterThan(a));
        assertFalse(b.isGreaterThan(b));
        assertFalse(b.isGreaterThan(c));

        assertTrue(c.isGreaterThan(a));
        assertTrue(c.isGreaterThan(b));
        assertFalse(c.isGreaterThan(c));
    }

    @Test
    void test_isGreaterThan_currenciesDiffer() {
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
        assertThrows(CurrencyMismatchException.class, () -> {
            a.isGreaterThan(b);
        });
    }

    //-----------------------------------------------------------------------
    // isGreaterThanOrEqual()
    //-----------------------------------------------------------------------
    @Test
    void test_isGreaterThanOrEqual() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
        assertTrue(a.isGreaterThanOrEqual(a));
        assertFalse(a.isGreaterThanOrEqual(b));
        assertFalse(a.isGreaterThanOrEqual(c));

        assertTrue(b.isGreaterThanOrEqual(a));
        assertTrue(b.isGreaterThanOrEqual(b));
        assertFalse(b.isGreaterThanOrEqual(c));

        assertTrue(c.isGreaterThanOrEqual(a));
        assertTrue(c.isGreaterThanOrEqual(b));
        assertTrue(c.isGreaterThanOrEqual(c));
    }

    @Test
    void test_isGreaterThanOrEqual_currenciesDiffer() {
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
        assertThrows(CurrencyMismatchException.class, () -> {
            a.isGreaterThanOrEqual(b);
        });
    }

    //-----------------------------------------------------------------------
    // isLessThan()
    //-----------------------------------------------------------------------
    @Test
    void test_isLessThan() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
        assertFalse(a.isLessThan(a));
        assertTrue(a.isLessThan(b));
        assertTrue(a.isLessThan(c));

        assertFalse(b.isLessThan(a));
        assertFalse(b.isLessThan(b));
        assertTrue(b.isLessThan(c));

        assertFalse(c.isLessThan(a));
        assertFalse(c.isLessThan(b));
        assertFalse(c.isLessThan(c));
    }

    @Test
    void test_isLessThan_currenciesDiffer() {
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
        assertThrows(CurrencyMismatchException.class, () -> {
            a.isLessThan(b);
        });
    }

    //-----------------------------------------------------------------------
    // isLessThanOrEqual()
    //-----------------------------------------------------------------------
    @Test
    void test_isLessThanOrEqual() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
        assertTrue(a.isLessThanOrEqual(a));
        assertTrue(a.isLessThanOrEqual(b));
        assertTrue(a.isLessThanOrEqual(c));

        assertFalse(b.isLessThanOrEqual(a));
        assertTrue(b.isLessThanOrEqual(b));
        assertTrue(b.isLessThanOrEqual(c));

        assertFalse(c.isLessThanOrEqual(a));
        assertFalse(c.isLessThanOrEqual(b));
        assertTrue(c.isLessThanOrEqual(c));
    }

    @Test
    void test_isLessThanOrEqual_currenciesDiffer() {
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
        assertThrows(CurrencyMismatchException.class, () -> {
            a.isLessThanOrEqual(b);
        });
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    @Test
    void test_equals_hashCode_positive() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_34;
        BigMoney c = GBP_2_35;
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));
        assertTrue(c.equals(c));

        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertTrue(a.hashCode() == b.hashCode());

        assertFalse(a.equals(c));
        assertFalse(b.equals(c));
    }

    @Test
    void test_equals_false() {
        BigMoney a = GBP_2_34;
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    void test_toString_positive() {
        BigMoney test = BigMoney.of(GBP, BIGDEC_2_34);
        assertEquals("GBP 2.34", test.toString());
    }

    @Test
    void test_toString_negative() {
        BigMoney test = BigMoney.of(EUR, BIGDEC_M5_78);
        assertEquals("EUR -5.78", test.toString());
    }

}
