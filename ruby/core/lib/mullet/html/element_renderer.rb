require 'mullet/container'
require 'mullet/template_error'
require 'mullet/html/command'
require 'mullet/html/message'
require 'mullet/html/message_attribute_command'
require 'mullet/html/model_attribute_command'

module Mullet; module HTML

  # Renders an HTML element.
  class ElementRenderer
    include Container

    ATTRIBUTE_SEPARATOR = ';'
    ATTRIBUTE_NAME_SEPARATOR = '='
    ATTRIBUTE_SYNTAX_ERROR = "expected '%s' in '%s'"
    ATTRIBUTE_NAME_MISSING_ERROR = "attribute name missing in '%s'"
    CONVENIENT_ATTRIBUTE_COMMANDS = [
        Command::ACTION,
        Command::ALT,
        Command::HREF,
        Command::SRC,
        Command::TITLE,
        Command::VALUE ]

    attr_reader :remove_mode

    # Constructor
    #
    # @param [Element] element
    #           element to render
    def initialize(element)
      super()

      @element = element
      @attribute_commands = []
      @remove_mode = nil
      @text_variable_name = nil
      @text_message = nil
      @template = nil
      @escape_xml = nil
    end

    def add_attribute_command(attribute_name, variable_name)
      @attribute_commands <<
          ModelAttributeCommand.new(attribute_name, variable_name)
    end

    def add_attribute_commands(attribute_variable_pairs)
      attribute_variable_pairs.split(ATTRIBUTE_SEPARATOR).each do |command_text|
        command_parts = command_text.split(ATTRIBUTE_NAME_SEPARATOR, 2)
        if command_parts.length() < 2
          raise TemplateError.new(
              ATTRIBUTE_SYNTAX_ERROR % [ATTRIBUTE_NAME_SEPARATOR, command_text])
        end

        attribute_name = command_parts[0].strip()
        if attribute_name.empty?()
          raise TemplateError.new(ATTRIBUTE_NAME_MISSING_ERROR % command_text)
        end

        variable_name = command_parts[1].strip()
        if variable_name.empty?()
          raise TemplateError.new("variable name missing in '#{command_text}'")
        end

        add_attribute_command(attribute_name, variable_name)
      end
    end

    def configure_attribute_commands(command_attributes)
      value = command_attributes.fetch(Command::ATTR, nil)
      if value != nil
        add_attribute_commands(value)
      end

      CONVENIENT_ATTRIBUTE_COMMANDS.each do |attribute_name|
        value = command_attributes.fetch(attribute_name, nil)
        if value != nil
          add_attribute_command(attribute_name, value)
        end
      end

      value = command_attributes.fetch(Command::REMOVE, nil)
      if value != nil
        value.strip!();
        value.downcase!()
        @remove_mode = RemoveMode.value_of(value)
        if @remove_mode == nil
          raise TemplateError.new("invalid remove argument '#{value}'")
        end
      end
    end

    def add_attribute_message_command(attribute_name, message_arguments)
      @attribute_commands << MessageAttributeCommand.new(
          attribute_name, Message.new(message_arguments))
    end

    def add_attribute_message_commands(attribute_message_pairs)
      attribute_message_pairs.split(ATTRIBUTE_SEPARATOR).each do |command_text|
        command_parts = command_text.split(ATTRIBUTE_NAME_SEPARATOR, 2)
        if parts.length() < 2
          raise TemplateError.new(
              ATTRIBUTE_SYNTAX_ERROR % [ATTRIBUTE_NAME_SEPARATOR, command_text])
        end

        attribute_name = command_parts[0].strip()
        if attribute_name.empty?()
          raise TemplateError.new(ATTRIBUTE_NAME_MISSING_ERROR % command_text)
        end

        message_arguments = command_parts[1].strip()
        if message_arguments.empty?()
          raise TemplateError.new(
              "message arguments missing in '#{command_text}'")
        end

        add_attribute_message_command(attribute_name, message_arguments)
      end
    end

    def configure_attribute_message_commands(command_attributes)
      value = command_attributes.fetch(Command::ALT_MESSAGE, nil)
      if value != nil
        add_attribute_message_command(Command::ALT, value)
      end

      value = command_attributes.fetch(Command::ATTR_MESSAGE, nil)
      if value != nil
        add_attribute_message_commands(value)
      end

      value = command_attributes.fetch(Command::TITLE_MESSAGE, nil)
      if value != nil
        add_attribute_message_command(Command::TITLE, value)
      end

      value = command_attributes.fetch(Command::VALUE_MESSAGE, nil)
      if value != nil
        add_attribute_message_command(Command::VALUE, value)
      end
    end

    def configure_content(command_attributes, template_loader)
      @text_variable_name = command_attributes.fetch(Command::TEXT, nil)
      if @text_variable_name != nil
        @text_variable_name = @text_variable_name.to_sym()
        return
      end

      text_message_arguments =
          command_attributes.fetch(Command::TEXT_MESSAGE, nil)
      if text_message_arguments != nil
        @text_message = Message.new(text_message_arguments)
        return
      end

      uri = command_attributes.fetch(Command::INCLUDE, nil)
      if uri != nil
        @template = template_loader.load(uri)
      end
    end

    def configure_commands(command_attributes, template_loader)
      configure_attribute_commands(command_attributes)
      configure_attribute_message_commands(command_attributes)

      configure_content(command_attributes, template_loader)

      escape_xml_value = command_attributes.fetch(Command::ESCAPE_XML, nil)
      if escape_xml_value != nil
        @escape_xml = escape_xml_value != 'false' 
      end
    end

    # Checks if this renderer has a command.  If it has a command, then the
    # template builder should not discard it.
    def has_command()
      return !@attribute_commands.empty?()
    end

    # Checks if the element content will be rendered by a command.
    def has_dynamic_content()
      return @text_variable_name != nil ||
             @text_message != nil ||
             @template != nil
    end

    def execute_attribute_commands(render_context)
      if @attribute_commands.empty?()
        return @element.attributes()
      end

      # Copy the original attributes.  The commands modify the copy to
      # produce the attributes to render.
      render_attributes = @element.attributes().dup()
      @attribute_commands.each do |command|
        command.execute(render_context, render_attributes)
      end
      return render_attributes
    end

    def should_render_tag()
      return @remove_mode == nil || @remove_mode == RemoveMode::CONTENT
    end

    def render_start_tag(render_context)
      if should_render_tag()
        attributes = execute_attribute_commands(render_context)
        render_context << @element.render_start_tag(attributes)
      end
    end

    def render_end_tag(render_context)
      if should_render_tag()
        render_context << @element.render_end_tag()
      end
    end

    def should_render_content()
      return @remove_mode != RemoveMode::CONTENT
    end

    def render_content(render_context)
      if should_render_content()
        if @text_variable_name != nil
          value = render_context.get_display_value(@text_variable_name)
          text = render_context.escape_xml(value.to_s())
          render_context << text
        elsif @text_message != nil
          text = @text_message.format(render_context)
          text = render_context.escape_xml(text)
          render_context << text
        elsif @template != nil
          @template.render(render_context)
        else
          render_children(render_context)
        end
      end
    end

    def render(render_context)
      # Process the command to change the escaping mode.
      original_escape_xml_enabled = render_context.escape_xml_enabled()
      if @escape_xml != nil
        render_context.escape_xml_enabled = @escape_xml
      end

      render_start_tag(render_context)
      render_content(render_context)
      render_end_tag(render_context)

      # Restore the original escaping mode.
      if @escape_xml != nil
        render_context.escape_xml_enabled = original_escape_xml_enabled
      end
    end
  end

end; end
