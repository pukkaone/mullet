$LOAD_PATH << File.expand_path('../core/lib', File.dirname(__FILE__))

require 'mullet/tilt'
require 'sinatra'

helpers do
  def mullet(*args)
    render(:html, *args)
  end
end

get '/' do
  @title = 'Title'
  mullet :index, locals: { greeting: 'Hello' }
end
