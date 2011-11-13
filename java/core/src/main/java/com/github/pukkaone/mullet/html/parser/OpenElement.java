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

import com.github.pukkaone.mullet.nu.validator.htmlparser.impl.HtmlAttributes;
import java.util.HashMap;
import javax.xml.XMLConstants;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Element where the parser has seen the start tag but not yet seen the end tag. 
 */
class OpenElement {
    // "xmlns:"
    private static final String XMLNS_ATTRIBUTE_PREFIX =
            XMLConstants.XMLNS_ATTRIBUTE + ':';

    OpenElement parent;
    String uri;
    String localName;
    String qualifiedName;
    Attributes attributes;

    // namespace prefix to URI map
    HashMap<String, String> namespaceDeclarations =
            new HashMap<String, String>();
    
    OpenElement(
            OpenElement parent,
            String qualifiedName,
            HtmlAttributes rawAttributes)
    {
        this.parent = parent;
        processNamespaceDeclarations(rawAttributes);
        this.uri = resolveQualifiedNameToNamespaceUri(qualifiedName);
        this.localName = extractLocalName(qualifiedName);
        this.qualifiedName = qualifiedName;
        this.attributes = toNamespaceAwareAttributes(rawAttributes);
    }
    
    private void declareNamespace(String rawAttributeName, String uri) {
        String prefix;
        if (rawAttributeName.startsWith(XMLNS_ATTRIBUTE_PREFIX)) {
            prefix = rawAttributeName.substring(
                    XMLNS_ATTRIBUTE_PREFIX.length());
        } else {
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
        }
        namespaceDeclarations.put(prefix, uri);
    }
    
    private void processNamespaceDeclarations(HtmlAttributes rawAttributes) {
        for (int i = 0; i < rawAttributes.getXmlnsLength(); ++i) {
            String qualifiedName = rawAttributes.getXmlnsLocalName(i);
            declareNamespace(qualifiedName, rawAttributes.getXmlnsValue(i));
        }
    }
    
    private String extractNamespacePrefix(String qualifiedName) {
        int iColon = qualifiedName.indexOf(':');
        return (iColon > 0)
                ? qualifiedName.substring(0, iColon)
                : XMLConstants.DEFAULT_NS_PREFIX;
    }
    
    private String extractLocalName(String qualifiedName) {
        int iColon = qualifiedName.indexOf(':');
        return (iColon > 0) ? qualifiedName.substring(iColon + 1) : qualifiedName;
    }

    private String resolvePrefixToUri(String prefix) {
        String uri = namespaceDeclarations.get(prefix);
        if (uri == null && parent != null) {
            return parent.resolvePrefixToUri(prefix);
        }
        return uri;
    }
    
    private String resolveQualifiedNameToNamespaceUri(String qualifiedName) {
        String prefix = extractNamespacePrefix(qualifiedName);
        String uri = resolvePrefixToUri(prefix);
        return (uri == null) ? XMLConstants.NULL_NS_URI : uri;
    }
    
    private Attributes toNamespaceAwareAttributes(Attributes rawAttributes) {
        AttributesImpl nsAttributes = new AttributesImpl();
        for (int i = 0; i < rawAttributes.getLength(); ++i) {
            String qualifiedName = rawAttributes.getQName(i);
            String uri = resolveQualifiedNameToNamespaceUri(qualifiedName);
            nsAttributes.addAttribute(
                    uri,
                    extractLocalName(qualifiedName),
                    qualifiedName,
                    "CDATA",
                    rawAttributes.getValue(i));
        }
        return nsAttributes;
    }
}
