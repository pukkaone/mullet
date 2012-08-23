---
layout: home
title: "logic-less HTML templates"
---

## Example

Given the template:

    <ul>
      <li data-for="repos">
        <a data-href="url" data-text="description"></a>
      </li>
    </ul>

Given the data in the hash:

    {
      repos: [
        { url: "hello", description: "Hello project" },
        { url: "world", description: "World project" }
      ]
    }

Will render the output:

    <ul>
      <li>
        <a href="hello">Hello project</a>
      </li>
      <li>
        <a href="world">World project</a>
      </li>
    </ul>


## Credits

  * Inspired by
    [ctemplate](http://code.google.com/p/google-ctemplate/),
    [mustache](http://mustache.github.com/),
    [Wicket](http://wicket.apache.org/),
    [Cambridge](http://code.google.com/p/cambridge/) and
    [Thymeleaf](http://www.thymeleaf.org/).
  * The Java code to parse templates uses the
    [Validator.nu HTML Parser](http://about.validator.nu/htmlparser/)
    patched to report CDATA sections.
  * The Java code to resolve variable values comes from
    [JMustache](https://github.com/samskivert/jmustache).
  * The Ruby code to parse templates uses
    [html5lib](http://code.google.com/p/html5lib/)
    patched to report CDATA sections.