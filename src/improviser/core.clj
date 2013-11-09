(ns improviser.core
  (:require [overtone.midi :as m]))


(def keyboard (m/midi-in))

(def phat-synth (m/midi-out))

; Connect ins and outs easily
(m/midi-route keyboard phat-synth)

; Trigger a note (note 40, velocity 100)
(m/midi-note-on phat-synth 40 100)
(Thread/sleep 500)
(m/midi-note-off phat-synth 40)

; Or the short-hand version to start and stop a note
(m/midi-note phat-synth 40 100 500)

; And the same thing with a sequence of notes
(m/midi-play phat-synth [30 41 40 55 42 34] [80 50 20 40 50 80] [250 500 250 250 500 250])

(def mid-C 64)
(def dom7 [0 4 7 10])
(def c7 (map #(+ mid-C %) dom7))
(take 5 c7)

(defn midi-chord
  "Play a seq of notes with the corresponding velocities and durations."
  [out notes velocity duration]
  (map #(m/midi-note phat-synth % velocity duration) notes))

(midi-chord phat-synth c7 80 1000)
