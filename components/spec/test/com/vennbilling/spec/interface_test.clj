(ns com.vennbilling.spec.interface-test
  (:require
   [clojure.test :as test :refer [deftest testing is]]
   [com.vennbilling.spec.interface :as spec]
   [ring.util.http-status :as http-status]))

(deftest testing-routes []
  (testing "IDENTIFY"
    (let [[path route] spec/identify-route]
      (testing "path"
        (is (= "/identify" path)))

      (testing "http"
        (testing "POST request"
          (testing "handler function"
            (let [{{:keys [handler]} :post} route]
              (testing "with no traits or billing provider"
                (let [request {:body-params {:identifier "a" :traits {} :billing_provider {}}}
                      resp (handler request)
                      {:keys [status body]} resp
                      {:keys [identifier traits billing-provider]} body]
                  (is (= http-status/created status))
                  (is (= "a" identifier))
                  (is (= {} traits))
                  (is (nil? billing-provider))))
              (testing "with traits but no billing provider"
                (let [request {:body-params {:identifier "a" :traits {:customer true} :billing_provider {}}}
                      resp (handler request)
                      {:keys [status body]} resp
                      {:keys [identifier traits billing-provider]} body]
                  (is (= http-status/created status))
                  (is (= "a" identifier))
                  (is (= {:customer true} traits))
                  (is (nil? billing-provider))))
              (testing "with all attributes"
                (let [request {:body-params {:identifier "a" :traits {:customer true} :billing_provider {:name "stripe"}}}
                      resp (handler request)
                      {:keys [status body]} resp
                      {:keys [identifier traits billing_provider]} body]
                  (is (= http-status/created status))
                  (is (= "a" identifier))
                  (is (= {:customer true} traits))
                  (is (= {:name "stripe"} billing_provider))))))))))

  (testing "CHARGE"
    (let [[path route] spec/charge-route]
      (testing "path"
        (is (= "/charge" path)))

      (testing "http"
        (testing "POST request"
          (testing "handler function"
            (let [{{:keys [handler]} :post} route]
              (testing "with no properties"
                (let [request {:body-params {:customer_id "a" :event "Test Ran" :properties {}}}
                      resp (handler request)
                      {:keys [status]} resp]
                  (is (= http-status/created status))))
              (testing "with missing properties"
                (let [request {:body-params {:customer_id "a" :event "Test Ran"}}
                      resp (handler request)
                      {:keys [status]} resp]
                  (is (= http-status/created status))))
              (testing "with properties"
                (let [request {:body-params {:customer_id "a" :event "Test Ran" :properties {:quantity 1}}}
                      resp (handler request)
                      {:keys [status]} resp]
                  (is (= http-status/created status))))))))))

  (testing "USAGE"
    (let [[path route] spec/usage-route]
      (testing "path"
        (is (= "/usage" path)))

      (testing "http"
        (testing "POST request"
          (testing "handler function"
            (let [{{:keys [handler]} :post} route]
              (testing "with no properties"
                (let [request {:body-params {:customer_id "a" :event "Test Ran" :properties {}}}
                      resp (handler request)
                      {:keys [status]} resp]
                  (is (= http-status/created status))))
              (testing "with missing properties"
                (let [request {:body-params {:customer_id "a" :event "Test Ran"}}
                      resp (handler request)
                      {:keys [status]} resp]
                  (is (= http-status/created status))))
              (testing "with properties"
                (let [request {:body-params {:customer_id "a" :event "Test Ran" :properties {:quantity 1}}}
                      resp (handler request)
                      {:keys [status]} resp]
                  (is (= http-status/created status))))))))))

  (testing "REVERSE"
    (let [[path route] spec/reverse-route]
      (testing "path"
        (is (= "/reverse" path)))

      (testing "http"
        (testing "POST request"
          (testing "handler function"
            (let [{{:keys [handler]} :post} route]
              (testing "with no properties"
                (let [request {:body-params {:customer_id "a" :event "Test Ran" :properties {}}}
                      resp (handler request)
                      {:keys [status]} resp]
                  (is (= http-status/created status))))
              (testing "with missing properties"
                (let [request {:body-params {:customer_id "a" :event "Test Ran"}}
                      resp (handler request)
                      {:keys [status]} resp]
                  (is (= http-status/created status))))
              (testing "with properties"
                (let [request {:body-params {:customer_id "a" :event "Test Ran" :properties {:quantity 1}}}
                      resp (handler request)
                      {:keys [status]} resp]
                  (is (= http-status/created status)))))))))))
