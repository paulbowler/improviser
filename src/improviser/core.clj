(ns improviser.core
  (:require [overtone.midi :as m]))

(map :description (m/midi-ports))

(def synth (m/midi-out "IAC"))

(defn midi-chord
  "Play a seq of notes with the corresponding velocities and durations."
  [out notes velocity duration]
  (map #(m/midi-note out % velocity duration 0) notes))

(def mid-C 60)
(def dom7 [-12 4 6 10 14])
(def c7 (map #(+ mid-C %) dom7))

(midi-chord synth c7 70 1000)
