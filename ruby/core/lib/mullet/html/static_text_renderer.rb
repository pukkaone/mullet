module Mullet; module HTML

  # Renders static markup and text. May output an unclosed start tag under the
  # assumption a subsequent static text renderer outputs the matching end tag.
  class StaticTextRenderer

    # Constructor
    #
    # @param [String] text
    #           rendered tags and text
    def initialize(text)
      @text = text
    end

    def render(render_context)
      render_context << @text
    end
  end

end; end
