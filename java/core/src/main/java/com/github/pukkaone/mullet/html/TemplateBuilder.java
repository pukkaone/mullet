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

import com.github.pukkaone.mullet.Container;
import com.github.pukkaone.mullet.Renderer;
import com.github.pukkaone.mullet.TemplateException;
import com.github.pukkaone.mullet.html.parser.SimpleParser;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Handles SAX events to build a template.
 */
class TemplateBuilder extends DefaultHandler2 {
    private static final HashSet<String> COMMANDS =
            new HashSet<String>(Arrays.asList(
                    Command.ACTION,
                    Command.ALT,
                    Command.ALT_MESSAGE,
                    Command.ATTR,
                    Command.ATTR_MESSAGE,
                    Command.ESCAPE_XML,
                    Command.FOR,
                    Command.HREF,
                    Command.IF,
                    Command.INCLUDE,
                    Command.REMOVE,
                    Command.SRC,
                    Command.TEXT,
                    Command.TEXT_MESSAGE,
                    Command.TITLE,
                    Command.TITLE_MESSAGE,
                    Command.UNLESS,
                    Command.VALUE,
                    Command.VALUE_MESSAGE));
    private static final char[] START_CDATA = "<![CDATA[".toCharArray();
    private static final char[] END_CDATA = "]]>".toCharArray();

    private TemplateLoader loader;

    // This is a stack of elements where this handler has seen the start tag and
    // not yet seen the end tag.
    private LinkedList<Element> openElements = new LinkedList<Element>();

    // stack of current containers to add renderers to
    private LinkedList<Container> containers = new LinkedList<Container>();

    private StringBuilder staticText = new StringBuilder();
    private Template template;

    TemplateBuilder(TemplateLoader loader) {
        this.loader = loader;
    }

    private void addChild(Renderer renderer) {
        containers.getFirst().addChild(renderer);
    }

    private void removeChild(Renderer renderer) {
        containers.getFirst().removeChild(renderer);
    }

    private boolean findCommands(
            org.xml.sax.Attributes inputAttributes,
            Attributes ordinaryAttributes,
            Attributes commandAttributes)
    {
        boolean foundCommand = false;
        for (int i = 0; i < inputAttributes.getLength(); ++i) {
            String value = inputAttributes.getValue(i);

            if (Command.NAMESPACE_URI.equals(inputAttributes.getURI(i))
             || inputAttributes.getQName(i).startsWith(Command.PREFIX))
            {
                String commandName = inputAttributes.getLocalName(i);
                if (!COMMANDS.contains(commandName)) {
                    throw new TemplateException(
                            "invalid command '" + commandName + "'");
                }
                commandAttributes.put(commandName, value);
                foundCommand = true;
            } else {
                String attributeName = inputAttributes.getQName(i);
                if (attributeName.length() == 0) {
                    attributeName = inputAttributes.getLocalName(i);
                }
                ordinaryAttributes.put(attributeName, value);
            }
        }
        return foundCommand;
    }

    private void appendStaticText(char[] ch, int start, int length) {
        staticText.append(ch, start, length);
    }

    private void appendStaticText(String text) {
        staticText.append(text);
    }

    private void endStaticText() {
        if (staticText.length() > 0) {
            addChild(new StaticTextRenderer(staticText.toString()));
            staticText.delete(0, staticText.length());
        }
    }

    // Marks the most deeply nested open element as having content.
    private void setHasContent() {
        if (!openElements.isEmpty()) {
            openElements.getFirst().hasContent = true;
        }
    }

    @Override
    public InputSource resolveEntity(
            String name,
            String publicId,
            String baseURI,
            String systemId)
        throws SAXException, IOException
    {
        // Return empty input for all entities, otherwise the parser by default
        // tries to read the DTD from the Internet if there is a doctype
        // declaration with a system ID.
        return new InputSource(new StringReader(""));
    }

    @Override
    public void startDocument() throws SAXException {
        template = new Template();
        containers.addFirst(template);
    }

    @Override
    public void endDocument() throws SAXException {
        endStaticText();
    }

