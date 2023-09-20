(ns venn.agent.http.routes
  (:require [integrant.core :as ig]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as malli]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]))


(defn internal-routes [_opts]
  ["/health" {:get {:responses {200 {:status string?}}
                    :handler (fn [_] {:status 200 :body {:status "ok"}})}}])

;;(defn api-routes [_opts]
;;  [[]])

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
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware]}))

(derive :agent.routes/internal :agent/routes)
;; (derive :agent.routes/api :agent/routes)

;; (defmethod ig/init-key :agent.routes/api
;;  [_ {:keys [base-path]
;;      :as opts}]
;; [base-path (dissoc (route-data opts) :base-path) (api-routes opts)])

(defmethod ig/init-key :agent.routes/internal
  [_ {:keys [base-path]
      :as opts}]
  [base-path (dissoc (route-data opts) :base-path) (internal-routes opts)])

(defmethod ig/init-key :router/routes
  [_ {:keys [routes]}]
  (apply conj [] routes))

(defmethod ig/init-key :router/core
  [_ {:keys [routes] :as opts}]
  (ring/router ["" opts routes]))
