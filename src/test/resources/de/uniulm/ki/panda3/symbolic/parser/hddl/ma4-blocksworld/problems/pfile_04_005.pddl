(define
 (problem pfile_04_005)
 (:domain blocks)
 (:objects arm1 arm2 arm3 arm4 - ARM b1 b2 b3 b4 b5 - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (clear b5)
  (on-table b4)
  (on b5 b3)
  (on b3 b2)
  (on b2 b1)
  (on b1 b4))
 (:goal (and
         (clear b3)
         (on-table b3)
         (clear b2)
         (on-table b1)
         (on b2 b4)
         (on b4 b5)
         (on b5 b1))))