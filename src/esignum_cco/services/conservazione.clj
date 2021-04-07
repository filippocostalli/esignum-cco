(ns esignum-cco.services.conservazione
  (:require
    [clojure.string :as s]
    [cambium.core :as log]
    [esignum-cco.config :as config]
    [esignum-cco.utils :as myutils]
    [esignum-cco.models.campionamento :as campionamento]
    [esignum-cco.models.versamento :as versamento]
    [esignum-cco.services.unisincro :as unisincro]
    [java-time :as time]
    [clojure.java.io :as io]))


(defn get-dest-dir [campionamento-id versamento-id]
  (let [root  config/pindex-file-root
        now   (time/local-date)
        year  (time/value (time/property now :year))
        month (time/value (time/property now :month-of-year))
        dest-dir (str root "/" year "/" month "/" campionamento-id "/" versamento-id)
        _ (io/make-parents (str dest-dir "/."))]
      (log/info (str "---- >" dest-dir))
      dest-dir))


(defn conservazione-campionamento! []
  (if-let [campionamento (campionamento/get-first config/db-regis)]
     (let [versamento (versamento/insert! config/db-esignum campionamento)
           _ (println versamento)
           dest-dir (get-dest-dir (:sesscamp-id campionamento) (:versamento-id versamento))
           campionamento-data (assoc campionamento :versamento versamento
                                                   :destination-dir dest-dir)]
      (try
          (-> campionamento-data
              (assoc :idc-path (unisincro/scatola->idc campionamento-data)))
              ;; inserisci in conservazione
              ;; firma UnitaDiArchiviazioneCartelleCliniche
              ;; crea rdv, update versamento,
        (catch Exception e
            ;; qui deve segnare nel db che tutto è andato una merda
            (log/error {:module "user-feedback"} e "Versamento fallito"))))))
