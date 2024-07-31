(ns venn.agent.env
  (:require
    [clojure.tools.logging :as log]))


(def ^:const banner (slurp "resources/banner.txt"))


(def defaults
  {:init       (fn []
                 (log/info "venn agent starting"))
   :start      (fn []
                 (log/infof "\n\n%s" banner)
                 (log/info "venn agent started successfully"))
   :stop       (fn []
                 (log/info "venn agent has shut down successfully"))
   :opts {:profile :prod}})
