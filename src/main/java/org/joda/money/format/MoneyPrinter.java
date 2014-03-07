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

import org.joda.money.BigMoney;

/**
 * Prints part of a monetary value to the output appendable.
 * <p>
 * The printer may print any part, or the whole, of the input {@link BigMoney}.
 * Typically, a complete print is constructed from a number of smaller printers
 * that have been combined using {@link MoneyFormatterBuilder}.
 * <p>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All instantiable implementations must be thread-safe, and should generally
 * be final and immutable.
 */
public interface MoneyPrinter {

    /**
     * Prints part of a monetary value to the output appendable.
     * <p>
     * The implementation determines what to append, which may be some or all
     * of the data held in the {@code BigMoney}.
     * <p>
     * The context is not a thread-safe object and a new instance will be created
     * for each print. The context must not be stored in an instance variable
     * or shared with any other threads.
     * 
     * @param context  the context being used, not null
     * @param appendable  the appendable to add to, not null
     * @param money  the money to print, not null
     * @throws MoneyFormatException if there is a problem while printing
     * @throws IOException if an IO exception occurs
     */
    void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException;

}
