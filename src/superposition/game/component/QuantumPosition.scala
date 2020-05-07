package superposition.game.component

import com.badlogic.ashley.core.{Component, ComponentMapper}
import superposition.math.{Vector2d, Vector2i}
import superposition.quantum.{MetaId, StateId}

/** The quantum position component has a position that may depend on which universe the entity is in.
  *
  * @param absolute the qudit representing the absolute position in camera coordinates
  * @param cell the qudit representing the cell position in grid coordinates
  * @param relative the position relative to the current cell
  */
final class QuantumPosition(val absolute: MetaId[Vector2d], val cell: StateId[Vector2i], var relative: Vector2d)
  extends Component

/** Contains the component mapper for the quantum position component. */
object QuantumPosition {
  /** The component mapper for the quantum position component. */
  val Mapper: ComponentMapper[QuantumPosition] = ComponentMapper.getFor(classOf[QuantumPosition])
}
