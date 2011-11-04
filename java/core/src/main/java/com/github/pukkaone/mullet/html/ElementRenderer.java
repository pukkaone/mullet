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
import com.github.pukkaone.mullet.RenderContext;
import com.github.pukkaone.mullet.Renderer;
import com.github.pukkaone.mullet.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Renders an HTML element.
 */
class ElementRenderer extends Container implements Renderer {

    private static final String ATTRIBUTE_SEPARATOR = ";";
    private static final char ATTRIBUTE_NAME_SEPARATOR = '=';
    private static final String ATTRIBUTE_SYNTAX_ERROR =
            "expected '%s' in '%s'";
    private static final String ATTRIBUTE_NAME_MISSING_ERROR =
            "attribute name missing in '%s'";
    private static final String[] CONVENIENT_ATTRIBUTE_COMMANDS =
            new String[] {
                Command.ACTION,
                Command.ALT,
                Command.HREF,
                Command.SRC,
                Command.TITLE,
                Command.VALUE,
            };

    private Element element;
    private ArrayList<AttributeCommand> attributeCommands =
            new ArrayList<AttributeCommand>();
    RemoveMode removeMode;
    private String textVariableName;
    private Message textMessage;
    private Template template;
    private Boolean escapeXml;

    ElementRenderer(Element element) {
        this.element = element;
    }

    private void addAttributeCommand(
            String attributeName, String variableName)
    {
        attributeCommands.add(
                new ModelAttributeCommand(attributeName, variableName));
    }

    private void addAttributeCommands(String attributeAndVariablePairs) {
        StringTokenizer iAttribute = new StringTokenizer(
                attributeAndVariablePairs, ATTRIBUTE_SEPARATOR);
        while (iAttribute.hasMoreTokens()) {
            String commandText = iAttribute.nextToken();

            int iColon = commandText.indexOf(ATTRIBUTE_NAME_SEPARATOR);
            if (iColon < 0) {
                throw new TemplateException(String.format(
                        ATTRIBUTE_SYNTAX_ERROR,
                        ATTRIBUTE_NAME_SEPARATOR,
                        commandText));
            }

            String attributeName = commandText.substring(0, iColon).trim();
            if (attributeName.length() == 0) {
                throw new TemplateException(String.format(
                        ATTRIBUTE_NAME_MISSING_ERROR, commandText));
            }

            String variableName = commandText.substring(iColon + 1).trim();
            if (variableName.length() == 0) {
                throw new TemplateException(String.format(
                        "variable name missing in '%s'", commandText));
            }

            addAttributeCommand(attributeName, variableName);
        }
    }

    private void configureAttributeCommands(Attributes commandAttributes) {
        String value = commandAttributes.get(Command.ATTR);
        if (value != null) {
            addAttributeCommands(value);
        }

        for (String attributeName : CONVENIENT_ATTRIBUTE_COMMANDS) {
            value = commandAttributes.get(attributeName);
            if (value != null) {
                addAttributeCommand(attributeName, value);
            }
        }

        value = commandAttributes.get(Command.REMOVE);
        if (value != null) {
            value = value.trim();
            try {
                removeMode = RemoveMode.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new TemplateException(
                        String.format("invalid remove argument '%s'", value),
                        e);
            }
        }
    }

    private void addAttributeMessageCommand(String attributeName,
            String messageArguments) {
        attributeCommands.add(
                new MessageAttributeCommand(
                        attributeName, new Message(messageArguments)));
    }

    private void addAttributeMessageCommands(String attributeAndMessagePairs) {
        StringTokenizer iAttribute = new StringTokenizer(
                attributeAndMessagePairs, ATTRIBUTE_SEPARATOR);
        while (iAttribute.hasMoreTokens()) {
            String commandText = iAttribute.nextToken();

            int iColon = commandText.indexOf(ATTRIBUTE_NAME_SEPARATOR);
            if (iColon < 0) {
                throw new TemplateException(String.format(
                        ATTRIBUTE_SYNTAX_ERROR,
                        ATTRIBUTE_NAME_SEPARATOR,
                        commandText));
            }

            String attributeName = commandText.substring(0, iColon).trim();
            if (attributeName.length() == 0) {
                throw new TemplateException(String.format(
                        ATTRIBUTE_NAME_MISSING_ERROR, commandText));
            }

            String messageArguments = commandText.substring(iColon + 1).trim();
            if (messageArguments.length() == 0) {
                throw new TemplateException(String.format(
                        "message arguments missing in '%s'", commandText));
            }

            addAttributeMessageCommand(attributeName, messageArguments);
        }
    }

