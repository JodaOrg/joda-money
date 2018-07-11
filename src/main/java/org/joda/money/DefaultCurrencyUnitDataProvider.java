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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provider for available currencies using a file.
 * <p>
 * This reads currencies from two files.
 * Firstly it reads the mandatory resource named {@code /org/joda/money/MoneyData.csv}.
 * Then it reads the mandatory resource named {@code /org/joda/money/CountryData.csv}.
 * Then it reads the optional resource named {@code /org/joda/money/MoneyDataExtension.csv}.
 * Then it reads the optional resource named {@code /org/joda/money/CountryDataExtension.csv}.
 * Both will be read as the first found on the classpath.
 * The second file may replace entries in the first file.
 */
class DefaultCurrencyUnitDataProvider extends CurrencyUnitDataProvider {

    /** Regex format for the money csv line. */
    private static final Pattern MONEY_REGEX_LINE = Pattern.compile("([A-Z]{3}),(-1|[0-9]{1,3}),(-1|[0-9]|[1-2][0-9]|30)(?:,([A-Z]*))? *(#.*)?");
    /** Regex format for the country csv line. */
    private static final Pattern COUNTRY_REGEX_LINE = Pattern.compile("([A-Z]{2}),([A-Z]{3}) *(#.*)?");

    /**
     * Registers all the currencies known by this provider.
     * 
     * @throws Exception if an error occurs
     */ 
    @Override
    protected void registerCurrencies() throws Exception {
        loadCurrenciesFromFile("/org/joda/money/MoneyData.csv", true);
        loadCountriesFromFile("/org/joda/money/CountryData.csv", true);
        loadCurrenciesFromFile("/org/joda/money/MoneyDataExtension.csv", false);
        loadCountriesFromFile("/org/joda/money/CountryDataExtension.csv", false);
    }

    /**
     * Loads Currencies from a file
     *  
     * @param fileName  the file to load, not null
     * @param isNecessary  whether or not the file is necessary
     * @throws Exception if a necessary file is not found
     */
    private void loadCurrenciesFromFile(String fileName, boolean isNecessary) throws Exception {
        InputStream in = null;
        Exception resultEx = null;
        try {
            in = getClass().getResourceAsStream(fileName);
            if (in == null) {
                if (isNecessary) {
                    throw new FileNotFoundException("Data file " + fileName + " not found");
                } else {
                    return; // no extension file found, no problem. just return
                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = MONEY_REGEX_LINE.matcher(line);
                if (matcher.matches()) {
                    List<String> countryCodes = new ArrayList<String>();
                    String codeStr = matcher.group(4);
                    codeStr = codeStr == null ? "" : codeStr;
                    String currencyCode = matcher.group(1);
                    if (codeStr.length() % 2 == 1) {
                        continue;  // invalid line
                    }
                    for (int i = 0; i < codeStr.length(); i += 2) {
                        countryCodes.add(codeStr.substring(i, i + 2));
                    }
                    int numericCode = Integer.parseInt(matcher.group(2));
                    int digits = Integer.parseInt(matcher.group(3));
                    registerCurrency(currencyCode, numericCode, digits, countryCodes);
                }
            }
        } catch (Exception ex) {
            resultEx = ex;
            throw ex;
        } finally {
            if (in != null) {
                if (resultEx != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                        throw resultEx;
                    }
                } else {
                    in.close();
                }
            }
        }
    }

    /**
     * Loads countries from a file
     *  
     * @param fileName  the file to load, not null
     * @param isNecessary  whether or not the file is necessary
     * @throws Exception if a necessary file is not found
     */
    private void loadCountriesFromFile(String fileName, boolean isNecessary) throws Exception {
        InputStream in = null;
        Exception resultEx = null;
        try {
            in = getClass().getResourceAsStream(fileName);
            if (in == null) {
                if (isNecessary) {
                    throw new FileNotFoundException("Data file " + fileName + " not found");
                } else {
                    return; // no extension file found, no problem. just return
                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = COUNTRY_REGEX_LINE.matcher(line);
                if (matcher.matches()) {
                    String countryCode = matcher.group(1);
                    String currencyCode = matcher.group(2);
                    registerCountry(countryCode, currencyCode);
                }
            }
        } catch (Exception ex) {
            resultEx = ex;
            throw ex;
        } finally {
            if (in != null) {
                if (resultEx != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                        throw resultEx;
                    }
                } else {
                    in.close();
                }
            }
        }
    }

}
