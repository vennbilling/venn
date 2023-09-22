(ns venn.agent.models.record)

(defprotocol Serialization
  (serialize [record]))

(defprotocol Validation
  (validate [record])
  (errors [record]))
