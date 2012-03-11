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

import java.util.Collections;
import java.util.concurrent.Callable;
import org.junit.Test;

public class DefaultScopeTest {

    private static final String VARIABLE_NAME = "firstName";
    private static final String VARIABLE_VALUE = "Joe";

    @Test
    public void should_return_this_object() throws Exception {
        Object data = VARIABLE_VALUE;
        DefaultScope scope = new DefaultScope(data);
        assertEquals(VARIABLE_VALUE, scope.getVariableValue("."));
    }

    @Test
    public void should_return_value_from_map() throws Exception {
        Object data = Collections.singletonMap(VARIABLE_NAME, VARIABLE_VALUE);
        DefaultScope scope = new DefaultScope(data);
        assertEquals(VARIABLE_VALUE, scope.getVariableValue(VARIABLE_NAME));
    }

    @Test
    public void should_return_method_return_value() throws Exception {
        Object data = new Object() {
            @SuppressWarnings("unused")
            public String firstName() {
                return VARIABLE_VALUE;
            }
        };
        DefaultScope scope = new DefaultScope(data);
        assertEquals(VARIABLE_VALUE, scope.getVariableValue(VARIABLE_NAME));
    }

    @Test
    public void should_return_property_value() throws Exception {
        Object data = new Object() {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return VARIABLE_VALUE;
            }
        };
        DefaultScope scope = new DefaultScope(data);
        assertEquals(VARIABLE_VALUE, scope.getVariableValue(VARIABLE_NAME));
    }

    @Test
    public void should_return_field_value() throws Exception {
        Object data = new Object() {
            @SuppressWarnings("unused")
            public String firstName = VARIABLE_VALUE;
        };
        DefaultScope scope = new DefaultScope(data);
        assertEquals(VARIABLE_VALUE, scope.getVariableValue(VARIABLE_NAME));
    }

    @Test
    public void should_return_Callable_return_value() throws Exception {
        Object data = new Object() {
            @SuppressWarnings("unused")
            public Callable<String> firstName = new Callable<String>() {
                public String call() throws Exception {
                    return VARIABLE_VALUE;
                }
            };
        };
        DefaultScope scope = new DefaultScope(data);
        assertEquals(VARIABLE_VALUE, scope.getVariableValue(VARIABLE_NAME));
    }
}
