require 'mullet'
require 'tilt'

# Registers the Mullet template engine in Tilt to handle file names with the
# `html` extension.
module Mullet; module Tilt

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
      @template.execute(DefaultNestedModel.new(scope, locals), output)
      return output
    end

    ::Tilt.register self, :html
  end

end; end
