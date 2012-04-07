require 'mullet/sinatra/engine'
require 'sinatra/base'

module Mullet

  # Sinatra extension for rendering views with Mullet.
  #
  # Example:
  #
  #   require 'mullet/sinatra'
  #
  #   class App < Sinatra::Base
  #     register Mullet::Sinatra
  #
  #     set :mullet, {
  #       # path to folder containing template .html files.  If not set, then
  #       # default is `settings.views`
  #       template_path: "views"
  #     }
  #
  #     get '/' do
  #       mullet :index
  #     end
  #   end
  #
  # When `mullet :index` is called, the engine will attempt to load a Ruby view
  # class named `Views::Index` from the `views/index.rb` file.  If the view
  # class is not found, then the engine will render the template file
  # `view/index.html` directly.
  #
  # By default, the rendered page will passed to a layout view named `:layout`.
  # The rendered page content is passed to the layout template in the variable
  # `content`.
  module Sinatra
    module Helpers
      @@engine = Mullet::Sinatra::Engine.new()

      # Renders output for a view
      #
      # @param [Symbol] view_name
      #         name of view to render
      # @param [Hash] options
      #         name to value hash of options:
      #         :layout
      #                 If value is `false`, no layout is used, otherwise the
      #                 value is the layout name to use.
      #         :locals
      #                 name to value hash of local variables to make available
      #                 to the view
      # @param [Hash] locals
      #         name to value hash of local variables to make available to the
      #         view
      # @return [String] rendered output
      def mullet(view_name, options={}, locals={})
        # The options hash may contain a key :locals with the value being a
        # hash mapping variable names to values.
        locals.merge!(options.delete(:locals) || {})

        # Get application settings.
        view_options = { root: settings.root, views: settings.views }

        if settings.respond_to?(:mullet)
          view_options = settings.mullet.merge(view_options)
        end

        view_options.merge!(options)

        # Copy instance variables set by Sinatra application.
        application_data = Object.new()
        instance_variables.each do |name|
          application_data.instance_variable_set(
              name, instance_variable_get(name))
        end

        # Render view.
        template = @@engine.get_template(view_name, view_options)
        view_class = @@engine.get_view_class(view_name, view_options)
        view = view_class.new()
        view.set_model(application_data, locals)
        output = ''
        template.execute(view, output)

        # Render layout.
        layout_name = :layout
        if options[:layout]
          layout_name = options[:layout]
        end

        if layout_name != false
          # If configured layout is true or nil, then use :layout.
          if layout_name == true || !layout_name
            layout_name = :layout
          end

          layout = @@engine.get_template(layout_name, view_options)
          view_class = @@engine.get_view_class(layout_name, view_options)
          view = view_class.new()
          view.set_model(application_data, locals, { content: output })
          layout_output = ''
          layout.execute(view, layout_output)
          output = layout_output
        end

        return output
      end
    end

    # Called when this extension is registered.
    def self.registered(app)
      app.helpers Mullet::Sinatra::Helpers
    end

  end
end

Sinatra.register Mullet::Sinatra
