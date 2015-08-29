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

/**
 * Provides a uniform interface to obtain a {@code BigMoney}.
 * <p>
 * This interface provides an abstraction over {@link Money} and {@link BigMoney}.
 * In general, applications should use the concrete types, not this interface.
 * However, utilities and frameworks may choose to make use of this abstraction.
 * <p>
 * Implementations of {@code BigMoneyProvider} may be mutable.
 * To minimise the risk of the value of the provider changing while processing,
 * any method that takes a {@code BigMoneyProvider} as a parameter should convert
 * it to a {@code BigMoney} immediately and use that directly from then on.
 * The method {@link BigMoney#of(BigMoneyProvider)} performs the conversion
 * safely with null checks and is recommended for this purpose.
 * <p>
 * This interface makes no guarantees about the immutability or
 * thread-safety of implementations.
 */
public interface BigMoneyProvider {

    /**
     * Returns a {@code BigMoney} instance equivalent to the value of this object.
     * <p>
     * It is recommended that {@link BigMoney#of(BigMoneyProvider)} is used in
     * preference to calling this method directly. It is also recommended that the
     * converted {@code BigMoney} is cached in a local variable instead of
     * performing the conversion multiple times.
     * 
     * @return the converted money instance, never null
     * @throws RuntimeException if conversion is not possible
     */
    BigMoney toBigMoney();

}
