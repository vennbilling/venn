(ns com.vennbilling.customer.interface
  (:require
   [com.vennbilling.customer.model :as model]
   [com.vennbilling.customer.handlers :as handlers]))

(def Schema
  model/Schema)

(def list-handler handlers/list)
(def show-handler handlers/show)

(defn new
  "Creates a customer map with the given identifier. Can pass in an optional set of traits and a billing provider"
  ([identifier]
   (model/make-customer identifier {} {}))
  ([identifier traits]
   (model/make-customer identifier traits {}))
  ([identifier traits billing-provider]
   (model/make-customer identifier traits billing-provider)))
