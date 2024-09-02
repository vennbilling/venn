(ns com.vennbilling.logging.interface
  (:require
    [io.pedestal.log :as log]))


(defn warn
  [msg]
  (log/warn :msg msg))


(defn info
  [msg]
  (log/info :msg msg))
