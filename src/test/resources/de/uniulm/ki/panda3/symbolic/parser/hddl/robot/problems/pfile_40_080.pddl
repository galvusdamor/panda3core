(define
 (problem pfile_40_080)
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
           o21
           o22
           o23
           o24
           o25
           o26
           o27
           o28
           o29
           o30
           o31
           o32
           o33
           o34
           o35
           o36
           o37
           o38
           o39
           o40
           o41
           o42
           o43
           o44
           o45
           o46
           o47
           o48
           o49
           o50
           o51
           o52
           o53
           o54
           o55
           o56
           o57
           o58
           o59
           o60
           o61
           o62
           o63
           o64
           o65
           o66
           o67
           o68
           o69
           o70
           o71
           o72
           o73
           o74
           o75
           o76
           o77
           o78
           o79
           o80
           - PACKAGE
           c
           r1
           r2
           r3
           r4
           r5
           r6
           r7
           r8
           r9
           r10
           r11
           r12
           r13
           r14
           r15
           r16
           r17
           r18
           r19
           r20
           r21
           r22
           r23
           r24
           r25
           r26
           r27
           r28
           r29
           r30
           r31
           r32
           r33
           r34
           r35
           r36
           r37
           r38
           r39
           r40
           - ROOM
           d239
           d212
           d1218
           d1618
           d227
           d2327
           d2740
           d2127
           d2122
           d2137
           d1537
           d1534
           d3436
           d1517
           d617
           d614
           d1420
           d2437
           d2433
           d2028
           d428
           d328
           d928
           d1133
           d2129
           d836
           d1631
           d2631
           d520
           d519
           d1319
           d718
           d730
           d1425
           d210
           d3138
           d038
           d139
           d1135
           d432
           - ROOMDOOR)
 (:init
  (rloc c)
  (armempty)
  (door c r38 d038)
  (door r1 r39 d139)
  (door r2 r10 d210)
  (door r2 r12 d212)
  (door r2 r27 d227)
  (door r2 r39 d239)
  (door r3 r28 d328)
  (door r4 r28 d428)
  (door r4 r32 d432)
  (door r5 r19 d519)
  (door r5 r20 d520)
  (door r6 r14 d614)
  (door r6 r17 d617)
  (door r7 r18 d718)
  (door r7 r30 d730)
  (door r8 r36 d836)
  (door r9 r28 d928)
  (door r10 r2 d210)
  (door r11 r33 d1133)
  (door r11 r35 d1135)
  (door r12 r2 d212)
  (door r12 r18 d1218)
  (door r13 r19 d1319)
  (door r14 r6 d614)
  (door r14 r20 d1420)
  (door r14 r25 d1425)
  (door r15 r17 d1517)
  (door r15 r34 d1534)
  (door r15 r37 d1537)
  (door r16 r18 d1618)
  (door r16 r31 d1631)
  (door r17 r6 d617)
  (door r17 r15 d1517)
  (door r18 r7 d718)
  (door r18 r12 d1218)
  (door r18 r16 d1618)
  (door r19 r5 d519)
  (door r19 r13 d1319)
  (door r20 r5 d520)
  (door r20 r14 d1420)
  (door r20 r28 d2028)
  (door r21 r22 d2122)
  (door r21 r27 d2127)
  (door r21 r29 d2129)
  (door r21 r37 d2137)
  (door r22 r21 d2122)
  (door r23 r27 d2327)
  (door r24 r33 d2433)
  (door r24 r37 d2437)
  (door r25 r14 d1425)
  (door r26 r31 d2631)
  (door r27 r2 d227)
  (door r27 r21 d2127)
  (door r27 r23 d2327)
  (door r27 r40 d2740)
  (door r28 r3 d328)
  (door r28 r4 d428)
  (door r28 r9 d928)
  (door r28 r20 d2028)
  (door r29 r21 d2129)
  (door r30 r7 d730)
  (door r31 r16 d1631)
  (door r31 r26 d2631)
  (door r31 r38 d3138)
  (door r32 r4 d432)
  (door r33 r11 d1133)
  (door r33 r24 d2433)
  (door r34 r15 d1534)
  (door r34 r36 d3436)
  (door r35 r11 d1135)
  (door r36 r8 d836)
  (door r36 r34 d3436)
  (door r37 r15 d1537)
  (door r37 r21 d2137)
  (door r37 r24 d2437)
  (door r38 c d038)
  (door r38 r31 d3138)
  (door r39 r1 d139)
  (door r39 r2 d239)
  (door r40 r27 d2740)
  (closed d1618)
  (closed d227)
  (closed d2327)
  (closed d2740)
  (closed d2122)
  (closed d2137)
  (closed d3436)
  (closed d617)
  (closed d1420)
  (closed d2433)
  (closed d836)
  (closed d2631)
  (closed d520)
  (closed d519)
  (closed d1425)
  (closed d210)
  (closed d1135)
  (closed d432)
  (in o1 r14)
  (in o2 r36)
  (in o3 r27)
  (in o4 r39)
  (in o5 r23)
  (in o6 r30)
  (in o7 r5)
  (in o8 r20)
  (in o9 r25)
  (in o10 r18)
  (in o11 r30)
  (in o12 r14)
  (in o13 r15)
  (in o14 r16)
  (in o15 r14)
  (in o16 r32)
  (in o17 r35)
  (in o18 r28)
  (in o19 r20)
  (in o20 r28)
  (in o21 r32)
  (in o22 r11)
  (in o23 r15)
  (in o24 r36)
  (in o25 r35)
  (in o26 r19)
  (in o27 r8)
  (in o28 r17)
  (in o29 r33)
  (in o30 r26)
  (in o31 r25)
  (in o32 r26)
  (in o33 r17)
  (in o34 r34)
  (in o35 r25)
  (in o36 r36)
  (in o37 r15)
  (in o38 r1)
  (in o39 r6)
  (in o40 r16)
  (in o41 r29)
  (in o42 r40)
  (in o43 r25)
  (in o44 r10)
  (in o45 r15)
  (in o46 r29)
  (in o47 r7)
  (in o48 r20)
  (in o49 r18)
  (in o50 r37)
  (in o51 r1)
  (in o52 r35)
  (in o53 r35)
  (in o54 r20)
  (in o55 r19)
  (in o56 r27)
  (in o57 r38)
  (in o58 r30)
  (in o59 r11)
  (in o60 r5)
  (in o61 r14)
  (in o62 r2)
  (in o63 r22)
  (in o64 r2)
  (in o65 r1)
  (in o66 r25)
  (in o67 r2)
  (in o68 r32)
  (in o69 r24)
  (in o70 r7)
  (in o71 r34)
  (in o72 r3)
  (in o73 r40)
  (in o74 r33)
  (in o75 r18)
  (in o76 r27)
  (in o77 r3)
  (in o78 r36)
  (in o79 r40)
  (in o80 r22))
 (:goal (and
         (in o1 r30)
         (in o2 r3)
         (in o3 r39)
         (in o4 r2)
         (in o5 r14)
         (in o6 r14)
         (in o7 r32)
         (in o8 r23)
         (in o9 r4)
         (in o10 r6)
         (in o11 r19)
         (in o12 r3)
         (in o13 r12)
         (in o14 r11)
         (in o15 r30)
         (in o16 r20)
         (in o17 r4)
         (in o18 r12)
         (in o19 r1)
         (in o20 r17)
         (in o21 r2)
         (in o22 r17)
         (in o23 r9)
         (in o24 r24)
         (in o25 r12)
         (in o26 r22)
         (in o27 r28)
         (in o28 r37)
         (in o29 r15)
         (in o30 r36)
         (in o31 r35)
         (in o32 r24)
         (in o33 r25)
         (in o34 r5)
         (in o35 r4)
         (in o36 r22)
         (in o37 r13)
         (in o38 r2)
         (in o39 r23)
         (in o40 r8)
         (in o41 r28)
         (in o42 r20)
         (in o43 r18)
         (in o44 r18)
         (in o45 r29)
         (in o46 r16)
         (in o47 r34)
         (in o48 r1)
         (in o49 r40)
         (in o50 r27)
         (in o51 r19)
         (in o52 r2)
         (in o53 r8)
         (in o54 r37)
         (in o55 r10)
         (in o56 r8)
         (in o57 r8)
         (in o58 r31)
         (in o59 r23)
         (in o60 r24)
         (in o61 r4)
         (in o62 r2)
         (in o63 r24)
         (in o64 r3)
         (in o65 r35)
         (in o66 r19)
         (in o67 r32)
         (in o68 r4)
         (in o69 r28)
         (in o70 r5)
         (in o71 r2)
         (in o72 r31)
         (in o73 r38)
         (in o74 r1)
         (in o75 r13)
         (in o76 r5)
         (in o77 r18)
         (in o78 r14)
         (in o79 r31)
         (in o80 r39)))
             (:tasks (task0 (achieve-goals)))
)