(ns venn.agent.models.customer-test
  (:require [venn.agent.models.customer :refer [make-customer]]
            [clojure.test :refer [deftest testing is]]))

(deftest customer-test
  (testing "Customer"
    (testing "without traits"
      (is (= {} (:traits (make-customer {}))))
      (is (nil? (.errors (make-customer {}))))
      (is (true? (.validate (make-customer {})))))

    (testing "with traits"
      (is (= {:name "Test"} (:traits (make-customer {:name "Test"}))))
      (is (nil? (.errors (make-customer {:name "Test"}))))
      (is (true? (.validate (make-customer {:name "Test"})))))

    (testing "with xt/id"
      (is (= false (nil? (:xt/id (make-customer {}))))))))
