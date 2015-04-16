(define (problem transport1)
 (:domain transport)
 (:objects
  city-loc-1 - location
  city-loc-2 - location
  city-loc-3 - location
  truck-1 - vehicle
  truck-2 - vehicle
  package-1 - package
  package-2 - package
  package-3 - package
  capacity-0 - capacity-number
  capacity-1 - capacity-number
 )
 (:htn
  :subtasks (and
    (t1 (deliver package-1))
    (t2 (deliver package-2))
    (t3 (deliver package-3)))
  :ordering ( )
  :constraints ( )
 )
 (:init
  (capacity-predecessor capacity-0 capacity-1)
  (road city-loc-3 city-loc-1)
  (road city-loc-1 city-loc-3)
  (at package-1 city-loc-1)
  (at package-2 city-loc-2)
  (at package-3 city-loc-3)
  (at truck-1 city-loc-2)
  (at truck-2 city-loc-1)
  (capacity truck-1 capacity-1)
  (capacity truck-2 capacity-1)
 (:goal (and
  (at package-1 city-loc-3)
  (at package-2 city-loc-1)
  (at package-3 city-loc-2)
 ))
)