(ns com.vennbilling.database.interface
  (:require [com.vennbilling.database.core :as db]))

(defn establish-connection
  "Establishes a connection database using the provided configuration and returns a javax.sql.Datasource."
  [dbtype config]
  (db/establish-connection dbtype config))

(defn bootstrap
  "Bootstraps a database, running all pending migrations with migratus"
  [migratus-config]
  (db/bootstrap migratus-config))

(defn exec!
  "Executes a query designed to not return any rows"
  [conn q]
  (db/exec! conn q))

(defn query!
  "Executes a query and returns any data"
  [conn q]
  (db/query! conn q))
