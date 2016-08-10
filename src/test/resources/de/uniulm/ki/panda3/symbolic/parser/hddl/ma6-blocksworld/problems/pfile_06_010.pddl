(define
 (problem pfile_06_010)
 (:domain blocks)
 (:objects arm1 arm2 arm3 arm4 arm5 arm6 - ARM
           b1 b2 b3 b4 b5 b6 b7 b8 b9 b10 - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (hand-empty arm5)
  (hand-empty arm6)
  (clear b10)
  (on-table b10)
  (clear b8)
  (on-table b8)
  (clear b6)
  (on-table b7)
  (on b6 b3)
  (on b3 b9)
  (on b9 b5)
  (on b5 b2)
  (on b2 b1)
  (on b1 b7)
  (clear b4)
  (on-table b4))
 (:goal (and
         (clear b6)
         (on-table b6)
         (clear b7)
         (on-table b4)
         (on b7 b8)
         (on b8 b2)
         (on b2 b9)
         (on b9 b5)
         (on b5 b10)
         (on b10 b1)
         (on b1 b4)
         (clear b3)
         (on-table b3))))