(define
 (problem pfile_04_050)
 (:domain blocks)
 (:objects arm1 arm2 arm3 arm4 - ARM
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
           b41
           b42
           b43
           b44
           b45
           b46
           b47
           b48
           b49
           b50
           - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (clear b5)
  (on-table b35)
  (on b5 b50)
  (on b50 b21)
  (on b21 b14)
  (on b14 b30)
  (on b30 b35)
  (clear b32)
  (on-table b32)
  (clear b34)
  (on-table b25)
  (on b34 b25)
  (clear b7)
  (on-table b23)
  (on b7 b19)
  (on b19 b40)
  (on b40 b16)
  (on b16 b37)
  (on b37 b43)
  (on b43 b28)
  (on b28 b2)
  (on b2 b1)
  (on b1 b48)
  (on b48 b23)
  (clear b45)
  (on-table b20)
  (on b45 b44)
  (on b44 b46)
  (on b46 b17)
  (on b17 b38)
  (on b38 b20)
  (clear b29)
  (on-table b11)
  (on b29 b13)
  (on b13 b26)
  (on b26 b31)
  (on b31 b18)
  (on b18 b27)
  (on b27 b49)
  (on b49 b10)
  (on b10 b4)
  (on b4 b24)
  (on b24 b36)
  (on b36 b12)
  (on b12 b11)
  (clear b3)
  (on-table b9)
  (on b3 b47)
  (on b47 b22)
  (on b22 b9)
  (clear b15)
  (on-table b8)
  (on b15 b33)
  (on b33 b6)
  (on b6 b41)
  (on b41 b42)
  (on b42 b39)
  (on b39 b8))
 (:goal (and
         (clear b2)
         (on-table b39)
         (on b2 b31)
         (on b31 b46)
         (on b46 b43)
         (on b43 b4)
         (on b4 b3)
         (on b3 b17)
         (on b17 b34)
         (on b34 b48)
         (on b48 b39)
         (clear b38)
         (on-table b37)
         (on b38 b47)
         (on b47 b11)
         (on b11 b50)
         (on b50 b30)
         (on b30 b37)
         (clear b32)
         (on-table b32)
         (clear b7)
         (on-table b29)
         (on b7 b27)
         (on b27 b49)
         (on b49 b29)
         (clear b20)
         (on-table b28)
         (on b20 b15)
         (on b15 b10)
         (on b10 b40)
         (on b40 b13)
         (on b13 b26)
         (on b26 b25)
         (on b25 b24)
         (on b24 b36)
         (on b36 b9)
         (on b9 b28)
         (clear b22)
         (on-table b21)
         (on b22 b42)
         (on b42 b1)
         (on b1 b21)
         (clear b8)
         (on-table b5)
         (on b8 b45)
         (on b45 b6)
         (on b6 b18)
         (on b18 b35)
         (on b35 b12)
         (on b12 b41)
         (on b41 b33)
         (on b33 b23)
         (on b23 b16)
         (on b16 b19)
         (on b19 b14)
         (on b14 b44)
         (on b44 b5)))
           (:tasks (task0 (achieve-goals arm1)))
           (:tasks (task1 (achieve-goals arm2)))
           (:tasks (task2 (achieve-goals arm3)))
           (:tasks (task3 (achieve-goals arm4)))
)