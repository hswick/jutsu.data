(set-env!
  :source-paths #{"src"}
  :resource-paths #{"data"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha15"]
                  [nightlight "1.6.4" :scope "test"]
                  [org.datavec/datavec-api "0.8.0"]
                  [org.deeplearning4j/deeplearning4j-core "0.8.0"]])

(require
  '[nightlight.boot :refer [nightlight]]
  'jutsu.data.core)

(deftask dev []
  (repl :init-ns 'jutsu.data.core))

(deftask night []
  (comp
    (wait)
    (nightlight :port 4000)))
