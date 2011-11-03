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
    # @param [Proc] missingValueStrategy
    #           executed on attempt to render a variable that was not found 
    # @param [Proc] nilValueStrategy
    #           executed on attempt to render nil value
    # @param [#<<] output
    #           IO stream where rendered output will be written
    def initialize(data, missingValueStrategy, nilValueStrategy, output)
      @model = data.is_a?(Model) ? data : DefaultNestedModel.new(data)
      @missingValueStrategy = missingValueStrategy
      @nilValueStrategy = nilValueStrategy
      @output = output
      @escape_xml_enabled = true
    end

    # Escapes characters that could be interpreted as XML markup if enabled.
    #
    # @param [String] input
    #           input string
    # @return escaped string, or the input string if escaping is disabled.
    def escape_xml(key)
      return @escape_xml_enabled ? CGI.escape_html(input) : input
    end

    # Resolves variable name to value.
    #
    # @param [Symbol] key
    #           variable name
    # @return value
    def fetch(key)
      return @model.fetch(key)
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
        value = @missingValueStrategy.call(key)
      end
      if value == nil
        value = @nilValueStrategy.call(key)
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
