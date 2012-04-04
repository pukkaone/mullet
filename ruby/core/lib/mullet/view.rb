require 'mullet/default_nested_scope'

module Mullet

  # Adapts model data for rendering in a template.  Applications will typically
  # define subclasses with attributes that will be referenced by name from the
  # templates.
  class View

    # Sets model to adapt.  Applications do not have to call this method
    # directly.  The template engine will call this method implicitly.
    #
    # @param data_objects
    #           scopes in outer to inner order
    def set_model(*data_objects)
      @model = DefaultNestedScope.new(*data_objects)
    end

    # Resolves variable name to value from the model.
    # 
    # @param [Symbol] name
    #           variable name
    # @return variable value
    def fetch(name)
      return @model.get_variable_value(name)
    end
  end

end
