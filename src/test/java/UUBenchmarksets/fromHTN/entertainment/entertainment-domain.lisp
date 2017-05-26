;; /home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/java/UUBenchmarksets/fromHTN/entertainment/entertainment-domain.lisp /home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/java/UUBenchmarksets/fromHTN/entertainment/p00-minimal.lisp 43 -astarRCGTDR -randomSelection -multicount -numPrec
;; overall_task
;; ordered:
;; - unplug and dump broken cables
;; - unplug if necessary
;; - re-plug

(define (domain entertainment)
  (:requirements :typing)
  (:types
      hardware connector conn_type - object
      cable device - hardware
      port plug - connector
  )

  (:predicates
     (isCable ?h - hardware)
     (audio_cable ?c - cable)
     (audio_connected ?d - device ?c - cable)
     (audio_port ?po2 - port)
     (compatible ?po - port ?pl - plug)
     (connection_type ?con - connector ?t - conn_type)
     (in_port ?po2 - port)
     (out_port ?po1 - port)
     (port_of ?d2 - device ?po2 - port)
     (plug_of ?c - cable ?pl1 - plug)
     (plugged ?po1 ?po2 - port ?c - cable)
     (same ?d1 ?d2 - device)
     (unused ?con - connector)
     (video_cable ?c - cable)
     (video_connected ?d - device ?c - cable)
     (video_port ?po1 - port)
  )
  
  (:task a_connect :parameters (?d1 ?d2 - device))
  (:task v_connect :parameters (?d1 ?d2 - device))
  (:task av_connect :parameters (?d1 ?d2 - device))
  (:task direct_a_connect :parameters (?d1 ?d2 - device))
  (:task direct_v_connect :parameters (?d1 ?d2 - device))
  (:task direct_av_connect :parameters (?d1 ?d2 - device))

; av connections----------------------------------------------------------

  ; direct av connection
  (:method m-connect-direct-av
    :parameters (?d1 ?d2 - device)
    :task (av_connect ?d1 ?d2)
    :subtasks (direct_av_connect ?d1 ?d2)
  )

  ; indirect av connection
  (:method m-connect-rec-av-1
    :parameters (?d1 ?d2 ?d3 - device)
    :task (av_connect ?d1 ?d3)
    :subtasks (and
        (direct_av_connect ?d1 ?d2)
        (av_connect ?d2 ?d3))
  )

  ; indirect av connection
  (:method m-connect-rec-av-2
    :parameters (?d1 ?d2 ?d3 - device)
    :task (av_connect ?d1 ?d3)
    :subtasks (and
        (av_connect ?d1 ?d2)
        (direct_av_connect ?d2 ?d3))
  )

  ; indirect av with split (no join possible anymore)
  (:method m-connect-split
    :parameters (?d1 ?d2 - device)
    :task (av_connect ?d1 ?d2)
    :subtasks (and
        (v_connect ?d1 ?d2)
        (a_connect ?d1 ?d2))
  )

; a connections----------------------------------------------------------

  ; direct a connection
  (:method m-connect-direct-a
    :parameters (?d1 ?d2 - device)
    :task (a_connect ?d1 ?d2)
    :subtasks (direct_a_connect ?d1 ?d2)
  )

  ; indirect a connection
  (:method m-connect-rec-a
    :parameters (?d1 ?d2 ?d3 - device)
    :task (a_connect ?d1 ?d3)
    :subtasks (and
        (a_connect ?d1 ?d2)
        (direct_a_connect ?d2 ?d3))
  )

; v connections----------------------------------------------------------

  ; direct v connection
  (:method m-connect-direct-v
    :parameters (?d1 ?d2 - device)
    :task (v_connect ?d1 ?d2)
    :subtasks (direct_v_connect ?d1 ?d2)
  )

  ; indirect v connection
  (:method m-connect-rec-v
    :parameters (?d1 ?d2 ?d3 - device)
    :task (v_connect ?d1 ?d3)
    :subtasks (and
        (v_connect ?d1 ?d2)
        (direct_v_connect ?d2 ?d3))
  )

; direct connections------------------------------------------------------

  (:method m-dconnect-av
    :parameters (?d1 ?d2 - device ?po1 ?po2 - port
                 ?c - cable ?pl1 ?pl2 - plug)
    :task (direct_av_connect ?d1 ?d2)
    :precondition (and
        ;;(not (= ?pl1 ?pl2))
        ;;(not (= ?d1 ?d2))
        (audio_port ?po1)
        (audio_port ?po2)
        (audio_cable ?c)
        (video_port ?po1)
        (video_port ?po2)
        (video_cable ?c)
        (out_port ?po1)
        (in_port ?po2)
      )
    :subtasks (and
        (plug ?d1 ?po1 ?c ?pl1)
        (plug ?d2 ?po2 ?c ??pl2))
  )

  (:method m-dconnect-a
    :parameters (?d1 ?d2 - device ?po1 ?po2 - port
                 ?c - cable ?pl1 ?pl2 - plug)
    :task (direct_a_connect ?d1 ?d2)
    :precondition (and
        ;;(not (= ?pl1 ?pl2))
        ;;(not (= ?d1 ?d2))
        (audio_port ?po1)
        (audio_port ?po2)
        (audio_cable ?c)
        (out_port ?po1)
        (in_port ?po2)
      )
    :subtasks (and
        (plug ?d1 ?po1 ?c ?pl1)
        (plug ?d2 ?po2 ?c ??pl2))
  )

  (:method m-dconnect-v
    :parameters (?d1 ?d2 - device ?po1 ?po2 - port
                 ?c - cable ?pl1 ?pl2 - plug)
    :task (direct_v_connect ?d1 ?d2)
    :precondition (and
        ;;(not (= ?pl1 ?pl2))
        ;;(not (= ?d1 ?d2))
        (video_port ?po1)
        (video_port ?po2)
        (video_cable ?c)
        (out_port ?po1)
        (in_port ?po2)
      )
    :subtasks (and
        (plug ?d1 ?po1 ?c ?pl1)
        (plug ?d2 ?po2 ?c ??pl2))
  )

; primitives -------------------------------------------------------------
  
  (:action plug
    :parameters (?d - device ?po - port 
                 ?c - cable ?pl - plug
                 ?t - conn_type)
    :precondition (and
        (unused ?po) (unused ?pl)
        (port_of ?d ?po)
        (plug_of ?c ?pl)
        (connection_type ?po ?t)
        (connection_type ?pl ?t)
      )
    :effect (and
        ;(plugged ?po ?pl)
        (when (and (audio_port ?po)
                   (audio_cable ?c))
              (audio_connected ?d ?c))
        (when (and (video_port ?po)
                   (video_cable ?c))
              (video_connected ?d ?c))
        (not (unused ?po))(not (unused ?pl)))
      )
  )
)
;;   (:action unplug
;;     :parameters (?d1 ?d2 - device ?po1 ?po2 - port
;;                  ?c - cable ?pl1 ?pl2 - plug)
;;     :precondition (and
;;         (plugged ?po1 ?po2 ?c)
;;         (port_of ?d1 ?po1)
;;         (port_of ?d2 ?po2)
;;         (plug_of ?c ?pl1)
;;         (plug_of ?c ?pl2)
;;         (compatible ?po1 ?pl1)
;;         (compatible ?po2 ?pl2)
;;       )
;;     :effect (and
;;         (not (audio_connected ?d1 ?d2))
;;         (not (video_connected ?d1 ?d2))
;; 
;;         (when (exists <so wie oben plus>
;;                    (audio_port ?d1)
;;                    (audio_port ?d2)
;;                    (audio_cable ?fc))
;;               (audio_connected ?d1 ?d2))
;;         (when (exists <so wie oben plus>
;;                    (video_port ?d1)
;;                    (video_port ?d2)
;;                    (video_cable ?c))
;;               (video_connected ?d1 ?d2))
;;       )
;;   )
