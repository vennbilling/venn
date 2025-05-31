(ns com.vennbilling.http.undertow
  (:require
   [ring.adapter.undertow :refer [run-undertow]]
   [com.vennbilling.logging.interface :as log]))

(defn serve
  [config handler]
  (let [handler-fn (atom (delay handler))]
    ;; TODO: Dependency injection of any system/storage
    ;; https://github.com/vennbilling/venn/issues/55
    (log/info "Starting undertow server")
    (run-undertow (fn [req] (@@handler-fn req)) config)))
