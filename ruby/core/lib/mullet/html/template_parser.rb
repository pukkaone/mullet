require 'mullet/html/filtered_element_handler'
require 'mullet/html/parser/simple_parser'
require 'mullet/html/template_builder'

module Mullet; module HTML

  class TemplateParser

    def initialize(loader)
      @loader = loader
    end

    # Parses template from string.
    #
    # @param [String] source
    #           string to parse
    # @return [Template] template
    def parse(source)
      template_builder = TemplateBuilder.new(@loader)
      parser = Parser::SimpleParser.new(template_builder)
      parser.parse(source)
      return template_builder.template
    end

    # Parses template from file.
    #
    # @param [String] file_name
    #           name of file containing template
    # @param [String] id
    #           If `nil`, then the template is the entire file, otherwise the
    #           template is the content of the element having an `id` attribute
    #           value equal to this argument.
    # @return [Template] template
    def parse_file(file_name, id)
      template_builder = TemplateBuilder.new(@loader)
      handler = (id == nil) ?
          template_builder : FilteredElementHandler.new(template_builder, id)

      parser = Parser::SimpleParser.new(handler)
      File.open(file_name) do |file|
        parser.parse(file)
      end

      return template_builder.template
    end
  end

end; end
