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

/**
 * Provides a uniform interface to obtain a {@code Money}.
 * <p>
 * When designing APIs, it is recommended to use {@code MoneyProvider}
 * in method signatures where possible to allow the widest possible use of the method.
 * Within Joda-Money, both {@code Money} and {@code StandardMoney} implement
 * the provider interface.
 * <p>
 * Implementations of {@code MoneyProvider} may be mutable.
 * To minimise the risk of the value of the provider changing while processing,
 * any method that takes a {@code MoneyProvider} as a parameter should convert
 * it to a {@code Money} immediately and use that directly from then on.
 * The method {@link Money#from(MoneyProvider)} performs the conversion
 * safely with null checks and is recommended for this purpose.
 * <p>
 * MoneyProvider is an interface and makes no guarantees about the
 * immutability or thread-safety of its implementations.
 */
public interface MoneyProvider {

    /**
     * Returns a {@code Money} instance equivalent to the value of this object.
     * <p>
     * It is recommended that {@link Money#from(MoneyProvider)} is used in
     * preference to calling this method directly. It is also recommended that the
     * converted {@code Money} is cached in a local variable instead of
     * performing the conversion multiple times.
     * 
     * @return the converted money instance, never null
     * @throws MoneyException if conversion is not possible
     */
    Money toMoney();

}
