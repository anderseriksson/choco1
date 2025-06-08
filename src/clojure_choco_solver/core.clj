(ns clojure-choco-solver.core
  (:require
   [clojure-choco-solver.util :refer [assign-ids column-from-java-array rpad]]
   [clojure.string :as str])
  (:require [clojure-choco-solver.input :refer [ledare patruller kalenderpass aktivitet ledar-aktivitet]])
  (:import
   [org.chocosolver.solver Model]
   [org.chocosolver.solver.constraints Constraint]
   [org.chocosolver.solver.variables IntVar]))






(def ledare-nr (assign-ids ledare))

(defn print-patrullkalender
  "Prints the values of the patrullkalenderaktivitet matrix."
  [patrullkalenderaktivitet]
  (println "Patruller: " (str/join " " patruller))
  (doseq [i (range (alength patrullkalenderaktivitet))]
    (println (str "Pass: " (rpad (nth kalenderpass i) 20) )
     (mapv (fn [j]
             (.getValue ^org.chocosolver.solver.variables.IntVar (aget (aget patrullkalenderaktivitet i) j)))
           (range (alength (aget patrullkalenderaktivitet i)))))))
; Print the values of the patrullkalenderaktivitet matrix

(let [model (Model. "Schema 0")
      patrullkalenderaktivitet
      (.intVarMatrix model "patrullkalender"
                     (count kalenderpass)
                     (count patruller)
                     0 (count aktivitet))
      c0 (.allDifferent model (into-array IntVar (aget patrullkalenderaktivitet 0)))
      c1 (.allDifferent model (into-array IntVar (aget patrullkalenderaktivitet 1)))
      c2 (.allDifferent model (into-array IntVar (aget patrullkalenderaktivitet 2)))
      c3 (.allDifferent model (into-array IntVar  (column-from-java-array patrullkalenderaktivitet 0)))
      c3b (.intValuePrecedeChain model (into-array IntVar  (column-from-java-array patrullkalenderaktivitet 0)) (into-array Integer/TYPE [0 1]))
      c4 (.allDifferent model (into-array IntVar  (column-from-java-array patrullkalenderaktivitet 1)))
      c4b (.intValuePrecedeChain model (into-array IntVar  (column-from-java-array patrullkalenderaktivitet 1)) (into-array Integer/TYPE [0 1]))
      c5 (.allDifferent model (into-array IntVar  (column-from-java-array patrullkalenderaktivitet 2)))
      c5b (.intValuePrecedeChain model (into-array IntVar  (column-from-java-array patrullkalenderaktivitet 2)) (into-array Integer/TYPE [0 1]))
      c6 (.allDifferent model (into-array IntVar  (column-from-java-array patrullkalenderaktivitet 3)))
      c6b (.intValuePrecedeChain model (into-array IntVar  (column-from-java-array patrullkalenderaktivitet 3)) (into-array Integer/TYPE [0 1]))
      
      
      ]
  (.post model (into-array Constraint [c0 c1 c2 c3 c3b c4 c4b c5 c5b  c6 c6b]))
  (println "constraints:" (.getCstrs model))
  (let [solver (.getSolver model)]
    (loop [i 0]
      (when (and (< i 100) (.solve solver))
        (println "patrullkalender variant " i )
        (print-patrullkalender patrullkalenderaktivitet)
        (recur (inc i))))
    (println "No more solutions or limit reached.")))




(defn -main
  "Main function to run the Choco solver example."
  [& args]
  (println "Running Choco solver example...")
  ())

(defn -mainx
  "Demo: Solve x + y = 5 with x, y in 0..5"
  [& args]
  (let [model (Model. "Simple Addition")
        x (.intVar model "x" 0 5)
        y (.intVar model "y" 0 5) 
        a (.arithm model x "+" y "=" 5)]
    
    (.post model (into-array Constraint [a]))
    (let [solver (.getSolver model)]
      (while (.solve solver)
        (println "x =" (.getValue x) ", y =" (.getValue y))))))


;; To run: clojure -M -m clojure-choco-solver.core

;; https://javadoc.io/doc/org.choco-solver/choco-solver/4.10.18/org.chocosolver.solver/org/chocosolver/solver/Model.html


