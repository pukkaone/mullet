require 'minitest/autorun'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class TextTest < MiniTest::Unit::TestCase
    include TemplateTests

    def test_set_text()
      set_variable('greeting', 'Hello')

      template = @loader.load('text.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p>Hello</p>
          </body>
          EOF
          )
      assert_equal expected_output, body()
    end

    def test_set_text_to_proc_value()
      set_variable('greeting', Proc.new { 'Hello' })

      template = @loader.load('text.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p>Hello</p>
          </body>
          EOF
          )
      assert_equal expected_output, body()
    end

    def test_message_command_should_format_message_arguments()
      template = @loader.load('text-message.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p>Subject subject 1 Date 2011-12-01</p>
          </body>
          EOF
          )
      assert_equal expected_output, body()
    end
  end

end; end
