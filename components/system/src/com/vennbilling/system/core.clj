(ns com.vennbilling.system.core
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [com.vennbilling.logging.interface :as log]
    [com.vennbilling.system.http]
    [integrant.core :as ig])
  (:import
    (java.io
      FileNotFoundException)))


(def ^:private default-config (io/resource "system/default.edn"))
(def ^:private defaults (aero/read-config default-config {:profile :dev}))


(def ^:private system-settings
  {:http/server
   {:handler (ig/ref :http/handler)
    :db (ig/refset :db/server)}

   :http/handler
   {:router (ig/ref :http/router)}

   :http/router
   {:routes []}

   :db.server/local {}
   :db.server/remote {}})


(derive :db.server/local :db/server)
(derive :db.server/mysql :db/server)


(defmethod ig/init-key :db.server/local
  [_ opts]
  {:sqlite opts})


(defmethod ig/init-key :db.server/remote
  [_ opts]
  {:mysql opts})


(defn- read-config
  [config-file profile]
  (try
    (let [config (aero/read-config config-file {:profile profile})]
      (merge config {}))
    (catch FileNotFoundException _
      ;; TODO: Log Exception
      (log/warn "invalid aero config. using default.")
      defaults)
    (catch IllegalArgumentException _
      ;; TODO: Log Exception
      (log/warn "invalid aero config. using default.")
      defaults)))


(defn init
  [config-file profile routes]

  (let [config (read-config config-file profile)
        router {:http/router {:routes routes}}
        system (merge-with into config system-settings router)]
    system))
