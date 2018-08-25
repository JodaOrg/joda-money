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

/**
 * Joda-Money <strong>test</strong> module.
 */
open module org.joda.money {

    // ---
    // Copied from "main" module descriptor
    // ---

    // only annotations are used, thus they are optional
    requires static org.joda.convert;

    // all packages are exported
    exports org.joda.money;
    exports org.joda.money.format;

    // ---
    // Add modules required by test code
    // ---

    requires junit; // JUnit 3/4 vintage API

}
