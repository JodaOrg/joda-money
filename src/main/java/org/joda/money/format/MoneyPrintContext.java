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
package org.joda.money.format;

import java.util.Locale;

/**
 * Prints instances of <code>Money</code>.
 * <p>
 * MoneyPrinter is an interface which must be implemented in a thread-safe manner.
 */
public class MoneyPrintContext {

    /**
     * The locale to print using.
     */
    private final Locale iLocale;

    /**
     * Constructor.
     * 
     * @param locale  the locale, not null
     */
    MoneyPrintContext(Locale locale) {
        this.iLocale = locale;
    }

    /**
     * Gets the locale.
     * 
     * @return the locale, never null
     */
    public Locale getLocale() {
        return iLocale;
    }

}
