(ns dev.venn
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [clojure.tools.namespace.repl :as repl]
   [clojure.pprint :refer [pprint]]
   [com.vennbilling.customer.interface :as customer]
   [com.vennbilling.healthcheck.interface :as healthcheck]
   [com.vennbilling.spec.interface :as venn-spec]
   [com.vennbilling.system.interface :as system]
   [integrant.core :as ig]
   [integrant.repl :refer [prep go halt reset init]]
   [integrant.repl.state :as s]))

(def profile :dev)

;; In development, Venn is a single monolith service with all the routes
;; We also generate the system.edn on the by merging all the bases' configs
(def agent-config-file (io/resource "../../../bases/agent/resources/agent/system.edn"))
(def server-config-file (io/resource "../../../bases/server/resources/server/system.edn"))

(defn gen-service-config-file
  "Generate a config for the venn monolith and write it to system.edn"
  []
  (let [agent-config (aero/read-config agent-config-file {:profile profile})
        server-config (aero/read-config server-config-file {:profile profile})
        mono-config (merge server-config agent-config)]
    ;; This file is in .gitignore
    (pprint mono-config (io/writer "development/resources/system.edn"))
    (io/resource "system.edn")))

(def base-path "/v1")
(def agent-routes
  [venn-spec/identify-route])
(def server-routes
  [customer/list-route
   customer/show-route])
(def internal-routes
  [healthcheck/simple-route])
(def routes (conj [base-path] (apply conj agent-routes server-routes internal-routes)))

(integrant.repl/set-prep! #(-> (gen-service-config-file)
                               (system/init profile routes)
                               (ig/prep)))

(repl/set-refresh-dirs "../../../components")

;; Helpers to start and stop the monolith
(comment
  (gen-service-config-file)
  (prep)
  (eval integrant.repl.state/config)
  (init)
  integrant.repl.state/system
  (go)
  (halt)
  (reset))

