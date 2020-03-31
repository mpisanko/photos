(ns mpisanko.duplicate
  (:require [clojure.java.io :as io]
            [clojure.set :as set]))

(defn- file-and-size [file]
  [(.getPath file) (.length file)])

(defn- file-sequence [root-directory]
  (let [contents (.listFiles root-directory)
        files (remove #(.isDirectory %) contents)
        dirs (filter #(.isDirectory %) contents)]
    (lazy-seq
      (concat
        files
        (mapcat file-sequence dirs)))))

(defn files-with-sizes
  "Returns a lazy sequence of vectors containing every filename and its byte size in given root directory.
   Eg: (lazy-seq [\"/tmp/a/file-a\" 123] [\"/tmp/b/file-b\" 234])"
  [directory]
  {:result (->> (io/file directory)
                file-sequence
                (map file-and-size))})

(defn candidates
  "Returns a map of sizes to filepaths"
  [files-with-sizes]
  {:result (group-by second (filter (comp pos? second) files-with-sizes))})

(defn- pairs [coll]
  (set
    (for [x coll
          y coll
          :when (not= x y)]
      #{x y})))

(defn- bytes-read-into-buffer [buffer-size [stream buffer]]
  [(.read stream buffer 0 buffer-size) buffer])

(defn- same-content? [byte-length [p1 p2]]
  (with-open [stream1 (io/input-stream p1)
              stream2 (io/input-stream p2)]
    (let [buffer-size 8192
          buffers [(byte-array buffer-size) (byte-array buffer-size)]]
      (loop [bytes-read 0]
        (let [offset-buffers (map (partial bytes-read-into-buffer buffer-size) (map vector [stream1 stream2] buffers))
              min-length (apply min (map first offset-buffers))]
          (if (neg? min-length)
            (= byte-length bytes-read)
            (when (apply = (map (comp (partial take min-length) second) offset-buffers))
              (recur (+ bytes-read min-length)))))))))

(defn- de-dupe [dupes]
  (let [by-first (reduce-kv
                  (fn [acc k v]
                    (conj acc (into #{k} (map second v))))
                  []
                  (group-by first dupes))
        parts (partition-all 2 (sort-by (juxt first count) (map (comp sort vec) by-first)))]
    (reduce
      (fn [acc [a b]]
        (if (set/superset? (set a) (set b))
          (conj acc a)
          (conj acc a b)))
      []
      parts)))

(defn find'
  "Find duplicate files in a group of same sized files"
  [[byte-length candidates]]
  (let [dupes (filter
                (partial same-content? byte-length)
                (map (comp sort vec) (pairs candidates)))]
    (de-dupe dupes)))
