/*
 *  Copyright 2009-2010 Stephen Colebourne
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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Defines the style that the amount of a monetary value will be formatted with.
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
 * <li>leave the four localized fields as {@code null} and let the locale in the
 *  formatter to determine the style
 * </ul>
 * <p>
 * MoneyAmountStyle is immutable and thread-safe.
 */
public final class MoneyAmountStyle implements Serializable {

    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and groups large amounts in 3's using a comma.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_POINT_GROUP3_COMMA =
        new MoneyAmountStyle('0', '+', '-', '.', ',', 3, true, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and groups large amounts in 3's using a space.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_POINT_GROUP3_SPACE =
        new MoneyAmountStyle('0', '+', '-', '.', ' ', 3, true, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and no grouping of large amounts.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_POINT_NO_GROUPING =
        new MoneyAmountStyle('0', '+', '-', '.', ',', 3, false, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal comma
     * and groups large amounts in 3's using a dot.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_COMMA_GROUP3_DOT =
        new MoneyAmountStyle('0', '+', '-', ',', '.', 3, true, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal comma
     * and groups large amounts in 3's using a space.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_COMMA_GROUP3_SPACE =
        new MoneyAmountStyle('0', '+', '-', ',', ' ', 3, true, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and no grouping of large amounts.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_COMMA_NO_GROUPING =
        new MoneyAmountStyle('0', '+', '-', ',', '.', 3, false, false);
    /**
     * A style that will be filled in with localized values using the locale of the formatter.
     * Grouping is enabled. Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle LOCALIZED_GROUPING =
        new MoneyAmountStyle(-1, -1, -1, -1, -1, -1, true, false);
    /**
     * A style that will be filled in with localized values using the locale of the formatter.
     * Grouping is disabled. Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle LOCALIZED_NO_GROUPING =
        new MoneyAmountStyle(-1, -1, -1, -1, -1, -1, false, false);
    /**
     * Cache of localized styles.
     */
    private static final ConcurrentMap<Locale, MoneyAmountStyle> LOCALIZED_CACHE = new ConcurrentHashMap<Locale, MoneyAmountStyle>();
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The character defining zero, and thus the numbers zero to nine.
     */
    private final int iZeroCharacter;
    /**
     * The character representing the positive sign.
     */
    private final int iPositiveCharacter;
    /**
     * The prefix string when the amount is negative.
     */
    private final int iNegativeCharacter;
    /**
     * The character used for the decimal point.
     */
    private final int iDecimalPointCharacter;
    /**
     * The character used for grouping.
     */
    private final int iGroupingCharacter;
    /**
     * The size of each group.
     */
    private final int iGroupingSize;
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
     * Gets a localized style.
     * <p>
     * This creates a localized style for the specified locale.
     * Grouping will be enabled, forced decimal point will be disabled.
     *
     * @param locale  the locale to use, not null
     * @return the new instance, never null
     */
    public static MoneyAmountStyle of(Locale locale) {
        return getLocalizedStyle(locale);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param zeroCharacter  the zero character
     * @param postiveCharacter  the positive sign
     * @param negativeCharacter  the negative sign
     * @param decimalPointCharacter  the decimal point character
     * @param groupingCharacter  the grouping character
     * @param groupingSize  the grouping size
     * @param group  whether to use the group character
     * @param forceDecimalPoint  whether to always use the decimal point character
     */
    private MoneyAmountStyle(
                int zeroCharacter,
                int positiveCharacter, int negativeCharacter,
                int decimalPointCharacter, int groupingCharacter,
                int groupingSize, boolean group, boolean forceDecimalPoint) {
        iZeroCharacter = zeroCharacter;
        iPositiveCharacter = positiveCharacter;
        iNegativeCharacter = negativeCharacter;
        iDecimalPointCharacter = decimalPointCharacter;
        iGroupingCharacter = groupingCharacter;
        iGroupingSize = groupingSize;
        iGrouping = group;
        iForceDecimalPoint = forceDecimalPoint;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a {@code MoneyAmountStyle} instance configured for the specified locale.
     * <p>
     * This method will return a new instance where each field that was defined
     * to be localized (by being set to {@code null}) is filled in.
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
        MoneyAmountStyle protoStyle = null;
        if (iZeroCharacter < 0) {
            protoStyle = getLocalizedStyle(locale);
            result = result.withZeroCharacter(protoStyle.getZeroCharacter());
        }
        if (iPositiveCharacter < 0) {
            protoStyle = getLocalizedStyle(locale);
            result = result.withPositiveSignCharacter(protoStyle.getPositiveSignCharacter());
        }
        if (iNegativeCharacter < 0) {
            protoStyle = getLocalizedStyle(locale);
            result = result.withNegativeSignCharacter(protoStyle.getNegativeSignCharacter());
        }
        if (iDecimalPointCharacter < 0) {
            protoStyle = (protoStyle == null ? getLocalizedStyle(locale) : protoStyle);
            result = result.withDecimalPointCharacter(protoStyle.getDecimalPointCharacter());
        }
        if (iGroupingCharacter < 0) {
            protoStyle = (protoStyle == null ? getLocalizedStyle(locale) : protoStyle);
            result = result.withGroupingCharacter(protoStyle.getGroupingCharacter());
        }
        if (iGroupingSize < 0) {
            protoStyle = (protoStyle == null ? getLocalizedStyle(locale) : protoStyle);
            result = result.withGroupingSize(protoStyle.getGroupingSize());
        }
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the prototype localized style for the given locale.
     * <p>
     * This uses {@link DecimalFormatSymbols} and {@link NumberFormat}.
     * <p>
     * If JDK 6 or newer is being used, {@code DecimalFormatSymbols.getInstance(locale)}
     * will be used in order to allow the use of locales defined as extensions.
     * Otherwise, {@code new DecimalFormatSymbols(locale)} will be used.
     * 
     * @param locale  the {@link Locale} used to get the correct {@link DecimalFormatSymbols}
     * @return the symbols, never null
     */
    private static MoneyAmountStyle getLocalizedStyle(Locale locale) {
        MoneyAmountStyle protoStyle = LOCALIZED_CACHE.get(locale);
        if (protoStyle == null) {
            DecimalFormatSymbols symbols;
            try {
                Method method = DecimalFormatSymbols.class.getMethod("getInstance", new Class[] {Locale.class});
                symbols = (DecimalFormatSymbols) method.invoke(null, new Object[] {locale});  // handle JDK 6
            } catch (Exception ex) {
                symbols = new DecimalFormatSymbols(locale);  // handle JDK 5
            }
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            int size = (format instanceof DecimalFormat ? ((DecimalFormat) format).getGroupingSize() : 3);
            protoStyle = new MoneyAmountStyle(
                    symbols.getZeroDigit(),
                    '+', symbols.getMinusSign(),
                    symbols.getMonetaryDecimalSeparator(),
                    symbols.getGroupingSeparator(), size, true, false);
            LOCALIZED_CACHE.putIfAbsent(locale, protoStyle);
        }
        return protoStyle;
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
        return iZeroCharacter < 0 ? null : (char) iZeroCharacter;
    }

    /**
     * Returns a copy of this style with the specified zero character.
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
        int zeroVal = (zeroCharacter == null ? -1 : zeroCharacter);
        if (zeroVal == iZeroCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroVal,
                iPositiveCharacter, iNegativeCharacter,
                iDecimalPointCharacter, iGroupingCharacter,
                iGroupingSize, iGrouping, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used for the positive sign character.
     * <p>
     * The standard ASCII symbol is '+'.
     * 
     * @return the format for positive amounts, null if to be determined by locale
     */
    public Character getPositiveSignCharacter() {
        return iPositiveCharacter < 0 ? null : (char) iPositiveCharacter;
    }

    /**
     * Returns a copy of this style with the specified positive sign character.
     * <p>
     * The standard ASCII symbol is '+'.
     * 
     * @param zeroCharacter  the zero character, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withPositiveSignCharacter(Character positiveCharacter) {
        int positiveVal = (positiveCharacter == null ? -1 : positiveCharacter);
        if (positiveVal == iPositiveCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter,
                positiveVal, iNegativeCharacter,
                iDecimalPointCharacter, iGroupingCharacter,
                iGroupingSize, iGrouping, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used for the negative sign character.
     * <p>
     * The standard ASCII symbol is '-'.
     * 
     * @return the format for negative amounts, null if to be determined by locale
     */
    public Character getNegativeSignCharacter() {
        return iNegativeCharacter < 0 ? null : (char) iNegativeCharacter;
    }

    /**
     * Returns a copy of this style with the specified negative sign character.
     * <p>
     * The standard ASCII symbol is '-'.
     * 
     * @param zeroCharacter  the zero character, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withNegativeSignCharacter(Character negativeCharacter) {
        int negativeVal = (negativeCharacter == null ? -1 : negativeCharacter);
        if (negativeVal == iNegativeCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter,
                iPositiveCharacter, negativeVal,
                iDecimalPointCharacter, iGroupingCharacter,
                iGroupingSize, iGrouping, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used for the decimal point.
     * 
     * @return the decimal point character, null if to be determined by locale
     */
    public Character getDecimalPointCharacter() {
        return iDecimalPointCharacter < 0 ? null : (char) iDecimalPointCharacter;
    }

    /**
     * Returns a copy of this style with the specified decimal point character.
     * <p>
     * For English, this is a dot.
     * 
     * @param decimalPointCharacter  the decimal point character, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withDecimalPointCharacter(Character decimalPointCharacter) {
        int dpVal = (decimalPointCharacter == null ? -1 : decimalPointCharacter);
        if (dpVal == iDecimalPointCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter,
                iPositiveCharacter, iNegativeCharacter,
                dpVal, iGroupingCharacter,
                iGroupingSize, iGrouping, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used to separate groups, typically thousands.
     * 
     * @return the grouping character, null if to be determined by locale
     */
    public Character getGroupingCharacter() {
        return iGroupingCharacter < 0 ? null : (char) iGroupingCharacter;
    }

    /**
     * Returns a copy of this style with the specified grouping character.
     * <p>
     * For English, this is a comma.
     * 
     * @param groupingCharacter  the grouping character, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withGroupingCharacter(Character groupingCharacter) {
        int groupingVal = (groupingCharacter == null ? -1 : groupingCharacter);
        if (groupingVal == iGroupingCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter,
                iPositiveCharacter, iNegativeCharacter,
                iDecimalPointCharacter, groupingVal,
                iGroupingSize, iGrouping, iForceDecimalPoint);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the size of each group, typically 3 for thousands.
     * 
     * @return the size of each group, null if to be determined by locale
     */
    public Integer getGroupingSize() {
        return iGroupingSize < 0 ? null : iGroupingSize;
    }

    /**
     * Returns a copy of this style with the specified grouping size.
     * 
     * @param groupingSize  the size of each group, such as 3 for thousands,
     *          not zero or negative, null if to be determined by locale
     * @return the new instance for chaining, never null
     * @throws IllegalArgumentException if the grouping size is zero or less
     */
    public MoneyAmountStyle withGroupingSize(Integer groupingSize) {
        int sizeVal = (groupingSize == null ? -1 : groupingSize);
        if (groupingSize != null && sizeVal <= 0 ) {
            throw new IllegalArgumentException("Grouping size must be greater than zero");
        }
        if (sizeVal == iGroupingSize) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter,
                iPositiveCharacter, iNegativeCharacter,
                iDecimalPointCharacter, iGroupingCharacter,
                sizeVal, iGrouping, iForceDecimalPoint);
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
     * Returns a copy of this style with the specified grouping setting.
     * 
     * @param grouping  true to use the grouping separator, false to not use it
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withGrouping(boolean grouping) {
        if (grouping == iGrouping) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter,
                iPositiveCharacter, iNegativeCharacter,
                iDecimalPointCharacter, iGroupingCharacter,
                iGroupingSize, grouping, iForceDecimalPoint);
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
     * Returns a copy of this style with the specified decimal point setting.
     * 
     * @param forceDecimalPoint  true to force the use of the decimal point, false to use it if required
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withForcedDecimalPoint(boolean forceDecimalPoint) {
        if (forceDecimalPoint == iForceDecimalPoint) {
            return this;
        }
        return new MoneyAmountStyle(
                iZeroCharacter,
                iPositiveCharacter, iNegativeCharacter,
                iDecimalPointCharacter, iGroupingCharacter,
                iGroupingSize, iGrouping, forceDecimalPoint);
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
        return (iZeroCharacter == otherStyle.iZeroCharacter) &&
                (iPositiveCharacter == otherStyle.iPositiveCharacter) &&
                (iNegativeCharacter == otherStyle.iNegativeCharacter) &&
                (iDecimalPointCharacter == otherStyle.iDecimalPointCharacter) &&
                (iGroupingCharacter == otherStyle.iGroupingCharacter) &&
                (iGroupingSize == otherStyle.iGroupingSize) &&
                (iGrouping == otherStyle.iGrouping) &&
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
        hash += iZeroCharacter * 17;
        hash += iPositiveCharacter * 17;
        hash += iNegativeCharacter * 17;
        hash += iDecimalPointCharacter * 17;
        hash += iGroupingCharacter * 17;
        hash += iGroupingSize * 17;
        hash += (iGrouping ? 1 : 0);
        hash += (iForceDecimalPoint ? 2 : 4);
        return hash;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a string summary of the style.
     * 
     * @return a string summarising the style, never null
     */
    @Override
    public String toString() {
        return "MoneyAmountStyle['" + getZeroCharacter() + "','" + getPositiveSignCharacter() + "','" +
            getNegativeSignCharacter() + "','" + getDecimalPointCharacter() + "','" +
            getGroupingCharacter() + "','" + getGroupingSize() +
            "'," + isGrouping() + "," + isForcedDecimalPoint() + "]";
    }

}
