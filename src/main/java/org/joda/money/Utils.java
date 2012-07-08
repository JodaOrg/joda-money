/*
 *  Copyright 2009-2011 Stephen Colebourne
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

/**
 * Common utility methods for use in the current package.
 * 
 * @author tpasierb
 */
class Utils {

    /**
     * <p>
     * Validate that the specified argument is not {@code null}; otherwise throwing an exception with the specified
     * message.
     * 
     * <pre>
     * Validate.notNull(myObject, &quot;The object must not be null&quot;);
     * </pre>
     * 
     * @param <T> the object type
     * @param object the object to check
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values the optional values for the formatted exception message
     * @return the validated object (never {@code null} for method chaining)
     * @throws NullPointerException if the object is {@code null}
     * 
     *             // copied from commons-lang3
     */
    public static <T> T notNull(T object, String message, Object... values) {
        if (object == null) {
            throw new NullPointerException(String.format(message, values));
        }
        return object;
    }

    /**
     * <p>
     * Validate that the argument condition is {@code true}; otherwise throwing an exception with the specified message.
     * This method is useful when validating according to an arbitrary boolean expression, such as validating a
     * primitive number or using your own custom validation expression.
     * </p>
     * 
     * <pre>
     * Validate.isTrue(i &gt;= min &amp;&amp; i &lt;= max, &quot;The value must be between %d and %d&quot;, min, max);
     * Validate.isTrue(myObject.isOk(), &quot;The object is not okay&quot;);
     * </pre>
     * 
     * @param expression the boolean expression to check
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values the optional values for the formatted exception message, null array not recommended
     * @throws IllegalArgumentException if expression is {@code false}
     * 
     *             // copied from commons-lang3
     */
    public static void isTrue(boolean expression, String message, Object... values) {
        if (expression == false) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

}
