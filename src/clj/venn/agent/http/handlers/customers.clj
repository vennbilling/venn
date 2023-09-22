(ns venn.agent.http.handlers.customers
  (:require [ring.util.http-response :as http]

            [venn.agent.models.customer :refer [make-customer]]))


(defn upsert! [_req]
  (http/created "" (make-customer "abc" {})))
