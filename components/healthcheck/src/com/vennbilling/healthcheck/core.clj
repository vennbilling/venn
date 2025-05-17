(ns com.vennbilling.healthcheck.core
  (:require
   [ring.util.http-response :as http]
   [ring.util.http-status :as http-status]))

(defn- healthcheck-handler
  [opts]
  (http/ok {}))

(def simple-route
  ["/health"
   {:get {:responses {http-status/ok {:body map?}
                      http-status/internal-server-error {}}
          :handler healthcheck-handler}}])
