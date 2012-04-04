---
layout: page
title: Ruby Guide
---

## Installation

Install the gem:

    gem install mullet


## Sinatra Extension

If you are have a "Classic" style Sinatra application, where the application is
defined at the top level:

    require 'mullet/sinatra'

For a "Modular" style application, you must also explicitly register the
extension:

    class App < Sinatra::Base
      register Mullet::Sinatra

The extension adds a helper method to render a view:

    get '/' do
      @first_name = 'John'
      @last_name = 'Smith'
      mullet :index
    end

A view consists of a Ruby view class loaded from a `*.rb` file and a template
loaded from `*.html` file.  By default, the templating engine looks for these
files under the `views` folder.  For example, when `mullet :index` is called,
the engine attempts to load a Ruby view class named `Views::Index` from the
`views/index.rb` file.  It loads the template from the `view/index.html` file.

The view class is responsible for transforming the application data into a
format suitable for rendering in the template.  The view class must extend the
`Mullet::View` class:

    require 'mullet/view'

    module Views
      class Index < Mullet::View
        def full_name()
          return "#{fetch(:first_name)} #{fetch(:last_name)}"
        end
      end
    end

In addition to the application data, the template can read the variable named
`full_name` and the value comes from calling the method with the same name in
the view class.  The `fetch` method reads the named variable value from the
application data.

If the view class is not found, then the engine will render the application
data in the template directly.


### Layout

Each time a template is rendered, the output is processed through a layout by
default.  The default layout consists of the view class named `Views::Layout`
in the `views/layout.rb` file and the template in the `views/layout.html` file.
If the view class is not found, then the template is rendered directly.   In
addition to the variables from the application, the layout template can also
read the `content` variable to get the original template output.  The `content`
variable typically contains HTML markup, so the layout must use the
`data-escape-xml="false"` command to prevent markup characters being escaped
when rendering the variable.

You can individually disable the layout by passing `layout: false` as an option:

    get '/' do
      mullet :index, layout: false
    end

You can individually specify another layout name using the `layout` option:

    get '/' do
      mullet :index, { :layout => :another_layout }
    end


## Ruby API

Create a `TemplateLoader` instance:

    require 'mullet'

    # Given template_path is the name of a folder containing template files.
    loader = Mullet::HTML::TemplateLoader.new(template_path)

Load a template from a file:

    # Given file_name is the name of the template file to load.
    # The loader finds the file relative to the template_path.
    template = loader.load(file_name)

Render the template:

    # Given data is an object providing the data to render.
    output = ''
    template.execute(data, output)

    # output contains the rendered output.


### Localization

Messages are resolved using [I18n](https://github.com/svenfuchs/i18n).  If
you're using I18n outside Rails, you need to tell it where to find the
translation files.

    I18n.load_path += Dir.glob(File.join(app.root, 'locale', '*.{rb,yml}'))
