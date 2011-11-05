---
layout: page
title: Command Reference
---
In this reference, `[ ]` surrounds optional parameters, and
`{ }` surrounds optional, repeated parameters.


### HTML5 data attributes

You write commands in specific attributes added to HTML markup.  These
attributes have names starting with the
[data- prefix](http://www.w3.org/TR/html5/elements.html#embedding-custom-non-visible-data-with-the-data-attributes).


### Namespace

Alternatively, you can write commands in attributes belonging to a specific
namespace, typically declared as:

    xmlns:d="http://pukkaone.github.com/mullet/1"

The examples in this reference use the `data-` prefix, but assuming there's a
namespace declared as above, the examples also work by replacing the `data-`
prefix with the `d:` prefix in the attribute names.

The template engine also recognizes attribute names with the prefix `mullet:`
as commands even when there is no namespace declaration for that prefix.


### Undefined variables

Variables which do not exist, or have the value `null` in Java or `nil` in
Ruby, render as an empty string by default.  The programmatic API allows you to
configure the template engine to render some other value or throw an exception
when this happens.


## Commands


### Set attribute

`data-attr = "`_attribute_`=`_variable_ `{;` _attribute_`=`_variable_`}"`

Set the attribute named _attribute_ with the value of the variable named
_variable_.  If the variable does not exist, or the value is `null` in Java or
`nil` in Ruby, then remove the attribute.

    <h1 data-attr="title=greeting">

For convenience, these commands set commonly used HTML attributes and require
only the variable as the parameter:

  * `data-action`
  * `data-alt`
  * `data-href`
  * `data-src`
  * `data-title`
  * `data-value`


### Set element content text

`data-text = "`_variable_`"`

Set the text content of the element to the value of the variable named
_variable_.  The text replaces any existing children of the element.

    <h1 data-text="greeting"></h1>


### Repeat element

`data-for = "`_variable_`"`

Render the element zero or more times depending on the value of the variable
named _variable_.

  * If the variable does not exist, or the value is Boolean `false` or `null`
    in Java or `nil` in Ruby, then do not render the element.
  * If the value is a collection, then render the element for each item in the
    collection.
  * If the value is an object, then render the element exactly once.

In each iteration, the current item is set as the current context for variable
lookups.  The special variable name `this` references the current item.

    <ul>
      <li data-for="repos">
        <span data-text="description"></span>
      </li>
    </ul>


### Render element if condition is true

`data-if = "`_variable_`"`

If the variable does not exist, or the value is an empty collection or Boolean
`false` or `null` in Java or `nil` in Ruby, then do not render the element.
Otherwise render the element.

    <ul data-if="repos">
      <li data-for="repos">
        <span data-text="description"></span>
      </li>
    </ul>


### Render element if condition is false

`data-unless = "`_variable_`"`

If the variable does not exist, or the value is an empty collection or Boolean
`false` or `null` in Java or `nil` in Ruby, then render the element.  Otherwise
do not render the element.

    <p data-unless="repos">
      No repos.
    </p>


### Remove tag or element content

`data-remove = "`_mode_`"`

Remove parts of the element from being rendered.  The _mode_ parameter is a
string specifying what parts to remove:

  * `element` - Remove entire element including start and end tags, and children
    of the element.
  * `tag` - Remove start and end tags. Preserve children of the element.
  * `content` - Preserve start and end tags. Remove children of the element.

For example, this is useful when you want your HTML prototype to display
multiple sample items in a list, but you don't want the template to render the
sample items when the application runs.

    <ul>
      <li data-for="colors">red</li>
      <li data-remove="element">green</li>
      <li data-remove="element">blue</li>
    </ul>

Another example renders table rows two at a time:

    <table>
      <span data-for="posts">
        <tr>...</tr>
        <tr>...</tr>
      </span>
    </table>

This is invalid HTML because `span` is not one of the elements allowed to
nest under `table`.  Use this command to not render the `span` tag, but still
render its contents:

    <table>
      <span data-for="posts" data-remove="tag">
        <tr>...</tr>
        <tr>...</tr>
      <span>
    </table>


### Set element content to included template

`data-include = "`_template_`[#`_id_`]"`

Set the element content to the template named _template_.  Specify the optional
fragment _id_ to include only the content of an element from the template
identified by an `id` attribute.  The included content replaces any existing
children of the element.

    <div data-include="footer.html#footer"></div>

Combine this command with the `data-remove="tag"` command to replace the entire
element including its start and end tags:

    <div data-include="footer.html#footer" data-remove="tag"></div>


### Escape XML

By default, values are escaped.  Set the attribute

    data-escape-xml="false"

to the literal string "false" to disable escaping within the element.

    <!-- Do not escape XML in this element and all its children. -->
    <h1 data-escape-xml="false">
      Hello, <span data-text="firstName"></span> <span data-text="lastName"></span>
    </h1>


## Localization Commands

These commands render translations using the platform's localization framework.


### Set attribute

`data-attr-message = "`_attribute_`=`_key_`{,`_variable_`} {;` _attribute_`=`_key_`{,`_variable_`}}"`

Set the attribute named _attribute_ with the message looked up using the key
_key_.  Variables named by the optional _variable_ parameters are interpolated
into the message.  In Java, placeholders such as `{0}` and `{1}` in the message
are substituted.  In Ruby, the values are available for string interpolation by
variable name in the form `#{`_variable_`}`.

    <a data-attr-message="title=summary,subject,date">

For convenience, these commands set commonly used HTML attributes and require
only the key and variable parameters:

  * `data-alt-message`
  * `data-title-message`
  * `data-value-message`


### Set element content text

`data-text-message = "`_key_`{,`_variable_`}"`

Set the text content of the element to the message looked up using the key
_key_.  The text replaces any existing children of the element.

    <h1 data-text-message="greeting">Hello</h1>


### Java localization

Unless changed through the Java API, a template resolves messages from a
resource bundle named by removing the extension from the template file name.
For example, if a template file name is `index.html`, the default resource
bundle name is `index`. You would typically put a `index.properties` file in
the same directory as the `index.html` file so the JDK ResourceBundle
implementation can find it.


### Ruby localization

Messages are resolved using [I18n](https://github.com/svenfuchs/i18n).  If
you're using I18n outside Rails, you need to tell it where to find the
translation files.

    I18n.load_path += Dir.glob(File.join(app.root, 'locale', '*.{rb,yml}'))
