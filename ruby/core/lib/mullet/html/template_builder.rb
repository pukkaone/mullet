require 'mullet/html/attributes'
require 'mullet/html/command'
require 'mullet/html/element'
require 'mullet/html/element_renderer'
require 'mullet/html/for_element_renderer'
require 'mullet/html/if_element_renderer'
require 'mullet/html/parser/default_handler'
require 'mullet/html/parser/simple_parser'
require 'mullet/html/remove_mode'
require 'mullet/html/static_text_renderer'
require 'mullet/html/template'
require 'mullet/html/unless_element_renderer'
require 'mullet/template_error'
require 'set'

module Mullet; module HTML

  # Handles SAX events to build a template.
  class TemplateBuilder < Parser::DefaultHandler
    include Command

    COMMANDS = Set[
        ACTION,
        ALT,
        ALT_MESSAGE,
        ATTR,
        ATTR_MESSAGE,
        ESCAPE_XML,
        FOR,
        HREF,
        IF,
        INCLUDE,
        REMOVE,
        SRC,
        TEXT,
        TEXT_MESSAGE,
        TITLE,
        TITLE_MESSAGE,
        UNLESS,
        VALUE,
        VALUE_MESSAGE]
    START_CDATA = '<![CDATA['
    END_CDATA = ']]>'

    attr_reader :template

    # Constructor
    #
    # @param [TemplateLoader] loader
    #           template loader to use to load included template files
    def initialize(loader)
      @loader = loader

      # Stack of elements where this handler has seen the start tag and not yet
      # seen the end tag.
      @open_elements = []

      # stack of current containers to add renderers to
      @containers = []
    
      @static_text = ''
      @template = nil
    end

    # Adds renderer to current container.
    #
    # @param [#render] renderer
    #           renderer to add
    def add_child(renderer)
      @containers.last().add_child(renderer)
    end

    # Deletes renderer from current container.
    #
    # @param [#render] renderer
    #           renderer to delete
    def delete_child(renderer)
      @containers.last().delete_child(renderer)
    end

    # Partitions the attributes into ordinary and command attributes.
    #
    # @param [Array] attributes
    #           input attributes
    # @param [Hash] ns
    #           hash of namespace prefix to uri mappings
    # @param [#store] ordinary_attributes
    #           hash will receive name to value mappings for ordinary attributes
    # @param [#store] command_attributes
    #           hash will receive name to value mappings for command attributes
    # @return [Boolean] true if any command attribute found
    def find_commands(attributes, ns, ordinary_attributes, command_attributes)
      found_command = false
      attributes.each do |attr|
        if attr.localname.start_with?(DATA_PREFIX)
          command_name = attr.localname.slice(DATA_PREFIX.length()..-1)
          if COMMANDS.include?(command_name)
            command_attributes.store(command_name, attr.value)
            found_command = true
          end
        elsif attr.uri == NAMESPACE_URI || attr.prefix == NAMESPACE_PREFIX
          command_name = attr.localname
          if !COMMANDS.include?(command_name)
            raise TemplateError("invalid command '#{command_name}'")
          end
          command_attributes.store(command_name, attr.value)
          found_command = true
        else
          attribute_name = [attr.prefix, attr.localname].compact().join(':')
          ordinary_attributes.store(attribute_name, attr.value)
        end
      end

      ns.each do |prefix, uri|
        if uri != NAMESPACE_URI
          attribute_name = ['xmlns', prefix].compact().join(':')
          ordinary_attributes.store(attribute_name, uri)
        end
      end

      return found_command
    end

    def append_static_text(data)
      @static_text << data
    end

    def end_static_text()
      if !@static_text.empty?()
        add_child(StaticTextRenderer.new(@static_text))
        @static_text = ''
      end
    end

    # Marks the most deeply nested open element as having content.
    def set_has_content()
      if !@open_elements.empty?()
        @open_elements.last().has_content = true
      end
    end

    def start_document()
      @template = Template.new()
      @containers.push(@template)
    end

    def end_document()
      end_static_text()
    end

    def doctype(name, public_id, system_id)
      text = "<!DOCTYPE #{name}"

      if public_id
        text << %( PUBLIC "#{public_id}")
      end

      if system_id
        text << %( "#{system_id}")
      end

      text << '>'
      append_static_text(text)
    end

    def create_element_renderer(element, command_attributes)
      renderer = nil

      variable_name = command_attributes.fetch(IF, nil)
      if variable_name != nil
        renderer = IfElementRenderer.new(element, variable_name)
      end

      if renderer == nil
        variable_name = command_attributes.fetch(UNLESS, nil)
        if variable_name != nil
          renderer = UnlessElementRenderer.new(element, variable_name)
        end
      end

      if renderer == nil
        variable_name = command_attributes.fetch(FOR, nil)
        if variable_name != nil
          renderer = ForElementRenderer.new(element, variable_name)
        end
      end

      if renderer == nil
        renderer = ElementRenderer.new(element)
      end

      renderer.configure_commands(command_attributes, @loader)
      return renderer
    end

    def start_element_namespace(name, attributes, prefix, uri, ns)
      set_has_content()

      ordinary_attributes = Attributes.new()
      command_attributes = Attributes.new()
      found_command = find_commands(
          attributes, ns, ordinary_attributes, command_attributes)

      qualified_name = [prefix, name].compact().join(':')
      element = Element.new(qualified_name, ordinary_attributes)
      @open_elements.push(element)

      if found_command
        end_static_text()

        element.has_command = true
        renderer = create_element_renderer(element, command_attributes)
        add_child(renderer)

        @containers.push(renderer)
      else
        append_static_text(element.render_start_tag(element.attributes))
      end
    end

    def configure_remove_command(element, renderer)
      case renderer.remove_mode
      when RemoveMode::TAG
        if !renderer.has_command && !renderer.has_dynamic_content
          # Discard tag, but preserve the children.
          delete_child(renderer)
          renderer.children.each do |child|
            add_child(child)
          end
        end
      when RemoveMode::CONTENT
        if !renderer.has_command
          # Discard children. Statically render the tag.
          delete_child(renderer)

          append_static_text(element.render_start_tag(element.attributes))
          append_static_text(element.render_end_tag())
        end
      when RemoveMode::ELEMENT
        # Discard element and all its content.
        delete_child(renderer)
      end
    end

    def end_element_namespace(name, prefix, uri)
      element = @open_elements.pop()
      if element.has_command
        end_static_text()

        renderer = @containers.pop()
        if renderer.has_dynamic_content
          # Discard children because the content will be replaced at render
          # time.
          renderer.clear_children()
        end

        configure_remove_command(element, renderer)
      elsif uri != Parser::SimpleParser::IMPLICIT_END_TAG_NS_URI
        append_static_text(element.render_end_tag())
      end
    end

    def characters(data)
      set_has_content()
      append_static_text(data)
    end

    def cdata_block(data)
      append_static_text(START_CDATA)
      characters(data)
      append_static_text(END_CDATA)
    end

    def comment(data)
      append_static_text('<!--')
      characters(data)
      append_static_text('-->')
    end

    def processing_instruction(data)
      append_static_text('<?')
      characters(data)
      append_static_text('?>')
    end
  end

end; end
