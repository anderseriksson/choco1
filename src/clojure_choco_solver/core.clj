(ns clojure-choco-solver.core
  (:require
   [clojure-choco-solver.util :refer [assign-ids column-from-java-array rpad]]
   [clojure.string :as str])
  (:require [clojure-choco-solver.input :refer [ledare patruller kalenderpass aktivitet ledar-aktivitet]])
  (:import
   [org.chocosolver.solver Model]
   [org.chocosolver.solver.constraints Constraint]
   [org.chocosolver.util.objects.setDataStructures.iterable IntIterableRangeSet]
   [org.chocosolver.solver.variables IntVar]))






(def ledare-nr (assign-ids ledare))

(defn print-patrullkalender
  "Prints the values of the patrullkalender matrix."
  [patrullkalenderaktivitet]
  (println "Patruller: " (str/join " " patruller))
  (doseq [i (range (alength patrullkalenderaktivitet))]
    (println (str "Pass: " (rpad (nth kalenderpass i) 20) )
     (mapv (fn [j]
             (.getValue ^org.chocosolver.solver.variables.IntVar (aget (aget patrullkalenderaktivitet i) j)))
           (range (alength (aget patrullkalenderaktivitet i)))))))

(defn print-ledarkalender
  "Prints the values of the ledarkalender matrix."
  [ledarkalender]
  (println "Ledare: " (str/join " " ledare))
  (doseq [i (range (alength ledarkalender))]
    (println (str "Pass: " (rpad (nth kalenderpass i) 20))
             (mapv (fn [j]
                     (.getValue ^org.chocosolver.solver.variables.IntVar (aget (aget ledarkalender i) j)))
                   (range (alength (aget ledarkalender i)))))))

(defn intvar-array-from-calendar-row
  "Returns a Java array of IntVar for the given row in the patrullkalenderaktivitet matrix."
  [patrullkalenderaktivitet row]
  (into-array IntVar (aget patrullkalenderaktivitet row)))

(defn intvar-array-from-calendar-column
  "Returns a Java array of IntVar for the given column in the patrullkalenderaktivitet matrix."
  [patrullkalenderaktivitet row]
  (into-array IntVar (column-from-java-array patrullkalenderaktivitet row)))

(let [model (Model. "Schema 0")
      patrullkalender
      (.intVarMatrix model "patrullkalender"
                     (count kalenderpass)
                     (count patruller)
                     0 (count aktivitet))
      ledarkalender
      (.intVarMatrix model "ledarkalender"
                     (count kalenderpass)
                     (count ledare)
                     0 (count ledar-aktivitet))


      c0 (.allDifferent model (intvar-array-from-calendar-row patrullkalender 0))
      c1 (.allDifferent model (intvar-array-from-calendar-row patrullkalender 1))
      c2 (.allDifferent model (intvar-array-from-calendar-row patrullkalender 2))
      c3 (.allDifferent model (intvar-array-from-calendar-column patrullkalender 0))
      c3b (.intValuePrecedeChain model (intvar-array-from-calendar-column patrullkalender 0) (into-array Integer/TYPE [0 1]))
      c4 (.allDifferent model (intvar-array-from-calendar-column patrullkalender 1))
      c4b (.intValuePrecedeChain model (intvar-array-from-calendar-column patrullkalender 1) (into-array Integer/TYPE [0 1]))
      c5 (.allDifferent model (intvar-array-from-calendar-column patrullkalender 2))
      c5b (.intValuePrecedeChain model (intvar-array-from-calendar-column patrullkalender 2) (into-array Integer/TYPE [0 1]))
      c6 (.allDifferent model (intvar-array-from-calendar-column patrullkalender 3))
      c6b (.intValuePrecedeChain model (intvar-array-from-calendar-column patrullkalender 3) (into-array Integer/TYPE [0 1]))

      ;; lc0 (.allDifferent model (intvar-array-from-calendar-row ledarkalender 0))
      ;; lc1 (.allDifferent model (intvar-array-from-calendar-row ledarkalender 1))
      ;; lc2 (.allDifferent model (intvar-array-from-calendar-row ledarkalender 2))

      lcx0 (.count model 0 (intvar-array-from-calendar-row ledarkalender 0) (.intVar model "constant 2" 2))
      lcx1 (.count model 0 (intvar-array-from-calendar-row ledarkalender 1) (.intVar model "constant 2" 2))
      lcx2 (.count model 0 (intvar-array-from-calendar-row ledarkalender 2) (.intVar model "constant 2" 2))
      lcx3 (.count model 5 (intvar-array-from-calendar-row ledarkalender 0) (.intVar model "constant 1" 1))
      lcx4 (.count model 5 (intvar-array-from-calendar-row ledarkalender 1) (.intVar model "constant 1" 1))
      lcx5 (.count model 5 (intvar-array-from-calendar-row ledarkalender 2) (.intVar model "constant 1" 1))
      ]
  (.post model (into-array Constraint [c0 c1 c2 c3 c3b c4 c4b c5 c5b  c6 c6b  lcx0 lcx1 lcx2 lcx3 lcx4 lcx5]))
  (println "constraints:" (.getCstrs model))
  (let [solver (.getSolver model)]
    (loop [i 0]
      (when (and (< i 100) (.solve solver))
        (println "patrull- och ledarkalender variant " i)
        (print-patrullkalender patrullkalender)
        (print-ledarkalender ledarkalender)
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


