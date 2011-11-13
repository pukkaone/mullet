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
package com.github.pukkaone.mullet.html.parser;

import com.github.pukkaone.mullet.nu.validator.htmlparser.common.TokenHandler;
import com.github.pukkaone.mullet.nu.validator.htmlparser.impl.ElementName;
import com.github.pukkaone.mullet.nu.validator.htmlparser.impl.HtmlAttributes;
import com.github.pukkaone.mullet.nu.validator.htmlparser.impl.Tokenizer;
import com.github.pukkaone.mullet.nu.validator.htmlparser.impl.UTF16Buffer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * SAX parser that reports elements in the order they appear in the input.  Does
 * not move elements to another parent.
 */
public class SimpleParser implements TokenHandler, XMLReader {

    /** identifier for accessing lexical handler property of SAX parser */
    public static final String LEXICAL_HANDLER =
            "http://xml.org/sax/properties/lexical-handler";

    /** special namespace URI indicating parser generated an implicit end tag */
    public static final String IMPLICIT_END_TAG_NS_URI =
            "http://pukkaone.github.com/mullet/end-tag";

    private ContentHandler contentHandler;
    private LexicalHandler lexicalHandler;
    private Reader reader;
    private Tokenizer tokenizer;
    private OpenElement openElement;

    public void startTokenization(Tokenizer arg0) throws SAXException {
        openElement = null;
        contentHandler.startDocument();
    }

    public void endTokenization() throws SAXException {
        contentHandler.endDocument();
    }

    public void doctype(
            String name, String publicId, String systemId, boolean forcequirks)
        throws SAXException
    {
        if (lexicalHandler != null) {
            lexicalHandler.startDTD(name, publicId, systemId);
        }
    }

    public void startTag(
            ElementName elementName,
            HtmlAttributes attributes,
            boolean selfClosing)
        throws SAXException
    {
        // Push open element onto stack.
        OpenElement element = new OpenElement(
                openElement, elementName.name, attributes);
        openElement = element;

        contentHandler.startElement(
                element.uri,
                element.localName,
                element.qualifiedName,
                element.attributes);
    }

    private OpenElement popOpenElement() {
        OpenElement element = openElement;
        if (openElement != null) {
            openElement = openElement.parent;
        }
        return element;
    }

    private void throwMismatchedEndTag(String tagName)
        throws SAXParseException
    {
        throw new SAXParseException(
                "End tag </" + tagName + "> has no matching start tag", null);
    }

    public void endTag(ElementName elementName) throws SAXException {
        OpenElement element = popOpenElement();
        if (element == null) {
            throwMismatchedEndTag(elementName.name);
        }

        // Generate implicit end tags up to this element.
        while (!element.qualifiedName.equals(elementName.name)) {
            // KLUDGE: Indicate a generated implicit end tag by passing a
            // special value for the namespace URI.
            contentHandler.endElement(
                    SimpleParser.IMPLICIT_END_TAG_NS_URI,
                    element.localName,
                    element.qualifiedName);

            element = popOpenElement();
            if (element == null) {
                throwMismatchedEndTag(elementName.name);
            }
        }

        contentHandler.endElement(
                element.uri, element.localName, element.qualifiedName);
    }

    public void characters(char[] data, int start, int length)
        throws SAXException
    {
        contentHandler.characters(data, start, length);
    }

    public boolean cdataSectionAllowed() throws SAXException {
        return true;
    }

    public void startCDATA() throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
        }
    }

    public void endCDATA() throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endCDATA();
        }
    }

    public boolean wantsComments() throws SAXException {
        return true;
    }

    private void processingInstruction(char[] data, int start, int length)
        throws SAXException
    {
        String instruction = new String(data, start + 1, length - 1);
        if (instruction.endsWith("?")) {
            instruction = instruction.substring(0, instruction.length() - 1);
        }

        String[] parts = instruction.split("\\s", 2);
        contentHandler.processingInstruction(parts[0], parts[1]);
    }

    public void comment(char[] data, int start, int length)
        throws SAXException
    {
        // FIXME: The HTML5 tokenizer treats <? as a bogus comment and emits it
        // as a comment.  If the comment begins with ? then turn it into a
        // processing instruction.  The problem is any legitimate comment
        // beginning with ? will also be turned into a processing instruction.
        if (length > 0 && data[start] == '?') {
            processingInstruction(data, start, length);
        } else if (lexicalHandler != null) {
            lexicalHandler.comment(data, start, length);
        }
    }

    public void eof() throws SAXException {
    }

    public void zeroOriginatingReplacementCharacter() throws SAXException {
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public DTDHandler getDTDHandler() {
        return null;
    }

    public void setDTDHandler(DTDHandler handler) {
    }

    public EntityResolver getEntityResolver() {
        return null;
    }

    public void setEntityResolver(EntityResolver resolver) {
    }

    public ErrorHandler getErrorHandler() {
        return null;
    }

    public void setErrorHandler(ErrorHandler handler) {
    }

    public boolean getFeature(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        return false;
    }

    public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
    }

    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        return null;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }

    public void setProperty(String name, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException
    {
        if (LEXICAL_HANDLER.equals(name)) {
            setLexicalHandler((LexicalHandler) value);
        }
    }

    private void runStates() throws SAXException, IOException {
        char[] buffer = new char[2048];
        UTF16Buffer bufr = new UTF16Buffer(buffer, 0, 0);
        boolean lastWasCR = false;
        int len = -1;
        if ((len = reader.read(buffer)) != -1) {
            assert len > 0;
            int streamOffset = 0;
            int offset = 0;
            int length = len;
            if (length > 0) {
                tokenizer.setTransitionBaseOffset(streamOffset);
                bufr.setStart(offset);
                bufr.setEnd(offset + length);
                while (bufr.hasMore()) {
                    bufr.adjust(lastWasCR);
                    lastWasCR = false;
                    if (bufr.hasMore()) {
                        lastWasCR = tokenizer.tokenizeBuffer(bufr);
                    }
                }
            }
            streamOffset = length;
            while ((len = reader.read(buffer)) != -1) {
                assert len > 0;
                tokenizer.setTransitionBaseOffset(streamOffset);
                bufr.setStart(0);
                bufr.setEnd(len);
                while (bufr.hasMore()) {
                    bufr.adjust(lastWasCR);
                    lastWasCR = false;
                    if (bufr.hasMore()) {
                        lastWasCR = tokenizer.tokenizeBuffer(bufr);
                    }
                }
                streamOffset += len;
            }
        }
        tokenizer.eof();
    }

    public void parse(InputSource is) throws IOException, SAXException {
        tokenizer = new Tokenizer(this);
        tokenizer.start();
        tokenizer.initLocation(is.getPublicId(), is.getSystemId());

        reader = is.getCharacterStream();
        if (reader == null) {
            reader = new InputStreamReader(is.getByteStream(), "UTF-8");
        }
        runStates();

        tokenizer.end();
    }

    public void parse(String systemId) throws IOException, SAXException {
        parse(new InputSource(systemId));
    }
}
