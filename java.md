---
layout: page
title: Java Guide
---

## Installation

Download the [compiled JAR file](https://github.com/pukkaone/mullet/downloads)
and add it to your application's classpath.


## Spring MVC integration

The core distribution includes a Spring view implemention that renders
templates.


### View resolver

A `TemplateViewResolver` resolves a view name to a template by looking for a
classpath resource file containing the template.  It appends the `suffix`
property to the name, then looks for the name in the classpath resource folder
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

To render templates with a layout, define a `LayoutViewResolver` (instead of a
`TemplateViewResolver`).  It resolves a view name to a view that renders a
template, then processes the template output through a layout template.

    <bean class="com.github.pukkaone.mullet.spring.LayoutViewResolver">
      <property name="templateLoaderPath" value="/views"/>
      <property name="suffix" value=".html"/>
    </bean>

By default, it loads the layout template by resolving the view name `"layout"`,
but you can change the layout by setting the `layout` property to another name.
The following variables are available to the layout template:

`contextPathURL`: request context path

`title`: content of the `title` element from the original template output

`body`: content of the `body` element from the original template output

The `body` variable typically contains HTML markup, so the layout must use the
`data-escape-xml="false"` command to prevent markup characters being escaped
when rendering the variable.


## Java API

A `TemplateLoader` loads templates from classpath resources.

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
