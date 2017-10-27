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
package org.joda.money.format;

import java.io.IOException;

import org.junit.Test;

/**
 * Test MoneyFormatterException.
 */
public class TestMoneyFormatterException {

    @Test(expected = IOException.class)
    public void test_MoneyFormatException_IOException_notRethrown() throws IOException {
        MoneyFormatException test = new MoneyFormatException("Error", new IOException("Inner"));
        test.rethrowIOException();  // should throw
    }

    @Test
    public void test_MoneyFormatException_nonIOException_notRethrown() throws IOException {
        MoneyFormatException test = new MoneyFormatException("Error", new IllegalStateException("Inner"));
        test.rethrowIOException();  // should succeed
    }

}
