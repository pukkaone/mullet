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

import com.github.pukkaone.mullet.FailedValueStrategy;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads templates from classpath resources, and caches them for fast retrieval
 * of already loaded templates.
 * <p>
 * By default, templates render an empty string when a variable is not found or
 * its value is null. Call the {@link #onMissing} and {@link #onNull} methods to
 * configure how templates loaded by this loader should handle missing and null
 * values respectively. For convenience, {@link FailedValueStrategy} provides
 * these actions you can set:
 * <ul>
 * <li>{@link FailedValueStrategy#RETURN_NULL},
 * <li>{@link FailedValueStrategy#RETURN_EMPTY_STRING}, and
 * <li>{@link FailedValueStrategy#THROW_EXCEPTION}
 * </ul>
 */
public class TemplateLoader {

    private String templatePath;
    private ConcurrentHashMap<String, Template> templateCache =
            new ConcurrentHashMap<String, Template>();
    private TemplateParser parser = new TemplateParser(this);
    private FailedValueStrategy missingValueStrategy =
            FailedValueStrategy.RETURN_EMPTY_STRING;
    private FailedValueStrategy nullValueStrategy =
            FailedValueStrategy.RETURN_EMPTY_STRING;

    /**
     * Constructor.
     *
     * @param templatePath
     *            path to search for template files
     */
    public TemplateLoader(String templatePath) {
        if (!templatePath.startsWith("/")) {
            templatePath = '/' + templatePath;
        }
        if (!templatePath.endsWith("/")) {
            templatePath += '/';
        }
        this.templatePath = templatePath;
    }

    /**
     * Constructor.
     *
     * @param templatePath
     *            path to search for template files is set to the package
     *            directory of the class
     */
    public TemplateLoader(Class<?> templatePath) {
        this(templatePath.getPackage().getName().replace('.', '/'));
    }

    /**
     * Sets action to perform when a variable name is not found.
     *
     * @param strategy
     *            action to perform
     * @return template loader
     */
    public TemplateLoader onMissing(FailedValueStrategy strategy) {
        missingValueStrategy = strategy;
        return this;
    }

    /**
     * Sets action to perform when a value is null.
     *
     * @param strategy
     *            action to perform
     * @return template loader
     */
    public TemplateLoader onNull(FailedValueStrategy strategy) {
        nullValueStrategy = strategy;
        return this;
    }

    private String getCacheKey(String fileName, String id) {
        String cacheKey = templatePath + fileName;
        if (id != null) {
            cacheKey += "#" + id;
        }
        return cacheKey;
    }

    protected Template parse(String fileName, String id) throws IOException {
        String filePath = templatePath + fileName;
        InputStream input = TemplateLoader.class.getResourceAsStream(filePath);
        if (input == null) {
            throw new FileNotFoundException(filePath);
        }
        Template template = parser.parse(input, id);
        input.close();

        // Construct default resource bundle name by stripping extension from
        // template file name.
        int iPeriod = filePath.lastIndexOf('.');
        if (iPeriod > 0) {
            filePath = filePath.substring(0, iPeriod);
        }
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }

        // Try to load default resource bundle.
        try {
            template.setMessages(ResourceBundle.getBundle(filePath));
        } catch (MissingResourceException e) {
            // Don't throw an exception if the resource bundle was not found
            // because it is reasonable for the template to not use any
            // messages.
            // TODO: Log resource bundle was not found.
        }

        template.onMissing(missingValueStrategy).onNull(nullValueStrategy);
        return template;
    }

    /**
     * Gets the template from cache if it's already there, otherwise loads it
     * from the file and caches it.
     *
     * @param fileName
     *            file name
     * @param id
     *            If {@code null}, then the template is the entire file,
     *            otherwise the template is the content of the element having an
     *            {@code id} attribute value equal to this argument.
     * @return template
     * @throws FileNotFoundException
     *             if the named template was not found.
     */
    protected Template load(String fileName, String id) throws IOException {
        String cacheKey = getCacheKey(fileName, id);
        Template template = templateCache.get(cacheKey);
        if (template == null) {
            template = parse(fileName, id);
            templateCache.put(cacheKey, template);
        }
        return template;
    }

    /**
     * Loads named template.
     *
     * @param uri
     *            file name optionally followed by {@code #}<i>id</i>
     * @return template
     * @throws FileNotFoundException
     *             if the named template was not found.
     */
    public Template load(String uri) throws IOException {
        String id = null;
        int iHash = uri.indexOf('#');
        if (iHash >= 0) {
            id = uri.substring(iHash + 1);
            uri = uri.substring(0, iHash);
        }

        return load(uri, id);
    }
}
