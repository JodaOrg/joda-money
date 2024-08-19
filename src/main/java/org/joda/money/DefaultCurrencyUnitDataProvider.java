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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Provider for available currencies using a file.
 * <p>
 * This reads currencies from various files.
 * Firstly it reads the mandatory resource named {@code /org/joda/money/CurencyData.csv}.
 * Then it reads the mandatory resource named {@code /org/joda/money/CountryData.csv}.
 * These files are located in the joda-money jar file.
 * <p>
 * Then it reads optional resources named {@code META-INF/org/joda/money/CurencyDataExtension.csv}.
 * Then it reads optional resources named {@code META-INF/org/joda/money/CountryDataExtension.csv}.
 * These will be read using {@link ClassLoader#getResources(String)}.
 * These files may augment or replace data from the first two files.
 */
class DefaultCurrencyUnitDataProvider extends CurrencyUnitDataProvider {

    /** Regex format for the money csv line. */
    private static final Pattern CURRENCY_REGEX_LINE = Pattern.compile("([A-Z]{3}),(-1|[0-9]{1,3}),(-1|[0-9]|[1-2][0-9]|30) *(#.*)?");
    /** Regex format for the country csv line. */
    private static final Pattern COUNTRY_REGEX_LINE = Pattern.compile("([A-Z]{2}),([A-Z]{3}) *(#.*)?");

    /**
     * Registers all the currencies known by this provider.
     *
     * @throws Exception if an error occurs
     */
    @Override
    protected void registerCurrencies() throws Exception {
        parseCurrencies(loadFromFile("/org/joda/money/CurrencyData.csv"));
        parseCountries(loadFromFile("/org/joda/money/CountryData.csv"));
        parseCurrencies(loadFromFiles("META-INF/org/joda/money/CurrencyDataExtension.csv"));
        parseCountries(loadFromFiles("META-INF/org/joda/money/CountryDataExtension.csv"));
    }

    // loads a file
    private List<String> loadFromFile(String fileName) throws Exception {
        try (var in = getClass().getResourceAsStream(fileName)) {
            if (in == null) {
                throw new FileNotFoundException("Data file " + fileName + " not found");
            }
            try (var reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
                String line;
                List<String> content = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    content.add(line);
                }
                return content;
            }
        }
    }

    // loads a file
    private List<String> loadFromFiles(String fileName) throws Exception {
        List<String> content = new ArrayList<>();
        var en = getClass().getClassLoader().getResources(fileName);
        while (en.hasMoreElements()) {
            var url = en.nextElement();
            try (var in = url.openStream()) {
                try (var reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.add(line);
                    }
                }
            }
        }
        return content;
    }

    // parse the currencies
    private void parseCurrencies(List<String> content) throws Exception {
        for (String line : content) {
            var matcher = CURRENCY_REGEX_LINE.matcher(line);
            if (matcher.matches()) {
                var currencyCode = matcher.group(1);
                var numericCode = Integer.parseInt(matcher.group(2));
                var digits = Integer.parseInt(matcher.group(3));
                registerCurrency(currencyCode, numericCode, digits);
            }
        }
    }

    // parse the countries
    private void parseCountries(List<String> content) throws Exception {
        for (String line : content) {
            var matcher = COUNTRY_REGEX_LINE.matcher(line);
            if (matcher.matches()) {
                var countryCode = matcher.group(1);
                var currencyCode = matcher.group(2);
                registerCountry(countryCode, currencyCode);
            }
        }
    }

}
