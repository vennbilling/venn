(ns com.vennbilling.spec.core
  (:require
   [com.vennbilling.customer.interface :as customer]))

(def spec-request-schema
  [:map
   [:customer_id string?]
   [:event string?]
   [:properties {:otional true} map?]])

(defn identify-handler
  [{{:keys [identifier traits]
     billing-provider :billing_provider
     :or {traits {} billing-provider {}}} :body-params}]
  [:created (customer/new identifier traits billing-provider)])

(defn charge-handler
  [_]
  [:created {}])

(defn usage-handler
  [_]
  [:created {}])

(defn reverse-handler
  [_]
  [:created {}])
