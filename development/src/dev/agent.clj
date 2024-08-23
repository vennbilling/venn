(ns dev.agent
  (:require
    [aero.core :refer [read-config]]
    [clojure.java.io :as io]
    [clojure.tools.namespace.repl :as repl]
    [com.vennbilling.agent.core :refer [settings]]
    [integrant.core :as ig]
    [integrant.repl :refer [prep go halt reset init]]
    [integrant.repl.state]
    [io.pedestal.log :as log]))


(def config-file (read-config (io/resource "../../../bases/agent/resources/agent/system.edn") {:profile :dev}))

(integrant.repl/set-prep! #(ig/prep (merge-with into config-file settings)))
(repl/set-refresh-dirs "../../../bases/agent/src")

(System/setProperty "logback.statusListenerClass" "ch.qos.logback.core.status.OnConsoleStatusListener")

(log/debug :msg "agent running in development mode")


;; Helpers to start and stop the agent
(comment
  (prep)
  (init)
  integrant.repl.state/config
  integrant.repl.state/system
  (go)
  (halt)
  (reset))
