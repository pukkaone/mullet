/*
Copyright (c) 2011, Chin Huang
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.github.pukkaone.mullet.html;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests include command.
 */
public class IncludeTest extends TemplateTests {

    @Test
    public void should_set_content_to_file() throws Exception {
        setModelValue("greeting", "Hello");

        Template template = loader.load("include.html");
        template.render(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p>Hello</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void should_set_content_to_fragment() throws Exception {
        setModelValue("greeting", "Hello");

        Template template = loader.load("include-id.html");
        template.render(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p>Hello</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void should_replace_element_with_file() throws Exception {
        setModelValue("greeting", "Hello");

        Template template = loader.load("include-remove-tag.html");
        template.render(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p>Hello</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void should_replace_element_with_fragment() throws Exception {
        setModelValue("greeting", "Hello");

        Template template = loader.load("include-id-remove-tag.html");
        template.render(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p>Hello</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }
}