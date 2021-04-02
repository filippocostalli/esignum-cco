(ns esignum-cco.models.campionamento-test
  (:require [clojure.test :refer [are deftest testing use-fixtures is]]
            [esignum-cco.models.campionamento :as campionamento]
            [esignum-cco.config :as config]))


(def db-situs-spec (:db-situs-spec config/configuration))


(defn database-reset-fixture
  "Setup: drop all tables, creates new tables, insert values
   Teardown: drop all tables
  SQL schema code has if clauses to avoid errors running SQL code.
  Arguments:
  test-function - a function to run a specific test"
  [test-function]
  (SUT/create-database)
  (test-function)
  (SUT/delete-database))
