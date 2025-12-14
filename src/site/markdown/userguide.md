## User guide

Joda-Money is a small Java library providing classes to store, format and parse amounts of money.
This is a brief user guide to the features available.


## Main classes

There are two main classes which are both based on `BigDecimal`:

* **`Money`** - an amount in a single currency where the decimal places are determined by the currency
* **`BigMoney`** - an amount in a single currency where there is no restriction on the scale

For example, the currencies of US dollars, Euros and British Pounds all use 2 decimal places,
thus a `Money` instance for any of these currencies will have 2 decimal places.
By contrast, the Japanese Yen has 0 decimal places, and thus any `Money` instance with that
currency will have 0 decimal places.

Instances of `BigMoney` may have a scale that is positive or zero.
Negative scales are normalized to zero.

Conversion between a `Money` and a `BigMoney` can be performed using the methods
`toBigMoney()` and `toMoney()`. The latter optionally takes a rounding mode to
handle the case where information must be lost.

Both classes implement the `BigMoneyProvider` interface.
This allows many different classes, including user supplied classes, to interoperate with Joda-Money.
In application code, the best advice is to use the concrete types.
However, utility and framework code will typically use the interface.
The formatting code is a good example of this.


## Currency information

Joda-Money includes its own currency class as the implementation in the JDK is too restrictive.
The data for the Joda-Money class is provided by two configuration files.

The file `org/joda/money/CurrencyData.csv` holds a basic list of valid currencies.
The columns are as follows:

* [ISO-4217](https://en.wikipedia.org/wiki/ISO_4217) currency code - three letters, mandatory
* [ISO-4217](https://en.wikipedia.org/wiki/ISO_4217) numeric code - from 1 to 999, set to -1 if no numeric code
* Number of decimal places in common usage - the supplied data is based on various sources

If you place a file on the classpath named `META-INF/org/joda/money/CurrencyDataExtension.csv`
then the data it contains will augment and replace the base data.
The extension file has the same format as the base file.

The file `org/joda/money/CountryData.csv` holds a mapping from ISO-3166 country code to currency.
The columns are as follows:

* [ISO-3166-1](https://en.wikipedia.org/wiki/ISO_3166-1) country code - two letters, mandatory
* [ISO-4217](https://en.wikipedia.org/wiki/ISO_4217) currency code - three letters, mandatory

If you place a file on the classpath named `META-INF/org/joda/money/CountryDataExtension.csv`
then the data it contains will augment and replace the base data.
The extension file has the same format as the base file.

(Note that in previous versions, these data files had different names and different formats.)


## Formatting

Formatting is based around the `MoneyFormatterBuilder` class.
The builder is used to create a suitable format which is then converted to an immutable `MoneyFormatter` instance.
The format may contain localized aspects which can be used by calling `withLocale()` on the formatter.
This returns a new formatter as it is immutable.

The symbol information for formatting is currently provided by the JDK.
  
<br />
<br />
<br />
