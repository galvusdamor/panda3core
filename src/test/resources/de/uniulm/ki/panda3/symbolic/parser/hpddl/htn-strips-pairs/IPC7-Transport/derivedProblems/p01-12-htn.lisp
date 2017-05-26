; Transport city-sequential-40nodes-1000size-4degree-100mindistance-4trucks-16packages-2008seed

(define (problem transport-city-sequential-40nodes-1000size-4degree-100mindistance-4trucks-16packages-2008seed)
 (:domain transport)
 (:objects
  city-loc-1 - location
  city-loc-2 - location
  city-loc-3 - location
  city-loc-4 - location
  city-loc-5 - location
  city-loc-6 - location
  city-loc-7 - location
  city-loc-8 - location
  city-loc-9 - location
  city-loc-10 - location
  city-loc-11 - location
  city-loc-12 - location
  city-loc-13 - location
  city-loc-14 - location
  city-loc-15 - location
  city-loc-16 - location
  city-loc-17 - location
  city-loc-18 - location
  city-loc-19 - location
  city-loc-20 - location
  city-loc-21 - location
  city-loc-22 - location
  city-loc-23 - location
  city-loc-24 - location
  city-loc-25 - location
  city-loc-26 - location
  city-loc-27 - location
  city-loc-28 - location
  city-loc-29 - location
  city-loc-30 - location
  city-loc-31 - location
  city-loc-32 - location
  city-loc-33 - location
  city-loc-34 - location
  city-loc-35 - location
  city-loc-36 - location
  city-loc-37 - location
  city-loc-38 - location
  city-loc-39 - location
  city-loc-40 - location
  truck-1 - vehicle
  truck-2 - vehicle
  truck-3 - vehicle
  truck-4 - vehicle
  package-1 - package
  package-2 - package
  package-3 - package
  package-4 - package
  package-5 - package
  package-6 - package
  package-7 - package
  package-8 - package
  package-9 - package
  package-10 - package
  package-11 - package
  package-12 - package
  package-13 - package
  package-14 - package
  package-15 - package
  package-16 - package
  capacity-0 - capacity-number
  capacity-1 - capacity-number
  capacity-2 - capacity-number
  capacity-3 - capacity-number
  capacity-4 - capacity-number
 )
 (:htn
  :parameters (?v - vehicle ?l2 ?l3 - location)
  :tasks (and
   (deliver package-1 city-loc-35)
   (deliver package-2 city-loc-6)
   (deliver package-3 city-loc-16)
   (deliver package-4 city-loc-4)
   (deliver package-5 city-loc-37)
   (deliver package-6 city-loc-36)
   (deliver package-7 city-loc-22)
   (deliver package-8 city-loc-26)
   (deliver package-9 city-loc-4)
   (deliver package-10 city-loc-28)
   (deliver package-11 city-loc-6)
   (deliver package-12 city-loc-32)
  ; (deliver package-13 city-loc-35)
  ; (deliver package-14 city-loc-36)
  ; (deliver package-15 city-loc-12)
  ; (deliver package-16 city-loc-31)
   )
  :ordering ( )
  :constraints ( ))
 (:init
  (capacity-predecessor capacity-0 capacity-1)
  (capacity-predecessor capacity-1 capacity-2)
  (capacity-predecessor capacity-2 capacity-3)
  (capacity-predecessor capacity-3 capacity-4)
  (road city-loc-3 city-loc-1)
  (road city-loc-1 city-loc-3)
  (road city-loc-4 city-loc-3)
  (road city-loc-3 city-loc-4)
  (road city-loc-5 city-loc-1)
  (road city-loc-1 city-loc-5)
  (road city-loc-5 city-loc-3)
  (road city-loc-3 city-loc-5)
  (road city-loc-7 city-loc-2)
  (road city-loc-2 city-loc-7)
  (road city-loc-8 city-loc-2)
  (road city-loc-2 city-loc-8)
  (road city-loc-8 city-loc-7)
  (road city-loc-7 city-loc-8)
  (road city-loc-10 city-loc-9)
  (road city-loc-9 city-loc-10)
  (road city-loc-12 city-loc-9)
  (road city-loc-9 city-loc-12)
  (road city-loc-13 city-loc-1)
  (road city-loc-1 city-loc-13)
  (road city-loc-13 city-loc-5)
  (road city-loc-5 city-loc-13)
  (road city-loc-14 city-loc-6)
  (road city-loc-6 city-loc-14)
  (road city-loc-15 city-loc-5)
  (road city-loc-5 city-loc-15)
  (road city-loc-15 city-loc-6)
  (road city-loc-6 city-loc-15)
  (road city-loc-16 city-loc-11)
  (road city-loc-11 city-loc-16)
  (road city-loc-17 city-loc-2)
  (road city-loc-2 city-loc-17)
  (road city-loc-17 city-loc-7)
  (road city-loc-7 city-loc-17)
  (road city-loc-17 city-loc-8)
  (road city-loc-8 city-loc-17)
  (road city-loc-18 city-loc-5)
  (road city-loc-5 city-loc-18)
  (road city-loc-19 city-loc-6)
  (road city-loc-6 city-loc-19)
  (road city-loc-19 city-loc-11)
  (road city-loc-11 city-loc-19)
  (road city-loc-19 city-loc-14)
  (road city-loc-14 city-loc-19)
  (road city-loc-19 city-loc-16)
  (road city-loc-16 city-loc-19)
  (road city-loc-20 city-loc-6)
  (road city-loc-6 city-loc-20)
  (road city-loc-20 city-loc-15)
  (road city-loc-15 city-loc-20)
  (road city-loc-21 city-loc-13)
  (road city-loc-13 city-loc-21)
  (road city-loc-22 city-loc-9)
  (road city-loc-9 city-loc-22)
  (road city-loc-22 city-loc-10)
  (road city-loc-10 city-loc-22)
  (road city-loc-24 city-loc-2)
  (road city-loc-2 city-loc-24)
  (road city-loc-24 city-loc-4)
  (road city-loc-4 city-loc-24)
  (road city-loc-24 city-loc-17)
  (road city-loc-17 city-loc-24)
  (road city-loc-25 city-loc-14)
  (road city-loc-14 city-loc-25)
  (road city-loc-25 city-loc-19)
  (road city-loc-19 city-loc-25)
  (road city-loc-25 city-loc-21)
  (road city-loc-21 city-loc-25)
  (road city-loc-26 city-loc-6)
  (road city-loc-6 city-loc-26)
  (road city-loc-26 city-loc-13)
  (road city-loc-13 city-loc-26)
  (road city-loc-26 city-loc-14)
  (road city-loc-14 city-loc-26)
  (road city-loc-26 city-loc-21)
  (road city-loc-21 city-loc-26)
  (road city-loc-26 city-loc-25)
  (road city-loc-25 city-loc-26)
  (road city-loc-27 city-loc-21)
  (road city-loc-21 city-loc-27)
  (road city-loc-28 city-loc-3)
  (road city-loc-3 city-loc-28)
  (road city-loc-28 city-loc-4)
  (road city-loc-4 city-loc-28)
  (road city-loc-28 city-loc-5)
  (road city-loc-5 city-loc-28)
  (road city-loc-28 city-loc-18)
  (road city-loc-18 city-loc-28)
  (road city-loc-29 city-loc-7)
  (road city-loc-7 city-loc-29)
  (road city-loc-29 city-loc-8)
  (road city-loc-8 city-loc-29)
  (road city-loc-29 city-loc-23)
  (road city-loc-23 city-loc-29)
  (road city-loc-30 city-loc-20)
  (road city-loc-20 city-loc-30)
  (road city-loc-30 city-loc-22)
  (road city-loc-22 city-loc-30)
  (road city-loc-31 city-loc-1)
  (road city-loc-1 city-loc-31)
  (road city-loc-31 city-loc-3)
  (road city-loc-3 city-loc-31)
  (road city-loc-31 city-loc-4)
  (road city-loc-4 city-loc-31)
  (road city-loc-32 city-loc-20)
  (road city-loc-20 city-loc-32)
  (road city-loc-33 city-loc-9)
  (road city-loc-9 city-loc-33)
  (road city-loc-33 city-loc-10)
  (road city-loc-10 city-loc-33)
  (road city-loc-33 city-loc-12)
  (road city-loc-12 city-loc-33)
  (road city-loc-33 city-loc-22)
  (road city-loc-22 city-loc-33)
  (road city-loc-33 city-loc-30)
  (road city-loc-30 city-loc-33)
  (road city-loc-34 city-loc-11)
  (road city-loc-11 city-loc-34)
  (road city-loc-34 city-loc-32)
  (road city-loc-32 city-loc-34)
  (road city-loc-35 city-loc-15)
  (road city-loc-15 city-loc-35)
  (road city-loc-35 city-loc-18)
  (road city-loc-18 city-loc-35)
  (road city-loc-35 city-loc-20)
  (road city-loc-20 city-loc-35)
  (road city-loc-36 city-loc-12)
  (road city-loc-12 city-loc-36)
  (road city-loc-36 city-loc-30)
  (road city-loc-30 city-loc-36)
  (road city-loc-36 city-loc-33)
  (road city-loc-33 city-loc-36)
  (road city-loc-37 city-loc-1)
  (road city-loc-1 city-loc-37)
  (road city-loc-37 city-loc-27)
  (road city-loc-27 city-loc-37)
  (road city-loc-37 city-loc-31)
  (road city-loc-31 city-loc-37)
  (road city-loc-38 city-loc-10)
  (road city-loc-10 city-loc-38)
  (road city-loc-38 city-loc-22)
  (road city-loc-22 city-loc-38)
  (road city-loc-38 city-loc-23)
  (road city-loc-23 city-loc-38)
  (road city-loc-39 city-loc-2)
  (road city-loc-2 city-loc-39)
  (road city-loc-39 city-loc-4)
  (road city-loc-4 city-loc-39)
  (road city-loc-39 city-loc-7)
  (road city-loc-7 city-loc-39)
  (road city-loc-39 city-loc-18)
  (road city-loc-18 city-loc-39)
  (road city-loc-39 city-loc-24)
  (road city-loc-24 city-loc-39)
  (road city-loc-39 city-loc-28)
  (road city-loc-28 city-loc-39)
  (road city-loc-40 city-loc-22)
  (road city-loc-22 city-loc-40)
  (road city-loc-40 city-loc-30)
  (road city-loc-30 city-loc-40)
  (road city-loc-40 city-loc-35)
  (road city-loc-35 city-loc-40)
  (road city-loc-40 city-loc-38)
  (road city-loc-38 city-loc-40)
  (at package-1 city-loc-34)
  (at package-2 city-loc-33)
  (at package-3 city-loc-4)
  (at package-4 city-loc-33)
  (at package-5 city-loc-27)
  (at package-6 city-loc-32)
  (at package-7 city-loc-20)
  (at package-8 city-loc-33)
  (at package-9 city-loc-15)
  (at package-10 city-loc-12)
  (at package-11 city-loc-4)
  (at package-12 city-loc-18)
  (at package-13 city-loc-38)
  (at package-14 city-loc-35)
  (at package-15 city-loc-31)
  (at package-16 city-loc-33)
  (at truck-1 city-loc-2)
  (capacity truck-1 capacity-4)
  (at truck-2 city-loc-21)
  (capacity truck-2 capacity-3)
  (at truck-3 city-loc-18)
  (capacity truck-3 capacity-4)
  (at truck-4 city-loc-27)
  (capacity truck-4 capacity-2)
 )
)
