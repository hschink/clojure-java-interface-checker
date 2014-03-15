;;
;;  Copyright 2014 Hagen Schink <hagen.schink@gmail.com>
;;
;;  This file is part of clojure-java-interface-verifier.
;;
;;  clojure-java-interface-verifier is free software: you can redistribute it and/or modify
;;  it under the terms of the GNU Lesser General Public License as published by
;;  the Free Software Foundation, either version 3 of the License, or
;;  (at your option) any later version.
;;
;;  clojure-java-interface-verifier is distributed in the hope that it will be useful,
;;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;  GNU Lesser General Public License for more details.
;;
;;   You should have received a copy of the GNU Lesser General Public License
;;  along with clojure-java-interface-verifier.  If not, see <http://www.gnu.org/licenses/>.

(ns org.iti.clojureJavaInterfaceVerifier.Main
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]])
  (:use [clojure.pprint :only [pprint]]
        [org.iti.clojureJavaInterfaceVerifier.utils.Clojure :only [clojure-source-files read-clojure-methods-by-namespace]]
        [org.iti.clojureJavaInterfaceVerifier.utils.Java :only [java-source-files clojure-calls]]
        [org.iti.clojureJavaInterfaceVerifier.utils.Graph :only [check-clojure2java-function-mapping]])
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

(defn- print-result [modifications]
  (let [path (key modifications)
        modification (val modifications)]
    (println (str (.getType modification) ": " path))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
    ;; Execute program with options
    (let [clojure-source-files (clojure-source-files (:folder options))
          java-source-files (java-source-files (:folder options))
          clojure-functions (read-clojure-methods-by-namespace clojure-source-files)
          java2clojure-calls (doall (clojure-calls java-source-files))
          result (check-clojure2java-function-mapping clojure-functions java2clojure-calls)]
      (doall (map print-result result)))))