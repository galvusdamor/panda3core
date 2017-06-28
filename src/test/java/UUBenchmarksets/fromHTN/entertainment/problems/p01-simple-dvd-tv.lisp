(define (problem p01-simple-dvd-tv)
 (:domain entertainment)
 (:objects
  simple-dvd-1 simple-tv-1 - equipment
  scart-cable-1 - equipment
  simple-dvd-1-scart-port-1 simple-tv-1-scart-port-1 - connector
  scart-cable-1-scart-plug-1 scart-cable-1-scart-plug-2 - connector
 )
 (:htn
  :tasks (and
     (av_connect simple-dvd-1 simple-tv-1)
   )
  :ordering ( )
  :constraints ( ))
 (:init
  (unused scart-cable-1-scart-plug-1) (unused scart-cable-1-scart-plug-2)
  (unused simple-dvd-1-scart-port-1) (unused simple-tv-1-scart-port-1)

  (out_connector scart-cable-1-scart-plug-1) (out_connector scart-cable-1-scart-plug-2) (out_connector simple-dvd-1-scart-port-1)
  (in_connector scart-cable-1-scart-plug-1) (in_connector scart-cable-1-scart-plug-2) (in_connector simple-tv-1-scart-port-1)

  (audio_connector scart-cable-1-scart-plug-1) (audio_connector scart-cable-1-scart-plug-2) (audio_connector simple-dvd-1-scart-port-1) (audio_connector simple-tv-1-scart-port-1)
  (video_connector scart-cable-1-scart-plug-1) (video_connector scart-cable-1-scart-plug-2) (video_connector simple-dvd-1-scart-port-1) (video_connector simple-tv-1-scart-port-1)

  (conn_of simple-dvd-1 simple-dvd-1-scart-port-1)
  (conn_of simple-tv-1 simple-tv-1-scart-port-1)
  (conn_of scart-cable-1 scart-cable-1-scart-plug-1) (conn_of scart-cable-1 scart-cable-1-scart-plug-2)

  (compatible simple-dvd-1-scart-port-1 scart-cable-1-scart-plug-1)
  (compatible scart-cable-1-scart-plug-1 simple-dvd-1-scart-port-1)
  (compatible simple-dvd-1-scart-port-1 scart-cable-1-scart-plug-2)
  (compatible scart-cable-1-scart-plug-2 simple-dvd-1-scart-port-1)
  (compatible simple-tv-1-scart-port-1 scart-cable-1-scart-plug-1)
  (compatible scart-cable-1-scart-plug-1 simple-tv-1-scart-port-1)
  (compatible simple-tv-1-scart-port-1 scart-cable-1-scart-plug-2)
  (compatible scart-cable-1-scart-plug-2 simple-tv-1-scart-port-1)
 )
)
