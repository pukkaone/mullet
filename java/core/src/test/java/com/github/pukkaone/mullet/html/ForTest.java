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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests for command.
 */
public class ForTest extends TemplateTests {

    private static final String EMPTY_LIST_OUTPUT =
            "<ul>" +
            "</ul>";

    private static final String NON_EMPTY_LIST_OUTPUT =
            "<ul>" +
              "<li title=\"subject 1\">" +
                "<span>subject 1</span>" +
              "</li>" +
              "<li title=\"subject 2\">" +
                "<span>subject 2</span>" +
              "</li>" +
            "</ul>";

    private String ul() throws IOException, SAXException {
        return elementToString("ul");
    }

    @Test
    public void missing_value_should_not_render_element() throws Exception {
        setVariable("name-is-not-posts", null);

        Template template = loader.load("for.html");
        template.execute(data, writer);

        assertEquals(EMPTY_LIST_OUTPUT, ul());
    }

    @Test
    public void null_value_should_not_render_element() throws Exception {
        setVariable("posts", null);

        Template template = loader.load("for.html");
        template.execute(data, writer);

        assertEquals(EMPTY_LIST_OUTPUT, ul());
    }

    @Test
    public void empty_array_should_not_render_element() throws Exception {
        setVariable("posts", new int[0]);

        Template template = loader.load("for.html");
        template.execute(data, writer);

        assertEquals(EMPTY_LIST_OUTPUT, ul());
    }

    @Test
    public void empty_list_should_not_render_element() throws Exception {
        setVariable("posts", Collections.EMPTY_LIST);

        Template template = loader.load("for.html");
        template.execute(data, writer);

        assertEquals(EMPTY_LIST_OUTPUT, ul());
    }

    @Test
    public void nonempty_array_should_render_elements() throws Exception {
        Template template = loader.load("for.html");
        template.execute(data, writer);

        assertEquals(NON_EMPTY_LIST_OUTPUT, ul());
    }

    @Test
    public void nonempty_list_should_render_elements() throws Exception {
        setVariable("posts", Arrays.asList(POSTS));

        Template template = loader.load("for.html");
        template.execute(data, writer);

        assertEquals(NON_EMPTY_LIST_OUTPUT, ul());
    }

    @Test
    public void object_should_render_element() throws Exception {
        setVariable("posts", POSTS[0]);

        Template template = loader.load("for.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<ul>" +
                  "<li title=\"subject 1\">" +
                    "<span>subject 1</span>" +
                  "</li>" +
                "</ul>";
        assertEquals(EXPECTED_OUTPUT, ul());
    }

    @Test
    public void this_should_render_current_item() throws Exception {
        final String[] COLORS = new String[] {
            "red", "green", "blue"
        };
        setVariable("colors", COLORS);

        Template template = loader.load("this.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<ul>" +
                  "<li>red</li>" +
                  "<li>green</li>" +
                  "<li>blue</li>" +
                "</ul>";
        assertEquals(EXPECTED_OUTPUT, ul());
    }

    @Test
    public void for_remove_tag_should_remove_tag_and_render_content()
        throws Exception
    {
        Template template = loader.load("for-remove-tag.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
            "<table>" +
              "<tbody>" +
                "<tr><td>Subject</td></tr>" +
                "<tr><td>subject 1</td></tr>" +
                "<tr><td>Subject</td></tr>" +
                "<tr><td>subject 2</td></tr>" +
              "</tbody>" +
            "</table>";
        assertEquals(EXPECTED_OUTPUT, elementToString("table"));
    }

    @Test
    public void for_remove_content_should_render_tag_and_remove_content()
        throws Exception
    {
        Template template = loader.load("for-remove-content.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
            "<ul>" +
              "<li title=\"subject 1\"></li>" +
              "<li title=\"subject 2\"></li>" +
            "</ul>";
        assertEquals(EXPECTED_OUTPUT, ul());
    }
}
