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
                  [nightlight "1.7.0" :scope "test"]]
  :repositories (conj (get-env :repositories)
                  ["clojars" {:url "https://clojars.org/repo"
                              :username (System/getenv "CLOJARS_USER")
                              :password (System/getenv "CLOJARS_PASS")}]))

(task-options!
  aot {:namespace '#{jutsu.data.core}}
  jar {:main 'jutsu.data.core
       :file "jutsu_data.jar"
       :manifest {"Description" "Data interaction component of the jutsu data science framework"}}
  pom {:project 'hswick/jutsu.data
       :version "0.0.1"
       :description "Data ETL for the jutsu data science framework"
       :url "https://github.com/hswick/jutsu.data"
       :scm {:url "https://github.com/hswick/jutsu.data"}
       :license {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}}
  push {:repo "clojars"})

(require
  '[nightlight.boot :refer [nightlight]]
  '[adzerk.boot-reload    :refer [reload]]
  '[samestep.boot-refresh :refer [refresh]]
  '[adzerk.boot-test :refer :all]
  'jutsu.data.core)

(deftask deploy []
  (comp
    (pom)
    (jar)
    (push)))

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
