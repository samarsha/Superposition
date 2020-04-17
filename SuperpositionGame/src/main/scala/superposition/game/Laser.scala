package superposition.game

import java.net.URL

import engine.core.Behavior.Entity
import engine.core.Game
import engine.core.Game.{dt, track}
import engine.core.Input.{mouse, mouseJustPressed}
import engine.graphics.Graphics._
import engine.graphics.sprites.Sprite
import engine.util.Color
import engine.util.Color._
import engine.util.math.Transformation
import superposition.game.Laser._
import superposition.math.Direction.{Down, Left, Right, Up}
import superposition.math.{Direction, Vec2i}
import superposition.quantum.{Gate, MetaId, StateId, Universe}

import scala.Function.const
import scala.jdk.CollectionConverters._
import scala.math.min

/**
 * A laser applies a quantum gate to any qubit hit by its beam.
 *
 * @param multiverse the multiverse this laser belongs to
 * @param cell       the position of this laser
 * @param gate       the gate to apply
 * @param direction  the direction this laser is pointing
 * @param controls   the cell that controls this laser if it contains a bit, or None if the laser is not controlled
 */
private final class Laser(multiverse: Multiverse,
                          cell: Vec2i,
                          gate: Gate[StateId[Boolean]],
                          direction: Direction,
                          controls: Iterable[Vec2i]) extends Entity {
  private val targetCell: MetaId[Option[Vec2i]] = multiverse.createIdMeta(None)

  private val elapsedTime: MetaId[Double] = multiverse.createIdMeta(0)

  add(new SpriteComponent(this,
    sprite = const(Sprite.load(Sprites(direction))),
    position = const(cell.toVec2d add 0.5)))

  private val universe: UniverseComponent = add(new UniverseComponent(this, multiverse))
  universe.blockingCells = const(List(cell))

  private val beam: LazyList[Vec2i] = LazyList.iterate(cell)(_ + direction.toVec2i).tail

  /**
   * Draws the laser in a universe.
   *
   * @param universe the universe to draw in
   */
  def draw(universe: Universe): Unit = {
    if (selected) {
      drawRectangleOutline(Transformation.create(cell.toVec2d, 0, 1), RED)
    }
    universe.meta(targetCell) match {
      case Some(targetCell) if universe.meta(elapsedTime) <= BeamDuration + FadeDuration =>
        drawWideLine(
          cell.toVec2d add 0.5,
          targetCell.toVec2d add 0.5,
          0.25,
          new Color(1, 0, 0,
            min(FadeDuration, BeamDuration + FadeDuration - universe.meta(elapsedTime)) / FadeDuration))
      case _ => ()
    }
  }

  private def targetCell(universe: Universe): Option[Vec2i] =
    if (universe.allOn(controls))
      beam.take(50) find (cell => universe.isBlocked(cell) || universe.allInCell(cell).nonEmpty)
    else
      None

  private def hits(universe: Universe): List[StateId[Boolean]] =
    targetCell(universe).toList flatMap universe.getPrimaryBits

  private def selected: Boolean =
    cell == Vec2i(mouse().x.floor.toInt, mouse().y.floor.toInt)

  private def step(): Unit = {
    if (mouseJustPressed(0) && selected) {
      multiverse.applyGate(gate.multi control const(hits), ())
      multiverse.universes = multiverse.universes map { universe =>
        if (targetCell(universe).isEmpty)
          universe.updatedMeta(targetCell)(None)
        else
          universe
            .updatedMeta(targetCell)(targetCell(universe))
            .updatedMeta(elapsedTime)(0)
      }
    }
    multiverse.universes = multiverse.universes map (_.updatedMetaWith(elapsedTime)(_ + dt))
  }
}

private object Laser {
  val All: Iterable[Laser] = track(classOf[Laser]).asScala

  private val Sprites: Map[Direction, URL] = Map(
    Up -> getClass.getResource("sprites/laser_up.png"),
    Down -> getClass.getResource("sprites/laser_down.png"),
    Left -> getClass.getResource("sprites/laser_left.png"),
    Right -> getClass.getResource("sprites/laser_right.png"))

  private val BeamDuration: Double = 0.2

  private val FadeDuration: Double = 0.3

  def declareSystem(): Unit = Game.declareSystem(classOf[Laser], (_: Laser).step())
}