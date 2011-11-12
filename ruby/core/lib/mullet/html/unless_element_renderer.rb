require 'mullet/html/if_element_renderer'

module Mullet; module HTML

  # Renders an element if variable is false.
  class UnlessElementRenderer < IfElementRenderer

    # Constructor
    #
    # @param [Element] element
    #           element to render
    # @param [String] variable_name
    #           name of variable containing condition
    def initialize(element, variable_name)
      super(element, variable_name)
      @variable_name = variable_name.to_sym()
    end

    def should_render_element(render_context)
      return !super
    end
  end

end; end
