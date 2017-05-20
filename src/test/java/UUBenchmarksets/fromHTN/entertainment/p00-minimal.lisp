(define (problem p00-minimal)
 (:domain entertainment)
 (:objects
  dvd-1 - device
  tv-1 - device
  p-dvd-scart-1 - port
  p-tv-scart-1 - port
  scart-cable-1 - cable
  pl-scart-1 - plug
  pl-scart-2 - plug
 )
 (:htn
  :tasks (and
   (av_connect dvd-1 tv-1)
   )
  :ordering ( )
  :constraints ( ))
 (:init
  (unused pl-scart-1)
  (unused pl-scart-2)
  (unused p-dvd-scart-1)
  (unused p-tv-scart-1)
  (out_port p-dvd-scart-1)
  (in_port p-tv-scart-1)
  (port_of tv-1 p-tv-scart-1)
  (port_of dvd-1 p-dvd-scart-1)
  (audio_port p-dvd-scart-1)
  (video_port p-dvd-scart-1)
  (audio_port p-tv-scart-1)
  (video_port p-tv-scart-1)
  (audio_cable scart-cable-1)
  (video_cable scart-cable-1)
  (plug_of scart-cable-1 pl-scart-1)
  (plug_of scart-cable-1 pl-scart-2)
  (compatible p-tv-scart-1 pl-scart-1)
  (compatible p-tv-scart-1 pl-scart-2)
  (compatible p-dvd-scart-1 pl-scart-1)
  (compatible p-dvd-scart-1 pl-scart-2)
 )
)
