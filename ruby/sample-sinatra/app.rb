$LOAD_PATH << File.expand_path('../core/lib', File.dirname(__FILE__))

require 'i18n'
require 'mullet/tilt'
require 'sinatra'

I18n.load_path += Dir.glob(File.join(settings.root, 'locale', '*.{rb,yml}'))

helpers do
  def mullet(*args)
    render(:html, *args)
  end
end

get '/' do
  @title = 'Title'
  mullet :index, locals: { name: 'Chris' }
end
