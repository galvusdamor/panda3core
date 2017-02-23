package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.{Task, Domain, DecompositionMethod}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object ReplaceInitialPlanByTop extends DecompositionMethodTransformer[Unit] {

  override protected val allowToRemoveTopMethod = false

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, unit: Unit, originalDomain: Domain):
  (Seq[DecompositionMethod], Seq[Task]) = (methods :+ topMethod, Nil)

  override protected val transformationName: String = "artificialTop"
}