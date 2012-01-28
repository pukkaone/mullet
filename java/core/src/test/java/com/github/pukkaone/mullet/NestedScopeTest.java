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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class NestedScopeTest {

    private static final String A_NAME = "a";
    private static final String A_VALUE_PARENT = "a-parent";
    private static final String A_VALUE_CHILD = "a-child";

    private static final String B_NAME = "b";
    private static final String B_VALUE_PARENT = "b-parent";

    private DefaultNestedScope model;

    @Before
    @SuppressWarnings("unused")
    public void setUp() {
        Object parent = new Object() {
            String a = A_VALUE_PARENT;
            String b = B_VALUE_PARENT;
        };

        Object child = new Object() {
            String a = A_VALUE_CHILD;
        };

        model = new DefaultNestedScope(parent, child);
    }

    @Test
    public void should_find_in_child() throws Exception {
        assertEquals(A_VALUE_CHILD, model.getVariableValue(A_NAME));
    }

    @Test
    public void should_find_in_parent() throws Exception {
        assertEquals(B_VALUE_PARENT, model.getVariableValue(B_NAME));
    }

    @Test
    public void should_return_not_found() throws Exception {
        assertEquals(Scope.NOT_FOUND, model.getVariableValue("c"));
    }
}
