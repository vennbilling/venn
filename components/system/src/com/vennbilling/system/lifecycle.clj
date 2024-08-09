(ns com.vennbilling.system.lifecycle
  (:require
    [clojure.java.io :as io]
    [com.vennbilling.system.config :as c]
    [integrant.core :as ig]))


(defonce system (atom nil))

(def ^:private default-config-file (io/resource "system/default.edn"))


(defn- stop
  []
  (some-> (deref system) (ig/halt!)))


(defn start
  [banner config-file opts]

  (when banner
    (println "\n" banner))

  (let [f (or config-file default-config-file)]
    (->> (c/read-config f opts)
         (ig/prep)
         (ig/init)
         (reset! system)))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop)))
