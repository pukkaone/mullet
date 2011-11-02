require 'mullet/default_model'

module Mullet

  # Composite model that combines models in nested scopes. Tries each model in
  # sequence until a value is successfully resolved.
  class DefaultNestedModel
    include Model

    # Constructor
    # 
    # @param dataObjects
    #           scopes in outer to inner order
    def initialize(*dataObjects)
      @scopes = []
      dataObjects.each {|data| push_scope(data) }
    end

    # Resolves variable name to value.
    # 
    # @param [Symbol] key
    #           variable name
    # @return variable value
    def fetch(key)
      @scopes.reverse_each do |scope|
        value = scope.fetch(key)
        if value != Model::NOT_FOUND
          return value
        end
      end

      return Model::NOT_FOUND
    end

    # Adds a nested scope to search in subsequent lookups.
    #
    # @param data
    #           data object
    def push_scope(data)
      @scopes.push(data.is_a?(Model) ? data : DefaultModel.new(data))
    end

    # Removes innermost nested scope.
    def pop_scope()
      @scopes.pop()
    end
  end

end
