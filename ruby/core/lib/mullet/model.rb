module Mullet

  # A model responds to the method `get_variable_value` taking a variable name
  # argument and returning the variable value.  
  module Model

    # special value indicating variable name was not found
    NOT_FOUND = Object.new()
  end

end
