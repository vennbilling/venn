(ns com.vennbilling.http.ring

  (:require [muuntaja.core :as m]
            [reitit.coercion.malli :as malli]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.logger :as logger]

            [com.vennbilling.http.middleware :as middleware]))

(def ^:private default-middleware [;; query-params & form-params
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
                                   coercion/coerce-request-middleware])

(def ^:private ring-router-config
  {:coercion malli/coercion
   :muuntaja m/instance})

(defn new-handler
  "Creates a new Ring handler with the given router and optional storage configuration.
   If a storage configuration is provided, adds middleware to inject connections to the storage
   into each request.

  TODO These middlewares should be injected via reitit with :middleware not by wrapping a ring handler"
  ([router]
   (-> (ring/ring-handler
        router
        (ring/create-default-handler))
       (logger/wrap-with-logger)))

  ([router storage]
   (-> (ring/ring-handler
        router
        (ring/create-default-handler))
       (middleware/wrap-storage storage)
       (logger/wrap-with-logger))))

(defn new-router
  [base-path routes & middleware]
  (let [middlewares (vec (set (into default-middleware middleware)))
        config (assoc ring-router-config :middleware middlewares)]
    (ring/router [base-path config routes])))
