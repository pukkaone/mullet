require 'minitest/autorun'
require 'mullet/default_scope'

class DefaultScopeTest < MiniTest::Unit::TestCase
  VARIABLE_NAME = :first_name
  VARIABLE_VALUE = 'Joe'

  def test_return_this_object()
    data = VARIABLE_VALUE
    scope = Mullet::DefaultScope.new(data)
    assert_equal VARIABLE_VALUE, scope.get_variable_value(:'.')
  end

  def test_return_value_from_hash()
    data = { VARIABLE_NAME => VARIABLE_VALUE }
    scope = Mullet::DefaultScope.new(data)
    assert_equal VARIABLE_VALUE, scope.get_variable_value(VARIABLE_NAME)
  end

  Struct.new('Person', VARIABLE_NAME)

  def test_return_method_return_value()
    data = Struct::Person.new(VARIABLE_VALUE)
    scope = Mullet::DefaultScope.new(data)
    assert_equal VARIABLE_VALUE, scope.get_variable_value(VARIABLE_NAME)
  end

  class Person
    def initialize(first_name)
      @first_name = first_name
    end
  end

  def test_return_instance_variable_value()
    data = Person.new(VARIABLE_VALUE)
    scope = Mullet::DefaultScope.new(data)
    assert_equal VARIABLE_VALUE, scope.get_variable_value(VARIABLE_NAME)
  end

  def test_return_proc_return_value()
    variable_proc = Proc.new { VARIABLE_VALUE }
    data = Person.new(variable_proc)
    scope = Mullet::DefaultScope.new(data)
    assert_equal VARIABLE_VALUE, scope.get_variable_value(VARIABLE_NAME)
  end
end
