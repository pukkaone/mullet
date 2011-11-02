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

import java.util.Collections;
import org.junit.Test;

/**
 * Tests if command.
 */
public class IfTest extends TemplateTests {

    private static final String FALSE_OUTPUT =
            "<body>" +
            "</body>";

    private static final String TRUE_OUTPUT =
            "<body>" +
              "<p>Hello</p>" +
            "</body>";

    @Test
    public void missing_value_should_not_render_element() throws Exception {
        Template template = loader.load("if.html");
        template.render(data, writer);

        assertEquals(FALSE_OUTPUT, body());
    }
    
    @Test
    public void null_value_should_not_render_element() throws Exception {
        setModelValue("condition", null);
        
        Template template = loader.load("if.html");
        template.render(data, writer);

        assertEquals(FALSE_OUTPUT, body());
    }
    
    @Test
    public void false_should_not_render_element() throws Exception {
        setModelValue("condition", false);
        
        Template template = loader.load("if.html");
        template.render(data, writer);

        assertEquals(FALSE_OUTPUT, body());
    }
    
    @Test
    public void empty_array_should_not_render_element() throws Exception {
        setModelValue("condition", new int[0]);
        
        Template template = loader.load("if.html");
        template.render(data, writer);

        assertEquals(FALSE_OUTPUT, body());
    }
    
    @Test
    public void empty_list_should_not_render_element() throws Exception {
        setModelValue("condition", Collections.EMPTY_LIST);
        
        Template template = loader.load("if.html");
        template.render(data, writer);

        assertEquals(FALSE_OUTPUT, body());
    }
    
    @Test
    public void true_should_render_element() throws Exception {
        setModelValue("condition", true);
        
        Template template = loader.load("if.html");
        template.render(data, writer);

        assertEquals(TRUE_OUTPUT, body());
    }
    
    @Test
    public void nonempty_array_should_render_element() throws Exception {
        setModelValue("condition", new int[1]);
        
        Template template = loader.load("if.html");
        template.render(data, writer);

        assertEquals(TRUE_OUTPUT, body());
    }
    
    @Test
    public void nonempty_list_should_render_element() throws Exception
    {
        setModelValue("condition", Collections.singletonList(1));
        
        Template template = loader.load("if.html");
        template.render(data, writer);

        assertEquals(TRUE_OUTPUT, body());
    }
    
    @Test
    public void object_should_render_element() throws Exception {
        setModelValue("condition", POSTS[0]);
        
        Template template = loader.load("if.html");
        template.render(data, writer);

        assertEquals(TRUE_OUTPUT, body());
    }    
}
