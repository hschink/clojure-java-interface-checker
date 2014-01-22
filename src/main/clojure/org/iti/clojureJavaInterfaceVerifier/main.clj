; java -cp "../clojure-1.5.1.jar;../jgrapht-0.8.3.jar;./dependencies/structureGraph-0.0.1.jar;./build/libs/clojure-java-interface-verifier-0.0.1.jar" "org.iti.clojureJavaInterfaceVerifier.Main" -f src

(ns org.iti.clojureJavaInterfaceVerifier.Main
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]])
  (:use [clojure.pprint :only [pprint]]
        [org.iti.clojureJavaInterfaceVerifier.utils.Clojure :only [clojure-source-files read-clojure-methods-by-namespace]]
        [org.iti.clojureJavaInterfaceVerifier.utils.Java :only [java-source-files]]
        [org.iti.clojureJavaInterfaceVerifier.utils.Graph :only [create-structure-graph]])
  (:gen-class))

(def cli-options
  [["-f" "--folder PATH" "Path to Java and Clojure source files"
    :parse-fn #(clojure.java.io/as-file %)
    :validate [#(and (.exists %) (.isDirectory %)) "Must be a valid path to a source directory"]]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["This is my program. There are many like it, but this one is mine."
        ""
        "Usage: program-name [options] action"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      ;;(not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    ;; Execute program with options
    (let [clojure-source-files (clojure-source-files (:folder options))
          java-source-files (java-source-files (:folder options))
          methods-by-namespaces (read-clojure-methods-by-namespace clojure-source-files)
          graph (create-structure-graph methods-by-namespaces)]
          (println)
          (pprint (.getIdentifiers graph)))))