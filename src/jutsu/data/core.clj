(ns jutsu.data.core
  (:require [cheshire.core :refer [parse-string generate-string]])
  (:import [org.datavec.api.util ClassPathResource]
           [org.datavec.api.records.reader.impl.csv CSVRecordReader]
           [org.datavec.api.split FileSplit]
           [org.deeplearning4j.datasets.datavec RecordReaderDataSetIterator]
           [org.nd4j.linalg.factory Nd4j]
           [org.nd4j.linalg.ops.transforms Transforms]
           [org.nd4j.linalg.api.ndarray INDArray]))

;;Thinking about making this a multimethod or part of a protocol
;;Should move this into a dif namespace like iterator
(defn jutsu-nth [coll index]
  (if (instance? INDArray coll)
    (let [shape (.shape coll)]
      (if (> (first shape) 1)
        (.getRow coll index)
        (.getDouble coll 0 index)))
    (nth coll index)))

;;Define schema and pipe values into different streams (columns)
;;Schema should be a vector of ranges or values
;;
(defn split-into-columns [rows schema]
  (into {} (map
             (fn [[column-name column-spec]]
               [column-name (if (seq? column-spec)
                              (map (fn [row] (map #(jutsu-nth row %) column-spec)) rows)
                              (map #(jutsu-nth % column-spec) rows))])
             (partition 2 schema))))

;;Split or partial datasets are more important to programmers than the whole dataset
(defn partition-by-column
  ([dataset column] (partition-by-column dataset column (keys dataset)))
  ([dataset column key-list]
   (let [indexed-elements (map-indexed (fn [id item] [item id]) (get dataset column))
         split-indexed (partition-by first indexed-elements)]
     (map (fn [grouping]
           (into {} (map (fn [k] (if (= column k)
                                   [k (map first grouping)]
                                   [k (map #(jutsu-nth (get dataset k) (second %)) grouping)]))
                      key-list)))
      split-indexed))))

(defn sort-by-column
  ([dataset column] (partition-by-column dataset column (keys dataset)))
  ([dataset column key-list]
   (let [indexed-elements (map-indexed (fn [id item] [item id]) (get dataset column))
         split-indexed (sort-by first indexed-elements)]
     (map (fn [grouping]
           (into {} (map (fn [k] (if (= column k)
                                   [k (map first grouping)]
                                   [k (map #(jutsu-nth (get dataset k) (second %)) grouping)]))
                      key-list)))
      split-indexed))))

(defn clj->json [data] (generate-string data))

(defn json->clj [json-string] (parse-string json-string))

(defn absolute-path [filename]
  (-> (ClassPathResource. filename)
      (.getFile)
      (.getAbsolutePath)))

;;Can only iterate over once
(defn csv-reader [filename];;default args
  ;num lines skip, delimiter;
  (let [reader (CSVRecordReader. 0 ",")]
    (.initialize reader (FileSplit. (clojure.java.io/file (absolute-path filename))))
    reader))

;;Options to string json (array of arrays)
;;Should accept an argument about how to deal with next
(defn till-no-next [nextable]
  (loop [data []]
    (if (not (.hasNext nextable))
      data
      (recur (conj data
               (seq (.next nextable)))))))
       
;;Gets records of strings
(defn line-records [data]
  (map (fn [row] (map #(String. (.getBytes %)) row)) data))

;;Returns a lazyseq of edn data
;;Assumes there is a header
;:Returns a map of header and data
(defn csv->clj [filename header]
  (-> (csv-reader filename)
      (till-no-next)
      (line-records)
      ((fn [csv-data]
         (if header csv-data (rest csv-data))))))

(defn rows [coll]
  (if (instance? INDArray coll)
    (first (.shape coll))
    (if (coll? (first coll)) (count coll) 1)))

(defn cols [coll]
  (if (instance? INDArray coll)
    (second (.shape coll))
    (if (coll? (first coll)) (count (first coll)) (count coll))))

;;Stacks a collection of ndarrays vertically (by row)
(defn vstack-arrays [ndarrays]
  (let [shape (.shape (first ndarrays))
        new-array (Nd4j/create (count ndarrays) (second shape))]
    (doseq [n (range 0 (count ndarrays))]
      (.putRow new-array n (nth ndarrays n)))
    new-array))

;;Stacks a collection of ndarrays horizontally
(defn hstack-arrays [ndarrays]
  (let [shape (.shape (first ndarrays))
        new-array (Nd4j/create (first shape) (count ndarrays))]
    (doseq [n (range 0 (count ndarrays))]
      (.putColumn new-array n (nth ndarrays n)))
    new-array))

(defn nd4j->clj [nd4j-array]
  (if (= 1 (first (.shape nd4j-array)))
    (into [] nd4j-array)
    (map #(into [] %) nd4j-array)))

;;Add good exceptions to this
(defn strings->floats [string-seq]
  (map #(Float/parseFloat %) string-seq))

;;Currently supports 1 and 2d arrays
(defn clj->nd4j-array [coll]
  (let [h (rows coll) w (cols coll)]
    (if (= h 1)
      (Nd4j/create (float-array (seq coll)))
      (let [new-array (Nd4j/create h w)]
        (doseq [i (range 0 h)]
          (.putRow new-array i (Nd4j/create (float-array (nth coll i)))))
        new-array))))

;;Best you can get is an iterator without lazy eval
;;This is eager
(defn csv->nd4j-array [filename label-index header]
  (->> (csv-reader filename)
       (till-no-next)
       (map #(take label-index %))
       ((fn [data]
         (if header (rest data) data)))
       (map (fn [row]
              (map #(.toDouble %) row)))
       clj->nd4j-array))

(defn get-double-from-row [ndarray row-index el-index]
  (.getDouble (.getRow ndarray row-index) 0 el-index))
