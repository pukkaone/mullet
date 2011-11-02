require 'nokogiri'
require 'mullet/html/command'
require 'set'

module Mullet; module HTML

  # Handles SAX events to build a template.
  class TemplateBuilder < Nokogiri::XML::SAX::Document
    include Command

    XMLNS_ATTRIBUTE_PREFIX = "xmlns:"
    COMMANDS = [
        ATTRIBUTE,
        ATTRIBUTE_MESSAGE,
        CONTENT,
        ESCAPE_XML,
        FOR,
        IF,
        INCLUDE,
        TEXT,
        TEXT_MESSAGE,
        UNLESS].to_set
    START_CDATA = "<![CDATA["
    END_CDATA = "]]>"

    @loader = nil

    # This is a stack of elements where this handler has seen the start tag and
    # not yet seen the end tag.
    @openElements = []

    # stack of current containers to add renderers to
    @containers = []
    
    @staticText = ""
    @template = nil
    
    # Constructor
    #
    # @param [TemplateLoader] loader
    #           template loader to use to load included template files
    def initialize(loader)
      @loader = loader
    end

    # Adds renderer to current container.
    #
    # @param [#render] renderer
    #           renderer to add
    def add_child(renderer)
      @containers.last.add_child(renderer)
    end

    # Deletes renderer from current container.
    #
    # @param [#render] renderer
    #           renderer to delete
    def delete_child(renderer)
      @containers.last.delete_child(renderer)
    end

    # Partitions the attributes into ordinary and command attributes.
    #
    # @param [Array] attributes
    #           input attributes
    # @param [Hash] ns
    #           hash of namespace prefix to uri mappings
    # @param [#store] ordinaryAttributes
    #           hash will receive name to value mappings for ordinary attributes
    # @param [#store] commandAttributes
    #           hash will receive name to value mappings for command attributes
    # @return [Boolean] true if any command attribute found
    def find_commands(attributes, ns, ordinaryAttributes, commandAttributes)
      foundCommand = false
      attributes.each do |attr|
        if attr.uri == NAMESPACE_URI
          commandName = attr.localname
          if !COMMANDS.contains(commandName)
            raise TemplateException("invalid command '#{commandName}'")
          end
          commandAttributes.store(commandName, attr.value)
          foundCommand = true
        else
          attributeName = [attr.prefix, attr.localname].compact.join(':')
          ordinaryAttributes.store(attributeName, attr.value)
        end
      end

      ns.each do |prefix, uri|
        if uri != NAMESPACE_URI
          attributeName = ['xmlns', prefix].compact.join(':')
          ordinaryAttributes.store(attributeName, uri)
        end
      end

      return foundCommand
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
