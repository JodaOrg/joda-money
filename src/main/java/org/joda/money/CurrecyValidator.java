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

import java.util.List;
import java.util.regex.Pattern;

/**
 * Validates currency registration parameters.
 * <p>
 * This class provides validation logic for currency codes, numeric codes,
 * decimal places, and country codes used during currency registration.
 * All methods are static as this is a utility class for validation only.
 * <p>
 * This class is immutable and thread-safe.
 */
final class CurrencyValidator {

    /**
     * The currency code pattern - three uppercase ASCII letters.
     */
    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("[A-Z][A-Z][A-Z]");

    /**
     * Private constructor to prevent instantiation.
     */
    private CurrencyValidator() {
        // Utility class - no instances allowed
    }

    //-----------------------------------------------------------------------
    /**
     * Validates all parameters required for currency registration.
     * <p>
     * This method performs comprehensive validation of all currency
     * registration parameters in a single call.
     *
     * @param currencyCode  the currency code to validate, not null
     * @param numericCurrencyCode  the numeric code to validate
     * @param decimalPlaces  the decimal places to validate
     * @param countryCodes  the country codes to validate, not null
     * @throws IllegalArgumentException if any parameter is invalid
     * @throws NullPointerException if currencyCode or countryCodes is null
     */
    static void validateRegistrationParameters(
            String currencyCode,
            int numericCurrencyCode,
            int decimalPlaces,
            List<String> countryCodes) {
        
        validateCurrencyCode(currencyCode);
        validateNumericCurrencyCode(numericCurrencyCode);
        validateDecimalPlaces(decimalPlaces);
        MoneyUtils.checkNotNull(countryCodes, "Country codes must not be null");
    }

    /**
     * Validates the currency code format.
     * <p>
     * A valid currency code must be:
     * <ul>
     * <li>Exactly 3 characters long
     * <li>Consist only of uppercase ASCII letters (A-Z)
     * </ul>
     * Based on ISO-4217 standard.
     *
     * @param currencyCode  the currency code to validate, not null
     * @throws IllegalArgumentException if the currency code is invalid
     * @throws NullPointerException if currencyCode is null
     */
    static void validateCurrencyCode(String currencyCode) {
        MoneyUtils.checkNotNull(currencyCode, "Currency code must not be null");
        
        if (currencyCode.length() != 3) {
            throw new IllegalArgumentException("Invalid string code, must be length 3");
        }
        
        if (!CURRENCY_CODE_PATTERN.matcher(currencyCode).matches()) {
            throw new IllegalArgumentException("Invalid string code, must be ASCII upper-case letters");
        }
    }

    /**
     * Validates the numeric currency code.
     * <p>
     * A valid numeric currency code must be:
     * <ul>
     * <li>Between 0 and 999 (inclusive), or
     * <li>-1 to indicate no numeric code
     * </ul>
     * Based on ISO-4217 standard.
     *
     * @param numericCurrencyCode  the numeric code to validate
     * @throws IllegalArgumentException if the numeric code is invalid
     */
    static void validateNumericCurrencyCode(int numericCurrencyCode) {
        if (numericCurrencyCode < -1 || numericCurrencyCode > 999) {
            throw new IllegalArgumentException("Invalid numeric code");
        }
    }

    /**
     * Validates the number of decimal places.
     * <p>
     * Valid decimal places must be:
     * <ul>
     * <li>Between 0 and 30 (inclusive), or
     * <li>-1 for pseudo-currencies
     * </ul>
     * Most currencies use 0, 2, or 3 decimal places.
     *
     * @param decimalPlaces  the decimal places to validate
     * @throws IllegalArgumentException if the decimal places value is invalid
     */
    static void validateDecimalPlaces(int decimalPlaces) {
        if (decimalPlaces < -1 || decimalPlaces > 30) {
            throw new IllegalArgumentException("Invalid number of decimal places");
        }
    }

}