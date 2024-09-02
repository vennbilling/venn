(ns com.vennbilling.system.core
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [com.vennbilling.logging.interface :as log]
    [com.vennbilling.system.http :as http])
  (:import
    (java.io
      FileNotFoundException)))


(def ^:private default-config (io/resource "system/default.edn"))


(defn- load-defaults
  []
  (aero/read-config default-config {:profile :dev}))


(defn- init
  [config-file profile]
  (try
    (let [config (aero/read-config config-file {:profile profile})]
      (merge config {}))
    (catch FileNotFoundException _
      ;; TODO: Log Exception
      (log/warn "invalid aero config. using default.")
      (load-defaults))
    (catch IllegalArgumentException _
      ;; TODO: Log Exception
      (log/warn "invalid aero config. using default.")
      (load-defaults))))


(defn create-http
  [config-file profile routes]

  (let [config (init config-file profile)
        settings (http/server-settings routes)
        system (merge-with into config settings)]
    system))
