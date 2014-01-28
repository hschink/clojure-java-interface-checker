(ns org.iti.clojureJavaInterfaceVerifier.edges)

(gen-class
  :name org.iti.clojureJavaInterfaceVerifier.edges.HasParameter
  :extends org.jgrapht.graph.DefaultEdge)

(gen-class
  :name org.iti.clojureJavaInterfaceVerifier.edges.HasMethod
  :extends org.jgrapht.graph.DefaultEdge)

(gen-class
  :name org.iti.clojureJavaInterfaceVerifier.edges.HasNamespace
  :extends org.jgrapht.graph.DefaultEdge)