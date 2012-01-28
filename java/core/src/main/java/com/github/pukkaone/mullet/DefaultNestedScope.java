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

import java.util.LinkedList;

/**
 * Composite scope that combines scopes in nested scopes. Tries each scope in
 * sequence until a value is successfully resolved.
 */
public class DefaultNestedScope implements NestedScope {

    // nested scopes in inner to outer order
    private LinkedList<Scope> scopes = new LinkedList<Scope>();

    /**
     * Constructor
     *
     * @param dataObjects
     *            scopes in outer to inner order
     */
    public DefaultNestedScope(Object... dataObjects) {
        for (Object data : dataObjects) {
            pushScope(data);
        }
    }

    public Object getVariableValue(String key) {
        for (Scope scope : scopes) {
            Object value = scope.getVariableValue(key);
            if (value != NOT_FOUND) {
                return value;
            }
        }
        return NOT_FOUND;
    }

    public void pushScope(Object data) {
        scopes.addFirst(
                (data instanceof Scope) ? (Scope) data : new DefaultScope(data));
    }

    public void popScope() {
        scopes.removeFirst();
    }
}
