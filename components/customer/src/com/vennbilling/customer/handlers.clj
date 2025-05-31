(ns com.vennbilling.customer.handlers
  (:require
   [com.vennbilling.customer.model :as customer]
   [ring.util.http-response :as http]
   [ring.util.http-status :as http-status]))

(def ^:private list-response-schema [:vector #'customer/Schema])
(def ^:private show-response-schema #'customer/Schema)

(defn- list-handler
  [_]
  (http/ok []))

(def list
  ["/customers"
   {:get {:responses {http-status/ok {:body list-response-schema}}
          :handler list-handler}}])

(defn- show-handler
  [_]
  (http/ok {}))

(def show
  ["/customers/:id"
   {:get {:responses {http-status/ok {:body show-response-schema}
                      http-status/not-found {}}
          :handler show-handler}}])
