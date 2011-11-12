module Mullet

  # Collection of renderers which will be rendered in the order they were added.
  module Container
    def initialize()
      super if defined?(super)
      @children = []
    end

    def add_child(child)
      @children.push(child)
    end

    def delete_child(child)
      @children.delete(child);
    end

    def children()
      return @children
    end

    def clear_children()
      @children.clear()
    end

    # Renders children in order they were added.
    # 
    # @param [RenderContext] render_context
    #           render context
    def render_children(render_context)
      @children.each do |child|
        child.render(render_context)
      end
    end
  end

end
