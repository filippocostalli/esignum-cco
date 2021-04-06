(ns esignum-cco.services.database
  (:require
    [clojure.string :as s]
    [cambium.core :as log]
    [esignum-cco.config :as config]
    [esignum-cco.utils :as myutils]
    [esignum-cco.models.campionamento :as campionamento]
    [esignum-cco.services.unisincro :as unisincro]))

(def db-regis (:db-regis-spec config/configuration))

(def not-nil? (complement nil?))

(defn conservazione-campionamento!
  (let [campionamento (campionamento/get-first db-regis)]
    (try
      (if (not-nil? campionamento)
        (-> campionamento
            (unisincro/scatola->idc))) ;; tutti idc cartelle e idc one creati efottutamente salvati dova cazzo debbono
            ;; inserisco il path nel versamento del cazzo
            ;; ritorna filepath udc -> inserire
      (catch Exception e (println "Cagata:\n" e))))) ;; qui deve segnare nel db che tutto è andato una merda
