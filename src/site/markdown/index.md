## <i></i> About

**Joda-Money** provides a library of classes to store amounts of money.

The JDK provides a standard currency class, but not a standard representation of money.
Joda-Money fills this gap, providing the value types to represent money.

Joda-Money is licensed under the business-friendly [Apache 2.0 licence](licenses.html).


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
  <b><a href="apidocs/org.joda.money/org/joda/money/Money.html">Money</a> money = Money.parse("USD 23.87");</b>
  
  // add another amount with safe double conversion
  <b><a href="apidocs/org.joda.money/org/joda/money/CurrencyUnit.html">CurrencyUnit</a> usd = CurrencyUnit.of("USD");</b>
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
  <b><a href="apidocs/org.joda.money/org/joda/money/BigMoney.html">BigMoney</a> moneyCalc = money.toBigMoney();</b>
</pre>


---

## <i></i> Releases

[Release 1.0.3](download.html) is the current release.
This release is considered stable and worthy of the 1.x tag.

Joda-Money requires Java SE 8 or later and has [no dependencies](dependencies.html).
There is a *compile-time* dependency on [Joda-Convert](https://www.joda.org/joda-convert/),
but this is not required at runtime thanks to the magic of annotations.

Available in [Maven Central](https://search.maven.org/search?q=g:org.joda%20AND%20a:joda-money&core=gav).

```xml
<dependency>
  <groupId>org.joda</groupId>
  <artifactId>joda-money</artifactId>
  <version>1.0.3</version>
</dependency>
```

Java module name: `org.joda.money`.

---

### For Enterprise

[Available as part of the Tidelift Subscription](https://tidelift.com/subscription/pkg/maven-org-joda-joda-money?utm_source=maven-org-joda-joda-money&utm_medium=referral&utm_campaign=enterprise).

Joda and the maintainers of thousands of other packages are working with Tidelift to deliver one
enterprise subscription that covers all of the open source you use.

If you want the flexibility of open source and the confidence of commercial-grade software, this is for you.
[Learn more](https://tidelift.com/subscription/pkg/maven-org-joda-joda-money?utm_source=maven-org-joda-joda-money&utm_medium=referral&utm_campaign=enterprise).


### Support

Please use [Stack Overflow](https://stackoverflow.com/questions/tagged/joda-money) for general usage questions.
GitHub [issues](https://github.com/JodaOrg/joda-money/issues) and [pull requests](https://github.com/JodaOrg/joda-money/pulls)
should be used when you want to help advance the project.

Any donations to support the project are accepted via [OpenCollective](https://opencollective.com/joda).

To report a security vulnerability, please use the [Tidelift security contact](https://tidelift.com/security).
Tidelift will coordinate the fix and disclosure.
