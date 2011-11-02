---
layout: page
title: Command Reference
---
In this reference, `[ ]` surrounds optional parameters, and
`{ }` surrounds optional, repeated parameters.


### Namespace

Commands are specific attributes and elements added to HTML markup.
These attributes and elements belong to a specific namespace, typically
declared as:

    xmlns:v="http://pukkaone.github.com/mullet/1"

The template engine also recognizes attributes and elements with the prefix
`mullet:` as commands even when there is no namespace declaration for that
prefix.  However, declaring a namespace is the suggested convention because it
allows you to declare a shorter prefix and thus make the commands shorter to
write.  The examples in this reference assume a namespace with the prefix `v:`
is declared.


### Undefined variables

Variables which do not exist, or have the value `null` in Java or `nil` in
Ruby, render as an empty string by default.  The programmatic API allows you to
configure the template engine to render some other value or throw an exception
when this happens.


## Commands


### Set attribute

`v:attr = "`_attribute_`=`_variable_ `{;` _attribute_`=`_variable_`}"`

Set the attribute named _attribute_ with the value of the variable named
_variable_.  If the variable does not exist, or the value is `null` in Java or
`nil` in Ruby, then remove the attribute.

    <h1 v:attr="title=greeting">

For convenience, these commands set commonly used HTML attributes and require
only the variable as the parameter:

  * `v:action`
  * `v:alt`
  * `v:href`
  * `v:src`
  * `v:title`
  * `v:value`


### Set element content text

`v:text = "`_variable_`"`

Set the text content of the element to the value of the variable named
_variable_.  The text replaces any existing children of the element.

    <span v:text="firstName"></span>


### Repeat element

`v:for = "`_variable_`"`

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
      <li v:for="repos">
        <span v:text="description"></span>
      </li>
    </ul>


### Render element if condition is true

`v:if = "`_variable_`"`

If the variable does not exist, or the value is an empty collection or Boolean
`false` or `null` in Java or `nil` in Ruby, then do not render the element.
Otherwise render the element.

    <ul v:if="repos">
      <li v:for="repos">
        <span v:text="description"></span>
      </li>
    </ul>


### Render element if condition is false

`v:unless = "`_variable_`"`

If the variable does not exist, or the value is an empty collection or Boolean
`false` or `null` in Java or `nil` in Ruby, then render the element.  Otherwise
do not render the element.

    <p v:unless="repos">
      No repos.
    </p>


### Remove tag or element content

`v:remove = "`_mode_`"`

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
      <li v:for="colors">red</li>
      <li v:remove="element">green</li>
      <li v:remove="element">blue</li>
    </ul>

Another example renders table rows two at a time:

    <table>
      <span v:for="posts">
        <tr>...</tr>
        <tr>...</tr>
      </span>
    </table>

This is invalid HTML because `span` is not one of the elements allowed to
nest under `table`.  Use this command to not render the `span` tag, but still
render its contents:

    <table>
      <span v:for="posts" v:remove="tag">
        <tr>...</tr>
        <tr>...</tr>
      <span>
    </table>


### Set element content to included template

`v:include = "`_template_`[#`_id_`]"`

Set the element content to the template named _template_.  Specify the optional
fragment _id_ to include only the content of an element from the template
identified by an `id` attribute.  The included content replaces any existing
children of the element.

    <div v:include="footer.html#footer"></div>

Combine this command with the `v:remove="tag"` command to replace the entire
element including its start and end tags:

    <div v:include="footer.html#footer" v:remove="tag"></div>


### Escape XML

By default, values are escaped.  Set the attribute

    v:escape-xml="false"

to the literal string "false" to disable escaping within the element.

    <!-- Do not escape XML in this element and all its children. -->
    <h1 v:escape-xml="false">
      Hello, <span v:text="firstName"></span> <span v:text="lastName"></span>
    </h1>


## Localization Commands

These commands render translations using the platform's localization framework.


### Set attribute

`v:attr-message = "`_attribute_`=`_key_`{,`_variable_`} {;` _attribute_`=`_key_`{,`_variable_`}}"`

Set the attribute named _attribute_ with the message looked up using the key
_key_.  Variables named by the optional _variable_ parameters are interpolated
into the message.  In Java, placeholders such as `{0}` and `{1}` in the message
are substituted.  In Ruby, the values are available for string interpolation by
variable name in the form `#{`_variable_`}`.

    <a v:attr-message="title=summary,subject,date">

For convenience, these commands set commonly used HTML attributes and require
only the key and variable parameters:

  * `v:alt-message`
  * `v:title-message`
  * `v:value-message`


### Set element content text

`v:text-message = "`_key_`{,`_variable_`}"`

Set the text content of the element to the message looked up using the key
_key_.  The text replaces any existing children of the element.

    <h1 v:text-message="greeting">Hello</h1>


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
