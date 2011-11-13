require 'minitest/autorun'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class ForTest < MiniTest::Unit::TestCase
    include TemplateTests

    EMPTY_LIST_OUTPUT = TemplateTests.strip_new_lines(<<-EOF
        <ul>
        </ul>
        EOF
        )

    NON_EMPTY_LIST_OUTPUT = TemplateTests.strip_new_lines(<<-EOF
        <ul>
          <li title="subject 1">
            <span>subject 1</span>
          </li>
          <li title="subject 2">
            <span>subject 2</span>
          </li>
        </ul>
        EOF
        )

    def ul()
      return element_to_string('ul')
    end

    def test_missing_value_should_not_render_element()
      set_variable('name-is-not-posts', nil)

      template = @loader.load('for.html')
      template.execute(@data, @output)

      assert_equal EMPTY_LIST_OUTPUT, ul() 
    end

    def test_nil_value_should_not_render_element()
      set_variable('posts', nil)

      template = @loader.load('for.html')
      template.execute(@data, @output)

      assert_equal EMPTY_LIST_OUTPUT, ul() 
    end

    def test_empty_array_should_not_render_element()
      set_variable('posts', [])

      template = @loader.load('for.html')
      template.execute(@data, @output)

      assert_equal EMPTY_LIST_OUTPUT, ul() 
    end

    def test_nonempty_array_should_render_elements()
      template = @loader.load('for.html')
      template.execute(@data, @output)

      assert_equal NON_EMPTY_LIST_OUTPUT, ul() 
    end

    def test_object_should_render_element()
      set_variable('posts', POSTS[0])

      template = @loader.load('for.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <ul>
            <li title="subject 1">
              <span>subject 1</span>
            </li>
          </ul>
          EOF
          )
      assert_equal expected_output, ul() 
    end

    def test_this_should_render_current_item()
      set_variable('colors', ['red', 'green', 'blue'])

      template = @loader.load('this.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <ul>
            <li>red</li>
            <li>green</li>
            <li>blue</li>
          </ul>
          EOF
          )
      assert_equal expected_output, ul() 
    end

    def test_for_remove_tag_should_remove_tag_and_render_content()
      template = @loader.load('for-remove-tag.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <table>
            <tr><td>Subject</td></tr>
            <tr><td>subject 1</td></tr>
            <tr><td>Subject</td></tr>
            <tr><td>subject 2</td></tr>
          </table>
          EOF
          )
      assert_equal expected_output, element_to_string('table') 
    end

    def test_for_remove_content_should_render_tag_and_remove_content()
      template = @loader.load('for-remove-content.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <ul>
            <li title="subject 1">
            <li title="subject 2">
          </ul>
          EOF
          )
      assert_equal expected_output, ul() 
    end
  end

end; end
