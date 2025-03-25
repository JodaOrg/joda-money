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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Defines the style that the amount of a monetary value will be formatted with.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class MoneyAmountStyle implements Serializable {

    // -----------------------------------------------------------------------
    // Private static fields for predefined styles
    // (Replaces the old public static final constants)
    private static final MoneyAmountStyle ASCII_DECIMAL_POINT_GROUP3_COMMA_CONST =
            new MoneyAmountStyle('0', '+', '-', '.', GroupingStyle.FULL, ',', 3, 0, false, false);

    private static final MoneyAmountStyle ASCII_DECIMAL_POINT_GROUP3_SPACE_CONST =
            new MoneyAmountStyle('0', '+', '-', '.', GroupingStyle.FULL, ' ', 3, 0, false, false);

    private static final MoneyAmountStyle ASCII_DECIMAL_POINT_NO_GROUPING_CONST =
            new MoneyAmountStyle('0', '+', '-', '.', GroupingStyle.NONE, ',', 3, 0, false, false);

    private static final MoneyAmountStyle ASCII_DECIMAL_COMMA_GROUP3_DOT_CONST =
            new MoneyAmountStyle('0', '+', '-', ',', GroupingStyle.FULL, '.', 3, 0, false, false);

    private static final MoneyAmountStyle ASCII_DECIMAL_COMMA_GROUP3_SPACE_CONST =
            new MoneyAmountStyle('0', '+', '-', ',', GroupingStyle.FULL, ' ', 3, 0, false, false);

    private static final MoneyAmountStyle ASCII_DECIMAL_COMMA_NO_GROUPING_CONST =
            new MoneyAmountStyle('0', '+', '-', ',', GroupingStyle.NONE, '.', 3, 0, false, false);

    private static final MoneyAmountStyle LOCALIZED_GROUPING_CONST =
            new MoneyAmountStyle(-1, -1, -1, -1, GroupingStyle.FULL, -1, -1, -1, false, false);

    private static final MoneyAmountStyle LOCALIZED_NO_GROUPING_CONST =
            new MoneyAmountStyle(-1, -1, -1, -1, GroupingStyle.NONE, -1, -1, -1, false, false);

    /**
     * Cache of localized styles.
     */
    private static final ConcurrentMap<Locale, MoneyAmountStyle> LOCALIZED_CACHE = new ConcurrentHashMap<>();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Private instance fields
    private final int zeroCharacter;
    private final int positiveCharacter;
    private final int negativeCharacter;
    private final int decimalPointCharacter;
    private final GroupingStyle groupingStyle;
    private final int groupingCharacter;
    private final int groupingSize;
    private final int extendedGroupingSize;
    private final boolean forceDecimalPoint;
    private final boolean absValue;

    // -----------------------------------------------------------------------
    // Public static methods to retrieve the styles (instead of old public constants)

    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and groups large amounts in 3's using a comma.
     * Forced decimal point is disabled.
     */
    public static MoneyAmountStyle asciiDecimalPointGroup3Comma() {
        return ASCII_DECIMAL_POINT_GROUP3_COMMA_CONST;
    }

    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and groups large amounts in 3's using a space.
     * Forced decimal point is disabled.
     */
    public static MoneyAmountStyle asciiDecimalPointGroup3Space() {
        return ASCII_DECIMAL_POINT_GROUP3_SPACE_CONST;
    }

    /**
     * A style that uses ASCII digits/negative sign, the decimal point
     * and no grouping of large amounts.
     * Forced decimal point is disabled.
     */
    public static MoneyAmountStyle asciiDecimalPointNoGrouping() {
        return ASCII_DECIMAL_POINT_NO_GROUPING_CONST;
    }

    /**
     * A style that uses ASCII digits/negative sign, the decimal comma
     * and groups large amounts in 3's using a dot.
     * Forced decimal point is disabled.
     */
    public static MoneyAmountStyle asciiDecimalCommaGroup3Dot() {
        return ASCII_DECIMAL_COMMA_GROUP3_DOT_CONST;
    }

    /**
     * A style that uses ASCII digits/negative sign, the decimal comma
     * and groups large amounts in 3's using a space.
     * Forced decimal point is disabled.
     */
    public static MoneyAmountStyle asciiDecimalCommaGroup3Space() {
        return ASCII_DECIMAL_COMMA_GROUP3_SPACE_CONST;
    }

    /**
     * A style that uses ASCII digits/negative sign, the decimal comma
     * and no grouping of large amounts.
     * Forced decimal point is disabled.
     */
    public static MoneyAmountStyle asciiDecimalCommaNoGrouping() {
        return ASCII_DECIMAL_COMMA_NO_GROUPING_CONST;
    }

    /**
     * A style that will be filled in with localized values using the locale of the formatter.
     * Grouping is enabled. Forced decimal point is disabled.
     */
    public static MoneyAmountStyle localizedGrouping() {
        return LOCALIZED_GROUPING_CONST;
    }

    /**
     * A style that will be filled in with localized values using the locale of the formatter.
     * Grouping is disabled. Forced decimal point is disabled.
     */
    public static MoneyAmountStyle localizedNoGrouping() {
        return LOCALIZED_NO_GROUPING_CONST;
    }

    // -----------------------------------------------------------------------
    // Private constructor
    private MoneyAmountStyle(
            int zeroCharacter,
            int positiveCharacter,
            int negativeCharacter,
            int decimalPointCharacter,
            GroupingStyle groupingStyle,
            int groupingCharacter,
            int groupingSize,
            int extendedGroupingSize,
            boolean forceDecimalPoint,
            boolean absValue) {

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

    // -----------------------------------------------------------------------
    // Public factory methods

    /**
     * Gets a localized style for the specified locale.
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

    /**
     * Returns a {@code MoneyAmountStyle} instance configured for the specified locale.
     * <p>
     * This method will return a new instance where each field that was defined
     * to be localized is filled in. If this instance is fully defined (with all fields
     * non-null), then this method has no effect.
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
            protoStyle = (protoStyle == null ? getLocalizedStyle(locale) : protoStyle);
            result = result.withPositiveSignCharacter(protoStyle.getPositiveSignCharacter());
        }
        if (negativeCharacter < 0) {
            protoStyle = (protoStyle == null ? getLocalizedStyle(locale) : protoStyle);
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

    private static MoneyAmountStyle getLocalizedStyle(Locale locale) {
        MoneyAmountStyle protoStyle = LOCALIZED_CACHE.get(locale);
        if (protoStyle == null) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            int size = (format instanceof DecimalFormat ? ((DecimalFormat) format).getGroupingSize() : 3);
            size = size <= 0 ? 3 : size;
            protoStyle = new MoneyAmountStyle(
                    symbols.getZeroDigit(),
                    '+',
                    symbols.getMinusSign(),
                    symbols.getMonetaryDecimalSeparator(),
                    GroupingStyle.FULL,
                    symbols.getGroupingSeparator(),
                    size,
                    0,
                    false,
                    false);
            LOCALIZED_CACHE.putIfAbsent(locale, protoStyle);
        }
        return protoStyle;
    }

    // -----------------------------------------------------------------------
    // Getters and 'with' methods

    public Character getZeroCharacter() {
        return zeroCharacter < 0 ? null : (char) zeroCharacter;
    }

    public MoneyAmountStyle withZeroCharacter(Character zeroCharacter) {
        int zeroVal = (zeroCharacter == null ? -1 : zeroCharacter);
        if (zeroVal == this.zeroCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroVal,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize,
                forceDecimalPoint, absValue);
    }

    public Character getPositiveSignCharacter() {
        return positiveCharacter < 0 ? null : (char) positiveCharacter;
    }

    public MoneyAmountStyle withPositiveSignCharacter(Character positiveCharacter) {
        int positiveVal = (positiveCharacter == null ? -1 : positiveCharacter);
        if (positiveVal == this.positiveCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveVal, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize,
                forceDecimalPoint, absValue);
    }

    public Character getNegativeSignCharacter() {
        return negativeCharacter < 0 ? null : (char) negativeCharacter;
    }

    public MoneyAmountStyle withNegativeSignCharacter(Character negativeCharacter) {
        int negativeVal = (negativeCharacter == null ? -1 : negativeCharacter);
        if (negativeVal == this.negativeCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeVal,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize,
                forceDecimalPoint, absValue);
    }

    public Character getDecimalPointCharacter() {
        return decimalPointCharacter < 0 ? null : (char) decimalPointCharacter;
    }

    public MoneyAmountStyle withDecimalPointCharacter(Character decimalPointCharacter) {
        int dpVal = (decimalPointCharacter == null ? -1 : decimalPointCharacter);
        if (dpVal == this.decimalPointCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                dpVal, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize,
                forceDecimalPoint, absValue);
    }

    public Character getGroupingCharacter() {
        return groupingCharacter < 0 ? null : (char) groupingCharacter;
    }

    public MoneyAmountStyle withGroupingCharacter(Character groupingCharacter) {
        int groupingVal = (groupingCharacter == null ? -1 : groupingCharacter);
        if (groupingVal == this.groupingCharacter) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingVal, groupingSize, extendedGroupingSize,
                forceDecimalPoint, absValue);
    }

    public Integer getGroupingSize() {
        return groupingSize < 0 ? null : groupingSize;
    }

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
                groupingCharacter, sizeVal, extendedGroupingSize,
                forceDecimalPoint, absValue);
    }

    public Integer getExtendedGroupingSize() {
        return extendedGroupingSize < 0 ? null : extendedGroupingSize;
    }

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
                groupingCharacter, groupingSize, sizeVal,
                forceDecimalPoint, absValue);
    }

    public GroupingStyle getGroupingStyle() {
        return groupingStyle;
    }

    public MoneyAmountStyle withGroupingStyle(GroupingStyle groupingStyle) {
        MoneyFormatter.checkNotNull(groupingStyle, "groupingStyle");
        if (this.groupingStyle == groupingStyle) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize,
                forceDecimalPoint, absValue);
    }

    public boolean isForcedDecimalPoint() {
        return forceDecimalPoint;
    }

    public MoneyAmountStyle withForcedDecimalPoint(boolean forceDecimalPoint) {
        if (this.forceDecimalPoint == forceDecimalPoint) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize,
                forceDecimalPoint, absValue);
    }

    public boolean isAbsValue() {
        return absValue;
    }

    public MoneyAmountStyle withAbsValue(boolean absValue) {
        if (this.absValue == absValue) {
            return this;
        }
        return new MoneyAmountStyle(
                zeroCharacter,
                positiveCharacter, negativeCharacter,
                decimalPointCharacter, groupingStyle,
                groupingCharacter, groupingSize, extendedGroupingSize,
                forceDecimalPoint, absValue);
    }

    // -----------------------------------------------------------------------
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MoneyAmountStyle)) {
            return false;
        }
        MoneyAmountStyle o = (MoneyAmountStyle) other;
        return (this.zeroCharacter == o.zeroCharacter) &&
                (this.positiveCharacter == o.positiveCharacter) &&
                (this.negativeCharacter == o.negativeCharacter) &&
                (this.decimalPointCharacter == o.decimalPointCharacter) &&
                (this.groupingStyle == o.groupingStyle) &&
                (this.groupingCharacter == o.groupingCharacter) &&
                (this.groupingSize == o.groupingSize) &&
                (this.extendedGroupingSize == o.extendedGroupingSize) &&
                (this.forceDecimalPoint == o.forceDecimalPoint) &&
                (this.absValue == o.absValue);
    }

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
        hash += extendedGroupingSize * 17;
        hash += (forceDecimalPoint ? 2 : 4);
        hash += (absValue ? 3 : 9);
        return hash;
    }

    @Override
    public String toString() {
        return "MoneyAmountStyle[" +
                "'" + getZeroCharacter() + "','" + getPositiveSignCharacter() + "','" +
                getNegativeSignCharacter() + "','" + getDecimalPointCharacter() + "'," +
                getGroupingStyle() + ",'" + getGroupingCharacter() + "','" +
                getGroupingSize() + "'," + isForcedDecimalPoint() + "," + isAbsValue() + "]";
    }
}
