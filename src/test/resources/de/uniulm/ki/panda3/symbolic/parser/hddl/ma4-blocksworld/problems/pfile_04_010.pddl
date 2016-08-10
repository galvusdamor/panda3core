(define
 (problem pfile_04_010)
 (:domain blocks)
 (:objects arm1 arm2 arm3 arm4 - ARM
           b1 b2 b3 b4 b5 b6 b7 b8 b9 b10 - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (clear b1)
  (on-table b7)
  (on b1 b10)
  (on b10 b6)
  (on b6 b2)
  (on b2 b7)
  (clear b8)
  (on-table b4)
  (on b8 b9)
  (on b9 b5)
  (on b5 b3)
  (on b3 b4))
 (:goal (and
         (clear b1)
         (on-table b10)
         (on b1 b7)
         (on b7 b5)
         (on b5 b10)
         (clear b9)
         (on-table b6)
         (on b9 b3)
         (on b3 b6)
         (clear b8)
         (on-table b4)
         (on b8 b4)
         (clear b2)
         (on-table b2))))