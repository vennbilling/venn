(ns com.vennbilling.spec.interface
  (:require
    [com.vennbilling.spec.core :as core]))


;; IDENTIFY

;; TODO: These should be private. The customer API is using them but should define its own schema + routes
(def identify-request-schema core/identify-request-schema)
(def identify-response-schema core/identify-response-schema)
(def identify-route core/identify-route)


;; CHARGE


;; USAGE


;; REVERSE
