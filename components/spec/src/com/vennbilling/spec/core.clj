(ns com.vennbilling.spec.core
  (:require
    [com.vennbilling.customer.interface :as customer]
    [ring.util.http-response :as http]
    [ring.util.http-status :as http-status]))


(def ^:private valid-billing-provider-types ["stripe"])


(def ^:private billing-provider-params-schema
  [:map
   [:type [:enum valid-billing-provider-types]]
   [:identifier [:or integer? string?]]])


(def ^:private identify-request-schema
  [:map
   [:identifier string?]
   [:traits {:optional true} map?]
   [:billing_provider {:optional true} [:or map? billing-provider-params-schema]]])


;; TODO: This should be private but the customer API is using this
(def identify-response-schema (conj identify-request-schema [:xt/id :uuid]))


(defn- identify-handler
  [{{:keys [identifier traits]
     billing-provider :billing_provider
     :or {traits {} billing-provider {}}} :body-params}]
  (http/created "" (customer/serialize (customer/make-customer identifier traits billing-provider))))


(def identify-route
  ["/identify"
   {:post {:parameters {:body identify-request-schema}
           :responses {http-status/created {:body identify-response-schema}}
           :handler identify-handler}}])
