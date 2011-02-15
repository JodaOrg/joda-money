/*
 *  Copyright 2009-2011 Stephen Colebourne
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

/**
 * Parses part of a textual input string of monetary information.
 * <p>
 * The parser is expected to start parsing at the specified text position
 * and match against whatever it represents.
 * The parsed result must be stored in the context.
 * <p>
 * The parse position will be updated during the parse. Parsing will start at
 * the specified index and the return value specifies the new parse position
 * for the next parser. If an error occurs, the returned index will be negative
 * and will have the error position encoded using the complement operator.
 * <p>
 * MoneyParser is an interface and must be implemented with care to ensure
 * other classes operate correctly.
 * All instantiable implementations must be thread-safe, and should generally
 * be final and immutable.
 * <p>
 * The context is not a thread-safe object and a new instance will be created
 * for each parse that occurs. The context must not be stored in an instance
 * variable or shared with any other threads.
 */
public interface MoneyParser {

    /**
     * Parses monetary information using a textual representation.
     * <p>
     * The text and parse index are stored in the context.
     * The parsed data and updated index is also stored in the context.
     * <p>
     * Implementations should avoid throwing exceptions and use the error index
     * in the context instead to record the problem.
     * The context can be assumed to not be in error on entry to this method.
     * 
     * @param context  the context to use and parse into, not null
     */
    void parse(MoneyParseContext context);

}
