(ns venn.agent.models.validation)

(defprotocol Validation
  (validate [record])
  (errors [record]))
