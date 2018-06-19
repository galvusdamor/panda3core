// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.efficient.plan.element

/**
  * A causal link. Producer and consumer are identified by their reprective plan step number.
  * The condition is given as the index of the conditions of producer and consumer it connects.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientCausalLink(producer : Int, consumer : Int, conditionIndexOfProducer : Int, conditionIndexOfConsumer : Int) {
  assert(producer >= 0)
  assert(consumer >= 0) // cannot be init
  assert(conditionIndexOfConsumer >= 0, "Consumer Condition Index was : " + conditionIndexOfConsumer)
  assert(conditionIndexOfProducer >= 0, "Producer Condition Index was : " + conditionIndexOfProducer)


  def consumerOrProducer(isProducer : Boolean) : Int = if (isProducer) producer else consumer
  def consumerOrProducerIndex(isProducer : Boolean) : Int = if (isProducer) conditionIndexOfProducer else conditionIndexOfConsumer

  def addOffsetToPlanStepsIfGreaterThan(offset : Int, ifGEQ : Int) : EfficientCausalLink = {
    var newProducer = producer
    var newConsumer = consumer
    if (newProducer >= ifGEQ) newProducer += offset
    if (newConsumer >= ifGEQ) newConsumer += offset

    EfficientCausalLink(newProducer, newConsumer, conditionIndexOfProducer, conditionIndexOfConsumer)
  }
}
