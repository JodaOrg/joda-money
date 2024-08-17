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
import static org.junit.jupiter.api.Assertions.assertNull;

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
        IllegalCurrencyException test = new IllegalCurrencyException("PROBLEM");
        assertEquals("PROBLEM", test.getMessage());
        assertNull(test.getCause());
    }

    @Test
    void test_String_nullString() {
        IllegalCurrencyException test = new IllegalCurrencyException(null);
        assertNull(test.getMessage());
        assertNull(test.getCause());
    }

}
