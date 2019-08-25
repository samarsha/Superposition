package superposition

import engine.core.Behavior
import engine.core.Behavior.Entity
import engine.graphics.sprites.Sprite
import engine.util.Color.{BLACK, WHITE}
import engine.util.math.{Transformation, Vec2d}
import extras.physics.{PhysicsComponent, Rectangle}

import scala.jdk.CollectionConverters._

/**
 * A physical object that carries a single bit as its state.
 *
 * Bits have a position and velocity in addition to their state and can be dragged using the mouse.
 *
 * @param position the initial position
 * @param velocity the initial velocity
 * @param on the initial state
 */
private class Quball(position: Vec2d, var on: Boolean = false) extends Entity {
  val physics: PhysicsComponent = require(classOf[PhysicsComponent])
  physics.position = position

  Behavior.track(classOf[Quball])

  override protected def onCreate(): Unit =
    physics.collider = PhysicsComponent.wallCollider(new Vec2d(1, 1), List(
      new Rectangle(new Vec2d(-8, -4.5), new Vec2d(-8, 4.5)),
      new Rectangle(new Vec2d(-8, -4.5), new Vec2d(8, -4.5)),
      new Rectangle(new Vec2d(-8, 4.5), new Vec2d(8, 4.5)),
      new Rectangle(new Vec2d(8, -4.5), new Vec2d(8, 4.5))
    ).asJavaCollection)

  /**
   * Draws this bit.
   */
  def draw(): Unit = {
    val color = if (on) WHITE else BLACK
    Sprite.load("ball.png").draw(Transformation.create(physics.position, 0, 1), color)
  }

  def copy(): Quball = {
    val quball = new Quball(physics.position, on)
    quball.physics.velocity = physics.velocity
    quball.physics.collider = physics.collider
    quball.physics.hitWall = physics.hitWall
    quball
  }
}