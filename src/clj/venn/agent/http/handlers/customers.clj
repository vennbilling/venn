(ns venn.agent.http.handlers.customers
  (:require [ring.util.http-response :as http]

            [venn.agent.models.customer :refer [make-customer]]
            [venn.agent.models.record :as record]))


(defn upsert! [{{:keys [identifier traits]} :body-params}]
  (if (seq traits)
    (http/created "" (record/serialize (make-customer identifier traits)))
    (http/created "" (record/serialize (make-customer identifier {})))))
