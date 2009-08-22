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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formats instances of Money to and from a String.
 * <p>
 * MoneyFormatter is immutable and thread-safe.
 */
public final class MoneyFormatter {

    /**
     * The locale to use.
     */
    private final Locale iLocale;
    /**
     * The character used for grouping.
     */
    private final char iGroupingCharacter;
    /**
     * The character used for the decimal point.
     */
    private final char iDecimalPointCharacter;
    /**
     * Whether to group or not.
     */
    private final boolean iGrouping;
    /**
     * Whether to always require the decimal point to be visible.
     */
    private final boolean iForceDecimalPoint;

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of MoneyFormatter for the specified locale.
     *
     * @param locale  the locale to use, not null
     * @return the new instance, never null
     */
    public static MoneyFormatter of(Locale locale) {
        MoneyUtils.checkNotNull(locale, "Locale must not be null");
        return new MoneyFormatter(locale, ',', '.', true, false);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param locale  the locale to use, not null
     * @param groupingCharacter  the grouping character
     * @param decimalPointCharacter  the decimal point character
     * @param group  whether to use the group character
     * @param forceDecimalPoint  whether to always use the decimal point character
     */
    private MoneyFormatter(
                Locale locale,
                char groupingCharacter, char decimalPointCharacter,
                boolean group, boolean forceDecimalPoint) {
        iLocale = locale;
        iGroupingCharacter = groupingCharacter;
        iDecimalPointCharacter = decimalPointCharacter;
        iGrouping = group;
        iForceDecimalPoint = forceDecimalPoint;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to use.
     * 
     * @return the locale, never null
     */
    public Locale getLocale() {
        return iLocale;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used to separate groups, typically thousands.
     * 
     * @return the grouping character
     */
    public char getGroupingCharacter() {
        return iGroupingCharacter;
    }

    /**
     * Returns a copy of this instance with the specified grouping character.
     * <p>
     * For English, this is a comma.
     * 
     * @param groupingCharacter  the grouping character
     * @return the new instance, never null
     */
    public MoneyFormatter withGroupingCharacter(char groupingCharacter) {
        return new MoneyFormatter(iLocale, groupingCharacter, iDecimalPointCharacter, iGrouping, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used for the decimal point.
     * 
     * @return the grouping character
     */
    public char getDecimalPointCharacter() {
        return iGroupingCharacter;
    }

    /**
     * Returns a copy of this instance with the specified decimal point character.
     * <p>
     * For English, this is a dot.
     * 
     * @param decimalPointCharacter  the decimal point character
     * @return the new instance, never null
     */
    public MoneyFormatter withDecimalPointCharacter(char decimalPointCharacter) {
        return new MoneyFormatter(iLocale, iGroupingCharacter, decimalPointCharacter, iGrouping, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets whether to use the grouping separator, typically for thousands.
     * 
     * @return whether to use the grouping separator
     */
    public char isGrouping() {
        return iGroupingCharacter;
    }

    /**
     * Returns a copy of this instance with the specified grouping setting.
     * 
     * @param grouping  true to use the grouping separator, false to not use it
     * @return the new instance, never null
     */
    public MoneyFormatter withGrouping(boolean grouping) {
        return new MoneyFormatter(iLocale, iGroupingCharacter, iDecimalPointCharacter, grouping, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets whether to always use the decimal point, even if there is no fraction.
     * 
     * @return whether to use the grouping separator
     */
    public char isForcedDecimalPoint() {
        return iGroupingCharacter;
    }

    /**
     * Returns a copy of this instance with the specified decimal point setting.
     * 
     * @param forceDecimalPoint  true to force the use of the decimal point, false to use it if required
     * @return the new instance, never null
     */
    public MoneyFormatter withForcedDecimalPoint(boolean forceDecimalPoint) {
        return new MoneyFormatter(iLocale, iGroupingCharacter, iDecimalPointCharacter, iGrouping, forceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets whether to always use the decimal point, even if there is no fraction.
     * 
     * @return whether to use the grouping separator
     */
    public String print(Money money) {
        NumberFormat format = NumberFormat.getCurrencyInstance(iLocale);
        format.setGroupingUsed(iGrouping);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setDecimalSeparatorAlwaysShown(iForceDecimalPoint);
        }
        format.setCurrency(money.getCurrencyUnit().toCurrency());
        return format.format(money.getAmount());
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the monetary value as a string.
//     * 
//     * @return true if this instance equals the other instance
//     */
//    @Override
//    public boolean equals(Object other) {
//        if (this == other) {
//            return true;
//        }
//        if (other instanceof MoneyFormatter) {
//            MoneyFormatter otherMoney = (MoneyFormatter) other;
//            return iCurrency.equals(otherMoney.getCurrencyUnit()) &&
//                    iAmount == otherMoney.getAmountMinor();
//        }
//        return false;
//    }
//
//    /**
//     * Returns a hash code for this instance.
//     * 
//     * @return a suitable hash code
//     */
//    @Override
//    public int hashCode() {
//        return iCurrency.hashCode() ^ ((int) (iAmount ^ (iAmount >>> 32)));
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Gets the monetary value as a string.
//     * 
//     * @return the monetary value, never null
//     */
//    @Override
//    public String toString() {
//        StringBuilder buf = new StringBuilder();
//        long factor = factor(getDecimalPlaces());
//        buf.append(iCurrency.getCurrencyCode()).append(' ');
//        long amount = iAmount;
//        long minor;
//        if (amount < 0) {
//            buf.append('-').append(-(amount / factor));
//            minor = -(amount % factor);
//        } else {
//            buf.append(amount / factor);
//            minor = amount % factor;
//        }
//        if (getDecimalPlaces() > 0) {
//            int index = buf.length();
//            buf.append(minor + factor).setCharAt(index, '.');
//        }
//        return buf.toString();
//    }

}
