package superposition.entity

import cats.syntax.applicative.catsSyntaxApplicativeId
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import superposition.component.{MapLayerView, Multiverse, Renderable}
import superposition.math.QExpr.QExpr

/** A map layer entity. */
object MapLayer {

  /** Creates a new map layer entity.
    *
    * @param renderer the tile map renderer
    * @param renderLayer the render layer to render the tile map in
    * @param mapLayer the tile map layer index to render
    * @param multiverse the multiverse
    * @param control the control for the layer
    */
  def apply(
      renderer: OrthogonalTiledMapRenderer,
      renderLayer: Int,
      mapLayer: Int,
      multiverse: Multiverse,
      control: QExpr[Boolean]
  ): Entity = {
    val entity = new Entity
    entity.add(new Renderable(renderLayer.pure[QExpr], control))
    entity.add(new MapLayerView(renderer, mapLayer, control))
    entity
  }
}
