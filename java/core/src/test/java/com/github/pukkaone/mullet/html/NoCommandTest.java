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
 * Tests templates having no commands.
 */
public class NoCommandTest extends TemplateTests {

    @Test
    public void no_command_should_render_template() throws Exception {
        Template template = loader.load("no-command.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p>Hello</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }

    @Test
    public void implicit_end_tag_should_not_render() throws Exception {
        Template template = loader.load("implicit-end-tag.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>\n" +
                "  <p>Hello\n" +
                "  <p>World\n" +
                "</body>\n";
        assertEquals(EXPECTED_OUTPUT, writer.toString());
    }

    @Test
    public void empty_content_model_should_render_single_tag()
        throws Exception
    {
        Template template = loader.load("empty-content-model.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>\n" +
                "  <br>\n" +
                "  <hr>\n" +
                "  <img src=\"logo.png\">\n" +
                "  <input>\n" +
                "</body>\n";
        assertEquals(EXPECTED_OUTPUT, writer.toString());
    }

    @Test
    public void non_empty_content_model_should_render_start_and_end_tag()
        throws Exception
    {
        Template template = loader.load("non-empty-content-model.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>\n" +
                "  <script src=\"jquery.min.js\"></script>\n" +
                "  <style type=\"text/css\"></style>\n" +
                "  <textarea name=\"comment\"></textarea>\n" +
                "</body>\n";
        assertEquals(EXPECTED_OUTPUT, writer.toString());
    }

    @Test
    public void comments_in_template_should_render() throws Exception {
        setVariable("greeting", "Hello");

        Template template = loader.load("comment.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
            "<!-- a -->\n" +
            "<!DOCTYPE html>\n" +
            "<!-- b -->\n" +
            "<html>\n" +
            "  <!-- c -->\n" +
            "  <body>Hello</body>\n" +
            "</html>\n" +
            "<!-- d -->\n";
        assertEquals(EXPECTED_OUTPUT, writer.toString());
    }

    @Test
    public void processing_instructions_in_template_should_render()
        throws Exception
    {
        setVariable("greeting", "Hello");

        Template template = loader.load("processing-instruction.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<?php a ?>\n" +
                "<!DOCTYPE html>\n" +
                "<?php b ?>\n" +
                "<html>\n" +
                "  <?php c ?>\n" +
                "  <body>Hello</body>\n" +
                "</html>\n" +
                "<?php d ?>\n";
        assertEquals(EXPECTED_OUTPUT, writer.toString());
    }

    @Test
    public void cdata_section_in_template_should_render() throws Exception {
        Template template = loader.load("cdata.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>\n" +
                "  <p><![CDATA[<&]]></p>\n" +
                "</body>\n";
        assertEquals(EXPECTED_OUTPUT, writer.toString());
    }
}
