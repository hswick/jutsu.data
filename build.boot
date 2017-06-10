(def project 'hswick/jutsu.data)
(def version "0.0.1")

(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha15"]
                  [nightlight "1.6.4" :scope "test"]
                  [adzerk/boot-reload    "0.5.1"      :scope "test"]
                  [samestep/boot-refresh "0.1.0" :scope "test"]
                  [adzerk/bootlaces "0.1.13" :scope "test"]
                  [org.datavec/datavec-api "0.8.0"]
                  [org.deeplearning4j/deeplearning4j-core "0.8.0"]])

(task-options! pom {:project project
                    :version version
                    :description "Data ETL for the jutsu data science framework"
                    :url "https://github.com/hswick/jutsu.data"
                    :scm {:url "https://github.com/hswick/jutsu.data"}
                    :license {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}}
               aot {:namespace '#{jutsu.data.core}}
               sift {:include #{#"\.jar$"}})

(require
  '[nightlight.boot :refer [nightlight]]
  '[adzerk.boot-reload    :refer [reload]]
  '[samestep.boot-refresh :refer [refresh]]
  '[adzerk.bootlaces :refer [build-jar]]
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
