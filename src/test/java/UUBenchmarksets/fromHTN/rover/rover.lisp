(define (domain rover)
  (:requirements :typing)

  (:types
        rover waypoint objective camera mode lander - object
  )

  (:predicates
     (at ?rover ?from - object))
     (at_lander ?l ?y - object))
     (at_rock_sample ?p - object))
     (at_soil_sample ?p - object))
     (available ?x - object))
     (calibrated ?i ?r - object))
     (calibration_target ?camera ?objective - object))
     (can_traverse ?rover ?from ?to - object))
     (channel_free ?l - object))
     (communicated_image_data ?o ?m - object))
     (communicated_rock_data ?p - object))
     (communicated_soil_data ?p - object))
     (empty ?s - object))
     (equipped_for_imaging ?rover - object))
     (equipped_for_rock_analysis ?rover - object))
     (equipped_for_soil_analysis ?rover - object))
     (full ?s - object))
     (have_image ?r ?o ?m - object))
     (have_rock_analysis ?x ?p - object))
     (have_soil_analysis ?x ?p - object))
     (on_board ?camera ?rover - object))
     (store_of ?s ?rover - object))
     (supports ?camera ?mode - object))
     (visible ?x ?y - object))
     (visible_from ?objective ?waypoint - object))
     (visited ?mid - object))
  )

  (:task calibrate :parameters (?rover ?camera - object))
  (:task empty-store :parameters (?s ?rover - object))
  (:task get_image_data :parameters (?objective ?mode - object))
  (:task get_rock_data :parameters (?waypoint - object))
  (:task get_soil_data :parameters (?waypoint - object))
  (:task navigate :parameters (?rover ?to - object))
  (:task send_image_data :parameters (?rover ?objective ?mode - object))
  (:task send_rock_data :parameters (?rover ?waypoint - object))
  (:task send_soil_data :parameters (?rover ?waypoint - object))
  
  (:method m-empty-store-1
    :parameters (?s ?rover - object)
    :task (empty-store ?s ?rover)
    :precondition (empty ?s)
    :subtasks ()
  )

  (:method m-empty-store-2
    :parameters (?s ?rover - object)
    :task (empty-store ?s ?rover)
    :precondition (not (empty ?s))
    :subtasks (drop ?rover ?s)
  )

  (:method m-navigate-1
    :parameters (?rover ?from ?to - object)
    :task (navigate ?rover ?to)
    :precondition (at ?rover ?from)
    :ordered-subtasks (and 
        (visit ?from)
        (navigate ?rover ?from ?to)
        (unvisit ?from)
      )
  )

  (:method m-navigate-2
    :parameters (?rover ?from ?to - object)
    :task (navigate ?rover ?to)
    :precondition (at ?rover ?to)
    :ordered-subtasks ( )
  )

  (:method m-navigate-3
    :parameters (?rover ?from ?to - object)
    :task (navigate ?rover ?to)
    :precondition (and 
        (not (at ?rover ?to))
        (can_traverse ?rover ?from ?to)
      )
    :ordered-subtasks (and 
        (navigate ?rover ?from ?to)
      )
  )

  (:method m-navigate-4
    :parameters (?rover ?from ?to - object ?mid - waypoint)
    :task (navigate ?rover ?to)
    :precondition (and 
        (not (at ?rover ?to))
        (not (can_traverse ?rover ?from ?to))
        (can_traverse ?rover ?from ?mid)
        (not (visited ?mid))
      )
    :ordered-subtasks (and 
        (navigate ?rover ?from ?mid)
        (visit ?mid)
        (navigate ?rover ?mid ?to)
        (unvisit ?mid)
      )
  )

  (:method m-send_soil_data
    :parameters (?rover ?waypoint ?x ?y - object ?l - lander)
    :task (send_soil_data ?rover ?waypoint)
    :precondition (and 
        (at_lander ?l ?y)
        (visible ?x ?y)
      )
    :ordered-subtasks (and 
        (navigate ?rover ?x)
        (communicate_soil_data ?rover ?l ?waypoint ?x ?y)
      )
  )

  (:method m-get_soil_data
    :parameters (?waypoint - waypoint ?rover - rover ?s - object)
    :task (get_soil_data ?waypoint)
    :precondition (and 
        (store_of ?s ?rover)
        (equipped_for_soil_analysis ?rover)
      )
    :ordered-subtasks (and 
        (navigate ?rover ?waypoint)
        (empty-store ?s ?rover)
        (sample_soil ?rover ?s ?waypoint)
        (send_soil_data ?rover ?waypoint)
      )
  )

  (:method m-send_rock_data
    :parameters (?waypoint ?rover ?x ?y - object ?l - lander)
    :task (send_rock_data ?rover ?waypoint)
    :precondition (and 
        (at_lander ?l ?y)
        (visible ?x ?y)
      )
    :ordered-subtasks (and 
        (navigate ?rover ?x)
        (communicate_rock_data ?rover ?l ?waypoint ?x ?y)
      )
  )

  (:method m-get_rock_data
    :parameters (?waypoint - waypoint ?rover - rover ?s - object)
    :task (get_rock_data ?waypoint)
    :precondition (and 
        (equipped_for_rock_analysis ?rover)
        (store_of ?s ?rover)
      )
    :ordered-subtasks (and 
        (navigate ?rover ?waypoint)
        (empty-store ?s ?rover)
        (sample_rock ?rover ?s ?waypoint)
        (send_rock_data ?rover ?waypoint)
      )
  )

  (:method m-send_image_data
    :parameters (?rover ?objective ?mode ?x ?y - object ?l - lander)
    :task (send_image_data ?rover ?objective ?mode)
    :precondition (and 
        (at_lander ?l ?y)
        (visible ?x ?y)
      )
    :ordered-subtasks (and 
        (navigate ?rover ?x)
        (communicate_image_data ?rover ?l ?objective ?mode ?x ?y)
      )
  )

  (:method m-get_image_data
    :parameters (?objective - objective ?mode ?camera ?rover ?waypoint - object)
    :task (get_image_data ?objective ?mode)
    :precondition (and 
        (equipped_for_imaging ?rover)
        (on_board ?camera ?rover)
        (supports ?camera ?mode)
        (visible_from ?objective ?waypoint)
      )
    :ordered-subtasks (and 
        (calibrate ?rover ?camera)
        (navigate ?rover ?waypoint)
        (take_image ?rover ?waypoint ?objective ?camera ?mode)
        (send_image_data ?rover ?objective ?mode)
      )
  )

  (:method m-calibrate
    :parameters (?rover ?camera ?objective ?waypoint - object)
    :task (calibrate ?rover ?camera)
    :precondition (and 
        (calibration_target ?camera ?objective)
        (visible_from ?objective ?waypoint)
      )
    :ordered-subtasks (and 
        (navigate ?rover ?waypoint)
        (calibrate ?rover ?camera ?objective ?waypoint)
      )
  )

  (:action navigate
    :parameters (?x - rover ?y ?z - waypoint)
    :precondition (and
        (can_traverse ?x ?y ?z)
        (available ?x)
        (at ?x ?y)
        (visible ?y ?z)
      )
    :effect (and
        (not (at ?x ?y))
        (at ?x ?z)
      )
  )

  (:action sample_soil
    :parameters (?x - rover ?s - store ?p - waypoint)
    :precondition (and
        (at ?x ?p)
        (at_soil_sample ?p)
        (equipped_for_soil_analysis ?x)
        (store_of ?s ?x)
        (empty ?s)
      )
    :effect (and
        (not (empty ?s))
        (not (at_soil_sample ?p))
        (full ?s)
        (have_soil_analysis ?x ?p)
      )
  )

  (:action sample_rock
    :parameters (?x - rover ?s - store ?p - waypoint)
    :precondition (and
        (at_rock_sample ?p)
        (equipped_for_rock_analysis ?x)
        (store_of ?s ?x)
        (empty ?s)
      )
    :effect (and
        (not (empty ?s))
        (not (at_rock_sample ?p))
        (full ?s)
        (have_rock_analysis ?x ?p)
      )
  )

  (:action drop
    :parameters (?x - rover ?y - store)
    :precondition (and
        (store_of ?y ?x)
        (full ?y)
      )
    :effect (and
        (not (full ?y))
        (empty ?y)
      )
  )

  (:action calibrate
    :parameters (?r - rover ?i - camera ?t - objective ?w - waypoint)
    :precondition (and
        (equipped_for_imaging ?r)
        (calibration_target ?i ?t)
        (at ?r ?w)
        (visible_from ?t ?w)
        (on_board ?i ?r)
      )
    :effect (and
        (calibrated ?i ?r)
      )
  )

  (:action take_image
    :parameters (?r - rover ?p - waypoint ?o - objective ?i - camera ?m - mode)
    :precondition (and
        (calibrated ?i ?r)
        (on_board ?i ?r)
        (equipped_for_imaging ?r)
        (supports ?i ?m)
        (visible_from ?o ?p)
        (at ?r ?p)
      )
    :effect (and
        (not (calibrated ?i ?r))
        (have_image ?r ?o ?m)
      )
  )

  (:action communicate_soil_data
    :parameters (?r - rover ?l - lander ?p ?x ?y - waypoint)
    :precondition (and
        (at ?r ?x)
        (at_lander ?l ?y)
        (have_soil_analysis ?r ?p)
        (visible ?x ?y)
        (available ?r)
        (channel_free ?l)
      )
    :effect (and
        (not (available ?r))
        (not (channel_free ?l))
        (channel_free ?l)
        (communicated_soil_data ?p)
        (available ?r)
      )
  )

  (:action communicate_rock_data
    :parameters (?r - rover ?l - lander ?p ?x ?y - waypoint)
    :precondition (and
        (at ?r ?x)
        (at_lander ?l ?y)
        (have_rock_analysis ?r ?p)
        (visible ?x ?y)
        (available ?r)
        (channel_free ?l)
      )
    :effect (and
        (not (available ?r))
        (not (channel_free ?l))
        (channel_free ?l)
        (communicated_rock_data ?p)
        (available ?r)
      )
  )

  (:action communicate_image_data
    :parameters (?r - rover ?l - lander ?o - objective ?m - mode ?x ?y - waypoint)
    :precondition (and
        (at ?r ?x)
        (at_lander ?l ?y)
        (have_image ?r ?o ?m)
        (visible ?x ?y)
        (available ?r)
        (channel_free ?l)
      )
    :effect (and
        (not (available ?r))
        (not (channel_free ?l))
        (channel_free ?l)
        (communicated_image_data ?o ?m)
        (available ?r)
      )
  )

  (:action visit
    :parameters (?waypoint - object)
    :precondition ( )
    :effect (and
        (visited ?waypoint)
      )
  )

  (:action unvisit
    :parameters (?waypoint - object)
    :precondition ( )
    :effect (and
        (not (visited ?waypoint))
      )
  )
)

