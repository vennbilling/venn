(ns com.vennbilling.logging.interface
  (:require
   [io.pedestal.log :as log]

   [malli.core :as m]))

(def ^:private ExceptionSchema
  (m/-simple-schema
   {:type :exception-info
    :pred #(instance? clojure.lang.ExceptionInfo %)
    :type-properties {:error/message "Must be a clojure.lang.ExceptionInfo"}}))

(defn warn
  "Logs a warning message."
  [msg]
  (log/warn :msg msg))

(defn info
  "Logs an info message."
  [msg]
  (log/info :msg msg))

(defn exception
  "Logs a clojure.lang.ExceptionInfo."
  [e]

  (if (m/validate ExceptionSchema e)
    (log/error :exception e)
    (exception (ex-info "Unsupported exception logged" {:name (.getName (class e)) :message (.getMessage ^Throwable e)}))))
