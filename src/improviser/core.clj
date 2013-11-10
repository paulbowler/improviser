(ns improviser.core
  (:require [overtone.midi :as m]))

(map :description (m/midi-ports))

(def synth (m/midi-out "IAC"))

(defn midi-chord
  "Play a seq of notes with the corresponding velocities and durations."
  [out notes velocity duration]
  (map #(m/midi-note out % velocity duration 0) notes))

(def mid-C 60)
(def F3 53)
(def dom7 [0 4 7 10 14 18 21])
(def M7 [0 4 7 11 14 17 21])
(def c7 (map #(+ mid-C %) dom7))
(def fM7 (map #(+ F3 %) M7))

(midi-chord synth c7  70 1000)
(midi-chord synth fM7 70 1000)
