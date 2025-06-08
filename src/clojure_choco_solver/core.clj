(ns clojure-choco-solver.core
  (:require
   [clojure-choco-solver.util :refer [assign-ids column-from-java-array rpad]]
   [clojure.string :as str]
   [malli.core :as m])
  (:require [clojure-choco-solver.input :refer [ledare patruller kalenderpass aktivitet ledar-aktivitet]])
  (:import
   [org.chocosolver.solver Model]
   [org.chocosolver.solver.constraints Constraint]
   [org.chocosolver.util.objects.setDataStructures.iterable IntIterableRangeSet]
   [org.chocosolver.solver.variables IntVar]))

(def ledare-nr (assign-ids ledare))


(defn ledare-name 
  "Returns the name of the ledare given an ID."
  [id]
  (ledare-nr id))

(defn ledare-id
  "Returns the ID of the ledare given a name."
  [name]
  (some (fn [[k v]] (when (= v name) k)) ledare-nr))

(def aktivitet-nr (assign-ids aktivitet))
(defn aktivitet-name
  "Returns the name of the aktivitet given an ID."
  [id]
  (aktivitet-nr id))
(defn aktivitet-id
  "Returns the ID of the aktivitet given a name."
  [name]
  (some (fn [[k v]] (when (= v name) k)) aktivitet-nr))

(defn print-patrullkalender2
  "Prints the values of the patrullkalender matrix, looking up activity names in the aktivitet list."
  [patrullkalender patruller aktivitet]
  (println "Patruller:                  " (str/join "        " patruller))
  (let [aktiviteter (vec aktivitet)] ; ensure indexable
    (doseq [i (range (alength patrullkalender))]
      (println
        (str "Pass: " (rpad (nth kalenderpass i) 20)
             (str/join "  "
               (map (fn [j]
                      (let [idx (.getValue ^org.chocosolver.solver.variables.IntVar (aget (aget patrullkalender i) j))]
                        (if (and (>= idx 0) (< idx (count aktiviteter)))
                          (rpad (str (nth aktiviteter idx) " (" idx ")") 20)
                          (rpad (str idx) 20))))
                    (range (alength (aget patrullkalender i))))))))))

(defn print-ledarkalender2
  "Prints the values of the ledarkalender matrix, looking up activity names in the aktivitet list."
  [ledarkalender ledare ledar-aktivitet]
  (println "Ledare:                    " (str/join "               " ledare))
  (let [aktiviteter (vec ledar-aktivitet)] ; ensure indexable
    (doseq [i (range (alength ledarkalender))]
      (println
       (str "Pass: " (rpad (nth kalenderpass i) 20)
            (str/join "  "
                      (map (fn [j]
                             (let [idx (.getValue ^org.chocosolver.solver.variables.IntVar (aget (aget ledarkalender i) j))]
                               (if (and (>= idx 0) (< idx (count aktiviteter)))
                                 (rpad (str (nth aktiviteter idx) " (" idx ")") 20)
                                 (rpad (str idx) 20))))
                           (range (alength (aget ledarkalender i))))))))))


(defn intvar-array-from-calendar-row
  "Returns a Java array of IntVar for a full row in the calender matrix."
  [calendar row]
  (into-array IntVar (aget calendar row)))


