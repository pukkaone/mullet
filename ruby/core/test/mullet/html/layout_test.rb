require 'minitest/autorun'
require 'mullet/html/layout'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class LayoutTest < MiniTest::Unit::TestCase
    include TemplateTests

    def test_render_page_in_layout()
      template = @loader.load('layout.html')
      layout = Layout.new(template)

      page_html =
%q{<html>
<head>
  <title>Title</title>
</head>
<body>
  <br>
</body>
</html>
}
      layout.execute(page_html, @output)

      expected_output =
%q{<html>
<head>
  <title>Title</title>
</head>
<body>
  <br>
</body>
</html>
}
      assert_equal expected_output, @output
    end
  end

end; end
