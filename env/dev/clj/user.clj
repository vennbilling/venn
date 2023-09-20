(ns user
  (:require [aero.core :refer [read-config]]
            [clojure.tools.logging :as log]
            [clojure.tools.namespace.repl :as repl]
            [clojure.java.io]
            [integrant.core :as ig]
            [integrant.repl :refer [prep go halt reset]]
            [integrant.repl.state]

            [venn.agent.server]
            [venn.agent.api.routes]))


(defmethod ig/init-key :system/env [_ env]
  (log/debugf "Starting system using environment %s" env))

(defmethod aero.core/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defmethod aero.core/reader 'ig/refset
  [_ _ value]
  (ig/refset value))

(def config (read-config (clojure.java.io/resource "system.edn") {:profile :dev}))

(integrant.repl/set-prep! #(ig/prep config))
(repl/set-refresh-dirs "src/clj" "env/dev/clj")


;; Helpers to start and stop the system
(comment
  (prep)
  integrant.repl.state/config
  (go)
  (halt)
  (reset))
