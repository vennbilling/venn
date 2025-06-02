(ns com.vennbilling.http.routes
  (:require [com.vennbilling.customer.interface :as customer]
            [com.vennbilling.healthcheck.interface :as healthcheck]
            [com.vennbilling.spec.interface :as spec]
            [ring.util.http-response :as http]
            [ring.util.http-status :as http-status]))

(defn- respond-with
  [result]
  (let [[status body] result]
    (case status
      :ok (http/ok body)
      :created (http/created "" body)
      :no-content (http/no-content)
      :bad-request (http/bad-request body)
      :not-found (http/not-found body)
      (http/bad-request body))))

(def healthchecks
  [["/health"
    {:get {:responses {http-status/ok {:body map?}
                       http-status/internal-server-error {}}
           :handler (comp respond-with healthcheck/simple-handler)}}]])

(def spec
  [["/identify"
    {:post {:parameters {:body customer/Schema}
            :responses {http-status/created {:body customer/Schema}}
            :handler (comp respond-with spec/identify-handler)}}]

   ["/charge"
    {:post {:parameters {:body spec/Schema}
            :handler (comp respond-with spec/charge-handler)}}]

   ["/usage"
    {:post {:parameters {:body spec/Schema}
            :handler (comp respond-with spec/usage-handler)}}]

   ["/reverse"
    {:post {:parameters {:body spec/Schema}
            :handler (comp respond-with spec/reverse-handler)}}]])

(def customers
  ["/customers"
   [""
    {:get {:responses {http-status/ok {:body [:vector customer/Schema]}}
           :handler (comp respond-with customer/list-handler)}}]
   ["/:id"
    {:get {:responses {http-status/ok {:body customer/Schema}
                       http-status/not-found {}}
           :handler (comp respond-with customer/show-handler)}}]])
