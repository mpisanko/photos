(ns mpisanko.photos-test
  (:require [clojure.test :refer :all]
            [mpisanko.photos :as photos]))

(deftest process-test
  (testing "passing a non zip calls exit with error"
    (let [exit-values (atom {})]
     (with-redefs [photos/exit-with-error (fn [msg c]
                                              (reset! exit-values {:msg msg :code c}))]
       (#'photos/process "CHALLENGE.md")
       (is (nil? (-> exit-values deref :code)))
       (is (nil? (-> exit-values deref :msg)))))))

(deftest strip-root-test
  (is (= "bar/baz"
         (#'photos/strip-root "/tmp/photos/123" "/tmp/photos/123/bar/baz")))
  (is (= "bar/baz"
         (#'photos/strip-root "/tmp/photos/123" "bar/baz"))))
