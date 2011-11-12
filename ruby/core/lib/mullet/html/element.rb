module Mullet; module HTML

  # HTML element
  class Element

    # attributes from template
    attr_reader :attributes

    # true if element has any child element or text content
    attr_accessor :has_content

    # true if element has any template command
    attr_accessor :has_command

    # Constructor
    #
    # @param [String] qualified_name
    #           tag name with namespace prefix
    # @param [Attributes] attributes
    #           attributes from template
    def initialize(qualified_name, attributes)
      @name = qualified_name
      @attributes = attributes
      @has_content = false
      @has_command = false
    end

    # Renders start tag
    #
    # @param [Attributes] attributes
    #           attributes to render
    def render_start_tag(attributes)
      return "<#{@name}#{attributes.render()}>"
    end

    def render_end_tag()
      return "</#{@name}>"
    end
  end

end; end
