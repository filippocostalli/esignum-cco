(ns esignum-cco.models.documento
  (:require
    [clojure.java.jdbc :as jdbc]
    [esignum-cco.config :as config]
    [esignum-cco.utils :as utils]))

(def query-documenti-cartella
 "SELECT doc_id, doc_ordine, doc_classificazione, COALESCE(doc_categoria_descrizione, '') AS doc_classificazione_desc, doc_path, doc_datacreazione, doc_fronteretro, doc_npagina, doc_gruppo
  FROM documento LEFT JOIN doc_categoria_regis ON documento.doc_classificazione = doc_categoria_regis.doc_categoria_id
  WHERE doc_cartella_id = ? AND doc_orfano_tipo = 0 ORDER BY doc_id")

(defn select-by-cartella
  [datasource id]
  (jdbc/query datasource [query-documenti-cartella id] {:row-fn #(utils/format-output-keywords %)}))
