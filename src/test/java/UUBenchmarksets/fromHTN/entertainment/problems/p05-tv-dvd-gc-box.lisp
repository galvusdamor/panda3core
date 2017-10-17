(define (problem p05-tv-dvd-gc-box)
 (:domain entertainment)
 (:objects
  dvd-1 tv1-1 game-console-1 scart-cable-1 scart-cable-2 scart-cable-3 cinch-cable-1 cinch-cable-2 active-speaker-1 - equipment
  dvd-1-scart-1 tv1-1-scart-1 tv1-1-scart-2 tv1-1-cinch-3 game-console-1-scart-1 scart-cable-1-scart-1 scart-cable-1-scart-2 scart-cable-2-scart-1 scart-cable-2-scart-2 scart-cable-3-scart-1 scart-cable-3-scart-2 cinch-cable-1-cinch-1 cinch-cable-1-cinch-2 cinch-cable-2-cinch-1 cinch-cable-2-cinch-2 active-speaker-1-cinch-1 active-speaker-1-cinch-2 - connector
 )
 (:htn
  :ordered-tasks (and
     (a_connect tv1-1 active-speaker-1)
     (av_connect dvd-1 tv1-1)
     (av_connect game-console-1 tv1-1)
   )
  :ordering ( )
  :constraints ( ))
 (:init
  ;; device dvd-1
  (conn_of dvd-1 dvd-1-scart-1)
  (unused dvd-1-scart-1)
  (out_connector dvd-1-scart-1)
  (audio_connector dvd-1-scart-1)
  (video_connector dvd-1-scart-1)

  ;; device tv1-1
  (conn_of tv1-1 tv1-1-scart-1)
  (unused tv1-1-scart-1)
  (in_connector tv1-1-scart-1)
  (audio_connector tv1-1-scart-1)
  (video_connector tv1-1-scart-1)
  (conn_of tv1-1 tv1-1-scart-2)
  (unused tv1-1-scart-2)
  (in_connector tv1-1-scart-2)
  (audio_connector tv1-1-scart-2)
  (video_connector tv1-1-scart-2)
  (conn_of tv1-1 tv1-1-cinch-3)
  (unused tv1-1-cinch-3)
  (out_connector tv1-1-cinch-3)
  (audio_connector tv1-1-cinch-3)

  ;; device game-console-1
  (conn_of game-console-1 game-console-1-scart-1)
  (unused game-console-1-scart-1)
  (out_connector game-console-1-scart-1)
  (audio_connector game-console-1-scart-1)
  (video_connector game-console-1-scart-1)

  ;; device scart-cable-1
  (conn_of scart-cable-1 scart-cable-1-scart-1)
  (unused scart-cable-1-scart-1)
  (in_connector scart-cable-1-scart-1)
  (audio_connector scart-cable-1-scart-1)
  (video_connector scart-cable-1-scart-1)
  (conn_of scart-cable-1 scart-cable-1-scart-2)
  (unused scart-cable-1-scart-2)
  (out_connector scart-cable-1-scart-2)
  (audio_connector scart-cable-1-scart-2)
  (video_connector scart-cable-1-scart-2)

  ;; device scart-cable-2
  (conn_of scart-cable-2 scart-cable-2-scart-1)
  (unused scart-cable-2-scart-1)
  (in_connector scart-cable-2-scart-1)
  (audio_connector scart-cable-2-scart-1)
  (video_connector scart-cable-2-scart-1)
  (conn_of scart-cable-2 scart-cable-2-scart-2)
  (unused scart-cable-2-scart-2)
  (out_connector scart-cable-2-scart-2)
  (audio_connector scart-cable-2-scart-2)
  (video_connector scart-cable-2-scart-2)

  ;; device scart-cable-3
  (conn_of scart-cable-3 scart-cable-3-scart-1)
  (unused scart-cable-3-scart-1)
  (in_connector scart-cable-3-scart-1)
  (audio_connector scart-cable-3-scart-1)
  (video_connector scart-cable-3-scart-1)
  (conn_of scart-cable-3 scart-cable-3-scart-2)
  (unused scart-cable-3-scart-2)
  (out_connector scart-cable-3-scart-2)
  (audio_connector scart-cable-3-scart-2)
  (video_connector scart-cable-3-scart-2)

  ;; device cinch-cable-1
  (conn_of cinch-cable-1 cinch-cable-1-cinch-1)
  (unused cinch-cable-1-cinch-1)
  (in_connector cinch-cable-1-cinch-1)
  (audio_connector cinch-cable-1-cinch-1)
  (video_connector cinch-cable-1-cinch-1)
  (conn_of cinch-cable-1 cinch-cable-1-cinch-2)
  (unused cinch-cable-1-cinch-2)
  (out_connector cinch-cable-1-cinch-2)
  (audio_connector cinch-cable-1-cinch-2)
  (video_connector cinch-cable-1-cinch-2)

  ;; device cinch-cable-2
  (conn_of cinch-cable-2 cinch-cable-2-cinch-1)
  (unused cinch-cable-2-cinch-1)
  (in_connector cinch-cable-2-cinch-1)
  (audio_connector cinch-cable-2-cinch-1)
  (video_connector cinch-cable-2-cinch-1)
  (conn_of cinch-cable-2 cinch-cable-2-cinch-2)
  (unused cinch-cable-2-cinch-2)
  (out_connector cinch-cable-2-cinch-2)
  (audio_connector cinch-cable-2-cinch-2)
  (video_connector cinch-cable-2-cinch-2)

  ;; device active-speaker-1
  (conn_of active-speaker-1 active-speaker-1-cinch-1)
  (unused active-speaker-1-cinch-1)
  (in_connector active-speaker-1-cinch-1)
  (audio_connector active-speaker-1-cinch-1)
  (conn_of active-speaker-1 active-speaker-1-cinch-2)
  (unused active-speaker-1-cinch-2)
  (in_connector active-speaker-1-cinch-2)
  (audio_connector active-speaker-1-cinch-2)

  ;; compatibility of connections
  (compatible dvd-1-scart-1 scart-cable-1-scart-1)
  (compatible dvd-1-scart-1 scart-cable-1-scart-2)
  (compatible dvd-1-scart-1 scart-cable-2-scart-1)
  (compatible dvd-1-scart-1 scart-cable-2-scart-2)
  (compatible dvd-1-scart-1 scart-cable-3-scart-1)
  (compatible dvd-1-scart-1 scart-cable-3-scart-2)
  (compatible tv1-1-scart-1 scart-cable-1-scart-1)
  (compatible tv1-1-scart-1 scart-cable-1-scart-2)
  (compatible tv1-1-scart-1 scart-cable-2-scart-1)
  (compatible tv1-1-scart-1 scart-cable-2-scart-2)
  (compatible tv1-1-scart-1 scart-cable-3-scart-1)
  (compatible tv1-1-scart-1 scart-cable-3-scart-2)
  (compatible tv1-1-scart-2 scart-cable-1-scart-1)
  (compatible tv1-1-scart-2 scart-cable-1-scart-2)
  (compatible tv1-1-scart-2 scart-cable-2-scart-1)
  (compatible tv1-1-scart-2 scart-cable-2-scart-2)
  (compatible tv1-1-scart-2 scart-cable-3-scart-1)
  (compatible tv1-1-scart-2 scart-cable-3-scart-2)
  (compatible tv1-1-cinch-3 cinch-cable-1-cinch-1)
  (compatible tv1-1-cinch-3 cinch-cable-1-cinch-2)
  (compatible tv1-1-cinch-3 cinch-cable-2-cinch-1)
  (compatible tv1-1-cinch-3 cinch-cable-2-cinch-2)
  (compatible game-console-1-scart-1 scart-cable-1-scart-1)
  (compatible game-console-1-scart-1 scart-cable-1-scart-2)
  (compatible game-console-1-scart-1 scart-cable-2-scart-1)
  (compatible game-console-1-scart-1 scart-cable-2-scart-2)
  (compatible game-console-1-scart-1 scart-cable-3-scart-1)
  (compatible game-console-1-scart-1 scart-cable-3-scart-2)
  (compatible scart-cable-1-scart-1 dvd-1-scart-1)
  (compatible scart-cable-1-scart-1 tv1-1-scart-1)
  (compatible scart-cable-1-scart-1 tv1-1-scart-2)
  (compatible scart-cable-1-scart-1 game-console-1-scart-1)
  (compatible scart-cable-1-scart-2 dvd-1-scart-1)
  (compatible scart-cable-1-scart-2 tv1-1-scart-1)
  (compatible scart-cable-1-scart-2 tv1-1-scart-2)
  (compatible scart-cable-1-scart-2 game-console-1-scart-1)
  (compatible scart-cable-2-scart-1 dvd-1-scart-1)
  (compatible scart-cable-2-scart-1 tv1-1-scart-1)
  (compatible scart-cable-2-scart-1 tv1-1-scart-2)
  (compatible scart-cable-2-scart-1 game-console-1-scart-1)
  (compatible scart-cable-2-scart-2 dvd-1-scart-1)
  (compatible scart-cable-2-scart-2 tv1-1-scart-1)
  (compatible scart-cable-2-scart-2 tv1-1-scart-2)
  (compatible scart-cable-2-scart-2 game-console-1-scart-1)
  (compatible scart-cable-3-scart-1 dvd-1-scart-1)
  (compatible scart-cable-3-scart-1 tv1-1-scart-1)
  (compatible scart-cable-3-scart-1 tv1-1-scart-2)
  (compatible scart-cable-3-scart-1 game-console-1-scart-1)
  (compatible scart-cable-3-scart-2 dvd-1-scart-1)
  (compatible scart-cable-3-scart-2 tv1-1-scart-1)
  (compatible scart-cable-3-scart-2 tv1-1-scart-2)
  (compatible scart-cable-3-scart-2 game-console-1-scart-1)
  (compatible cinch-cable-1-cinch-1 tv1-1-cinch-3)
  (compatible cinch-cable-1-cinch-1 active-speaker-1-cinch-1)
  (compatible cinch-cable-1-cinch-1 active-speaker-1-cinch-2)
  (compatible cinch-cable-1-cinch-2 tv1-1-cinch-3)
  (compatible cinch-cable-1-cinch-2 active-speaker-1-cinch-1)
  (compatible cinch-cable-1-cinch-2 active-speaker-1-cinch-2)
  (compatible cinch-cable-2-cinch-1 tv1-1-cinch-3)
  (compatible cinch-cable-2-cinch-1 active-speaker-1-cinch-1)
  (compatible cinch-cable-2-cinch-1 active-speaker-1-cinch-2)
  (compatible cinch-cable-2-cinch-2 tv1-1-cinch-3)
  (compatible cinch-cable-2-cinch-2 active-speaker-1-cinch-1)
  (compatible cinch-cable-2-cinch-2 active-speaker-1-cinch-2)
  (compatible active-speaker-1-cinch-1 cinch-cable-1-cinch-1)
  (compatible active-speaker-1-cinch-1 cinch-cable-1-cinch-2)
  (compatible active-speaker-1-cinch-1 cinch-cable-2-cinch-1)
  (compatible active-speaker-1-cinch-1 cinch-cable-2-cinch-2)
  (compatible active-speaker-1-cinch-2 cinch-cable-1-cinch-1)
  (compatible active-speaker-1-cinch-2 cinch-cable-1-cinch-2)
  (compatible active-speaker-1-cinch-2 cinch-cable-2-cinch-1)
  (compatible active-speaker-1-cinch-2 cinch-cable-2-cinch-2)
 )
)