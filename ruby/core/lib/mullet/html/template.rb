require 'mullet/container'

module Mullet; module HTML

  # Template containing static text and dynamically generated content.
  class Template
    include Container

    RETURN_EMPTY_STRING = Proc.new { '' }

    def initialize()
      @missing_value_strategy = RETURN_EMPTY_STRING
      @nil_value_strategy = RETURN_EMPTY_STRING
    end

    def on_missing(strategy)
      @missing_value_strategy = strategy
      return self
    end

    def on_nil(strategy)
      @nil_value_strategy = strategy
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
      render_context = RenderContext.new(data, output)
      render(render_context)
    end
  end

end; end
