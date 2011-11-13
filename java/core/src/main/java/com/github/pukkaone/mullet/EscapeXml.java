/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pukkaone.mullet;

/**
 * Handles escaping of characters that could be interpreted as XML markup.
 * <p>The specification for <code>&lt;c:out&gt;</code> defines the following
 * character conversions to be applied:
 * <table rules="all" frame="border">
 * <tr><th>Character</th><th>Character Entity Code</th></tr>
 * <tr><td>&lt;</td><td>&amp;lt;</td></tr>
 * <tr><td>&gt;</td><td>&amp;gt;</td></tr>
 * <tr><td>&amp;</td><td>&amp;amp;</td></tr>
 * <tr><td>&#39;</td><td>&amp;#39;</td></tr>
 * <tr><td>&#34;</td><td>&amp;#34;</td></tr>
 * </table>
 */
public class EscapeXml {

    private static final String[] ESCAPES;

    static {
        int size = '>' + 1; // '>' is the largest escaped value
        ESCAPES = new String[size];
        ESCAPES['<'] = "&lt;";
        ESCAPES['>'] = "&gt;";
        ESCAPES['&'] = "&amp;";
        ESCAPES['\''] = "&#39;";
        ESCAPES['"'] = "&#34;";
    }

    private static String getEscape(char c) {
        if (c < ESCAPES.length) {
            return ESCAPES[c];
        } else {
            return null;
        }
    }

    /**
     * Escape a string.
     *
     * @param src
     *            the string to escape; must not be null
     * @return the escaped string
     */
    public static String escape(String src) {
        // first pass to determine the length of the buffer so we only allocate once
        int destLength = 0;
        for (int i = 0; i < src.length(); ++i) {
            char c = src.charAt(i);
            String escape = getEscape(c);
            if (escape != null) {
                destLength += escape.length();
            } else {
                ++destLength;
            }
        }

        // skip copy if no escaping is needed
        if (destLength == src.length()) {
            return src;
        }

        // second pass to build the escaped string
        StringBuilder dest = new StringBuilder(destLength);
        for (int i = 0; i < src.length(); ++i) {
            char c = src.charAt(i);
            String escape = getEscape(c);
            if (escape != null) {
                dest.append(escape);
            } else {
                dest.append(c);
            }
        }
        return dest.toString();
    }
}
