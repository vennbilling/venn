(ns com.vennbilling.customer.interface-test
  (:require
    [clojure.test :as test :refer [deftest testing is]]
    [com.vennbilling.customer.interface :as customer]
    [ring.util.http-status :as http-status]))


(deftest testing-customer
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


(deftest testing-routes
  (testing "List"
    (let [[path route] customer/list-route]
      (testing "path"
        (is (= "/customers" path)))


      (testing "http"
        (testing "GET request"
          (testing "handler function"
            (let [{{:keys [handler]} :get} route
                  resp (handler {})
                  {:keys [status body]} resp
                  customer (first body)
                  {:keys [identifier traits billing-provider]} customer]
              (is (= http-status/ok status))
              (is (= "1" identifier))
              (is (= {} traits))
              (is (= {} billing-provider))))))))

  (testing "Show"
    (let [[path route] customer/show-route]
      (testing "path"
        (is (= "/customers/:id" path)))

      (testing "http"
        (testing "GET request"
          (testing "handler function"
            (let [{{:keys [handler]} :get} route
                  resp (handler {:path-params {:id "1"}})
                  {:keys [status body]} resp
                  {:keys [identifier traits billing-provider]} body]
              (is (= http-status/ok status))
              (is (= "1" identifier))
              (is (= {} traits))
              (is (= {} billing-provider)))))))))
