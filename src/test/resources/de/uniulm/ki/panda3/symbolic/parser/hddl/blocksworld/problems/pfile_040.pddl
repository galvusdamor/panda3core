(define
 (problem pfile_040)
 (:domain blocks)
 (:objects b1
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
           b36
           b37
           b38
           b39
           b40
           - BLOCK)
 (:init
  (hand-empty)
  (clear b4)
  (on-table b40)
  (on b4 b14)
  (on b14 b40)
  (clear b23)
  (on-table b38)
  (on b23 b38)
  (clear b28)
  (on-table b37)
  (on b28 b5)
  (on b5 b18)
  (on b18 b26)
  (on b26 b19)
  (on b19 b1)
  (on b1 b22)
  (on b22 b31)
  (on b31 b34)
  (on b34 b9)
  (on b9 b10)
  (on b10 b12)
  (on b12 b37)
  (clear b8)
  (on-table b36)
  (on b8 b13)
  (on b13 b11)
  (on b11 b25)
  (on b25 b33)
  (on b33 b29)
  (on b29 b7)
  (on b7 b35)
  (on b35 b36)
  (clear b30)
  (on-table b30)
  (clear b20)
  (on-table b27)
  (on b20 b15)
  (on b15 b2)
  (on b2 b39)
  (on b39 b32)
  (on b32 b6)
  (on b6 b24)
  (on b24 b21)
  (on b21 b27)
  (clear b17)
  (on-table b3)
  (on b17 b16)
  (on b16 b3))
 (:goal (and
         (clear b35)
         (on-table b38)
         (on b35 b36)
         (on b36 b23)
         (on b23 b38)
         (clear b13)
         (on-table b27)
         (on b13 b20)
         (on b20 b5)
         (on b5 b40)
         (on b40 b32)
         (on b32 b7)
         (on b7 b14)
         (on b14 b27)
         (clear b34)
         (on-table b12)
         (on b34 b29)
         (on b29 b24)
         (on b24 b10)
         (on b10 b25)
         (on b25 b33)
         (on b33 b2)
         (on b2 b11)
         (on b11 b18)
         (on b18 b9)
         (on b9 b17)
         (on b17 b4)
         (on b4 b3)
         (on b3 b37)
         (on b37 b30)
         (on b30 b19)
         (on b19 b8)
         (on b8 b31)
         (on b31 b26)
         (on b26 b21)
         (on b21 b28)
         (on b28 b39)
         (on b39 b1)
         (on b1 b12)
         (clear b22)
         (on-table b6)
         (on b22 b15)
         (on b15 b16)
         (on b16 b6)))
             (:tasks (task0 (achieve-goals)))
)