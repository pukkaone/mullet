---
layout: page
title: Java Guide
---

## Installation

Download the [compiled JAR file](https://github.com/pukkaone/mullet/downloads)
and add it to your application's class path.


## Spring MVC integration

The core distribution includes a Spring view implemention that renders
templates.


### View resolver

A `TemplateViewResolver` resolves a view name to a template by looking for a
class path resource file containing the template.  It appends the `suffix`
property to the name, then looks for the name in the class path resource folder
specified by the `templateLoaderPath` property.

    <bean class="com.github.pukkaone.mullet.spring.TemplateViewResolver">
      <property name="templateLoaderPath" value="/views"/>
      <property name="suffix" value=".html"/>
    </bean>


### Layout

If the same elements, such as headings and navigation bars, appears on every
page in your application, a layout offers a simple way to render those common
elements.  For more complicated page composition needs,
[SiteMesh](https://github.com/sitemesh/sitemesh2) might be a more appropriate
solution.

To enable a layout, configure the `layout` property.  The `layout` property is
the view name to resolve to the layout template.

    <bean class="com.github.pukkaone.mullet.spring.TemplateViewResolver">
      <property name="templateLoaderPath" value="/views"/>
      <property name="suffix" value=".html"/>
      <property name="layout" value="layout"/>
    </bean>

The view renders the page from a template, then processes the output through
the layout template.  The following variables are available to the layout
template:

`contextPathURL`: request context path

`title`: content of the `title` element from the original template output

`body`: content of the `body` element from the original template output

The `body` variable typically contains HTML markup, so the layout must use the
`data-escape-xml="false"` command to prevent markup characters being escaped
when rendering the variable.


### Localization

Messages are resolved from the Spring application context
[MessageSource](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/beans.html#context-functionality-messagesource).


## Java API

A `TemplateLoader` loads templates from class path resources.

    // Given templatePath is the name of a folder containing template files.
    loader = new TemplateLoader(templatePath)

Load a template from a file:

    // Given fileName is the name of the template file to load.
    // The loader finds the file relative to the templatePath.
    Template template = loader.load(fileName)

Render the template:

    // Given data is an object providing the data to render.
    StringWriter writer = new StringWriter();
    template.execute(data, writer)

    // writer contains the rendered output.


### Localization

Unless changed through the Java API, a template resolves messages from a
resource bundle named by removing the extension from the template file name.
For example, if a template file name is `index.html`, the default resource
bundle name is `index`. You would typically put a `index.properties` file in
the same directory as the `index.html` file so the JDK ResourceBundle
implementation can find it.

To force a template to use a specific resource bundle, call the overloaded
`execute` method which accepts a resource bundle parameter:

    // Given messages is a resource bundle.
    template.execute(data, messages, writer)
