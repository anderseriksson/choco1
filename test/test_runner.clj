(ns test-runner
  (:require [clojure.test :refer [run-all-tests]]))

(defn -main []
  (run-all-tests #"clojure-choco-solver\..*"))