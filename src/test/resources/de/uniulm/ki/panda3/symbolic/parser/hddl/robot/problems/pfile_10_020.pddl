(define
 (problem pfile_10_020)
 (:domain robot)
 (:objects o1
           o2
           o3
           o4
           o5
           o6
           o7
           o8
           o9
           o10
           o11
           o12
           o13
           o14
           o15
           o16
           o17
           o18
           o19
           o20
           - PACKAGE
           c r1 r2 r3 r4 r5 r6 r7 r8 r9 r10 - ROOM
           d210 d710 d17 d13 d15 d56 d46 d04 d68 d09 - ROOMDOOR)
 (:init
  (rloc c)
  (armempty)
  (door c r4 d04)
  (door c r9 d09)
  (door r1 r3 d13)
  (door r1 r5 d15)
  (door r1 r7 d17)
  (door r2 r10 d210)
  (door r3 r1 d13)
  (door r4 c d04)
  (door r4 r6 d46)
  (door r5 r1 d15)
  (door r5 r6 d56)
  (door r6 r4 d46)
  (door r6 r5 d56)
  (door r6 r8 d68)
  (door r7 r1 d17)
  (door r7 r10 d710)
  (door r8 r6 d68)
  (door r9 c d09)
  (door r10 r2 d210)
  (door r10 r7 d710)
  (closed d210)
  (closed d710)
  (closed d17)
  (closed d04)
  (closed d09)
  (in o1 r9)
  (in o2 r2)
  (in o3 r6)
  (in o4 r5)
  (in o5 r9)
  (in o6 r10)
  (in o7 r8)
  (in o8 r10)
  (in o9 r4)
  (in o10 r8)
  (in o11 r8)
  (in o12 r8)
  (in o13 r10)
  (in o14 r6)
  (in o15 r6)
  (in o16 r3)
  (in o17 r8)
  (in o18 r5)
  (in o19 r9)
  (in o20 r7))
 (:goal (and
         (in o1 r5)
         (in o2 r8)
         (in o3 r2)
         (in o4 r1)
         (in o5 r1)
         (in o6 r5)
         (in o7 r5)
         (in o8 r1)
         (in o9 r9)
         (in o10 r6)
         (in o11 r2)
         (in o12 r4)
         (in o13 r9)
         (in o14 r3)
         (in o15 r1)
         (in o16 r2)
         (in o17 r5)
         (in o18 r10)
         (in o19 r3)
         (in o20 r2)))
             (:tasks (task0 (achieve-goals)))
)