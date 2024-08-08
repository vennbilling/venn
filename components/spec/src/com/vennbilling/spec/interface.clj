(ns com.vennbilling.spec.interface
  (:require
    [com.vennbilling.spec.core :as core]))


;; IDENTIFY

;; TODO: This should be private but the customer API is using this
(def identify-response-schema core/identify-response-schema)

(def identify-route core/identify-route)


;; CHARGE
(def charge-route [])


;; USAGE
(def usage-route [])


;; REVERSE
(def reverse-route [])
