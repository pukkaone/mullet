# Logic-less HTML templates

  * Extremely simple variable syntax is incapable of expressing logic in the
    templates.
  * Templates are clean HTML.  Your HTML authoring tool and browser will
    correctly display the templates while you prototype your user interface.


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


## Java build instructions


### Prerequisites

  * JDK 1.5 or better
  * Ant


### Building

Starting in the top distribution directory (where this README.md file is
located), run the commands:

    cd java/core
    ant

That created a JAR file in the `java/core/target` directory.


## Ruby build instructions

### Prerequisites

  * Ruby 1.9 or better
  * I18n
  * Nokogiri
  * Redcarpet
  * YARD


### Building

Starting in the top distribution directory (where this README.md file is
located), run the commands:

    cd ruby/core
    rake


## Project

**Web site:**    http://pukkaone.github.com/mullet/

**Source code:** https://github.com/pukkaone/mullet
