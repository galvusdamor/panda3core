package de.uniulm.ki.panda3.efficient.plan.element

/**
  * A causal link. Producer and consumer are identified by their reprective plan step number.
  * The condition is given as the index of the conditions of producer and consumer it connects.
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientCausalLink(producer : Int, consumer : Int, conditionIndexOfProducer : Int, conditionIndexOfConsuer : Int) {


  def addOffsetToPlanStepsIfGreaterThan(offset : Int, ifGEQ : Int) : EfficientCausalLink = {
    var newProducer = producer
    var newConsumer = consumer
    if (newProducer >= ifGEQ) newProducer += offset
    if (newConsumer >= ifGEQ) newConsumer += offset

    EfficientCausalLink(newProducer,newConsumer,conditionIndexOfProducer,conditionIndexOfConsuer)
  }
}
