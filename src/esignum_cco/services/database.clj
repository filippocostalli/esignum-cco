(ns esignum-cco.services.database
  (:require
    [clojure.string :as s]
    [cambium.core :as log]
    [esignum-cco.utils :as myutils]))


(defn snake-case->kebab-case
    [column]
    (when (keyword? column)
        (keyword (s/replace (name column) #"_" "-"))))

(defn format-output-keywords
  "Convert `output` keywords from snake_case to kebab-case."
  [output]
  (reduce-kv (fn [m k v]
               (assoc m (snake-case->kebab-case k) v))
             {}
             output))
