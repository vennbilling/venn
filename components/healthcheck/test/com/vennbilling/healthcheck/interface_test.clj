(ns com.vennbilling.healthcheck.interface-test
  (:require
    [clojure.test :as test :refer [deftest is testing]]
    [com.vennbilling.healthcheck.interface :as healthcheck]
    [ring.util.http-status :as http-status]))


(deftest testing-routes []
  (testing "simple healthcheck"
    (let [[path route] healthcheck/simple-route]
      (testing "path"
        (is (= "/health" path)))

      (testing "http"
        (testing "GET request"
          (testing "handler function"
            (let [{{:keys [handler]} :get} route]
              (testing "response"
                (let [resp (handler {})
                      {:keys [status body]} resp]
                  (is (= http-status/ok status))
                  (is (= {} body)))))))))))
