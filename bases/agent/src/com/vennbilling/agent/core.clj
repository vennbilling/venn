(ns com.vennbilling.agent.core
  (:gen-class)
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [com.vennbilling.customer.interface :as customer]
    [com.vennbilling.healthcheck.interface :as healthcheck]
    [com.vennbilling.spec.interface :as venn-spec]
    [com.vennbilling.system.interface :as system]
    [integrant.core :as ig]
    [io.pedestal.log :as log]
    [muuntaja.core :as m]
    [reitit.coercion.malli :as malli]
    [reitit.ring :as ring]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.adapter.undertow :refer [run-undertow]]
    [ring.logger :as logger]))


(defn internal-routes
  [_opts]
  [healthcheck/simple-route])


(def api-routes
  [venn-spec/identify-route
   customer/list-route
   customer/show-route])


(def router-config
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


;; TODO Move this to system component
(defmethod ig/init-key :http/server
  [_ opts]
  (let [handler (atom (delay (:handler opts)))
        undertow-opts (dissoc opts :handler)]
    {:server (run-undertow (fn [req] (@@handler req)) undertow-opts)}))


;; TODO Move this to system component
(defmethod ig/halt-key! :http/server
  [_ {:keys [server]}]
  (.stop server))


;; TODO Move this to system component
(defmethod ig/init-key :handler/ring
  [_ {:keys [router]}]
  (logger/wrap-with-logger
    (ring/ring-handler
      router
      (ring/create-default-handler))))


;; TODO Move this to system component
(defmethod ig/init-key :router/ring
  [_ {:keys [base-path]}]
  (ring/router [base-path router-config api-routes]))


(def ^:const banner (slurp (io/resource "agent/banner.txt")))
(def config-file (io/resource "agent/system.edn"))


;; TODO Move this to system component
(def settings
  {:http/server
   {:handler (ig/ref :handler/ring)}

   :handler/ring
   {:router (ig/ref :router/ring)}})


(defn -main
  [& _]
  ;; TODO: Pass profile as args
  (let [config (aero/read-config config-file {:profile :prod})
        agent (merge-with into config settings)]
    (system/start banner agent))
  (log/info :msg "venn agent started successfully."))
