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

import com.github.pukkaone.mullet.DefaultNestedScope;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.dom.Dom2Sax;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;
import nu.validator.htmlparser.sax.HtmlSerializer;
import org.junit.Before;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Template tests extend this class for implementation reuse.
 */
public abstract class TemplateTests {

    protected static class Post {
        private int id;
        private String subject;
        private Date date;
        private String selected;

        public Post(int id, String subject, String date, String selected) {
            this.id = id;
            this.subject = subject;
            this.selected = selected;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                this.date = dateFormat.parse(date);
            } catch (ParseException e) {
                throw new IllegalArgumentException("parse", e);
            }
        }

        public int getId() {
            return id;
        }

        public String getSubject() {
            return subject;
        }

        public Date getDate() {
            return date;
        }

        public String getSelected() {
            return selected;
        }
    }

    protected static final Post[] POSTS = new Post[] {
        new Post(1, "subject 1", "2011-12-01", null),
        new Post(2, "subject 2", "2011-12-02", "selected"),
    };

    protected TemplateLoader loader;
    protected Object data;
    protected HashMap<String, Object> modelMap;
    protected StringWriter writer;

    @Before
    @SuppressWarnings("unused")
    public void setUp() {
        loader = new TemplateLoader(TemplateTests.class);
        data = new Object() {
            Post[] posts = POSTS;
            Post post = POSTS[0];
        };
        writer = new StringWriter();
    }

    protected void setVariable(String variableName, Object value) {
        if (modelMap == null) {
            modelMap = new HashMap<String, Object>();
            data = new DefaultNestedScope(modelMap);
        }

        modelMap.put(variableName, value);
    }

    protected static String stripNewlines(String input) {
        return input.replaceAll("\\n\\s*", "");
    }

    protected String elementToString(String tagName)
        throws IOException, SAXException
    {
        String inputHtml = writer.toString();
        HtmlDocumentBuilder parser = new HtmlDocumentBuilder(
                XmlViolationPolicy.ALLOW);
        StringReader reader = new StringReader(inputHtml);
        Document document = parser.parse(new InputSource(reader));

        StringWriter output = new StringWriter();
        HtmlSerializer serializer = new HtmlSerializer(output);
        Dom2Sax dom2sax = new Dom2Sax(serializer, serializer);
        dom2sax.parse(document.getElementsByTagName(tagName).item(0));
        return stripNewlines(output.toString());
    }

    protected String body() throws IOException, SAXException {
        return elementToString("body");
    }
}
