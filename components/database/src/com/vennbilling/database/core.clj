(ns com.vennbilling.database.core
  (:require
   [next.jdbc :as jdbc]
   [honey.sql :as sql])

  (:import (javax.sql DataSource)))

(defn exec!
  [conn q]
  {})

(defn query!
  [^DataSource conn q]
  (jdbc/execute! conn (sql/format q)))
