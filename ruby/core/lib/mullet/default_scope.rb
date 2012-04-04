require 'mullet/scope'

module Mullet

  # Default scope implementation which resolves variable names to values by
  # reading from a data object. Given a variable name _key_, the following
  # mechanisms are tried in this order:
  #
  #   * If the variable name is `.`, then return the object.
  #   * If the object has a method named _key_ taking no parameters, then use
  #     the value returned from calling the method.
  #   * If the object has an instance variable named @_key_, then use the
  #     variable value.
  #   * If the object is a `Hash`, then use _key_ as the key to retrieve the
  #     value from the hash.
  #
  # If the value is a Proc, then use the value returned from calling it.
  class DefaultScope
    include Scope

    def initialize(data)
      @data = data
    end

    def fetch_impl(name)
      if name == :'.'
        return @data
      end

      # Does the variable name match a method name in the object?
      if @data.respond_to?(name)
        method = @data.method(name)
        if method.arity == 0
          return method.call()
        end
      end

      # Does the variable name match an instance variable name in the object?
      variable = :"@#{name}"
      if @data.instance_variable_defined?(variable)
        return @data.instance_variable_get(variable)
      end

      # Is the variable name a key in a Hash?
      if @data.respond_to?(:fetch)
        # If the key was not found, then try to find it as a String.
        return @data.fetch(name) {|k| @data.fetch(k.to_s(), NOT_FOUND) }
      end

      return NOT_FOUND
    end

    # Resolves variable name to value.
    # 
    # @param [Symbol] name
    #           variable name
    # @return variable value
    def get_variable_value(name)
      value = fetch_impl(name)
      if value.is_a?(Proc)
        value = value.call()
      end
      return value
    end
  end

end
