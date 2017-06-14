(define (problem p03-split-with-cable)
 (:domain entertainment)
 (:objects
  simple-dvd-1 simple-tv-1 - equipment
  scart-to-cinch-cable-1 - equipment
  simple-dvd-1-scart-port-1 simple-tv-1-cinch-port-1 simple-tv-1-cinch-port-2 - connector
  scart-to-cinch-cable-1-scart-plug-1 scart-to-cinch-cable-1-cinch-plug-2 scart-to-cinch-cable-1-cinch-plug-3 - connector
 )
 (:htn
  :tasks (and
     (av_connect simple-dvd-1 simple-tv-1)
   )
  :ordering ( )
  :constraints ( ))
 (:init
  (unused scart-to-cinch-cable-1-scart-plug-1) (unused scart-to-cinch-cable-1-cinch-plug-2) (unused scart-to-cinch-cable-1-cinch-plug-3)
  (unused simple-dvd-1-scart-port-1) (unused simple-tv-1-cinch-port-1) (unused simple-tv-1-cinch-port-2)

  (out_connector scart-to-cinch-cable-1-scart-plug-1) (out_connector scart-to-cinch-cable-1-cinch-plug-2) (out_connector scart-to-cinch-cable-1-cinch-plug-3) (out_connector simple-dvd-1-scart-port-1)
  (in_connector scart-to-cinch-cable-1-scart-plug-1) (in_connector scart-to-cinch-cable-1-cinch-plug-2) (in_connector scart-to-cinch-cable-1-cinch-plug-3) (in_connector simple-tv-1-cinch-port-1) (in_connector simple-tv-1-cinch-port-2)

  (audio_connector scart-to-cinch-cable-1-scart-plug-1) (audio_connector scart-to-cinch-cable-1-cinch-plug-2) (audio_connector simple-dvd-1-scart-port-1) (audio_connector simple-tv-1-cinch-port-2)
  (video_connector scart-to-cinch-cable-1-scart-plug-1) (video_connector scart-to-cinch-cable-1-cinch-plug-3) (video_connector simple-dvd-1-scart-port-1) (video_connector simple-tv-1-cinch-port-1)

  (conn_of simple-dvd-1 simple-dvd-1-scart-port-1)
  (conn_of simple-tv-1 simple-tv-1-cinch-port-1) (conn_of simple-tv-1 simple-tv-1-cinch-port-2)
  (conn_of scart-to-cinch-cable-1 scart-to-cinch-cable-1-scart-plug-1) (conn_of scart-to-cinch-cable-1 scart-to-cinch-cable-1-cinch-plug-2) (conn_of scart-to-cinch-cable-1 scart-to-cinch-cable-1-cinch-plug-3)

  (compatible simple-dvd-1-scart-port-1 scart-to-cinch-cable-1-scart-plug-1)
  (compatible scart-to-cinch-cable-1-scart-plug-1 simple-dvd-1-scart-port-1)
  (compatible simple-tv-1-cinch-port-1 scart-to-cinch-cable-1-cinch-plug-2)
  (compatible scart-to-cinch-cable-1-cinch-plug-2 simple-tv-1-cinch-port-1)
  (compatible simple-tv-1-cinch-port-1 scart-to-cinch-cable-1-cinch-plug-3)
  (compatible scart-to-cinch-cable-1-cinch-plug-3 simple-tv-1-cinch-port-1)
  (compatible simple-tv-1-cinch-port-2 scart-to-cinch-cable-1-cinch-plug-2)
  (compatible scart-to-cinch-cable-1-cinch-plug-2 simple-tv-1-cinch-port-2)
  (compatible simple-tv-1-cinch-port-2 scart-to-cinch-cable-1-cinch-plug-3)
  (compatible scart-to-cinch-cable-1-cinch-plug-3 simple-tv-1-cinch-port-2)
 )
)
