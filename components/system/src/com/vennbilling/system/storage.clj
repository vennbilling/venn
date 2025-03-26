(ns com.vennbilling.system.storage
  (:require [integrant.core :as ig]
            [malli.core :as m]

            [com.vennbilling.logging.interface :as log]
            [com.vennbilling.database.interface :as database])

  (:import  [clojure.lang ExceptionInfo]))

;; Supported Storage Engines
(derive :storage.type/postgresql :db/connection)
(derive :storage.type/sqlite :db/connection)

(def ^:private storage-config
  {:system/storage (ig/refset :storage/type)})

(defn with-storage
  [storage]
  (merge-with into storage-config storage))

(defmethod ig/init-key :system/storage
  [_ stores]
  stores)

(def ^:private DBMigrationsConfig
  [:map
   [:directory :string]
   [:managed-connection :boolean]])

(def ^:private DBConfig
  [:map
   {:closed true}
   [:database-name :string]
   [:host {:optional true} :string]
   [:port {:optional true} :int]
   [:user {:optional true} :string]
   [:password {:optional true} :string]
   [:migrations DBMigrationsConfig]])

(defn- build-migrations-config
  "Returns a map that represents a configuration used by migratus.core"
  [datasource {:keys [directory managed-connection]}]
  {:store :database
   :migration-dir directory
   :db {:datasource datasource
        :managed-connection? managed-connection}})

(defn- bootstrap?
  "Determines if migrations should automatically be run.

  In SQLite, we limit one migration per table because of a lack of ALTER TABLE support.
  Migrations should automatically be run to ensure the latest DB state is accurate."
  [storage-type]
  (= storage-type :storage.type/sqlite))

(defmethod ig/init-key :db/connection
  [storage-type {:keys [migrations] :as db-config}]

  (try
    (let [valid-db-config (m/coerce DBConfig db-config)
          dbtype (name storage-type)
          db-connection-config (dissoc valid-db-config :migrations)
          ds (database/establish-connection dbtype db-connection-config)
          migratus-config (build-migrations-config ds migrations)]

      (when (bootstrap? storage-type)
        (database/bootstrap migratus-config))

      (log/info (str "Database " dbtype " is configured and ready to accept connections."))

      {:datasource ds :migrations-config migratus-config})
    (catch ExceptionInfo e
      (log/exception e)
      (throw (IllegalArgumentException. (ex-message e))))))

(defmethod ig/halt-key! :db/connection
  [storage-type db]
  (when-let [{:keys [datasource]} db]
    (log/info (str "Closing connection to " (name storage-type)))
    (.close datasource)))
