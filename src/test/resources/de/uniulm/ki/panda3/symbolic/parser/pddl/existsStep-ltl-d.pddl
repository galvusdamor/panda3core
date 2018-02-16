(define (domain existsstep)
  (:requirements :typing)
  (:predicates 
		(a)
		(b)
		(c)
		(d)
		(e)
)

;; solution
;; a b
;; Y
;; a b d
;; X
;; b c d
;; Z
;; b c d e

;; test G (d & a -> G - Z)   // should not have a solution


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
