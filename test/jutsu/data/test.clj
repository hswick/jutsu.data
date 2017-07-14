(ns jutsu.data.test
  (:require [jutsu.data.core :refer :all]))

(defn test-split []
  (let [label-index 4]
    (split-into-columns (csv->clj "iris.csv" false) 
      [:data (range 0 4) :labels 4])))

(defn test-etl []
  (csv->nd4j-array "iris.csv" 4 true))

(test-split)
(test-etl)

(println (clj->nd4j-array [[1 2 3 4]
                           [4 3 2 1]]))


