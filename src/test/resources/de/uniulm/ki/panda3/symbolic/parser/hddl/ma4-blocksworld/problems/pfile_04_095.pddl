(define
 (problem pfile_04_095)
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
           b91
           b92
           b93
           b94
           b95
           - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (clear b41)
  (on-table b79)
  (on b41 b77)
  (on b77 b2)
  (on b2 b15)
  (on b15 b21)
  (on b21 b10)
  (on b10 b26)
  (on b26 b35)
  (on b35 b81)
  (on b81 b50)
  (on b50 b92)
  (on b92 b3)
  (on b3 b63)
  (on b63 b27)
  (on b27 b71)
  (on b71 b14)
  (on b14 b31)
  (on b31 b65)
  (on b65 b13)
  (on b13 b68)
  (on b68 b43)
  (on b43 b45)
  (on b45 b79)
  (clear b16)
  (on-table b70)
  (on b16 b40)
  (on b40 b53)
  (on b53 b70)
  (clear b85)
  (on-table b64)
  (on b85 b66)
  (on b66 b48)
  (on b48 b87)
  (on b87 b6)
  (on b6 b33)
  (on b33 b42)
  (on b42 b37)
  (on b37 b69)
  (on b69 b76)
  (on b76 b75)
  (on b75 b49)
  (on b49 b95)
  (on b95 b19)
  (on b19 b64)
  (clear b32)
  (on-table b58)
  (on b32 b60)
  (on b60 b44)
  (on b44 b73)
  (on b73 b25)
  (on b25 b94)
  (on b94 b83)
  (on b83 b78)
  (on b78 b58)
  (clear b18)
  (on-table b52)
  (on b18 b62)
  (on b62 b8)
  (on b8 b17)
  (on b17 b84)
  (on b84 b30)
  (on b30 b59)
  (on b59 b89)
  (on b89 b1)
  (on b1 b29)
  (on b29 b61)
  (on b61 b86)
  (on b86 b23)
  (on b23 b55)
  (on b55 b90)
  (on b90 b11)
  (on b11 b82)
  (on b82 b47)
  (on b47 b5)
  (on b5 b24)
  (on b24 b67)
  (on b67 b22)
  (on b22 b54)
  (on b54 b56)
  (on b56 b80)
  (on b80 b74)
  (on b74 b12)
  (on b12 b4)
  (on b4 b9)
  (on b9 b34)
  (on b34 b51)
  (on b51 b28)
  (on b28 b52)
  (clear b91)
  (on-table b36)
  (on b91 b36)
  (clear b93)
  (on-table b20)
  (on b93 b46)
  (on b46 b88)
  (on b88 b7)
  (on b7 b38)
  (on b38 b57)
  (on b57 b39)
  (on b39 b72)
  (on b72 b20))
 (:goal (and
         (clear b6)
         (on-table b94)
         (on b6 b24)
         (on b24 b68)
         (on b68 b86)
         (on b86 b28)
         (on b28 b81)
         (on b81 b94)
         (clear b29)
         (on-table b75)
         (on b29 b17)
         (on b17 b93)
         (on b93 b37)
         (on b37 b78)
         (on b78 b77)
         (on b77 b57)
         (on b57 b55)
         (on b55 b42)
         (on b42 b46)
         (on b46 b71)
         (on b71 b74)
         (on b74 b54)
         (on b54 b75)
         (clear b25)
         (on-table b73)
         (on b25 b80)
         (on b80 b56)
         (on b56 b61)
         (on b61 b89)
         (on b89 b40)
         (on b40 b47)
         (on b47 b91)
         (on b91 b38)
         (on b38 b26)
         (on b26 b30)
         (on b30 b33)
         (on b33 b39)
         (on b39 b90)
         (on b90 b53)
         (on b53 b13)
         (on b13 b3)
         (on b3 b20)
         (on b20 b36)
         (on b36 b35)
         (on b35 b73)
         (clear b50)
         (on-table b72)
         (on b50 b8)
         (on b8 b76)
         (on b76 b72)
         (clear b59)
         (on-table b34)
         (on b59 b16)
         (on b16 b1)
         (on b1 b67)
         (on b67 b43)
         (on b43 b27)
         (on b27 b34)
         (clear b9)
         (on-table b19)
         (on b9 b45)
         (on b45 b70)
         (on b70 b84)
         (on b84 b62)
         (on b62 b85)
         (on b85 b88)
         (on b88 b52)
         (on b52 b19)
         (clear b44)
         (on-table b7)
         (on b44 b92)
         (on b92 b15)
         (on b15 b49)
         (on b49 b11)
         (on b11 b58)
         (on b58 b48)
         (on b48 b7)
         (clear b10)
         (on-table b5)
         (on b10 b4)
         (on b4 b32)
         (on b32 b87)
         (on b87 b65)
         (on b65 b83)
         (on b83 b95)
         (on b95 b5)
         (clear b66)
         (on-table b2)
         (on b66 b64)
         (on b64 b21)
         (on b21 b60)
         (on b60 b79)
         (on b79 b82)
         (on b82 b22)
         (on b22 b63)
         (on b63 b31)
         (on b31 b69)
         (on b69 b41)
         (on b41 b23)
         (on b23 b12)
         (on b12 b14)
         (on b14 b18)
         (on b18 b51)
         (on b51 b2))))