(define (problem paper)
   (:domain driverlog)
   (:objects
     d1 - driver
     d2 - driver
     d3 - driver
     d4 - driver
     t1 - truck
     t2 - truck
     t3 - truck
     t4 - truck
     s1 - location
     s2 - location
     p1 - location
   )
   (:init
     (at d1 p1)
     (at d2 s1)
     (at t1 s1)
     (at t2 s1)
     (at t3 s1)
     (at t4 s2)
     (empty t2)
     (empty t3)
     (path p1 s1)
     (path s1 p1)
     (link s1 s2)
     (link s2 s1)
     (driving d3 t4)
     (driving d4 t1)
   )
   (:goal )
)
