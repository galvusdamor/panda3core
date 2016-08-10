(define
 (problem pfile_06_055)
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
           b51
           b52
           b53
           b54
           b55
           - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (hand-empty arm5)
  (hand-empty arm6)
  (clear b55)
  (on-table b55)
  (clear b12)
  (on-table b33)
  (on b12 b46)
  (on b46 b35)
  (on b35 b17)
  (on b17 b33)
  (clear b22)
  (on-table b32)
  (on b22 b32)
  (clear b50)
  (on-table b31)
  (on b50 b7)
  (on b7 b52)
  (on b52 b16)
  (on b16 b29)
  (on b29 b25)
  (on b25 b19)
  (on b19 b3)
  (on b3 b5)
  (on b5 b27)
  (on b27 b6)
  (on b6 b31)
  (clear b24)
  (on-table b26)
  (on b24 b30)
  (on b30 b26)
  (clear b11)
  (on-table b10)
  (on b11 b23)
  (on b23 b10)
  (clear b4)
  (on-table b4)
  (clear b36)
  (on-table b1)
  (on b36 b48)
  (on b48 b15)
  (on b15 b39)
  (on b39 b54)
  (on b54 b20)
  (on b20 b45)
  (on b45 b43)
  (on b43 b53)
  (on b53 b9)
  (on b9 b34)
  (on b34 b47)
  (on b47 b40)
  (on b40 b13)
  (on b13 b44)
  (on b44 b18)
  (on b18 b37)
  (on b37 b28)
  (on b28 b21)
  (on b21 b2)
  (on b2 b8)
  (on b8 b38)
  (on b38 b14)
  (on b14 b49)
  (on b49 b41)
  (on b41 b51)
  (on b51 b42)
  (on b42 b1))
 (:goal (and
         (clear b48)
         (on-table b48)
         (clear b5)
         (on-table b42)
         (on b5 b6)
         (on b6 b12)
         (on b12 b16)
         (on b16 b29)
         (on b29 b37)
         (on b37 b9)
         (on b9 b17)
         (on b17 b52)
         (on b52 b41)
         (on b41 b2)
         (on b2 b39)
         (on b39 b36)
         (on b36 b30)
         (on b30 b46)
         (on b46 b32)
         (on b32 b45)
         (on b45 b22)
         (on b22 b19)
         (on b19 b55)
         (on b55 b49)
         (on b49 b34)
         (on b34 b8)
         (on b8 b44)
         (on b44 b50)
         (on b50 b53)
         (on b53 b51)
         (on b51 b26)
         (on b26 b42)
         (clear b27)
         (on-table b27)
         (clear b18)
         (on-table b18)
         (clear b21)
         (on-table b4)
         (on b21 b4)
         (clear b11)
         (on-table b3)
         (on b11 b13)
         (on b13 b28)
         (on b28 b54)
         (on b54 b25)
         (on b25 b23)
         (on b23 b15)
         (on b15 b35)
         (on b35 b40)
         (on b40 b20)
         (on b20 b10)
         (on b10 b24)
         (on b24 b47)
         (on b47 b14)
         (on b14 b38)
         (on b38 b31)
         (on b31 b33)
         (on b33 b43)
         (on b43 b7)
         (on b7 b3)
         (clear b1)
         (on-table b1))))