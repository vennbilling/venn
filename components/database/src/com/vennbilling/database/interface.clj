(ns com.vennbilling.database.interface
  (:require [migratus.core :as migratus]))

(defn init [cfg]
  (migratus/init cfg))
