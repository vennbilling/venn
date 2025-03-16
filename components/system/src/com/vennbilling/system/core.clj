(ns com.vennbilling.system.core
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [malli.core :as m]

   [com.vennbilling.logging.interface :as log]
   [com.vennbilling.system.storage :as storage]
   [com.vennbilling.system.server :as server])
  (:import
   (java.io
    FileNotFoundException)
   (clojure.lang ExceptionInfo)))

(def ^:private VennSystem
  "A representation of what a system in Venn should look like"
  [:map
   {:closed true}
   [:system/env :keyword]
   [:system/server :map]
   [:system/storage :map]])

(def ^:private default-config (io/resource "system/default.edn"))
(def ^:private defaults (m/coerce VennSystem (aero/read-config default-config {:profile :unknown})))

(defn- read-aero-config
  "
  Reads an aero config and returns config that is unversal to all bases.

  A map with the follow shape will always be returned with values overrided by
  the aero config.

  ```clojure
    {:system/env {}
     :system/server {}
     :system/storage {}}
  ```

  These top level keys can be initialized in a integrant system using using `integrant.core/init-key`. You must
  ensure initializers exist for all keys returned in this map.

  The returned system keys' values represent the following:

  - `:system/env` - the current environment for the system
  - `:system/server` - the backing web server, if applicable
  - `:system/storage` - any storage mechanisms for this server such as DB or filesystem
  "
  [config profile]
  (try
    (let [c (aero/read-config config {:profile profile})
          valid (m/coerce VennSystem c)]
      (merge defaults valid))
    (catch FileNotFoundException _
      ;; TODO: Log Exception
      (log/warn "invalid aero config. using default.")
      defaults)
    (catch IllegalArgumentException _
      ;; TODO: Log Exception
      (log/warn "invalid aero config. using default.")
      defaults)
    (catch ExceptionInfo e
      (log/exception e)
      (throw e))))

(defn init
  "Generates a system configuration that Integrant can consume"
  [config-file profile routes]

  ;; TODO we should conditionally call these "with-" functions based on the presence of
  ;; a system key. Right now, we assume every system wants an http server, database, etc
  (let [base (read-aero-config config-file profile)
        server-config (server/with-http-server routes)
        storage-config (storage/with-storage (base :system/storage))
        system (merge-with into base server-config storage-config)]
    system))
