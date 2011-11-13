require 'minitest/autorun'
require 'mullet/html/parser/tokenizer'

module Mullet; module HTML; module Parser

  class TokenizerTest < MiniTest::Unit::TestCase

    def test_yields_start_tags_without_paired_end_tags()
      input_html =
%q{<body>
  <p>Hello
  <p>World
</body>}

      tokenizer = Tokenizer.new(input_html)
      tokens = []
      tokenizer.each do |token|
        tokens << token
      end

      assert_equal :StartTag, tokens[0][:type]
      assert_equal 'body', tokens[0][:name]

      assert_equal :EndTag, tokens[6][:type]
      assert_equal 'body', tokens[6][:name]
    end
  end

end; end; end
