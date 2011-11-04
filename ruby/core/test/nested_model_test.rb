require 'minitest/autorun'
require 'mullet/default_nested_model'

class NestedModelTest < MiniTest::Unit::TestCase
  A_NAME = :a
  A_VALUE_PARENT = 'a-parent'
  A_VALUE_CHILD = 'a-child'

  B_NAME = :b
  B_VALUE_PARENT = 'b-parent'

  def setup()
    parent = { A_NAME => A_VALUE_PARENT, B_NAME => B_VALUE_PARENT }
    child = { A_NAME => A_VALUE_CHILD }
    @model = Mullet::DefaultNestedModel.new(parent, child)
  end

  def test_find_variable_in_child()
    assert_equal A_VALUE_CHILD, @model.fetch(A_NAME)
  end

  def test_find_variable_in_parent()
    assert_equal B_VALUE_PARENT, @model.fetch(B_NAME)
  end

  def test_return_not_found_when_variable_not_found()
    assert_equal Mullet::Model::NOT_FOUND, @model.fetch(:c)
  end
end
