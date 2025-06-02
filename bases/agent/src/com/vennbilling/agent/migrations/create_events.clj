(ns com.vennbilling.agent.migrations.create-events

  (:require [com.vennbilling.database.interface :as database]))

(def up-query {:create-table [:events :if-not-exists]
               :with-columns
               [[:uuid :text {:primary-key true}
                 :payload :text {:not-null true}
                 :timestamp :integer {:not-null true}
                 :flushed_at :integer {:not-null true}]]})

(def down-query {:drop-table :events})

(defn migrate-up
  [{:keys [db]}]
  (let [{:keys [datasource]} db]
    (database/exec! datasource up-query)))

(defn migrate-down
  [{:keys [db]}]
  (let [{:keys [datasource]} db]
    (database/exec! datasource down-query)))
