(ns com.vennbilling.system.db
  [:require [integrant.core :as ig]])


;; TODO: init-key for db/local and db/server
(defmethod ig/init-key :db/server
  [_ opts])


(defmethod ig/halt-key! :db/server [_ {:keys [server]}])
