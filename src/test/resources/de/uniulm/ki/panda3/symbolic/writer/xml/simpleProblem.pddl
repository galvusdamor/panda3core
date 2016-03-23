(define (problem simple)
 (:domain transport)
 (:objects
  a1 a2 a3 a4 a5 a6 - a
 )
 (:htn
  :tasks (and
    (t1 (abstract a1 a2))
    (t2 (abstract a2 a3))
    (t3 (abstract a3 a4))
   )
  :ordering ( )
  :constraints ( )
 )
 (:init )
 (:goal (and
  (p a1 a2)
 ))
)