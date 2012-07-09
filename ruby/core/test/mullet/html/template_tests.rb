require 'mullet/default_nested_scope'
require 'mullet/html/template_loader'

module Mullet; module HTML

  class Post
    attr_accessor :id, :subject, :date, :selected

    def initialize(id, subject, date, selected)
      @id = id
      @subject = subject
      @date = date
      @selected = selected
    end
  end

  module TemplateTests
    CURRENT_DIR = File.dirname(__FILE__)
    TOP_DIR = File.expand_path('../../../../..', CURRENT_DIR)
    TEST_JAVA_DIR = File.join(TOP_DIR, 'java/core/src/test/java')
    TEMPLATE_PATH = File.join(TEST_JAVA_DIR, 'com/github/pukkaone/mullet/html')

    POSTS = [
      Post.new(1, 'subject 1', '2011-12-01', nil),
      Post.new(2, 'subject 2', '2011-12-02', 'selected')
    ]

    def setup()
      @loader = Mullet::HTML::TemplateLoader.new(TEMPLATE_PATH)
      @data = { posts: POSTS, post: POSTS[0] }
      I18n.load_path += Dir.glob(File.join(CURRENT_DIR, 'locale', '*.{rb,yml}'))
      @output = ''
    end

    def set_variable(variable_name, value)
      if @scope_map == nil
        @scope_map = Hash.new()
        @data = DefaultNestedScope.new(@scope_map)
      end

      @scope_map.store(variable_name, value)
    end

    def self.strip_new_lines(input)
      return input.gsub(/\n\s*/, '').strip()
    end

    def strip_new_lines(input)
      return TemplateTests.strip_new_lines(input)
    end

    def element_to_string(tag_name)
      fragment = Nokogiri::HTML.fragment(@output)
      elements = fragment.css(tag_name)
      return strip_new_lines(elements.first().to_html())
    end

    def body()
      return element_to_string('body')
    end
  end

end; end
