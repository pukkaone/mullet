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

import com.github.pukkaone.mullet.DefaultNestedScope;
import java.util.concurrent.Callable;
import org.junit.Test;

/**
 * Tests text command.
 */
public class TextTest extends TemplateTests {

    @Test
    public void should_set_text() throws Exception {
        setVariable("greeting", "Hello");
        
        Template template = loader.load("text.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p>Hello</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }
    
    @Test
    public void should_set_text_to_callable_value() throws Exception {
        data = new DefaultNestedScope(new Object() {
            @SuppressWarnings("unused")
            Callable<String> greeting = new Callable<String>() {
                public String call() throws Exception {
                    return "Hello";
                }
            };
        });
        
        Template template = loader.load("text.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p>Hello</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }
    
    @Test
    public void message_command_should_format_message_arguments() 
        throws Exception
    {
        Template template = loader.load("text-message.html");
        template.execute(data, writer);

        final String EXPECTED_OUTPUT =
                "<body>" +
                  "<p>Subject subject 1 Date 2011-12-01</p>" +
                "</body>";
        assertEquals(EXPECTED_OUTPUT, body());
    }    
}
