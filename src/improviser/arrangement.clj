(ns improviser.arrangement
  (:use improviser.pitch)
  (:require [overtone.midi :as m]
            [clojure.math.combinatorics :as combo]))

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

(def NOTE-POSITION
              {:1   0
               :b9  1
               :9   2
               :#9  3
               :b3  3
               :3   4
               :11  5
               :#11 6
               :b5  6
               :5   7
               :#5  8
               :b13 8
               :13  9
               :b7  10
               :7   11})

(defn notes-to-position [notes]
  (sort (map #(% NOTE-POSITION) notes)))

(def CHORDS
  (let [major7 #{:1 :3 :5 :7 :9 :#11 :13}
        dom7   #{:1 :3 :5 :b7 :9 :#11 :13}
        hdim7  #{:1 :3 :b5 :b7 :9}
        minor7 #{:1 :b3 :5 :b7 :9 :11 :13}
        aug7   #{:1 :3 :#5 :b7 :9 :#11 :13}
        dim7   #{:1 :b3 :#11 :13}]
    {:M7         major7
     :m7         minor7
     :7          dom7
     :a7         aug7
     :d7         dim7
     :h7         hdim7}))

(def POSITION {:1  0
               :3  1
               :5  2
               :7  3
               :9  4
               :11 5
               :13 6})

(def VOICINGS {:basic  [:1 :3 :5 :7]
               :shell  [:3 :7]
               :ninth  [:3 :7 :9]
               :9+13th [:3 :5 :7 :9 :13]})

(defn voice-range [start end]
  (range (note start) (note end)))

(defn note-choice [note range]
  (map find-note-name (filter #(= (note NOTE) (rem % 12)) range)))

(defn find-notes [notes]
  (map #(REVERSE-NOTES (rem % 12)) notes))

(defn chord-tones [note chord]
  (map #(+ (note NOTE) (% NOTE-POSITION)) (chord CHORDS)))

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
  (map note (take-first (generate-voicings chord-key chord-quality :shell :F3 :G4))))

(defn midi-player [instrument chords voicing]
  (m/midi-play-song instrument (map #(generate-midi-chords % voicing) chords)))

(voicing-options [:E :Bb :A :D] (voice-range :E2 :C4))

(apply combo/cartesian-product (generate-voicings :C :7 :basic :E2 :C4))

(map #(apply - (sort > %)) (partition 2 1 (sort > (map note '(:Bb3 :G3 :C3 :E3)))))

(defn note-separation [chord]
  (map #(apply - (sort > %)) (partition 2 1 (sort > (map note chord)))))

(map note-separation (apply combo/cartesian-product (generate-voicings :C :7 :9+13th :E2 :G3)))

(reduce + (map #(apply - %) (partition 2 1 (sort > (map note '(:Bb3 :G3 :C3 :E3))))))

(defn total-separation [notes]
  (reduce + (map #(apply - %) (partition 2 1 (sort > (map note notes))))))

(map total-separation (apply combo/cartesian-product (generate-voicings :C :7 :9+13th :E2 :G3)))


