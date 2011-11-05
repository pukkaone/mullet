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
package com.github.pukkaone.mullet.spring;

import com.github.pukkaone.mullet.TemplateException;
import com.github.pukkaone.mullet.html.Template;
import com.github.pukkaone.mullet.html.TemplateLoader;
import com.github.pukkaone.mullet.html.parser.SimpleParser;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.xml.sax.InputSource;

/**
 * View that renders output from a template.
 */
public class TemplateView extends AbstractTemplateView {

    private TemplateLoader templateLoader;
    private ResourceBundle messages;
    private Template layout;

    public void setTemplateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    public void setLayout(Template layout) {
        this.layout = layout;
    }

    @Override
    public boolean checkResource(Locale locale) throws Exception {
        try {
            // Check that we can get the template, even if we might subsequently
            // get it again.
            templateLoader.load(getUrl());
        } catch (FileNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Template not found for URL: " + getUrl());
            }
            return false;
        }
        return true;
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

    @Override
    protected void renderMergedTemplateModel(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response)
        throws Exception
    {
        Template template = templateLoader.load(getUrl());
        if (layout == null) {
            // When there's no layout, render the page directly to the response.
            template.render(model, messages, response.getWriter());
            return;
        }

        // Render template then extract data from rendered page.
        StringWriter pageHtml = new StringWriter();
        template.render(model, messages, pageHtml);

        Page page = parsePage(pageHtml.toString());
        page.baseURL = request.getContextPath() + "/";
        page.requestContextPath = request.getContextPath();

        // Render page data in a layout.
        layout.render(page, messages, response.getWriter());
    }
}