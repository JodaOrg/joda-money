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

/**
 * Joda-Money provides a library of classes to store amounts of money.
 * <p>
 * Joda-Money does not provide monetary algorithms beyond the most basic and obvious.
 * This is because the requirements for these algorithms vary widely between domains.
 * This library is intended to act as the base layer, providing classes that should be in the JDK.
 * <p>
 * As a flavour of Joda-Money, here's some example code:
 * <pre>
 * // create a monetary value
 * Money money = Money.parse("USD 23.87");
 *
 * // add another amount with safe double conversion
 * CurrencyUnit usd = CurrencyUnit.of("USD");
 * money = money.plus(Money.of(usd, 12.43d));
 * </pre>
 */
module org.joda.money {

    // only annotations are used, thus they are optional
    requires static org.joda.convert;

    // all packages are exported
    exports org.joda.money;
    exports org.joda.money.format;

}
