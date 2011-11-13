require 'mullet/html/parser/attribute'
require 'mullet/html/parser/constants'
require 'mullet/html/parser/input_stream'

module Mullet; module HTML; module Parser

  # Element where the parser has seen the start tag but not yet seen the end
  # tag.
  class OpenElement

    XMLNS_ATTRIBUTE = 'xmlns'
    XMLNS_ATTRIBUTE_PREFIX = XMLNS_ATTRIBUTE + ':'
    DEFAULT_NS_PREFIX = ''

    attr_reader :parent, :qualified_name, :prefix, :local_name, :uri
    attr_reader :attributes

    def initialize(parent, qualified_name, raw_attributes)
      # namespace prefix to URI map
      @namespace_declarations = Hash.new()

      @parent = parent

      processNamespaceDeclarations(raw_attributes)
      @qualified_name = qualified_name
      @prefix, @local_name = extract_prefix_and_local_name(qualified_name)
      @uri = resolve_prefix_to_uri(@prefix)

      @attributes = to_namespace_aware_attributes(raw_attributes)
    end

    def resolve_prefix_to_uri(prefix)
      uri = @namespace_declarations.fetch(prefix, nil)
      if uri == nil && @parent != nil
        return @parent.resolve_prefix_to_uri(prefix)
      end
      return uri
    end

    private

    def declare_namespace(raw_attribute_name, uri)
      if raw_attribute_name.start_with?(XMLNS_ATTRIBUTE_PREFIX)
        prefix = raw_attribute_name[XMLNS_ATTRIBUTE_PREFIX.length()..-1]
      else
        prefix = DEFAULT_NS_PREFIX
      end
      @namespace_declarations.store(prefix, uri)
    end

    def process_namespace_declarations(raw_attributes)
      raw_attributes.each do |attribute_name, attribute_value|
        if attribute_name.start_with?(XMLNS_ATTRIBUTE)
          declare_namespace(attribute_name, attribute_value)
        end
      end
    end

    def extract_prefix_and_local_name(qualified_name)
      name_parts = qualified_name.split(':', 2)
      prefix = (name_parts.size() > 1) ? name_parts[0] : DEFAULT_NS_PREFIX
      local_name = (name_parts.size() > 1) ? name_parts[1] : qualified_name
      return [prefix, local_name]
    end

    def extract_namespace_prefix(qualified_name)
      colon_index = qualified_name.index(':')
      return (colon_index != nil) ?
          qualified_name[0...colon_index] : DEFAULT_NS_PREFIX;
    end

    def to_namespace_aware_attributes(raw_attributes)
      attributes = []
      raw_attributes.each do |qualified_name, attribute_value|
        prefix, local_name = extract_prefix_and_local_name(qualified_name)
        uri = resolve_prefix_to_uri(prefix)
        attributes << Attribute.new(local_name, prefix, uri, attribute_value)
      end

      return attributes
    end
  end

end; end; end
