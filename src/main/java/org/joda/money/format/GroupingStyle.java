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

/**
 * Defines the style for numeric grouping.
 * <p>
 * This provides control over the grouping of numbers in formatting.
 * <p>
 * This class is immutable and thread-safe.
 */
public enum GroupingStyle {

    /**
     * No grouping occurs.
     */
    NONE,
    /**
     * No grouping occurs.
     */
    FULL,
    /**
     * Grouping occurs, but only before the decimal point.
     */
    BEFORE_DECIMAL_POINT;

}
