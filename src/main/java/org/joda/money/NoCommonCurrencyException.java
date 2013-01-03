/*
 *  Copyright 2009-2013 Stephen Colebourne
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
 * Indicates that operation requiring a common currency cannot be performed.
 * <p>
 * This exception makes no guarantees about immutability or thread-safety.
 */
public class NoCommonCurrencyException extends IllegalArgumentException {

    /** Serialization lock. */
    private static final long serialVersionUID = 1L;

    /** First exchange rate. */
    private final ExchangeRate first;
    /** Second exchange rate. */
    private final ExchangeRate second;

    /**
     * Creates an instance.
     * 
     * @param first  the first rate at fault, may be null
     * @param second  the second rate at fault, may be null
     */
    public NoCommonCurrencyException(ExchangeRate first, ExchangeRate second) {
        super(String.format("Exchange rates have no common currency: %s vs %s", first, second));
        this.first = first;
        this.second = second;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the first rate at fault.
     * 
     * @return the rate at fault, may be null
     */
    public ExchangeRate getFirst() {
        return first;
    }

    /**
     * Gets the second rate at fault.
     * 
     * @return the rate at fault, may be null
     */
    public ExchangeRate getSecond() {
        return second;
    }

}
