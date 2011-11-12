require 'mullet/html/template_builder'
require 'nokogiri'

module Mullet; module HTML

  class TemplateParser

    def initialize(loader)
      @loader = loader
    end

    def parse(file_name, id)
      template_builder = TemplateBuilder.new(@loader)
      handler = (id == nil) ?
          template_builder : FilteredElementHandler.new(template_builder, id)

      parser = Nokogiri::HTML::SAX::Parser.new(handler)
      file = File.open(file_name)
      parser.parse(file)
      file.close()

      return template_builder.template
    end
  end

end; end
