require 'cgi'
require 'mullet/default_nested_model'

module Mullet

  # Holds the rendering context to reduce the number of parameters passed to
  # render methods.
  class RenderContext

    attr_accessor :escape_xml_enabled

    # Constructor.
    #
    # @param [Object] data
    #           provides data to render
    # @param [Proc] missing_value_strategy
    #           executed on attempt to render a variable that was not found 
    # @param [Proc] nil_value_strategy
    #           executed on attempt to render nil value
    # @param [#<<] output
    #           where to write rendered output
    def initialize(data, missing_value_strategy, nil_value_strategy, output)
      @model = data.is_a?(DefaultNestedModel) ?
          data : DefaultNestedModel.new(data)
      @on_missing = missing_value_strategy
      @on_nil = nil_value_strategy
      @output = output
      @escape_xml_enabled = true
    end

    # Escapes characters that could be interpreted as XML markup if enabled.
    #
    # @param [String] input
    #           input string
    # @return escaped string, or the input string if escaping is disabled.
    def escape_xml(input)
      return @escape_xml_enabled ? CGI.escape_html(input) : input
    end

    # Resolves variable name to value.
    #
    # @param [Symbol] name
    #           variable name
    # @return value
    def get_variable_value(name)
      return @model.get_variable_value(name)
    end

    # Adds a nested scope to search in subsequent lookups.
    #
    # @param data
    #           data object
    def push_scope(data)
      @model.push_scope(data)
    end

    # Removes innermost nested scope.
    def pop_scope()
      @model.pop_scope()
    end

    # Gets model value that is intended for display in the rendered output.
    # Applies configured strategies for handling missing and nil values.
    #
    # @param [Symbol] key
    #           variable name
    # @return value
    def get_display_value(key)
      value = @model.fetch(key)
      if value == Model::NOT_FOUND
        value = @on_missing.call(key)
      end
      if value == nil
        value = @on_nil.call(key)
      end
      return value
    end

    # Writes rendered output.
    #
    # @param [String] str
    #           string to write
    # @return this object
    def <<(str)
      @output << str
      return self
    end
  end

end
