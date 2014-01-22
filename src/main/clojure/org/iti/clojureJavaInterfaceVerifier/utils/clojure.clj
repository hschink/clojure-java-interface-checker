(ns org.iti.clojureJavaInterfaceVerifier.utils.Clojure
  (:use [org.iti.clojureJavaInterfaceVerifier.utils.File :only [get-source-files get-lines]]))
;  (:require org.iti.clojureJavaInterfaceVerifier.utils.Graph)
;  (:import [org.iti.clojureJavaInterfaceVerifier.utils.Graph Namespace Function]))

(defn- match-named-group [regex group line]
  (let [matcher (re-matcher regex line)]
    (if (re-find matcher) (.group matcher group) nil)))

(defn- is-namespace-string? [line]
  (match-named-group #"^\(ns (?<ns>\w(\w|\.)*)($|\))" "ns" line))

(defn- is-function-string? [line]
  (match-named-group #"^\(defn (?<fun>(\w|\-)*\?*)" "fun" line))

(defn- parse-clojure-namespace [line]
  (is-namespace-string? line))

(defn- func-params [line]
  (let [params (match-named-group #"^\(defn (?<fun>\w(\w|\-)*)\?* \[(?<args>((\w|\-)*\s*)*)\]" "args" line)]
    (if params (.split params " ") '())))

(defn- parse-clojure-function [line]
  (let [function (is-function-string? line)
        function-and-parameters (if function {function (func-params line)} nil)]
    function-and-parameters))

(defn- read-clojure-methods-by-namespace-from-file [lines]
  (loop [result {} 
         namespace :default
         [line & rest] (seq lines)]
    (if (nil? line)
      result
      (let [new-namespace (parse-clojure-namespace line)
            function-name (parse-clojure-function line)
            current-list (or (get result namespace) '())
            new-method-list (filter identity (cons function-name current-list))
            new-namespace (or new-namespace namespace)
            intermediate-result (merge result {new-namespace '()} {namespace new-method-list})]
        (recur intermediate-result new-namespace rest)))))

(defn read-clojure-methods-by-namespace [files]
  (let [clojure-files (filter #(-> % (.getName) (.endsWith "clj")) files)
        lines-by-files (reduce merge (map get-lines clojure-files))]
    (loop [result {}
           [lines-by-file & rest] (seq lines-by-files)]
      (if (nil? lines-by-file)
        result
        (let [file (key lines-by-file)
              lines (val lines-by-file)
              intermediate-result {file (read-clojure-methods-by-namespace-from-file lines)}]
          (recur (merge result intermediate-result) rest))))))

(defn clojure-source-files [path]
  (get-source-files path "clj"))