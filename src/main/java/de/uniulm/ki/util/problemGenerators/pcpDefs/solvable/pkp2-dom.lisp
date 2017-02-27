(define (domain someDomain)
	(:requirements :typing :hierachie)
	(:predicates
		(turnA)
		(turnB)
		(lt1)
		(lt2)
		(lt3)
		(lt4)
		(lt1)
		(lt2)
		(lt3)
		(lt4)
		(l0)
		(l1)
	)

	(:task SG1
		:parameters ()
	)

	(:task SG2
		:parameters ()
	)

	(:method G1_S2t1_S_1_0_0
		:parameters ()
		:task (SG1)
		:subtasks (and
		 (task0 (t1G1))
		 (task1 (SG1))
		 (task2 (p1G1))
		 (task3 (p0G1))
		 (task4 (p0G1))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
			(task3 < task4)
		)
	)

	(:method G1_S2t2_S_1_0
		:parameters ()
		:task (SG1)
		:subtasks (and
		 (task0 (t2G1))
		 (task1 (SG1))
		 (task2 (p1G1))
		 (task3 (p0G1))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
		)
	)

	(:method G1_S2t3_S_1_0
		:parameters ()
		:task (SG1)
		:subtasks (and
		 (task0 (t3G1))
		 (task1 (SG1))
		 (task2 (p1G1))
		 (task3 (p0G1))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
		)
	)

	(:method G1_S2t4_S_0_1
		:parameters ()
		:task (SG1)
		:subtasks (and
		 (task0 (t4G1))
		 (task1 (SG1))
		 (task2 (p0G1))
		 (task3 (p1G1))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
		)
	)

	(:method G1_S2t1_1_0_0
		:parameters ()
		:task (SG1)
		:subtasks (and
		 (task0 (t1G1))
		 (task1 (p1G1))
		 (task2 (p0G1))
		 (task3 (p0G1))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
		)
	)

	(:method G1_S2t2_1_0
		:parameters ()
		:task (SG1)
		:subtasks (and
		 (task0 (t2G1))
		 (task1 (p1G1))
		 (task2 (p0G1))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
		)
	)

	(:method G1_S2t3_1_0
		:parameters ()
		:task (SG1)
		:subtasks (and
		 (task0 (t3G1))
		 (task1 (p1G1))
		 (task2 (p0G1))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
		)
	)

	(:method G1_S2t4_0_1
		:parameters ()
		:task (SG1)
		:subtasks (and
		 (task0 (t4G1))
		 (task1 (p0G1))
		 (task2 (p1G1))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
		)
	)

	(:method G2_S2t1_S_0
		:parameters ()
		:task (SG2)
		:subtasks (and
		 (task0 (t1G2))
		 (task1 (SG2))
		 (task2 (p0G2))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
		)
	)

	(:method G2_S2t2_S_1_1_0
		:parameters ()
		:task (SG2)
		:subtasks (and
		 (task0 (t2G2))
		 (task1 (SG2))
		 (task2 (p1G2))
		 (task3 (p1G2))
		 (task4 (p0G2))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
			(task3 < task4)
		)
	)

	(:method G2_S2t3_S_1_0_1
		:parameters ()
		:task (SG2)
		:subtasks (and
		 (task0 (t3G2))
		 (task1 (SG2))
		 (task2 (p1G2))
		 (task3 (p0G2))
		 (task4 (p1G2))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
			(task3 < task4)
		)
	)

	(:method G2_S2t4_S_1_0_0
		:parameters ()
		:task (SG2)
		:subtasks (and
		 (task0 (t4G2))
		 (task1 (SG2))
		 (task2 (p1G2))
		 (task3 (p0G2))
		 (task4 (p0G2))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
			(task3 < task4)
		)
	)

	(:method G2_S2t1_0
		:parameters ()
		:task (SG2)
		:subtasks (and
		 (task0 (t1G2))
		 (task1 (p0G2))
		)
		:ordering (and
			(task0 < task1)
		)
	)

	(:method G2_S2t2_1_1_0
		:parameters ()
		:task (SG2)
		:subtasks (and
		 (task0 (t2G2))
		 (task1 (p1G2))
		 (task2 (p1G2))
		 (task3 (p0G2))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
		)
	)

	(:method G2_S2t3_1_0_1
		:parameters ()
		:task (SG2)
		:subtasks (and
		 (task0 (t3G2))
		 (task1 (p1G2))
		 (task2 (p0G2))
		 (task3 (p1G2))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
		)
	)

	(:method G2_S2t4_1_0_0
		:parameters ()
		:task (SG2)
		:subtasks (and
		 (task0 (t4G2))
		 (task1 (p1G2))
		 (task2 (p0G2))
		 (task3 (p0G2))
		)
		:ordering (and
			(task0 < task1)
			(task1 < task2)
			(task2 < task3)
		)
	)

	(:action epsilon
		:parameters ()
	)

	(:action t1G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(lt1)
			)
	)

	(:action t2G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(lt2)
			)
	)

	(:action t3G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(lt3)
			)
	)

	(:action t4G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(lt4)
			)
	)

	(:action t1G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(lt1)
			)
	)

	(:action t2G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(lt2)
			)
	)

	(:action t3G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(lt3)
			)
	)

	(:action t4G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(lt4)
			)
	)

	(:action p0G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(l0)
			)
	)

	(:action p1G1
		:parameters ()
		:precondition 
			(and
				(turnA)
			)
		:effect
			(and
				(not (turnA))
				(turnB)
				(l1)
			)
	)

	(:action t1G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(lt1)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (lt1))
			)
	)

	(:action t2G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(lt2)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (lt2))
			)
	)

	(:action t3G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(lt3)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (lt3))
			)
	)

	(:action t4G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(lt4)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (lt4))
			)
	)

	(:action t1G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(lt1)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (lt1))
			)
	)

	(:action t2G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(lt2)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (lt2))
			)
	)

	(:action t3G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(lt3)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (lt3))
			)
	)

	(:action t4G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(lt4)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (lt4))
			)
	)

	(:action p0G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(l0)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (l0))
			)
	)

	(:action p1G2
		:parameters ()
		:precondition 
			(and
				(turnB)
				(l1)
			)
		:effect
			(and
				(not (turnB))
				(turnA)
				(not (l1))
			)
	)
)