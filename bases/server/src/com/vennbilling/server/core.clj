(ns com.vennbilling.server.core
  (:gen-class)
  (:require
   [clojure.java.io :as io]
   [com.vennbilling.http.interface :as http]
   [com.vennbilling.logging.interface :as log]
   [com.vennbilling.system.interface :as system]))

(def api-routes
  ["/v1"
   [http/server-routes
    http/healthcheck-routes]])

(def ^:const banner (slurp (io/resource "server/banner.txt")))
(def config-file (io/resource "server/system.edn"))

(defn -main
  [& _]
  (let [server (system/init config-file :prod api-routes)]
    (system/start banner server))
  (log/info "venn server started successfully."))
