(ns com.vennbilling.agent.core
  (:gen-class)
  (:require
    [aero.core :refer [read-config]]
    [clojure.java.io :as io]
    [clojure.tools.logging :as log]
    [com.vennbilling.customer.interface :as customer]
    [integrant.core :as ig]
    [muuntaja.core :as m]
    [reitit.coercion.malli :as malli]
    [reitit.ring :as ring]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.adapter.undertow :refer [run-undertow]]
    [ring.util.http-response :as http]))


(defonce system (atom nil))
(def ^:const banner (slurp (io/resource "agent/banner.txt")))


(def defaults
  {:init       (fn []
                 (log/info "venn agent starting using the development or test profile"))
   :start      (fn []
                 (log/infof "\n\n%s" banner)
                 (log/info "venn agent started successfully using the development or test profile"))
   :stop       (fn []
                 (log/info "venn agent has shut down successfully"))
   :opts {:profile :dev}})


(defmethod aero.core/reader 'ig/ref
  [_ _ value]
  (ig/ref value))


(defmethod aero.core/reader 'ig/refset
  [_ _ value]
  (ig/refset value))


(defmethod ig/init-key :system/env [_ env] env)


(defmethod ig/init-key :server/http
  [_ {:keys [db] :as opts}]
  (let [handler (atom (delay (:handler opts)))]
    {:handler handler}
    {:server (run-undertow (fn [req] (@@handler (assoc req :db db))) (dissoc opts :handler))}))


(defmethod ig/halt-key! :server/http
  [_ {:keys [server]}]
  (.stop server))


(defmethod ig/init-key :handler/ring
  [_ {:keys [router]}]
  (ring/ring-handler
    router
    (ring/create-default-handler)))


(def valid-billing-provider-types ["stripe"])


(def billing-provider-params-schema
  [:map
   [:type [:enum valid-billing-provider-types]]
   [:identifier [:or integer? string?]]])


(def identify-request-schema
  [:map
   [:identifier string?]
   [:traits {:optional true} map?]
   [:billing_provider {:optional true} [:or map? billing-provider-params-schema]]])


(def identify-response-schema (conj identify-request-schema [:xt/id :uuid]))


(def list-response-schema [:vector identify-response-schema])

(def show-response-schema identify-response-schema)


(defn upsert!
  [{{:keys [identifier traits]
     billing-provider :billing_provider
     :or {traits {} billing-provider {}}} :body-params}]
  (http/created "" (customer/serialize (customer/make-customer identifier traits billing-provider))))


(defn index
  [_]
  (http/ok (customer/all)))


(defn show
  [{{:keys [id]} :path-params}]
  (let [c (customer/find-by-id id)]
    (if (seq c)
      (http/ok c)
      (http/not-found))))


(defn internal-routes
  [_opts]
  [["/health"
    {:get {:parameters {:body {}}
           :responses {200 {:body {}}}
           :handler (fn [])}}]])


(defn api-routes
  [_opts]
  [["/identify"
    {:post {:parameters {:body identify-request-schema}
            :responses {201 {:body identify-response-schema}}
            :handler upsert!}}]

   ["/customers"
    {:get {:responses {200 {:body list-response-schema}}
           :handler index}}]

   ["/customers/:id"
    {:get {:responses {200 {:body show-response-schema}
                       404 {}}
           :handler show}}]])


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
(derive :agent.routes/api :agent/routes)


(defmethod ig/init-key :agent.routes/api
  [_ {:keys [base-path]
      :as opts}]
  [base-path (dissoc (route-data opts) :base-path) (api-routes opts)])


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


(defn- config
  [opts]
  (read-config (io/resource "agent/system.edn") opts))


(defn stop-app
  []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!)))


(defn start-app
  []
  ((or (:start defaults) (fn [])))
  (->> (config (:opts defaults))
       (ig/prep)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))


(defn -main
  [& _]
  (start-app))
