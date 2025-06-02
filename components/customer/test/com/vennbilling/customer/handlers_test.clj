(ns com.vennbilling.customer.handlers-test
  (:require [clojure.test :as test :refer [deftest testing is]]
            [com.vennbilling.customer.handlers :as customer]))

(deftest testing-handlers
  (testing "List"
    (let [[status body] (customer/list-handler {})]
      (is (= :ok status))
      (is (= [] body)))))
