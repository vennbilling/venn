(ns com.vennbilling.customer.core)

(def ^:private billing-providers ["undefined" "stripe" "third-party"])

(def Schema
  [:map
   [:identifier :string]
   [:traits :map]
   [:billing_provider {:optional true} [:map
                                        [:type [:enum billing-providers]]
                                        [:identifier [:or :integer :string]]]]])

(defn make-customer
  [identifier traits billing-provider]
  {:identifier identifier
   :traits traits
   :billing_provider billing-provider})

(defn find-by-id
  [id]
  (make-customer id {} {}))
