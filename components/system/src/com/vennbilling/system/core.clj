(ns com.vennbilling.system.core
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [com.vennbilling.logging.interface :as log]
   [com.vennbilling.system.db]
   [com.vennbilling.system.http]
   [integrant.core :as ig])
  (:import
   (java.io
    FileNotFoundException)))

(def ^:private default-config (io/resource "system/default.edn"))
(def ^:private defaults (aero/read-config default-config {:profile :dev}))

(def ^:private ig-settings
  "Settings that will leverage integrant.core on system start. Used to also configure system dependencies."
  {:http/server {:handler (ig/ref :http/handler)
                 :db (ig/ref :db/server)}

   :http/handler
   {:router (ig/ref :http/router)}})

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

  ;; TODO: We shouldn't assume a system wants a :http/router
  ;; Potentially do a "with-http" wrapper
  (let [config (read-config config-file profile)
        router {:http/router {:routes routes}}
        system (merge-with into config ig-settings router)]
    system))