(defn intvars-of-parts-of-calendar-row
  "Returns a Java array of IntVar for parts of a row in the calender matrix. Parts is a sequence of indices."
  [calendar row parts]
  (m/validate [:seqable :int] parts)
  (into-array IntVar (map #(nth (intvar-array-from-calendar-row calendar row) %) parts)))

(defn intvar-array-from-calendar-column
  "Returns a Java array of IntVar for the complete column in the calendar matrix."
  [patrullkalenderaktivitet row]
  (into-array IntVar (column-from-java-array patrullkalenderaktivitet row)))

(defn constraints-all-different-during-a-calendar-slot
  "Returns a sequence of .allDifferent constraints of all rows of the given matrix."
  [model matrix]
  (map #( .allDifferent model (intvar-array-from-calendar-row matrix %))
       (range (count matrix) )))

(defn constraints-all-different-for-every-patrol-throughout-the-calendar
  "Returns a sequence of .allDifferent constraints for each patrol throughout the calendar."
  [model matrix]
  (map #(.allDifferent model (intvar-array-from-calendar-column matrix %))
       (range (count (nth matrix 0)))))

(defn constraints-sequence-for-every-patrol-throughout-the-calendar
  "Returns a sequence of sequence constraints where first must come before following for each patrol throughout the calendar."
  [model matrix first following]
  (map #(.intValuePrecedeChain model (intvar-array-from-calendar-column matrix %) (into-array Integer/TYPE [first following]))
       (range (count (nth matrix 0)))))

(defn specific-leaders
  "Returns a constraint that ensures specific leaders are assigned to specific activities in the calendars."
  [model activity-name patrolcalendar leadercalendar leader-ids]
  (.or model (into-array Constraint
                                             [(.count model (aktivitet-id activity-name) 
                                                      (intvar-array-from-calendar-row patrolcalendar 0) (.intVar model "constant 0" 0))
                                              (.count model (aktivitet-id activity-name) 
                                                      (intvars-of-parts-of-calendar-row leadercalendar 0 leader-ids) (.intVar model "constant 1" 1))])))


(let [model (Model. "Schema 0")
      patrullkalender
      (.intVarMatrix model "patrullkalender"
                     (count kalenderpass)
                     (count patruller)
                     0 (- (count aktivitet) 1))
      ledarkalender
      (.intVarMatrix model "ledarkalender"
                     (count kalenderpass)
                     (count ledare)
                     0 (- (count ledar-aktivitet) 1))


      c012 (constraints-all-different-during-a-calendar-slot model patrullkalender)
      c3456 (constraints-all-different-for-every-patrol-throughout-the-calendar model patrullkalender)
      c3456b (constraints-sequence-for-every-patrol-throughout-the-calendar model patrullkalender 
                                                                            (aktivitet-id "Segla 2-krona 1") 
                                                                            (aktivitet-id "Segla 2-krona 2"))
      
      c3456c (constraints-sequence-for-every-patrol-throughout-the-calendar model patrullkalender
                                                                            (aktivitet-id "Kanot 1")
                                                                            (aktivitet-id "Kanot 2"))


      ;; lc0 (.allDifferent model (intvar-array-from-calendar-row ledarkalender 0))
      ;; lc1 (.allDifferent model (intvar-array-from-calendar-row ledarkalender 1))
      ;; lc2 (.allDifferent model (intvar-array-from-calendar-row ledarkalender 2))
      
      lcx0 (.count model 0 (intvar-array-from-calendar-row ledarkalender 0) (.intVar model "constant 2" 2))
      lcx1 (.count model 0 (intvar-array-from-calendar-row ledarkalender 1) (.intVar model "constant 2" 2))
      lcx2 (.count model 0 (intvar-array-from-calendar-row ledarkalender 2) (.intVar model "constant 2" 2))
      lcx3 (.count model 1 (intvar-array-from-calendar-row ledarkalender 0) (.intVar model "constant 1" 1))
      lcx4 (.count model 1 (intvar-array-from-calendar-row ledarkalender 1) (.intVar model "constant 1" 1))
      lcx5 (.count model 1 (intvar-array-from-calendar-row ledarkalender 2) (.intVar model "constant 1" 1))
      lcx6 (.count model 2 (intvar-array-from-calendar-row ledarkalender 0) (.intVar model "constant 2" 2))
      lcx7 (.count model 2 (intvar-array-from-calendar-row ledarkalender 1) (.intVar model "constant 2" 2))
      lcx8 (.count model 2 (intvar-array-from-calendar-row ledarkalender 2) (.intVar model "constant 2" 2))

      

      specific-leader-kanot (specific-leaders model "Kanot 1"  patrullkalender ledarkalender[(ledare-id "Jonas S") (ledare-id "Stuart")] ) 
      specific-leader-segla (specific-leaders model "Segla 2-krona 1"  patrullkalender ledarkalender[(ledare-id "Jonas S") (ledare-id "Jonas N")] ) 
      specific-leader-segla (specific-leaders model "Segla 2-krona 2"  patrullkalender ledarkalender[(ledare-id "Jonas S") (ledare-id "Jonas N")] ) 
      
      
      constraints (flatten [;; Patrullkalender constraints
                            c012
                            c3456
                            c3456b
                            c3456c


                            ;; Ledarkalender constraints
                            lcx0 lcx1 lcx2 lcx3 lcx4 lcx5 lcx6 lcx7 lcx8
                            specific-leader-kanot
                            specific-leader-segla
                            ])]






  (doseq [c constraints]
    (.post model (into-array Constraint [c])))

  (println "constraints count in model:" (count (.getCstrs model)))

  (let [solver (.getSolver model)]
    (loop [i 0]
      (when (and (< i 2) (.solve solver))
        (println)
        (println)
        (println "patrull- och ledarkalender variant " i)
        (println)
        (print-patrullkalender2 patrullkalender patruller aktivitet)
        (println)
        (print-ledarkalender2 ledarkalender ledare ledar-aktivitet)
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


