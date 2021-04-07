(ns esignum-cco.models.versamento
  (:require
    [clojure.java.jdbc :as jdbc]
    [esignum-cco.utils :as utils]
    [java-time :as time]
    [clj-pdf.core :as pdf-core]))

(def in-corso 0)
(def terminato-ok 1)
(def terminato-ko 2)

(defn insert! [datasource campionamento-map]
  (let [{:keys [sesscamp-id sesscamp-scatola-codice]} campionamento-map]
    (first (jdbc/insert! datasource :versamento {:versamento_campionamento_id sesscamp-id
                                                        :versamento_scatola_codice sesscamp-scatola-codice
                                                        :versamento_stato in-corso
                                                        :versamento_inizio (time/local-date)}
                    {:row-fn #(utils/format-output-keywords %)}))))


(defn create-report [versamento]
  (pdf-core/pdf
    [{:header "ESIGNUM - Sistema di conservazione CCO - Marno S.r.l."}
     [:image "resources/logo-marno.png"]
     [:paragraph {:style :bold :size 16 :family :helvetica :color [61 92 92] :align :center}  "RAPPORTO DI VERSAMENTO"]
     [:paragraph {:style :bold}  "DATI VERSAMENTO"]
     [:table {:header [{:backdrop-color [100 100 100]} "ELEMENTO" "DESCRIZIONE"]}
        [[:chunk "IDENTIFICATIVO VERSAMENTO"] [:phrase "baz"]]
        [[:phrase "INDICE DI VERSAMENTO"] [:phrase "idv.xml aloa bellissimo"]]
        [[:phrase "HASH IDV"] [:phrase "foo"]]]
     [:paragraph {:style :bold} "\n\nDATI CONSERVAZIONE"]]
    "e:/doc.pdf"))


;;    INIZIO VERSAMENTO 01/02/2021 05:08:31
;;    FINE VERSAMENTO 01/02/2021 09:43:34
;;    ESITO VERSAMENTO))
