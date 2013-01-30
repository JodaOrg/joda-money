/*
 *  Copyright 2009-2013 Stephen Colebourne
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.testng.annotations.Test;

/**
 * Test CurrencyUnit.
 */
@Test
public class TestCurrencyUnitExtension {

  
  public void test_CurrencyFromMoneyData() {
    List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
    boolean found = false;
    for (CurrencyUnit currencyUnit : curList) {
        if (currencyUnit.getCode().equals("GBP")) {
            found = true;
            break;
        }
    }
    assertEquals(found, true);
   }

    public void test_CurrencyFromMoneyDataExtension() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("BTC")) {
                found = true;
                break;
            }
        }
        assertEquals(found, true);
    }

    public void test_CurrencyMissing() {
      List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
      boolean found = false;
      for (CurrencyUnit currencyUnit : curList) {
          if (currencyUnit.getCode().equals("NMC")) {
              found = true;
              break;
          }
      }
      assertEquals(found, false);
  }

}
