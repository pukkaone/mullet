require 'minitest/autorun'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class RemoveTest < MiniTest::Unit::TestCase
    include TemplateTests

    def test_remove_tag_should_remove_tag_and_preserve_content()
      set_variable('greeting', 'Hello')

      template = @loader.load('remove-tag.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            HelloWorld
          </body>
          EOF
          )
      assert_equal expected_output, body()
    end

    def test_remove_content_should_preserve_tag_and_remove_content()
      set_variable('greeting', 'Hello')

      template = @loader.load('remove-content.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p title="Hello"></p>
            <p></p>
            <p></p>
          </body>
          EOF
          )
      assert_equal expected_output, body()
    end

    def test_remove_element_should_remove_tag_and_content()
      template = @loader.load('remove-element.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <ul>
            <li>subject 1</li>
            <li>subject 2</li>
          </ul>
          EOF
          )
      assert_equal expected_output, element_to_string('ul')
    end
  end

end; end
