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

import org.testng.annotations.Test;

/**
 * Test MoneyException.
 */
@Test
public class TestMoneyException {

    //-----------------------------------------------------------------------
    // new (String)
    //-----------------------------------------------------------------------
    public void test_String() {
        MoneyException test = new MoneyException("PROBLEM");
        assertEquals(test.getMessage(), "PROBLEM");
        assertEquals(test.getCause(), null);
    }

    public void test_String_nullString() {
        MoneyException test = new MoneyException(null);
        assertEquals(test.getMessage(), null);
        assertEquals(test.getCause(), null);
    }

    //-----------------------------------------------------------------------
    // new (String,Throwable)
    //-----------------------------------------------------------------------
    public void test_StringThrowable() {
        NullPointerException npe = new NullPointerException();
        MoneyException test = new MoneyException("PROBLEM", npe);
        assertEquals(test.getMessage(), "PROBLEM");
        assertSame(test.getCause(), npe);
    }

    public void test_StringThrowable_nullString() {
        NullPointerException npe = new NullPointerException();
        MoneyException test = new MoneyException(null, npe);
        assertEquals(test.getMessage(), null);
        assertSame(test.getCause(), npe);
    }

    public void test_StringThrowable_nullThrowable() {
        MoneyException test = new MoneyException("PROBLEM", null);
        assertEquals(test.getMessage(), "PROBLEM");
        assertEquals(test.getCause(), null);
    }

}
