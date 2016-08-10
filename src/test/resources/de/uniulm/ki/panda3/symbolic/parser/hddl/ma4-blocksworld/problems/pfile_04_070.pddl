(define
 (problem pfile_04_070)
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
           b51
           b52
           b53
           b54
           b55
           b56
           b57
           b58
           b59
           b60
           b61
           b62
           b63
           b64
           b65
           b66
           b67
           b68
           b69
           b70
           - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (clear b44)
  (on-table b68)
  (on b44 b43)
  (on b43 b23)
  (on b23 b64)
  (on b64 b60)
  (on b60 b55)
  (on b55 b37)
  (on b37 b27)
  (on b27 b45)
  (on b45 b20)
  (on b20 b1)
  (on b1 b12)
  (on b12 b14)
  (on b14 b52)
  (on b52 b70)
  (on b70 b65)
  (on b65 b18)
  (on b18 b31)
  (on b31 b9)
  (on b9 b53)
  (on b53 b68)
  (clear b67)
  (on-table b67)
  (clear b21)
  (on-table b61)
  (on b21 b42)
  (on b42 b61)
  (clear b11)
  (on-table b58)
  (on b11 b41)
  (on b41 b2)
  (on b2 b16)
  (on b16 b58)
  (clear b46)
  (on-table b56)
  (on b46 b5)
  (on b5 b56)
  (clear b51)
  (on-table b54)
  (on b51 b28)
  (on b28 b25)
  (on b25 b8)
  (on b8 b34)
  (on b34 b3)
  (on b3 b33)
  (on b33 b26)
  (on b26 b4)
  (on b4 b47)
  (on b47 b36)
  (on b36 b22)
  (on b22 b48)
  (on b48 b39)
  (on b39 b7)
  (on b7 b54)
  (clear b15)
  (on-table b49)
  (on b15 b10)
  (on b10 b6)
  (on b6 b35)
  (on b35 b63)
  (on b63 b50)
  (on b50 b38)
  (on b38 b66)
  (on b66 b62)
  (on b62 b59)
  (on b59 b49)
  (clear b29)
  (on-table b29)
  (clear b32)
  (on-table b24)
  (on b32 b57)
  (on b57 b24)
  (clear b13)
  (on-table b19)
  (on b13 b69)
  (on b69 b19)
  (clear b40)
  (on-table b17)
  (on b40 b30)
  (on b30 b17))
 (:goal (and
         (clear b70)
         (on-table b70)
         (clear b49)
         (on-table b69)
         (on b49 b36)
         (on b36 b11)
         (on b11 b69)
         (clear b59)
         (on-table b66)
         (on b59 b10)
         (on b10 b58)
         (on b58 b66)
         (clear b53)
         (on-table b53)
         (clear b19)
         (on-table b46)
         (on b19 b27)
         (on b27 b15)
         (on b15 b65)
         (on b65 b2)
         (on b2 b64)
         (on b64 b44)
         (on b44 b24)
         (on b24 b22)
         (on b22 b47)
         (on b47 b39)
         (on b39 b33)
         (on b33 b57)
         (on b57 b32)
         (on b32 b46)
         (clear b29)
         (on-table b42)
         (on b29 b16)
         (on b16 b60)
         (on b60 b67)
         (on b67 b43)
         (on b43 b28)
         (on b28 b54)
         (on b54 b42)
         (clear b55)
         (on-table b25)
         (on b55 b17)
         (on b17 b62)
         (on b62 b34)
         (on b34 b30)
         (on b30 b50)
         (on b50 b48)
         (on b48 b56)
         (on b56 b8)
         (on b8 b26)
         (on b26 b21)
         (on b21 b6)
         (on b6 b5)
         (on b5 b45)
         (on b45 b38)
         (on b38 b25)
         (clear b37)
         (on-table b23)
         (on b37 b35)
         (on b35 b9)
         (on b9 b52)
         (on b52 b68)
         (on b68 b23)
         (clear b61)
         (on-table b18)
         (on b61 b40)
         (on b40 b18)
         (clear b41)
         (on-table b12)
         (on b41 b31)
         (on b31 b13)
         (on b13 b51)
         (on b51 b20)
         (on b20 b1)
         (on b1 b12)
         (clear b63)
         (on-table b3)
         (on b63 b4)
         (on b4 b7)
         (on b7 b14)
         (on b14 b3))))