(ns mpisanko.duplicate-test
  (:require [clojure.test :refer :all]
            [mpisanko.duplicate :as duplicate]))

(deftest files-with-sizes
  (testing "directory with single file"
    (let [{:keys [result error]} (duplicate/files-with-sizes "test/resources")]
      (is (nil? error))
      (is (= [["test/resources/test.zip" 2642]] result)))))
