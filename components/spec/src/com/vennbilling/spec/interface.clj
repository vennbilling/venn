(ns com.vennbilling.spec.interface
  (:require
   [com.vennbilling.spec.core :as core]))

(def Schema core/spec-request-schema)

;; IDENTIFY
(def identify-handler core/identify-handler)

;; CHARGE
(def charge-handler core/charge-handler)

;; USAGE
(def usage-handler core/usage-handler)

;; REVERSE
(def reverse-handler core/reverse-handler)
