(define
 (problem pfile_050)
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
  (hand-empty)
  (clear b37)
  (on-table b41)
  (on b37 b13)
  (on b13 b41)
  (clear b44)
  (on-table b34)
  (on b44 b32)
  (on b32 b34)
  (clear b28)
  (on-table b28)
  (clear b45)
  (on-table b25)
  (on b45 b27)
  (on b27 b2)
  (on b2 b39)
  (on b39 b36)
  (on b36 b46)
  (on b46 b19)
  (on b19 b25)
  (clear b49)
  (on-table b23)
  (on b49 b22)
  (on b22 b31)
  (on b31 b23)
  (clear b14)
  (on-table b14)
  (clear b33)
  (on-table b10)
  (on b33 b26)
  (on b26 b47)
  (on b47 b43)
  (on b43 b18)
  (on b18 b20)
  (on b20 b4)
  (on b4 b50)
  (on b50 b10)
  (clear b17)
  (on-table b7)
  (on b17 b9)
  (on b9 b15)
  (on b15 b21)
  (on b21 b8)
  (on b8 b5)
  (on b5 b42)
  (on b42 b6)
  (on b6 b35)
  (on b35 b30)
  (on b30 b3)
  (on b3 b48)
  (on b48 b11)
  (on b11 b12)
  (on b12 b16)
  (on b16 b38)
  (on b38 b1)
  (on b1 b24)
  (on b24 b29)
  (on b29 b40)
  (on b40 b7))
 (:goal (and
         (clear b50)
         (on-table b41)
         (on b50 b1)
         (on b1 b34)
         (on b34 b7)
         (on b7 b33)
         (on b33 b19)
         (on b19 b29)
         (on b29 b41)
         (clear b44)
         (on-table b30)
         (on b44 b16)
         (on b16 b10)
         (on b10 b38)
         (on b38 b25)
         (on b25 b31)
         (on b31 b37)
         (on b37 b49)
         (on b49 b2)
         (on b2 b32)
         (on b32 b3)
         (on b3 b24)
         (on b24 b40)
         (on b40 b42)
         (on b42 b35)
         (on b35 b6)
         (on b6 b30)
         (clear b14)
         (on-table b17)
         (on b14 b47)
         (on b47 b17)
         (clear b13)
         (on-table b12)
         (on b13 b4)
         (on b4 b36)
         (on b36 b21)
         (on b21 b12)
         (clear b43)
         (on-table b11)
         (on b43 b22)
         (on b22 b26)
         (on b26 b8)
         (on b8 b15)
         (on b15 b20)
         (on b20 b46)
         (on b46 b9)
         (on b9 b48)
         (on b48 b5)
         (on b5 b27)
         (on b27 b45)
         (on b45 b23)
         (on b23 b39)
         (on b39 b18)
         (on b18 b28)
         (on b28 b11)))
             (:tasks (task0 (achieve-goals)))
)