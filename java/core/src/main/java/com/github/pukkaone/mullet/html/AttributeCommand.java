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

import com.github.pukkaone.mullet.Model;
import com.github.pukkaone.mullet.RenderContext;

/**
 * Operation to set attribute value.
 */
abstract class AttributeCommand {

    protected String attributeName;

    protected AttributeCommand(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * Gets value to render in attribute value.
     *
     * @param renderContext
     *            render content
     * @return value
     */
    protected abstract Object getValue(RenderContext renderContext);

    void execute(RenderContext renderContext, Attributes attributes) {
        Object value = getValue(renderContext);
        if (value == Model.NOT_FOUND || value == null) {
            // Value not found.  Do not render the attribute.
            attributes.remove(attributeName);
        } else {
            String sValue = renderContext.escapeXml(value.toString());
            attributes.put(attributeName, sValue);
        }
    }
}
