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

    def test_implicit_end_tag_should_not_render()
      template = @loader.load('implicit-end-tag.html')
      template.execute(@data, @output)

      expected_output = <<EOF
<body>
  <p>Hello
  <p>World
</body>
EOF
      assert_equal expected_output, @output
    end

    def test_empty_content_model_should_render_single_tag()
      template = @loader.load('empty-content-model.html')
      template.execute(@data, @output)

      expected_output = <<EOF
<body>
  <br>
  <hr>
  <img src="logo.png">
  <input>
</body>
EOF
      assert_equal expected_output, @output
    end

    def test_non_empty_content_model_should_render_start_and_end_tag()
      template = @loader.load('non-empty-content-model.html')
      template.execute(@data, @output)

      expected_output = <<EOF
<body>
  <script src=\"jquery.min.js\"></script>
  <style type=\"text/css\"></style>
  <textarea name=\"comment\"></textarea>
</body>
EOF
      assert_equal expected_output, @output
    end

    def test_cdata_section_in_template_should_render()
      template = @loader.load('cdata.html')
      template.execute(@data, @output)

      expected_output = <<EOF
<body>
  <p><![CDATA[<&]]></p>
</body>
EOF
      assert_equal expected_output, @output
    end

    def test_comments_in_template_should_render()
      set_variable('greeting', 'Hello');

      template = @loader.load('comment.html')
      template.execute(@data, @output)

      expected_output = <<EOF
<!-- a -->
<!DOCTYPE html>
<!-- b -->
<html>
  <!-- c -->
  <body>Hello</body>
</html>
<!-- d -->
EOF
      assert_equal expected_output, @output
    end

    def test_processing_instructions_in_template_should_render()
      set_variable('greeting', 'Hello');

      template = @loader.load('processing-instruction.html')
      template.execute(@data, @output)

      expected_output = <<EOF
<?php a ?>
<!DOCTYPE html>
<?php b ?>
<html>
  <?php c ?>
  <body>Hello</body>
</html>
<?php d ?>
EOF
      assert_equal expected_output, @output
    end
  end

end; end
