require 'minitest/autorun'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class IfTest < MiniTest::Unit::TestCase
    include TemplateTests

    FALSE_OUTPUT = TemplateTests.strip_new_lines(<<-EOF
        <body>
        </body>
        EOF
        )

    TRUE_OUTPUT = TemplateTests.strip_new_lines(<<-EOF
        <body>
          <p>Hello</p>
        </body>
        EOF
        )

    def test_missing_value_should_not_render_element()
      template = @loader.load('if.html')
      template.execute(@data, @output)

      assert_equal FALSE_OUTPUT, body() 
    end

    def test_nil_value_should_not_render_element()
      set_variable('condition', nil)

      template = @loader.load('if.html')
      template.execute(@data, @output)

      assert_equal FALSE_OUTPUT, body() 
    end

    def test_false_should_not_render_element()
      set_variable('condition', false)

      template = @loader.load('if.html')
      template.execute(@data, @output)

      assert_equal FALSE_OUTPUT, body() 
    end

    def test_empty_array_should_not_render_element()
      set_variable('condition', [])

      template = @loader.load('if.html')
      template.execute(@data, @output)

      assert_equal FALSE_OUTPUT, body() 
    end

    def test_true_should_render_element()
      set_variable('condition', true)

      template = @loader.load('if.html')
      template.execute(@data, @output)

      assert_equal TRUE_OUTPUT, body() 
    end

    def test_non_empty_array_should_render_element()
      set_variable('condition', [ 1 ])

      template = @loader.load('if.html')
      template.execute(@data, @output)

      assert_equal TRUE_OUTPUT, body() 
    end

    def test_object_should_render_element()
      set_variable('condition', POSTS[0])

      template = @loader.load('if.html')
      template.execute(@data, @output)

      assert_equal TRUE_OUTPUT, body() 
    end
  end

end; end
