; Transport city-sequential-40nodes-1000size-4degree-100mindistance-4trucks-16packages-2008seed

(define (problem p1)
 (:domain transport)
 (:objects
  city-loc-1 - location
  city-loc-2 - location
  city-loc-3 - location
  city-loc-4 - location
  city-loc-5 - location
  city-loc-6 - location
  truck-1 - vehicle
  truck-2 - vehicle
  package-1 - package
  package-2 - package
  package-3 - package
  package-4 - package
  package-5 - package
  capacity-0 - capacity-number
  capacity-1 - capacity-number
  capacity-2 - capacity-number
  capacity-3 - capacity-number
  capacity-4 - capacity-number
 )
 (:htn
  :tasks (and
   (deliver package-1 city-loc-5)
   (deliver package-2 city-loc-4)
   (deliver package-3 city-loc-3)
;   (deliver package-4 city-loc-2)
;   (deliver package-5 city-loc-1)
   )
  :ordering ( )
  :constraints ( ))
 (:init
  (capacity-predecessor capacity-0 capacity-1)
  (capacity-predecessor capacity-1 capacity-2)
  (capacity-predecessor capacity-2 capacity-3)
  (capacity-predecessor capacity-3 capacity-4)
  (road city-loc-1 city-loc-2);  
  (road city-loc-2 city-loc-1);   2--3
  (road city-loc-2 city-loc-3);  /    \
  (road city-loc-3 city-loc-2); 1      4
  (road city-loc-3 city-loc-4);  \    /
  (road city-loc-4 city-loc-3);   6--5
  (road city-loc-4 city-loc-5)
  (road city-loc-5 city-loc-4)
  (road city-loc-5 city-loc-6)
  (road city-loc-6 city-loc-5)
  (road city-loc-6 city-loc-1)
  (road city-loc-1 city-loc-6)
  (at package-1 city-loc-1)
  (at package-2 city-loc-2)
  (at package-3 city-loc-3)
  (at package-4 city-loc-4)
  (at package-5 city-loc-5)
  (at truck-1 city-loc-1)
  (at truck-2 city-loc-4)
  (capacity truck-1 capacity-4)
  (capacity truck-2 capacity-4)
 )
)
