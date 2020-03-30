(ns mpisanko.duplicate
  (:require [clojure.java.io :as io]))

(defn- file-and-size [file]
  [(.getPath file) (.length file)])

(defn- files-seq [directory]
  (let [contents (.listFiles directory)
        files (remove #(.isDirectory %) contents)
        dirs (filter #(.isDirectory %) contents)]
    (lazy-seq
      (concat
        files
        (mapcat files-seq dirs)))))

(defn files-with-sizes
  "Returns a lazy sequence of vectors containing every filename and its byte size in given root directory.
   Eg: (lazy-seq [\"/tmp/a/file-a\" 123] [\"/tmp/b/file-b\" 234])"
  [directory]
  {:result (map file-and-size (files-seq (io/file directory)))})
