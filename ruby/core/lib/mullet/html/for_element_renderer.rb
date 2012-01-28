require 'mullet/html/command_element_renderer'
require 'mullet/scope'

module Mullet; module HTML

  # Renders an element for each item in a collection.
  class ForElementRenderer < CommandElementRenderer

    # Constructor
    #
    # @param [Element] element
    #           element to render
    # @param [String] variable_name
    #           name of variable containing collection
    def initialize(element, variable_name)
      super(element)
      @variable_name = variable_name.to_sym()
    end

    alias :super_render :render

    def render_nested_scope(data, render_context)
      render_context.push_scope(data)
      super_render(render_context)
      render_context.pop_scope()
    end

    def render(render_context)
      value = render_context.get_variable_value(@variable_name)
      if value == Scope::NOT_FOUND || value == nil || value == false
        return
      end

      if value.respond_to?(:empty?) && value.empty?()
        return
      end

      if value.respond_to?(:each)
        value.each {|item| render_nested_scope(item, render_context) }
        return
      end

      render_nested_scope(value, render_context)
    end
  end

end; end
