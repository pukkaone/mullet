require 'mullet/template_error'

module Mullet; module HTML

  # Specifies what to remove.
  class RemoveMode
    # remove tag, and preserve children of element
    TAG = RemoveMode.new()

    # preserve tag, and remove children of element
    CONTENT = RemoveMode.new()

    # remove tag and children of element
    ELEMENT = RemoveMode.new()
    
    def self.value_of(argument)
      string = argument.downcase()
      if string == 'element'
        return ELEMENT
      elsif string == 'tag'
        return TAG
      elsif string == 'content'
        return CONTENT
      else
        raise TemplateError.new("invalid remove argument '%{argument}'")
      end
    end
  end

end; end
