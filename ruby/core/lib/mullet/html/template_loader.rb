require 'nokogiri'

module Mullet

class TemplateDocument < Nokogiri::XML::SAX::Document
  TEMPLATE_NAMESPACE_URI = "http://pukkaone.github.com/mullet/1"

  def render_start_tag(name, attributes, prefix, uri, namespaceDecls)
    tag = "<"
    if prefix
      tag << prefix << ":"
    end
    tag << name

    attributes.each do |attribute|
      tag << " "
      if attribute.prefix
        tag << attribute.prefix << ":"
      end
      tag = attribute.localname << '="' << attribute.value << '"'
    end

    namespaceDecls.each do |namespaceDecl|
      uri = namespaceDecl[1] 
      if uri != TEMPLATE_NAMESPACE_URI
        tag << " xmlns:" << namespaceDecl[0] << '="' << uri << '"'
      end
    end

    tag << ">"
    return tag
  end

  def start_element_namespace(name, attributes, prefix, uri, ns)
    puts "start element #{name} #{attributes} #{prefix} #{uri} #{ns}"
    templateAttributes = attributes.select do |attribute|
      attribute.uri == TEMPLATE_NAMESPACE_URI
    end
    if templateAttributes.empty?
      puts render_start_tag(name, attributes, prefix, uri, ns)
    else
      puts "attributes #{templateAttributes}"
    end
  end

  def end_document
    puts "the document has ended"
  end
end

parser = Nokogiri::HTML::SAX::Parser.new(TemplateDocument.new)
f = File.open("login.html")
parser.parse(f)
f.close
end
