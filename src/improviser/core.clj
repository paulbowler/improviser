(ns improviser.core
  (:use improviser.pitch)
  (:require [overtone.midi :as m]))

(def *tempo* 150)
(def *octave* 3)

(distinct (map :description (m/midi-ports)))

(def synth (m/midi-out "IAC"))

;; (m/midi-play synth [(chord :D4 :m9)(chord :g3 :9)(chord :c4 :6*9)] [80 80 80] [1000 1000 2000])

(def MIDI-CHORD-RE-STR "([A-G][#b]?)([m|M|d|a]?)([6|7|9]?)([A-G][#b]?)?:([0-9]+)" )
(def MIDI-CHORD-RE (re-pattern MIDI-CHORD-RE-STR))
(def ONLY-MIDI-CHORD-RE (re-pattern (str MIDI-CHORD-RE-STR)))

(defn chord-matcher
  "Determines whether a midi keyword is valid or not. If valid,
  returns a regexp match object"
  [mk]
  (re-find ONLY-MIDI-CHORD-RE (name mk)))

(defn validate-chord-string!
  "Throws a friendly exception if midi-keyword mk is not
  valid. Returns matches if valid."
  [mk]
  (let [matches (chord-matcher mk)]
    (when-not matches
      (throw (IllegalArgumentException.
              (str "Invalid midi-string. " mk
                   " does not appear to be in the required format i.e. C#M7:4"))))
    matches))

(defn chord-info
  "Takes a string representing a chord such as CM7:8 and returns a map of chord info"
  [chord-string]
  (let [[match pitch style tension root beats] (validate-chord-string! chord-string)
        beats                               (Integer. beats)
        pitch-octave                         (keyword (str pitch *octave*))
        chord-type                           (keyword (str style tension))
        duration                             (* *tempo* (Integer. beats))
        bass-note                            (or (keyword root) (keyword pitch))]
    {:match     match
     :pitch     pitch-octave
     :type      chord-type
     :root      bass-note
     :notes     (chord pitch-octave chord-type)
     :beats     beats
     :delay     0
     :rest      0
     :duration  duration}))

(validate-chord-string! :Dm7:2)

(str "C" 4)

(chord-info :Dm7A:2)

(def LADYBIRD [:CM9:8 :Fm7:4 :Bb7:4 :CM7:8
               :Bbm7:4 :Eb7:4 :AbM7:8 :Am7:4
               :D7:4 :Dm7:4 :G7:4 :CM7:2
               :Eb7:2 :AbM7:2 :D7:2])

(map chord-info LADYBIRD)

(m/midi-play-song synth (map chord-info LADYBIRD))

