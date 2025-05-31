(ns com.vennbilling.customer.interface
  (:require
   [com.vennbilling.customer.core :as core]
   [com.vennbilling.customer.routes :as routes]))

(def Schema
  core/Schema)

(defn make-customer
  "Creates a customer record with the given identifier. Can pass in an optional set of traits and a billing provider"
  ([identifier]
   (core/make-customer identifier {} {}))
  ([identifier traits]
   (core/make-customer identifier traits {}))
  ([identifier traits billing-provider]
   (core/make-customer identifier traits billing-provider)))

(defn find-by-id
  [id]
  (core/find-by-id id))

(defn all
  []
  [(core/find-by-id "1")])

(def list-route routes/list-route)
(def show-route routes/show-route)
