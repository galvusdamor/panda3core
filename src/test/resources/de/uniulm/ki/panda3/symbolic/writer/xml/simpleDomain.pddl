(define (domain simple)
  (:requirements :typing :action-costs :hierachie )
  (:types
        a - object
  )

  (:predicates
     (p ?x1 ?x2 - a)
  )

  (:task abstract
    :parameters (?x1 ?x2 - a)
    :precondition ( )
    :effect ( )
  )

  (:method method
    :parameters (?x1 ?x2 - a)
    :task (abstract ?x1 ?x2)
    :subtasks (and
      (t1 (primitive ?x1 ?x2))
      (t2 (primitive ?x2 ?x1))
      )
     :ordering (and
           (t1 < t2)
           )
   :constraints (and
      (not (= ?x1 ?x2))
      )
  )

  (:action primitive
    :parameters (?x1 ?x2 - a)
    :precondition (and
        (not (p ?x1 ?x2))
      )
    :effect (and
        (p ?x1 ?x2)
      )
  )
)
