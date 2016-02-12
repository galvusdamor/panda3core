package de.uniulm.ki.panda3.efficient.plan.modification


import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.symbolic.plan.modification.AddOrderingTestData
import org.scalatest.FlatSpec

/**
  * This reuses the testcases for the symbolic [[de.uniulm.ki.panda3.symbolic.plan.modification.AddOrderingTest]].
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off null
class EfficientAddOrderingTest extends FlatSpec with AddOrderingTestData{


  "Generating Ordering Modifications for causal threads" must "produce promotion and demotion" in {
    // creating the wrapper
    val wrapper  = Wrapping(promotionDemotionDomain,promotionDemotionPlan)
    val plan = wrapper.unwrap(promotionDemotionPlan)
    val causalLink = wrapper.unwrap(promotionDemotionPlanCL,promotionDemotionPlan)
    val ps3 = wrapper.unwrap(promotionDemotionPlanPS3,promotionDemotionPlan)
    val ps2 = wrapper.unwrap(promotionDemotionPlanPS2,promotionDemotionPlan)
    val psg = wrapper.unwrap(psgoal,promotionDemotionPlan)

    val addOrderingModifications = EfficientAddOrdering(plan,null,causalLink,ps3)

    assert(addOrderingModifications.length == 2)
    assert(addOrderingModifications exists { case EfficientAddOrdering(_,_,before,after) => before == ps3 && after == ps2 })
    assert(addOrderingModifications exists { case EfficientAddOrdering(_,_,before,after) => before == psg && after == ps3 })
  }

  "Generating Ordering Modifications for causal threads" must "be correct, if demotion is not possible" in {
    val wrapper  = Wrapping(exampleDomain2,demotionNotPossiblePlan)
    val plan = wrapper.unwrap(demotionNotPossiblePlan)
    val causalLink = wrapper.unwrap(demotionNotPossiblePlanCL,demotionNotPossiblePlan)
    val ps3 = wrapper.unwrap(demotionNotPossiblePlanPS3,demotionNotPossiblePlan)
    val ps2 = wrapper.unwrap(demotionNotPossiblePlanPS2,demotionNotPossiblePlan)


    val singleOrderingModification = EfficientAddOrdering(plan,null,causalLink,ps3)
    assert(singleOrderingModification.length == 1)
    assert(singleOrderingModification exists { case EfficientAddOrdering(_,_,before,after) => before == ps3 && after == ps2 })
  }
}