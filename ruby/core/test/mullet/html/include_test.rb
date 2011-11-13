require 'minitest/autorun'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class IncludeTest < MiniTest::Unit::TestCase
    include TemplateTests

    EXPECTED_OUTPUT = TemplateTests.strip_new_lines(<<-EOF
        <body>
          <p>Hello</p>
        </body>
        EOF
        )

    def test_set_content_to_file()
      set_variable('greeting', 'Hello')

      template = @loader.load('include.html')
      template.execute(@data, @output)

      assert_equal EXPECTED_OUTPUT, body() 
    end

    def test_set_content_to_fragment()
      set_variable('greeting', 'Hello')

      template = @loader.load('include-id.html')
      template.execute(@data, @output)

      assert_equal EXPECTED_OUTPUT, body() 
    end

    def test_replace_element_with_file()
      set_variable('greeting', 'Hello')

      template = @loader.load('include-remove-tag.html')
      template.execute(@data, @output)

      assert_equal EXPECTED_OUTPUT, body() 
    end

    def test_replace_element_with_fragment()
      set_variable('greeting', 'Hello')

      template = @loader.load('include-id-remove-tag.html')
      template.execute(@data, @output)

      assert_equal EXPECTED_OUTPUT, body() 
    end
  end

end; end
