(defproject esignum-cco "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cambium/cambium.core "0.9.3"]
                 [cambium/cambium.codec-simple "0.9.3"]
                 [cambium/cambium.logback.core "0.4.3"]
                 [overtone/at-at "1.2.0"]
                 [cprop "0.1.16"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.postgresql/postgresql "42.2.9"]
                 [digest "1.4.10"]
                 [com.novemberain/pantomime "2.11.0"]
                 [selmer "1.12.24"]
                 [clojure.java-time "0.3.2"]
                 [clj-xml-validation "1.0.2"]]
  :main ^:skip-aot esignum-cco.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
