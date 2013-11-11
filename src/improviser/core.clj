(ns improviser.core
  (:use improviser.pitch)
  (:require [overtone.midi :as m]))

(distinct (map :description (m/midi-ports)))

(def synth (m/midi-out "IAC"))

(m/midi-play synth [(chord :d3 :m9)(chord :g3 :9)(chord :c4 :6*9)] [80 80 80] [1000 1000 2000])
