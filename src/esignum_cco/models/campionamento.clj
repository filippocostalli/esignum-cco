(ns esignum-cco.models.campionamento
  (:require
    [clojure.java.jdbc :as jdbc]
    [esignum-cco.utils :as utils]))

(def stato-terminato-ok 1)
(def stato-conservazione-in-corso 4)
(def stato-conservazione-ok 3)
(def stato-conservazione-fallita 5)

(def not-nil? (complement nil?))

(def query-campionamento
  "SELECT sesscamp_id, sesscamp_scatola_codice, sesscamp_rapporto_path, sesscamp_inizio,
   utente_cognome, utente_nome, utente_codfiscale
   FROM sessione_campionamento, UTENTE
   WHERE sesscamp_esito = 1
   AND sesscamp_utente_id = utente_id
   ORDER BY sesscamp_id LIMIT 1")

(def query-metadata
  "SELECT cartella_scatola, asl_codice, asl_ipa, asl_descrizione FROM cartella, asl
   WHERE cartella_scatola = ? AND cartella_asl_id = asl_id LIMIT 1")

(defn get-first
  "Cerca il primo campionamento disponibile in stato 1 (terminato con successo).
  Se disponibile lo rotorna dopo aver reperito i metadata della azienda
  e avere aggiornato il record a stato 4 (conservazione in corso) utilizzando la stessa connessione"
  [datasource]
  (jdbc/with-db-connection [db-con datasource]
    (let [campionamento (first (jdbc/query db-con [query-campionamento] {:row-fn #(utils/format-output-keywords %)}))]
      (if (not-nil? campionamento)
        (do
          ;;(jdbc/update! db-con :sessione_campionamento {:sesscamp_esito stato-conservazione-in-corso} ["sesscamp_id = ?" (:sesscamp_id campionamento)])
          (assoc campionamento :metadata (first (jdbc/query db-con [query-metadata (:sesscamp-scatola-codice campionamento)]
                                                   {:row-fn #(utils/format-output-keywords %)}))))))))
