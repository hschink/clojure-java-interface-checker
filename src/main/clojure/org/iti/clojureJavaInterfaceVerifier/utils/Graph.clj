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

(ns org.iti.clojureJavaInterfaceVerifier.utils.Graph
  (:require org.iti.clojureJavaInterfaceVerifier.edges)
  (:use [clojure.pprint :only [pprint]]
        [clojure.reflect :only [reflect]]
        [org.iti.clojureJavaInterfaceVerifier.Nodes :only [element]])
  (:import (org.iti.structureGraph StructureGraph)
           (org.iti.structureGraph.comparison StatementStructureGraphComparer)
           (org.iti.structureGraph.comparison.result Type)
           (org.jgrapht.graph SimpleDirectedGraph DefaultEdge)
           (org.iti.clojureJavaInterfaceVerifier.edges HasParameter HasMethod HasNamespace)))

(defrecord Function [name parameters])

(defrecord Namespace [name functions])

(defrecord File [name namespaces])

(defn- add-parameter-to-graph [graph source mandatory parameter]
  (let [parameter-element (element parameter mandatory)]
    (do
      (.addVertex graph parameter-element)
      (.addEdge graph source parameter-element (HasParameter.)))))

(defn- add-function-to-graph [graph source mandatory function]
  (let [element (element (:name function) false)]
    (do
       (.addVertex graph element)
       (.addEdge graph source element (HasMethod.))
       (doall
         (map (partial add-parameter-to-graph graph element mandatory) (:parameters function))))))

(defn- add-namespace-to-graph [graph file mandatory namespace]
  (let [ns-name (:name namespace)
        is-default-ns (= :default ns-name)
        ns-element (if is-default-ns nil (element (:name namespace) false))
        source (if is-default-ns file ns-element)]
    (do
      (if (not is-default-ns)
        (do
          (.addVertex graph ns-element)
          (if-not (nil? file) (.addEdge graph file ns-element (HasNamespace.)))))
      (doall
        (map (partial add-function-to-graph graph source mandatory) (:functions namespace))))))

(defn- add-file-to-graph [graph file mandatory]
  (let [file-element (element (:name file) false)]
    (do
      (.addVertex graph file-element)
      (doall
        (map (partial add-namespace-to-graph graph file-element mandatory) (:namespaces file))))))

(defn- add-element-to-graph [graph mandatory element]
  (let [type-of-element (type element)]
    (cond
      (= type-of-element Namespace) (add-namespace-to-graph graph nil mandatory element)
      (= type-of-element File) (add-file-to-graph graph element mandatory))))

(defn- add-elements-to-graph [graph files mandatory]
  (doall
    (map (partial add-element-to-graph graph mandatory) files)))

(defn create-structure-graph-with-mandatory-functions [methods-by-namespace mandatory]
  (let [graph (SimpleDirectedGraph. DefaultEdge)]
    (add-elements-to-graph graph methods-by-namespace mandatory)
    (StructureGraph. graph)))

(defn create-structure-graph [methods-by-namespace]
  (create-structure-graph-with-mandatory-functions methods-by-namespace false))

(defn- compare-clojure-java-graphs [clojure-graph java-graph]
  (let [comparer (StatementStructureGraphComparer.)
        clojure-structure-graph (create-structure-graph-with-mandatory-functions clojure-graph true)
        java-structure-graph (create-structure-graph java-graph)
        result (.compare comparer java-structure-graph clojure-structure-graph)]
    result))

(declare normalize-clojure-funcs)

(defn- normalize-clojure-func [clojure-element]
  (let [type-of-element (type clojure-element)
        name (:name clojure-element)]
    (cond
      (= type-of-element File) (File. name (normalize-clojure-funcs (:namespaces clojure-element)))
      (= type-of-element Namespace) (Namespace. name (normalize-clojure-funcs (:functions clojure-element)))
      (= type-of-element Function) (Function. name (map str (range 0 (count (:parameters clojure-element))))))))

(defn- normalize-clojure-funcs [clojure-functions]
  (map normalize-clojure-func clojure-functions))

(defn check-clojure2java-function-mapping [clojure-functions java2clojure-calls]
  (let [clojure-funcs-without-files (mapcat :namespaces clojure-functions)
        normalized-clojure-funcs (normalize-clojure-funcs clojure-funcs-without-files)
        result (compare-clojure-java-graphs normalized-clojure-funcs java2clojure-calls)
        modifications (.getNodeModifications result)]
     modifications))