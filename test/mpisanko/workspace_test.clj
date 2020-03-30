(ns mpisanko.workspace-test
  (:require [clojure.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [mpisanko.workspace :as workspace])
  (:import (java.io File)))

(use-fixtures :once (fn [f]
                      (f)
                      (sh "rm" "-fr" "./tmp")))

(deftest create-test
  (testing "It creates a workspace directory in system temporary directory"
    (let [{:keys [result error]} (workspace/create)]
      (is (nil? error))
      (is (.startsWith result (str (System/getProperty "java.io.tmpdir") File/separatorChar "photos")))))

  (testing "When there is no system temp dir it falls back to current dir ./tmp"
    (let [temp-dir (System/getProperty "java.io.tmpdir")]
      (System/setProperty "java.io.tmpdir" "")
      (let [{:keys [result error]} (workspace/create)]
        (is (nil? error))
        (is (.startsWith result (str "./tmp/photos"))))
      (System/setProperty "java.io.tmpdir" temp-dir))))

(deftest unzip-test
  (testing "Using a known zip with 7 entries but many directories"
    (let [{:keys [result error]} (workspace/unzip "test/resources/test.zip" "./tmp/photos/test")]
      (is (nil? error))
      (is (= 7 result)))))
