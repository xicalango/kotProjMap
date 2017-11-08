package xx.projmap.graphics

interface Renderer {
    fun render(scene: RenderableScene)
}

interface RenderableScene {
    fun render(graphicsAdapter: GraphicsAdapter)
}