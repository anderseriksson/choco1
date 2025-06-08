(ns clojure-choco-solver.core-test
  (:require
   [clojure.test :refer [deftest is]]
   [clojure-choco-solver.core :as subject]))

(deftest ledare-name-test
  (is (= "Anders"
         (subject/ledare-name 0))))