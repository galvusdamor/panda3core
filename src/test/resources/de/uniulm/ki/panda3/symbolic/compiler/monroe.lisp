;
; Log:
;
; - There is a type as well as a unary predicate "wrecked-car", and a 3-ary predicate "wrecked-vehicle" indicating that there is a wrecked vehicle between two points. The predicate "wrecked-car" is used once as negative precondition, but never set in a problem. "wrecked-vehicle" is used as precondition and set in the problem. -> Commented all lines concerning "wrecked-car", seems to be useless.
;
; - The predicate "non-ambulatory" used (negated) in some preconditions, but it is never set in a problem. In the original domain it can be inferred from "has-condition(broken-back)". However, this is also never set in any problem. -> Commented all lines concerning "non-ambulatory", seems to be useless.
;
; - The predicate "assigned-to-shelter" is included exactly once: as a existential quantified, negated precondition (not exists...), but is never set and can not be inferred -> Commented all lines concerning "wrecked-car", seems to be useless.
;
; - Due to the "can-drive(p)"-precondition, the parameter type of "p" was altered from "person" to "adult" in drive method and action.
;
; - added parameters for actions are a problem during recognition
; - in "load" is a vehicle used in "atloc" as a point -> therefore "vehicle" is moved from parent "object" to parent "point"
;
;
; FOR DEBUG:
; - forall condition is commented -> uncomment it for final version

