module Mullet

  # A model responds to the method `fetch` taking a variable name argument and
  # returning the variable value.  If the variable name is not found, it
  # returns the value `NOT_FOUND` instead of raising an exception.  A model
  # class must include this module because the implementation calls
  # `is_a?(Model)` to determine if an object satisfies the concept of a model.
  module Model

    # special value indicating variable name was not found
    NOT_FOUND = Object.new()
  end

end
