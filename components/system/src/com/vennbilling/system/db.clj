(ns com.vennbilling.system.db
  (:require [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :refer [jdbc-url]])

  (:import (com.zaxxer.hikari HikariConfig
                              HikariDataSource)))

(defn- init-connection-pool-config
  [url]
  (let [config (new HikariConfig)]
    (-> config
        (.setJdbcUrl url))
    config))

(defmethod ig/init-key :db/server
  [_ {:keys [db]}]
  (prn db)
  (let [url (jdbc-url db)
        config (init-connection-pool-config url)
        ds (new HikariDataSource config)
        conn (jdbc/get-connection ds)]
    (into {:db {:datasource ds
                :connection conn}}
          db)))
(defmethod ig/halt-key! :db/server [_ {:keys [db]}]
  (let [{:keys [datasource]} db]
    (.close datasource)))
