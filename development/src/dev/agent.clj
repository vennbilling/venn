(ns dev.agent
  (:require
    [aero.core :refer [read-config]]
    [clojure.java.io :as io]
    [clojure.tools.namespace.repl :as repl]
    [com.vennbilling.agent.core]
    [integrant.core :as ig]
    [integrant.repl :refer [prep go halt reset init]]
    [integrant.repl.state]))


(def config-file (io/resource "../../../bases/agent/resources/agent/system.edn"))
(def config (read-config config-file {:profile :dev}))

(integrant.repl/set-prep! #(ig/prep config))
(repl/set-refresh-dirs "../../../bases/agent/src")

(System/setProperty "logback.statusListenerClass" "ch.qos.logback.core.status.OnConsoleStatusListener")


;; Helpers to start and stop the agent
(comment
  (prep)
  (init)
  integrant.repl.state/config
  integrant.repl.state/system
  (go)
  (halt)
  (reset))
