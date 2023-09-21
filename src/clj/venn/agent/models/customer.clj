(ns venn.agent.models.customer
  (:require [venn.agent.models.validation :refer [Validation]]
            [malli.core :as m]
            [malli.error :as me]))


(def Schema
  [:map
   [:xt/id :uuid]
   [:traits :map]])

(defrecord Customer [traits]
  Validation
  (validate [this] (-> Schema
                     (m/schema)
                     (m/validate this)))

  (errors [this] (-> Schema
                     (m/explain this)
                     (me/humanize))))


(defn make-customer [traits]
  (let [record (->Customer traits)]
    (assoc record :xt/id (java.util.UUID/randomUUID))))
