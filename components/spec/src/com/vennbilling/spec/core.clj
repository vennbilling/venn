(ns com.vennbilling.spec.core
  (:require
   [com.vennbilling.customer.interface :as customer]
   [ring.util.http-response :as http]
   [ring.util.http-status :as http-status]))

(defn- identify-handler
  [{{:keys [identifier traits]
     billing-provider :billing_provider
     :or {traits {} billing-provider {}}} :body-params}]
  (http/created "" (customer/new identifier traits billing-provider)))

(def identify-route
  ["/identify"
   {:post {:parameters {:body customer/Schema}
           :responses {http-status/created {:body customer/Schema}}
           :handler identify-handler}}])

(def ^:private spec-request-schema
  [:map
   [:customer_id string?]
   [:event string?]
   [:properties {:otional true} map?]])

(defn- charge-handler
  [_]
  (http/created "" {}))

(def charge-route
  ["/charge"
   {:post {:parameters {:body spec-request-schema}
           :handler charge-handler}}])

(defn- usage-handler
  [_]
  (http/created "" {}))

(def usage-route
  ["/usage"
   {:post {:parameters {:body spec-request-schema}
           :handler usage-handler}}])

(defn- reverse-handler
  [_]
  (http/created "" {}))

(def reverse-route
  ["/reverse"
   {:post {:parameters {:body spec-request-schema}
           :handler reverse-handler}}])
