(ns com.vennbilling.spec.interface-test
  (:require
   [clojure.test :as test :refer [deftest testing is]]
   [com.vennbilling.spec.interface :as spec]))

(deftest testing-handlers []
  (testing "IDENTIFY"
    (testing "with no traits or billing provider"
      (let [[status body] (spec/identify-handler {:body-params {:identifier "a"}})
            {:keys [identifier traits billing_provider]} body
            billing-provider billing_provider]
        (is (= :created status))
        (is (= "a" identifier))
        (is (= {} traits))
        (is (= {} billing-provider))))
    (testing "with traits but no billing provider"
      (let [[status body] (spec/identify-handler {:body-params {:identifier "a" :traits {:testing true}}})
            {:keys [identifier traits billing_provider]} body
            billing-provider billing_provider]
        (is (= :created status))
        (is (= "a" identifier))
        (is (= {:testing true} traits))
        (is (= {} billing-provider))))
    (testing "with all attributes"
      (let [[status body] (spec/identify-handler {:body-params {:identifier "a" :traits {:testing true} :billing_provider {:type "stripe", :identifier "b"}}})
            {:keys [identifier traits billing_provider]} body
            billing-provider billing_provider]
        (is (= :created status))
        (is (= "a" identifier))
        (is (= {:testing true} traits))
        (is (= {:type "stripe" :identifier "b"} billing-provider)))))

  (testing "CHARGE"
    (testing "with no properties"
      (let [[status body] (spec/charge-handler {:body-params {:customer_id "a" :event "Test Ran"}})]
        (is (= :created status))
        (is (= {} body))))
    (testing "with properties"
      (let [[status body] (spec/charge-handler {:body-params {:customer_id "a" :event "Test Ran" :properties {:quantity 1}}})]
        (is (= :created status))
        (is (= {} body)))))

  (testing "USAGE"
    (testing "with no properties"
      (let [[status body] (spec/usage-handler {:body-params {:customer_id "a" :event "Test Ran"}})]
        (is (= :created status))
        (is (= {} body))))
    (testing "with properties"
      (let [[status body] (spec/usage-handler {:body-params {:customer_id "a" :event "Test Ran" :properties {:quantity 1}}})]
        (is (= :created status))
        (is (= {} body)))))

  (testing "REVERSE"
    (testing "with no properties"
      (let [[status body] (spec/reverse-handler {:body-params {:customer_id "a" :event "Test Ran"}})]
        (is (= :created status))
        (is (= {} body))))
    (testing "with properties"
      (let [[status body] (spec/reverse-handler {:body-params {:customer_id "a" :event "Test Ran" :properties {:quantity 1}}})]
        (is (= :created status))
        (is (= {} body))))))
