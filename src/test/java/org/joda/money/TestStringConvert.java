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


import org.joda.convert.StringConvert;
import org.junit.jupiter.api.Test;

/**
 * Test string conversion.
 */
class TestStringConvert {

    @Test
    void test_BigMoney() {
        BigMoney test = BigMoney.of(CurrencyUnit.CHF, 1234.5678d);
        String str = StringConvert.INSTANCE.convertToString(test);
        assertEquals("CHF 1234.5678", str);
        assertEquals(test, StringConvert.INSTANCE.convertFromString(BigMoney.class, str));
    }

    @Test
    void test_Money() {
        Money test = Money.of(CurrencyUnit.CHF, 1234.56d);
        String str = StringConvert.INSTANCE.convertToString(test);
        assertEquals("CHF 1234.56", str);
        assertEquals(test, StringConvert.INSTANCE.convertFromString(Money.class, str));
    }

    @Test
    void test_CurrencyUnit() {
        CurrencyUnit test = CurrencyUnit.CHF;
        String str = StringConvert.INSTANCE.convertToString(test);
        assertEquals("CHF", str);
        assertEquals(test, StringConvert.INSTANCE.convertFromString(CurrencyUnit.class, str));
    }

}
