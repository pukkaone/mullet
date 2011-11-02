# Logic-less HTML templates

  * Extremely simple variable syntax is incapable of expressing logic in the
    templates.
  * Templates are clean HTML.  Your HTML authoring tool and browser will
    correctly display the templates while you prototype your user interface.


## Example

    <ul v:if="posts">
      <li v:for="posts">
        <a v:href="url" v:text="subject"></a>
      </li>
    </ul>
    <p v:unless="posts">
      No posts.
    </p>


## Commands

In the following reference, `[]` surrounds optional parameters, and
`{}` surrounds optional, repeated parameters.


### Namespace

Commands are specific attributes and elements added to HTML markup.
These attributes and elements belong to a namespace, typically declared using

    xmlns:v="http://pukkaone.github.com/mullet/1"

The template engine also recognizes attributes and elements with the prefix
`mullet:` as commands even when there is no namespace declaration for that
prefix.  However, declaring a namespace is the suggested convention because it
allows you to declare a shorter prefix and thus make the commands shorter to
write.  The examples in this reference assume a namespace with the prefix `v:`
is declared.


### Set attribute

`v:attr = "`_attribute_`:`_variable_ {`;` _attribute_`:`_variable_}`"`

Set the attribute named _attribute_ with the value of the variable named
_variable_.  If the variable does not exist, or the value is null in Java or
nil in Ruby, then remove the attribute.

    <h1 v:attr="title:greeting">

For convenience, these commands set commonly used HTML attributes and require
only the variable as the parameter.

  * v:action
  * v:alt
  * v:href
  * v:src
  * v:title
  * v:value


### Set element content text

`v:text = "`_variable_`"`

Set the text content of the element to the value of the variable named
_variable_.  The text replaces any existing children of the element.

    <span v:text="firstName"></span>


### Loop

`v:for = "`_variable_`"`

If the value is an object, then render the loop once.

    <ul>
      <li v:for="repos">
        <span v:text="description"></span>
      </li>
    </ul>


### Render element if condition is true

    v:if="name"

If the key does not exist, or the value is false or an empty collection,
then do not render the element, otherwise render the element.

    <ul v:if="repos">
      <li v:for="repos">
        <span v:text="description"></span>
      </li>
    </ul>


### Render element if condition is false

    v:unless="name"

If the key does not exist, or the value is false or an empty collection,
then render the element, otherwise do not render the element.

    <p v:unless="repos">
      No repos.
    </p>


### Set element content to included template

    v:content="template[#id]"

    <div v:content="footer.html#footer"></div>


### Replace element with included template

    v:include="template[#id]"

    <div v:include="footer.html#footer"></div>


### Escape XML

By default, values are escaped.  Set the attribute

    v:escape-xml="false"

to the literal string "false" to disable escaping.

    <!-- Do not escape XML in this element and all its children. -->
    <h1 v:escape-xml="false">
      Hello, <span v:text="firstName"></span> <span v:text="lastName"></span>
    </h1>


### Container

    <v:container>

For example, you want to render table rows two at a time:

    <table>
      <span v:for="posts">
        <tr>...</tr>
        <tr>...</tr>
      </span>
    </table>

This is invalid HTML because `span` is not one of the elements allowed to
nest under `table`.  You can rewrite the example as

    <table>
      <v:container v:for="posts">
        <tr>...</tr>
        <tr>...</tr>
      </v:container>
    </table>


### Remove

    <v:remove>

Enclose content in this element to remove it from the rendered output.  This is
useful when you want your HTML prototype to display multiple sample items in a
list but all the items will be dynamically generated at run time.

    <ul>
      <li v:for="colors">red</li>
      <v:remove>
        <li>green</li>
        <li>blue</li>
      </v:remove>
    </ul>


## Internationalization


### Set attribute

`v:attr-message = "`_attribute_`:`_key_{`,`_variable_} {`;` _attribute_`:`_key_{`,`_variable_}}`"`

Set the attribute named _attribute_ with the message looked up using the key
_key_.  Message formatting interpolates the value of variables named by
_variable_ parameters.  In Java, placeholders such as `{0}` and `{1}` in the
message are substituted.  In Ruby, the values are available for string
interpolation by variable name in the form `#{`_variable_`}`.

    <a v:attr-message="title:summary,subject,date">

For convenience, these commands set commonly used HTML attributes and require
only the key and variable parameters.

  * v:alt-message
  * v:title-message
  * v:value-message


### Set element content text

`v:text-message = "`_key_{`,`_variable_}`"`

Set the text content of the element to the message looked up using the key
_key_.  The text replaces any existing children of the element.

    <h1 v:text-message="greeting">Hello</h1>


### Java

Unless changed through the Java API, a template resolves messages from a
resource bundle named by removing the extension from the template file name.
For example, if a template file name is `index.html`, the default resource
bundle name is `index`. You would typically put a `index.properties` file in
the same directory as the `index.html` file so the JDK ResourceBundle
implementation can find it.


### Ruby

Messages are resolved using [I18n](https://github.com/svenfuchs/i18n).  To
configure I18n, you need to tell it where to find the translation files.

    I18n.load_path += Dir.glob(File.join(app.root, 'locale', '*.{rb,yml}'))


## Credits

* Inspired by ctemplate, mustache, Wicket, Cambridge and Thymeleaf.
* The Java code to parse templates uses the
  [Validator.nu HTML Parser](http://about.validator.nu/htmlparser/)
  patched to report CDATA sections.
* The Java code to resolve model values comes from Michael Bayne's
  [JMustache](https://github.com/samskivert/jmustache).
