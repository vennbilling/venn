(ns com.vennbilling.system.interface
  (:require
    [com.vennbilling.system.core :as core]
    [com.vennbilling.system.lifecycle :as lifecycle]))


(defn create-http
  [config-file profile routes]
  (core/create-http config-file profile routes))


(defn new-http-server
  [routes]
  (core/new-http-server routes))


(defn init
  [config profile]
  (core/init-system config profile))


(defn with-http-component
  [system server]
  (core/with-http-component system server))


(defn start
  [banner system]
  (lifecycle/start banner system))
