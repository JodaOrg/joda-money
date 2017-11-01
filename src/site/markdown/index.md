## <i></i> About

**Joda-Money** provides a library of classes to store amounts of money.

The JDK provides a standard currency class, but not a standard representation of money.
Joda-Money fills this gap, providing the value types to represent money.

Joda-Money is licensed under the business-friendly [Apache 2.0 licence](license.html).


## <i></i> Features

A selection of key features:

* `CurrencyUnit` - representing a currency
* `Money` - a fixed precision monetary value type
* `BigMoney` - a variable precision monetary type
* A customizable formatter


## <i></i> Documentation

Various documentation is available:

* The helpful [user guide](userguide.html)
* The [Javadoc](apidocs/index.html)
* The [change notes](changes-report.html) for each release
* The [GitHub](https://github.com/JodaOrg/joda-money) source repository


---

## <i></i> Why Joda Money?

Joda-Money provides simple value types, representing currency and money.

The project does not provide, nor is it intended to provide, monetary algorithms beyond the most basic and obvious.
This is because the requirements for these algorithms vary widely between domains.
This library is intended to act as the base layer, providing classes that should be in the JDK.

As a flavour of Joda-Money, here is some example code:

<pre>
  // create a monetary value
  <b><a href="apidocs/org/joda/money/Money.html">Money</a> money = Money.parse("USD 23.87");</b>
  
  // add another amount with safe double conversion
  <b><a href="apidocs/org/joda/money/CurrencyUnit.html">CurrencyUnit</a> usd = CurrencyUnit.of("USD");</b>
  <b>money = money.plus(Money.of(usd, 12.43d));</b>
  
  // subtracts an amount in dollars
  <b>money = money.minusMajor(2);</b>
  
  // multiplies by 3.5 with rounding
  <b>money = money.multipliedBy(3.5d, RoundingMode.DOWN);</b>
  
  // compare two amounts
  <b>boolean bigAmount = money.isGreaterThan(dailyWage);</b>
  
  // convert to GBP using a supplied rate
  <b>BigDecimal conversionRate = ...;  // obtained from code outside Joda-Money</b>
  <b>Money moneyGBP = money.convertedTo(CurrencyUnit.GBP, conversionRate, RoundingMode.HALF_UP);</b>
  
  // use a BigMoney for more complex calculations where scale matters
  <b><a href="apidocs/org/joda/money/BigMoney.html">BigMoney</a> moneyCalc = money.toBigMoney();</b>
</pre>


---

## <i></i> Releases

[Release 1.0](download.html) is the current release.
This release is considered stable and worthy of the 1.x tag.

Joda-Money requires Java SE 6 or later and has [no dependencies](dependencies.html).
There is a *compile-time* dependency on [Joda-Convert](http://www.joda.org/joda-convert/),
but this is not required at runtime thanks to the magic of annotations.

Available in [Maven Central](http://search.maven.org/#artifactdetails%7Corg.joda%7Cjoda-money%7C1.0%7Cjar).

```xml
<dependency>
  <groupId>org.joda</groupId>
  <artifactId>joda-money</artifactId>
  <version>1.0</version>
</dependency>
```

---

### Support

Support on bugs, library usage or enhancement requests is available on a best efforts basis.

To suggest enhancements or contribute, please [fork the source code](https://github.com/JodaOrg/joda-money)
on GitHub and send a Pull Request.

Alternatively, use GitHub [issues](https://github.com/JodaOrg/joda-money/issues).