    private void configureAttributeMessageCommands(Attributes commandAttributes) {
        String value = commandAttributes.get(Command.ALT_MESSAGE);
        if (value != null) {
            addAttributeMessageCommand(Command.ALT, value);
        }

        value = commandAttributes.get(Command.ATTR_MESSAGE);
        if (value != null) {
            addAttributeMessageCommands(value);
        }

        value = commandAttributes.get(Command.TITLE_MESSAGE);
        if (value != null) {
            addAttributeMessageCommand(Command.TITLE, value);
        }

        value = commandAttributes.get(Command.VALUE_MESSAGE);
        if (value != null) {
            addAttributeMessageCommand(Command.VALUE, value);
        }
    }

    private void configureContent(
            Attributes commandAttributes, TemplateLoader loader)
    {
        textVariableName = commandAttributes.get(Command.TEXT);
        if (textVariableName != null) {
            textVariableName = textVariableName.intern();
            return;
        }

        String textMessageArguments =
                commandAttributes.get(Command.TEXT_MESSAGE);
        if (textMessageArguments != null) {
            textMessage = new Message(textMessageArguments);
            return;
        }

        String uri = commandAttributes.get(Command.INCLUDE);
        if (uri != null) {
            try {
                template = loader.load(uri);
            } catch (IOException e) {
                throw new TemplateException("load", e);
            }
        }
    }

    void configureCommands(
            Attributes commandAttributes, TemplateLoader loader)
    {
        configureAttributeCommands(commandAttributes);
        configureAttributeMessageCommands(commandAttributes);

        configureContent(commandAttributes, loader);

        String escapeXmlValue = commandAttributes.get(Command.ESCAPE_XML);
        if (escapeXmlValue != null) {
            escapeXml = !Boolean.FALSE.toString().equals(escapeXmlValue);
        }
    }

    /**
     * Checks if this renderer has a command.  If it has a command, then the
     * template builder should not discard it.
     */
    boolean hasCommand() {
        return !attributeCommands.isEmpty();
    }

    /**
     * Checks if the element content will be rendered by a command.
     */
    boolean hasDynamicContent() {
        return textVariableName != null
            || textMessage != null
            || template != null;
    }

    private Attributes executeAttributeCommands(RenderContext renderContext) {
        if (attributeCommands.isEmpty()) {
            return element.attributes;
        }

        // Copy the original attributes.  The commands modify the copy to
        // produce the attributes to render.
        Attributes renderAttributes = (Attributes) element.attributes.clone();
        for (AttributeCommand command : attributeCommands) {
            command.execute(renderContext, renderAttributes);
        }
        return renderAttributes;
    }

    private boolean shouldRenderTag() {
        return removeMode == null || removeMode == RemoveMode.CONTENT;
    }

    private void renderStartTag(RenderContext renderContext) {
        if (shouldRenderTag()) {
            Attributes attributes = executeAttributeCommands(renderContext);
            renderContext.write(element.renderStartTag(attributes));
        }
    }

    private void renderEndTag(RenderContext renderContext) {
        if (shouldRenderTag()) {
            renderContext.write(element.renderEndTag());
        }
    }

    private boolean shouldRenderContent() {
        return removeMode != RemoveMode.CONTENT;
    }

    private void renderContent(RenderContext renderContext) {
        if (shouldRenderContent()) {
            if (textVariableName != null) {
                Object value = renderContext.getDisplayValue(textVariableName);
                String text = renderContext.escapeXml(value.toString());
                renderContext.write(text);
            } else if (textMessage != null) {
                String text = textMessage.format(renderContext);
                text = renderContext.escapeXml(text);
                renderContext.write(text);
            } else if (template != null) {
                template.render(renderContext);
            } else {
                renderChildren(renderContext);
            }
        }
    }

    public void render(RenderContext renderContext) {
        // Process the command to change the escaping mode.
        boolean originalEscapeXmlEnabled = renderContext.isEscapeXmlEnabled();
        if (escapeXml != null) {
            renderContext.setEscapeXmlEnabled(escapeXml);
        }

        renderStartTag(renderContext);
        renderContent(renderContext);
        renderEndTag(renderContext);

        // Restore the original escaping mode.
        if (escapeXml != null) {
            renderContext.setEscapeXmlEnabled(originalEscapeXmlEnabled);
        }
    }
}
