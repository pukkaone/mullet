require 'mullet/html/template'
require 'mullet/html/template_parser'

module Mullet; module HTML

  # Loads templates from files, and caches them for fast retrieval of already
  # loaded templates.
  #
  # By default, templates render an empty string when a variable is not found
  # or its value is null. Call the `on_missing` and `on_nil` methods to
  # configure how templates loaded by this loader should handle missing and nil
  # values respectively.
  class TemplateLoader
    
    attr_accessor :template_path

    # Constructor
    #
    # @param [String] template_path
    #           name of directory to load templates from
    def initialize(template_path)
      @template_path = template_path
      @template_cache = Hash.new()
      @parser = TemplateParser.new(self)
      @on_missing = Template::RETURN_EMPTY_STRING
      @on_nil = Template::RETURN_EMPTY_STRING
    end
    
    # Sets block to execute on attempt to render a variable that was not found.
    #
    # @param [Proc] strategy
    #           The value returned from block will be rendered.
    # @return [TemplateLoader] this object to allow method call chaining
    def on_missing(strategy)
      @on_missing = strategy
      return self
    end

    # Sets block to execute on attempt to render a nil value.
    #
    # @param [Proc] strategy
    #           The value returned from block will be rendered.
    # @return [TemplateLoader] this object to allow method call chaining
    def on_nil(strategy)
      @on_nil = strategy
      return self
    end

    # Loads named template.
    #
    # @param [String] uri
    #           file name optionally followed by `#`_id_
    def load(uri)
      id = nil
      hash_index = uri.index('#')
      if hash_index
        id = uri[(hash_index + 1)..-1]
        uri = uri[0...hash_index]
      end

      return load_file(uri, id)
    end

    private

    def get_cache_key(file_name, id)
      cache_key = File.join(@template_path, file_name)
      if id != nil
        cache_key << '#' << id
      end
      return cache_key
    end

    def parse_file(file_name, id)
      template_file = File.join(@template_path, file_name)
      template = @parser.parse_file(template_file, id)

      template.on_missing(@on_missing).on_nil(@on_nil)
      return template
    end

    def load_file(file_name, id)
      cache_key = get_cache_key(file_name, id)
      template = @template_cache.fetch(cache_key, nil)
      if template == nil
        template = parse_file(file_name, id)
        @template_cache.store(cache_key, template)
      end
      return template
    end
  end

end; end
