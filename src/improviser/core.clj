(ns improviser.core
  (:require [overtone.midi :as m]))

(map :description (m/midi-ports))

(def synth (m/midi-out "IAC"))

(defn midi-chord
  "Play a seq of notes with the corresponding velocities and durations."
  [out notes velocity duration]
  (map #(m/midi-note out % velocity duration 0) notes))

(def NOTES {:C  0  :c  0  :b# 0  :B# 0
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

(def CHORD
  (let [major7  #{0 4 7 11 14 17 21}
        dom7    #{0 4 7 10 14 18 21}
        minor7  #{0 3 7 10 14 17 21}
        rst     #{}]
    {:7sus2     #{0 2 7 10}
     :7sus4     #{0 5 7 10}
     :7-5       #{0 4 6 10}
     :m7-5      #{0 3 6 10}
     :7+5       #{0 4 8 10}
     :m7+5      #{0 3 8 10}
     :6*9       #{0 4 7 9 14}
     :m6*9      #{0 3 9 7 14}
     :rest      rst
     :major7    major7
     :dom7      dom7
     :7         dom7
     :M7        major7
     :minor7    minor7
     :m7        minor7}))


(def mid-C 60)
(def F3 53)
(def dom7 [0 4 7 10 14 18 21])
(def M7 [0 4 7 11 14 17 21])
(def c7 (map #(+ mid-C %) dom7))
(def fM7 (map #(+ F3 %) M7))

(midi-chord synth c7  70 1000)
(midi-chord synth fM7 70 1000)

;; (midi-chord synth {:c7 4, :fM7 4})
