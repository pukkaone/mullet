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

import com.github.pukkaone.mullet.TemplateException;
import com.github.pukkaone.mullet.html.parser.SimpleParser;
import java.io.StringReader;
import java.io.Writer;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import org.xml.sax.InputSource;

/**
 * Extracts content from an HTML page and renders it in a layout.  The layout
 * is a template given these variables:
 * <dl>
 * <dt>contextPathURL
 * <dd>request context path with '/' appended to the end
 * <dt>title
 * <dd>content of the {@code title} element from the page
 * <dt>body
 * <dd>content of the {@code body} element from the page
 * </dl>
 * The {@code body} variable typically contains HTML markup, so the layout must
 * use the {@code data-escape-xml="false"} command to prevent markup characters
 * being escaped when rendering the variable.
 */
public class Layout {

    private Template template;

    public Layout(Template template) {
        this.template = template;
    }

    private Page parsePage(String pageHtml) {
        StringReader reader = new StringReader(pageHtml);
        PageBuilder handler = new PageBuilder();
        try {
            SimpleParser parser = new SimpleParser();
            parser.setContentHandler(handler);
            parser.setProperty(SimpleParser.LEXICAL_HANDLER, handler);
            parser.parse(new InputSource(reader));
        } catch (Exception e) {
            throw new TemplateException("parse", e);
        }
        return handler.getPage();
    }

    /**
     * Renders page data in a layout.
     *
     * @param pageHtml
     *            content from this HTML page will be rendered in the layout
     * @param messages
     *            resource bundle to resolve messages from
     * @param writer
     *            where rendered output will be written
     */
    public void execute(
            String pageHtml,
            ResourceBundle messages,
            Writer writer)
    {
        Page page = parsePage(pageHtml);
        template.execute(page, messages, writer);
    }

    /**
     * Renders page data in a layout.
     *
     * @param request
     *            provides data to render
     * @param pageHtml
     *            content from this HTML page will be rendered in the layout
     * @param messages
     *            resource bundle to resolve messages from
     * @param writer
     *            where rendered output will be written
     */
    public void execute(
            HttpServletRequest request,
            String pageHtml,
            ResourceBundle messages,
            Writer writer)
    {
        Page page = parsePage(pageHtml);
        page.contextPathURL = request.getContextPath() + "/";
        page.requestContextPath = request.getContextPath();

        template.execute(page, messages, writer);
    }
}
