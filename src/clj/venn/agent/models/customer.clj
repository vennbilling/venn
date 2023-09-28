(ns venn.agent.models.customer
  (:require [venn.agent.models.record :as record :refer [Validation Serialization]]
            [malli.core :as m]
            [malli.error :as me]))


(def valid-billing-provider-types ["stripe"])

(def Schema
  [:map
   [:xt/id :uuid]
   [:identifier :string]
   [:traits :map]
   [:billing-provider :map]])

(defrecord Customer [identifier traits billing-provider]
  Validation
  (validate [this] (-> Schema
                       (m/schema)
                       (m/validate this)))

  (errors [this] (-> Schema
                     (m/explain this)
                     (me/humanize)))

  Serialization
  (serialize [this] (-> this
                        (dissoc :billing-provider)
                        (assoc :billing_provider (:billing-provider this)))))


(defn make-customer [identifier traits billing-provider]
  (assoc (->Customer identifier traits billing-provider) :xt/id (java.util.UUID/randomUUID)))

(defn find-by-id [id]
  (assoc (make-customer id {} {}) :xt/id (java.util.UUID/randomUUID)))
