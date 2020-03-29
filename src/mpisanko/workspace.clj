(ns mpisanko.workspace
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  (:import (java.io File FileOutputStream)
           (java.util UUID)
           (java.util.zip ZipInputStream)))

(defn- path [& segments]
  (str/join File/separatorChar segments))

(defn create
  "Create work directory in system's temporary directory"
  []
  (let [tmp (or
              (System/getProperty "java.io.tmpdir")
              (path "." "tmp"))
        workspace (path tmp "photos" (UUID/randomUUID))]
    (if (.mkdirs (File. workspace))
      {:result workspace}
      {:error {:message (str "No write access in temporary directory: " workspace)
               :code    4}})))

(defn unzip
  "Unzips the ZIP archive (filepath) into given location (directory)"
  [filepath directory]
  (with-open [zip-stream (ZipInputStream. (io/input-stream filepath))]
    (loop [zip-entry (.getNextEntry zip-stream)
           counter 1]
      (if-let [entry-name (some-> zip-entry .getName)]
        (let [out-name (path directory entry-name)]
          (if (.isDirectory zip-entry)
            (.mkdirs (File. (path directory entry-name)))
            (with-open [out-stream (FileOutputStream. (File. out-name))]
              (io/copy zip-stream out-stream)))
          (recur (.getNextEntry zip-stream) (inc counter)))
        {:result counter}))))
