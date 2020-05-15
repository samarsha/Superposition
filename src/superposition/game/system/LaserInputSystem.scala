package superposition.game.system

import com.badlogic.ashley.core.{Entity, Family}
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx.input
import superposition.game.component.{Beam, ClassicalPosition, Multiverse}
import superposition.game.entity.Level
import superposition.game.system.LaserInputSystem.{beamHits, beamTarget}
import superposition.math.Vector2
import superposition.quantum.{StateId, Universe}

import scala.Function.const

/** The system for activating lasers based on player input.
  *
  * @param level a function that returns the current level
  */
final class LaserInputSystem(level: () => Option[Level])
  extends IteratingSystem(Family.all(classOf[Beam], classOf[ClassicalPosition]).get) {
  override def processEntity(entity: Entity, deltaTime: Float): Unit = {
    val beam = Beam.Mapper.get(entity)
    val cell = ClassicalPosition.Mapper.get(entity).cells.head
    val multiverse = level().get.multiverse
    val multiverseView = level().get.multiverseView

    // Apply the gate when the laser is clicked.
    if (input.isButtonJustPressed(0) && multiverseView.isSelected(cell)) {
      multiverse.applyGate(beam.gate.multi controlled const(beamHits(multiverse, entity)), ())
      multiverse.updateMetaWith(beam.lastTarget)(const(beamTarget(multiverse, entity)))
      multiverse.updateMetaWith(beam.elapsedTime) { time => universe =>
        if (beamTarget(multiverse, entity)(universe).isEmpty)
          time
        else 0
      }
    }
    multiverse.updateMetaWith(beam.elapsedTime)(time => const(time + deltaTime))
  }
}

/** Laser settings and functions for computing properties of the laser beam. */
object LaserInputSystem {
  /** The maximum length of the laser beam. */
  private val BeamLength: Int = 25

  /** Returns the cells in the path of the laser beam.
    *
    * @param entity the entity shooting the laser beam
    * @return the cells in the path of the laser beam
    */
  private def beamPath(entity: Entity): Seq[Vector2[Int]] = {
    val source = ClassicalPosition.Mapper.get(entity).cells.head
    val direction = Beam.Mapper.get(entity).direction
    LazyList.iterate(source)(_ + direction.toVector2).tail.take(BeamLength)
  }

  /** The target of the laser beam.
    *
    * @param multiverse the multiverse
    * @param entity the entity shooting the laser beam
    * @param universe the universe
    * @return the target of the laser beam
    */
  private def beamTarget(multiverse: Multiverse, entity: Entity)(universe: Universe): Option[Vector2[Int]] = {
    val controls = Beam.Mapper.get(entity).controls
    if (multiverse.allOn(universe, controls))
      beamPath(entity) find { cell =>
        multiverse.isBlocked(universe, cell) || multiverse.allInCell(universe, cell).nonEmpty
      }
    else None
  }

  /** Returns the qubits that are hit by the laser beam.
    *
    * @param multiverse the multiverse
    * @param entity the entity shooting the laser beam
    * @param universe the universe
    * @return the qubits that are hit by the laser beam
    */
  private def beamHits(multiverse: Multiverse, entity: Entity)(universe: Universe): Seq[StateId[Boolean]] =
    (beamTarget(multiverse, entity)(universe).iterator.to(Seq)
      flatMap (cell => multiverse.primaryBits(universe, cell)))
}