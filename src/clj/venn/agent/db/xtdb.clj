(ns venn.agent.db.xtdb
  (:require [integrant.core :as ig]
            [xtdb.api :as xt]))

(defmethod ig/init-key :db/xtdb
  [_ config] (xt/start-node config))

(defmethod ig/halt-key! :db/xtdb
  [_ node]
  (.close node))
