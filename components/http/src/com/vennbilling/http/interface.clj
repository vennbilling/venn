(ns com.vennbilling.http.interface
  (:require [com.vennbilling.http.ring :as ring]
            [com.vennbilling.http.undertow :as undertow]))

(defn serve
  "Starts up an HTTP server with a io.undertow config and ring handler. Returns an undertow server"
  [config handler]
  (undertow/serve config handler))

(defn new-ring-handler
  "Returns a ring handler function that wraps a ring router and can be used with an HTTP server."
  ([router]
   (ring/new-handler router))

  ([router storage]
   (ring/new-handler router storage)))

(defn new-ring-router
  "Returns a new ring router that can be used with an ring handler"
  ([routes]
   (new-ring-router "" routes []))
  ([routes middleware]
   (new-ring-router "" routes middleware))
  ([base-path routes middleware]
   (ring/new-router base-path routes middleware)))
