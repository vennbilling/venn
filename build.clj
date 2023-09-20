(ns build
  (:require [clojure.string :as string]
            [clojure.tools.build.api :as b]))

(def lib 'io.github.venn-billing/agent)
(def root-name (first (string/split (last (string/split (namespace lib) #"\.")) #"-")))
(def main-cls (string/join "." (filter some? [root-name (name lib) "core"])))
(def jdk-version (or (System/getenv "JDK_VERSION") "17"))
(def version "0.0.1-alpha-SNAPSHOT")
(def target-dir "target")
(def class-dir (str target-dir "/" "classes"))
(def uber-file (format "%s/%s-standalone.jar" target-dir (name lib)))
(def basis (b/create-basis {:project "deps.edn"}))

(defn- compile-clj [_]
  (println (format "Compiling Clojure with jdk %s..." jdk-version))
  (b/compile-clj {:basis basis
                  :src-dirs ["src/clj" "resources" "env/prod/clj"]
                  :class-dir class-dir}))

(defn clean
  [_]
  (println (str "Cleaning " target-dir))
  (b/delete {:path target-dir}))

(defn prep [_]
  (println "Writing pom.xml...")
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src/clj"]})
  (println "Copying source files...")
  (b/copy-dir {:src-dirs ["src/clj" "resources" "env/prod/clj"]
               :target-dir class-dir}))

(defn uber [_]
  (compile-clj _)
  (println (format "Making uberjar %s..." uber-file))
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :main main-cls
           :basis basis}))

(defn all [_]
  (do (clean nil) (prep nil) (uber nil)))

(defn build [_]
  (do (clean nil) (prep nil) (compile-clj nil)))
