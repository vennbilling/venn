(ns com.vennbilling.customer.interface-test
  (:require
    [clojure.test :as test :refer [deftest testing is]]
    [com.vennbilling.customer.interface :as customer]))


(deftest customer-test
  (testing "Customer"
    (testing "with identifier"
      (is (= "abc" (:identifier (customer/make-customer "abc" {} {})))))

    (testing "without traits"
      (is (= {} (:traits (customer/make-customer "abc" {} {}))))
      (is (nil? (.errors (customer/make-customer "abc" {} {}))))
      (is (true? (.validate (customer/make-customer "abc" {} {})))))

    (testing "with traits"
      (is (= {:name "Test"} (:traits (customer/make-customer "abc" {:name "Test"} {}))))
      (is (nil? (.errors (customer/make-customer "abc" {:name "Test"} {}))))
      (is (true? (.validate (customer/make-customer "abc" {:name "Test"} {})))))

    (testing "with billing-provider"
      (is (= {:type "stripe" :identifier "abc"} (:billing-provider (customer/make-customer "abc" {:name "Test"} {:type "stripe" :identifier "abc"}))))
      (is (nil? (.errors (customer/make-customer "abc" {:name "Test"} {}))))
      (is (true? (.validate (customer/make-customer "abc" {:name "Test"} {})))))

    (testing "with xt/id"
      (is (= false (nil? (:xt/id (customer/make-customer "abc" {} {}))))))))
