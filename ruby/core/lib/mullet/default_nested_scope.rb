require 'mullet/default_scope'

module Mullet

  # Composite scope that combines scopes in nested scopes. Tries each scope in
  # sequence until a value is successfully resolved.
  class DefaultNestedScope
    include Scope

    # Constructor
    #
    # @param data_objects
    #           scopes in outer to inner order
    def initialize(*data_objects)
      @scopes = []
      data_objects.each {|data| push_scope(data) }
    end

    # Resolves variable name to value.
    #
    # @param [Symbol] name
    #           variable name
    # @return variable value
    def get_variable_value(name)
      @scopes.reverse_each do |scope|
        value = scope.get_variable_value(name)
        if value != NOT_FOUND
          return value
        end
      end

      return NOT_FOUND
    end

    # Adds new innermost nested scope.
    #
    # @param data
    #           data object
    def push_scope(data)
      @scopes.push(
          data.respond_to?(:get_variable_value) ? data : DefaultScope.new(data))
    end

    # Removes innermost nested scope.
    def pop_scope()
      @scopes.pop()
    end
  end

end
