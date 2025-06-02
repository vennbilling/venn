(ns com.vennbilling.healthcheck.interface-test
  (:require
   [clojure.test :as test :refer [deftest is testing]]
   [com.vennbilling.healthcheck.interface :as healthcheck]))

(deftest testing-handlers []
  (testing "simple healthcheck"
    (let [[status body] (healthcheck/simple-handler {})]
      (is (= :ok status))
      (is (= {} body)))))
