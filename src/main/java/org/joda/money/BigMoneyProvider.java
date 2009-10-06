/*
 *  Copyright 2009 Stephen Colebourne
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
 * Provides a uniform interface to obtain a <code>BigMoney</code>.
 * <p>
 * When designing APIs, it is recommended to use <code>BigMoneyProvider</code>
 * in method signatures where possible to allow the widest possible use of the method.
 * Within Joda-Money, both <code>BigMoney</code> and <code>Money</code> implement
 * the provider interface.
 * <p>
 * BigMoneyProvider is an interface and makes no guarantees about the
 * immutability or thread-safety of its implementations.
 */
public interface BigMoneyProvider {

    /**
     * Returns a <code>BigMoney</code> instance equivalent to the value of this object.
     * 
     * @return the money instance, never null
     * @throws MoneyException if conversion is not possible
     */
    BigMoney toBigMoney();

}
