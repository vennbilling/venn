(ns com.vennbilling.database.interface
  (:require [com.vennbilling.database.core :as db]))

(defn exec!
  "Executes a query designed to not return any rows"
  [conn q]
  (db/exec! conn q))

(defn query!
  "Executes a query and returns any data"
  [conn q]
  (db/query! conn q))
