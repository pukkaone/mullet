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

import com.github.pukkaone.mullet.RenderContext;
import com.github.pukkaone.mullet.TemplateException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Message {
    
    private static final String ARGUMENT_SEPARATOR = ",";
    private static final String MESSAGE_SYNTAX_ERROR =
            "message syntax is incorrect: ";

    private String messageKey;
    private ArrayList<String> argumentKeys;
    
    Message(String messageArguments) {
        StringTokenizer iArgument = new StringTokenizer(
                messageArguments, ARGUMENT_SEPARATOR);

        if (!iArgument.hasMoreTokens()) {
            throw new TemplateException(
                    MESSAGE_SYNTAX_ERROR + messageArguments);
        }
        messageKey = iArgument.nextToken().trim().intern();
        
        argumentKeys = new ArrayList<String>();
        while (iArgument.hasMoreTokens()) {
            String argumentKey = iArgument.nextToken().trim();
            argumentKeys.add(argumentKey);
        }
    }
        
    String format(RenderContext renderContext) {
        String message = renderContext.getMessage(messageKey);
        if (argumentKeys.isEmpty()) {
            return message;
        }
        
        Object[] arguments = new Object[argumentKeys.size()];
        int i = 0;
        for (String argumentKey : argumentKeys) {
            arguments[i++] = renderContext.getDisplayValue(argumentKey);
        }
        return MessageFormat.format(message, arguments);
    }
}
