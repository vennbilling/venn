(ns venn.agent.http.handlers.customers
  (:require [ring.util.http-response :as http]

            [venn.agent.models.customer :as customer :refer [make-customer valid-billing-provider-types]]
            [venn.agent.models.record :as record]))

(def billing-provider-params-schema [:map
                                     [:type [:enum valid-billing-provider-types]]
                                     [:identifier [:or integer? string?]]])

(def identify-request-schema [:map
                              [:identifier string?]
                              [:traits {:optional true} map?]
                              [:billing_provider {:optional true} [:or map? billing-provider-params-schema]]])


(def identify-response-schema (conj identify-request-schema [:xt/id :uuid]))


(def list-response-schema [:vector identify-response-schema])

(def show-response-schema identify-response-schema)


(defn upsert! [{{:keys [identifier traits]
                 billing-provider :billing_provider
                 :or { traits {} billing-provider {} }} :body-params}]
  (http/created "" (record/serialize (make-customer identifier traits billing-provider))))


(defn index [_]
  (http/ok (customer/all)))

(defn show [{{:keys [id]} :path-params}]
  (let [c (customer/find-by-id id)]
    (if (seq c)
      (http/ok c)
      (http/not-found))))
