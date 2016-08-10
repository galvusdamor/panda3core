(define
 (problem pfile_06_025)
 (:domain blocks)
 (:objects arm1 arm2 arm3 arm4 arm5 arm6 - ARM
           b1
           b2
           b3
           b4
           b5
           b6
           b7
           b8
           b9
           b10
           b11
           b12
           b13
           b14
           b15
           b16
           b17
           b18
           b19
           b20
           b21
           b22
           b23
           b24
           b25
           - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (hand-empty arm5)
  (hand-empty arm6)
  (clear b22)
  (on-table b20)
  (on b22 b13)
  (on b13 b20)
  (clear b16)
  (on-table b16)
  (clear b23)
  (on-table b11)
  (on b23 b11)
  (clear b9)
  (on-table b6)
  (on b9 b5)
  (on b5 b18)
  (on b18 b6)
  (clear b15)
  (on-table b4)
  (on b15 b3)
  (on b3 b4)
  (clear b2)
  (on-table b2)
  (clear b14)
  (on-table b1)
  (on b14 b24)
  (on b24 b12)
  (on b12 b25)
  (on b25 b7)
  (on b7 b21)
  (on b21 b19)
  (on b19 b8)
  (on b8 b17)
  (on b17 b10)
  (on b10 b1))
 (:goal (and
         (clear b1)
         (on-table b21)
         (on b1 b2)
         (on b2 b20)
         (on b20 b21)
         (clear b12)
         (on-table b10)
         (on b12 b10)
         (clear b8)
         (on-table b5)
         (on b8 b13)
         (on b13 b14)
         (on b14 b6)
         (on b6 b23)
         (on b23 b15)
         (on b15 b16)
         (on b16 b18)
         (on b18 b7)
         (on b7 b9)
         (on b9 b17)
         (on b17 b24)
         (on b24 b4)
         (on b4 b3)
         (on b3 b22)
         (on b22 b11)
         (on b11 b25)
         (on b25 b19)
         (on b19 b5))))