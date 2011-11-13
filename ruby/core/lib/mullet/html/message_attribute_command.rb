require 'mullet/html/attribute_command'

module Mullet; module HTML

  # Operation to set attribute value from translation.
  class MessageAttributeCommand
    include AttributeCommand

    # Constructor
    #
    # @param [Symbol] attribute_name
    #           name of attribute this command sets
    # @param [Message] message
    #           message to format to get attribute value
    def initialize(attribute_name, message)
      super(attribute_name)
      @message = message
    end

    # Gets attribute value by formatting localized message.
    #
    # @param [RenderContext] render_context
    #           render context
    # @return attribute value
    def get_value(render_context)
      return @message.translate(render_context)
    end
  end

end; end
