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

import org.joda.convert.StringConvert;
import org.junit.jupiter.api.Test;

/**
 * Test string conversion.
 */
class TestStringConvert {

    @Test
    void test_BigMoney() {
        var test = BigMoney.of(CurrencyUnit.CHF, 1234.5678d);
        var str = StringConvert.INSTANCE.convertToString(test);
        assertThat(str).isEqualTo("CHF 1234.5678");
        assertThat(StringConvert.INSTANCE.convertFromString(BigMoney.class, str)).isEqualTo(test);
    }

    @Test
    void test_Money() {
        var test = Money.of(CurrencyUnit.CHF, 1234.56d);
        var str = StringConvert.INSTANCE.convertToString(test);
        assertThat(str).isEqualTo("CHF 1234.56");
        assertThat(StringConvert.INSTANCE.convertFromString(Money.class, str)).isEqualTo(test);
    }

    @Test
    void test_CurrencyUnit() {
        var test = CurrencyUnit.CHF;
        var str = StringConvert.INSTANCE.convertToString(test);
        assertThat(str).isEqualTo("CHF");
        assertThat(StringConvert.INSTANCE.convertFromString(CurrencyUnit.class, str)).isEqualTo(test);
    }

}
