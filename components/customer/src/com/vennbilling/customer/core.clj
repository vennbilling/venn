(ns com.vennbilling.customer.core)


(defrecord Customer
  [identifier traits billing-provider])


(defn make-customer
  [identifier traits billing-provider]
  (assoc (->Customer identifier traits billing-provider) :xt/id (java.util.UUID/randomUUID)))


(defn find-by-id
  [id]
  (assoc (make-customer id {} {}) :xt/id (java.util.UUID/randomUUID)))
