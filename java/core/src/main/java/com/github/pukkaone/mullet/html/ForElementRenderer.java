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

import com.github.pukkaone.mullet.Scope;
import com.github.pukkaone.mullet.RenderContext;
import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * Renders an element for each item in a collection.
 */
class ForElementRenderer extends CommandElementRenderer {

    private String variableName;

    ForElementRenderer(Element element, String variableName) {
        super(element);
        this.variableName = variableName.intern();
    }

    private void renderNestedModel(
            Object data, RenderContext renderContext)
    {
        renderContext.pushScope(data);
        super.render(renderContext);
        renderContext.popScope();
    }

    @Override
    public void render(RenderContext renderContext) {
        Object value = renderContext.getVariableValue(variableName);
        if (value == Scope.NOT_FOUND || value == null) {
            return;
        }

        if ((value instanceof Boolean) && !(Boolean) value) {
            return;
        }

        if (value instanceof Iterable<?>) {
            value = ((Iterable<?>) value).iterator();
        }
        if (value instanceof Iterator<?>) {
            for (Iterator<?> iter = (Iterator<?>) value; iter.hasNext(); ) {
                Object item = iter.next();
                renderNestedModel(item, renderContext);
            }
            return;
        }

        if (value.getClass().isArray()) {
            for (int i = 0, len = Array.getLength(value); i < len; ++i) {
                Object item = Array.get(value, i);
                renderNestedModel(item, renderContext);
            }
            return;
        }

        renderNestedModel(value, renderContext);
    }
}
