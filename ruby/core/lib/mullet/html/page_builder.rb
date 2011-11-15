require 'mullet/html/attributes'
require 'mullet/html/command'
require 'mullet/html/element'
require 'mullet/html/element_renderer'
require 'mullet/html/for_element_renderer'
require 'mullet/html/if_element_renderer'
require 'mullet/html/parser/default_handler'
require 'mullet/html/parser/simple_parser'
require 'mullet/html/remove_mode'
require 'mullet/html/static_text_renderer'
require 'mullet/html/template'
require 'mullet/html/unless_element_renderer'
require 'mullet/template_error'
require 'set'

module Mullet; module HTML

  # Handles SAX events to extract content from an HTML page.
  class PageBuilder < Parser::DefaultHandler

    HEAD = 'head'
    TITLE = 'title'
    BODY = 'body'
    START_CDATA = '<![CDATA['
    END_CDATA = ']]>'

    # TODO: Write an alternative implementation where we don't have to know
    # which HTML elements have an empty content model.
    EMPTY_CONTENT_ELEMENTS = Set[
        'br',
        'hr',
        'img',
        'input']

    attr_reader :page

    def start_document()
      # number of open head elements
      @head_count = 0

      # Count of nested open elements where this handler is extracting their
      # inner HTML.
      @inner_depth = 0
    
      @page = Hash.new()
    end

    def start_element(name, attributes)
      if name == HEAD
        @head_count += 1
      end

      if extracting_inner_html?()
        @inner_depth += 1
        render_start_tag(name, attributes)
        return
      end

      if @head_count > 0 && name == TITLE
        start_extracting_inner_html()
        return
      end

      if name == BODY
        start_extracting_inner_html()
      end
    end

    def end_element(name)
      if name == HEAD
        @head_count -= 1
      end

      if extracting_inner_html?()
        @inner_depth -= 1
        if extracting_inner_html?() && !EMPTY_CONTENT_ELEMENTS.include?(name)
          render_end_tag(name)
        end
 
      end

      if @head_count > 0 && name == TITLE
        @page.store(:title, @inner_html)
        return
      end

      if name == BODY
        @page.store(:body, @inner_html)
      end
    end

    def characters(data)
      if extracting_inner_html?()
        append(data)
      end
    end

    def cdata_block(data)
      if extracting_inner_html?()
        append(data)
      end
    end

    def comment(data)
      if extracting_inner_html?()
        append("<!--#{data}-->")
      end
    end

    def processing_instruction(data)
      if extracting_inner_html?()
        append("<?#{data}?>")
      end
    end

    private

    def extracting_inner_html?()
      return @inner_depth > 0
    end

    def start_extracting_inner_html()
      @inner_depth = 1
      @inner_html = ''
    end

    def append(data)
      @inner_html << data
    end

    def escape_quote(value)
      return value.include?('"') ? value.gsub(/"/, '&#34;') : value
    end

    def render_start_tag(tag_name, attributes)
      append("<#{tag_name}")

      attributes.each do |attribute|
        qualified_name =
            [attribute.prefix, attribute.localname].compact().join(':')
        append(%( #{qualified_name}="#{escape_quote(attribute.value)}"))
      end

      append('>')
    end

    def render_end_tag(name)
      append("</#{name}>")
    end
  end

end; end
