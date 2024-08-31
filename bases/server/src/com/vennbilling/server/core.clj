(ns com.vennbilling.server.core
  (:gen-class)
  (:require
    [clojure.java.io :as io]
    [com.vennbilling.customer.interface :as customer]
    [com.vennbilling.healthcheck.interface :as healthcheck]
    [com.vennbilling.system.interface :as system]
    [io.pedestal.log :as log]))


(def api-routes
  ["/v1"
   [customer/list-route
    customer/show-route

    healthcheck/simple-route]])


(def ^:const banner (slurp (io/resource "server/banner.txt")))
(def config-file (io/resource "server/system.edn"))


(defn -main
  [& _]
  (let [agent (system/create-http config-file :prod api-routes)]
    (system/start banner agent))
  (log/info :msg "venn server started successfully."))
