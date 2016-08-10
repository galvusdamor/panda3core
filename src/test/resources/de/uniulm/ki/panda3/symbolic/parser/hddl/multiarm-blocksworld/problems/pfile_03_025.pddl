(define
 (problem pfile_03_025)
 (:domain blocks)
 (:objects arm1 arm2 arm3 - ARM
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
  (clear b7)
  (on-table b12)
  (on b7 b6)
  (on b6 b5)
  (on b5 b24)
  (on b24 b22)
  (on b22 b15)
  (on b15 b23)
  (on b23 b18)
  (on b18 b25)
  (on b25 b12)
  (clear b4)
  (on-table b9)
  (on b4 b1)
  (on b1 b14)
  (on b14 b20)
  (on b20 b19)
  (on b19 b10)
  (on b10 b2)
  (on b2 b9)
  (clear b21)
  (on-table b8)
  (on b21 b8)
  (clear b13)
  (on-table b3)
  (on b13 b17)
  (on b17 b16)
  (on b16 b11)
  (on b11 b3))
 (:goal (and
         (clear b21)
         (on-table b19)
         (on b21 b24)
         (on b24 b22)
         (on b22 b20)
         (on b20 b19)
         (clear b12)
         (on-table b17)
         (on b12 b11)
         (on b11 b23)
         (on b23 b6)
         (on b6 b18)
         (on b18 b7)
         (on b7 b8)
         (on b8 b5)
         (on b5 b13)
         (on b13 b17)
         (clear b3)
         (on-table b14)
         (on b3 b14)
         (clear b16)
         (on-table b4)
         (on b16 b15)
         (on b15 b2)
         (on b2 b9)
         (on b9 b10)
         (on b10 b1)
         (on b1 b25)
         (on b25 b4)))
                             (:tasks (task0 (achieve-goals arm1)))
                             (:tasks (task1 (achieve-goals arm2)))
                             (:tasks (task2 (achieve-goals arm3)))
)