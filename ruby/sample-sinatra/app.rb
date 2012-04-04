$LOAD_PATH << File.expand_path('../core/lib', File.dirname(__FILE__))

require 'i18n'
require 'sinatra'
require 'mullet/sinatra'

I18n.load_path += Dir.glob(File.join(settings.root, 'locale', '*.{rb,yml}'))

get '/' do
  @title = 'Title'
  mullet :index, locals: { name: 'Chris' }
end

get '/now' do
  mullet :now, locals: { first_name: 'John', last_name: 'Smith' }
end
