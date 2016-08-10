(define
 (problem pfile_02_035)
 (:domain blocks)
 (:objects arm1 arm2 - ARM
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
           b26
           b27
           b28
           b29
           b30
           b31
           b32
           b33
           b34
           b35
           - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (clear b28)
  (on-table b24)
  (on b28 b19)
  (on b19 b11)
  (on b11 b26)
  (on b26 b24)
  (clear b1)
  (on-table b21)
  (on b1 b31)
  (on b31 b2)
  (on b2 b25)
  (on b25 b12)
  (on b12 b16)
  (on b16 b30)
  (on b30 b35)
  (on b35 b21)
  (clear b14)
  (on-table b20)
  (on b14 b18)
  (on b18 b4)
  (on b4 b3)
  (on b3 b34)
  (on b34 b23)
  (on b23 b20)
  (clear b13)
  (on-table b13)
  (clear b7)
  (on-table b5)
  (on b7 b8)
  (on b8 b9)
  (on b9 b6)
  (on b6 b27)
  (on b27 b32)
  (on b32 b29)
  (on b29 b22)
  (on b22 b17)
  (on b17 b33)
  (on b33 b10)
  (on b10 b15)
  (on b15 b5))
 (:goal (and
         (clear b33)
         (on-table b32)
         (on b33 b30)
         (on b30 b19)
         (on b19 b32)
         (clear b20)
         (on-table b28)
         (on b20 b4)
         (on b4 b24)
         (on b24 b17)
         (on b17 b28)
         (clear b2)
         (on-table b25)
         (on b2 b8)
         (on b8 b34)
         (on b34 b25)
         (clear b1)
         (on-table b22)
         (on b1 b3)
         (on b3 b14)
         (on b14 b23)
         (on b23 b31)
         (on b31 b16)
         (on b16 b15)
         (on b15 b10)
         (on b10 b22)
         (clear b27)
         (on-table b18)
         (on b27 b21)
         (on b21 b35)
         (on b35 b29)
         (on b29 b5)
         (on b5 b18)
         (clear b26)
         (on-table b13)
         (on b26 b13)
         (clear b7)
         (on-table b12)
         (on b7 b9)
         (on b9 b6)
         (on b6 b12)
         (clear b11)
         (on-table b11)))
                      (:tasks (task0 (achieve-goals arm1)))
                      (:tasks (task1 (achieve-goals arm2)))
)