(define (domain monroe)
    (:types
          person tree condition food callable hazardousness town generator gas-can point - object
          child adult - person
          waterco powerco - callable
          vehicle transport-hub shelter service-station hospital dump university park school mall - point
          bus car ambulance truck police-van power-van - vehicle
          ;; wrecked-car - vehicle
          work-crew shelter-leader tow-truck-driver plowdriver emt-crew police-unit bus-driver truck-driver - adult
          dump-truck backhoe tree-or-tow-truck water-truck snowplow - truck
          garbage-dump - dump
          hazard-team power-crew tree-crew water-crew construction-crew - work-crew
          tree-truck tow-truck - tree-or-tow-truck
    )

    (:constants 
       fema EBS police-chief - callable
       very-hazardous normal-hazardous medium-hazardous - hazardousness)

    (:predicates
      (atloc ?o - object ?p - point)
      (can-drive ?a - adult ?v - vehicle)
      (can-lift ?p - person ?o - object)
      (fit-in ?o - object ?v - vehicle)
      (has-condition ?p - person ?c - condition)
      (hazard-seriousness ?p1 - point ?p2 - point ?h - hazardousness)
      (hospital-doesnt-treat ?h - hospital ?c - condition)
      (in-town ?p - point ?t - town)
      (no-electricity ?p - point)
      (powerco-of ?t - town ?p - powerco)
      (road-snowy ?p1 - point ?p2 - point)
      (serious-condition ?c - condition)
      (tree-blocking-road ?p1 - point ?p2 - point ?t - tree)
      (waterco-of ?t - town ?w - waterco)
      (wrecked-vehicle ?p1 - point ?p2 - point ?v - vehicle)
      ;; (assigned-to-shelter ?s1 - shelter-leader ?s2 - shelter)
      ;; (non-ambulatory ?o1 - object)
      ;; (wrecked-car ?v1 - vehicle)
    )
    
    ;; artificial new top-level-tasks
    (:task mtlt :parameters ()) ;; use this in the initial task network to (possibly) have multiple top level tasks
    (:task tlt :parameters ())  ;; use this to have a single top level task
    
    ;; original top-level-tasks
    (:task set-up-shelter :parameters (?p0 - point))
    (:task fix-water-main :parameters (?p0 - point ?p1 - point))
    (:task clear-road-hazard :parameters (?p0 - point ?p1 - point))
    (:task clear-road-wreck :parameters (?p0 - point ?p1 - point))
    (:task clear-road-tree :parameters (?p0 - point ?p1 - point))
    (:task plow-road :parameters (?p0 - point ?p1 - point))
    (:task quell-riot :parameters (?p0 - point))
    (:task provide-temp-heat :parameters (?p0 - person))
    (:task fix-power-line :parameters (?p0 - point))
    (:task provide-medical-attention :parameters (?p0 - person))

    ;; other tasks
    (:task turn-on-power :parameters (?p0 - power-crew ?p1 - point))
    (:task clear-tree :parameters (?t0 - tree))
    (:task close-hole :parameters (?u0 - point ?u1 - point))
    (:task clear-wreck :parameters (?p0 - point ?p1 - point))
    (:task set-up-cones :parameters (?p0 - point ?p1 - point))
    (:task get-in :parameters (?o0 - object ?v1 - vehicle))
    (:task get-to :parameters (?o0 - object ?p1 - point))
    (:task remove-blockage :parameters (?t0 - tree))
    (:task block-road :parameters (?p0 - point ?p1 - point))
    (:task take-down-cones :parameters (?p0 - point ?p1 - point))
    (:task shut-off-power :parameters (?p0 - power-crew ?p1 - point))
    (:task open-hole :parameters (?p0 - point ?p1 - point))
    (:task declare-curfew :parameters (?t0 - town))
    (:task turn-on-water :parameters (?p0 - point ?p1 - point))
    (:task shut-off-water :parameters (?p0 - point ?p1 - point))
    (:task clean-up-hazard :parameters (?p0 - point ?p1 - point))
    (:task drive-to :parameters (?p0 - person ?v1 - vehicle ?p2 - point))
    (:task stabilize :parameters (?o0 - object))
    (:task unblock-road :parameters (?p0 - point ?p1 - point))
    (:task tow-to :parameters (?v0 - vehicle ?g1 - garbage-dump))
    (:task repair-line :parameters (?p0 - power-crew ?p1 - point))
    (:task add-fuel :parameters (?s0 - service-station ?o1 - object))
    (:task get-out :parameters (?o0 - object ?v1 - vehicle))
    (:task repair-pipe :parameters (?p0 - point ?p1 - point))
    (:task get-electricity :parameters (?p0 - point))
    (:task generate-temp-electricity :parameters (?p0 - point))
    (:task emt-treat :parameters (?o0 - object))
    (:task make-full-fuel :parameters (?g0 - generator))
    
    ;; insert n unordered tlts with n in N0
    (:method m-multiple-tlt
      :parameters ()
      :task (mtlt)
      :subtasks (and
        (mtlt)
        (tlt))
    )
    
    (:method m-del-mtlt
      :parameters ()
      :task (mtlt)
      :subtasks ()
    )
    
    ;; decompose the artificial tlt into an original tlt
    (:method m-tlt-set-up-shelter
      :parameters (?p0 - point)
      :task (tlt)
      :subtasks (and
        (set-up-shelter ?p0))
    )

    (:method m-tlt-fix-water-main
      :parameters (?p0 - point ?p1 - point)
      :task (tlt)
      :subtasks (and
        (fix-water-main ?p0 ?p1))
    )

    (:method m-tlt-clear-road-hazard
      :parameters (?p0 - point ?p1 - point)
      :task (tlt)
      :subtasks (and
        (clear-road-hazard ?p0 ?p1))
    )

    (:method m-tlt-clear-road-wreck
      :parameters (?p0 - point ?p1 - point)
      :task (tlt)
      :subtasks (and
        (clear-road-wreck ?p0 ?p1))
    )

    (:method m-tlt-clear-road-tree
      :parameters (?p0 - point ?p1 - point)
      :task (tlt)
      :subtasks (and
        (clear-road-tree ?p0 ?p1))
    )

    (:method m-tlt-plow-road
      :parameters (?p0 - point ?p1 - point)
      :task (tlt)
      :subtasks (and
        (plow-road ?p0 ?p1))
    )
    
    (:method m-tlt-quell-riot
      :parameters (?p0 - point)
      :task (tlt)
      :subtasks (and
        (quell-riot ?p0))
    )
    
    (:method m-tlt-provide-temp-heat
      :parameters (?p0 - person)
      :task (tlt)
      :subtasks (and
        (provide-temp-heat ?p0))
    )
    
    (:method m-tlt-fix-power-line
      :parameters (?p0 - point)
      :task (tlt)
      :subtasks (and
        (fix-power-line ?p0))
    )

    (:method m-tlt-provide-medical-attention
      :parameters (?p0 - person)
      :task (tlt)
      :subtasks (and
        (provide-medical-attention ?p0))
    )
    
    ;; set-up-shelter sets up a shelter at a certain location
    (:method m-set-up-shelter
      :parameters (?shelter - shelter ?leader - shelter-leader ?food - food ?loc - point)
      :task (set-up-shelter ?shelter)
      ;; :precondition (and
      ;;   (not (exists (?other-shelter - shelter)
      ;;                (assigned-to-shelter ?leader ?other-shelter)))
      :ordered-subtasks (and
        (get-electricity ?loc)
        (get-to ?leader ?loc)
        (get-to ?food ?loc))
    )
    
    ;; fix-water-main
    (:method m-fix-water-main
      :parameters (?from - point ?to - point)
      :task (fix-water-main ?from ?to)
      :ordered-subtasks (and
        (shut-off-water ?from ?to)
        (repair-pipe ?from ?to)
        (turn-on-water ?from ?to))
    )
    
    ;; clear-road-hazard - cleans up a hazardous spill
    (:method m-clear-road-hazard
      :parameters (?from - point ?to - point)
      :task (clear-road-hazard ?from ?to)
      :ordered-subtasks (and
        (block-road ?from ?to)
        (clean-up-hazard ?from ?to)
        (unblock-road ?from ?to))
    )
    
    ;;  clear-road-wreck - gets a wreck out of the road
    (:method m-clear-road-wreck
      :parameters (?from - point ?to - point)
      :task (clear-road-wreck ?from ?to)
      :ordered-subtasks (and
        (set-up-cones ?from ?to)
        (clear-wreck ?from ?to)
        (take-down-cones ?from ?to))
    )
    
    ;;  clear-road-tree - clears a tree that's in the road
    (:method m-clear-road-tree
      :parameters (?from - point ?to - point ?tree - tree)
      :task (clear-road-tree ?from ?to)
      :precondition (and
        (tree-blocking-road ?from ?to ?tree))
      :ordered-subtasks (and
        (set-up-cones ?from ?to)
        (clear-tree ?tree)
        (take-down-cones ?from ?to))
    )
    
    ;;  plow-road - must use nav-snowplow since regular cars can't drive if snowy
    (:method m-plow-road
      :parameters (?from - point ?to - point ?plow - snowplow ?driver - plowdriver ?plowloc - point)
      :task (plow-road ?from ?to)
      :precondition (and
        (road-snowy ?from ?to)
        (atloc ?plow ?plowloc))
      :ordered-subtasks (and
        (get-to ?driver ?plowloc)
        (navegate-snowplow ?driver ?plow ?from)
        (engage-plow ?driver ?plow)
        (navegate-snowplow ?driver ?plow ?to)
        (disengage-plow ?driver ?plow))
    )
    
    ;; quell-riot
    (:method m-quell-riot
      :parameters (?loc - point ?p1 - police-unit ?p2 - police-unit ?town - town)
      :task (quell-riot ?loc)
      :precondition (and
        (in-town ?loc ?town)
        (not (= ?p1 ?p2)))
      :ordered-subtasks (and
        (declare-curfew ?town)
        (get-to ?p1 ?loc)
        (get-to ?p2 ?loc)
        (set-up-barricades ?p1)
        (set-up-barricades ?p2))
    )
    
    ;; provide-temp-heat
    (:method m-provide-temp-heat-to-shelter
      :parameters (?person - person ?shelter - shelter)
      :task (provide-temp-heat ?person)
      :ordered-subtasks (and
        (get-to ?person ?shelter))
    )
    
    ;; provide-temp-heat
    (:method m-provide-temp-heat-local-electricity
      :parameters (?person - person ?ploc - point)
      :task (provide-temp-heat ?person)
      :precondition (and
        (atloc ?person ?ploc))
      :ordered-subtasks (and
        (generate-temp-electricity ?ploc)
        (turn-on-heat ?ploc))
    )
    
    ;; fix-power-line
    (:method m-fix-power-line
      :parameters (?lineloc - point ?crew - power-crew ?van - power-van)
      :task (fix-power-line ?lineloc)
      :ordered-subtasks (and
        (get-to ?crew ?lineloc)
        (get-to ?van ?lineloc)
        (repair-line ?crew ?lineloc))
    )
    
    ;; provide-medical-attention in hospital
    (:method m-provide-medical-attention-in-hospital
      :parameters (?person - person ?hosp - hospital ?cond - condition)
      :task (provide-medical-attention ?person)
      :precondition (and
        (has-condition ?person ?cond)
        (not (hospital-doesnt-treat ?hosp ?cond)))
      :ordered-subtasks (and
        (get-to ?person ?hosp)
        (treat-in-hospital ?person ?hosp))
    )
    
    (:method m-provide-medical-attention-simple-on-site
      :parameters (?person - person ?cond - condition)
      :task (provide-medical-attention ?person)
      :precondition (and
        (has-condition ?person ?cond)
        (not (serious-condition ?cond)))
      :ordered-subtasks (and
        (emt-treat ?person))
    )

    ;;;;;;;;;;;;;;;;;;; subgoals ;;;;;;;;;;;;;;;;;;;

    ;;  clean-up-hazard - just call the feds
    (:method m-clean-up-hazard-very-hazardous
      :parameters (?from - point ?to - point)
      :task (clean-up-hazard ?from ?to)
      :precondition (and
        (hazard-seriousness ?from ?to very-hazardous))
      :ordered-subtasks (and
        (call fema))
    )
    
    ;;  clean-up-hazard - we can take care of it
    (:method m-clean-up-hazard-normal
      :parameters (?from - point ?to - point ?ht - hazard-team)
      :task (clean-up-hazard ?from ?to)
      :precondition (and 
        (not (hazard-seriousness ?from ?to very-hazardous)))
      :ordered-subtasks (and
        (get-to ?ht ?from)
        (clean-hazard ?ht ?from ?to))
    )
    
    ;;  block-road - blocks off a road
    (:method m-block-road
      :parameters (?from - point ?to - point ?police - police-unit)
      :task (block-road ?from ?to)
      :subtasks (and
        (set-up-cones ?from ?to)
        (get-to ?police ?from))
    )
    
    ;;  unblock-road - unblocks a road
    (:method m-unblock-road
      :parameters (?from - point ?to - point)
      :task (unblock-road ?from ?to)
      :ordered-subtasks (and
        (take-down-cones ?from ?to))
    )
    
    ;;  get-electricity provides electricity to a site (if not already there)
    ;;  do nothing
    (:method m-get-electricity-noop
      :parameters (?loc - point)
      :task (get-electricity ?loc)
      :precondition (and
        (not (no-electricity ?loc)))
    )
    
    ;;  no-electricity
    (:method m-get-electricity
      :parameters (?loc - point)
      :task (get-electricity ?loc)
      :precondition (and
        (no-electricity ?loc))
      :ordered-subtasks (and
        (generate-temp-electricity ?loc))
    )
    
    ;;  repair-pipe - repairs a pipe at location
    (:method m-repair-pipe
      :parameters (?from - point ?to - point ?crew - water-crew)
      :task (repair-pipe ?from ?to)
      :ordered-subtasks (and
        (get-to ?crew ?from)
        (set-up-cones ?from ?to)
        (open-hole ?from ?to)
        (replace-pipe ?crew ?from ?to)
        (close-hole ?from ?to)
        (take-down-cones ?from ?to))
    )
    
    ;;  open-hole - opens a hole in the street
    (:method m-open-hole
      :parameters (?from - point ?to - point ?backhoe - backhoe)
      :task (open-hole ?from ?to)
      :ordered-subtasks (and
        (get-to ?backhoe ?from)
        (dig ?backhoe ?from))
    )
    
    ;;  close-hole - closes a hole in the street
    (:method m-close-hole
      :parameters (?from - point ?to - point ?backhoe - backhoe)
      :task (close-hole ?from ?to)
      :ordered-subtasks (and
        (get-to ?backhoe ?from)
        (fill-in ?backhoe ?from))
    )
    
    ;;  set-up-cones - sets up orange cones at road
    (:method m-set-up-cones
      :parameters (?from - point ?to - point ?crew - work-crew)
      :task (set-up-cones ?from ?to)
      :ordered-subtasks (and
        (get-to ?crew ?from)
        (place-cones ?crew))
    )
    
    ;;  take-down-cones
    (:method m-take-down-cones
      :parameters (?from - point ?to - point ?crew - work-crew)
      :task (take-down-cones ?from ?to)
      :ordered-subtasks (and
        (get-to ?crew ?from)
        (pickup-cones ?crew))
    )
    
    ;;  clear-wreck - gets rid of a wreck in any loc
    (:method m-clear-wreck
      :parameters (?from - point ?to - point ?dump - garbage-dump ?veh - vehicle)
      :task (clear-wreck ?from ?to)
      :precondition (and
        (wrecked-vehicle ?from ?to ?veh))
      :ordered-subtasks (and
        (tow-to ?veh ?dump))
    )
    
    ;;  tow-to - tows a vehicle somewhere
    (:method m-tow-to
      :parameters (?veh - vehicle ?to - garbage-dump ?ttruck - tow-truck ?vehloc - point)
      :task (tow-to ?veh ?to)
      :precondition (and
        (atloc ?veh ?vehloc))
      :ordered-subtasks (and
        (get-to ?ttruck ?vehloc)
        (hook-to-tow-truck ?ttruck ?veh)
        (get-to ?ttruck ?to)
        (unhook-from-tow-truck ?ttruck ?veh))
    )
    
    ;;  clear-tree - this gets rid of a tree in any loc
    (:method m-clear-tree
      :parameters (?tree - tree ?tcrew - tree-crew ?treeloc - point)
      :task (clear-tree ?tree)
      :precondition (and
        (atloc ?tree ?treeloc))
      :ordered-subtasks (and
        (get-to ?tcrew ?treeloc)
        (cut-tree ?tcrew ?tree)
        (remove-blockage ?tree))
    )
    
    ;;  remove-blockage
    (:method m-remove-blockage-move-to-side-of-street
      :parameters (?stuff - tree ?crew - work-crew ?loc - point)
      :task (remove-blockage ?stuff)
      :precondition (and
        (atloc ?stuff ?loc))
      :ordered-subtasks (and
        (get-to ?crew ?loc)
        (carry-blockage-out-of-way ?crew ?stuff))
    )
    
    (:method m-remove-blockage-carry-away
      :parameters (?stuff - tree ?dump - garbage-dump)
      :task (remove-blockage ?stuff)
      :ordered-subtasks (and
        (get-to ?stuff ?dump))
    )
    
    ;;  declare-curfew
    (:method m-declare-curfew
      :parameters (?town - town)
      :task (declare-curfew ?town)
      :subtasks (and
        (call EBS)
        (call police-chief))
    )
    
    ;;  generate-temp-electricity with-generator
    (:method m-generate-temp-electricity
      :parameters (?loc - point ?gen - generator)
      :task (generate-temp-electricity ?loc)
      :ordered-subtasks (and
        (make-full-fuel ?gen)
        (get-to ?gen ?loc)
        (hook-up ?gen ?loc)
        (turn-on ?gen))
    )
    
    ;;  make-full-fuel - makes sure arg1 is full of fuel
    (:method m-make-full-fuel-with-gas-can
      :parameters (?gen - generator ?gc - gas-can ?ss - service-station ?genloc - point)
      :task (make-full-fuel ?gen)
      :precondition (and
        (atloc ?gen ?genloc))
      :ordered-subtasks (and
        (get-to ?gc ?ss)
        (add-fuel ?ss ?gc)
        (get-to ?gc ?genloc)
        (pour-into ?gc ?gen))
    )
    
    (:method m-make-full-fuel-at-service-station
      :parameters (?gen - generator ?ss - service-station)
      :task (make-full-fuel ?gen)
      :ordered-subtasks (and
        (get-to ?gen ?ss)
        (add-fuel ?ss ?gen))
    )
    
    ;; add-fuel (at service-station)
    (:method m-add-fuel
      :parameters (?ss - service-station ?obj - object)
      :task (add-fuel ?ss ?obj)
      :subtasks (and
        (pay ?ss)
        (pump-gas-into ?ss ?obj))
    )
    
    ;;  repair-line
    (:method m-repair-line-with-tree
      :parameters (?crew - power-crew ?lineloc - point ?tree - tree)
      :task (repair-line ?crew ?lineloc)
      :precondition (and
        (atloc ?tree ?lineloc)
        (atloc ?crew ?lineloc))
      :subtasks (and
        (t0 (shut-off-power ?crew ?lineloc))
        (t1 (clear-tree ?tree))
        (t2 (remove-wire ?crew ?lineloc))
        (t3 (string-wire ?crew ?lineloc))
        (t4 (turn-on-power ?crew ?lineloc)))
      :order (and
        (t0 < t1)
        (t0 < t2)
        (t1 < t3)
        (t2 < t3)
        (t3 < t4)))
    
    ;;  repair-line without-tree
    (:method m-repair-line-without-tree
      :parameters (?crew - power-crew ?lineloc - point)
      :task (repair-line ?crew ?lineloc)
      :precondition (and
;;        (forall (?tree - tree) ;; todo this is commented for debug! uncomment it for a final version!
;;                   (not (atloc ?tree ?lineloc)))
        (atloc ?crew ?lineloc))
      :ordered-subtasks (and
        (shut-off-power ?crew ?lineloc)
        (remove-wire ?crew ?lineloc)
        (string-wire ?crew ?lineloc)
        (turn-on-power ?crew ?lineloc))
    )
    
    ;;  shut-off-power
    (:method m-shut-off-power
      :parameters (?crew - power-crew ?loc - point ?town - town ?powerco - powerco)
      :task (shut-off-power ?crew ?loc)
      :precondition (and
        (in-town ?loc ?town)
        (powerco-of ?town ?powerco))
      :ordered-subtasks (and
        (call ?powerco))
    )
    
    ;;  turn-on-power
    (:method m-turn-on-power
      :parameters (?crew - power-crew ?loc - point ?town - town ?powerco - powerco)
      :task (turn-on-power ?crew ?loc)
      :precondition (and
        (in-town ?loc ?town)
        (powerco-of ?town ?powerco))
      :ordered-subtasks (and
        (call ?powerco))
    )
    
    ;;  shut-off-water
    (:method m-shut-off-water
      :parameters (?from - point ?to - point ?town - town ?waterco - waterco)
      :task (shut-off-water ?from ?to)
      :precondition (and
        (in-town ?from ?town)
        (waterco-of ?town ?waterco))
      :ordered-subtasks (and
        (call ?waterco))
    )
    
    ;;  turn-on-water
    (:method m-turn-on-water
      :parameters (?from - point ?to - point ?town - town ?waterco - waterco)
      :task (turn-on-water ?from ?to)
      :precondition (and
        (in-town ?from ?town)
        (waterco-of ?town ?waterco))
      :ordered-subtasks (and
        (call ?waterco))
    )
    
    ;;  emt-treat
    (:method m-emt-treat
      :parameters (?person - object ?emt - emt-crew ?personloc - point)
      :task (emt-treat ?person)
      :precondition (and
        (atloc ?person ?personloc))
      :ordered-subtasks (and
        (get-to ?emt ?personloc)
        (treat ?emt ?person ?personloc))
    )
    
    ;;  stabilize
    (:method m-stabilize
      :parameters (?person - object)
      :task (stabilize ?person)
      :ordered-subtasks (and
        (emt-treat ?person))
    )
    
    ;;  get-to
    (:method m-get-to-already-there
      :parameters (?obj - object ?place - point)
      :task (get-to ?obj ?place)
      :precondition (and
        (atloc ?obj ?place))
    )
    
    (:method m-get-to-person-drives-themself
      :parameters (?person - person ?place - point ?veh - vehicle ?vehloc - point)
      :task (get-to ?person ?place)
      :precondition (and
        (not (atloc ?person ?place))
        (atloc ?veh ?vehloc)
        (atloc ?person ?vehloc))
      :ordered-subtasks (and
        (drive-to ?person ?veh ?place))
    )
    
    (:method m-get-to-vehicle-gets-driven
      :parameters (?veh - vehicle ?place - point ?person - person ?vehloc - point)
      :task (get-to ?veh ?place)
      :precondition (and
        (not (atloc ?veh ?place))
        (atloc ?veh ?vehloc)
        (atloc ?person ?vehloc))
      :ordered-subtasks (and
        (drive-to ?person ?veh ?place))
    )

    (:method m-get-to-as-cargo
      :parameters (?obj - object ?place - point ?veh - vehicle ?objloc - point)
      :task (get-to ?obj ?place)
      :precondition (and
        (not (atloc ?obj ?place))
        (atloc ?obj ?objloc)
        (fit-in ?obj ?veh)
        ;; (not (non-ambulatory ?obj))
        )
      :ordered-subtasks (and
        (get-to ?veh ?objloc)
        (get-in ?obj ?veh)
        (get-to ?veh ?place)
        (get-out ?obj ?veh))
    )
    
    ;; with-ambulance - same as above, just with ambulance
    (:method m-get-to-with-ambulance
      :parameters (?obj - object ?place - point ?veh - ambulance ?objloc - point)
      :task (get-to ?obj ?place)
      :precondition (and
        (not (atloc ?obj ?place))
        (atloc ?obj ?objloc)
        (fit-in ?obj ?veh))
      :ordered-subtasks (and
        (get-to ?veh ?objloc)
        (stabilize ?obj)
        (get-in ?obj ?veh)
        (get-to ?veh ?place)
        (get-out ?obj ?veh))
    )
    
    (:method m-drive-to
      :parameters (?person - adult ?veh - vehicle ?loc - point ?vehloc - point)
      :task (drive-to ?person ?veh ?loc)
      :precondition (and
        (atloc ?veh ?vehloc)
        (atloc ?person ?vehloc)
        (can-drive ?person ?veh))
      :ordered-subtasks (and
        (navegate-vehicle ?person ?veh ?loc ?vehloc))
    )
    
    (:method m-get-in-ambulatory-person
      :parameters (?obj - person ?veh - vehicle ?objloc - point)
      :task (get-in ?obj ?veh)
      :precondition (and
        (atloc ?obj ?objloc)
        (atloc ?veh ?objloc)
        ;; (not (non-ambulatory ?obj))
        )
      :ordered-subtasks (and
        (climb-in ?obj ?veh ?objloc))
    )
    
    ;; load-in
    (:method m-get-in-load-in
      :parameters (?obj - object ?veh - vehicle ?person - person ?objloc - point)
      :task (get-in ?obj ?veh)
      :precondition (and
        (atloc ?obj ?objloc)
        (atloc ?veh ?objloc)
        (can-lift ?person ?obj))
      :ordered-subtasks (and
        (get-to ?person ?objloc)
        (load ?person ?obj ?veh ?objloc))
    )
    
    (:method m-get-out-ambulatory-person
      :parameters (?obj - person ?veh - vehicle ?p - point)
      :task (get-out ?obj ?veh)
      ;; :precondition (and
      ;;   (not (non-ambulatory ?obj)))
      :ordered-subtasks (and
        (climb-out ?obj ?veh ?p))
    )
    
    ;; unload
    (:method m-get-out-unload
      :parameters (?obj - object ?veh - vehicle ?person - person ?vehloc - point)
      :task (get-out ?obj ?veh)
      :precondition (and
        (atloc ?veh ?vehloc)
        (can-lift ?person ?obj))
      :ordered-subtasks (and
        (get-to ?person ?vehloc)
        (unload ?person ?obj ?veh ?vehloc))
    )
    
    ;;;;;;;;;;;;;;;;;; actions ;;;;;;;;;;;;;;;;;;
    
    (:action navegate-snowplow
      :parameters   (?person - plowdriver ?veh - snowplow ?loc - point)
      :precondition ()
      :effect       ()
    )
    
    (:action engage-plow
      :parameters   (?person - plowdriver ?plow - snowplow)
      :precondition ()
      :effect       ()
    )
    
    (:action disengage-plow
      :parameters   (?person - plowdriver ?plow - snowplow)
      :precondition ()
      :effect       ()
    )
    
    (:action navegate-vehicle
      :parameters   (?person - adult ?veh - vehicle ?loc - point ?vehloc - point) ;; added parameter
      :precondition (and (atloc ?veh ?vehloc)
                         (atloc ?person ?vehloc)
                         (can-drive ?person ?veh)
                         ;; (not (wrecked-car ?veh))
                         )
      :effect       (and (atloc ?veh ?loc)
                         (atloc ?person ?loc)
                         (not (atloc ?veh ?vehloc))
                         (not (atloc ?person ?vehloc)))
    )
    
    (:action climb-in
      :parameters   (?obj - person ?veh - vehicle ?objloc - point) ;; added parameter
      :precondition (and (atloc ?obj ?objloc)
                         (atloc ?veh ?objloc)
                         (fit-in ?obj ?veh))
      :effect       (and (atloc ?obj ?veh)
                         (not (atloc ?obj ?objloc)))
    )
    
    (:action climb-out
      :parameters   (?obj - person ?veh - vehicle ?vehloc - point) ;; added parameter
      :precondition (and (atloc ?obj ?veh)
                         (atloc ?veh ?vehloc))
      :effect       (and (atloc ?obj ?vehloc)
                         (not (atloc ?obj ?veh)))
    )
    
    (:action load
      :parameters   (?person - person ?obj - object ?veh - vehicle ?objloc - point) ;; added parameter
      :precondition (and (atloc ?obj ?objloc)
                         (atloc ?veh ?objloc)
                         (atloc ?person ?objloc)
                         (fit-in ?obj ?veh))
      :effect       (and (atloc ?obj ?veh)
                         (not (atloc ?obj ?objloc)))
    )
    
    (:action unload
      :parameters   (?person - person ?obj - object ?veh - vehicle ?vehloc - point) ;; added parameter
      :precondition (and (atloc ?obj ?veh)
                         (atloc ?veh ?vehloc)
                         (atloc ?person ?vehloc))
      :effect       (and (atloc ?obj ?vehloc)
                         (not (atloc ?obj ?veh)))
    )
    
    (:action treat
      :parameters   (?emt - emt-crew ?person - object ?ploc - point) ;; added parameter
      :precondition (and (atloc ?person ?ploc)
                         (atloc ?emt ?ploc))
      :effect       ()
    )
    
    (:action treat-in-hospital
      :parameters   (?person - person ?hospital - hospital)
      :precondition (and (atloc ?person ?hospital))
      :effect       ()
    )
    
    (:action call
      :parameters   (?place - callable)
      :precondition ()
      :effect       ()
    )
    
    (:action remove-wire
      :parameters   (?crew - power-crew ?lineloc - point)
      :precondition ()
      :effect       ()
    )
    
    (:action string-wire
      :parameters   (?crew - power-crew ?lineloc - point)
      :precondition ()
      :effect       ()
    )
    
    (:action carry-blockage-out-of-way
      :parameters   (?crew - work-crew ?stuff - tree)
      :precondition ()
      :effect       ()
    )
    
    (:action cut-tree
      :parameters   (?crew - tree-crew ?tree - tree)
      :precondition ()
      :effect       ()
    )
    
    (:action hook-up
      :parameters   (?obj - generator ?loc - point)
      :precondition ()
      :effect       ()
    )
    
    (:action pour-into
      :parameters   (?obj - gas-can ?obj2 - generator)  
      :precondition ()
      :effect       ()
    )
    
    (:action turn-on
      :parameters   (?obj - generator)
      :precondition ()
      :effect       ()
    )
    
    (:action pay
      :parameters   (?loc - service-station)
      :precondition ()
      :effect       ()
    )
    
    (:action pump-gas-into
      :parameters   (?loc - service-station ?obj - object)
      :precondition ()
      :effect       ()
    )
    
    (:action turn-on-heat
      :parameters   (?loc - point)
      :precondition ()
      :effect       ()
    )
    
    (:action set-up-barricades
      :parameters   (?police - police-unit)
      :precondition ()
      :effect       ()
    )
    
    (:action place-cones
      :parameters   (?police - work-crew)
      :precondition ()
      :effect       ()
    )
    
    (:action pickup-cones
      :parameters   (?police - work-crew)
      :precondition ()
      :effect       ()
    )
    
    (:action hook-to-tow-truck
      :parameters   (?ttruck - tow-truck ?veh - vehicle)
      :precondition ()
      :effect       ()
    )
    
    (:action unhook-from-tow-truck
      :parameters   (?ttruck - tow-truck ?veh - vehicle)
      :precondition ()
      :effect       ()
    )
    
    (:action dig
      :parameters   (?backhoe - backhoe ?place - point)
      :precondition ()
      :effect       ()
    )
    
    (:action fill-in
      :parameters   (?backhoe - backhoe ?place - point)
      :precondition ()
      :effect       ()
    )
    
    (:action replace-pipe
      :parameters   (?crew - water-crew ?from - point ?to - point)
      :precondition ()
      :effect       ()
    )
    
    (:action clean-hazard
      :parameters   (?hazard-team - hazard-team ?from - point ?to - point)
      :precondition ()
      :effect       ()
    )
)
