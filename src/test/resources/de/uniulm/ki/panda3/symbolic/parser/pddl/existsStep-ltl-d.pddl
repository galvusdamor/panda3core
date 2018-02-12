(define (domain existsstep)
  (:requirements :typing)
  (:predicates 
		(a)
		(b)
		(c)
		(d)
		(e)
)


(:action X
  :parameters()
  :precondition
   (and (a) (b))
  :effect
   (and (not (a)) (c)))

(:action Y
  :parameters()
  :precondition
   (and (a) (b))
  :effect
   (and (d)))

(:action Z
  :parameters()
  :precondition
   (and (c) (d))
  :effect
   (and (e) (not (a))))
)
