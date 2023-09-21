(ns venn.agent.models.customer
  (:require [venn.agent.models.validation :refer [Validation]]
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
                     (me/humanize))))


(defn make-customer [identifier traits]
  (assoc (->Customer identifier traits) :xt/id (java.util.UUID/randomUUID)))
