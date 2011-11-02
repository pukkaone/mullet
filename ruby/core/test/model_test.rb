require 'minitest/autorun'
require 'mullet/default_model'

class NestedModelTest < MiniTest::Unit::TestCase
  VARIABLE_NAME = :first_name
  VARIABLE_VALUE = 'Joe'

  def test_should_return_this_object()
    data = VARIABLE_VALUE
    model = Mullet::DefaultModel.new(data)
    assert_equal VARIABLE_VALUE, model.fetch(:this)
  end

  def test_should_return_value_from_hash()
    data = { VARIABLE_NAME => VARIABLE_VALUE }
    model = Mullet::DefaultModel.new(data)
    assert_equal VARIABLE_VALUE, model.fetch(VARIABLE_NAME)
  end

  Struct.new('Person', VARIABLE_NAME)

  def test_should_return_method_return_value()
    data = Struct::Person.new(VARIABLE_VALUE)
    model = Mullet::DefaultModel.new(data)
    assert_equal VARIABLE_VALUE, model.fetch(VARIABLE_NAME)
  end

  class Person
    def initialize(first_name)
      @first_name = first_name
    end
  end

  def test_should_return_instance_variable_value()
    data = Person.new(VARIABLE_VALUE)
    model = Mullet::DefaultModel.new(data)
    assert_equal VARIABLE_VALUE, model.fetch(VARIABLE_NAME)
  end

  def test_should_return_proc_return_value()
    variable_proc = Proc.new { VARIABLE_VALUE }
    data = Person.new(variable_proc)
    model = Mullet::DefaultModel.new(data)
    assert_equal VARIABLE_VALUE, model.fetch(VARIABLE_NAME)
  end
end
