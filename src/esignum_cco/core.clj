(ns esignum-cco.core
  (:gen-class)
  (:require
    [cambium.core :as log]
    [overtone.at-at :as scheduler]))

(def my-pool (scheduler/mk-pool))

(defn conservazione []
  (log/info "Start ciclo conservazione ..."))

(defn -main
  [& args]
  (scheduler/interspaced 10000 conservazione my-pool))
