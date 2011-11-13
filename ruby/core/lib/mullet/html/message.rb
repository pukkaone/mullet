require 'i18n'
require 'mullet/html/attribute_command'

module Mullet; module HTML

  # Holds the resource key and variable names that need to be resolved to
  # format a localized message.
  class Message
    ARGUMENT_SEPARATOR = ','

    # Constructor
    #
    # @param [String] message_arguments
    #           message key and arguments string
    def initialize(message_arguments)
      arguments = message_arguments.split(ARGUMENT_SEPARATOR)
      if arguments.empty?()
        raise TemplateException.new(
            "incorrect syntax in message #{message_arguments}")
      end

      @message_key = arguments.shift().strip()
      if @message_key.empty?()
        raise TemplateException.new(
            "empty message key in message #{message_arguments}")
      end

      @argument_keys = []
      arguments.each do |argument_key|
        argument_key = argument_key.strip()
        if argument_key.empty?()
          raise TemplateException.new(
              "empty argument key in message #{message_arguments}")
        end
        @argument_keys << argument_key.to_sym()
      end
    end

    # Formats localized message.
    #
    # @param [RenderContext] render_context
    #           render context
    # @return localized message
    def translate(render_context)
      arguments = Hash.new()
      @argument_keys.each do |argument_key|
        arguments.store(
            argument_key, render_context.get_display_value(argument_key))
      end

      return I18n.translate(@message_key, arguments)
    end
  end

end; end
