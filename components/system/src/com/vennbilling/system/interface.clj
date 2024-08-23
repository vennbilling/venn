(ns com.vennbilling.system.interface
  (:require
    [com.vennbilling.system.core :as core]
    [com.vennbilling.system.lifecycle :as lifecycle]))


(defn start
  ([banner settings]
   (lifecycle/start banner settings)))
