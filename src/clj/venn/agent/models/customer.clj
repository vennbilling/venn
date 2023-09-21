(ns venn.agent.models.customer
  (:require [malli.core :as m]
            [malli.error :as me]))


(def Customer
  [:map
   [:xt/id :uuid]
   [:traits :map]])


(defn- errors [record]
  (-> Customer
      (m/explain record)
      (me/humanize)))

(defn- valid? [record]
  (-> Customer
      (m/schema)
      (m/validate record)))

(defn new-customer
  [traits]
  (let [customer {:xt/id (java.util.UUID/randomUUID) :traits traits}]
    (if (valid? customer)
      customer
      (errors customer))))
