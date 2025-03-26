(ns com.vennbilling.database.core
  (:require
   [honey.sql :as sql]
   [migratus.core :as migratus]
   [next.jdbc :as jdbc]
   [next.jdbc.connection :refer [jdbc-url]]

   [com.vennbilling.logging.interface :as log])

  (:import [javax.sql DataSource]
           [java.sql SQLException]
           [com.zaxxer.hikari HikariDataSource]))

(defn- build-jdbc-url
  [dbtype config]
  (let [{:keys [database-name]} config
        conn (dissoc config :database-name)
        db (assoc conn :dbname database-name :dbtype dbtype)]
    (jdbc-url db)))

(defn establish-connection
  [dbtype config]
  (let [jdbc-url (build-jdbc-url dbtype config)
        ds (HikariDataSource.)]

    (try
      ;; Configure the datastore
      (-> ds
          (.setJdbcUrl jdbc-url))

      ;; Test DB Connection
      (log/info (str "Testing database connection to " dbtype "..."))
      (let [test-conn (.getConnection ds)]
        (log/info (str "Database connection to " dbtype " is healthy"))
        (.close test-conn)

        ;; Return the datasource
        ds)

      (catch SQLException e
        (let [ex (ex-info "Unable to connect to database" {:cause :sql-exception :exception e})]
          (throw ex))))))

(defn bootstrap
  [migratus-config]
  (if (seq (migratus/pending-list migratus-config))
    (do
      (log/info "Bootstrapping database. Running all migrations...")
      (migratus/migrate migratus-config)
      (log/info "Bootstrapping complete."))

    (log/info "Skipped bootstrapping database. Schema is up to date.")))

(defn exec!
  [conn q]
  {})

(defn query!
  [^DataSource conn q]
  (jdbc/execute! conn (sql/format q)))
