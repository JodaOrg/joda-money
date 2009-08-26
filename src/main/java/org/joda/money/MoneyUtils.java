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
 * Utilities for working with monetary values.
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
     * Checks if the money is zero, treating null as zero
     * 
     * @return true if the money is null or zero
     */
    public static boolean isZero(Money money) {
        return (money == null || money.isZero());
    }

    //-----------------------------------------------------------------------
    /**
     * Finds the maximum monetary value, handing null.
     * <p>
     * This returns the greater of money1 or money2 treating null as infinitely small.
     * At least one of the two parameters must be non-null.
     * 
     * @param money1  the first money instance, null ignored
     * @param money2  the first money instance, null ignored
     * @return the maximum value, never null
     * @throws NullPointerException if both values are null
     * @throws MoneyException if the currencies differ
     */
    public static Money max(Money money1, Money money2) {
        if (money1 == null || money2 == null) {
            return handleNull(money1, money2);
        }
        return money1.compareTo(money2) > 0 ? money1 : money2;
    }

    /**
     * Finds the minimum monetary value, handing null.
     * <p>
     * This returns the greater of money1 or money2 treating null as infinitely small.
     * At least one of the two parameters must be non-null.
     * 
     * @param money1  the first money instance, null ignored
     * @param money2  the first money instance, null ignored
     * @return the minimum value, never null
     * @throws NullPointerException if both values are null
     * @throws MoneyException if the currencies differ
     */
    public static Money min(Money money1, Money money2) {
        if (money1 == null || money2 == null) {
            return handleNull(money1, money2);
        }
        return money1.compareTo(money2) < 0 ? money1 : money2;
    }

    //-----------------------------------------------------------------------
    /**
     * Adds two money objects, handling null.
     * <p>
     * This returns <code>money1 + money2</code> where null is treated as zero.
     * At least one of the two parameters must be non-null.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the total, where null is ignored, never null
     * @throws NullPointerException if both values are null
     * @throws MoneyException if the currencies differ
     * @throws ArithmeticException if the amount is too large
     */
    public static Money add(Money money1, Money money2) {
        if (money1 == null || money2 == null) {
            return handleNull(money1, money2);
        }
        return money1.plus(money2);
    }

    //-----------------------------------------------------------------------
    /**
     * Subtracts the second monetary value from the first, handling null.
     * <p>
     * This returns <code>money1 - money2</code>.
     * The first parameter must be non-null.
     * If the second parameter is null, then it is treated as zero.
     * 
     * @param money1  the first money instance, not null
     * @param money2  the first money instance, null returns money1
     * @return the total, where null is ignored, null if both inputs are null
     * @throws NullPointerException if the first value is null
     * @throws MoneyException if the currencies differ
     * @throws ArithmeticException if the amount is too large
     */
    public static Money subtract(Money money1, Money money2) {
        checkNotNull(money1, "First Money argument must not be null");
        if (money2 == null) {
            return money1;
        }
        return money1.minus(money2);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the other value when null is encountered.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the merged result, never null
     * @throws NullPointerException if both values are null
     */
    private static Money handleNull(Money money1, Money money2) {
        if (money1 == null) {
            if (money2 == null) {
                throw new NullPointerException("Both monetary values cannot be null");
            }
            return money2;
        } else {
            return money1;
        }
    }

}
