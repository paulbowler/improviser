(ns improviser.core
  (:use improviser.arrangement)
  (:require [overtone.midi :as m]))

(distinct (map :description (m/midi-ports)))

(def synth (m/midi-out "IAC"))

(note-choice :C (voice-range :E1 :G3))

(chord-notes (chord-tones :G :m7) :9+13th)

(voicing-options (chord-notes (chord-tones :C :M7) :9+13th) (voice-range :E2 :G3))
(voicing-options (chord-notes (chord-tones :F :m7) :9+13th) (voice-range :E2 :G3))
(voicing-options (chord-notes (chord-tones :Bb :7) :9+13th) (voice-range :E2 :G3))

(generate-voicings :C :M7 :9+13th :E2 :G3)

(validate-chord-string! :Dm7:2)

(my-voicing :C :M7)

(def LADYBIRD [:CM7:8 :Fm7:4 :Bb7:4
               :CM7:8 :Bbm7:4 :Eb7:4
               :AbM7:8 :Am7:4 :D7:4
               :Dm7:4 :G7:4 :CM7:2 :Eb7:2 :AbM7:2 :D7:2])

(midi-player synth LADYBIRD my-voicing)

(map #(generate-midi-chords % my-voicing) LADYBIRD)

(generate-voicings :C :7 :shell :E2 :G4)
