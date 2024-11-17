(ns com.vennbilling.system.db
  (:require [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :refer [jdbc-url]])

  (:import (com.zaxxer.hikari HikariConfig
                              HikariDataSource)))

(def ^:private migrations-store :database)
(def ^:private migrations-dir "components/database/resources/database/migrations")
(def ^:private migrations-table-name "schema_versions")
(def ^:private migrations {:store migrations-store
                           :migrations-dir migrations-dir
                           :migration-table-name migrations-table-name})

(defn- init-connection-pool-config
  [url]
  (let [config (new HikariConfig)]
    (-> config
        (.setJdbcUrl url))
    config))

(defmethod ig/init-key :db/server
  [_ opts]
  (let [url (jdbc-url opts)
        config (init-connection-pool-config url)
        ds (new HikariDataSource config)
        conn (jdbc/get-connection ds)]
    {:db {:datasource ds
          :connection conn}
     :migrations migrations}))

(defmethod ig/halt-key! :db/server [_ {:keys [db]}]
  (let [{:keys [datasource]} db]
    (.close datasource)))
