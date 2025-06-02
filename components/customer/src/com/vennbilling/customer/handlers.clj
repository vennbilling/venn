(ns com.vennbilling.customer.handlers)

(defn list-handler
  [_]
  [:ok []])

(defn show-handler
  [_]
  [:not-found {}])
