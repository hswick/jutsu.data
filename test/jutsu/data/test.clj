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

; (defn test-iris-3 []
;   (-> (csv->clj "iris.csv" false)
;       (split-into-columns [:data (range 0 4) :labels 4])
;       (update :data #(->> (map strings->floats %)
;                           clj->nd4j
;                           normalize-zero
;                           (pca 2)))
;       (partition-by-column :labels)))

(defn test-etl []
  (csv->nd4j-array "iris.csv" 4 true))

(test-split)
(test-etl)

; (j/start-jutsu!)
; (Thread/sleep 3000)
; (let [iris-data (test-iris-3)]
;   (j/graph! "test-pca" (map (fn [specie]
;                               {:x (map #(jutsu-nth % 0) (:data specie))
;                                :y (map #(jutsu-nth % 1) (:data specie))
;                                :text (:labels specie)
;                                :mode "markers"
;                                :type "scatter"})    
;                          iris-data)))
;(Thread/sleep 3000)

(println (clj->nd4j-array [[1 2 3 4]
                           [4 3 2 1]]))


