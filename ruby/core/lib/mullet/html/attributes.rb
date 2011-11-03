require 'cgi'

module Mullet; module HTML

  # Maps attribute names to values.  Also renders attributes to HTML.
  class Attributes < Hash

    def escape_quote(value)
      return value.include?('"') ? value.gsub(/"/, '&#034;') : value
    end

    # Renders attributes to HTML syntax.
    #
    # @param [@<<] output
    #           where to write rendered output
    def render(output)
      each do |key, value|
        output << ' ' << key << '="' << escape_quote(value) << '"'
      end
    end
  end

end; end
