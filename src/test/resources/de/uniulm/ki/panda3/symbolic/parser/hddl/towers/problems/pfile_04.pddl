(define
 (problem tower_problem_4)
 (:domain towers)
 (:objects t1 t2 t3 - TOWER r1 r2 r3 r4 - RING)
 (:init
  (smallerThan r1 t1)
  (smallerThan r1 t2)
  (smallerThan r1 t3)
  (smallerThan r2 t1)
  (smallerThan r2 t2)
  (smallerThan r2 t3)
  (smallerThan r3 t1)
  (smallerThan r3 t2)
  (smallerThan r3 t3)
  (smallerThan r4 t1)
  (smallerThan r4 t2)
  (smallerThan r4 t3)
  (smallerThan r1 r2)
  (smallerThan r1 r3)
  (smallerThan r1 r4)
  (smallerThan r2 r3)
  (smallerThan r2 r4)
  (smallerThan r3 r4)
  (on r1 r2)
  (on r2 r3)
  (on r3 r4)
  (on r4 t1)
  (towerTop r1 t1)
  (towerTop t2 t2)
  (towerTop t3 t3))
 (:goal (and (on r1 r2) (on r2 r3) (on r3 r4) (on r4 t3))))