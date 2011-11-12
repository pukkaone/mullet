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
 * Tests attribute command.
 */
public class AttributeTest extends TemplateTests {

    @Test
    public void should_escape_quote_in_attribute_value() throws Exception {
        Template template = loader.load("attribute-quote.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>\n" +
                "  <p lang=\"&#034;&\"></p>\n" +
                "  <p lang=\"'&\"></p>\n" +
                "</body>\n";
        assertEquals(EXPECTED_OUTPUT, writer.toString());
    }

    @Test
    public void namespace_prefix_attribute_command_should_set_attribute()
        throws Exception
    {
        setVariable("languageCode", "add");

        Template template = loader.load("namespace-prefix-attribute.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p lang=\"add\"></p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void default_prefix_attribute_command_should_set_attribute()
        throws Exception
    {
        setVariable("languageCode", "add");

        Template template = loader.load("default-prefix-attribute.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p lang=\"add\"></p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void should_set_attribute_when_attribute_does_not_exist()
        throws Exception
    {
        setVariable("languageCode", "add");

        Template template = loader.load("attribute-add.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p lang=\"add\"></p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void should_replace_existing_attribute() throws Exception {
        setVariable("languageCode", "replace");

        Template template = loader.load("attribute-replace.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p lang=\"replace\"></p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void should_set_multiple_attributes() throws Exception {
        setVariable("languageCode", "add");
        setVariable("greeting", "replace");

        Template template = loader.load("attribute-multiple.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p lang=\"add\" title=\"replace\">" +
                  "</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void missing_value_should_remove_attribute() throws Exception {
        setVariable("greeting", "replace");

        Template template = loader.load("attribute-multiple.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p title=\"replace\">" +
                  "</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void null_value_should_remove_attribute() throws Exception {
        setVariable("languageCode", null);
        setVariable("greeting", "replace");

        Template template = loader.load("attribute-multiple.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p title=\"replace\">" +
                  "</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void message_command_should_format_message_arguments()
        throws Exception
    {
        Template template = loader.load("attribute-message.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p title=\"Subject subject 1 Date 2011-12-01\">" +
                  "</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void action_command_should_set_action_attribute() throws Exception {
        setVariable("greeting", "add");

        Template template = loader.load("action.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<form action=\"add\">" +
                  "</form>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void alt_command_should_set_alt_attribute() throws Exception {
        setVariable("greeting", "add");

        Template template = loader.load("alt.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<img alt=\"add\">" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void alt_message_command_should_set_alt_attribute() throws Exception {
        Template template = loader.load("alt-message.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p alt=\"Subject subject 1 Date 2011-12-01\">" +
                  "</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void href_command_should_set_href_attribute() throws Exception {
        setVariable("greeting", "add");

        Template template = loader.load("href.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<a href=\"add\"></a>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void src_command_should_set_src_attribute() throws Exception {
        setVariable("greeting", "add");

        Template template = loader.load("src.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<img src=\"add\">" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void title_command_should_set_title_attribute() throws Exception {
        setVariable("greeting", "add");

        Template template = loader.load("title.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p title=\"add\">" +
                  "</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void title_message_command_should_set_title_attribute()
        throws Exception
    {
        Template template = loader.load("title-message.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p title=\"Subject subject 1 Date 2011-12-01\">" +
                  "</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void value_command_should_set_value_attribute() throws Exception {
        setVariable("greeting", "add");

        Template template = loader.load("value.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<input value=\"add\">" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void value_message_command_should_set_value_attribute()
        throws Exception
    {
        Template template = loader.load("value-message.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<input value=\"Subject subject 1 Date 2011-12-01\">" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }
}
