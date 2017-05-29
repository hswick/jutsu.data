(set-env!
  :source-paths #{"src"}
  :resource-paths #{"data"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha15"]
                  [nightlight "1.6.4" :scope "test"]
                  [adzerk/boot-reload    "0.5.1"      :scope "test"]
                  [samestep/boot-refresh "0.1.0" :scope "test"]
                  [org.datavec/datavec-api "0.8.0"]
                  [org.deeplearning4j/deeplearning4j-core "0.8.0"]])

(require
  '[nightlight.boot :refer [nightlight]]
  '[adzerk.boot-reload    :refer [reload]]
  '[samestep.boot-refresh :refer [refresh]]
  'jutsu.data.core)

(deftask dev []
  (comp
    (watch)
    (reload)
    (refresh)
    (repl
      :server true
      :init-ns 'jutsu.data.core)))

(deftask repl-client []
  (repl :client true))

(deftask night []
  (comp
    (wait)
    (nightlight :port 4000)))
