(ns com.vennbilling.system.server
  (:require
   [com.vennbilling.logging.interface :as log]
   [integrant.core :as ig]
   [malli.core :as ma]
   [muuntaja.core :as m]
   [reitit.coercion.malli :as malli]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.adapter.undertow :refer [run-undertow]]
   [ring.logger :as logger])

  (:import
   [clojure.lang ExceptionInfo]))

(def ^:private ServerConfig
  :map)

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

(defn with-http-server
  [routes]
  {:system/server {:handler (ig/ref :http/handler)
                   :storage (ig/ref :system/storage)}

   :http/handler
   {:router (ig/ref :http/router)}

   :http/router
   {:routes routes}})

(defmethod ig/init-key :system/server
  [_ server-config]
  (try
    (let [valid (ma/coerce ServerConfig server-config)
          handler (atom (delay (:handler valid)))
          undertow-opts (dissoc valid :handler)]

      (log/info "Starting undertow server")

      ;; TODO: Dependency injection of localdb and serverdb
      ;; https://github.com/vennbilling/venn/issues/55
      {:undertow (run-undertow (fn [req] (@@handler req)) undertow-opts)})

    (catch ExceptionInfo e
      (log/exception e)
      (throw (IllegalArgumentException. (ex-message e))))))

(defmethod ig/halt-key! :system/server
  [_ {:keys [undertow]}]
  (log/info "Stopping undertow server")
  (.stop undertow))

;; TODO These should probably live in a different component or namespace
(defmethod ig/init-key :http/handler
  [_ {:keys [router]}]

  (logger/wrap-with-logger
   (ring/ring-handler
    router
    (ring/create-default-handler))))

(defmethod ig/init-key :http/router
  [_ {:keys [routes]}]
  (ring/router ["" router-config routes]))
