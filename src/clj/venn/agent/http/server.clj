(ns venn.agent.http.server
  (:require [integrant.core :as ig]
            [reitit.ring :as ring]
            [ring.adapter.undertow :refer [run-undertow]]))

(defmethod ig/init-key :server/http
  [_ opts]
  (let [handler (atom (delay (:handler opts)))]
    {:handler handler}
    {:server (run-undertow (fn [req] (@@handler req)) (dissoc opts :handler))}))

(defmethod ig/halt-key! :server/http
  [_ {:keys [server]}]
  (.stop server))

(defmethod ig/init-key :handler/ring
  [_ {:keys [router]}]
  (ring/ring-handler
   router
   (ring/create-default-handler)))
