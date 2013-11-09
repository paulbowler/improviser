(ns improviser.core
  (:require [overtone.midi :as m]))


(def keyboard (m/midi-in))

(def phat-synth (m/midi-out))


(def ax (m/midi-in "axiom"))

; Connect ins and outs easily
(m/midi-route m/keyboard m/phat-synth)

; Trigger a note (note 40, velocity 100)
(m/midi-note-on phat-synth 40 100)
(Thread/sleep 500)
(m/midi-note-off m/phat-synth 0)

; Or the short-hand version to start and stop a note
(m/midi-note m/phat-synth 40 100 500)

; And the same thing with a sequence of notes
(m/midi-play m/phat-synth [40 47 40] [80 50 110] [250 500 250])
