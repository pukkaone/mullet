module Mullet; module HTML

  # Recognized template commands
  module Command
    DATA_PREFIX = 'data-'
    NAMESPACE_PREFIX = 'mullet'
    NAMESPACE_URI = 'http://pukkaone.github.com/mullet/1'

    ACTION = 'action'
    ALT = 'alt'
    ALT_MESSAGE = 'alt-message'
    ATTR = 'attr'
    ATTR_MESSAGE = 'attr-message'
    CHECKED = 'checked'
    DISABLED = 'disabled'
    ESCAPE_XML = 'escape-xml'
    FOR = 'for'
    HREF = 'href'
    IF = 'if'
    INCLUDE = 'include'
    READONLY = 'readonly'
    REMOVE = 'remove'
    SELECTED = 'selected'
    SRC = 'src'
    TEXT = 'text'
    TEXT_MESSAGE = 'text-message'
    TITLE = 'title'
    TITLE_MESSAGE = 'title-message'
    UNLESS = 'unless'
    VALUE = 'value'
    VALUE_MESSAGE = 'value-message'
  end

end; end
