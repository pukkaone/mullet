require 'mullet/html/page_builder'
require 'nokogiri'

module Mullet; module HTML

  # Extracts content from an HTML page and renders it in a layout.  The layout
  # is a template given these variables:
  #
  # `title`
  # :   content of the `title` element from the page
  # `body`
  # :   content of the `body` element from the page
  #
  # The `body` variable typically contains HTML markup, so the layout must
  # use the `data-escape-xml="false"` command to prevent markup characters
  # being escaped when rendering the variable.
  class Layout

    # Constructor
    #
    # @param [Template] template
    #           layout using the template
    def initialize(template)
      @template = template
    end

    # Renders page data in a layout.
    #
    # @param [String] page_html
    #           content from this HTML page will be rendered in the layout
    # @param [#<<] output
    #           where to write rendered output
    def execute(page_html, output)
      page = parse_page(page_html)
      @template.execute(page, output)
    end

    private

    def parse_page(page_html)
      handler = PageBuilder.new()
      parser = Nokogiri::HTML::SAX::Parser.new(handler)
      parser.parse(page_html)
      return handler.page
    end
  end

end; end
