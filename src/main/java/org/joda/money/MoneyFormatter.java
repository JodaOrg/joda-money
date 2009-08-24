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
     * The character defining zero, and thus the numbers zero to nine.
     */
    private final char iZeroCharacter;
    /**
     * The character used for the decimal point.
     */
    private final char iDecimalPointCharacter;
    /**
     * The character used for grouping.
     */
    private final char iGroupingCharacter;
    /**
     * Whether to group or not.
     */
    private final boolean iGrouping;
    /**
     * The size of each group.
     */
    private final int iGroupingSize;
    /**
     * Whether to always require the decimal point to be visible.
     */
    private final boolean iForceDecimalPoint;
    /**
     * The positive pattern.
     */
    private final String iPositivePattern;
    /**
     * The negative pattern.
     */
    private final String iNegativePattern;

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of MoneyFormatter for the specified locale.
     *
     * @param locale  the locale to use, not null
     * @return the new instance, never null
     */
    public static MoneyFormatter of(Locale locale) {
        MoneyUtils.checkNotNull(locale, "Locale must not be null");
        return new MoneyFormatter(locale, '0', '.', ',', true, 3, false, "L #", "L -#");
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param locale  the locale to use, not null
     * @param decimalPointCharacter  the decimal point character
     * @param groupingCharacter  the grouping character
     * @param group  whether to use the group character
     * @param groupingSize  the grouping size
     * @param forceDecimalPoint  whether to always use the decimal point character
     * @param postivePattern  the positive pattern
     * @param negativePattern  the negative pattern
     */
    private MoneyFormatter(
                Locale locale, char zeroCharacter,
                char decimalPointCharacter, char groupingCharacter,
                boolean group, int groupingSize, boolean forceDecimalPoint,
                String postivePattern, String negativePattern) {
        iLocale = locale;
        iZeroCharacter = zeroCharacter;
        iDecimalPointCharacter = decimalPointCharacter;
        iGroupingCharacter = groupingCharacter;
        iGrouping = group;
        iGroupingSize = groupingSize;
        iForceDecimalPoint = forceDecimalPoint;
        iPositivePattern = postivePattern;
        iNegativePattern = negativePattern;
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
     * Gets the character used for zero, and defining the characters zero to nine.
     * <p>
     * The UTF-8 standard supports a number of different numeric scripts.
     * Each script has the characters in order from zero to nine.
     * This method returns the zero character, which therefore also defines one to nine.
     * 
     * @return the grouping character
     */
    public char getZeroCharacter() {
        return iZeroCharacter;
    }

    /**
     * Returns a copy of this instance with the specified zero character.
     * <p>
     * The UTF-8 standard supports a number of different numeric scripts.
     * Each script has the characters in order from zero to nine.
     * This method sets the zero character, which therefore also defines one to nine.
     * <p>
     * For English, this is a '0'. Some other scripts use different characters
     * for the numbers zero to nine.
     * 
     * @param groupingCharacter  the grouping character
     * @return the new instance, never null
     */
    public MoneyFormatter withZeroCharacter(char zeroCharacter) {
        return new MoneyFormatter(iLocale, zeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
                iGrouping, iGroupingSize, iForceDecimalPoint, iPositivePattern, iNegativePattern);
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
        return new MoneyFormatter(iLocale, iZeroCharacter, iDecimalPointCharacter, groupingCharacter,
                iGrouping, iGroupingSize, iForceDecimalPoint, iPositivePattern, iNegativePattern);
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
        return new MoneyFormatter(iLocale, iZeroCharacter, decimalPointCharacter, iGroupingCharacter,
                iGrouping, iGroupingSize, iForceDecimalPoint, iPositivePattern, iNegativePattern);
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
        return new MoneyFormatter(iLocale, iZeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
                grouping, iGroupingSize, iForceDecimalPoint, iPositivePattern, iNegativePattern);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the size of each group, typically 3 for thousands.
     * 
     * @return the size of each group
     */
    public int getGroupingSize() {
        return iGroupingSize;
    }

    /**
     * Returns a copy of this instance with the specified grouping size.
     * 
     * @param groupingSize  the size of each group, such as 3 for thousands
     * @return the new instance, never null
     */
    public MoneyFormatter withGroupingSize(int groupingSize) {
        return new MoneyFormatter(iLocale, iZeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
                iGrouping, groupingSize, iForceDecimalPoint, iPositivePattern, iNegativePattern);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets whether to always use the decimal point, even if there is no fraction.
     * 
     * @return whether to use the grouping separator
     */
    public boolean isForcedDecimalPoint() {
        return iForceDecimalPoint;
    }

    /**
     * Returns a copy of this instance with the specified decimal point setting.
     * 
     * @param forceDecimalPoint  true to force the use of the decimal point, false to use it if required
     * @return the new instance, never null
     */
    public MoneyFormatter withForcedDecimalPoint(boolean forceDecimalPoint) {
        return new MoneyFormatter(iLocale, iZeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
                iGrouping, iGroupingSize, forceDecimalPoint, iPositivePattern, iNegativePattern);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the positive pattern to use.
     * 
     * @return the positive pattern
     */
    public String getPositivePattern() {
        return iPositivePattern;
    }

    /**
     * Gets the negative pattern to use.
     * 
     * @return the negative pattern
     */
    public String getNegativePattern() {
        return iNegativePattern;
    }

    /**
     * Returns a copy of this instance with the specified positive and negative pattern.
     * <p>
     * The pattern contains the following elements:<br />
     * <ul>
     * <li><code>#</code> : the monetary amount itself
     * <li><code>L</code> : the letter-based currency code, such as 'USD'
     * <li><code>N</code> : the numeric currency code, such as '840'
     * <li><code>S</code> : the currency symbol, such as '$'
     * <li><code>-</code> : the negative symbol, such as '-'
     * </ul>
     * 
     * @param positivePattern  the positive pattern
     * @param negativePattern  the negative pattern
     * @return the new instance, never null
     */
    public MoneyFormatter withPattern(String positivePattern, String negativePattern) {
        MoneyUtils.checkNotNull(positivePattern, "positive pattern");
        MoneyUtils.checkNotNull(positivePattern, "negative pattern");
        if (positivePattern.contains("#") == false || negativePattern.contains("#") == false) {
            throw new IllegalArgumentException("The fraction digit values are invalid");
        }
        return new MoneyFormatter(iLocale, iZeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
                iGrouping, iGroupingSize, iForceDecimalPoint, positivePattern, negativePattern);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets whether to always use the decimal point, even if there is no fraction.
     * 
     * @return whether to use the grouping separator
     */
    public String print(Money money) {
        Locale[] availableLocales = NumberFormat.getAvailableLocales();
        for (Locale locale : availableLocales) {
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            System.out. println(locale + "  " + format.format(1.23) + "  " + ((DecimalFormat) format).toPattern());
        }
        
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
