(ns esignum-cco.models.cartella
  (:require
    [clojure.java.jdbc :as jdbc]
    [esignum-cco.config :as config]
    [esignum-cco.utils :as utils]))

(def query-operatore "SELECT operatore_nome, operatore_cognome, operatore_codfiscale FROM operatore WHERE operatore_id = ?")

(def query-cartella "SELECT
  cartella_id, cartella_barcode, cartella_datascansione, cartella_datatipizzazione,
  cartella_sdo_codice, cartella_sdo_anno, cartella_dataricovero,
  cartella_presidio_codice, cartella_presidio_descrizione, cartella_reparto_descrizione,
  cartella_codicefiscale, cartella_cognome, cartella_nome,
  cartella_op_tipizzazione_id, cartella_op_scansione_id
  FROM cartella WHERE cartella_id = ? ")

(defn select-by-id [m]
  (jdbc/with-db-connection [db-con (:datasource m)]
    (let [cartella (first (jdbc/query (:datasource m) [query-cartella (:cartella-id m)]
                             {:row-fn #(utils/format-output-keywords %)}))]
      (if (utils/not-nil? cartella)
        (-> cartella
            (assoc  :operatore-scansione (first (jdbc/query db-con [query-operatore (:cartella-op-scansione-id cartella)]
                                                    {:row-fn #(utils/format-output-keywords %)})))
            (assoc  :operatore-tipizzazione (first (jdbc/query db-con [query-operatore (:cartella-op-tipizzazione-id cartella)]
                                                    {:row-fn #(utils/format-output-keywords %)}))))))))

(defn select-by-scatola
  [datasource id]
  (jdbc/query datasource ["SELECT cartella_id FROM cartella WHERE cartella_scatola = ? ORDER BY cartella_id LIMIT 5" id]
    {:row-fn #(utils/format-output-keywords %)}))
