(ns improviser.core
  (:require [overtone.midi :as m]))

(map :description (m/midi-ports))

(def synth (m/midi-out "IAC"))

(def mid-C 60)
(def F3 53)
(def dom7 [0 4 7 10 14 18 21])
(def M7 [0 4 7 11 14 17 21])
(def c7 (map #(+ mid-C %) dom7))
(def fM7 (map #(+ F3 %) M7))
(def rst [])

(m/midi-chord synth c7  70 1000)
(m/midi-chord synth fM7 70 1000)

c7
fM7
[c7 fM7]

(m/midi-chords synth [c7 rst fM7 rst c7] [70 100 80 100 70] [500 50 1100 50 2000])
