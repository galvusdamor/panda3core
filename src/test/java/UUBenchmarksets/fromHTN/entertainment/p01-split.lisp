;; /home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/java/UUBenchmarksets/fromHTN/entertainment/entertainment-domain.lisp /home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/java/UUBenchmarksets/fromHTN/entertainment/p01-split.lisp 43 -astarRCGTDR -randomSelection -multicount -numPrec
(define (problem p01-split)
 (:domain entertainment)
 (:objects
  dvd-1 tv-1 scart-to-cinch - device
  p-dvd-scart-1 - port
  p-tv-cinch-1 p-tv-cinch-2 - port
  p-adapter-scart p-adapter-cinch-1 p-adapter-cinch-2 - port
  scart-cable-1 - cable
  pl-scart-1 pl-scart-2 - plug
  cinch-cable-1 cinch-cable-2 - cable
  pl-cinch-1-1 pl-cinch-1-2 pl-cinch-2-1 pl-cinch-2-2 - plug
 )
 (:htn
  :tasks (and
     (av_connect dvd-1 tv-1)
   )
  :ordering ( )
  :constraints ( ))
 (:init
  (unused pl-scart-1) (unused pl-scart-2) (unused p-dvd-scart-1) (unused p-tv-cinch-1) (unused p-tv-cinch-2) (unused pl-cinch-1-1) (unused pl-cinch-1-2) (unused pl-cinch-2-1) (unused pl-cinch-2-2) (unused p-adapter-cinch-1) (unused p-adapter-cinch-2) (unused p-adapter-scart)
  
  (out_port p-dvd-scart-1)
  (in_port p-tv-cinch-1)
  (in_port p-tv-cinch-2)
  (in_port p-adapter-scart)
  (out_port p-adapter-cinch-1)
  (out_port p-adapter-cinch-2)
  
  (port_of dvd-1 p-dvd-scart-1)
  (port_of tv-1 p-tv-cinch-1)
  (port_of tv-1 p-tv-cinch-2)
  (port_of scart-to-cinch p-adapter-scart)
  (port_of scart-to-cinch p-adapter-cinch-1)
  (port_of scart-to-cinch p-adapter-cinch-2)
  
  (audio_port p-dvd-scart-1)
  (video_port p-dvd-scart-1)
  
  (audio_port p-tv-cinch-1)
  (video_port p-tv-cinch-2)
  
  (audio_port p-adapter-scart)
  (video_port p-adapter-scart)
  (audio_port p-adapter-cinch-1)
  (video_port p-adapter-cinch-2)
  
  (audio_cable scart-cable-1)
  (video_cable scart-cable-1)
  
  (audio_cable cinch-cable-1)
  (video_cable cinch-cable-1)
  
  (audio_cable cinch-cable-2)
  (video_cable cinch-cable-2)
  
  (plug_of scart-cable-1 pl-scart-1)
  (plug_of scart-cable-1 pl-scart-2)
  
  (plug_of cinch-cable-1 pl-cinch-1-1)
  (plug_of cinch-cable-1 pl-cinch-1-2)

  (plug_of cinch-cable-2 pl-cinch-2-1)
  (plug_of cinch-cable-2 pl-cinch-2-2)
  
  (compatible p-dvd-scart-1 pl-scart-1)
  (compatible p-dvd-scart-1 pl-scart-2)

  (compatible p-tv-cinch-1 pl-cinch-1-1)
  (compatible p-tv-cinch-1 pl-cinch-1-2)
  (compatible p-tv-cinch-1 pl-cinch-2-1)
  (compatible p-tv-cinch-1 pl-cinch-2-2)

  (compatible p-tv-cinch-2 pl-cinch-1-1)
  (compatible p-tv-cinch-2 pl-cinch-1-2)
  (compatible p-tv-cinch-2 pl-cinch-2-1)
  (compatible p-tv-cinch-2 pl-cinch-2-2)

  (compatible p-adapter-scart pl-scart-1)
  (compatible p-adapter-scart pl-scart-2)
  (compatible p-adapter-cinch-1 pl-cinch-1-1)
  (compatible p-adapter-cinch-1 pl-cinch-1-2)
  (compatible p-adapter-cinch-1 pl-cinch-2-1)
  (compatible p-adapter-cinch-1 pl-cinch-2-2)
  (compatible p-adapter-cinch-2 pl-cinch-1-1)
  (compatible p-adapter-cinch-2 pl-cinch-1-2)
  (compatible p-adapter-cinch-2 pl-cinch-2-1)
  (compatible p-adapter-cinch-2 pl-cinch-2-2)
 )
)
