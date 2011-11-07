require 'nokogiri'
require 'mullet/html/command'
require 'mullet/template_error'
require 'set'

module Mullet; module HTML

  # Handles SAX events to build a template.
  class TemplateBuilder < Nokogiri::XML::SAX::Document
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

    @loader = nil
    
    # Constructor
    #
    # @param [TemplateLoader] loader
    #           template loader to use to load included template files
    def initialize(loader)
      @loader = loader

      # Stack of elements where this handler has seen the start tag and not yet
      # seen the end tag.
      @openElements = []

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
        if attr.local_name().start_with(DATA_PREFIX)
          command_name = attr.localname().substring(DATA_PREFIX.length())
          if COMMANDS.include?(command_name)
            command_attributes.store(command_name, attr.value)
            found_command = true
          end
        elsif attr.uri == NAMESPACE_URI
          command_name = attr.localname()
          if !COMMANDS.include?(command_name)
            raise TemplateError("invalid command '#{command_name}'")
          end
          command_attributes.store(command_name, attr.value)
          found_command = true
        else
          attribute_name = [attr.prefix, attr.localname].compact.join(':')
          ordinary_attributes.store(attribute_name, attr.value)
        end
      end

      ns.each do |prefix, uri|
        if uri != NAMESPACE_URI
          attribute_name = ['xmlns', prefix].compact.join(':')
          ordinary_attributes.store(attribute_name, uri)
        end
      end

      return found_command
    end

    def render_start_tag(name, attributes, prefix, uri, namespaceDecls)
      tag = "<"
      if prefix
        tag << prefix << ":"
      end
      tag << name

      attributes.each do |attribute|
        tag << " "
        if attribute.prefix
          tag << attribute.prefix << ":"
        end
        tag = attribute.localname << '="' << attribute.value << '"'
      end

      namespaceDecls.each do |namespaceDecl|
        uri = namespaceDecl[1] 
        if uri != TEMPLATE_NAMESPACE_URI
          tag << " xmlns:" << namespaceDecl[0] << '="' << uri << '"'
        end
      end

      tag << ">"
      return tag
    end

    def start_element_namespace(name, attributes, prefix, uri, ns)
      puts "start element #{name} #{attributes} #{prefix} #{uri} #{ns}"
      templateAttributes = attributes.select do |attribute|
        attribute.uri == TEMPLATE_NAMESPACE_URI
      end
      if templateAttributes.empty?
        puts render_start_tag(name, attributes, prefix, uri, ns)
      else
        puts "attributes #{templateAttributes}"
      end
    end

    def end_document
      puts "the document has ended"
    end
  end

end; end
