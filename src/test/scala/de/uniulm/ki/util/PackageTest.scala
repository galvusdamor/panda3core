package de.uniulm.ki.util

import org.scalatest.FlatSpec
import de.uniulm.ki.util._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class PackageTest extends FlatSpec{

  "Computing all Mappings bewteen two Seqs" must "yield all possible combinations"in {
    val listA = 0 :: 1 :: 2 :: Nil
    val listB = 10 :: 11 :: 12 :: Nil

    val allPossible = allMappings(listA,listB)

    assert(allPossible.length == 6)
    assert(allPossible contains ((2,10) :: (1,11) :: (0,12) :: Nil))
    assert(allPossible contains ((2,10) :: (1,12) :: (0,11) :: Nil))
    assert(allPossible contains ((2,11) :: (1,10) :: (0,12) :: Nil))
    assert(allPossible contains ((2,11) :: (1,12) :: (0,10) :: Nil))
    assert(allPossible contains ((2,12) :: (1,10) :: (0,11) :: Nil))
    assert(allPossible contains ((2,12) :: (1,11) :: (0,10) :: Nil))
  }
}