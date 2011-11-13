require 'mullet/html/command'
require 'mullet/template_error'
require 'nokogiri'
require 'set'

module Mullet; module HTML

  # Proxy for a SAX event handler that forwards events only while the
  # parser is within an element identified by an `id` attribute.
  class FilteredElementHandler < Nokogiri::XML::SAX::Document
    ID = 'id'

    # Constructor
    #
    # @param [Document] handler
    #           event handler to forward events to
    # @param [String] id
    #           id attribute value
    def initialize(handler, id)
      @handler = handler
      @id = id
      @depth = 0
      @found_id = false
    end

    def should_forward()
      return @depth > 0
    end

    def start_document()
      @handler.start_document()
    end

    def end_document()
      @handler.end_document()

      if !@found_id
        raise TemplateError.new("element with attribute id='#{@id}' not found")
      end
    end

    def start_element_namespace(name, attributes, prefix, uri, namespaces)
      if should_forward()
        @depth += 1
        return @handler.start_element_namespace(
            name, attributes, prefix, uri, namespaces)
      end

      value = attributes.each do |attr|
        qualified_name = [attr.prefix, attr.localname].compact.join(':')
        if qualified_name == ID && attr.value == @id
          # Enable event forwarding.
          @depth = 1
          @found_id = true
        end
      end
    end

    def end_element_namespace(name, prefix, uri)
      if should_forward()
        @depth -= 1
        if @depth > 0
          @handler.end_element_namespace(name, prefix, uri)
        end
      end
    end

    def characters(data)
      if should_forward()
        @handler.characters(data)
      end
    end

    def cdata_block(data)
      if should_forward()
        @handler.cdata_block(data)
      end
    end

    def comment(data)
      if should_forward()
        @handler.comment(data)
      end
    end
  end

end; end
