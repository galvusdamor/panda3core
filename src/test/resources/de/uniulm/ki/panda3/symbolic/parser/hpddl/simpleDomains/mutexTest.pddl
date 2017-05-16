(define (domain mutex)
  (:requirements :typing :action-costs :hierachie )
  (:types
        object
  )

  (:predicates
     (p)
  )

  (:task abstr
    :parameters ()
    :precondition ( )
    :effect ( )
  )

  (:method m1
    :parameters ()
    :task (abstr)
    :subtasks (and (a))
  )

  (:method m2
    :parameters ()
    :task (abstr)
    :subtasks (and (b))
  )

  (:action a
    :parameters ()
    :precondition ()
    :effect ()
  )

  (:action b
    :parameters ()
    :precondition ()
    :effect ()
  )


)
