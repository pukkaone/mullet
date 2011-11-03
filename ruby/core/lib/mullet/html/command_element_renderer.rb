module Mullet; module HTML

  # Answers true when asked if it has any command.
  class CommandElementRenderer < ElementRenderer

    # Constructor
    #
    # @param [Element] element
    #           element to render
    def initialize(element)
      super(element)
    end

    def has_command()
      return true
    end
  end

end; end
