(ns com.vennbilling.system.interface
  (:require
    [com.vennbilling.system.config :as config]
    [com.vennbilling.system.lifecycle :as lifecycle]))


(defn start
  [banner config-file opts]
  (lifecycle/start banner config-file opts))


(defn read-config
  [config-file opts]
  (config/read-config config-file opts))
