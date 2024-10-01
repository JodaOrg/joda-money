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

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Test that the classpath/modulepath is correctly set.
 */
class TestModulepath {

    @Test
    void dumpPaths() {
        var classpath = System.getProperty("java.class.path", "");
        var modulepath = System.getProperty("jdk.module.path", "");
        System.out.println("Classpath:  " + describePath(classpath));
        System.out.println("Modulepath: " + describePath(modulepath));
    }

    private static String describePath(String path) {
        if (path.isEmpty()) {
            return "<empty>";
        }
        var list = new ArrayList<String>();
        if (path.contains("target/classes") || path.contains("target\\classes")) {
            list.add("target/classes");
        }
        if (path.contains("target/test-classes") || path.contains("target\\test-classes")) {
            list.add("target/test-classes");
        }
        if (path.contains("joda-convert")) {
            list.add("joda-convert");
        }
        if (path.contains("junit-jupiter")) {
            list.add("junit-jupiter");
        }
        return list.isEmpty() ? path : list.stream().collect(joining(" "));
    }
}
