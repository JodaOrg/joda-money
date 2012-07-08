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
package org.joda.money;

/**
 * Indicates that operatation requiring a common currency cannot be performed.
 * 
 * @author tpasierb
 */
public class NoCommonCurrencyException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final ExchangeRate first;
    private final ExchangeRate second;

    public NoCommonCurrencyException(ExchangeRate first, ExchangeRate second) {
        super(String.format("Exchange rates have no common currency: %s vs %s", first, second));
        this.first = first;
        this.second = second;
    }

    public ExchangeRate getFirst() {
        return first;
    }

    public ExchangeRate getSecond() {
        return second;
    }

}
