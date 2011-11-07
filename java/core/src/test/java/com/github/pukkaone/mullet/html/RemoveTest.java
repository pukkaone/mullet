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
 * Tests remove command.
 */
public class RemoveTest extends TemplateTests {

    @Test
    public void remove_element_should_remove_tag_and_content()
        throws Exception
    {
        Template template = loader.load("remove-element.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<ul>" +
                  "<li>subject 1</li>" +
                  "<li>subject 2</li>" +
                "</ul>";
        assertEquals(EXPECTED_OUTPUT, elementToString("ul"));
    }

    @Test
    public void remove_tag_should_remove_tag_and_preserve_content()
        throws Exception
    {
        setModelValue("greeting", "Hello");

        Template template = loader.load("remove-tag.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "HelloWorld" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void remove_content_should_preserve_tag_and_remove_content()
        throws Exception
    {
        setModelValue("greeting", "Hello");

        Template template = loader.load("remove-content.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p title=\"Hello\"></p>" +
                  "<p></p>" +
                  "<p></p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }
}
