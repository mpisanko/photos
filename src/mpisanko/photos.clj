(ns mpisanko.photos
  (:gen-class)
  (:require [clojure.java.io :as io]
            [mpisanko.workspace :as workspace]))

(defn- exit-with-error
  ([{:keys [message code]}]
   (exit-with-error message code))
  ([message code]
   (println message)
   (System/exit code)))

(defn- result-or-exit-with-error [{:keys [error result]}]
  (if result
    result
    (exit-with-error error)))

(defn- process [filepath]
  (try
   (if-not (.exists (io/as-file filepath))
     (exit-with-error (str "The file '" filepath "' does not exist") 2)
     (let [workspace (result-or-exit-with-error (workspace/create))
           unzipped (result-or-exit-with-error (workspace/unzip filepath workspace))
           file-sizes (result-or-exit-with-error (workspace/files-with-sizes workspace))
           files->sizes (into {} file-sizes)]
      (println "Workspace" workspace "unzipped" unzipped "entries" files->sizes)))
   (catch Throwable t
     (exit-with-error (str "Encountered problem " (.getMessage t)) 3))))

(defn -main
  "This is main entrypoint to the application"
  [& args]
  (if-let [filepath (first args)]
    (process filepath)
    (exit-with-error "Please provide path to ZIP archive with photos" 1)))

