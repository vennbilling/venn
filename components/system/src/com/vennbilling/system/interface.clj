(ns com.vennbilling.system.interface
  (:require
    [com.vennbilling.system.core :as core]
    [com.vennbilling.system.lifecycle :as lifecycle]))


(defn create-http
  [config-file profile routes]
  (core/create-http config-file profile routes))


(defn start
  [banner system]
  (lifecycle/start banner system))
