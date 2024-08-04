(ns com.vennbilling.customer.interface
  (:require
    [com.vennbilling.customer.core :as core]))


(defn make-customer
  [identifier traits billing-provider]
  (core/make-customer identifier traits billing-provider))


(defn find-by-id
  [id]
  (core/find-by-id id))


(defn all
  []
  [(core/find-by-id "1")])
