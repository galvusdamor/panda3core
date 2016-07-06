package de.uniulm.ki.panda3.efficient.logic

/**
  * Represents an efficient literal
  *
  * If the value of a variable is negative it is in fact a constant. See [[de.uniulm.ki.panda3.efficient.switchConstant]]
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientLiteral(predicate: Int, isPositive: Boolean, parameterVariables: Array[Int]) {

  def checkPredicateAndSign(other: EfficientLiteral): Boolean = predicate == other.predicate && isPositive == other.isPositive
}

//scalastyle:off covariant.equals
case class EfficientGroundLiteral(predicate: Int, isPositive : Boolean, arguments: Array[Int]) {
  // we need a special equals as we use arrays
  override def equals(o: scala.Any): Boolean = if (o.isInstanceOf[EfficientGroundLiteral]) {
    val that = o.asInstanceOf[EfficientGroundLiteral]
    if (this.predicate != that.predicate) false else
      this.isPositive == that.isPositive && (this.arguments sameElements that.arguments)
  } else false

  override def hashCode(): Int = predicate
}