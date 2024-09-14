(ns com.vennbilling.system.interface
  (:require
    [com.vennbilling.system.core :as core]
    [com.vennbilling.system.lifecycle :as lifecycle]))


(defn start
  [banner system]
  (lifecycle/start banner system))


(defn init
  [config profile routes]
  (core/init config profile routes))
