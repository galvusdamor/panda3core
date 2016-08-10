(define (domain transport)
  (:requirements :typing :action-costs :hierachie )
  (:types
        location target locatable - object
        vehicle package - locatable
        capacity-number - object
  )

  (:predicates
     (road ?l1 ?l2 - location)
     (at ?x - locatable ?v - location)
     (in ?x - package ?v - vehicle)
     (capacity ?v - vehicle ?s1 - capacity-number)
     (capacity-predecessor ?s1 ?s2 - capacity-number)
  )

  (:task tname
    :parameters (?v - vehicle ?l1 ?l2 - location)
    :precondition ( )
    :effect ( )
  )

  (:method mname
    :parameters (?v - vehicle ?l1 ?l2 - location)
    :task (tname ?l1 ?v)
    :precondition (and
        (at ?v ?l1)
        (road ?l1 ?l2))
    :subtasks (and
      (t1 (tname ?v ?l2))
      (t2 (tname ?v ?l1))
      (t3 (tname ?v ?l2)))
    :ordering (and
      (t1 < t2)
      (t1 < t3))
    :constraints (and
      (not (= ?l1 ?l2))
      (= ?v ?l2))
    :causal-links (and
      (t1 (at ?v ?l2) t2)
    )
  )

  (:action drive ; This is a comment!
    :parameters (?v - vehicle ?l1 ?l2 - location)
    :precondition (and
        (at ?v ?l1)
        (road ?l1 ?l2)
      )
    :effect (and
        (not (at ?v ?l1))
        (at ?v ?l2)
      )
  )

 (:action pick-up
    :parameters (?v - vehicle ?l - location ?p - package ?s1 ?s2 - capacity-number)
    :precondition (and
        (at ?v ?l)
        (at ?p ?l)
        (capacity-predecessor ?s1 ?s2)
        (capacity ?v ?s2)
      )
    :effect (and
        (not (at ?p ?l))
        (in ?p ?v)
        (capacity ?v ?s1)
        (not (capacity ?v ?s2))
      )
  )

  (:action drop
    :parameters (?v - vehicle ?l - location ?p - package ?s1 ?s2 - capacity-number)
    :precondition (and
        (at ?v ?l)
        (in ?p ?v)
        (capacity-predecessor ?s1 ?s2)
        (capacity ?v ?s1)
      )
    :effect (and
        (not (in ?p ?v))
        (at ?p ?l)
        (capacity ?v ?s2)
        (not (capacity ?v ?s1))
      )
  )

)
