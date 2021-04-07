(ns esignum-cco.models.versamento
  (:require
    [clojure.java.jdbc :as jdbc]
    [esignum-cco.utils :as utils]
    [java-time :as time]))

(def in-corso 0)
(def terminato-ok 1)
(def terminato-ko 2)

(defn insert! [datasource campionamento-map]
  (let [{:keys [sesscamp-id sesscamp-scatola-codice]} campionamento-map]
    (jdbc/insert! datasource :versamento {:versamento_campionamento_id sesscamp-id
                                          :versamento_scatola_codice sesscamp-scatola-codice
                                          :versamento_stato in-corso
                                          :versamento_inizio (time/local-date)})))
