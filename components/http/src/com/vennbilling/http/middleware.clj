(ns com.vennbilling.http.middleware)

(defn wrap-storage
  "Ring middleware that injects database connections into the request.

   Takes a handler and a storage registry, and returns a new handler
   that adds the database connections to each request.

   The connections will be available in the request under:
   - :db-connections - Map of all database connections
  "
  [handler {:keys [db-connections]}]
  (fn [request]
    (let [enhanced-request (-> request
                               (assoc :db-connections db-connections))]
      (handler enhanced-request))))
