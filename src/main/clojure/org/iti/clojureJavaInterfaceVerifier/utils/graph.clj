(ns org.iti.clojureJavaInterfaceVerifier.utils.Graph
  (:use [clojure.pprint :only [pprint]]
        [org.iti.clojureJavaInterfaceVerifier.Nodes :only [element]])
  (:import (org.iti.structureGraph StructureGraph)
           (org.jgrapht.graph SimpleDirectedGraph DefaultEdge)))

;(defrecord Namespace [name functions])
;
;(defrecord Function [name parameters])

(declare create-elements create-element-hash)

(defn- create-element-hash [kv]
  (let [original-key (key kv)
        key (if (keyword? original-key) original-key (element original-key))
        value (create-elements (val kv))]
    {key value}))

(defn- create-elements [elements]
  (if (map? (first elements))
    (let [element-hash (reduce merge elements)]
       (reduce merge (map create-element-hash element-hash)))
    (let [element-list (concat elements)]
      (map element element-list))))

(defn- add-elements-for-key-to-graph [graph elements key]
  (let [values (if (map? elements) (keys elements) elements)]
    (doall
      (map #(do (.addVertex graph %) (if key (.addEdge graph key %))) values))))

(defn- add-elements-for-keys-to-graph [graph elements]
  (let [keys (filter (comp not keyword?) (keys elements))]
    (doall
      (map #(let [key-values (get elements %)
                  key (if (= :default (.getIdentifier %)) nil %)]
              (if key (.addVertex graph key))
              (add-elements-for-key-to-graph graph key-values key)) keys))))

(defn- add-elements-to-graph [graph elements]
  (add-elements-for-keys-to-graph graph elements)
  (let [values (vals elements)]
    (if (some map? values)
      (let [value-map (reduce merge (filter (comp not empty?) values))]
        (add-elements-to-graph graph value-map))
      (add-elements-for-keys-to-graph graph elements))))

(defn create-structure-graph [methods-by-namespace]
  (let [graph (SimpleDirectedGraph. DefaultEdge)
        elements (create-elements (vals methods-by-namespace))]
    (add-elements-to-graph graph elements)
    (StructureGraph. graph)))