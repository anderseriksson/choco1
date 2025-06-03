(ns clojure-choco-solver.core
  (:import [org.chocosolver.solver Model]
           [org.chocosolver.solver.constraints Constraint]))

(defn -main
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


