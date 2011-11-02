require 'mullet/model'

module Mullet

  # Default model implementation which resolves variable names to values by
  # reading from a data object. Given a variable name _key_, the following
  # mechanisms are tried in this order:
  #
  #   * If the variable name is `this`, then return the object.
  #   * If the object is a `Hash`, then use _key_ as the key to retrieve the
  #     value from the hash.
  #   * If the object has a method named _key_ taking no parameters, then use
  #     the value returned from calling the method.
  #   * If the object has an instance variable named @_key_, then use the
  #     variable value.
  #
  # If the value is a Proc, then use the value returned from calling it.
  class DefaultModel
    include Model

    def initialize(data)
      @data = data
    end

    def fetch_impl(key)
      if key == :this
        return @data
      end

      # Is the variable name a key in a Hash?
      if @data.respond_to?(:fetch)
        # Call the block if the key is not found.
        return @data.fetch(key) {|k| @data.fetch(k.to_s(), NOT_FOUND) }
      end

      # Does the variable name match a method name in the object?
      if @data.respond_to?(key)
        method = @data.method(key)
        if method.arity == 0
          return method.call()
        end
      end

      # Does the variable name match an instance variable name in the object?
      variable = :"@#{key}"
      if @data.instance_variable_defined?(variable)
        return @data.instance_variable_get(variable)
      end

      return NOT_FOUND
    end

    # Resolves variable name to value.
    # 
    # @param [Symbol] key
    #           variable name
    # @return variable value
    def fetch(key)
      value = fetch_impl(key)
      if value.is_a?(Proc)
        value = value.call()
      end
      return value
    end
  end

end
