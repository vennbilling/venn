(ns build
  (:require
    [clojure.java.io :as io]
    [clojure.tools.build.api :as b]
    [clojure.tools.deps :as t]
    [clojure.tools.deps.util.dir :refer [with-dir]]))


(def default-version "0.0.0-alpha-SNAPSHOT")
(def target-dir "target")
(def class-dir (str target-dir "/" "classes"))

(def jdk-version (or (System/getenv "JDK_VERSION") "17"))


(defn- get-project-aliases
  []
  (let [edn-fn (juxt :root-edn :project-edn)]
    (-> (t/find-edn-maps)
        (edn-fn)
        (t/merge-edns)
        :aliases)))


(defn- ensure-project-root
  "Given a task name and a project name, ensure the project
   exists and seems valid, and return the absolute path to it."
  [task project]
  (let [project-root (str (System/getProperty "user.dir") "/projects/" project)]
    (when-not (and project
                   (.exists (io/file project-root))
                   (.exists (io/file (str project-root "/deps.edn"))))
      (throw (ex-info (str task " task requires a valid :project option") {:project project})))
    project-root))


(defn uberjar
  [{:keys [project uber-file] :as opts}]
  (let [project-root (ensure-project-root "uberjar" project)
        aliases (with-dir (io/file project-root) (get-project-aliases))
        main (-> aliases :uberjar :main)
        version (-> aliases :uberjar :version)]

    (when-not main
      (throw (ex-info (str "the " project " project's deps.edn file does not specify the :main namespace in its :uberjar alias")
                      {:aliases aliases})))

    (when-not version
      (println (str "*Warning*. The " project " project's deps.edn file does not specify a :version. The default of :version is being used.")
               {:version default-version}))

    (b/with-project-root project-root
                         (let [version (or version default-version)
                               uber-file (or uber-file
                                             (-> aliases :uberjar :uber-file)
                                             (str target-dir "-" project "-" version ".jar"))

                               opts (merge opts
                                           {:basis (b/create-basis)
                                            :class-dir class-dir
                                            :compile-opts {:direct-linking true}
                                            :main main
                                            :ns-compile [main]
                                            :uber-file uber-file})]

                           (b/delete {:path class-dir})
                           ;; no src or resources to copy
                           (println "\nCompiling" (str main "..."))
                           (b/compile-clj opts)
                           (println "Building uberjar" (str uber-file "..."))
                           (b/uber opts)
                           (b/delete {:path class-dir})
                           (println "Uberjar is built.")
                           opts))))
