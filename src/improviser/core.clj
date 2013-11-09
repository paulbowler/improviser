(ns improviser.core
  (:require [overtone.midi :as m]))

(def synth (m/midi-out "IAC Bus 2"))

(defn midi-chord
  "Play a seq of notes with the corresponding velocities and durations."
  [out notes velocity duration]
  (map #(m/midi-note out % velocity duration) notes))

(def mid-C 60)
(def dom7 [4 6 9 10 14])
(def c7 (map #(+ mid-C %) dom7))

(midi-chord synth c7 80 1000)

(m/midi-ports)
