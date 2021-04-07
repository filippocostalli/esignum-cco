(ns esignum-cco.services.conservazione
  (:require
    [clojure.string :as s]
    [cambium.core :as log]
    [esignum-cco.config :as config]
    [esignum-cco.utils :as myutils]
    [esignum-cco.models.campionamento :as campionamento]
    [esignum-cco.models.versamento :as versamento]
    [esignum-cco.services.unisincro :as unisincro]))


(defn conservazione-campionamento! []
  (if-let [campionamento (campionamento/get-first config/db-regis)]
    (let [new-versamento (versamento/insert! config/db-esignum campionamento)]
      (try
          (-> campionamento
              (unisincro/scatola->idc)) ;; tutti idc cartelle e idc one creati efottutamente salvati dova cazzo debbono
              ;; inserisco il path nel versamento
        (catch Exception e (println "Cagata:\n" e)))))) ;; qui deve segnare nel db che tutto è andato una merda
