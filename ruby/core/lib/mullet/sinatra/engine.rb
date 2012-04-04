require 'mullet'

module Mullet; module Sinatra

  # Loads view classes and template files.
  class Engine

    def initialize()
      @class_cache = Hash.new()
      @loader = Mullet::HTML::TemplateLoader.new(nil)
    end

    # Gets template.
    #
    # @param [Symbol] view_name
    #           view name
    # @param [Hash] options
    #           options
    # @return template
    def get_template(view_name, options)
      template_path = options[:template_path] || options[:views]
      @loader.template_path = template_path
      return @loader.load("#{view_name.to_s()}.html")
    end

    # Gets view class.
    #
    # @param [Symbol] view_name
    #           view name
    # @param [Hash] options
    #           options
    # @return view class
    def get_view_class(view_name, options)
      view_class = @class_cache.fetch(view_name, nil)
      if view_class == nil
        view_class = find_class(view_name.to_s(), options)
        @class_cache.store(view_name, view_class)
      end
      return view_class
    end

    private

    # Gets the named view class.
    #
    # @param [String] view_name
    #           view name
    # @param [Hash] options
    #           options
    # @return view class, or `View` if not found
    def find_class(view_name, options)
      relative_view_path = options[:views]
      if relative_view_path.start_with?(options[:root])
        relative_view_path = relative_view_path[options[:root].size()..-1]
      end
      relative_view_path.sub!(/^\//, '')

      # Construct string in form sub_folder/user_list
      relative_view_name = File.join(relative_view_path, view_name)

      # Convert view name from the format sub_folder/user_list to the format
      # SubFolder::UserList
      class_name = relative_view_name.split('/').map do |namespace|
        namespace.split(/[-_]/).map do |part|
          part[0] = part[0].chr.upcase
          part
        end.join
      end.join('::')

      # Is the class already defined?
      if const = const_get!(class_name)
        return const
      end

      full_view_name = File.join(options[:views], view_name)
      if File.exists?("#{full_view_name}.rb")
        require full_view_name
      else
        return View
      end
      
      if const = const_get!(class_name)
        return const
      end
      return View
    end

    # Finds constant by fully qualified name.
    #
    # @param [String] name
    #           fully qualified name to find
    # @return constant, or `nil` if not found
    def const_get!(name)
      name.split('::').inject(Object) do |cur_class, part|
        cur_class.const_get(part)
      end
    rescue NameError
      nil
    end
  end

end; end
