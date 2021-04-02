(ns esignum-cco.core
  (:gen-class)
  (:require
    [cambium.core :as log]
    [overtone.at-at :as scheduler]
    esignum-cco.models.campionamento :as campionamento))


(def db-regis (:db-regis-spec config/configuration))

(def my-pool (scheduler/mk-pool))

(defn conservazione []
  (log/info "Start ciclo conservazione ..."))

(defn -main
  [& args]
  (scheduler/interspaced 10000 conservazione my-pool))
