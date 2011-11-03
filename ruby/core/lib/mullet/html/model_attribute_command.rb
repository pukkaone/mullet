require 'mullet/html/attribute_command'

module Mullet; module HTML

  # Operation to set attribute value from model.
  class ModelAttributeCommand
    include AttributeCommand

    # Constructor
    #
    # @param [Symbol] attribute_name
    #           name of attribute this command sets
    # @param [Symbole] variable_name
    #           variable name to lookup to get value
    def initialize(attribute_name, variable_name)
      super(attribute_name)
      @variable_name = variable_name
    end

    # Gets attribute value by looking up variable name.
    #
    # @param [RenderContext] render_context
    #           render context
    # @return attribute value
    def get_value(render_context)
      return render_context.fetch(@variable_name)
    end
  end

end; end
