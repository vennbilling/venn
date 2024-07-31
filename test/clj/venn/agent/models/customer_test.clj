(ns venn.agent.models.customer-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [venn.agent.models.customer :refer [make-customer]]))


(deftest customer-test
  (testing "Customer"
    (testing "with identifier"
      (is (= "abc" (:identifier (make-customer "abc" {} {})))))

    (testing "without traits"
      (is (= {} (:traits (make-customer "abc" {} {}))))
      (is (nil? (.errors (make-customer "abc" {} {}))))
      (is (true? (.validate (make-customer "abc" {} {})))))

    (testing "with traits"
      (is (= {:name "Test"} (:traits (make-customer "abc" {:name "Test"} {}))))
      (is (nil? (.errors (make-customer "abc" {:name "Test"} {}))))
      (is (true? (.validate (make-customer "abc" {:name "Test"} {})))))

    (testing "with billing-provider"
      (is (= {:type "stripe" :identifier "abc"} (:billing-provider (make-customer "abc" {:name "Test"} {:type "stripe" :identifier "abc"}))))
      (is (nil? (.errors (make-customer "abc" {:name "Test"} {}))))
      (is (true? (.validate (make-customer "abc" {:name "Test"} {})))))

    (testing "with xt/id"
      (is (= false (nil? (:xt/id (make-customer "abc" {} {}))))))))
