(ns venn.agent.api.routes
  (:require [muuntaja.core :as m]
            [reitit.coercion.malli :as malli]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]))


(defn internal-routes [_opts]
  [["/health" {:get {:responses {200 {}}
                     :handler (fn [_] {:status 200 :body {}})}}]])

(defn api-routes [_opts]
  [[]])

(defn route-data
  [opts]
  (merge
   opts
   {:coercion malli/coercion
    :muuntaja m/instance
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 coercion/coerce-exceptions-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware]}))

(derive :agent.routes/internal :router/routes)
(derive :agent.routes/api :router/routes)
