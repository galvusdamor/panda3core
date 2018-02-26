(define (domain existsstep)
  (:requirements :typing)
  (:predicates 
		(a)
		(b)
		(c)
		(d)
		(e)
		(f)
)

;; solution
;; a b
;; Y
;; a b d
;; X
;; b c d
;; Z
;; b c d e

;; test G (d & a -> G - e)   // should not have a solution
;; test G (d & a -> F f)   // should have a solution (*must* execute essZ)


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

(:action essZ
   :parameters()
   :precondition
    (and (c) (d))
   :effect
    (and (f)))
)
