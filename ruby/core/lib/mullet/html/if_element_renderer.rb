require 'mullet/model'

module Mullet; module HTML

  # Renders an element if variable is true.
  class IfElementRenderer < ElementRenderer

    # Constructor
    #
    # @param [Element] element
    #           element to render
    # @param [String] variable_name
    #           name of variable containing condition
    def initialize(element, variable_name)
      super(element)
      @variable_name = variable_name.to_sym()
    end

    def should_render_element(render_context)
      value = render_context.get_variable_value(@variable_name)
      if value == Model::NOT_FOUND || value == nil
        return false
      end

      if value.is_a?(Boolean)
        return value
      end

      if value.respond_to?(:empty?)
        return !value.empty?()
      end

      return true
    end

    alias :super_render :render

    def render(render_context)
      if should_render_element()
        super_render(render_context)
      end
    end
  end

end; end
