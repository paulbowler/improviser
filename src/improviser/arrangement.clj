(ns improviser.arrangement
  (:use improviser.pitch)
  (:require [overtone.midi :as m]))

(def *tempo* 200)
(def *octave* 3)

(def NOTE {:C  0  :c  0  :b# 0  :B# 0
           :C# 1  :c# 1  :Db 1  :db 1  :DB 1  :dB 1
           :D  2  :d  2
           :D# 3  :d# 3  :Eb 3  :eb 3  :EB 3  :eB 3
           :E  4  :e  4
           :E# 5  :e# 5  :F  5  :f  5
           :F# 6  :f# 6  :Gb 6  :gb 6  :GB 6  :gB 6
           :G  7  :g  7
           :G# 8  :g# 8  :Ab 8  :ab 8  :AB 8  :aB 8
           :A  9  :a  9
           :A# 10 :a# 10 :Bb 10 :bb 10 :BB 10 :bB 10
           :B  11 :b  11 :Cb 11 :cb 11 :CB 11 :cB 11})

(def CHORDS
  (let [major7 #{0 4 7 11 14 18 21}
        dom7   #{0 4 7 10 14 18 21}
        hdim7  #{0 4 6 10 14 18 21}
        minor7 #{0 3 7 10 14 17 21}
        aug7   #{0 4 8 10 14 17 21}
        dim7   #{0 3 6 9}]
    {:M7         major7
     :m7         minor7
     :7          dom7
     :a7         aug7
     :d7         dim7
     :h7         hdim7}))

(def POSITION {:1st  0
               :3rd  1
               :5th  2
               :7th  3
               :9th  4
               :11th 5
               :13th 6})

(def VOICINGS {:basic  [:1st :3rd :5th :7th]
               :shell  [:3rd :7th]
               :ninth  [:3rd :7th :9th]
               :9+13th [:3rd :7th :9th :13th]})

(defn voice-range [start end]
  (range (note start) (note end)))

(defn note-choice [note range]
  (map find-note-name (filter #(= (note NOTE) (rem % 12)) range)))

(note-choice :C (voice-range :E1 :G3))

(defn find-notes [notes]
  (map #(REVERSE-NOTES (rem % 12)) notes))

(defn chord-tones [note chord]
  (map #(+ (note NOTE) %) (chord CHORDS)))

(defn chord-notes [tones voicing]
  (find-notes (map #(nth tones (% POSITION)) (voicing VOICINGS))))

(defn voicing-options [notes range]
  (map #(note-choice % range) notes))

(defn generate-voicings [note chord-type voicing start end]
  (voicing-options (chord-notes (chord-tones note chord-type) voicing) (voice-range start end)))

(defn take-first [options]
  (map first options))

(def MIDI-CHORD-RE-STR "([A-G][#b]?)([m|M|d|a|h]?)([6|7|9]?)([A-G][#b]?)?:([0-9]+)" )
(def MIDI-CHORD-RE (re-pattern MIDI-CHORD-RE-STR))

(defn chord-matcher
  "Determines whether a midi keyword is valid or not. If valid,
  returns a regexp match object"
  [mk]
  (re-find MIDI-CHORD-RE (name mk)))

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

(defn generate-midi-chords
  "Takes a string representing a chord such as CM7:8 and returns a map of chord info"
  [chord-string f]
  (let [[match pitch style tension root beats] (validate-chord-string! chord-string)
        chord-key                              (keyword pitch)
        beats                                  (Integer. beats)
        pitch-octave                           (keyword (str pitch *octave*))
        chord-type                             (keyword (str style tension))
        duration                               (* *tempo* (Integer. beats))
        bass-note                              (or (keyword root) (keyword pitch))]
    {:match     match
     :key       chord-key
     :pitch     pitch-octave
     :type      chord-type
     :root      bass-note
     :notes     (f chord-key chord-type)
     :beats     beats
     :delay     0
     :rest      0
     :duration  duration}))

(defn my-voicing [chord-key chord-quality]
  (map note (take-first (generate-voicings chord-key chord-quality :9+13th :F3 :G4))))

(defn midi-player [instrument chords voicing]
  (m/midi-play-song instrument (map #(generate-midi-chords % voicing) chords)))
