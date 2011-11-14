require 'minitest/autorun'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class EscapeXmlTest < MiniTest::Unit::TestCase
    include TemplateTests

    def test_should_disable_then_enable_escaping()
      set_variable('greeting', '<b>')

      template = @loader.load('escape-xml.html')
      template.execute(@data, @output)

      expected_output = <<EOF
<body>
  <p><b></p>
  <div>
    <p>&lt;b&gt;</p>
  </div>
</body>
EOF
      assert_equal expected_output, @output
    end
  end

end; end
