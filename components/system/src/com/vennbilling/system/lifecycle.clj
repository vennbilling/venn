(ns com.vennbilling.system.lifecycle
  (:require
    [integrant.core :as ig]))


(defonce  system (atom nil))

(defmethod ig/init-key :system/env, [_ env] env)


(defn- stop
  []
  (some-> (deref system) (ig/halt!)))


(defn start
  [banner settings]
  (when banner
    (println "\n" banner))
  (->> settings
       (ig/prep)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop)))
