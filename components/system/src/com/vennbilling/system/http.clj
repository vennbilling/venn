(ns com.vennbilling.system.http
  (:require
   [integrant.core :as ig]
   [muuntaja.core :as m]
   [reitit.coercion.malli :as malli]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.adapter.undertow :refer [run-undertow]]
   [ring.logger :as logger]))

(def ^:private router-config
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
                coercion/coerce-request-middleware]})

(defmethod ig/init-key :http/server
  [_ opts]
  (let [handler (atom (delay (:handler opts)))
        undertow-opts (dissoc opts :handler)]
    ;; TODO: Dependency injection of localdb and serverdb
    {:server (run-undertow (fn [req] (@@handler req)) undertow-opts)}))

(defmethod ig/halt-key! :http/server
  [_ {:keys [server]}]
  (.stop server))

(defmethod ig/init-key :http/handler
  [_ {:keys [router]}]

  (logger/wrap-with-logger
   (ring/ring-handler
    router
    (ring/create-default-handler))))

(defmethod ig/init-key :http/router
  [_ {:keys [routes]}]
  (ring/router ["" router-config routes]))
