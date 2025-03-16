(ns dev.venn
  (:require
   [clojure.java.io :as io]
   [clojure.tools.namespace.repl :as repl]
   [com.vennbilling.customer.interface :as customer]
   [com.vennbilling.healthcheck.interface :as healthcheck]
   [com.vennbilling.spec.interface :as venn-spec]
   [com.vennbilling.system.interface :as system]
   [integrant.core :as ig]
   [integrant.repl :refer [prep go halt reset init]]
   [integrant.repl.state :as s]
   [migratus.core :as migratus]))

;; In development, Venn is a single monolith service with all the routes across all services joined togehter
(def profile :development)
(def mono-config (io/resource "system.edn"))
(def base-path "/v1")
(def routes
  [base-path
   [venn-spec/identify-route
    customer/list-route
    customer/show-route
    healthcheck/simple-route]])

;; Configure Integrant REPL
(integrant.repl/set-prep! #(-> mono-config
                               (system/init profile routes)
                               (ig/prep)))

(repl/set-refresh-dirs "../../../components")

(def migratus-cfg (if-let [db (-> s/system
                                  (:db/server))]
                    db
                    {}))

;; Config states for easy inspection
(comment
  s/system
  migratus-cfg)

;; Helpers to start and stop the monolith
(comment
  (prep)
  (init)
  (go)
  (halt)
  (reset))

;; Helpers for DB-related operations
(comment
  (migratus/init migratus-cfg)
  ;;(migratus/create migratus-cfg "migration_name")
  (migratus/pending-list migratus-cfg)
  (migratus/migrate migratus-cfg)
  (migratus/rollback migratus-cfg))

