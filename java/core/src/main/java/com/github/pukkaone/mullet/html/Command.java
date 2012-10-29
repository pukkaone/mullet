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

/**
 * Recognized template commands
 */
interface Command {
    static final String DATA_PREFIX = "data-";
    static final String NAMESPACE_PREFIX = "mullet:";
    static final String NAMESPACE_URI = "http://pukkaone.github.com/mullet/1";

    static final String ACTION = "action";
    static final String ALT = "alt";
    static final String ALT_MESSAGE = "alt-message";
    static final String ATTR = "attr";
    static final String ATTR_MESSAGE = "attr-message";
    static final String CHECKED = "checked";
    static final String DISABLED = "disabled";
    static final String ESCAPE_XML = "escape-xml";
    static final String FOR = "for";
    static final String HREF = "href";
    static final String IF = "if";
    static final String INCLUDE = "include";
    static final String READONLY = "readonly";
    static final String REMOVE = "remove";
    static final String SELECTED = "selected";
    static final String SRC = "src";
    static final String TEXT = "text";
    static final String TEXT_MESSAGE = "text-message";
    static final String TITLE = "title";
    static final String TITLE_MESSAGE = "title-message";
    static final String UNLESS = "unless";
    static final String VALUE = "value";
    static final String VALUE_MESSAGE = "value-message";
}
