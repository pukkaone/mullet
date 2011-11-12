require 'minitest/autorun'
require 'mullet/html/template_tests'

module Mullet; module HTML

  class AttributeTest < MiniTest::Unit::TestCase
    include TemplateTests

    def test_set_attribute_when_attribute_does_not_exist()
      set_variable('languageCode', 'add')

      template = @loader.load('attribute-add.html')
      template.execute(@data, @output)

      expected_output = strip_new_lines(<<EOF
<body>
  <p lang="add"></p>
</body>
EOF
)
      assert_equal expected_output, body() 
    end
  end

end; end
