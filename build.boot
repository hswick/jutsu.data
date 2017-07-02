(def project 'hswick/jutsu.data)
(def version "0.0.1")
(set-env!
  :resource-paths #{"resources" "src"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [adzerk/boot-reload    "0.5.1"      :scope "test"]
                  [samestep/boot-refresh "0.1.0" :scope "test"]
                  [adzerk/boot-test "1.2.0" :scope "test"]
                  [org.nd4j/nd4j-native-platform "0.8.0" :scope "test"]
                  [org.datavec/datavec-api "0.8.0"]
                  [org.deeplearning4j/deeplearning4j-core "0.8.0"]
                  [org.nd4j/nd4j-api "0.8.0"]
                  [cheshire "5.7.1"]
                  [hswick/jutsu "0.0.1"]
                  [nightlight "1.7.0" :scope "test"]])

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
  '[samestep.boot-refresh :refer [refresh]]
  '[adzerk.boot-test :refer :all]
  'jutsu.data.core)

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
      :server true
      :init-ns 'jutsu.data.core)))

(deftask repl-client []
  (repl :client true))

(deftask night []
  (comp
    (wait)
    (nightlight :port 4000)))

(deftask testing [] (merge-env! :source-paths #{"test"}) identity)

(deftask test-jutsu []
  (comp
    (testing)
    (test)))
