(define
 (problem pfile_06_100)
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
           b96
           b97
           b98
           b99
           b100
           - BLOCK)
 (:init
  (hand-empty arm1)
  (hand-empty arm2)
  (hand-empty arm3)
  (hand-empty arm4)
  (hand-empty arm5)
  (hand-empty arm6)
  (clear b26)
  (on-table b71)
  (on b26 b44)
  (on b44 b71)
  (clear b84)
  (on-table b59)
  (on b84 b25)
  (on b25 b90)
  (on b90 b86)
  (on b86 b75)
  (on b75 b1)
  (on b1 b61)
  (on b61 b55)
  (on b55 b20)
  (on b20 b81)
  (on b81 b52)
  (on b52 b10)
  (on b10 b21)
  (on b21 b92)
  (on b92 b14)
  (on b14 b47)
  (on b47 b95)
  (on b95 b66)
  (on b66 b56)
  (on b56 b79)
  (on b79 b18)
  (on b18 b49)
  (on b49 b77)
  (on b77 b73)
  (on b73 b19)
  (on b19 b74)
  (on b74 b50)
  (on b50 b58)
  (on b58 b29)
  (on b29 b5)
  (on b5 b83)
  (on b83 b82)
  (on b82 b93)
  (on b93 b42)
  (on b42 b11)
  (on b11 b100)
  (on b100 b32)
  (on b32 b96)
  (on b96 b63)
  (on b63 b22)
  (on b22 b67)
  (on b67 b88)
  (on b88 b37)
  (on b37 b35)
  (on b35 b98)
  (on b98 b17)
  (on b17 b6)
  (on b6 b97)
  (on b97 b43)
  (on b43 b2)
  (on b2 b33)
  (on b33 b23)
  (on b23 b45)
  (on b45 b31)
  (on b31 b3)
  (on b3 b85)
  (on b85 b65)
  (on b65 b64)
  (on b64 b59)
  (clear b46)
  (on-table b46)
  (clear b40)
  (on-table b40)
  (clear b78)
  (on-table b38)
  (on b78 b4)
  (on b4 b60)
  (on b60 b13)
  (on b13 b38)
  (clear b89)
  (on-table b27)
  (on b89 b99)
  (on b99 b70)
  (on b70 b76)
  (on b76 b16)
  (on b16 b69)
  (on b69 b30)
  (on b30 b68)
  (on b68 b34)
  (on b34 b54)
  (on b54 b28)
  (on b28 b94)
  (on b94 b87)
  (on b87 b24)
  (on b24 b27)
  (clear b80)
  (on-table b9)
  (on b80 b7)
  (on b7 b53)
  (on b53 b39)
  (on b39 b51)
  (on b51 b57)
  (on b57 b36)
  (on b36 b91)
  (on b91 b72)
  (on b72 b8)
  (on b8 b62)
  (on b62 b41)
  (on b41 b12)
  (on b12 b15)
  (on b15 b48)
  (on b48 b9))
 (:goal (and
         (clear b73)
         (on-table b97)
         (on b73 b60)
         (on b60 b97)
         (clear b63)
         (on-table b94)
         (on b63 b23)
         (on b23 b94)
         (clear b89)
         (on-table b89)
         (clear b84)
         (on-table b86)
         (on b84 b26)
         (on b26 b75)
         (on b75 b86)
         (clear b69)
         (on-table b74)
         (on b69 b47)
         (on b47 b42)
         (on b42 b21)
         (on b21 b6)
         (on b6 b67)
         (on b67 b81)
         (on b81 b38)
         (on b38 b82)
         (on b82 b98)
         (on b98 b34)
         (on b34 b39)
         (on b39 b56)
         (on b56 b30)
         (on b30 b16)
         (on b16 b79)
         (on b79 b92)
         (on b92 b36)
         (on b36 b77)
         (on b77 b50)
         (on b50 b27)
         (on b27 b45)
         (on b45 b46)
         (on b46 b99)
         (on b99 b90)
         (on b90 b95)
         (on b95 b100)
         (on b100 b54)
         (on b54 b52)
         (on b52 b74)
         (clear b48)
         (on-table b72)
         (on b48 b72)
         (clear b55)
         (on-table b55)
         (clear b80)
         (on-table b53)
         (on b80 b10)
         (on b10 b22)
         (on b22 b40)
         (on b40 b25)
         (on b25 b68)
         (on b68 b53)
         (clear b85)
         (on-table b51)
         (on b85 b8)
         (on b8 b91)
         (on b91 b93)
         (on b93 b70)
         (on b70 b51)
         (clear b29)
         (on-table b28)
         (on b29 b11)
         (on b11 b20)
         (on b20 b88)
         (on b88 b12)
         (on b12 b18)
         (on b18 b37)
         (on b37 b65)
         (on b65 b87)
         (on b87 b71)
         (on b71 b58)
         (on b58 b96)
         (on b96 b83)
         (on b83 b9)
         (on b9 b28)
         (clear b19)
         (on-table b19)
         (clear b64)
         (on-table b17)
         (on b64 b7)
         (on b7 b57)
         (on b57 b24)
         (on b24 b1)
         (on b1 b49)
         (on b49 b32)
         (on b32 b44)
         (on b44 b62)
         (on b62 b76)
         (on b76 b66)
         (on b66 b14)
         (on b14 b35)
         (on b35 b41)
         (on b41 b17)
         (clear b15)
         (on-table b4)
         (on b15 b78)
         (on b78 b59)
         (on b59 b4)
         (clear b5)
         (on-table b2)
         (on b5 b13)
         (on b13 b61)
         (on b61 b3)
         (on b3 b31)
         (on b31 b43)
         (on b43 b33)
         (on b33 b2)))
                    (:tasks (task0 (achieve-goals arm1)))
                    (:tasks (task1 (achieve-goals arm2)))
                    (:tasks (task2 (achieve-goals arm3)))
                    (:tasks (task3 (achieve-goals arm4)))
                    (:tasks (task4 (achieve-goals arm5)))
                    (:tasks (task5 (achieve-goals arm6)))
)