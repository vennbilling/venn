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
  [_ db-spec]
  (let [url (jdbc-url db-spec)
        config (init-connection-pool-config url)
        ds (new HikariDataSource config)
        conn (jdbc/get-connection ds)]
    {:db-conn conn}))

(defmethod ig/halt-key! :db/server [_ _])
