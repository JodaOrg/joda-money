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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Defines the style that the amount of a <code>Money</code> will be formatted with.
 * <p>
 * The style contains four fields that may be configured based on the locale:
 * <ul>
 * <li>character used for zero, which defined all the numbers from zero to nine
 * <li>character used for the decimal point
 * <li>character used for grouping, such as grouping thousands
 * <li>size for each group, such as 3 for thousands
 * </ul>
 * <p>
 * The style can be used in three basic ways.
 * <ul>
 * <li>set all the fields manually, resulting in the same amount style for all locales
 * <li>call {@link #localize} manually and optionally adjust to set as required
 * <li>leave the four localized fields as <code>null</code> and let the locale in the
 *  formatter to determine the style
 * </ul>
 * <p>
 * MoneyAmountStyle is immutable and thread-safe.
 */
public final class MoneyAmountStyle {

    /**
     * A style that uses ASCII digits, the decimal point and groups in 3's using a comma.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_POINT_GROUP3_COMMA =
        new MoneyAmountStyle('0', '.', ',', true, 3, false);
    /**
     * A style that uses ASCII digits, the decimal point and groups in 3's using a space.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_POINT_GROUP3_SPACE =
        new MoneyAmountStyle('0', '.', ' ', true, 3, false);
    /**
     * A style that uses ASCII digits and the decimal point.
     * Grouping is setup to group in 3's using a comma, but is disabled.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_POINT_NO_GROUPING =
        new MoneyAmountStyle('0', '.', ',', false, 3, false);
    /**
     * A style that uses ASCII digits, the decimal comma and groups in 3's using a dot.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_COMMA_GROUP3_DOT =
        new MoneyAmountStyle('0', ',', '.', true, 3, false);
    /**
     * A style that uses ASCII digits, the decimal comma and groups in 3's using a space.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_COMMA_GROUP3_SPACE =
        new MoneyAmountStyle('0', ',', ' ', true, 3, false);
    /**
     * A style that uses ASCII digits and the decimal comma.
     * Grouping is setup to group in 3's using a dot, but is disabled.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_COMMA_NO_GROUPING =
        new MoneyAmountStyle('0', ',', '.', false, 3, false);
    /**
     * A style that will be filled in with localized values using the locale of the formatter.
     * Grouping is enabled. Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle LOCALIZED_GROUPING =
        new MoneyAmountStyle(null, null, null, true, null, false);
    /**
     * A style that will be filled in with localized values using the locale of the formatter.
     * Grouping is disabled. Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle LOCALIZED_NO_GROUPING =
        new MoneyAmountStyle(null, null, null, false, null, false);

    /**
     * The character defining zero, and thus the numbers zero to nine.
     */
    private final Character iZeroCharacter;
    /**
     * The character used for the decimal point.
     */
    private final Character iDecimalPointCharacter;
    /**
     * The character used for grouping.
     */
    private final Character iGroupingCharacter;
    /**
     * Whether to group or not.
     */
    private final boolean iGrouping;
    /**
     * The size of each group.
     */
    private final Integer iGroupingSize;
    /**
     * Whether to always require the decimal point to be visible.
     */
    private final boolean iForceDecimalPoint;

    //-----------------------------------------------------------------------
    /**
     * Gets a localized style.
     * <p>
     * This creates a localized style for the specified locale.
     * Grouping will be enabled, forced decimal point will be disabled.
     *
     * @param locale  the locale to use, not null
     * @return the new instance, never null
     */
    public static MoneyAmountStyle of(Locale locale) {
        return LOCALIZED_GROUPING.localize(locale);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param decimalPointCharacter  the decimal point character
     * @param groupingCharacter  the grouping character
     * @param group  whether to use the group character
     * @param groupingSize  the grouping size
     * @param forceDecimalPoint  whether to always use the decimal point character
     * @param locale  the locale to use, not null
     * @param postivePattern  the positive pattern
     * @param negativePattern  the negative pattern
     * @param zeroPattern  the zero pattern
     */
    private MoneyAmountStyle(
                Character zeroCharacter,
                Character decimalPointCharacter, Character groupingCharacter,
                boolean group, Integer groupingSize, boolean forceDecimalPoint) {
        iZeroCharacter = zeroCharacter;
        iDecimalPointCharacter = decimalPointCharacter;
        iGroupingCharacter = groupingCharacter;
        iGrouping = group;
        iGroupingSize = groupingSize;
        iForceDecimalPoint = forceDecimalPoint;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a <code>MoneyAmountStyle</code> instance configured for the specified locale.
     * <p>
     * This method will return a new instance where each field that was defined
     * to be localized (by being set to <code>null</code>) is filled in.
     * If this instance is fully defined (with all fields non-null), then this
     * method has no effect. Once this method is called, no method will return null.
     * <p>
     * The settings for the locale are pulled from {@link DecimalFormatSymbols} and
     * {@link DecimalFormat}.
     * 
     * @param locale  the locale to use, not null
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle localize(Locale locale) {
        MoneyFormatter.checkNotNull(locale, "Locale must not be null");
        MoneyAmountStyle result = this;
        DecimalFormatSymbols symbols = null;
        if (iZeroCharacter == null) {
            symbols = new DecimalFormatSymbols(locale);
            result = result.withZeroCharacter(symbols.getZeroDigit());
        }
        if (iDecimalPointCharacter == null) {
            symbols = (symbols == null ? new DecimalFormatSymbols(locale) : symbols);
            result = result.withDecimalPointCharacter(symbols.getMonetaryDecimalSeparator());
        }
        if (iGroupingCharacter == null) {
            symbols = (symbols == null ? new DecimalFormatSymbols(locale) : symbols);
            result = result.withGroupingCharacter(symbols.getGroupingSeparator());
        }
        if (iGroupingSize == null) {
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            int size = (format instanceof DecimalFormat ? ((DecimalFormat) format).getGroupingSize() : 3);
            result = result.withGroupingSize(size);
        }
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used for zero, and defining the characters zero to nine.
     * <p>
     * The UTF-8 standard supports a number of different numeric scripts.
     * Each script has the characters in order from zero to nine.
     * This method returns the zero character, which therefore also defines one to nine.
     * 
     * @return the zero character, null if to be determined by locale
     */
    public Character getZeroCharacter() {
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
     * @param zeroCharacter  the zero character, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withZeroCharacter(Character zeroCharacter) {
        if (zeroCharacter == iZeroCharacter ||
                (zeroCharacter != null && zeroCharacter.equals(iZeroCharacter))) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
                iGrouping, iGroupingSize, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used for the decimal point.
     * 
     * @return the decimal point character, null if to be determined by locale
     */
    public Character getDecimalPointCharacter() {
        return iDecimalPointCharacter;
    }

    /**
     * Returns a copy of this instance with the specified decimal point character.
     * <p>
     * For English, this is a dot.
     * 
     * @param decimalPointCharacter  the decimal point character, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withDecimalPointCharacter(Character decimalPointCharacter) {
        if (decimalPointCharacter == iDecimalPointCharacter ||
                (decimalPointCharacter != null && decimalPointCharacter.equals(iDecimalPointCharacter))) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter, decimalPointCharacter, iGroupingCharacter,
                iGrouping, iGroupingSize, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used to separate groups, typically thousands.
     * 
     * @return the grouping character, null if to be determined by locale
     */
    public Character getGroupingCharacter() {
        return iGroupingCharacter;
    }

    /**
     * Returns a copy of this instance with the specified grouping character.
     * <p>
     * For English, this is a comma.
     * 
     * @param groupingCharacter  the grouping character, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withGroupingCharacter(Character groupingCharacter) {
        if (groupingCharacter == iGroupingCharacter ||
                (groupingCharacter != null && groupingCharacter.equals(iGroupingCharacter))) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter, iDecimalPointCharacter, groupingCharacter,
                iGrouping, iGroupingSize, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets whether to use the grouping separator, typically for thousands.
     * 
     * @return whether to use the grouping separator
     */
    public boolean isGrouping() {
        return iGrouping;
    }

    /**
     * Returns a copy of this instance with the specified grouping setting.
     * 
     * @param grouping  true to use the grouping separator, false to not use it
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withGrouping(boolean grouping) {
        if (grouping == iGrouping) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
                grouping, iGroupingSize, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the size of each group, typically 3 for thousands.
     * 
     * @return the size of each group, null if to be determined by locale
     */
    public Integer getGroupingSize() {
        return iGroupingSize;
    }

    /**
     * Returns a copy of this instance with the specified grouping size.
     * 
     * @param groupingSize  the size of each group, such as 3 for thousands, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withGroupingSize(Integer groupingSize) {
        if (groupingSize == iGroupingSize || (groupingSize != null && groupingSize.equals(iGroupingSize))) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
                iGrouping, groupingSize, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets whether to always use the decimal point, even if there is no fraction.
     * 
     * @return whether to force the decimal point on output
     */
    public boolean isForcedDecimalPoint() {
        return iForceDecimalPoint;
    }

    /**
     * Returns a copy of this instance with the specified decimal point setting.
     * 
     * @param forceDecimalPoint  true to force the use of the decimal point, false to use it if required
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withForcedDecimalPoint(boolean forceDecimalPoint) {
        if (forceDecimalPoint == iForceDecimalPoint) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
                iGrouping, iGroupingSize, forceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this style with another.
     * 
     * @param other  the other style, null returns false
     * @return true if equal
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MoneyAmountStyle == false) {
            return false;
        }
        MoneyAmountStyle otherStyle = (MoneyAmountStyle) other;
        return (iZeroCharacter == otherStyle.iZeroCharacter ||
                    iZeroCharacter != null && iZeroCharacter.equals(otherStyle.iZeroCharacter)) &&
                (iDecimalPointCharacter == otherStyle.iDecimalPointCharacter ||
                    iDecimalPointCharacter != null && iDecimalPointCharacter.equals(otherStyle.iDecimalPointCharacter)) &&
                (iGroupingCharacter == otherStyle.iGroupingCharacter ||
                    iGroupingCharacter != null && iGroupingCharacter.equals(otherStyle.iGroupingCharacter)) &&
                (iGrouping == otherStyle.iGrouping) &&
                (iGroupingSize == otherStyle.iGroupingSize ||
                    iGroupingSize != null && iGroupingSize.equals(otherStyle.iGroupingSize)) &&
                (iForceDecimalPoint == otherStyle.iForceDecimalPoint);
    }

    /**
     * A suitable hash code.
     * 
     * @return a hash code
     */
    @Override
    public int hashCode() {
        int hash = 13;
        hash += (iZeroCharacter == null ? 0 : iZeroCharacter.hashCode()) * 17;
        hash += (iDecimalPointCharacter == null ? 0 : iDecimalPointCharacter.hashCode()) * 17;
        hash += (iGroupingCharacter == null ? 0 : iGroupingCharacter.hashCode()) * 17;
        hash += (iGroupingSize == null ? 0 : iGroupingSize.hashCode()) * 17;
        hash += (iGrouping ? 1 : 0);
        hash += (iForceDecimalPoint ? 2 : 4);
        return hash;
    }

}
