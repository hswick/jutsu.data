(def project 'hswick/jutsu.data)
(def version "0.0.1")

(set-env!
  :resource-paths #{"resources" "src"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha15"]
                  [nightlight "1.6.5" :scope "test"]
                  [adzerk/boot-reload    "0.5.1"      :scope "test"]
                  [samestep/boot-refresh "0.1.0" :scope "test"]
                  [org.datavec/datavec-api "0.8.0"]
                  [org.deeplearning4j/deeplearning4j-core "0.8.0"]
                  [org.nd4j/nd4j-native "0.8.0" :scope "test"]
                  [org.nd4j/nd4j-api "0.8.0"]
                  [cheshire "5.7.1"]])

(task-options! pom {:project project
                    :version version
                    :description "Data ETL for the jutsu data science framework"
                    :url "https://github.com/hswick/jutsu.data"
                    :scm {:url "https://github.com/hswick/jutsu.data"}
                    :license {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}}
              sift {:include #{#"\.jar$"}})

(require
  '[nightlight.boot :refer [nightlight]]
  '[adzerk.boot-reload    :refer [reload]]
  '[samestep.boot-refresh :refer [refresh]])

(deftask build
  "Build and install the project locally"
  []
  (comp
    (pom)
    (jar)
    (install)))

(deftask dev []
  (comp
    (watch)
    (reload)
    (refresh)
    (repl
      :server true)))

(deftask repl-client []
  (repl :client true))

(deftask night []
  (comp
    (wait)
    (nightlight :port 4000)))
