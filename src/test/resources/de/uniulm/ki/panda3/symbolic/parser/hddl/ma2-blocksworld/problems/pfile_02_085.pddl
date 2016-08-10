(define
 (problem pfile_02_085)
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
  (clear b4)
  (on-table b83)
  (on b4 b83)
  (clear b50)
  (on-table b55)
  (on b50 b61)
  (on b61 b13)
  (on b13 b85)
  (on b85 b46)
  (on b46 b35)
  (on b35 b56)
  (on b56 b65)
  (on b65 b79)
  (on b79 b55)
  (clear b38)
  (on-table b37)
  (on b38 b81)
  (on b81 b8)
  (on b8 b69)
  (on b69 b78)
  (on b78 b44)
  (on b44 b77)
  (on b77 b22)
  (on b22 b53)
  (on b53 b58)
  (on b58 b33)
  (on b33 b60)
  (on b60 b10)
  (on b10 b1)
  (on b1 b14)
  (on b14 b74)
  (on b74 b9)
  (on b9 b37)
  (clear b39)
  (on-table b26)
  (on b39 b32)
  (on b32 b49)
  (on b49 b64)
  (on b64 b70)
  (on b70 b73)
  (on b73 b7)
  (on b7 b15)
  (on b15 b80)
  (on b80 b18)
  (on b18 b5)
  (on b5 b43)
  (on b43 b36)
  (on b36 b11)
  (on b11 b54)
  (on b54 b76)
  (on b76 b47)
  (on b47 b75)
  (on b75 b63)
  (on b63 b16)
  (on b16 b23)
  (on b23 b72)
  (on b72 b6)
  (on b6 b27)
  (on b27 b48)
  (on b48 b31)
  (on b31 b26)
  (clear b19)
  (on-table b21)
  (on b19 b45)
  (on b45 b40)
  (on b40 b12)
  (on b12 b29)
  (on b29 b24)
  (on b24 b62)
  (on b62 b66)
  (on b66 b67)
  (on b67 b57)
  (on b57 b71)
  (on b71 b2)
  (on b2 b30)
  (on b30 b25)
  (on b25 b59)
  (on b59 b17)
  (on b17 b42)
  (on b42 b21)
  (clear b52)
  (on-table b20)
  (on b52 b68)
  (on b68 b28)
  (on b28 b34)
  (on b34 b84)
  (on b84 b82)
  (on b82 b3)
  (on b3 b41)
  (on b41 b51)
  (on b51 b20))
 (:goal (and
         (clear b37)
         (on-table b77)
         (on b37 b55)
         (on b55 b65)
         (on b65 b60)
         (on b60 b5)
         (on b5 b36)
         (on b36 b64)
         (on b64 b18)
         (on b18 b63)
         (on b63 b77)
         (clear b38)
         (on-table b57)
         (on b38 b2)
         (on b2 b83)
         (on b83 b4)
         (on b4 b74)
         (on b74 b57)
         (clear b52)
         (on-table b44)
         (on b52 b10)
         (on b10 b53)
         (on b53 b7)
         (on b7 b31)
         (on b31 b81)
         (on b81 b9)
         (on b9 b44)
         (clear b19)
         (on-table b41)
         (on b19 b46)
         (on b46 b54)
         (on b54 b25)
         (on b25 b29)
         (on b29 b85)
         (on b85 b3)
         (on b3 b78)
         (on b78 b22)
         (on b22 b84)
         (on b84 b79)
         (on b79 b41)
         (clear b20)
         (on-table b33)
         (on b20 b50)
         (on b50 b51)
         (on b51 b49)
         (on b49 b32)
         (on b32 b61)
         (on b61 b1)
         (on b1 b27)
         (on b27 b14)
         (on b14 b72)
         (on b72 b82)
         (on b82 b17)
         (on b17 b12)
         (on b12 b59)
         (on b59 b39)
         (on b39 b70)
         (on b70 b13)
         (on b13 b11)
         (on b11 b42)
         (on b42 b62)
         (on b62 b33)
         (clear b69)
         (on-table b30)
         (on b69 b23)
         (on b23 b26)
         (on b26 b16)
         (on b16 b67)
         (on b67 b24)
         (on b24 b73)
         (on b73 b43)
         (on b43 b68)
         (on b68 b71)
         (on b71 b6)
         (on b6 b28)
         (on b28 b30)
         (clear b45)
         (on-table b8)
         (on b45 b75)
         (on b75 b47)
         (on b47 b48)
         (on b48 b15)
         (on b15 b35)
         (on b35 b21)
         (on b21 b56)
         (on b56 b40)
         (on b40 b66)
         (on b66 b80)
         (on b80 b76)
         (on b76 b58)
         (on b58 b34)
         (on b34 b8)))
                               (:tasks (task0 (achieve-goals arm1)))
                               (:tasks (task1 (achieve-goals arm2)))
)