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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Resolves view name to a view that renders a page from a template then passes
 * the page to a layout. The default layout is a template named {@code layout},
 * but this can be configured by the layout property. The following variables
 * are available to the layout:
 * <dl>
 * <dt>baseURL
 * <dd>request context path with '/' appended to the end
 * <dt>title
 * <dd>content of the {@code title} element from the page
 * <dt>body
 * <dd>content of the {@code body} element from the page
 * </dl>
 * The {@code body} variable typically contains HTML markup, so the layout must
 * use the {@code v:escape-xml="false"} command to prevent markup characters
 * being escaped when rendering the variable.
 * <p>
 * The simplest way to use this class is to set the {@code templateLoaderPath}
 * and {@code suffix} properties:
 * <pre>
 * &lt;bean class="com.github.pukkaone.mullet.spring.view.TemplateLayoutViewResolver"&gt;
 *   &lt;property name="templateLoaderPath" value="/views"/&gt;
 *   &lt;property name="suffix" value=".html"/&gt;
 * &lt;/bean&gt;
 * </pre>
 */
public class TemplateLayoutViewResolver extends AbstractTemplateViewResolver
    implements MessageSourceAware
{
    private static final String DEFAULT_LAYOUT = "layout";
    
    private TemplateLoader templateLoader;
    private ResourceBundle messages;
    private String layoutName = DEFAULT_LAYOUT;
    
    public TemplateLayoutViewResolver() {
        setViewClass(requiredViewClass());
        
        // TODO: Maybe the content type should be derived from the template file
        // encoding.
        setContentType("text/html; charset=UTF-8");
    }
    
    public void setTemplateLoaderPath(String templatePath) {
        templateLoader = new TemplateLoader(templatePath);
    }

    public void setMessageSource(MessageSource messageSource) {
        messages = new MessageSourceResourceBundle(
                messageSource, LocaleContextHolder.getLocale());
    }
    
    public void setLayout(String layoutName) {
        this.layoutName = layoutName;
    }
    
    /**
     * Sets the view type of this resolver to {@link TemplateView}.
     */
    @Override
    protected Class<?> requiredViewClass() {
       return TemplateView.class;
    }
    
    private Template getLayout() {
        Template layout;
        try {
            String url = getPrefix() + layoutName + getSuffix();
            layout = templateLoader.load(url);
        } catch (FileNotFoundException e) {
            layout = null;
        } catch (IOException e) {
            throw new TemplateException("load", e);
        }
        return layout;
    }
    
    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        TemplateView view = (TemplateView) super.buildView(viewName);
        view.setTemplateLoader(templateLoader);
        view.setMessages(messages);
        view.setLayout(getLayout());
        return view;
    }
}
