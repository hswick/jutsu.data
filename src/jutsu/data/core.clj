(ns jutsu.data.core
  (:import [org.datavec.api.util ClassPathResource]
           [org.datavec.api.records.reader.impl.csv CSVRecordReader]
           [org.datavec.api.split FileSplit]
           [org.deeplearning4j.datasets.datavec RecordReaderDataSetIterator]))

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
(defn csv->edn [filename]
  (-> (csv-reader filename)
      (till-no-next)
      (line-records)))
                     
