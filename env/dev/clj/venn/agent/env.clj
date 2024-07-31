(ns venn.agent.env
  (:require
    [clojure.tools.logging :as log]))


(def ^:const banner (slurp "resources/banner.txt"))


(def defaults
  {:init       (fn []
                 (log/info "venn agent starting using the development or test profile"))
   :start      (fn []
                 (log/infof "\n\n%s" banner)
                 (log/info "venn agent started successfully using the development or test profile"))
   :stop       (fn []
                 (log/info "venn agent has shut down successfully"))
   :opts {:profile :dev}})
