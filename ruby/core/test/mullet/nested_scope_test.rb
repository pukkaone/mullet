require 'minitest/autorun'
require 'mullet/default_nested_scope'

class NestedScopeTest < MiniTest::Unit::TestCase
  A_NAME = :a
  A_VALUE_PARENT = 'a-parent'
  A_VALUE_CHILD = 'a-child'

  B_NAME = :b
  B_VALUE_PARENT = 'b-parent'

  def setup()
    parent = { A_NAME => A_VALUE_PARENT, B_NAME => B_VALUE_PARENT }
    child = { A_NAME => A_VALUE_CHILD }
    @scope = Mullet::DefaultNestedScope.new(parent, child)
  end

  def test_find_variable_in_child()
    assert_equal A_VALUE_CHILD, @scope.get_variable_value(A_NAME)
  end

  def test_find_variable_in_parent()
    assert_equal B_VALUE_PARENT, @scope.get_variable_value(B_NAME)
  end

  def test_return_not_found_when_variable_not_found()
    assert_equal Mullet::Scope::NOT_FOUND, @scope.get_variable_value(:c)
  end
end
