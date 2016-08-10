(define
 (problem pfile_04_090)
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
           b86
           b87
           b88
           b89
           b90
           - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (clear b35)
  (on-table b85)
  (on b35 b88)
  (on b88 b73)
  (on b73 b49)
  (on b49 b1)
  (on b1 b42)
  (on b42 b56)
  (on b56 b25)
  (on b25 b15)
  (on b15 b85)
  (clear b13)
  (on-table b78)
  (on b13 b20)
  (on b20 b78)
  (clear b37)
  (on-table b72)
  (on b37 b63)
  (on b63 b61)
  (on b61 b22)
  (on b22 b71)
  (on b71 b11)
  (on b11 b75)
  (on b75 b74)
  (on b74 b12)
  (on b12 b83)
  (on b83 b24)
  (on b24 b76)
  (on b76 b10)
  (on b10 b80)
  (on b80 b54)
  (on b54 b2)
  (on b2 b62)
  (on b62 b72)
  (clear b68)
  (on-table b69)
  (on b68 b90)
  (on b90 b69)
  (clear b65)
  (on-table b65)
  (clear b44)
  (on-table b39)
  (on b44 b32)
  (on b32 b7)
  (on b7 b55)
  (on b55 b30)
  (on b30 b40)
  (on b40 b66)
  (on b66 b39)
  (clear b41)
  (on-table b31)
  (on b41 b58)
  (on b58 b57)
  (on b57 b43)
  (on b43 b9)
  (on b9 b31)
  (clear b16)
  (on-table b28)
  (on b16 b28)
  (clear b51)
  (on-table b21)
  (on b51 b50)
  (on b50 b17)
  (on b17 b60)
  (on b60 b33)
  (on b33 b46)
  (on b46 b89)
  (on b89 b26)
  (on b26 b8)
  (on b8 b19)
  (on b19 b38)
  (on b38 b18)
  (on b18 b34)
  (on b34 b48)
  (on b48 b52)
  (on b52 b4)
  (on b4 b67)
  (on b67 b70)
  (on b70 b81)
  (on b81 b84)
  (on b84 b27)
  (on b27 b64)
  (on b64 b14)
  (on b14 b53)
  (on b53 b23)
  (on b23 b87)
  (on b87 b21)
  (clear b59)
  (on-table b6)
  (on b59 b79)
  (on b79 b29)
  (on b29 b36)
  (on b36 b86)
  (on b86 b3)
  (on b3 b77)
  (on b77 b5)
  (on b5 b45)
  (on b45 b47)
  (on b47 b82)
  (on b82 b6))
 (:goal (and
         (clear b73)
         (on-table b87)
         (on b73 b80)
         (on b80 b6)
         (on b6 b5)
         (on b5 b34)
         (on b34 b55)
         (on b55 b3)
         (on b3 b40)
         (on b40 b71)
         (on b71 b87)
         (clear b62)
         (on-table b83)
         (on b62 b83)
         (clear b54)
         (on-table b75)
         (on b54 b30)
         (on b30 b22)
         (on b22 b29)
         (on b29 b11)
         (on b11 b44)
         (on b44 b52)
         (on b52 b43)
         (on b43 b67)
         (on b67 b24)
         (on b24 b79)
         (on b79 b75)
         (clear b36)
         (on-table b63)
         (on b36 b49)
         (on b49 b88)
         (on b88 b25)
         (on b25 b12)
         (on b12 b63)
         (clear b48)
         (on-table b48)
         (clear b47)
         (on-table b47)
         (clear b90)
         (on-table b46)
         (on b90 b31)
         (on b31 b26)
         (on b26 b9)
         (on b9 b86)
         (on b86 b59)
         (on b59 b60)
         (on b60 b2)
         (on b2 b41)
         (on b41 b46)
         (clear b89)
         (on-table b45)
         (on b89 b45)
         (clear b19)
         (on-table b27)
         (on b19 b85)
         (on b85 b32)
         (on b32 b50)
         (on b50 b10)
         (on b10 b78)
         (on b78 b15)
         (on b15 b65)
         (on b65 b20)
         (on b20 b84)
         (on b84 b51)
         (on b51 b38)
         (on b38 b27)
         (clear b70)
         (on-table b23)
         (on b70 b81)
         (on b81 b17)
         (on b17 b1)
         (on b1 b13)
         (on b13 b66)
         (on b66 b57)
         (on b57 b69)
         (on b69 b16)
         (on b16 b76)
         (on b76 b77)
         (on b77 b14)
         (on b14 b33)
         (on b33 b72)
         (on b72 b39)
         (on b39 b53)
         (on b53 b28)
         (on b28 b64)
         (on b64 b56)
         (on b56 b23)
         (clear b68)
         (on-table b18)
         (on b68 b74)
         (on b74 b8)
         (on b8 b7)
         (on b7 b35)
         (on b35 b42)
         (on b42 b18)
         (clear b21)
         (on-table b4)
         (on b21 b82)
         (on b82 b37)
         (on b37 b58)
         (on b58 b61)
         (on b61 b4))))