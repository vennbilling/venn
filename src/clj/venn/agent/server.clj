(ns venn.agent.server
  (:require [integrant.core :as ig]
            [reitit.ring :as ring]
            [ring.adapter.undertow :refer [run-undertow]]

            [venn.agent.api.routes :refer [route-data api-routes]]))

(defmethod ig/init-key :server/http
  [_ opts]
  (let [handler (atom (delay (:handler opts)))]
    {:handler handler}
    {:server (run-undertow (fn [req] @@handler req) (dissoc opts :handler))}))

(defmethod ig/halt-key! :server/http
  [_ {:keys [server]}]
  (.stop server))

(defmethod ig/init-key :handler/ring
  [_ {:keys [routes]}]
  (ring/ring-handler
   (ring/router routes)))

(defmethod ig/init-key :routes/api
  [_ {:keys [base-path]
      :as opts}]
  [base-path (route-data opts) (api-routes opts)])
