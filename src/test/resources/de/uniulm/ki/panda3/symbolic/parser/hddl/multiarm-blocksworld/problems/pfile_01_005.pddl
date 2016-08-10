(define
 (problem pfile_01_005)
 (:domain blocks)
 (:objects arm1 - ARM b1 b2 b3 b4 b5 - BLOCK)
 (:init
  (hand-empty arm1)
  (clear b5)
  (on-table b2)
  (on b5 b1)
  (on b1 b3)
  (on b3 b4)
  (on b4 b2))
 (:goal (and
         (clear b1)
         (on-table b4)
         (on b1 b4)
         (clear b5)
         (on-table b2)
         (on b5 b3)
         (on b3 b2))))