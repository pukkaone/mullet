require 'mullet/container'
require 'mullet/render_context'

module Mullet; module HTML

  # Template containing static text and dynamically generated content.
  class Template
    include Container

    RETURN_EMPTY_STRING = Proc.new { '' }

    def initialize()
      super
      @on_missing = RETURN_EMPTY_STRING
      @on_nil = RETURN_EMPTY_STRING
    end

    def on_missing(strategy)
      @on_missing = strategy
      return self
    end

    def on_nil(strategy)
      @on_nil = strategy
      return self
    end

    def render(render_context)
      render_children(render_context)
    end

    # Renders the template.
    #
    # @param [Object] data
    #           provides data to render
    # @param [#<<] output
    #           where to write rendered output
    def execute(data, output)
      render_context = RenderContext.new(data, @on_missing, @on_nil, output)
      render(render_context)
    end
  end

end; end
