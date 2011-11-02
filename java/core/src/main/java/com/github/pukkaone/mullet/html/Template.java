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

import com.github.pukkaone.mullet.Container;
import com.github.pukkaone.mullet.FailedValueStrategy;
import com.github.pukkaone.mullet.RenderContext;
import java.io.Writer;
import java.util.ResourceBundle;

/**
 * Template containing static text and dynamically generated content.
 */
public class Template extends Container {

    private ResourceBundle messages;

    private FailedValueStrategy missingValueStrategy =
            FailedValueStrategy.RETURN_EMPTY_STRING;
    private FailedValueStrategy nullValueStrategy =
            FailedValueStrategy.RETURN_EMPTY_STRING;

    /**
     * Sets resource bundle to resolve messages from.
     *
     * @param messages
     *            resource bundle
     * @return template
     */
    public Template setMessages(ResourceBundle messages) {
        this.messages = messages;
        return this;
    }

    /**
     * Sets action to perform when a variable name is not found.
     *
     * @param strategy
     *            action to perform
     * @return template
     */
    public Template onMissing(FailedValueStrategy strategy) {
        missingValueStrategy = strategy;
        return this;
    }

    /**
     * Sets action to perform when a value is null.
     *
     * @param strategy
     *            action to perform
     * @return template
     */
    public Template onNull(FailedValueStrategy strategy) {
        nullValueStrategy = strategy;
        return this;
    }

    void render(RenderContext renderContext) {
        renderChildren(renderContext);
    }

    /**
     * Renders the template.
     *
     * @param data
     *            provides data to render
     * @param messages
     *            resource bundle to resolve messages from
     * @param writer
     *            where rendered output will be written
     */
    public void render(
            Object data, ResourceBundle messages, Writer writer)
    {
        if (messages == null) {
            messages = this.messages;
        }

        RenderContext renderContext = new RenderContext(
                data,
                messages,
                missingValueStrategy,
                nullValueStrategy,
                writer);
        render(renderContext);
    }

    /**
     * Renders the template. Unless configured by calling the
     * {@link #setMessages(ResourceBundle)} method, the template resolves
     * messages from a resource bundle named by removing the extension from the
     * template file name. For example, if a template file name is {@code
     * index.html}, the default resource bundle name is {@code index}. You would
     * typically put a {@code index.properties} file in the same directory as
     * the {@code index.html} file so the JDK {@link ResourceBundle}
     * implementation can find it.
     *
     * @param data
     *            provides data to render
     * @param writer
     *            where rendered output will be written
     */
    public void render(Object data, Writer writer) {
        render(data, null, writer);
    }
}
