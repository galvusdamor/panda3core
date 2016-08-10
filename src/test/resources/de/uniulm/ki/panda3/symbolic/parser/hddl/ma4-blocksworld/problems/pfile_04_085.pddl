(define
 (problem pfile_04_085)
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
           b71
           b72
           b73
           b74
           b75
           b76
           b77
           b78
           b79
           b80
           b81
           b82
           b83
           b84
           b85
           - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (clear b17)
  (on-table b81)
  (on b17 b61)
  (on b61 b41)
  (on b41 b50)
  (on b50 b78)
  (on b78 b37)
  (on b37 b74)
  (on b74 b55)
  (on b55 b70)
  (on b70 b81)
  (clear b44)
  (on-table b80)
  (on b44 b6)
  (on b6 b10)
  (on b10 b11)
  (on b11 b80)
  (clear b42)
  (on-table b77)
  (on b42 b76)
  (on b76 b53)
  (on b53 b8)
  (on b8 b21)
  (on b21 b66)
  (on b66 b46)
  (on b46 b2)
  (on b2 b23)
  (on b23 b19)
  (on b19 b54)
  (on b54 b62)
  (on b62 b58)
  (on b58 b29)
  (on b29 b83)
  (on b83 b85)
  (on b85 b73)
  (on b73 b13)
  (on b13 b43)
  (on b43 b77)
  (clear b30)
  (on-table b75)
  (on b30 b68)
  (on b68 b27)
  (on b27 b63)
  (on b63 b67)
  (on b67 b24)
  (on b24 b51)
  (on b51 b34)
  (on b34 b52)
  (on b52 b33)
  (on b33 b64)
  (on b64 b75)
  (clear b31)
  (on-table b65)
  (on b31 b3)
  (on b3 b72)
  (on b72 b71)
  (on b71 b60)
  (on b60 b84)
  (on b84 b48)
  (on b48 b1)
  (on b1 b4)
  (on b4 b14)
  (on b14 b20)
  (on b20 b32)
  (on b32 b22)
  (on b22 b65)
  (clear b82)
  (on-table b59)
  (on b82 b12)
  (on b12 b59)
  (clear b40)
  (on-table b47)
  (on b40 b79)
  (on b79 b25)
  (on b25 b49)
  (on b49 b47)
  (clear b69)
  (on-table b39)
  (on b69 b56)
  (on b56 b36)
  (on b36 b15)
  (on b15 b38)
  (on b38 b18)
  (on b18 b45)
  (on b45 b39)
  (clear b28)
  (on-table b28)
  (clear b57)
  (on-table b9)
  (on b57 b16)
  (on b16 b35)
  (on b35 b7)
  (on b7 b9)
  (clear b26)
  (on-table b5)
  (on b26 b5))
 (:goal (and
         (clear b27)
         (on-table b82)
         (on b27 b38)
         (on b38 b3)
         (on b3 b68)
         (on b68 b71)
         (on b71 b74)
         (on b74 b56)
         (on b56 b8)
         (on b8 b16)
         (on b16 b82)
         (clear b72)
         (on-table b69)
         (on b72 b79)
         (on b79 b12)
         (on b12 b54)
         (on b54 b59)
         (on b59 b23)
         (on b23 b70)
         (on b70 b69)
         (clear b66)
         (on-table b51)
         (on b66 b4)
         (on b4 b11)
         (on b11 b80)
         (on b80 b75)
         (on b75 b84)
         (on b84 b51)
         (clear b67)
         (on-table b37)
         (on b67 b21)
         (on b21 b5)
         (on b5 b13)
         (on b13 b35)
         (on b35 b40)
         (on b40 b15)
         (on b15 b28)
         (on b28 b45)
         (on b45 b60)
         (on b60 b29)
         (on b29 b52)
         (on b52 b14)
         (on b14 b34)
         (on b34 b49)
         (on b49 b47)
         (on b47 b2)
         (on b2 b61)
         (on b61 b20)
         (on b20 b31)
         (on b31 b64)
         (on b64 b78)
         (on b78 b76)
         (on b76 b25)
         (on b25 b37)
         (clear b50)
         (on-table b10)
         (on b50 b46)
         (on b46 b22)
         (on b22 b57)
         (on b57 b36)
         (on b36 b19)
         (on b19 b63)
         (on b63 b17)
         (on b17 b44)
         (on b44 b77)
         (on b77 b41)
         (on b41 b26)
         (on b26 b83)
         (on b83 b7)
         (on b7 b30)
         (on b30 b10)
         (clear b85)
         (on-table b6)
         (on b85 b39)
         (on b39 b6)
         (clear b43)
         (on-table b1)
         (on b43 b18)
         (on b18 b53)
         (on b53 b65)
         (on b65 b9)
         (on b9 b62)
         (on b62 b33)
         (on b33 b48)
         (on b48 b81)
         (on b81 b73)
         (on b73 b55)
         (on b55 b58)
         (on b58 b32)
         (on b32 b24)
         (on b24 b42)
         (on b42 b1))))