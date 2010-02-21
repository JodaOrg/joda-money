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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.testng.annotations.Test;

/**
 * Test BigMoney.
 */
@Test
public class TestBigMoney {

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
    private static final BigMoney GBP_LONG_MAX_MAJOR_PLUS1 = BigMoney.of(GBP,
            BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
    private static final BigMoney GBP_LONG_MIN_MAJOR_MINUS1 = BigMoney.of(GBP,
            BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)));
    private static final BigMoney JPY_423 = BigMoney.parse("JPY 423");
    private static final BigMoney USD_1_23 = BigMoney.parse("USD 1.23");
    private static final BigMoney USD_2_34 = BigMoney.parse("USD 2.34");
    private static final BigMoney USD_2_35 = BigMoney.parse("USD 2.35");

    private static BigDecimal bd(String str) {
        return new BigDecimal(str);
    }

    //-----------------------------------------------------------------------
    // of(Currency,BigDecimal)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency_BigDecimal() {
        BigMoney test = BigMoney.of(GBP, BIGDEC_2_345);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BIGDEC_2_345);
        assertEquals(test.getScale(), 3);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullCurrency() {
        BigMoney.of((CurrencyUnit) null, BIGDEC_2_345);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_BigDecimal_nullBigDecimal() {
        BigMoney.of(GBP, (BigDecimal) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_of_Currency_subClass1() {
        class BadDecimal extends BigDecimal {
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
        BigMoney.of(GBP, sub);
    }

    public void test_factory_of_Currency_subClass2() {
        class BadInteger extends BigInteger {
            public BadInteger() {
                super("123");
            }
        }
        class BadDecimal extends BigDecimal {
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
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("12.3"));
        assertEquals(test.getScale(), 1);
        assertEquals(test.getAmount().getClass() == BigDecimal.class, true);
    }

    //-----------------------------------------------------------------------
    // of(Currency,double)
    //-----------------------------------------------------------------------
    public void test_factory_of_Currency_double() {
        BigMoney test = BigMoney.of(GBP, 2.345d);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BIGDEC_2_345);
        assertEquals(test.getScale(), 3);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_Currency_double_nullCurrency() {
        BigMoney.of((CurrencyUnit) null, 2.345d);
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,BigDecimal, int)
    //-----------------------------------------------------------------------
    public void test_factory_ofScale_Currency_BigDecimal_int() {
        BigMoney test = BigMoney.ofScale(GBP, BIGDEC_2_34, 4);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(23400, 4));
    }

    public void test_factory_ofScale_Currency_BigDecimal_negativeScale() {
        BigMoney test = BigMoney.ofScale(GBP, BigDecimal.valueOf(23400), -2);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(234, -2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_ofScale_Currency_BigDecimal_invalidScale() {
        BigMoney.ofScale(GBP, BIGDEC_2_345, 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofScale_Currency_BigDecimal_nullCurrency() {
        BigMoney.ofScale((CurrencyUnit) null, BIGDEC_2_34, 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofScale_Currency_BigDecimal_nullBigDecimal() {
        BigMoney.ofScale(GBP, (BigDecimal) null, 2);
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,BigDecimal,int,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_DOWN() {
        BigMoney test = BigMoney.ofScale(GBP, BIGDEC_2_34, 1, RoundingMode.DOWN);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(23, 1));
    }

    public void test_factory_ofScale_Currency_BigDecimal_int_JPY_RoundingMode_UP() {
        BigMoney test = BigMoney.ofScale(JPY, BIGDEC_2_34, 0, RoundingMode.UP);
        assertEquals(test.getCurrencyUnit(), JPY);
        assertEquals(test.getAmount(), BigDecimal.valueOf(3, 0));
    }

    public void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_negativeScale() {
        BigMoney test = BigMoney.ofScale(GBP, BigDecimal.valueOf(23400), -2, RoundingMode.DOWN);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(234, -2));
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_UNNECESSARY() {
        BigMoney.ofScale(JPY, BIGDEC_2_34, 1, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_nullCurrency() {
        BigMoney.ofScale((CurrencyUnit) null, BIGDEC_2_34, 2, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_nullBigDecimal() {
        BigMoney.ofScale(GBP, (BigDecimal) null, 2, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofScale_Currency_BigDecimal_int_RoundingMode_nullRoundingMode() {
        BigMoney.ofScale(GBP, BIGDEC_2_34, 2, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // ofScale(Currency,long, int)
    //-----------------------------------------------------------------------
    public void test_factory_ofScale_Currency_long_int() {
        BigMoney test = BigMoney.ofScale(GBP, 234, 4);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(234, 4));
    }

    public void test_factory_ofScale_Currency_long_int_negativeScale() {
        BigMoney test = BigMoney.ofScale(GBP, 234, -4);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(234, -4));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofScale_Currency_long_int_nullCurrency() {
        BigMoney.ofScale((CurrencyUnit) null, 234, 2);
    }

    //-----------------------------------------------------------------------
    // ofMajor(Currency,long)
    //-----------------------------------------------------------------------
    public void test_factory_ofMajor_Currency_long() {
        BigMoney test = BigMoney.ofMajor(GBP, 234);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("234"));
        assertEquals(test.getScale(), 0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofMajor_Currency_long_nullCurrency() {
        BigMoney.ofMajor((CurrencyUnit) null, 234);
    }

    //-----------------------------------------------------------------------
    // ofMinor(Currency,long)
    //-----------------------------------------------------------------------
    public void test_factory_ofMinor_Currency_long() {
        BigMoney test = BigMoney.ofMinor(GBP, 234);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("2.34"));
        assertEquals(test.getScale(), 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_ofMinor_Currency_long_nullCurrency() {
        BigMoney.ofMinor((CurrencyUnit) null, 234);
    }

    //-----------------------------------------------------------------------
    // zero(Currency)
    //-----------------------------------------------------------------------
    public void test_factory_zero_Currency() {
        BigMoney test = BigMoney.zero(GBP);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.ZERO);
        assertEquals(test.getScale(), 0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_zero_Currency_nullCurrency() {
        BigMoney.zero((CurrencyUnit) null);
    }

    //-----------------------------------------------------------------------
    // zero(Currency, int)
    //-----------------------------------------------------------------------
    public void test_factory_zero_Currency_int() {
        BigMoney test = BigMoney.zero(GBP, 3);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(0, 3));
    }

    public void test_factory_zero_Currency_int_negativeScale() {
        BigMoney test = BigMoney.zero(GBP, -3);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(0, -3));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_zero_Currency_int_nullCurrency() {
        BigMoney.zero((CurrencyUnit) null, 3);
    }

    //-----------------------------------------------------------------------
    // from(BigMoneyProvider)
    //-----------------------------------------------------------------------
    public void test_factory_from_BigMoneyProvider() {
        BigMoney test = BigMoney.from(BigMoney.parse("GBP 104.23"));
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 10423);
        assertEquals(test.getScale(), 2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_nullBigMoneyProvider() {
        BigMoney.from((BigMoneyProvider) null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_from_BigMoneyProvider_badProvider() {
        BigMoney.from(new BigMoneyProvider() {
            public BigMoney toBigMoney() {
                return null;  // shouldn't return null
            }
        });
    }

    //-----------------------------------------------------------------------
    // total(Iterable)
    //-----------------------------------------------------------------------
    public void test_factory_total_Iterable() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_1_23, GBP_2_33, BigMoney.of(GBP, 2.361d));
        BigMoney test = BigMoney.total(iterable);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(5921, 3));
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void test_factory_total_Iterable_empty() {
        Iterable<BigMoney> iterable = Collections.emptyList();
        BigMoney.total(iterable);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_Iterable_currenciesDiffer() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, JPY_423);
        BigMoney.total(iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_Iterable_nullFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        BigMoney.total(iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_Iterable_nullNotFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        BigMoney.total(iterable);
    }

    //-----------------------------------------------------------------------
    // total(CurrencyUnit,Iterable)
    //-----------------------------------------------------------------------
    public void test_factory_total_CurrencyUnitIterable() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_1_23, GBP_2_33, BigMoney.of(GBP, 2.361d));
        BigMoney test = BigMoney.total(GBP, iterable);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), BigDecimal.valueOf(5921, 3));
    }

    public void test_factory_total_CurrencyUnitIterable_empty() {
        Iterable<BigMoney> iterable = Collections.emptyList();
        BigMoney test = BigMoney.total(GBP, iterable);
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmountMinorInt(), 0);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_factory_total_CurrencyUnitIterable_currenciesDiffer() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, JPY_423);
        BigMoney.total(GBP, iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_CurrencyUnitIterable_nullFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(null, GBP_2_33, GBP_2_36);
        BigMoney.total(GBP, iterable);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_total_CurrencyUnitIterable_nullNotFirst() {
        Iterable<BigMoney> iterable = Arrays.asList(GBP_2_33, null, GBP_2_36);
        BigMoney.total(GBP, iterable);
    }

    //-----------------------------------------------------------------------
    // parse(String)
    //-----------------------------------------------------------------------
    public void test_factory_parse_String() {
        BigMoney test = BigMoney.parse("GBP 2.43");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("2.43"));
        assertEquals(test.getScale(), 2);
    }

    public void test_factory_parse_String_positive() {
        BigMoney test = BigMoney.parse("GBP +2.43");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("2.43"));
        assertEquals(test.getScale(), 2);
    }

    public void test_factory_parse_String_negative() {
        BigMoney test = BigMoney.parse("GBP -5.87");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("-5.87"));
        assertEquals(test.getScale(), 2);
    }

    public void test_factory_parse_String_decimalStart() {
        BigMoney test = BigMoney.parse("GBP .43");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("0.43"));
        assertEquals(test.getScale(), 2);
    }

    public void test_factory_parse_String_decimalStartPositive() {
        BigMoney test = BigMoney.parse("GBP +.43");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("0.43"));
        assertEquals(test.getScale(), 2);
    }

    public void test_factory_parse_String_decimalStartNegative() {
        BigMoney test = BigMoney.parse("GBP -.43");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("-0.43"));
        assertEquals(test.getScale(), 2);
    }

    public void test_factory_parse_String_decimalEnd() {
        BigMoney test = BigMoney.parse("GBP 43.");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("43"));
        assertEquals(test.getScale(), 0);
    }

    public void test_factory_parse_String_decimalEndPositive() {
        BigMoney test = BigMoney.parse("GBP +43.");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("43"));
        assertEquals(test.getScale(), 0);
    }

    public void test_factory_parse_String_decimalEndNegative() {
        BigMoney test = BigMoney.parse("GBP -43.");
        assertEquals(test.getCurrencyUnit(), GBP);
        assertEquals(test.getAmount(), bd("-43"));
        assertEquals(test.getScale(), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_tooShort() {
        BigMoney.parse("GBP ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_noSpace() {
        BigMoney.parse("GBP2.34");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_exponent() {
        BigMoney.parse("GBP 234E2");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_parse_String_badCurrency() {
        BigMoney.parse("GBX 2.34");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_parse_String_nullString() {
        BigMoney.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // nonNull(BigMoney,CurrencyUnit)
    //-----------------------------------------------------------------------
    public void test_nonNull_BigMoneyCurrencyUnit_nonNull() {
        BigMoney test = BigMoney.nonNull(GBP_1_23, GBP);
        assertSame(test, GBP_1_23);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_nonNull_BigMoneyCurrencyUnit_nonNullCurrencyMismatch() {
        BigMoney.nonNull(GBP_1_23, JPY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_nonNull_BigMoneyCurrencyUnit_nonNull_nullCurrency() {
        BigMoney.nonNull(GBP_1_23, null);
    }

    public void test_nonNull_BigMoneyCurrencyUnit_null() {
        BigMoney test = BigMoney.nonNull(null, GBP);
        assertEquals(test, BigMoney.ofMajor(GBP, 0));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_nonNull_BigMoneyCurrencyUnit_null_nullCurrency() {
        BigMoney.nonNull(null, null);
    }

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    public void test_constructor_null1() throws Exception {
        Constructor<BigMoney> con = BigMoney.class.getDeclaredConstructor(CurrencyUnit.class, BigDecimal.class);
        assertEquals(Modifier.isPrivate(con.getModifiers()), true);
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] { null, BIGDEC_2_34 });
            fail();
        } catch (InvocationTargetException ex) {
            assertEquals(ex.getCause().getClass(), AssertionError.class);
        }
    }

    public void test_constructor_null2() throws Exception {
        Constructor<BigMoney> con = BigMoney.class.getDeclaredConstructor(CurrencyUnit.class, BigDecimal.class);
        try {
            con.setAccessible(true);
            con.newInstance(new Object[] { GBP, null });
            fail();
        } catch (InvocationTargetException ex) {
            assertEquals(ex.getCause().getClass(), AssertionError.class);
        }
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        BigMoney a = BigMoney.parse("GBP 2.34");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(a);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        BigMoney input = (BigMoney) ois.readObject();
        assertEquals(input, a);
    }

    //-----------------------------------------------------------------------
    // getCurrencyUnit()
    //-----------------------------------------------------------------------
    public void test_getCurrencyUnit_GBP() {
        assertEquals(GBP_2_34.getCurrencyUnit(), GBP);
    }

    public void test_getCurrencyUnit_EUR() {
        assertEquals(BigMoney.parse("EUR -5.78").getCurrencyUnit(), EUR);
    }

    //-----------------------------------------------------------------------
    // withCurrencyUnit(Currency)
    //-----------------------------------------------------------------------
    public void test_withCurrencyUnit_Currency() {
        BigMoney test = GBP_2_34.withCurrencyUnit(USD);
        assertEquals(test.toString(), "USD 2.34");
    }

    public void test_withCurrencyUnit_Currency_same() {
        BigMoney test = GBP_2_34.withCurrencyUnit(GBP);
        assertSame(test, GBP_2_34);
    }

    public void test_withCurrencyUnit_Currency_differentCurrencyScale() {
        BigMoney test = GBP_2_34.withCurrencyUnit(JPY);
        assertEquals(test.toString(), "JPY 2.34");
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
    // isCurrencyScale()
    //-----------------------------------------------------------------------
    public void test_isCurrencyScale_GBP() {
        assertEquals(BigMoney.parse("GBP 2").isCurrencyScale(), false);
        assertEquals(BigMoney.parse("GBP 2.3").isCurrencyScale(), false);
        assertEquals(BigMoney.parse("GBP 2.34").isCurrencyScale(), true);
        assertEquals(BigMoney.parse("GBP 2.345").isCurrencyScale(), false);
    }

    public void test_isCurrencyScale_JPY() {
        assertEquals(BigMoney.parse("JPY 2").isCurrencyScale(), true);
        assertEquals(BigMoney.parse("JPY 2.3").isCurrencyScale(), false);
        assertEquals(BigMoney.parse("JPY 2.34").isCurrencyScale(), false);
        assertEquals(BigMoney.parse("JPY 2.345").isCurrencyScale(), false);
    }

    //-----------------------------------------------------------------------
    // withScale(int)
    //-----------------------------------------------------------------------
    public void test_withScale_int_same() {
        BigMoney test = GBP_2_34.withScale(2);
        assertSame(test, GBP_2_34);
    }

    public void test_withScale_int_more() {
        BigMoney test = GBP_2_34.withScale(3);
        assertEquals(test.getAmount(), bd("2.340"));
        assertEquals(test.getScale(), 3);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_withScale_int_less() {
        BigMoney.parse("GBP 2.345").withScale(2);
    }

    //-----------------------------------------------------------------------
    // withScale(int,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_withScale_intRoundingMode_less() {
        BigMoney test = GBP_2_34.withScale(1, RoundingMode.UP);
        assertEquals(test.getAmount(), bd("2.4"));
        assertEquals(test.getScale(), 1);
    }

    public void test_withScale_intRoundingMode_more() {
        BigMoney test = GBP_2_34.withScale(3, RoundingMode.UP);
        assertEquals(test.getAmount(), bd("2.340"));
        assertEquals(test.getScale(), 3);
    }

    //-----------------------------------------------------------------------
    // withCurrencyScale()
    //-----------------------------------------------------------------------
    public void test_withCurrencyScale_int_same() {
        BigMoney test = GBP_2_34.withCurrencyScale();
        assertSame(test, GBP_2_34);
    }

    public void test_withCurrencyScale_int_more() {
        BigMoney test = BigMoney.parse("GBP 2.3").withCurrencyScale();
        assertEquals(test.getAmount(), bd("2.30"));
        assertEquals(test.getScale(), 2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_withCurrencyScale_int_less() {
        BigMoney.parse("GBP 2.345").withCurrencyScale();
    }

    //-----------------------------------------------------------------------
    // withCurrencyScale(RoundingMode)
    //-----------------------------------------------------------------------
    public void test_withCurrencyScale_intRoundingMode_less() {
        BigMoney test = BigMoney.parse("GBP 2.345").withCurrencyScale(RoundingMode.UP);
        assertEquals(test.getAmount(), bd("2.35"));
        assertEquals(test.getScale(), 2);
    }

    public void test_withCurrencyScale_intRoundingMode_more() {
        BigMoney test = BigMoney.parse("GBP 2.3").withCurrencyScale(RoundingMode.UP);
        assertEquals(test.getAmount(), bd("2.30"));
        assertEquals(test.getScale(), 2);
    }

    public void test_withCurrencyScale_intRoundingMode_lessJPY() {
        BigMoney test = BigMoney.parse("JPY 2.345").withCurrencyScale(RoundingMode.UP);
        assertEquals(test.getAmount(), bd("3"));
        assertEquals(test.getScale(), 0);
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
        BigMoney test = GBP_2_34.withAmount(BIGDEC_2_345);
        assertEquals(test.getAmount(), bd("2.345"));
        assertEquals(test.getScale(), 3);
    }

    public void test_withAmount_BigDecimal_same() {
        BigMoney test = GBP_2_34.withAmount(BIGDEC_2_34);
        assertSame(test, GBP_2_34);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_withAmount_BigDecimal_nullBigDecimal() {
        GBP_2_34.withAmount((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // withAmount(double)
    //-----------------------------------------------------------------------
    public void test_withAmount_double() {
        BigMoney test = GBP_2_34.withAmount(2.345d);
        assertEquals(test.getAmount(), bd("2.345"));
        assertEquals(test.getScale(), 3);
    }

    public void test_withAmount_double_same() {
        BigMoney test = GBP_2_34.withAmount(2.34d);
        assertSame(test, GBP_2_34);
    }

    //-----------------------------------------------------------------------
    // plus(BigMoneyProvider)
    //-----------------------------------------------------------------------
    public void test_plus_BigMoneyProvider_zero() {
        BigMoney test = GBP_2_34.plus(GBP_0_00);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_BigMoneyProvider_positive() {
        BigMoney test = GBP_2_34.plus(GBP_1_23);
        assertEquals(test.toString(), "GBP 3.57");
        assertEquals(test.getScale(), 2);
    }

    public void test_plus_BigMoneyProvider_negative() {
        BigMoney test = GBP_2_34.plus(GBP_M1_23);
        assertEquals(test.toString(), "GBP 1.11");
        assertEquals(test.getScale(), 2);
    }

    public void test_plus_BigMoneyProvider_scale() {
        BigMoney test = GBP_2_34.plus(BigMoney.parse("GBP 1.111"));
        assertEquals(test.toString(), "GBP 3.451");
        assertEquals(test.getScale(), 3);
    }

    public void test_plus_BigMoneyProvider_Money() {
        BigMoney test = GBP_2_34.plus(BigMoney.ofMinor(GBP, 1));
        assertEquals(test.toString(), "GBP 2.35");
        assertEquals(test.getScale(), 2);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_plus_BigMoneyProvider_currencyMismatch() {
        GBP_M5_78.plus(USD_1_23);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_BigMoneyProvider_nullBigMoneyProvider() {
        GBP_M5_78.plus((BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // plus(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_plus_BigDecimal_zero() {
        BigMoney test = GBP_2_34.plus(BigDecimal.ZERO);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_BigDecimal_positive() {
        BigMoney test = GBP_2_34.plus(bd("1.23"));
        assertEquals(test.toString(), "GBP 3.57");
        assertEquals(test.getScale(), 2);
    }

    public void test_plus_BigDecimal_negative() {
        BigMoney test = GBP_2_34.plus(bd("-1.23"));
        assertEquals(test.toString(), "GBP 1.11");
        assertEquals(test.getScale(), 2);
    }

    public void test_plus_BigDecimal_scale() {
        BigMoney test = GBP_2_34.plus(bd("1.235"));
        assertEquals(test.toString(), "GBP 3.575");
        assertEquals(test.getScale(), 3);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plus_BigDecimal_nullBigDecimal() {
        GBP_M5_78.plus((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // plus(double)
    //-----------------------------------------------------------------------
    public void test_plus_double_zero() {
        BigMoney test = GBP_2_34.plus(0d);
        assertSame(test, GBP_2_34);
    }

    public void test_plus_double_positive() {
        BigMoney test = GBP_2_34.plus(1.23d);
        assertEquals(test.toString(), "GBP 3.57");
        assertEquals(test.getScale(), 2);
    }

    public void test_plus_double_negative() {
        BigMoney test = GBP_2_34.plus(-1.23d);
        assertEquals(test.toString(), "GBP 1.11");
        assertEquals(test.getScale(), 2);
    }

    public void test_plus_double_scale() {
        BigMoney test = GBP_2_34.plus(1.234d);
        assertEquals(test.toString(), "GBP 3.574");
        assertEquals(test.getScale(), 3);
    }

    //-----------------------------------------------------------------------
    // plusMajor(long)
    //-----------------------------------------------------------------------
    public void test_plusMajor_zero() {
        BigMoney test = GBP_2_34.plusMajor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_plusMajor_positive() {
        BigMoney test = GBP_2_34.plusMajor(123);
        assertEquals(test.toString(), "GBP 125.34");
        assertEquals(test.getScale(), 2);
    }

    public void test_plusMajor_negative() {
        BigMoney test = GBP_2_34.plusMajor(-123);
        assertEquals(test.toString(), "GBP -120.66");
        assertEquals(test.getScale(), 2);
    }

    //-----------------------------------------------------------------------
    // plusMinor(long)
    //-----------------------------------------------------------------------
    public void test_plusMinor_zero() {
        BigMoney test = GBP_2_34.plusMinor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_plusMinor_positive() {
        BigMoney test = GBP_2_34.plusMinor(123);
        assertEquals(test.toString(), "GBP 3.57");
        assertEquals(test.getScale(), 2);
    }

    public void test_plusMinor_negative() {
        BigMoney test = GBP_2_34.plusMinor(-123);
        assertEquals(test.toString(), "GBP 1.11");
        assertEquals(test.getScale(), 2);
    }

    public void test_plusMinor_scale() {
        BigMoney test = BigMoney.parse("GBP 12").plusMinor(123);
        assertEquals(test.toString(), "GBP 13.23");
        assertEquals(test.getScale(), 2);
    }

    //-----------------------------------------------------------------------
    // plusRetainScale(BigMoneyProvider,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_plusRetainScale_BigMoneyProviderRoundingMode_zero() {
        BigMoney test = GBP_2_34.plusRetainScale(BigMoney.zero(GBP), RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_plusRetainScale_BigMoneyProviderRoundingMode_positive() {
        BigMoney test = GBP_2_34.plusRetainScale(BigMoney.parse("GBP 1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plusRetainScale_BigMoneyProviderRoundingMode_negative() {
        BigMoney test = GBP_2_34.plusRetainScale(BigMoney.parse("GBP -1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_plusRetainScale_BigMoneyProviderRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.plusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plusRetainScale_BigMoneyProviderRoundingMode_roundUnecessary() {
        GBP_2_34.plusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plusRetainScale_BigMoneyProviderRoundingMode_nullBigDecimal() {
        GBP_M5_78.plusRetainScale((BigDecimal) null, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plusRetainScale_BigMoneyProviderRoundingMode_nullRoundingMode() {
        GBP_M5_78.plusRetainScale(BigMoney.parse("GBP 1.23"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // plusRetainScale(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_plusRetainScale_BigDecimalRoundingMode_zero() {
        BigMoney test = GBP_2_34.plusRetainScale(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_plusRetainScale_BigDecimalRoundingMode_positive() {
        BigMoney test = GBP_2_34.plusRetainScale(bd("1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plusRetainScale_BigDecimalRoundingMode_negative() {
        BigMoney test = GBP_2_34.plusRetainScale(bd("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_plusRetainScale_BigDecimalRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.plusRetainScale(bd("1.235"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plusRetainScale_BigDecimalRoundingMode_roundUnecessary() {
        GBP_2_34.plusRetainScale(bd("1.235"), RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plusRetainScale_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_M5_78.plusRetainScale((BigDecimal) null, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plusRetainScale_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_M5_78.plusRetainScale(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // plusRetainScale(double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_plusRetainScale_doubleRoundingMode_zero() {
        BigMoney test = GBP_2_34.plusRetainScale(0d, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_plusRetainScale_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_34.plusRetainScale(1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_plusRetainScale_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_34.plusRetainScale(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_plusRetainScale_doubleRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.plusRetainScale(1.235d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 3.57");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_plusRetainScale_doubleRoundingMode_roundUnecessary() {
        GBP_2_34.plusRetainScale(1.235d, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_plusRetainScale_doubleRoundingMode_nullRoundingMode() {
        GBP_M5_78.plusRetainScale(2.34d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // minus(BigMoneyProvider)
    //-----------------------------------------------------------------------
    public void test_minus_BigMoneyProvider_zero() {
        BigMoney test = GBP_2_34.minus(GBP_0_00);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_BigMoneyProvider_positive() {
        BigMoney test = GBP_2_34.minus(GBP_1_23);
        assertEquals(test.toString(), "GBP 1.11");
        assertEquals(test.getScale(), 2);
    }

    public void test_minus_BigMoneyProvider_negative() {
        BigMoney test = GBP_2_34.minus(GBP_M1_23);
        assertEquals(test.toString(), "GBP 3.57");
        assertEquals(test.getScale(), 2);
    }

    public void test_minus_BigMoneyProvider_scale() {
        BigMoney test = GBP_2_34.minus(BigMoney.parse("GBP 1.111"));
        assertEquals(test.toString(), "GBP 1.229");
        assertEquals(test.getScale(), 3);
    }

    public void test_minus_BigMoneyProvider_Money() {
        BigMoney test = GBP_2_34.minus(BigMoney.ofMinor(GBP, 1));
        assertEquals(test.toString(), "GBP 2.33");
        assertEquals(test.getScale(), 2);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_minus_BigMoneyProvider_currencyMismatch() {
        GBP_M5_78.minus(USD_1_23);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_BigMoneyProvider_nullBigMoneyProvider() {
        GBP_M5_78.minus((BigMoneyProvider) null);
    }

    //-----------------------------------------------------------------------
    // minus(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_minus_BigDecimal_zero() {
        BigMoney test = GBP_2_34.minus(BigDecimal.ZERO);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_BigDecimal_positive() {
        BigMoney test = GBP_2_34.minus(bd("1.23"));
        assertEquals(test.toString(), "GBP 1.11");
        assertEquals(test.getScale(), 2);
    }

    public void test_minus_BigDecimal_negative() {
        BigMoney test = GBP_2_34.minus(bd("-1.23"));
        assertEquals(test.toString(), "GBP 3.57");
        assertEquals(test.getScale(), 2);
    }

    public void test_minus_BigDecimal_scale() {
        BigMoney test = GBP_2_34.minus(bd("1.235"));
        assertEquals(test.toString(), "GBP 1.105");
        assertEquals(test.getScale(), 3);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minus_BigDecimal_nullBigDecimal() {
        GBP_M5_78.minus((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // minus(double)
    //-----------------------------------------------------------------------
    public void test_minus_double_zero() {
        BigMoney test = GBP_2_34.minus(0d);
        assertSame(test, GBP_2_34);
    }

    public void test_minus_double_positive() {
        BigMoney test = GBP_2_34.minus(1.23d);
        assertEquals(test.toString(), "GBP 1.11");
        assertEquals(test.getScale(), 2);
    }

    public void test_minus_double_negative() {
        BigMoney test = GBP_2_34.minus(-1.23d);
        assertEquals(test.toString(), "GBP 3.57");
        assertEquals(test.getScale(), 2);
    }

    public void test_minus_double_scale() {
        BigMoney test = GBP_2_34.minus(1.235d);
        assertEquals(test.toString(), "GBP 1.105");
        assertEquals(test.getScale(), 3);
    }

    //-----------------------------------------------------------------------
    // minusMajor(long)
    //-----------------------------------------------------------------------
    public void test_minusMajor_zero() {
        BigMoney test = GBP_2_34.minusMajor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_minusMajor_positive() {
        BigMoney test = GBP_2_34.minusMajor(123);
        assertEquals(test.toString(), "GBP -120.66");
        assertEquals(test.getScale(), 2);
    }

    public void test_minusMajor_negative() {
        BigMoney test = GBP_2_34.minusMajor(-123);
        assertEquals(test.toString(), "GBP 125.34");
        assertEquals(test.getScale(), 2);
    }

    //-----------------------------------------------------------------------
    // minusMinor(long)
    //-----------------------------------------------------------------------
    public void test_minusMinor_zero() {
        BigMoney test = GBP_2_34.minusMinor(0);
        assertSame(test, GBP_2_34);
    }

    public void test_minusMinor_positive() {
        BigMoney test = GBP_2_34.minusMinor(123);
        assertEquals(test.toString(), "GBP 1.11");
        assertEquals(test.getScale(), 2);
    }

    public void test_minusMinor_negative() {
        BigMoney test = GBP_2_34.minusMinor(-123);
        assertEquals(test.toString(), "GBP 3.57");
        assertEquals(test.getScale(), 2);
    }

    public void test_minusMinor_scale() {
        BigMoney test = BigMoney.parse("GBP 12").minusMinor(123);
        assertEquals(test.toString(), "GBP 10.77");
        assertEquals(test.getScale(), 2);
    }

    //-----------------------------------------------------------------------
    // minusRetainScale(BigMoneyProvider,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_minusRetainScale_BigMoneyProviderRoundingMode_zero() {
        BigMoney test = GBP_2_34.minusRetainScale(BigMoney.zero(GBP), RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_minusRetainScale_BigMoneyProviderRoundingMode_positive() {
        BigMoney test = GBP_2_34.minusRetainScale(BigMoney.parse("GBP 1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minusRetainScale_BigMoneyProviderRoundingMode_negative() {
        BigMoney test = GBP_2_34.minusRetainScale(BigMoney.parse("GBP -1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_minusRetainScale_BigMoneyProviderRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.minusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 1.10");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minusRetainScale_BigMoneyProviderRoundingMode_roundUnecessary() {
        GBP_2_34.minusRetainScale(BigMoney.parse("GBP 1.235"), RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minusRetainScale_BigMoneyProviderRoundingMode_nullBigMoneyProvider() {
        GBP_M5_78.minusRetainScale((BigMoneyProvider) null, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minusRetainScale_BigMoneyProviderRoundingMode_nullRoundingMode() {
        GBP_M5_78.minusRetainScale(BigMoney.parse("GBP 123"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // minusRetainScale(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_minusRetainScale_BigDecimalRoundingMode_zero() {
        BigMoney test = GBP_2_34.minusRetainScale(BigDecimal.ZERO, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_minusRetainScale_BigDecimalRoundingMode_positive() {
        BigMoney test = GBP_2_34.minusRetainScale(bd("1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minusRetainScale_BigDecimalRoundingMode_negative() {
        BigMoney test = GBP_2_34.minusRetainScale(bd("-1.23"), RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_minusRetainScale_BigDecimalRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.minusRetainScale(bd("1.235"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 1.10");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minusRetainScale_BigDecimalRoundingMode_roundUnecessary() {
        GBP_2_34.minusRetainScale(bd("1.235"), RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minusRetainScale_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_M5_78.minusRetainScale((BigDecimal) null, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minusRetainScale_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_M5_78.minusRetainScale(BIGDEC_2_34, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // minusRetainScale(double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_minusRetainScale_doubleRoundingMode_zero() {
        BigMoney test = GBP_2_34.minusRetainScale(0d, RoundingMode.UNNECESSARY);
        assertSame(test, GBP_2_34);
    }

    public void test_minusRetainScale_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_34.minusRetainScale(1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 1.11");
    }

    public void test_minusRetainScale_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_34.minusRetainScale(-1.23d, RoundingMode.UNNECESSARY);
        assertEquals(test.toString(), "GBP 3.57");
    }

    public void test_minusRetainScale_doubleRoundingMode_roundDown() {
        BigMoney test = GBP_2_34.minusRetainScale(1.235d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 1.10");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void test_minusRetainScale_doubleRoundingMode_roundUnecessary() {
        GBP_2_34.minusRetainScale(1.235d, RoundingMode.UNNECESSARY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_minusRetainScale_doubleRoundingMode_nullRoundingMode() {
        GBP_M5_78.minusRetainScale(2.34d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(BigDecimal)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_BigDecimal_one() {
        BigMoney test = GBP_2_34.multipliedBy(BigDecimal.ONE);
        assertSame(test, GBP_2_34);
    }

    public void test_multipliedBy_BigDecimal_positive() {
        BigMoney test = GBP_2_33.multipliedBy(bd("2.5"));
        assertEquals(test.toString(), "GBP 5.825");
        assertEquals(test.getScale(), 3);
    }

    public void test_multipliedBy_BigDecimal_negative() {
        BigMoney test = GBP_2_33.multipliedBy(bd("-2.5"));
        assertEquals(test.toString(), "GBP -5.825");
        assertEquals(test.getScale(), 3);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multipliedBy_BigDecimal_nullBigDecimal() {
        GBP_5_78.multipliedBy((BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(double)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_doubleRoundingMode_one() {
        BigMoney test = GBP_2_34.multipliedBy(1d);
        assertSame(test, GBP_2_34);
    }

    public void test_multipliedBy_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_33.multipliedBy(2.5d);
        assertEquals(test.toString(), "GBP 5.825");
        assertEquals(test.getScale(), 3);
    }

    public void test_multipliedBy_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_33.multipliedBy(-2.5d);
        assertEquals(test.toString(), "GBP -5.825");
        assertEquals(test.getScale(), 3);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(long)
    //-----------------------------------------------------------------------
    public void test_multipliedBy_long_one() {
        BigMoney test = GBP_2_34.multipliedBy(1);
        assertSame(test, GBP_2_34);
    }

    public void test_multipliedBy_long_positive() {
        BigMoney test = GBP_2_34.multipliedBy(3);
        assertEquals(test.toString(), "GBP 7.02");
        assertEquals(test.getScale(), 2);
    }

    public void test_multipliedBy_long_negative() {
        BigMoney test = GBP_2_34.multipliedBy(-3);
        assertEquals(test.toString(), "GBP -7.02");
        assertEquals(test.getScale(), 2);
    }

    //-----------------------------------------------------------------------
    // multiplyRetainScale(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_multiplyRetainScale_BigDecimalRoundingMode_one() {
        BigMoney test = GBP_2_34.multiplyRetainScale(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_multiplyRetainScale_BigDecimalRoundingMode_positive() {
        BigMoney test = GBP_2_33.multiplyRetainScale(bd("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 5.82");
    }

    public void test_multiplyRetainScale_BigDecimalRoundingMode_positive_halfUp() {
        BigMoney test = GBP_2_33.multiplyRetainScale(bd("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 5.83");
    }

    public void test_multiplyRetainScale_BigDecimalRoundingMode_negative() {
        BigMoney test = GBP_2_33.multiplyRetainScale(bd("-2.5"), RoundingMode.FLOOR);
        assertEquals(test.toString(), "GBP -5.83");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multiplyRetainScale_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_5_78.multiplyRetainScale((BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multiplyRetainScale_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_5_78.multiplyRetainScale(bd("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // multiplyRetainScale(double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_multiplyRetainScale_doubleRoundingMode_one() {
        BigMoney test = GBP_2_34.multiplyRetainScale(1d, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_multiplyRetainScale_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_33.multiplyRetainScale(2.5d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 5.82");
    }

    public void test_multiplyRetainScale_doubleRoundingMode_positive_halfUp() {
        BigMoney test = GBP_2_33.multiplyRetainScale(2.5d, RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 5.83");
    }

    public void test_multiplyRetainScale_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_33.multiplyRetainScale(-2.5d, RoundingMode.FLOOR);
        assertEquals(test.toString(), "GBP -5.83");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_multiplyRetainScale_doubleRoundingMode_nullRoundingMode() {
        GBP_5_78.multiplyRetainScale(2.5d, (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // dividedBy(BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_dividedBy_BigDecimalRoundingMode_one() {
        BigMoney test = GBP_2_34.dividedBy(BigDecimal.ONE, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_dividedBy_BigDecimalRoundingMode_positive() {
        BigMoney test = GBP_2_34.dividedBy(bd("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.93");
    }

    public void test_dividedBy_BigDecimalRoundingMode_positive_halfUp() {
        BigMoney test = GBP_2_34.dividedBy(bd("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 0.94");
    }

    public void test_dividedBy_BigDecimalRoundingMode_negative() {
        BigMoney test = GBP_2_34.dividedBy(bd("-2.5"), RoundingMode.FLOOR);
        assertEquals(test.toString(), "GBP -0.94");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_dividedBy_BigDecimalRoundingMode_nullBigDecimal() {
        GBP_5_78.dividedBy((BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_dividedBy_BigDecimalRoundingMode_nullRoundingMode() {
        GBP_5_78.dividedBy(bd("2.5"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // dividedBy(double,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_dividedBy_doubleRoundingMode_one() {
        BigMoney test = GBP_2_34.dividedBy(1d, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_dividedBy_doubleRoundingMode_positive() {
        BigMoney test = GBP_2_34.dividedBy(2.5d, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.93");
    }

    public void test_dividedBy_doubleRoundingMode_positive_halfUp() {
        BigMoney test = GBP_2_34.dividedBy(2.5d, RoundingMode.HALF_UP);
        assertEquals(test.toString(), "GBP 0.94");
    }

    public void test_dividedBy_doubleRoundingMode_negative() {
        BigMoney test = GBP_2_34.dividedBy(-2.5d, RoundingMode.FLOOR);
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
        BigMoney test = GBP_2_34.dividedBy(1, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_dividedBy_long_positive() {
        BigMoney test = GBP_2_34.dividedBy(3, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.78");
    }

    public void test_dividedBy_long_positive_roundDown() {
        BigMoney test = GBP_2_35.dividedBy(3, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 0.78");
    }

    public void test_dividedBy_long_positive_roundUp() {
        BigMoney test = GBP_2_35.dividedBy(3, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 0.79");
    }

    public void test_dividedBy_long_negative() {
        BigMoney test = GBP_2_34.dividedBy(-3, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP -0.78");
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    public void test_negated_zero() {
        BigMoney test = GBP_0_00.negated();
        assertSame(test, GBP_0_00);
    }

    public void test_negated_positive() {
        BigMoney test = GBP_2_34.negated();
        assertEquals(test.toString(), "GBP -2.34");
    }

    public void test_negated_negative() {
        BigMoney test = BigMoney.parse("GBP -2.34").negated();
        assertEquals(test.toString(), "GBP 2.34");
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    public void test_abs_positive() {
        BigMoney test = GBP_2_34.abs();
        assertSame(test, GBP_2_34);
    }

    public void test_abs_negative() {
        BigMoney test = BigMoney.parse("GBP -2.34").abs();
        assertEquals(test.toString(), "GBP 2.34");
    }

    //-----------------------------------------------------------------------
    // rounded(int,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_round_2down() {
        BigMoney test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_round_2up() {
        BigMoney test = GBP_2_34.rounded(2, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    public void test_round_1down() {
        BigMoney test = GBP_2_34.rounded(1, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.30");
    }

    public void test_round_1up() {
        BigMoney test = GBP_2_34.rounded(1, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 2.40");
    }

    public void test_round_0down() {
        BigMoney test = GBP_2_34.rounded(0, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 2.00");
    }

    public void test_round_0up() {
        BigMoney test = GBP_2_34.rounded(0, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 3.00");
    }

    public void test_round_M1down() {
        BigMoney test = BigMoney.parse("GBP 432.34").rounded(-1, RoundingMode.DOWN);
        assertEquals(test.toString(), "GBP 430.00");
    }

    public void test_round_M1up() {
        BigMoney test = BigMoney.parse("GBP 432.34").rounded(-1, RoundingMode.UP);
        assertEquals(test.toString(), "GBP 440.00");
    }

    public void test_round_3() {
        BigMoney test = GBP_2_34.rounded(3, RoundingMode.DOWN);
        assertSame(test, GBP_2_34);
    }

    //-----------------------------------------------------------------------
    // convertedTo(CurrencyUnit,BigDecimal)
    //-----------------------------------------------------------------------
    public void test_convertedTo_CurrencyUnit_BigDecimal_positive() {
        BigMoney test = GBP_2_33.convertedTo(EUR, bd("2.5"));
        assertEquals(test.toString(), "EUR 5.825");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertedTo_CurrencyUnit_BigDecimal_negative() {
        GBP_2_33.convertedTo(EUR, bd("-2.5"));
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertedTo_CurrencyUnit_BigDecimal_sameCurrency() {
        GBP_2_33.convertedTo(GBP, bd("2.5"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_CurrencyUnit_BigDecimal_nullCurrency() {
        GBP_5_78.convertedTo((CurrencyUnit) null, bd("2"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertedTo_CurrencyUnit_BigDecimal_nullBigDecimal() {
        GBP_5_78.convertedTo(EUR, (BigDecimal) null);
    }

    //-----------------------------------------------------------------------
    // convertRetainScale(CurrencyUnit,BigDecimal,RoundingMode)
    //-----------------------------------------------------------------------
    public void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_positive() {
        BigMoney test = BigMoney.parse("GBP 2.2").convertRetainScale(EUR, bd("2.5"), RoundingMode.DOWN);
        assertEquals(test.toString(), "EUR 5.5");
    }

    public void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_roundHalfUp() {
        BigMoney test = BigMoney.parse("GBP 2.21").convertRetainScale(EUR, bd("2.5"), RoundingMode.HALF_UP);
        assertEquals(test.toString(), "EUR 5.53");
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_negative() {
        GBP_2_33.convertRetainScale(EUR, bd("-2.5"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_sameCurrency() {
        GBP_2_33.convertRetainScale(GBP, bd("2.5"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_nullCurrency() {
        GBP_5_78.convertRetainScale((CurrencyUnit) null, bd("2"), RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_nullBigDecimal() {
        GBP_5_78.convertRetainScale(EUR, (BigDecimal) null, RoundingMode.DOWN);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertRetainScale_CurrencyUnit_BigDecimal_RoundingMode_nullRoundingMode() {
        GBP_5_78.convertRetainScale(EUR, bd("2"), (RoundingMode) null);
    }

    //-----------------------------------------------------------------------
    // toMoney()
    //-----------------------------------------------------------------------
    public void test_toBigMoney() {
        assertSame(GBP_2_34.toBigMoney(), GBP_2_34);
    }

    //-----------------------------------------------------------------------
    // toFixedMoney()
    //-----------------------------------------------------------------------
    public void test_toFixedMoney() {
        assertEquals(GBP_2_34.toFixedMoney(), FixedMoney.of(GBP, BIGDEC_2_34, 2));
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
    public void test_isSameCurrency_BigMoney_same() {
        assertEquals(GBP_2_34.isSameCurrency(GBP_2_35), true);
    }

    public void test_isSameCurrency_BigMoney_different() {
        assertEquals(GBP_2_34.isSameCurrency(USD_2_34), false);
    }

    public void test_isSameCurrency_Money_same() {
        assertEquals(GBP_2_34.isSameCurrency(Money.parse("GBP 2")), true);
    }

    public void test_isSameCurrency_Money_different() {
        assertEquals(GBP_2_34.isSameCurrency(Money.parse("USD 2")), false);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_isSameCurrency_Money_nullMoney() {
        GBP_2_34.isSameCurrency((BigMoney) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_BigMoney() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
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
        BigMoney t = GBP_2_35;
        Money a = Money.ofMinor(GBP, 234);
        Money b = Money.ofMinor(GBP, 235);
        Money c = Money.ofMinor(GBP, 236);
        assertEquals(t.compareTo(a), 1);
        assertEquals(t.compareTo(b), 0);
        assertEquals(t.compareTo(c), -1);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_compareTo_currenciesDiffer() {
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
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
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
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
        BigMoney a = GBP_2_34;
        Money b = Money.ofMinor(GBP, 234);
        assertEquals(a.isEqual(b), true);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_isEqual_currenciesDiffer() {
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
        a.isEqual(b);
    }

    //-----------------------------------------------------------------------
    // isGreaterThan()
    //-----------------------------------------------------------------------
    public void test_isGreaterThan() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
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
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
        a.isGreaterThan(b);
    }

    //-----------------------------------------------------------------------
    // isLessThan()
    //-----------------------------------------------------------------------
    public void test_isLessThan() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_35;
        BigMoney c = GBP_2_36;
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
        BigMoney a = GBP_2_34;
        BigMoney b = USD_2_35;
        a.isLessThan(b);
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    public void test_equals_hashCode_positive() {
        BigMoney a = GBP_2_34;
        BigMoney b = GBP_2_34;
        BigMoney c = GBP_2_35;
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
        BigMoney a = GBP_2_34;
        assertEquals(a.equals(null), false);
        assertEquals(a.equals("String"), false);
        assertEquals(a.equals(new Object()), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString_positive() {
        BigMoney test = BigMoney.of(GBP, BIGDEC_2_34);
        assertEquals(test.toString(), "GBP 2.34");
    }

    public void test_toString_negative() {
        BigMoney test = BigMoney.of(EUR, BIGDEC_M5_78);
        assertEquals(test.toString(), "EUR -5.78");
    }

}
