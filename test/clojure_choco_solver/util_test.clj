(ns clojure-choco-solver.util-test
  (:require
   [clojure.test :refer [deftest is]]
   [clojure-choco-solver.util :as subject]))

(deftest assign-ids-test
  (is (= {0 "a" 
          1 "b" 
          2 "c" }
       (subject/assign-ids '("a" "b" "c"))))
  
  (is (= {0 "c"
          1 "b"
          2 "a"} 
         (subject/assign-ids '("c" "b" "a"))))
  
  
  )