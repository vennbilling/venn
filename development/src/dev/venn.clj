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
   [integrant.repl.state]))

(def config-file (io/resource "system.edn"))

;; TODO: We should leverage profile in juxt.aero more instead of having a system.edn file for the dev project
(def profile :dev)
(def base-path "/v1")

(def agent-routes
  [venn-spec/identify-route
   healthcheck/simple-route])

(def server-routes
  [customer/list-route
   customer/show-route])

(def all-routes (apply conj agent-routes server-routes))

;; In development, Venn is a single service with all the routes
(def routes (conj [base-path] all-routes))
(def venn (system/init config-file profile routes))

(integrant.repl/set-prep! #(ig/prep venn))
(repl/set-refresh-dirs "../../../components")

;; Helpers to start and stop the monolith
(comment
  (prep)
  (init)
  integrant.repl.state/config
  integrant.repl.state/system
  (go)
  (halt)
  (reset))
