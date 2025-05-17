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

;; Helpers to start and stop the monolith
(comment
  (prep)
  (init)
  (go)
  s/system
  (halt)
  (reset))

;; Helpers for DB-related operations
(defn migratus-config
  [storage]
  (if (seq s/system)
    (let [k (keyword (str "storage.type/" storage))]
      (get-in s/system [k :migrations-config]))
    {}))

(def agent-db 'sqlite)
(def server-db 'postgresql)

(comment
  ;; venn agent
  (migratus/init (migratus-config agent-db))
  (migratus/create (migratus-config agent-db) "create-events" :edn)
  (migratus/pending-list (migratus-config agent-db))
  (migratus/migrate (migratus-config agent-db))
  (migratus/rollback (migratus-config agent-db))

  ;; venn server
  (migratus/init (migratus-config server-db))
  (migratus/create (migratus-config server-db) "migration_name" :edn)
  (migratus/pending-list (migratus-config server-db))
  (migratus/migrate (migratus-config server-db))
  (migratus/rollback (migratus-config server-db)))
