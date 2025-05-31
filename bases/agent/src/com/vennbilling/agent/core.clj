(ns com.vennbilling.agent.core
  (:gen-class)
  (:require
   [clojure.java.io :as io]
   [com.vennbilling.healthcheck.interface :as healthcheck]
   [com.vennbilling.logging.interface :as log]
   [com.vennbilling.spec.interface :as venn-spec]
   [com.vennbilling.system.interface :as system]))

(def api-routes
  ["/v1"
   [venn-spec/identify-route
    venn-spec/charge-route
    venn-spec/usage-route
    venn-spec/reverse-route
    healthcheck/simple-route]])

(def ^:const banner (slurp (io/resource "agent/banner.txt")))
(def config-file (io/resource "agent/system.edn"))

(defn -main
  [& _]
  ;; TODO: Pass profile as args or env var
  (let [agent (system/init config-file :prod api-routes)]
    (system/start banner agent))
  (log/info "venn agent started successfully."))
