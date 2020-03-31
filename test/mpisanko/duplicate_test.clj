(ns mpisanko.duplicate-test
  (:require [clojure.test :refer :all]
            [clojure.math.combinatorics :as reference]
            [mpisanko.duplicate :as duplicate]))

(deftest files-with-sizes
  (testing "directory with single file"
    (let [{:keys [result error]} (duplicate/files-with-sizes "test/resources")]
      (is (nil? error))
      (is (= [["test/resources/test.zip" 2642]
              ["test/resources/duplicates/sf.jpg" 7058]
              ["test/resources/duplicates/starfish.jpg" 8155]
              ["test/resources/duplicates/sf-super-duper.jpg" 7058]
              ["test/resources/duplicates/low-tide.jpg" 7058]
              ["test/resources/duplicates/lo-tide.jpg" 7058]
              ["test/resources/duplicates/low-tide-dupe.jpg" 7058]
              ["test/resources/duplicates/sf-dupe.jpg" 7058]]
             result)))

    (let [{:keys [result error]} (duplicate/files-with-sizes "resources")]
      (is (nil? error))
      (is (= [["resources/.keep" 0]] result)))))

(deftest candidates-test
  (testing "returns groups of files of the same size"
    (is (= {123 ["/a/b" "/a/c"]
            234 ["/d/e"]}
           (:result (duplicate/candidates [["/a/b" 123] ["/d/e" 234] ["/a/c" 123]])))))

  (testing "discards empty files"
    (is (empty? (:result (duplicate/candidates [["/dev/null" 0]]))))))

(deftest pairs-test
  (testing "pairs returns a list of different pairs of elements of the collection"
    (is (= #{#{:a :b} #{:a :c} #{:a :d} #{:b :c} #{:b :d} #{:c :d}}
           (#'duplicate/pairs [:a :b :c :d]))))

  (testing "against a reference implementation"
    (let [coll [:a :b :c :d :e :f :g :h :i :j :k :l :m :n]]
      (is (= (set (map set (reference/combinations coll 2)))
             (#'duplicate/pairs coll))))))

(deftest find-single-test
  (testing "just two identical files"
    (is (empty?
          (#'duplicate/find-single [7058 ["test/resources/duplicates/low-tide.jpg"
                                  "test/resources/duplicates/sf-super-duper.jpg"]]))))
  (testing "two identical files repeated three times"
    (is (= (map sort
                [["test/resources/duplicates/lo-tide.jpg"
                  "test/resources/duplicates/low-tide.jpg"
                  "test/resources/duplicates/low-tide-dupe.jpg"]
                 ["test/resources/duplicates/sf-dupe.jpg"
                  "test/resources/duplicates/sf.jpg"
                  "test/resources/duplicates/sf-super-duper.jpg"]])
           (#'duplicate/find-single [7058 ["test/resources/duplicates/low-tide.jpg"
                                   "test/resources/duplicates/lo-tide.jpg"
                                   "test/resources/duplicates/low-tide-dupe.jpg"
                                   "test/resources/duplicates/sf.jpg"
                                   "test/resources/duplicates/sf-dupe.jpg"
                                   "test/resources/duplicates/sf-super-duper.jpg"]])))))