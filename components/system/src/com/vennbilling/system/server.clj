(ns com.vennbilling.system.server
  (:require
   [integrant.core :as ig]
   [malli.core :as ma]

   [com.vennbilling.logging.interface :as log]
   [com.vennbilling.http.interface :as http])

  (:import
   [clojure.lang ExceptionInfo]))

(def ^:private ServerConfig
  :map)

(defn with-http-server
  [routes]
  {:system/server {:handler (ig/ref :http/handler)
                   :storage (ig/ref :system/storage)}

   :http/handler
   {:router (ig/ref :http/router)}

   :http/router
   {:routes routes}})

(defmethod ig/init-key :system/server
  [_ server-config]
  (try
    (let [valid (ma/coerce ServerConfig server-config)
          handler (:handler valid)
          undertow-opts (dissoc valid :handler)]

      {:undertow (http/serve undertow-opts handler)})

    (catch ExceptionInfo e
      (log/exception e)
      (throw (IllegalArgumentException. (ex-message e))))))

(defmethod ig/halt-key! :system/server
  [_ {:keys [undertow]}]
  (log/info "Stopping undertow server")
  (.stop undertow))

(defmethod ig/init-key :http/handler
  [_ {:keys [router]}]
  (http/new-ring-handler router))

(defmethod ig/init-key :http/router
  [_ {:keys [routes]}]
  (http/new-ring-router routes))
