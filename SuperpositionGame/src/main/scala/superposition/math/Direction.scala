package superposition.math

import enumeratum._
import superposition.math.Direction.{Down, Left, Right, Up}

/**
 * A cardinal direction.
 */
sealed trait Direction extends EnumEntry {
  def toVec2i: Vec2i = this match {
    case Up => Vec2i(0, 1)
    case Down => Vec2i(0, -1)
    case Left => Vec2i(-1, 0)
    case Right => Vec2i(1, 0)
  }
}

object Direction extends Enum[Direction] {

  /**
   * The up direction.
   */
  case object Up extends Direction

  /**
   * The down direction.
   */
  case object Down extends Direction

  /**
   * The left direction.
   */
  case object Left extends Direction

  /**
   * The right direction.
   */
  case object Right extends Direction

  override def values: IndexedSeq[Direction] = findValues
}
