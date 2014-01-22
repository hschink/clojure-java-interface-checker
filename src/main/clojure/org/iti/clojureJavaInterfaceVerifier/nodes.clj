(ns org.iti.clojureJavaInterfaceVerifier.Nodes)

(defn element [id]
  (reify org.iti.structureGraph.nodes.IStructureElement
    (getIdentifier [_] id)))