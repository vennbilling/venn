(ns com.vennbilling.system.storage
  (:require [integrant.core :as ig]
            [malli.core :as m]
            [next.jdbc.connection :refer [jdbc-url]]

            [com.vennbilling.logging.interface :as log])

  (:import [com.zaxxer.hikari HikariDataSource]
           [java.sql Connection SQLException]

           [clojure.lang ExceptionInfo]))

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

(defn- build-jdbc-url
  [dbtype config]
  (let [{:keys [database-name]} config
        conn (dissoc config :database-name)
        db (assoc conn :dbname database-name :dbtype dbtype)]
    (jdbc-url db)))

(defn- build-migrations-config
  "Returns a map that represents a configuration used by migratus.core"
  [^HikariDataSource datasource {:keys [directory managed-connection]}]
  {:migration-dir directory
   :db {:datasource datasource
        :managed-connection? managed-connection}})

(defmethod ig/init-key :db/connection
  [storage-type {:keys [migrations] :as db-config}]

  (try
    (let [valid-db-config (m/coerce DBConfig db-config)
          dbtype (name storage-type)
          db (dissoc valid-db-config :migrations)
          jdbc-url (build-jdbc-url dbtype db)
          ^HikariDataSource ds (new HikariDataSource)]

      ;; Configure the datastore
      (-> ds
          (.setJdbcUrl jdbc-url))

      ;; Test DB Connection
      (let [^Connection test-conn (.getConnection ds)]
        (log/info (str "Database connection to " dbtype " is healthy"))
        (.close test-conn)

        ;; Configure Migrations
        (let [migratus-config (build-migrations-config ds migrations)]
          {:datasource ds :migrations-config migratus-config})))
    (catch ExceptionInfo e
      (log/exception e)
      (throw (IllegalArgumentException. (ex-message e))))
    (catch SQLException e
      (log/exception (ex-info "Unable to connect to database" {:cause :sql-exception :exception e}))
      (throw e))))

(defmethod ig/halt-key! :db/connection
  [storage-type db]
  (when-let [{:keys [datasource]} db]
    (log/info (str "Closing connection to " (name storage-type)))
    (.close datasource)))
