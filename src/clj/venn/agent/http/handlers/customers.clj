(ns venn.agent.http.handlers.customers
  (:require [ring.util.http-response :as http]

            [venn.agent.models.customer :refer [make-customer valid-billing-provider-types]]
            [venn.agent.models.record :as record]))

(def billing-provider-params-schema [:map
                                     [:type [:enum valid-billing-provider-types]]
                                     [:identifier [:or integer? string?]]])

(def identify-params-schema [:map
                             [:identifier string?]
                             [:traits map?]
                             [:billing_provider {:optional true} billing-provider-params-schema]])

(defn upsert! [{{:keys [identifier traits] billing-provider :billing_provider :or { billing-provider {} }} :body-params}]
  (if (seq traits)
    (http/created "" (record/serialize (make-customer identifier traits billing-provider)))
    (http/created "" (record/serialize (make-customer identifier {} billing-provider)))))
