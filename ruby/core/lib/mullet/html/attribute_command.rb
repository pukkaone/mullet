module Mullet

  # Operation to set attribute value.
  module AttributeCommand

    # Constructor
    #
    # @param [Symbol] attribute_name
    #           name of attribute this command sets
    def initialize(attribute_name)
      @attribute_name = attribute_name
    end

    # Sets attribute in the _attributecommand.
    #
    # @param [RenderContext] render_context
    #           render context
    # @param [Attributes] attributes
    #           attributes to update
    def execute(render_context, attributes)
      value = get_value(render_context)
      if value == Model::NOT_FOUND || value == nil
        # Value not found.  Do not render the attribute.
        attributes.delete(@attribute_name)
      else
        escaped_value = render_context.escape_xml(value)
        attributes.store(@attribute_name, escaped_value)
      end
    end
  end

end
