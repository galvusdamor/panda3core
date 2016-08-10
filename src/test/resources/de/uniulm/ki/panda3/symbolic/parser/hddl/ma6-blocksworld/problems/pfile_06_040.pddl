(define
 (problem pfile_06_040)
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
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (hand-empty arm5)
  (hand-empty arm6)
  (clear b4)
  (on-table b30)
  (on b4 b8)
  (on b8 b29)
  (on b29 b16)
  (on b16 b32)
  (on b32 b34)
  (on b34 b37)
  (on b37 b27)
  (on b27 b7)
  (on b7 b38)
  (on b38 b6)
  (on b6 b33)
  (on b33 b30)
  (clear b25)
  (on-table b25)
  (clear b18)
  (on-table b13)
  (on b18 b9)
  (on b9 b20)
  (on b20 b1)
  (on b1 b36)
  (on b36 b15)
  (on b15 b13)
  (clear b23)
  (on-table b12)
  (on b23 b14)
  (on b14 b2)
  (on b2 b31)
  (on b31 b28)
  (on b28 b35)
  (on b35 b12)
  (clear b17)
  (on-table b3)
  (on b17 b40)
  (on b40 b10)
  (on b10 b19)
  (on b19 b11)
  (on b11 b26)
  (on b26 b22)
  (on b22 b24)
  (on b24 b21)
  (on b21 b5)
  (on b5 b39)
  (on b39 b3))
 (:goal (and
         (clear b16)
         (on-table b31)
         (on b16 b11)
         (on b11 b27)
         (on b27 b1)
         (on b1 b35)
         (on b35 b20)
         (on b20 b34)
         (on b34 b31)
         (clear b26)
         (on-table b30)
         (on b26 b21)
         (on b21 b32)
         (on b32 b30)
         (clear b13)
         (on-table b29)
         (on b13 b37)
         (on b37 b6)
         (on b6 b12)
         (on b12 b8)
         (on b8 b39)
         (on b39 b14)
         (on b14 b36)
         (on b36 b29)
         (clear b23)
         (on-table b23)
         (clear b18)
         (on-table b17)
         (on b18 b4)
         (on b4 b3)
         (on b3 b33)
         (on b33 b17)
         (clear b7)
         (on-table b15)
         (on b7 b24)
         (on b24 b19)
         (on b19 b28)
         (on b28 b40)
         (on b40 b38)
         (on b38 b9)
         (on b9 b5)
         (on b5 b22)
         (on b22 b2)
         (on b2 b10)
         (on b10 b25)
         (on b25 b15))))