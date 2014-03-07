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
 * Exception thrown when the requested currency is illegal.
 * <p>
 * For example, this exception would be thrown when trying to obtain a
 * currency using an unrecognised currency code or locale.
 * <p>
 * This exception makes no guarantees about immutability or thread-safety.
 */
public class IllegalCurrencyException extends IllegalArgumentException {

    /** Serialization lock. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param message  the message, may be null
     */
    public IllegalCurrencyException(String message) {
        super(message);
    }

}
