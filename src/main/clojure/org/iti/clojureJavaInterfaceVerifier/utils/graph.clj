(ns org.iti.clojureJavaInterfaceVerifier.utils.Graph
  (:use [clojure.pprint :only [pprint]]
        [org.iti.clojureJavaInterfaceVerifier.Nodes :only [element]])
  (:import (org.iti.structureGraph StructureGraph)
           (org.jgrapht.graph SimpleDirectedGraph DefaultEdge)))

(defrecord Function [name parameters])

(defrecord Namespace [name functions])

(defrecord File [name namespaces])

(defn- add-parameter-to-graph [graph source parameter]
  (let [parameter-element (element parameter)]
    (do
      (.addVertex graph parameter-element)
      (.addEdge graph source parameter-element))))

(defn- add-function-to-graph [graph source function]
  (let [element (element (:name function))]
    (do
       (.addVertex graph element)
       (.addEdge graph source element)
       (doall
         (map (partial add-parameter-to-graph graph element) (:parameters function))))))

(defn- add-namespace-to-graph [graph file namespace]
  (let [ns-name (:name namespace)
        is-default-ns (= :default ns-name)
        ns-element (if is-default-ns nil (element (:name namespace)))
        source (if is-default-ns file ns-element)]
    (do
      (if (not is-default-ns)
        (do
          (.addVertex graph ns-element)
          (.addEdge graph file ns-element)))
      (doall
        (map (partial add-function-to-graph graph source) (:functions namespace))))))

(defn- add-file-to-graph [graph file]
  (let [file-element (element (:name file))]
    (do
      (.addVertex graph file-element)
      (doall
        (map (partial add-namespace-to-graph graph file-element) (:namespaces file))))))

(defn- add-elements-to-graph [graph files]
  (doall
    (map (partial add-file-to-graph graph) files)))

(defn create-structure-graph [methods-by-namespace]
  (let [graph (SimpleDirectedGraph. DefaultEdge)]
    (add-elements-to-graph graph methods-by-namespace)
    (StructureGraph. graph)))