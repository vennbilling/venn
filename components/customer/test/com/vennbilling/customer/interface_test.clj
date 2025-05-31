(ns com.vennbilling.customer.interface-test
  (:require
   [clojure.test :as test :refer [deftest testing is]]
   [com.vennbilling.customer.interface :as customer]
   [ring.util.http-status :as http-status]))

(deftest testing-customer
  (testing "Customer"
    (testing "with identifier"
      (is (= "abc" (:identifier (customer/new "abc" {} {})))))

    (testing "without traits"
      (is (= {} (:traits (customer/new "abc" {} {})))))

    (testing "with traits"
      (is (= {:name "Test"} (:traits (customer/new "abc" {:name "Test"} {})))))

    (testing "with billing-provider"
      (is (= {:type "stripe" :identifier "abc"} (:billing_provider (customer/new "abc" {:name "Test"} {:type "stripe" :identifier "abc"})))))))

(deftest testing-routes
  (testing "List"
    (let [[path route] customer/list-handler]
      (testing "path"
        (is (= "/customers" path)))

      (testing "http"
        (testing "GET request"
          (testing "handler function"
            (let [{{:keys [handler]} :get} route
                  resp (handler {})
                  {:keys [status body]} resp]
              (is (= http-status/ok status))
              (is (= [] body))))))))

  (testing "Show"
    (let [[path route] customer/show-handler]
      (testing "path"
        (is (= "/customers/:id" path)))

      (testing "http"
        (testing "GET request"
          (testing "handler function"
            (let [{{:keys [handler]} :get} route
                  resp (handler {:path-params {:id "1"}})
                  {:keys [status body]} resp]
              (is (= http-status/ok status))
              (is (= {} body)))))))))
