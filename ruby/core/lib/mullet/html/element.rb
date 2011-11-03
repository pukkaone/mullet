module Mullet; module HTML

  # HTML element
  class Element

    attr_reader :attributes

    # Constructor
    #
    # @param [String] local_name
    #           tag name without namespace prefix
    # @param [String] qualified_name
    #           tag name with namespace prefix
    # @param [Attributes] attributes
    #           attributes from template
    def initialize(local_name, qualified_name, attributes)
      @name = qualified_name.empty?() ? local_name : qualified_name
      @attributes = attributes
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
