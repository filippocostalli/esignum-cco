(ns esignum-cco.services.unisincro
  (:require
    [clojure.string :as s]
    [digest :as digest]
    [clojure.java.io :as io]
    [pantomime.mime :as pm]
    [esignum-cco.utils :as utils]
    [esignum-cco.config :as config]
    [esignum-cco.models.documento :as documento]
    [esignum-cco.models.cartella :as cartella]
    [selmer.parser :as parser]
    [cambium.core :as log]
    [java-time :as time]))

(defn get-destination-dir [campionamento-id]
  (let [root  config/pindex-file-root
        now   (time/local-date)
        year  (time/value (time/property now :year))
        month (time/value (time/property now :month-of-year))
        uuid  (.toString (java.util.UUID/randomUUID))
        dest-dir (str root "/" year "/" month "/" campionamento-id "_" uuid)]
    (io/make-parents (str dest-dir "/."))
    dest-dir))

(defn sha512 [s]
  (digest/sha-256 (io/input-stream s)))

(defn mime-type [s]
  (pm/mime-type-of (io/input-stream s)))

(defn filename->unisincro [s]
    {:id (.toString (java.util.UUID/randomUUID))
     :file-name (subs s (+ (s/last-index-of s "/")))
     :hash (sha512 s)
     :hash-alg "SHA512"
     :mime-type (mime-type s)})

(defn doc->unisincro
  "Prende un record documento e torna una mappa con tutti i dati per unisincro: hash, mimetype etc"
  [d]
  (let [file-path (if (utils/is-windows?)
                      (s/replace (:doc-path d) "/home/osboxes/recepta/" "//nas202/CartellinaturaPisa/CartellinaturaPisa/")
                      (:doc-path d))]
     (assoc d :unisincro (filename->unisincro file-path))))

(defn get-documenti-unisincro
  "Prende un id cartella e ritorna la mappa dei documenti associati con tutti i dati per creare un unisincro"
  [cartella-id]
  (->> cartella-id
    (documento/select-by-cartella config/db-regis) ;;kebab-case
    (pmap #(doc->unisincro %))))

(defn cartella->unisincro-data
    "Prende un id cartella e restituisce la mappa completa per generarne un indice unisincro"
    [cartella-id]
    (-> {:datasource config/db-regis :cartella-id cartella-id}
        (cartella/select-by-id)
        (assoc :documenti (get-documenti-unisincro cartella-id))
        (assoc :data-creazione (time/format "yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX" (time/zoned-date-time)))))

(defn cartella->idc
  "Prende un id cartella, crea e salva l'indice e neritorna i dati unisincro (hash etc)"
  [dest-dir cartella-id]
  (let [dati-cartella (cartella->unisincro-data cartella-id)
        idc-string   (parser/render-file config/template-idc-cco dati-cartella)
        file-dest-name (str dest-dir "/PIndexCCO" cartella-id ".xml")]
      (println (str "Faccio " cartella-id))
      (spit file-dest-name idc-string)
      (filename->unisincro file-dest-name)))

(defn scatola->idc [campionamento-data]
  "Salva tutti gli idc della cartella e ritorna la lista dei dati per costruire unisincro dell'indice finale, che ritorna"
  (let [dest-dir (get-destination-dir (:sesscamp-id campionamento-data))]
      (->> campionamento-data
           :sesscamp-scatola-codice ;; codice scatoloa
           (cartella/select-by-scatola config/db-regis)
           (map #(:cartella-id %))
           (map (partial cartella->idc dest-dir))
           (assoc campionamento-data :idc-cartelle))))
       ;;(parser/render-file template-idc-uda))) ;; create così crea e salva e rotorna il path dell'idc
