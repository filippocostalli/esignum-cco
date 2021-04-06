(ns esignum-cco.config
  (:require
    [cprop.core :refer [load-config]]
    [cprop.source :as source]
    [esignum-cco.utils :as myutils]))

(def configuration (load-config))

(def db-regis (:db-regis-spec configuration))

(def pindex-file-root
    (if (myutils/is-linux?)
      (:pindex-root-lin configuration)
      (:pindex-root-win configuration)))

(def template-idc-cco (:cco-idc-template configuration))
(def template-idc-uda (:uda-idc-template configuration))

(def pindex-xsd-validator (:pindex-xsd-validator configuration))
