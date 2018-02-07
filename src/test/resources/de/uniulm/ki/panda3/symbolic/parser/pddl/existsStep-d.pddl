(define (domain existsstep)
  (:requirements :typing)
  (:predicates 
		(a)
		(b)
		(c)
		(d)
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
)
