package superposition

import scala.math.{atan2, cos, sin, sqrt}

/**
 * Factory for [[superposition.Complex]] instances.
 */
private object Complex {
  /**
   * Creates a complex number from its polar form re^iθ^.
   *
   * @param r the radius r
   * @param theta the angle θ
   * @return the complex number re^iθ^
   */
  def polar(r: Double, theta: Double) = Complex(r * cos(theta), r * sin(theta))
}

/**
 * A complex number.
 *
 * @param real the real part
 * @param imag the imaginary part
 */
private case class Complex(real: Double, imag: Double = 0.0) {
  /**
   * Adds two complex numbers.
   *
   * @param that the complex number to add to this
   * @return the sum of the two complex numbers
   */
  def +(that: Complex): Complex = Complex(real + that.real, imag + that.imag)

  /**
   * Subtracts two complex numbers.
   *
   * @param that the complex number to subtract from this
   * @return the difference of the two complex numbers
   */
  def -(that: Complex): Complex = Complex(real - that.real, imag - that.imag)

  /**
   * Multiplies two complex numbers.
   *
   * @param that the complex number to multiply with this
   * @return the product of the two complex numbers
   */
  def *(that: Complex): Complex = Complex(
    real * that.real - imag * that.imag,
    real * that.imag + imag * that.real
  )

  /**
   * Divides two complex numbers.
   *
   * @param that the complex number to divide into this
   * @return the quotient of the two complex numbers
   */
  def /(that: Complex): Complex = Complex(
    (real * that.real + imag * that.imag) / that.squaredMagnitude,
    (imag * that.real - real * that.imag) / that.squaredMagnitude
  )

  /**
   * The magnitude, or absolute value, of this complex nuumber.
   */
  def magnitude: Double = sqrt(squaredMagnitude)

  /**
   * The squared magnitude, or squared absolute value, of this complex number.
   * @return
   */
  def squaredMagnitude: Double = real * real + imag * imag

  /**
   * The phase angle θ when this complex number is written in the polar form re^iθ^.
   */
  def phase: Double = atan2(imag, real)
}
