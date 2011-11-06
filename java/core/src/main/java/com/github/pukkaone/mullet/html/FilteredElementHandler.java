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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * This is a proxy for a SAX event handler that forwards events only while the
 * parser is within an element identified by an {@code id} attribute.
 */
class FilteredElementHandler extends DefaultHandler2 {

    private static final String ID = "id";

    private DefaultHandler2 handler;
    private String id;
    private int depth;

    FilteredElementHandler(DefaultHandler2 handler, String id) {
        this.handler = handler;
        this.id = id;
    }

    private boolean shouldForward() {
        return depth > 0;
    }

    @Override
    public void startDocument() throws SAXException {
        handler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        handler.endDocument();
    }

    @Override
    public void startElement(
            String namespaceUri,
            String localName,
            String qualifiedName,
            Attributes attributes)
        throws SAXException
    {
        if (shouldForward()) {
            ++depth;
            handler.startElement(
                    namespaceUri, localName, qualifiedName, attributes);
            return;
        }

        String value = attributes.getValue(ID);
        if (id.equals(value)) {
            // Enable event forwarding.
            depth = 1;
        }
    }

    @Override
    public void endElement(
            String namespaceUri, String localName, String qualifiedName)
        throws SAXException
    {
        if (shouldForward()) {
            if (--depth > 0) {
                handler.endElement(namespaceUri, localName, qualifiedName);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        if (shouldForward()) {
            handler.characters(ch, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException
    {
        if (shouldForward()) {
            handler.ignorableWhitespace(ch, start, length);
        }
    }

    @Override
    public void startCDATA() throws SAXException {
        if (shouldForward()) {
            handler.startCDATA();
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        if (shouldForward()) {
            handler.endCDATA();
        }
    }

    @Override
    public void comment(char[] ch, int start, int length)
        throws SAXException
    {
        if (shouldForward()) {
            handler.comment(ch, start, length);
        }
    }

    @Override
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        if (shouldForward()) {
            handler.processingInstruction(target, data);
        }
    }
}
