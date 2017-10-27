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

import org.joda.convert.StringConvert;
import org.junit.Test;

/**
 * Test string conversion.
 */
public class TestStringConvert {

    @Test
    public void test_BigMoney() {
        BigMoney test = BigMoney.of(CurrencyUnit.CHF, 1234.5678d);
        String str = StringConvert.INSTANCE.convertToString(test);
        assertEquals(str, "CHF 1234.5678");
        assertEquals(test, StringConvert.INSTANCE.convertFromString(BigMoney.class, str));
    }

    @Test
    public void test_Money() {
        Money test = Money.of(CurrencyUnit.CHF, 1234.56d);
        String str = StringConvert.INSTANCE.convertToString(test);
        assertEquals(str, "CHF 1234.56");
        assertEquals(test, StringConvert.INSTANCE.convertFromString(Money.class, str));
    }

    @Test
    public void test_CurrencyUnit() {
        CurrencyUnit test = CurrencyUnit.CHF;
        String str = StringConvert.INSTANCE.convertToString(test);
        assertEquals(str, "CHF");
        assertEquals(test, StringConvert.INSTANCE.convertFromString(CurrencyUnit.class, str));
    }

}
