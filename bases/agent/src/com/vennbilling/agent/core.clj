(ns com.vennbilling.agent.core
  (:gen-class)
  (:require
    [clojure.java.io :as io]
    [com.vennbilling.customer.interface :as customer]
    [com.vennbilling.healthcheck.interface :as healthcheck]
    [com.vennbilling.spec.interface :as venn-spec]
    [com.vennbilling.system.interface :as system]
    [io.pedestal.log :as log]))


(def api-routes
  ["/v1"
   [venn-spec/identify-route
    customer/list-route
    customer/show-route

    healthcheck/simple-route]])


(def ^:const banner (slurp (io/resource "agent/banner.txt")))
(def config-file (io/resource "agent/system.edn"))


(defn -main
  [& _]
  ;; TODO: Pass profile as args
  (let [agent (system/create-http config-file :prod api-routes)]
    (system/start banner agent))
  (log/info :msg "venn agent started successfully."))
