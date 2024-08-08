(ns com.vennbilling.spec.interface-test
  (:require
    [clojure.test :as test :refer [deftest testing is]]
    [com.vennbilling.spec.interface :as spec]))


(deftest testing-routes []
  (testing "IDENTIFY"
    (let [[path route] spec/identify-route]
      (testing "path"
        (is (= "/identify" path)))

      (testing "http"
        (testing "POST request"
          (testing "handler function"
            (let [{{:keys [handler]} :post} route]
              (testing "with no traits or billing providider"
                (let [request {:body-params {:identifier "a" :traits {} :billing_provider {}}}
                      resp (handler request)
                      {:keys [status body]} resp
                      {:keys [identifier traits billing-provider]} body]
                  (is (= 201 status))
                  (is (= "a" identifier))
                  (is (= {} traits))
                  (is (nil? billing-provider))))
              (testing "with traits but no billing provider"
                (let [request {:body-params {:identifier "a" :traits {:customer true} :billing_provider {}}}
                      resp (handler request)
                      {:keys [status body]} resp
                      {:keys [identifier traits billing-provider]} body]
                  (is (= 201 status))
                  (is (= "a" identifier))
                  (is (= {:customer true} traits))
                  (is (nil? billing-provider))))
              (testing "with all attributes"
                (let [request {:body-params {:identifier "a" :traits {:customer true} :billing_provider {:name "stripe"}}}
                      resp (handler request)
                      {:keys [status body]} resp
                      {:keys [identifier traits billing_provider]} body]
                  (is (= 201 status))
                  (is (= "a" identifier))
                  (is (= {:customer true} traits))
                  (is (= {:name "stripe"} billing_provider)))))))))))
