(ns org.iti.clojureJavaInterfaceVerifier.utils.Graph
  (:use [clojure.pprint :only [pprint]]
        [org.iti.clojureJavaInterfaceVerifier.Nodes :only [element]])
  (:import (org.iti.structureGraph StructureGraph)
           (org.jgrapht.graph SimpleDirectedGraph DefaultEdge)))

(defrecord Function [name parameters])

(defn- create-function-element [function]
  (Function. (element (.name function)) (map #(element %) (.parameters function))))

(defn- create-namespace-element [namespace]
  (let [namespace-name (key namespace)
        functions (val namespace)
        key (if (keyword? namespace-name) namespace-name (element namespace-name))]
    {key (concat (map create-function-element functions))}))

(defn- create-namespace-elements [namespaces]
  (reduce merge (map create-namespace-element (conj {} namespaces))))

(defn- create-file-element [file]
  {(element (.getName (key file))) (create-namespace-elements (val file))})

(defn- create-elements [files]
  (reduce merge (map create-file-element files)))

(defn- add-parameter-to-graph [graph source parameter]
  (do
    (.addVertex graph parameter)
    (.addEdge graph source parameter)))

(defn- add-function-to-graph [graph source function]
  (println ">> function")
  (pprint function)
  (let [element (:name function)]
    (do
       (.addVertex graph element)
       (.addEdge graph source element)
       (doall
         (map (partial add-parameter-to-graph graph element) (:parameters function))))))

(defn- add-namespace-to-graph [graph file namespace]
  (println ">> namespace")
  (pprint namespace)
  (let [key (key namespace)
        source (if (keyword? key) file key)]
    (do
      (if (not (keyword? key))
        (do
          (.addVertex graph key)
          (.addEdge graph file key)))
      (doall
        (map (partial add-function-to-graph graph source) (val namespace))))))

(defn- add-file-to-graph [graph file]
  (println ">> file")
  (pprint file)
  (let [element (key file)]
    (do
      (.addVertex graph element)
      (doall
        (map (partial add-namespace-to-graph graph element) (val file))))))

(defn- add-elements-to-graph [graph files]
  (doall
    (map (partial add-file-to-graph graph) files)))

(defn create-structure-graph [methods-by-namespace]
  (let [graph (SimpleDirectedGraph. DefaultEdge)
        elements (create-elements methods-by-namespace)]
    (add-elements-to-graph graph elements)
    (StructureGraph. graph)))