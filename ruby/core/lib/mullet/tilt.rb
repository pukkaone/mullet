require 'mullet'
require 'tilt'

module Mullet; module Tilt

  # Registers the Mullet template engine in Tilt to handle file names with the
  # `html` extension.
  class MulletTemplate < ::Tilt::Template

    def initialize_engine
      @@loader = Mullet::HTML::TemplateLoader.new(nil)
      @@parser = Mullet::HTML::TemplateParser.new(@@loader)
    end

    def prepare
      if file
        @@loader.template_path = File.dirname(file)
      end

      @template = @@parser.parse(data)
    end

    def evaluate(scope, locals, &block)
      if block
        content = block.call()
        locals = locals.merge(content: content)
      end

      output = ''
      @template.execute(DefaultNestedScope.new(scope, locals), output)
      return output
    end

    ::Tilt.register self, :html
  end

end; end
