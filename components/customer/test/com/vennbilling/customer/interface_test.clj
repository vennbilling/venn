(ns com.vennbilling.customer.interface-test
  (:require
   [clojure.test :as test :refer [deftest testing is]]
   [com.vennbilling.customer.interface :as customer]))

(deftest testing-customer
  (testing "new"
    (testing "with identifier"
      (is (= "abc" (:identifier (customer/new "abc" {} {})))))

    (testing "without traits"
      (is (= {} (:traits (customer/new "abc" {} {})))))

    (testing "with traits"
      (is (= {:name "Test"} (:traits (customer/new "abc" {:name "Test"} {})))))

    (testing "with billing-provider"
      (is (= {:type "stripe" :identifier "abc"} (:billing_provider (customer/new "abc" {:name "Test"} {:type "stripe" :identifier "abc"})))))))
