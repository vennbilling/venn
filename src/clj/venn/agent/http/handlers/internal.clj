(ns venn.agent.http.handlers.internal
  (:require
    [ring.util.http-response :as http]
    [xtdb.api :as xt]))


(def health-response-schema
  [:map
   [:db map?]])


(defn health
  [{:keys [db]}]
  (http/ok {:db (xt/status db)}))
