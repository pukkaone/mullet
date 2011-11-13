require 'minitest/autorun'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class AttributeTest < MiniTest::Unit::TestCase
    include TemplateTests

    def test_escape_quote_in_attribute_value()
      template = @loader.load('attribute-quote.html')
      template.execute(@data, @output)

      expected_output =
%q{<body>
  <p lang="&#34;"></p>
  <p lang="'"></p>
</body>}
      assert_equal expected_output, @output
    end

    def test_namespace_prefix_attribute_command_should_set_attribute()
      set_variable('languageCode', 'add')

      template = @loader.load('namespace-prefix-attribute.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p lang="add"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_default_prefix_attribute_command_should_set_attribute()
      set_variable('languageCode', 'add')

      template = @loader.load('default-prefix-attribute.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p lang="add"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_set_attribute_when_attribute_does_not_exist()
      set_variable('languageCode', 'add')

      template = @loader.load('attribute-add.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p lang="add"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_replace_existing_attribute()
      set_variable('languageCode', 'replace')

      template = @loader.load('attribute-replace.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p lang="replace"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_set_multiple_attributes()
      set_variable('languageCode', 'add')
      set_variable('greeting', 'replace')

      template = @loader.load('attribute-multiple.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p title="replace" lang="add"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_missing_value_should_remove_attribute()
      set_variable('greeting', 'replace')

      template = @loader.load('attribute-multiple.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p title="replace"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_nil_value_should_remove_attribute()
      set_variable('languageCode', nil)
      set_variable('greeting', 'replace')

      template = @loader.load('attribute-multiple.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p title="replace"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_message_command_should_format_message_arguments()
      template = @loader.load('attribute-message.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p title="Subject subject 1 Date 2011-12-01"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_action_command_should_set_action_attribute()
      set_variable('greeting', 'add')

      template = @loader.load('action.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <form action="add">
            </form>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_alt_command_should_set_alt_attribute()
      set_variable('greeting', 'add')

      template = @loader.load('alt.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <img alt="add">
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_alt_message_command_should_set_alt_attribute()
      template = @loader.load('alt-message.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p alt="Subject subject 1 Date 2011-12-01"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_href_command_should_set_href_attribute()
      set_variable('greeting', 'add')

      template = @loader.load('href.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <a href="add"></a>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_src_command_should_set_src_attribute()
      set_variable('greeting', 'add')

      template = @loader.load('src.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <img src="add">
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_title_command_should_set_title_attribute()
      set_variable('greeting', 'add')

      template = @loader.load('title.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p title="add"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_title_message_command_should_set_title_attribute()
      template = @loader.load('title-message.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p title="Subject subject 1 Date 2011-12-01"></p>
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_value_command_should_set_value_attribute()
      set_variable('greeting', 'add')

      template = @loader.load('value.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <input value="add">
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end

    def test_value_message_command_should_set_value_attribute()
      template = @loader.load('value-message.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <input value="Subject subject 1 Date 2011-12-01">
          </body>
          EOF
          )
      assert_equal expected_output, body() 
    end
  end

end; end
