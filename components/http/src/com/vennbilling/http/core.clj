(ns com.vennbilling.http.core
  (:require [ring.util.http-response :as http]))

(defn respond-with
  "Converts handler result tuples like [:ok data] into proper HTTP responses"
  [result]
  (let [[status body] result]
    (case status
      :ok (http/ok body)
      :created (http/created "" body)
      :no-content (http/no-content)
      :bad-request (http/bad-request body)
      :not-found (http/not-found body)
      (http/bad-request body))))
