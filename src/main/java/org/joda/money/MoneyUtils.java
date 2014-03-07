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
 * Utilities for working with monetary values that handle null.
 * <p>
 * This utility class contains thread-safe static methods.
 */
public final class MoneyUtils {

    /**
     * Validates that the object specified is not null.
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
     * Checks if the monetary value is zero, treating null as zero.
     * <p>
     * This method accepts any implementation of {@code BigMoneyProvider}.
     * 
     * @param moneyProvider  the money to check, null returns zero
     * @return true if the money is null or zero
     */
    public static boolean isZero(BigMoneyProvider moneyProvider) {
        return (moneyProvider == null || moneyProvider.toBigMoney().isZero());
    }

    /**
     * Checks if the monetary value is positive and non-zero, treating null as zero.
     * <p>
     * This method accepts any implementation of {@code BigMoneyProvider}.
     * 
     * @param moneyProvider  the money to check, null returns false
     * @return true if the money is non-null and positive
     */
    public static boolean isPositive(BigMoneyProvider moneyProvider) {
        return (moneyProvider != null && moneyProvider.toBigMoney().isPositive());
    }

    /**
     * Checks if the monetary value is positive or zero, treating null as zero.
     * <p>
     * This method accepts any implementation of {@code BigMoneyProvider}.
     * 
     * @param moneyProvider  the money to check, null returns true
     * @return true if the money is null, zero or positive
     */
    public static boolean isPositiveOrZero(BigMoneyProvider moneyProvider) {
        return (moneyProvider == null || moneyProvider.toBigMoney().isPositiveOrZero());
    }

    /**
     * Checks if the monetary value is negative and non-zero, treating null as zero.
     * <p>
     * This method accepts any implementation of {@code BigMoneyProvider}.
     * 
     * @param moneyProvider  the money to check, null returns false
     * @return true if the money is non-null and negative
     */
    public static boolean isNegative(BigMoneyProvider moneyProvider) {
        return (moneyProvider != null && moneyProvider.toBigMoney().isNegative());
    }

    /**
     * Checks if the monetary value is negative or zero, treating null as zero.
     * <p>
     * This method accepts any implementation of {@code BigMoneyProvider}.
     * 
     * @param moneyProvider  the money to check, null returns true
     * @return true if the money is null, zero or negative
     */
    public static boolean isNegativeOrZero(BigMoneyProvider moneyProvider) {
        return (moneyProvider == null || moneyProvider.toBigMoney().isNegativeOrZero());
    }

    //-----------------------------------------------------------------------
    /**
     * Finds the maximum {@code Money} value, handing null.
     * <p>
     * This returns the greater of money1 or money2 where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the maximum value, null if both inputs are null
     * @throws CurrencyMismatchException if the currencies differ
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
     * Finds the minimum {@code Money} value, handing null.
     * <p>
     * This returns the greater of money1 or money2 where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the minimum value, null if both inputs are null
     * @throws CurrencyMismatchException if the currencies differ
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
     * Adds two {@code Money} objects, handling null.
     * <p>
     * This returns {@code money1 + money2} where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the total, where null is ignored, null if both inputs are null
     * @throws CurrencyMismatchException if the currencies differ
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
     * Subtracts the second {@code Money} from the first, handling null.
     * <p>
     * This returns {@code money1 - money2} where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null treated as zero
     * @param money2  the first money instance, null returns money1
     * @return the total, where null is ignored, null if both inputs are null
     * @throws CurrencyMismatchException if the currencies differ
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

    //-----------------------------------------------------------------------
    /**
     * Finds the maximum {@code BigMoney} value, handing null.
     * <p>
     * This returns the greater of money1 or money2 where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the maximum value, null if both inputs are null
     * @throws CurrencyMismatchException if the currencies differ
     */
    public static BigMoney max(BigMoney money1, BigMoney money2) {
        if (money1 == null) {
            return money2;
        }
        if (money2 == null) {
            return money1;
        }
        return money1.compareTo(money2) > 0 ? money1 : money2;
    }

    /**
     * Finds the minimum {@code BigMoney} value, handing null.
     * <p>
     * This returns the greater of money1 or money2 where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the minimum value, null if both inputs are null
     * @throws CurrencyMismatchException if the currencies differ
     */
    public static BigMoney min(BigMoney money1, BigMoney money2) {
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
     * Adds two {@code BigMoney} objects, handling null.
     * <p>
     * This returns {@code money1 + money2} where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null returns money2
     * @param money2  the first money instance, null returns money1
     * @return the total, where null is ignored, null if both inputs are null
     * @throws CurrencyMismatchException if the currencies differ
     */
    public static BigMoney add(BigMoney money1, BigMoney money2) {
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
     * Subtracts the second {@code BigMoney} from the first, handling null.
     * <p>
     * This returns {@code money1 - money2} where null is ignored.
     * If both input values are null, then null is returned.
     * 
     * @param money1  the first money instance, null treated as zero
     * @param money2  the first money instance, null returns money1
     * @return the total, where null is ignored, null if both inputs are null
     * @throws CurrencyMismatchException if the currencies differ
     */
    public static BigMoney subtract(BigMoney money1, BigMoney money2) {
        if (money2 == null) {
            return money1;
        }
        if (money1 == null) {
            return money2.negated();
        }
        return money1.minus(money2);
    }

}
