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
 * The style contains a number of fields that may be configured based on the locale:
 * <ul>
 * <li>character used for zero, which defined all the numbers from zero to nine
 * <li>character used for positive and negative symbols
 * <li>character used for the decimal point
 * <li>whether and how to group the amount
 * <li>character used for grouping, such as grouping thousands
 * <li>size for each group, such as 3 for thousands
 * <li>whether to always use a decimal point
 * </ul>
 * <p>
 * The style can be used in three basic ways.
 * <ul>
 * <li>set all the fields manually, resulting in the same amount style for all locales
 * <li>call {@link #localize} manually and optionally adjust to set as required
 * <li>leave the localized fields as {@code null} and let the locale in the
 *  formatter to determine the style
 * </ul>
 * <p>
 * This class is immutable and thread-safe.
 */
public final class MoneyAmountStyle implements Serializable {

    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and groups large amounts in 3's using a comma.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_POINT_GROUP3_COMMA =
        new MoneyAmountStyle('0', '+', '-', '.', GroupingStyle.FULL, ',', 3, 0, false, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and groups large amounts in 3's using a space.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_POINT_GROUP3_SPACE =
        new MoneyAmountStyle('0', '+', '-', '.', GroupingStyle.FULL, ' ', 3, 0, false, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and no grouping of large amounts.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_POINT_NO_GROUPING =
        new MoneyAmountStyle('0', '+', '-', '.', GroupingStyle.NONE, ',', 3, 0, false, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal comma
     * and groups large amounts in 3's using a dot.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_COMMA_GROUP3_DOT =
        new MoneyAmountStyle('0', '+', '-', ',', GroupingStyle.FULL, '.', 3, 0, false, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal comma
     * and groups large amounts in 3's using a space.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_COMMA_GROUP3_SPACE =
        new MoneyAmountStyle('0', '+', '-', ',', GroupingStyle.FULL, ' ', 3, 0, false, false);
    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and no grouping of large amounts.
     * Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle ASCII_DECIMAL_COMMA_NO_GROUPING =
        new MoneyAmountStyle('0', '+', '-', ',', GroupingStyle.NONE, '.', 3, 0, false, false);
    /**
     * A style that will be filled in with localized values using the locale of the formatter.
     * Grouping is enabled. Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle LOCALIZED_GROUPING =
        new MoneyAmountStyle(-1, -1, -1, -1, GroupingStyle.FULL, -1, -1, -1, false, false);
    /**
     * A style that will be filled in with localized values using the locale of the formatter.
     * Grouping is disabled. Forced decimal point is disabled.
     */
    public static final MoneyAmountStyle LOCALIZED_NO_GROUPING =
        new MoneyAmountStyle(-1, -1, -1, -1, GroupingStyle.NONE, -1, -1, -1, false, false);
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
    private final int zeroCharacter;
    /**
     * The character representing the positive sign.
     */
    private final int positiveCharacter;
    /**
     * The prefix string when the amount is negative.
     */
    private final int negativeCharacter;
    /**
     * The character used for the decimal point.
     */
    private final int decimalPointCharacter;
    /**
     * Whether to group or not.
     */
    private final GroupingStyle groupingStyle;
    /**
     * The character used for grouping.
     */
    private final int groupingCharacter;
    /**
     * The size of each group.
     */
    private final int groupingSize;
    /**
     * The size of each group.
     */
    private final int extendedGroupingSize;
    /**
     * Whether to always require the decimal point to be visible.
     */
    private final boolean forceDecimalPoint;
    /**
     * Whether to use the absolute value instead of the signed value.
     */
    private final boolean absValue;

    //-----------------------------------------------------------------------
    /**
     * Gets a localized style.
     * <p>
     * This creates a localized style for the specified locale.
     * Grouping will be enabled, forced decimal point will be disabled,
     * absolute values will be disabled.
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
     * @param groupingStyle  the grouping style, not null
     * @param groupingCharacter  the grouping character
     * @param groupingSize  the grouping size
     * @param forceDecimalPoint  whether to always use the decimal point character
     * @param absValue  true to output the absolute value rather than the signed value
     */
    private MoneyAmountStyle(
                int zeroCharacter,
                int positiveCharacter, int negativeCharacter,
                int decimalPointCharacter, GroupingStyle groupingStyle,
                int groupingCharacter, int groupingSize, int extendedGroupingSize,
                boolean forceDecimalPoint, boolean absValue) {
        this.zeroCharacter = zeroCharacter;
        this.positiveCharacter = positiveCharacter;
        this.negativeCharacter = negativeCharacter;
        this.decimalPointCharacter = decimalPointCharacter;
        this.groupingStyle = groupingStyle;
        this.groupingCharacter = groupingCharacter;
        this.groupingSize = groupingSize;
        this.extendedGroupingSize = extendedGroupingSize;
        this.forceDecimalPoint = forceDecimalPoint;
        this.absValue = absValue;
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
        if (zeroCharacter < 0) {
            protoStyle = getLocalizedStyle(locale);
            result = result.withZeroCharacter(protoStyle.getZeroCharacter());
        }
        if (positiveCharacter < 0) {
            protoStyle = getLocalizedStyle(locale);
            result = result.withPositiveSignCharacter(protoStyle.getPositiveSignCharacter());
        }
        if (negativeCharacter < 0) {
            protoStyle = getLocalizedStyle(locale);
            result = result.withNegativeSignCharacter(protoStyle.getNegativeSignCharacter());
        }
        if (decimalPointCharacter < 0) {
            protoStyle = (protoStyle == null ? getLocalizedStyle(locale) : protoStyle);
            result = result.withDecimalPointCharacter(protoStyle.getDecimalPointCharacter());
        }
        if (groupingCharacter < 0) {
            protoStyle = (protoStyle == null ? getLocalizedStyle(locale) : protoStyle);
            result = result.withGroupingCharacter(protoStyle.getGroupingCharacter());
        }
        if (groupingSize < 0) {
            protoStyle = (protoStyle == null ? getLocalizedStyle(locale) : protoStyle);
            result = result.withGroupingSize(protoStyle.getGroupingSize());
        }
        if (extendedGroupingSize < 0) {
            protoStyle = (protoStyle == null ? getLocalizedStyle(locale) : protoStyle);
            result = result.withExtendedGroupingSize(protoStyle.getExtendedGroupingSize());
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
                    GroupingStyle.FULL, symbols.getGroupingSeparator(), size, 0, false, false);
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
        return zeroCharacter < 0 ? null : (char) zeroCharacter;
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
        if (zeroVal == this.zeroCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroVal,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize, forceDecimalPoint, absValue);
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
        return positiveCharacter < 0 ? null : (char) positiveCharacter;
    }

    /**
     * Returns a copy of this style with the specified positive sign character.
     * <p>
     * The standard ASCII symbol is '+'.
     * 
     * @param positiveCharacter  the positive character, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withPositiveSignCharacter(Character positiveCharacter) {
        int positiveVal = (positiveCharacter == null ? -1 : positiveCharacter);
        if (positiveVal == this.positiveCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveVal, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize, forceDecimalPoint, absValue);
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
        return negativeCharacter < 0 ? null : (char) negativeCharacter;
    }

    /**
     * Returns a copy of this style with the specified negative sign character.
     * <p>
     * The standard ASCII symbol is '-'.
     * 
     * @param negativeCharacter  the negative character, null if to be determined by locale
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withNegativeSignCharacter(Character negativeCharacter) {
        int negativeVal = (negativeCharacter == null ? -1 : negativeCharacter);
        if (negativeVal == this.negativeCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeVal,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize, forceDecimalPoint, absValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used for the decimal point.
     * 
     * @return the decimal point character, null if to be determined by locale
     */
    public Character getDecimalPointCharacter() {
        return decimalPointCharacter < 0 ? null : (char) decimalPointCharacter;
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
        if (dpVal == this.decimalPointCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                dpVal, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize, forceDecimalPoint, absValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character used to separate groups, typically thousands.
     * 
     * @return the grouping character, null if to be determined by locale
     */
    public Character getGroupingCharacter() {
        return groupingCharacter < 0 ? null : (char) groupingCharacter;
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
        if (groupingVal == this.groupingCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingVal, groupingSize, extendedGroupingSize, forceDecimalPoint, absValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the size of each group, typically 3 for thousands.
     * 
     * @return the size of each group, null if to be determined by locale
     */
    public Integer getGroupingSize() {
        return groupingSize < 0 ? null : groupingSize;
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
        if (groupingSize != null && sizeVal <= 0) {
            throw new IllegalArgumentException("Grouping size must be greater than zero");
        }
        if (sizeVal == this.groupingSize) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, sizeVal, extendedGroupingSize, forceDecimalPoint, absValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the size of each group, not typically used.
     * <p>
     * This is primarily used to enable the Indian Number System, where the group
     * closest to the decimal point is of size 3 and other groups are of size 2.
     * The extended grouping size is used for groups that are not next to the decimal point.
     * The value zero is used to indicate that extended grouping is not needed.
     * 
     * @return the size of each group, null if to be determined by locale
     */
    public Integer getExtendedGroupingSize() {
        return extendedGroupingSize < 0 ? null : extendedGroupingSize;
    }

    /**
     * Returns a copy of this style with the specified extended grouping size.
     * 
     * @param extendedGroupingSize  the size of each group, such as 3 for thousands,
     *          not zero or negative, null if to be determined by locale
     * @return the new instance for chaining, never null
     * @throws IllegalArgumentException if the grouping size is zero or less
     */
    public MoneyAmountStyle withExtendedGroupingSize(Integer extendedGroupingSize) {
        int sizeVal = (extendedGroupingSize == null ? -1 : extendedGroupingSize);
        if (extendedGroupingSize != null && sizeVal < 0) {
            throw new IllegalArgumentException("Extended grouping size must not be negative");
        }
        if (sizeVal == this.extendedGroupingSize) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, sizeVal, forceDecimalPoint, absValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the style of grouping required.
     * 
     * @return the grouping style, not null
     */
    public GroupingStyle getGroupingStyle() {
        return groupingStyle;
    }

    /**
     * Returns a copy of this style with the specified grouping setting.
     * 
     * @param groupingStyle  the grouping style, not null
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withGroupingStyle(GroupingStyle groupingStyle) {
        MoneyFormatter.checkNotNull(groupingStyle, "groupingStyle");
        if (this.groupingStyle == groupingStyle) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize, forceDecimalPoint, absValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets whether to always use the decimal point, even if there is no fraction.
     * 
     * @return whether to force the decimal point on output
     */
    public boolean isForcedDecimalPoint() {
        return forceDecimalPoint;
    }

    /**
     * Returns a copy of this style with the specified decimal point setting.
     * 
     * @param forceDecimalPoint  true to force the use of the decimal point, false to use it if required
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withForcedDecimalPoint(boolean forceDecimalPoint) {
        if (this.forceDecimalPoint == forceDecimalPoint) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize, forceDecimalPoint, absValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns true if the absolute value setting.
     * <p>
     * If this is set to true, the absolute (unsigned) value will be output.
     * If this is set to false, the signed value will be output.
     * Note that when parsing, signs are accepted.
     * 
     * @return true to output the absolute value, false for the signed value
     */
    public boolean isAbsValue() {
        return absValue;
    }

    /**
     * Returns a copy of this style with the specified absolute value setting.
     * <p>
     * If this is set to true, the absolute (unsigned) value will be output.
     * If this is set to false, the signed value will be output.
     * Note that when parsing, signs are accepted.
     * 
     * @param absValue  true to output the absolute value, false for the signed value
     * @return the new instance for chaining, never null
     */
    public MoneyAmountStyle withAbsValue(boolean absValue) {
        if (this.absValue == absValue) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize, forceDecimalPoint, absValue);
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
        return (zeroCharacter == otherStyle.zeroCharacter) &&
                (positiveCharacter == otherStyle.positiveCharacter) &&
                (negativeCharacter == otherStyle.negativeCharacter) &&
                (decimalPointCharacter == otherStyle.decimalPointCharacter) &&
                (groupingStyle == otherStyle.groupingStyle) &&
                (groupingCharacter == otherStyle.groupingCharacter) &&
                (groupingSize == otherStyle.groupingSize) &&
                (forceDecimalPoint == otherStyle.forceDecimalPoint) &&
                (absValue == otherStyle.absValue);
    }

    /**
     * A suitable hash code.
     * 
     * @return a hash code
     */
    @Override
    public int hashCode() {
        int hash = 13;
        hash += zeroCharacter * 17;
        hash += positiveCharacter * 17;
        hash += negativeCharacter * 17;
        hash += decimalPointCharacter * 17;
        hash += groupingStyle.hashCode() * 17;
        hash += groupingCharacter * 17;
        hash += groupingSize * 17;
        hash += (forceDecimalPoint ? 2 : 4);
        hash += (absValue ? 3 : 9);
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
            getGroupingStyle() + "," + getGroupingCharacter() + "','" + getGroupingSize() + "'," +
            isForcedDecimalPoint() + "'," + isAbsValue() + "]";
    }

}
