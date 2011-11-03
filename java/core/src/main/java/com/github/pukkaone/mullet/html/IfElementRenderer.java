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
import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * Renders an element if conditon is true.
 */
class IfElementRenderer extends CommandElementRenderer {

    private String key;

    IfElementRenderer(Element element, String key) {
        super(element);
        this.key = key.intern();
    }

    /**
     * Decides if the element should be rendered. A subclass may override this
     * method to change the decision.
     *
     * @param renderContext
     *            render context
     * @return {@code true} if element should be rendered.
     */
    protected boolean shouldRenderElement(RenderContext renderContext) {
        Object value = renderContext.getVariableValue(key);
        if (value == Model.NOT_FOUND || value == null) {
            return false;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Iterable<?>) {
            value = ((Iterable<?>) value).iterator();
        }
        if (value instanceof Iterator<?>) {
            Iterator<?> iter = (Iterator<?>) value;
            return iter.hasNext();
        }

        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0;
        }

        return true;
    }

    @Override
    public void render(RenderContext renderContext) {
        if (shouldRenderElement(renderContext)) {
            super.render(renderContext);
        }
    }
}
