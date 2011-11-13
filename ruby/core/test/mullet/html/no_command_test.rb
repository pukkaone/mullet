require 'minitest/autorun'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class NoCommandTest < MiniTest::Unit::TestCase
    include TemplateTests

    def test_no_command_should_render_template()
      template = @loader.load('no-command.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<-EOF
          <body>
            <p>Hello</p>
          </body>
          EOF
          )
      assert_equal expected_output, body()
    end

    # TODO: def test_implicit_end_tag_should_not_render()

=begin
    def test_empty_content_model_should_render_single_tag()
      template = @loader.load('empty-content-model.html')
      template.execute(@data, @output)

      expected_output =
%q{<body>
  <br>
  <hr>
  <img src="logo.png">
  <input>
</body>}
      assert_equal expected_output, @output
    end
=end

  end

end; end
