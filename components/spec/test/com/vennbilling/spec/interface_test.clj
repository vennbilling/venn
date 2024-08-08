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
          (let [{{:keys [parameters responses]} :post} route]
            (testing "parameter schema"
              (is (= (:body parameters) spec/identify-request-schema)))

            (testing "responses"
              (is (= responses {201 {:body spec/identify-response-schema}}))))

          (testing "handler function"
            (let [{{:keys [handler]} :post} route
                  request {:body-params {:identifier "a" :traits {} :billing_provider {}}}
                  resp (handler request)
                  {:keys [status body]} resp
                  {:keys [identifier traits billing-provider]} body]
              (is (= 201 status))
              (is (= "a" identifier))
              (is (= {} traits))
              (is (nil? billing-provider)))))))))


((:handler (:post (last spec/identify-route))) {:body-params {:identifier "a" :traits {} :billing_provider {}}})
