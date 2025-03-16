(ns com.vennbilling.system.storage
  (:require [integrant.core :as ig]
            [next.jdbc.connection :refer [jdbc-url]]

            [malli.core :as m]

            [com.vennbilling.logging.interface :as log])

  (:import [com.zaxxer.hikari HikariDataSource]
           [java.sql Connection SQLException]

           [clojure.lang ExceptionInfo]))

;; Supported Storage Engines
(derive :storage.type/postgresql :db/connection)

(def ^:private storage-config
  {:system/storage (ig/refset :storage/type)})

(defn with-storage
  [storage]
  (merge-with into storage-config storage))

(defmethod ig/init-key :system/storage
  [_ stores]
  stores)

(def ^:private DBConfig
  [:map
   {:closed true}
   [:database_name :string]
   [:host :string]
   [:port :int]
   [:user :string]
   [:password :string]])

(defn- build-jdbc-url
  [dbtype config]
  (let [{:keys [database_name]} config
        conn (dissoc config :database_name)
        db (assoc conn :dbname database_name :dbtype dbtype)]
    (jdbc-url db)))

(defmethod ig/init-key :db/connection
  [storage-type db-config]

  (try
    (let [valid (m/coerce DBConfig db-config)
          url (build-jdbc-url (name storage-type) valid)
          ^HikariDataSource ds (new HikariDataSource)]

      ;; Configure the datastore
      (-> ds
          (.setJdbcUrl url))

      (let [^Connection test-conn (.getConnection ds)]
        (log/info "Database connection healthy")
        (.close test-conn)

      ;; TODO Need to configure Migratus
      ;; https://github.com/vennbilling/venn/issues/55

        {:datasource ds}))
    (catch ExceptionInfo e
      (log/exception e)
      (throw (IllegalArgumentException. (ex-message e))))
    (catch SQLException e
      (log/exception (ex-info "Unable to connect to database" {:cause :sql-exception :exception e}))
      (throw e))))

(defmethod ig/halt-key! :db/connection
  [_ db]
  (when-let [{:keys [datasource]} db]
    (.close datasource)))
