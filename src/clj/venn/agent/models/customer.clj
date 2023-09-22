(ns venn.agent.models.customer
  (:require [venn.agent.models.record :as record :refer [Validation Serialization]]
            [malli.core :as m]
            [malli.error :as me]))


(def Schema
  [:map
   [:xt/id :uuid]
   [:identifier :string]
   [:traits :map]])

(defrecord Customer [identifier traits]
  Validation
  (validate [this] (-> Schema
                       (m/schema)
                       (m/validate this)))

  (errors [this] (-> Schema
                     (m/explain this)
                     (me/humanize)))

  Serialization
  (serialize [this] this))


(defn make-customer [identifier traits]
  (assoc (->Customer identifier traits) :xt/id (java.util.UUID/randomUUID)))
