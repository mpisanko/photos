(ns mpisanko.photos
  (:gen-class)
  (:require [clojure.java.io :as io]
            [mpisanko.workspace :as workspace]
            [mpisanko.duplicate :as duplicate]
            [clojure.string :as str]))

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

(defn- strip-root [root filepath]
  (if (.startsWith filepath root) (subs filepath (inc (count root))) filepath))

(defn- report [workspace unzipped dupes]
  (println (format "Extracted %s files into %s. Found following duplicates:\n\n%s"
                   unzipped workspace (str/join "\n\n\n" (map
                                                           (partial str/join "\n")
                                                           dupes)))))

(defn- process [filepath]
  (try
    (if-not (.exists (io/file filepath))
      (exit-with-error (str "The file '" filepath "' does not exist") 2)
      (let [workspace (result-or-exit-with-error (workspace/create))
            unzipped (result-or-exit-with-error (workspace/unzip filepath workspace))
            file-sizes (result-or-exit-with-error (duplicate/files-with-sizes workspace))
            candidates (result-or-exit-with-error (duplicate/candidates file-sizes))
            dupes (remove nil? (mapcat duplicate/find' candidates))
            dupes-stripped-workspace (map #(map (partial strip-root workspace) %) dupes)]
        (report workspace unzipped dupes-stripped-workspace)))
    (catch Throwable t
      (exit-with-error (.getMessage t) 3))))

(defn -main
  "This is main entrypoint to the application"
  [& args]
  (if-let [filepath (first args)]
    (process filepath)
    (exit-with-error "Please provide path to ZIP archive with photos" 1)))

