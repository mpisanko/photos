(ns mpisanko.workspace
  (:require [clojure.java.io :as io])
  (:import (java.util UUID)
           (java.util.zip ZipInputStream)))

(defn create
  "Create work directory in system's temporary directory"
  []
  (let [tmp (if (seq (System/getProperty "java.io.tmpdir"))
              (System/getProperty "java.io.tmpdir")
              "./tmp")
        workspace (io/file tmp "photos" (str (UUID/randomUUID)))]
    (if (.mkdirs workspace)
      {:result (.getPath workspace)}
      {:error {:message (str "No write access in temporary directory: " workspace)
               :code    4}})))

(defn unzip
  "Unzips the ZIP archive (filepath) into given location (directory)"
  [filepath directory]
  (with-open [zip-stream (ZipInputStream. (io/input-stream filepath))]
    (loop [zip-entry (.getNextEntry zip-stream)
           counter 0]
      (if-let [entry-name (some-> zip-entry .getName)]
        (let [dir? (.isDirectory zip-entry)]
          (if dir?
            (.mkdirs (io/file directory entry-name))
            (io/copy zip-stream (io/file directory entry-name)))
          (recur (.getNextEntry zip-stream) (if dir? counter (inc counter))))
        {:result counter}))))

