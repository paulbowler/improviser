(ns improviser.core
  (:use improviser.pitch)
  (:require [overtone.midi :as m]))

(distinct (map :description (m/midi-ports)))

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

(seq? mid-C)

(m/midi-play synth [c7 rst fM7 rst c7] [70 100 60 100 70] [550 50 1150 50 2000])

(def changes [:c7:4 :c7:4 :fM7:2 :c7:4])

(map #(name %) changes)

(m/midi-play synth [[40 47 40][52 59 52][40 47 49]] [80 100 110] [250 500 250])

(m/midi-play synth [(chord :d4 :m9)(chord :g3 :9)(chord :c4 :6*9)] [80 80 80] [1000 1000 2000])

(chord :d-1 :9)

(note-info "C4")
