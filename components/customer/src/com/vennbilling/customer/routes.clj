(ns com.vennbilling.customer.routes
  (:require
    [com.vennbilling.customer.core :as core]
    [ring.util.http-response :as http]
    [ring.util.http-status :as http-status]))


(def ^:private valid-billing-provider-types ["stripe"])


(def ^:private billing-provider-params-schema
  [:map
   [:type [:enum valid-billing-provider-types]]
   [:identifier [:or integer? string?]]])


(def ^:private customer-response-schema
  [:map
   [:identifier string?]
   [:traits {:optional true} map?]
   [:billing_provider {:optional true} [:or map? billing-provider-params-schema]]])


(def ^:private list-response-schema [:vector customer-response-schema])
(def ^:private show-response-schema customer-response-schema)


(defn- list-handler
  [_]
  (http/ok [(core/find-by-id "1")]))


(def list-route
  ["/customers"
   {:get {:responses {http-status/ok {:body list-response-schema}}
          :handler list-handler}}])


(defn- show-handler
  [{{:keys [id]} :path-params}]
  (let [c (core/find-by-id id)]
    (if (seq c)
      (http/ok c)
      (http/not-found))))


(def show-route
  ["/customers/:id"
   {:get {:responses {http-status/ok {:body show-response-schema}
                      http-status/not-found {}}
          :handler show-handler}}])
