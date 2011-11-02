module Mullet

  # Collection of renderers which will be rendered in the order they were added.
  module Container
    def initialize()
      super if defined?(super)
      @children = []
    end

    def add_child(child)
      @children << child
    end

    def delete_child(child)
      @children.delete(child);
    end

    def clear_children()
      @children.clear()
    end

    # Renders children in order they were added.
    # 
    # @param [RenderContext] renderContext
    #           render context
    def render_children(renderContext)
      @children.each do |child|
        child.render(renderContext)
      end
    end
  end

end
