---
layout: page
title: Ruby Guide
---

## Installation

Install the gem:

    gem install mullet


## As Sinatra view

Register the template engine with Tilt, then define a rendering method:

    require 'mullet/tilt'

    helpers do
      def mullet(*args)
        render(:html, *args)
      end
    end

This example renders the template in `./views/index.html`:

    get '/' do
      mullet :index
    end


### Layout

Each time a template is rendered, the output is processed through a layout by
default.  The default layout is the template in `./views/layout.html`.  In
addition to the variables from the context and locals, the layout template can
also read the `content` variable to get the original template output.  The
`content` variable typically contains HTML markup, so the layout must use the
`data-escape-xml="false"` command to prevent markup characters being escaped
when rendering the variable.

You can individually disable layouts by passing `layout: false`:

    get '/' do
      mullet :index, layout: false
    end

Or disable them by default with:

    set :html, layout: false


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
