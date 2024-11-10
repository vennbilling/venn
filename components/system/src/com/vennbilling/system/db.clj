(ns com.vennbilling.system.db
  (:require [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defmethod ig/init-key :db/server
  [_ db]
  (let [ds (jdbc/get-datasource db)
        db-conn (jdbc/get-connection ds)]
    {:db-conn db-conn}))

(defmethod ig/halt-key! :db/server [_ _])