    @Override
    public void startDTD(String name, String publicId, String systemId)
         throws SAXException
    {
        StringBuilder doctype = new StringBuilder();
        doctype.append("<!DOCTYPE ").append(name);
        if (publicId != null) {
            doctype.append(" PUBLIC \"").append(publicId).append('"');
        }
        if (systemId != null) {
            doctype.append(" \"").append(systemId).append('"');
        }
        doctype.append(">");

        appendStaticText(doctype.toString());
    }

    private ElementRenderer createElementRenderer(
            Element element, Attributes commandAttributes)
    {
        ElementRenderer renderer = null;

        String value = commandAttributes.get(Command.IF);
        if (value != null) {
            renderer = new IfElementRenderer(element, value);
        }

        if (renderer == null) {
            value = commandAttributes.get(Command.UNLESS);
            if (value != null) {
                renderer = new UnlessElementRenderer(element, value);
            }
        }

        if (renderer == null) {
            value = commandAttributes.get(Command.FOR);
            if (value != null) {
                renderer = new ForElementRenderer(element, value);
            }
        }

        if (renderer == null) {
            renderer = new ElementRenderer(element);
        }

        if (!(renderer instanceof IncludeElementRenderer)) {
            // The include command replaces the entire element, so we don't have
            // handle other commands in the element.
            renderer.configureCommands(commandAttributes, loader);
        }
        return renderer;
    }

    @Override
    public void startElement(
            String namespaceUri,
            String localName,
            String qualifiedName,
            org.xml.sax.Attributes attributes)
        throws SAXException
    {
        setHasContent();

        Attributes ordinaryAttributes = new Attributes();
        Attributes commandAttributes = new Attributes();
        boolean foundCommand = findCommands(
                attributes, ordinaryAttributes, commandAttributes);

        Element element = new Element(
                localName, qualifiedName, ordinaryAttributes);
        openElements.addFirst(element);

        if (foundCommand) {
            endStaticText();

            element.hasCommand = true;
            ElementRenderer renderer = createElementRenderer(
                    element, commandAttributes);
            addChild(renderer);

            containers.addFirst(renderer);
        } else {
            appendStaticText(element.renderStartTag(element.attributes));
        }
    }

    private void configureRemoveCommand(
            Element element, ElementRenderer renderer)
    {
        if (renderer.removeMode == null) {
            return;
        }

        switch (renderer.removeMode) {
        case ELEMENT:
            // Discard element and all its content.
            removeChild(renderer);
            break;

        case TAG:
            if (!renderer.hasCommand() && !renderer.hasDynamicContent()) {
                // Discard tag, but preserve the children.
                removeChild(renderer);
                for (Renderer child : renderer.getChildren()) {
                    addChild(child);
                }
            }
            break;

        case CONTENT:
            if (!renderer.hasCommand()) {
                // Discard children.  Statically render the tag.
                removeChild(renderer);

                appendStaticText(element.renderStartTag(element.attributes));
                appendStaticText(element.renderEndTag());
            }
            break;
        }
    }

    @Override
    public void endElement(
            String namespaceUri, String localName, String qualifiedName)
        throws SAXException
    {
        Element element = openElements.removeFirst();
        if (element.hasCommand) {
            endStaticText();

            ElementRenderer renderer =
                    (ElementRenderer) containers.removeFirst();
            if (renderer.hasDynamicContent()) {
                // Discard children because the content will be replaced at
                // render time.
                renderer.clearChildren();
            }

            configureRemoveCommand(element, renderer);
        } else if (!SimpleParser.IMPLICIT_END_TAG_NS_URI.equals(namespaceUri)) {
            appendStaticText(element.renderEndTag());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        setHasContent();

        appendStaticText(ch, start, length);
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
        appendStaticText("<!--");
        characters(ch, start, length);
        appendStaticText("-->");
    }

    @Override
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        setHasContent();

        appendStaticText("<?");
        appendStaticText(target);
        appendStaticText(" ");
        appendStaticText(data);
        appendStaticText("?>");
    }

    Template getTemplate() {
        return template;
    }
}
