(define
 (problem pfile_50_100)
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
           o81
           o82
           o83
           o84
           o85
           o86
           o87
           o88
           o89
           o90
           o91
           o92
           o93
           o94
           o95
           o96
           o97
           o98
           o99
           o100
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
           r41
           r42
           r43
           r44
           r45
           r46
           r47
           r48
           r49
           r50
           - ROOM
           d1113
           d1126
           d2526
           d2637
           d1335
           d2335
           d2342
           d542
           d547
           d013
           d742
           d741
           d3347
           d1423
           d2235
           d125
           d139
           d635
           d617
           d68
           d3841
           d1242
           d1244
           d1944
           d3133
           d1637
           d1643
           d243
           d2443
           d1415
           d1530
           d937
           d946
           d2046
           d929
           d429
           d1627
           d3040
           d628
           d3441
           d745
           d949
           d349
           d3748
           d3850
           d1046
           d1846
           d3243
           d2132
           d1436
           - ROOMDOOR)
 (:init
  (rloc c)
  (armempty)
  (door c r13 d013)
  (door r1 r25 d125)
  (door r1 r39 d139)
  (door r2 r43 d243)
  (door r3 r49 d349)
  (door r4 r29 d429)
  (door r5 r42 d542)
  (door r5 r47 d547)
  (door r6 r8 d68)
  (door r6 r17 d617)
  (door r6 r28 d628)
  (door r6 r35 d635)
  (door r7 r41 d741)
  (door r7 r42 d742)
  (door r7 r45 d745)
  (door r8 r6 d68)
  (door r9 r29 d929)
  (door r9 r37 d937)
  (door r9 r46 d946)
  (door r9 r49 d949)
  (door r10 r46 d1046)
  (door r11 r13 d1113)
  (door r11 r26 d1126)
  (door r12 r42 d1242)
  (door r12 r44 d1244)
  (door r13 c d013)
  (door r13 r11 d1113)
  (door r13 r35 d1335)
  (door r14 r15 d1415)
  (door r14 r23 d1423)
  (door r14 r36 d1436)
  (door r15 r14 d1415)
  (door r15 r30 d1530)
  (door r16 r27 d1627)
  (door r16 r37 d1637)
  (door r16 r43 d1643)
  (door r17 r6 d617)
  (door r18 r46 d1846)
  (door r19 r44 d1944)
  (door r20 r46 d2046)
  (door r21 r32 d2132)
  (door r22 r35 d2235)
  (door r23 r14 d1423)
  (door r23 r35 d2335)
  (door r23 r42 d2342)
  (door r24 r43 d2443)
  (door r25 r1 d125)
  (door r25 r26 d2526)
  (door r26 r11 d1126)
  (door r26 r25 d2526)
  (door r26 r37 d2637)
  (door r27 r16 d1627)
  (door r28 r6 d628)
  (door r29 r4 d429)
  (door r29 r9 d929)
  (door r30 r15 d1530)
  (door r30 r40 d3040)
  (door r31 r33 d3133)
  (door r32 r21 d2132)
  (door r32 r43 d3243)
  (door r33 r31 d3133)
  (door r33 r47 d3347)
  (door r34 r41 d3441)
  (door r35 r6 d635)
  (door r35 r13 d1335)
  (door r35 r22 d2235)
  (door r35 r23 d2335)
  (door r36 r14 d1436)
  (door r37 r9 d937)
  (door r37 r16 d1637)
  (door r37 r26 d2637)
  (door r37 r48 d3748)
  (door r38 r41 d3841)
  (door r38 r50 d3850)
  (door r39 r1 d139)
  (door r40 r30 d3040)
  (door r41 r7 d741)
  (door r41 r34 d3441)
  (door r41 r38 d3841)
  (door r42 r5 d542)
  (door r42 r7 d742)
  (door r42 r12 d1242)
  (door r42 r23 d2342)
  (door r43 r2 d243)
  (door r43 r16 d1643)
  (door r43 r24 d2443)
  (door r43 r32 d3243)
  (door r44 r12 d1244)
  (door r44 r19 d1944)
  (door r45 r7 d745)
  (door r46 r9 d946)
  (door r46 r10 d1046)
  (door r46 r18 d1846)
  (door r46 r20 d2046)
  (door r47 r5 d547)
  (door r47 r33 d3347)
  (door r48 r37 d3748)
  (door r49 r3 d349)
  (door r49 r9 d949)
  (door r50 r38 d3850)
  (closed d1113)
  (closed d2637)
  (closed d1335)
  (closed d2342)
  (closed d542)
  (closed d547)
  (closed d013)
  (closed d742)
  (closed d3347)
  (closed d139)
  (closed d635)
  (closed d617)
  (closed d1637)
  (closed d243)
  (closed d2443)
  (closed d1415)
  (closed d946)
  (closed d429)
  (closed d1627)
  (closed d3040)
  (closed d3441)
  (closed d745)
  (closed d949)
  (closed d349)
  (closed d3748)
  (closed d3850)
  (closed d1846)
  (in o1 r16)
  (in o2 r8)
  (in o3 r11)
  (in o4 r31)
  (in o5 r6)
  (in o6 r20)
  (in o7 r22)
  (in o8 r26)
  (in o9 r42)
  (in o10 r16)
  (in o11 r40)
  (in o12 r29)
  (in o13 r20)
  (in o14 r26)
  (in o15 r20)
  (in o16 r42)
  (in o17 r43)
  (in o18 r16)
  (in o19 r33)
  (in o20 r24)
  (in o21 r16)
  (in o22 r31)
  (in o23 r14)
  (in o24 r25)
  (in o25 r15)
  (in o26 r1)
  (in o27 r35)
  (in o28 r19)
  (in o29 r7)
  (in o30 r2)
  (in o31 r42)
  (in o32 r27)
  (in o33 r40)
  (in o34 r9)
  (in o35 r1)
  (in o36 r6)
  (in o37 r5)
  (in o38 r34)
  (in o39 r37)
  (in o40 r23)
  (in o41 r34)
  (in o42 r12)
  (in o43 r34)
  (in o44 r26)
  (in o45 r35)
  (in o46 r40)
  (in o47 r35)
  (in o48 r9)
  (in o49 r43)
  (in o50 r39)
  (in o51 r32)
  (in o52 r26)
  (in o53 r20)
  (in o54 r26)
  (in o55 r15)
  (in o56 r31)
  (in o57 r4)
  (in o58 r16)
  (in o59 r37)
  (in o60 r11)
  (in o61 r18)
  (in o62 r39)
  (in o63 r10)
  (in o64 r39)
  (in o65 r42)
  (in o66 r31)
  (in o67 r28)
  (in o68 r26)
  (in o69 r1)
  (in o70 r34)
  (in o71 r39)
  (in o72 r32)
  (in o73 r12)
  (in o74 r30)
  (in o75 r2)
  (in o76 r16)
  (in o77 r2)
  (in o78 r46)
  (in o79 r12)
  (in o80 r45)
  (in o81 r5)
  (in o82 r20)
  (in o83 r32)
  (in o84 r5)
  (in o85 r46)
  (in o86 r6)
  (in o87 r6)
  (in o88 r23)
  (in o89 r5)
  (in o90 r32)
  (in o91 r1)
  (in o92 r30)
  (in o93 r29)
  (in o94 r24)
  (in o95 r45)
  (in o96 r23)
  (in o97 r4)
  (in o98 r28)
  (in o99 r24)
  (in o100 r15))
 (:goal (and
         (in o1 r16)
         (in o2 r30)
         (in o3 r36)
         (in o4 r32)
         (in o5 r4)
         (in o6 r39)
         (in o7 r50)
         (in o8 r12)
         (in o9 r13)
         (in o10 r19)
         (in o11 r44)
         (in o12 r6)
         (in o13 r8)
         (in o14 r8)
         (in o15 r3)
         (in o16 r40)
         (in o17 r35)
         (in o18 r7)
         (in o19 r36)
         (in o20 r46)
         (in o21 r44)
         (in o22 r33)
         (in o23 r13)
         (in o24 r27)
         (in o25 r4)
         (in o26 r5)
         (in o27 r39)
         (in o28 r14)
         (in o29 r15)
         (in o30 r25)
         (in o31 r8)
         (in o32 r43)
         (in o33 r44)
         (in o34 r40)
         (in o35 r32)
         (in o36 r35)
         (in o37 r20)
         (in o38 r29)
         (in o39 r5)
         (in o40 r23)
         (in o41 r1)
         (in o42 r35)
         (in o43 r36)
         (in o44 r16)
         (in o45 r31)
         (in o46 r47)
         (in o47 r5)
         (in o48 r41)
         (in o49 r19)
         (in o50 r4)
         (in o51 r6)
         (in o52 r15)
         (in o53 r37)
         (in o54 r46)
         (in o55 r34)
         (in o56 r3)
         (in o57 r29)
         (in o58 r11)
         (in o59 r33)
         (in o60 r22)
         (in o61 r46)
         (in o62 r18)
         (in o63 r26)
         (in o64 r10)
         (in o65 r16)
         (in o66 r22)
         (in o67 r50)
         (in o68 r1)
         (in o69 r31)
         (in o70 r44)
         (in o71 r21)
         (in o72 r34)
         (in o73 r30)
         (in o74 r2)
         (in o75 r21)
         (in o76 r3)
         (in o77 r38)
         (in o78 r47)
         (in o79 r22)
         (in o80 r48)
         (in o81 r5)
         (in o82 r44)
         (in o83 r43)
         (in o84 r18)
         (in o85 r40)
         (in o86 r43)
         (in o87 r38)
         (in o88 r24)
         (in o89 r28)
         (in o90 r14)
         (in o91 r33)
         (in o92 r32)
         (in o93 r13)
         (in o94 r28)
         (in o95 r45)
         (in o96 r6)
         (in o97 r29)
         (in o98 r47)
         (in o99 r5)
         (in o100 r45)))
             (:tasks (task0 (achieve-goals)))
)