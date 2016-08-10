(define
 (problem tower_problem_8)
 (:domain towers)
 (:objects t1 t2 t3 - TOWER r1 r2 r3 r4 r5 r6 r7 r8 - RING)
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
  (smallerThan r5 t1)
  (smallerThan r5 t2)
  (smallerThan r5 t3)
  (smallerThan r6 t1)
  (smallerThan r6 t2)
  (smallerThan r6 t3)
  (smallerThan r7 t1)
  (smallerThan r7 t2)
  (smallerThan r7 t3)
  (smallerThan r8 t1)
  (smallerThan r8 t2)
  (smallerThan r8 t3)
  (smallerThan r1 r2)
  (smallerThan r1 r3)
  (smallerThan r1 r4)
  (smallerThan r1 r5)
  (smallerThan r1 r6)
  (smallerThan r1 r7)
  (smallerThan r1 r8)
  (smallerThan r2 r3)
  (smallerThan r2 r4)
  (smallerThan r2 r5)
  (smallerThan r2 r6)
  (smallerThan r2 r7)
  (smallerThan r2 r8)
  (smallerThan r3 r4)
  (smallerThan r3 r5)
  (smallerThan r3 r6)
  (smallerThan r3 r7)
  (smallerThan r3 r8)
  (smallerThan r4 r5)
  (smallerThan r4 r6)
  (smallerThan r4 r7)
  (smallerThan r4 r8)
  (smallerThan r5 r6)
  (smallerThan r5 r7)
  (smallerThan r5 r8)
  (smallerThan r6 r7)
  (smallerThan r6 r8)
  (smallerThan r7 r8)
  (on r1 r2)
  (on r2 r3)
  (on r3 r4)
  (on r4 r5)
  (on r5 r6)
  (on r6 r7)
  (on r7 r8)
  (on r8 t1)
  (towerTop r1 t1)
  (towerTop t2 t2)
  (towerTop t3 t3))
 (:goal (and
         (on r1 r2)
         (on r2 r3)
         (on r3 r4)
         (on r4 r5)
         (on r5 r6)
         (on r6 r7)
         (on r7 r8)
         (on r8 t3))))