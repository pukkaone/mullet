---
layout: page
title: Java Guide
---

## Installation

Download the [compiled JAR file](https://github.com/pukkaone/mullet/downloads)
and add it to your application's class path.


## Spring MVC Integration

The core distribution includes a Spring view implemention that renders
templates.


### View

A view consists of a model decorator class and a template.  A model decorator
extends the model passed to the template by adding variables and implementing
logic for setting those variables.


### Model Decorator Class

Define a model decorator class by extending the `ModelDecorator` base class:

    import com.github.pukkaone.mullet.ModelDecorator;

    public class Home extends ModelDecorator {
        public String getFullName() {
            return getString("firstName") + " " + getString("lastName");
        }
    }

The base class provides the convenience method `getString` to get a model
value as a String.


### View Resolver

A `TemplateViewResolver` resolves a view name to a model decorator class and a
template.  It finds the model decorator class by looking for a class with the
same name as the view name in the Java package specified by the `viewPackage`
property.  It finds the template by appending the suffix specified by the
`suffix` property to the view name and looking for a file with that name in the
same specified Java package.  If the model decorator class is not found, then
the view will render the model data to template directly.

    <bean class="com.github.pukkaone.mullet.spring.TemplateViewResolver">
      <property name="viewPackage" value="com.example.view"/>
      <property name="suffix" value=".html"/>
    </bean>


### Layout

If the same elements, such as headings and navigation bars, appears on every
page in your application, a layout offers a simple way to render those common
elements.  For more complicated page composition needs,
[SiteMesh](https://github.com/sitemesh/sitemesh2) might be a more appropriate
solution.

To enable a layout, configure the `layout` property to the name of the view to
use as the layout.  Like any other view, the layout view consists of a model
decorator class and a template.

    <bean class="com.github.pukkaone.mullet.spring.TemplateViewResolver">
      <property name="viewPackage" value="com.example.view"/>
      <property name="suffix" value=".html"/>
      <property name="layout" value="Layout"/>
    </bean>

The output from the page view is passed to the layout view in the following
variables:

`contextPathURL`: request context path

`title`: content of the `title` element from the original page output

`body`: content of the `body` element from the original page output

The `body` variable typically contains HTML markup, so the layout template must
use the `data-escape-xml="false"` command to prevent markup characters being
escaped when rendering the variable.


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
