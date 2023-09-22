(ns venn.agent.http.handlers.customers
  (:require [ring.util.http-response :as http]

            [venn.agent.models.customer :refer [make-customer]]))


(defn upsert! [{{:keys [identifier traits]} :body-params}]
  (if (seq traits)
    (http/created "" (.serialize (make-customer identifier traits)))
    (http/created "" (.serialize (make-customer identifier {})))))
