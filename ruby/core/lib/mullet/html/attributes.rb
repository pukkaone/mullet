module Mullet; module HTML

  # Maps attribute names to values.  Also renders attributes to HTML.
  class Attributes < Hash

    def escape_quote(value)
      return value.include?('"') ? value.gsub(/"/, '&#034;') : value
    end

    # Renders attributes to HTML syntax.
    #
    # @return rendered HTML
    def render()
      output = ''
      each do |key, value|
        output << ' ' << key << '="' << escape_quote(value) << '"'
      end
      return output
    end
  end

end; end
