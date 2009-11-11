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
 * Utilities for working with monetary values that handle null.
 *
 * @author Stephen Colebourne
 */
public final class MoneyUtils {

    /**
     * Validates that the object specified is not null
     *
     * @param object  the object to check, not null
     * @throws NullPointerException if the input value is null
     */
    static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Private constructor.
     */
    private MoneyUtils() {
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the <code>Money</code> is zero, treating null as zero
     * 
     * @return true if the money is null or zero
     */
    public static boolean isZero(Money money) {
        return (money == null || money.isZero());
    }

    //-----------------------------------------------------------------------
    /**
     * Checks a <code>Money</code> returning a non-null value.
     * <p>
     * This returns <code>money</code> if it is non-null.
     * Otherwise it returns a zero value based on the currency.
     * 
     * @param money  the money to check, null returns zero
     * @param currency  the currency of the monetary values, used to return zero
     * @return the defaulted money, never null
     * @throws NullPointerException if the currency is null
     */
    public static Money defaultToZero(Money money, CurrencyUnit currency) {
        MoneyUtils.checkNotNull(currency, "Currency must not be null");
        return (money != null ? money : Money.zero(currency));
    }

    //-----------------------------------------------------------------------
    /**
     * Finds the maximum <code>Money</code> value, handing null.
     * <p>
     * This returns the greater of money1 or money2 where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the maximum value, null if both inputs are null
     * @throws MoneyException if the currencies differ
     */
    public static Money max(Money money1, Money money2) {
        if (money1 == null) {
            return money2;
        }
        if (money2 == null) {
            return money1;
        }
        return money1.compareTo(money2) > 0 ? money1 : money2;
    }

    /**
     * Finds the minimum <code>Money</code> value, handing null.
     * <p>
     * This returns the greater of money1 or money2 where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the minimum value, null if both inputs are null
     * @throws MoneyException if the currencies differ
     */
    public static Money min(Money money1, Money money2) {
        if (money1 == null) {
            return money2;
        }
        if (money2 == null) {
            return money1;
        }
        return money1.compareTo(money2) < 0 ? money1 : money2;
    }

    //-----------------------------------------------------------------------
    /**
     * Adds two <code>Money</code> objects, handling null.
     * <p>
     * This returns <code>money1 + money2</code> where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the total, where null is ignored, null if both inputs are null
     * @throws MoneyException if the currencies differ
     */
    public static Money add(Money money1, Money money2) {
        if (money1 == null) {
            return money2;
        }
        if (money2 == null) {
            return money1;
        }
        return money1.plus(money2);
    }

    //-----------------------------------------------------------------------
    /**
     * Subtracts the second <code>Money</code> from the first, handling null.
     * <p>
     * This returns <code>money1 - money2</code> where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null treated as zero
     * @param money2  the first money instance, null returns money1
     * @return the total, where null is ignored, null if both inputs are null
     * @throws MoneyException if the currencies differ
     */
    public static Money subtract(Money money1, Money money2) {
        if (money2 == null) {
            return money1;
        }
        if (money1 == null) {
            return money2.negated();
        }
        return money1.minus(money2);
    }

}
