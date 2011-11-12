require 'mullet/default_nested_model'
require 'mullet/html/template_loader'

module Mullet; module HTML

  class Post
    attr_accessor :subject, :date

    def initialize(subject, date)
      @subject = subject
      @date = date
    end
  end

  module TemplateTests
    TOP_DIR = File.expand_path('../../../../..', File.dirname(__FILE__))
    TEST_DIR = File.join(TOP_DIR, 'java/core/src/test/java')
    TEMPLATE_PATH = File.join(TEST_DIR, 'com/github/pukkaone/mullet/html')

    POSTS = [
      Post.new('subject 1', '2011-12-01'),
      Post.new('subject 2', '2011-12-02')
    ]

    def setup()
      @loader = Mullet::HTML::TemplateLoader.new(TEMPLATE_PATH)
      @data = { posts: POSTS, post: POSTS[0] }
      @output = ''
    end

    def set_variable(variable_name, value)
      if @model_map == nil
        @model_map = Hash.new()
        @data = DefaultNestedModel.new(@model_map)
      end

      @model_map.store(variable_name, value)
    end

    def strip_new_lines(input)
      return input.gsub(/\n\s*/, '')
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
