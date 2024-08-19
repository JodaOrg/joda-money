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

import org.junit.jupiter.api.Test;

/**
 * Test IllegalCurrencyException.
 */
class TestIllegalCurrencyException {

    //-----------------------------------------------------------------------
    // new (String)
    //-----------------------------------------------------------------------
    @Test
    void test_String() {
        var test = new IllegalCurrencyException("PROBLEM");
        assertThat(test.getMessage()).isEqualTo("PROBLEM");
        assertThat(test.getCause()).isNull();
    }

    @Test
    void test_String_nullString() {
        var test = new IllegalCurrencyException(null);
        assertThat(test.getMessage()).isNull();
        assertThat(test.getCause()).isNull();
    }

}
