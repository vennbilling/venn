(ns venn.agent.config
  (:require
    [aero.core :refer [read-config]]
    [clojure.java.io]
    [integrant.core :as ig]))


(defmethod aero.core/reader 'ig/ref
  [_ _ value]
  (ig/ref value))


(defmethod aero.core/reader 'ig/refset
  [_ _ value]
  (ig/refset value))


(defmethod ig/init-key :system/env [_ env] env)

(def x 1)


(defn config
  [opts]
  (read-config (clojure.java.io/resource "system.edn") opts))
