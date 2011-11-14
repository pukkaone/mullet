require 'mullet/html/parser/open_element'
require 'mullet/html/parser/tokenizer'
require 'mullet/template_error'

module Mullet; module HTML; module Parser

  # SAX parser that reports elements in the order they appear in the input.
  # Does not move elements to another parent.
  class SimpleParser

    # special namespace URI indicating parser generated an implicit end tag
    IMPLICIT_END_TAG_NS_URI = 'http://pukkaone.github.com/mullet/end-tag'

    # Constructor
    #
    # @param [DefaultHandler] handler
    #           object to receive events
    def initialize(handler)
      @handler = handler
    end

    def parse(input)
      @open_element = nil
      @handler.start_document()

      tokenizer = Tokenizer.new(input)
      tokenizer.each do |token|
        case token[:type]
        when :Doctype
          doctype(token[:name], token[:publicId], token[:systemId])
        when :StartTag
          start_tag(token[:name], token[:data])
        when :EndTag
          end_tag(token[:name])
        when :Characters, :SpaceCharacters
          characters(token[:data])
        when :CDATA
          cdata(token[:data])
        when :Comment
          comment(token[:data])
        end
      end

      @handler.end_document()
    end

    private

    def doctype(name, public_id, system_id)
      @handler.doctype(name, public_id, system_id)
    end

    def start_tag(name, attributes)
      # Push open element onto stack.
      element = OpenElement.new(@open_element, name, attributes)
      @open_element = element

      @handler.start_element_namespace(
          element.local_name,
          element.attributes,
          element.prefix,
          element.uri,
          element.namespace_declarations)
    end

    def pop_open_element()
      element = @open_element
      if @open_element != nil
        @open_element = @open_element.parent
      end
      return element
    end

    def raise_mismatched_end_tag(name)
      raise TemplateError.new("End tag </#{name}> has no matching start tag")
    end

    def end_tag(name)
      element = pop_open_element()
      if element == nil
        raise_mismatched_end_tag(name)
      end

      # Generate implicit end tags up to this element.
      while element.qualified_name != name
        # KLUDGE: Indicate a generated implicit end tag by passing a special
        # value for the namespace URI.
        @handler.end_element_namespace(name, nil, IMPLICIT_END_TAG_NS_URI)

        element = pop_open_element()
        if element == nil
          raise_mismatched_end_tag(name)
        end
      end

      @handler.end_element_namespace(element.qualified_name, nil, nil)
    end

    def characters(data)
      @handler.characters(data)
    end

    def cdata(data)
      @handler.cdata_block(data)
    end

    def processing_instruction(data)
      @handler.processing_instruction(data)
    end

    def comment(data)
      # FIXME: The HTML5 tokenizer treats <? as a bogus comment and emits it as
      # a comment.  If the comment begins with ? then turn it into a processing
      # instruction.  The problem is any legitimate comment beginning with ?
      # will also be turned into a processing instruction.
      if data.start_with?('?')
        last_index = data.length() - 1
        if last_index > 0 && data[last_index] == '?'
          last_index -= 1
        end
        processing_instruction(data[1..last_index])
      else
        @handler.comment(data)
      end
    end
  end

end; end; end
