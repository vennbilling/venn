(ns com.vennbilling.system.db
  (:require [integrant.core :as ig]
            [next.jdbc.connection :refer [jdbc-url]]

            [com.vennbilling.logging.interface :as l])

  (:import (com.zaxxer.hikari HikariConfig
                              HikariDataSource)))

(defn- init-connection-pool-config
  [url]
  (let [^HikariConfig config (new HikariConfig)]
    (-> config
        (.setJdbcUrl url))
    config))

(defmethod ig/init-key :db/server
  [_ {:keys [db]}]
  (let [{:keys [managed-connection?]} db
        migration-dir-config (dissoc db :managed-connection?)
        url (jdbc-url db)
        config (init-connection-pool-config url)]

    (try
      (let [^HikariDataSource ds (new HikariDataSource config)
            test-conn (.getConnection ds)]
        (l/info "Database connection healthy")
        (.close test-conn)

        ;; TODO: This feels weird. We are forming a config in a structure migratus.core wants. 
        ;; Ideally, :db/server shouldn't understand or care. A separate key, :db/migrations should handle this
        (into {:db {:datasource ds
                    :managed-connection? managed-connection?}}
              migration-dir-config))
      (catch Exception e
        (l/warn (str "Failed to connect to database. err: " e))
        (throw e)))))

(defmethod ig/halt-key! :db/server [_ {:keys [db]}]
  (let [{:keys [datasource]} db]
    (.close datasource)))
