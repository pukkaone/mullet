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

import com.github.pukkaone.mullet.EscapeXml;
import com.github.pukkaone.mullet.html.parser.SimpleParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Handles SAX events to extract data from rendered page.
 */
class PageBuilder extends DefaultHandler2 {
    private static final String HEAD = "head";
    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final char[] START_CDATA = "<![CDATA[".toCharArray();
    private static final char[] END_CDATA = "]]>".toCharArray();
    
    // number of open head elements
    private int headCount;
    
    // Count of nested open elements where this handler is extracting their
    // inner HTML.
    private int innerDepth;
    private StringBuilder innerHtml;
    private Page page = new Page();

    private boolean isExtractingInnerHtml() {
        return innerDepth > 0;
    }
    
    private void startExtractingInnerHtml() {
        innerDepth = 1;
        innerHtml = new StringBuilder();
    }
    
    private void append(char[] data, int start, int length) {
        innerHtml.append(data, start, length);
    }
    
    private PageBuilder append(char data) {
        innerHtml.append(data);
        return this;
    }
    
    private PageBuilder append(String data) {
        innerHtml.append(data);
        return this;
    }
    
    private String getInnerHtml() {
        return innerHtml.toString();
    }
    
    private void renderStartTag(String qualifiedName, Attributes attributes) {
        append('<').append(qualifiedName);

        for (int i = 0; i < attributes.getLength(); ++i) {
            append(' ').append(attributes.getQName(i)).append('=')
                    .append('"')
                    .append(EscapeXml.escape(attributes.getValue(i)))
                    .append('"');
        }
        
        append('>');
    }
    
    @Override
    public void startElement(
            String namespaceUri,
            String localName,
            String qualifiedName,
            Attributes attributes)
        throws SAXException
    {
        if (HEAD.equals(qualifiedName)) {
            ++headCount;
        }
        
        if (isExtractingInnerHtml()) {
            ++innerDepth;
            renderStartTag(qualifiedName, attributes);
            return;
        }
        
        if (headCount > 0 && TITLE.equals(qualifiedName)) {
            startExtractingInnerHtml();
            return;
        }
        
        if (BODY.equals(qualifiedName)) {
            startExtractingInnerHtml();
        }
    }
    
    private void renderEndTag(String qualifiedName) {
        append("</").append(qualifiedName).append('>');
    }
    
    @Override
    public void endElement(
            String namespaceUri, String localName, String qualifiedName)
        throws SAXException
    {
        if (HEAD.equals(qualifiedName)) {
            --headCount;
        }
        
        if (isExtractingInnerHtml()) {
            --innerDepth;
            if (isExtractingInnerHtml()
             && !SimpleParser.IMPLICIT_END_TAG_NS_URI.equals(namespaceUri)) {
                renderEndTag(qualifiedName);
            }
        }

        if (headCount > 0 && TITLE.equals(qualifiedName)) {
            page.title = getInnerHtml();
            return;
        }
        
        if (BODY.equals(qualifiedName)) {
            page.body = getInnerHtml();
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        if (isExtractingInnerHtml()) {
            append(ch, start, length);
        }
    }
    
    @Override
    public void startCDATA() throws SAXException {
        characters(START_CDATA, 0, START_CDATA.length);
    }
    
    @Override
    public void endCDATA() throws SAXException {
        characters(END_CDATA, 0, END_CDATA.length);
    }
    
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException
    {
        characters(ch, start, length);
    }
    
    @Override
    public void comment(char[] ch, int start, int length)
        throws SAXException
    {
        if (isExtractingInnerHtml()) {
            append("<!--");
            characters(ch, start, length);
            append("-->");
        }
    }
    
    @Override
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        if (isExtractingInnerHtml()) {
            append("<?");
            append(target);
            append(" ");
            append(data);
            append("?>");
        }
    }
    
    Page getPage() {
        return page;
    }
}
