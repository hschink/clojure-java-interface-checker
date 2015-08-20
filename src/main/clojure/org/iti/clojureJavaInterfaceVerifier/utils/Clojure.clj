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

(ns org.iti.clojureJavaInterfaceVerifier.utils.Clojure
  (:use [clojure.pprint :only [pprint]]
        [org.iti.clojureJavaInterfaceVerifier.utils.File :only [get-source-files get-lines]])
  (:require org.iti.clojureJavaInterfaceVerifier.utils.Graph)
  (:import [org.iti.clojureJavaInterfaceVerifier.utils.Graph File Namespace Function Parameter]))

(defn- match-named-group [regex group line]
  (let [matcher (re-matcher regex line)]
    (if (re-find matcher) (.group matcher group) nil)))

(defn- is-namespace-string? [line]
  (match-named-group #"^\(ns (?<ns>\w(\w|\.)*)($|\))" "ns" line))

(defn- is-function-string? [line]
  (match-named-group #"^\(defn (?<fun>(\w|\-)*\?*)" "fun" line))

(defn- clojure-namespace [line]
  (let [namespace (is-namespace-string? line)]
    (if namespace (Namespace. namespace []) nil)))

(defn- func-params [line]
  (let [params (match-named-group #"^\(defn (?<fun>\w(\w|\-)*)\?* \[(?<args>((\w|\-|\&)*\s*)*)\]" "args" line)]
    (if params (let [param-names (.split params " ")
                     param-count (count param-names)
                     optional-param (take param-count (mapcat #(if (= % "&") '(true) '(false)) param-names))
                     params-without-keyword (filter #(not= "&" %) param-names)
                     params-with-optional-flag (map vector params-without-keyword optional-param)]
                 (map #(Parameter. (first %) (last %)) params-with-optional-flag))
      '())))

(defn- clojure-function [line]
  (let [function (is-function-string? line)]
    (if function (Function. function (func-params line)) nil)))

(defn- structure-element [line]
  (let [namespace (clojure-namespace line)
        func (clojure-function line)]
    (or namespace func)))

(defn- merge-clojure-ns-funcs [list structure-element]
  (let [ns (last list)
        structure-element-type (type structure-element)
        is-ns (= Namespace structure-element-type)
        is-func (= Function structure-element-type)
        new-list (if is-ns
                   (conj list structure-element)
                   (conj (vec (drop-last list)) (assoc ns :functions (conj (.functions ns) structure-element))))]
    new-list))

(defn- read-clojure-methods-by-namespace-from-file [lines]
  (let [ns-and-funcs (filter identity (map structure-element lines))
        namespaces (reduce merge-clojure-ns-funcs [(Namespace. :default [])] ns-and-funcs)]
    (filter #(not (and (= :default (.name %)) (empty? (.functions %)))) namespaces)))

(defn- file-element [lines-by-file]
  (let [file-name (.getName (key lines-by-file))
        lines (val lines-by-file)
        namespaces (read-clojure-methods-by-namespace-from-file lines)]
    (File. file-name namespaces)))

(defn read-clojure-methods-by-namespace [files]
  (let [clojure-files (filter #(-> % (.getName) (.endsWith "clj")) files)
        lines-by-files (reduce merge (map get-lines clojure-files))
        result (map file-element lines-by-files)]
    result))

(defn clojure-source-files [path]
  (get-source-files path "clj"))