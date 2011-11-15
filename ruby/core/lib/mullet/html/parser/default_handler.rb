require 'nokogiri'

module Mullet; module HTML; module Parser

  # SAX event handler implementation which can be subclassed.
  class DefaultHandler < Nokogiri::XML::SAX::Document

    # Called at a DOCTYPE declaration.
    #
    # @param [String] name
    #           document type name
    # @param [String] public_id
    #           public identifier, or `nil` if not specified
    # @param [String] system_id
    #           system identifier, or `nil` if not specified
    def doctype(name, public_id, system_id)
    end

    # Called at a processing instruction.
    #
    # @param [String] data
    #           processing instruction data
    def processing_instruction(data)
    end
  end

end; end; end
