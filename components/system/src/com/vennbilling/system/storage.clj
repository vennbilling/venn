(ns com.vennbilling.system.storage
  (:require [integrant.core :as ig]
            [malli.core :as m]

            [com.vennbilling.logging.interface :as log]
            [com.vennbilling.database.interface :as database])

  (:import  [clojure.lang ExceptionInfo]))

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

(derive :system.storage/postgresql :db/connection)
(derive :system.storage/sqlite :db/connection)

(defn- get-ig-ref-for-storage
  "Returns the ig/ref for the storage type"
  [storage]
  (ig/ref storage))

(defn with-storage
  [config]
  (let [storage-configs
        (filter (fn [[k _]] (= "system.storage" (namespace k)))
                config)]
    (if (seq storage-configs)
      (do
        (log/info (str "System will be initialized with storage types: " (keys storage-configs)))
        (let [refs (into {} (map (fn [[k _]] [k (get-ig-ref-for-storage k)])) storage-configs)]
          (assoc-in config [:system/storage :databases] refs)))
      (merge config {:system/storage {}}))))

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

(defmethod ig/init-key :system/storage
  [_ {:keys [databases]}]

  ;; Database connections
  (let [db-connections (into {}
                             (filter (fn [[_ v]] (contains? v :datasource))
                                     databases))]

;; Return the registry with database connections
    {:db-connections db-connections}))

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
