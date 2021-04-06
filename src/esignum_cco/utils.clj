(ns esignum-cco.utils
  (:require
   [cambium.core :as log]
   [clojure.string :as str]
   [java-time :as time]))


(def not-nil? (complement nil?))

(defn string->date [s]
  (if s
    (.parse
      (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss") s)
    nil))

(defn date->string [s]
  (.format
    (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss") s))

(defn str->integer
  [s]
  (if (every? #(Character/isDigit %) s)
    (Integer/parseInt s)
    0))


(defn strings->integers
  [xs]
  (if (sequential? xs)
    (map str->integer xs)
    (str->integer xs)))

(defmacro with-err-str
    "Evaluates exprs in a context in which *err* is bound to a fresh
    StringWriter.  Returns the string created by any nested printing
    calls."
    [& body]
    `(let [s# (new java.io.StringWriter)]
         (binding [*err* s#]
           ~@body
           (str s#))))

(defn despace [m]
  (zipmap (map #(keyword (clojure.string/replace (name %) " " "_")) (keys m))
      (vals m)))

(defn stacktrace
  [exception]
  (log/error  exception "Email notification failed"))

;; ------------- detect OS
(def features
   { :name (System/getProperty "os.name"),
     :version (System/getProperty "os.version"),
     :arch (System/getProperty "os.arch")})

(defn get-os []
   (str/lower-case (System/getProperty "os.name")))

(defn is-windows? []
   (-> (get-os)
       (str/includes? "windows")))

(defn is-linux? []
   (-> (get-os)
       (str/includes? "linux")))

(defn is-mac? []
   (-> (get-os)
       (str/includes? "mac")))

;; -- snak kebab
(defn snake-case->kebab-case
    [column]
    (when (keyword? column)
        (keyword (str/replace (name column) #"_" "-"))))

(defn format-output-keywords
  "Convert `output` keywords from snake_case to kebab-case."
  [output]
  (reduce-kv (fn [m k v]
               (assoc m (snake-case->kebab-case k) v))
             {}
             output))

;; Date

(defn sql-date->xsd-datetime
   [d]
   (let [local-d (time/local-date d)
         year    (time/value (time/property local-d :year))
         month   (time/value (time/property local-d :month-of-year))
         day     (time/value (time/property local-d :day-of-month))]
     (->>
        (time/zoned-date-time year month day)
        (time/format "yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX"))))
