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
package com.github.pukkaone.mullet;

import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

/**
 * Holds the rendering context to reduce the number of parameters passed to
 * render methods.
 */
public class RenderContext {

    private NestedModel model;
    private ResourceBundle messages;
    private FailedValueStrategy missingValueStrategy;
    private FailedValueStrategy nullValueStrategy;
    private boolean escapeXmlEnabled = true;
    private Writer writer;

    public RenderContext(
            Object data,
            ResourceBundle messages,
            FailedValueStrategy missingValueStrategy,
            FailedValueStrategy nullValueStrategy, 
            Writer writer)
    {
        this.model = (data instanceof NestedModel)
                ? (NestedModel) data
                : new DefaultNestedModel(data);
        this.messages = messages;
        this.missingValueStrategy = missingValueStrategy;
        this.nullValueStrategy = nullValueStrategy;
        this.writer = writer;
    }
    
    public boolean isEscapeXmlEnabled() {
        return escapeXmlEnabled;
    }

    public void setEscapeXmlEnabled(boolean escapeXmlEnabled) {
        this.escapeXmlEnabled = escapeXmlEnabled;
    }

    /**
     * Escapes characters that could be interpreted as XML markup if enabled.
     * 
     * @param input
     *            input string
     * @return escaped string, or the input string if escaping is disabled.
     */
    public String escapeXml(String input) {
        return escapeXmlEnabled ? EscapeXml.escape(input) : input;
    }
    
    /**
     * Resolves variable name to value.
     * 
     * @param key
     *            variable name
     * @return value
     */
    public Object getValue(String key) {
        return model.getValue(key);
    }

    /**
     * Adds a nested scope to search in subsequent lookups.
     * 
     * @param data
     *            data object
     */
    public void pushScope(Object data) {
        model.pushScope(data);
    }
    
    /**
     * Removes innermost nested scope.
     */
    public void popScope() {
        model.popScope();
    }
    
    /**
     * Gets model value that is intended for display in the rendered output.
     * Applies configured strategies for handling missing and null values.
     * 
     * @param modelKey
     *            variable name
     * @return value
     */
    public Object getDisplayValue(String modelKey) {
        Object value = model.getValue(modelKey);
        if (value == Model.NOT_FOUND) {
            value = missingValueStrategy.getValue(modelKey);
        }
        if (value == null) {
            value = nullValueStrategy.getValue(modelKey);
        }
        return value;
    }
    
    /**
     * Gets message pattern from resource bundle.
     * 
     * @param messageKey
     *            message key
     * @return message pattern
     */
    public String getMessage(String messageKey) {
        if (messages == null) {
            throw new TemplateException(
                    "Failed to get message for key '" + messageKey +
                    "' because there is no resource bundle");
        }
        return messages.getString(messageKey);
    }
    
    /**
     * Writes rendered output.
     * 
     * @param str
     *            string to write
     * @return this object
     */
    public RenderContext write(CharSequence str) {
        try {
            writer.append(str);
        } catch (IOException e) {
            throw new RuntimeException("append", e);
        }
        
        return this;
    }
}
