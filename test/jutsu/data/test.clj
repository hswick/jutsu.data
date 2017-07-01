(ns jutsu.data.test
  (:require [jutsu.core :as j]
            [jutsu.data.core :refer :all]))

(defn test-covar []
   (let [data (->> (csv->clj "iris.csv" true)
                  rest
                  (map #(take 4 %))
                  (map strings->floats)
                  clj->nd4j
                  (pca 2))]
     data))

(defn test-split []
  (let [label-index 4]
    (split-into-columns (csv->clj "iris.csv" false) 
      [:data (range 0 4) :labels 4])))

(defn test-iris-3 []
  (-> (csv->clj "iris.csv" false)
      (split-into-columns [:data (range 0 4) :labels 4])
      (update :data #(->> (map strings->floats %)
                          clj->nd4j
                          normalize-zero
                          (pca 2)))
      (partition-by-column :labels)))

(defn test-etl []
  (csv->nd4j-array "iris.csv" 4 true))

(test-split)
(test-etl)
(println (test-iris-3))
