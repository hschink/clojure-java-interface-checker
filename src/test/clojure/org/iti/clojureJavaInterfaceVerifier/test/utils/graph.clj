(ns org.iti.clojureJavaInterfaceVerifier.test.utils.Graph
  (:use [clojure.pprint :only [pprint]]
        [org.iti.clojureJavaInterfaceVerifier.utils.Graph :only [create-structure-graph]]
        clojure.test)
  (:import (org.iti.structureGraph.comparison StructureGraphComparer)
           (org.iti.structureGraph.comparison.result Type)))

(def ^:private file-version-original
  [{:name "test.clj"
    :namespaces
    [{:name "org.iti.clojureJavaInterfaceVerifier.Test"
      :functions
      [{:name "add", :parameters ["x"]}
       {:name "get-ast", :parameters ["x"]}]}
     {:name "org.iti.clojureJavaInterfaceVerifier.eeek"
      :functions
      [{:name "add2", :parameters ["x"]}
       {:name "get-ast", :parameters ["x"]}]}]}])

(def ^:private file-version-add-parameter
  [{:name "test.clj"
    :namespaces
    [{:name "org.iti.clojureJavaInterfaceVerifier.Test"
      :functions
      [{:name "add", :parameters ["x" "y"]}
       {:name "get-ast", :parameters ["x"]}]}
     {:name "org.iti.clojureJavaInterfaceVerifier.eeek"
      :functions
      [{:name "add2", :parameters ["x"]}
       {:name "get-ast", :parameters ["x"]}]}]}])

(def ^:private file-version-rename-method
  [{:name "test.clj"
    :namespaces
    [{:name "org.iti.clojureJavaInterfaceVerifier.Test"
      :functions
      [{:name "add", :parameters ["x"]}
       {:name "get-ast", :parameters ["x"]}]}
     {:name "org.iti.clojureJavaInterfaceVerifier.eeek"
      :functions
      [{:name "add2", :parameters ["x"]}
       {:name "get-ast2", :parameters ["x"]}]}]}])

(def ^:private file-version-move-method
  [{:name "test.clj"
    :namespaces
    [{:name "org.iti.clojureJavaInterfaceVerifier.Test"
      :functions
      [{:name "get-ast", :parameters ["x"]}]}
     {:name "org.iti.clojureJavaInterfaceVerifier.eeek"
      :functions
      [{:name "add", :parameters ["x"]}
       {:name "add2", :parameters ["x"]}
       {:name "get-ast", :parameters ["x"]}]}]}])

(defn- check-graph-nodes [nodes]
  (is (some #{"test.clj"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.Test)"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.Test.HasMethod(add))"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.Test.HasMethod(get-ast))"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.Test.HasMethod(add.HasParameter(x)))"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.Test.HasMethod(get-ast.HasParameter(x)))"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.eeek)"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.eeek.HasMethod(add2))"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.eeek.HasMethod(get-ast))"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.eeek.HasMethod(add2.HasParameter(x)))"} nodes))
  (is (some #{"test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.eeek.HasMethod(get-ast.HasParameter(x)))"} nodes)))

(def add-parameter-id "test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.Test.HasMethod(add.HasParameter(y)))")

(def rename-method-id "test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.eeek.HasMethod(get-ast2))")

(def move-method-id "test.clj.HasNamespace(org.iti.clojureJavaInterfaceVerifier.eeek.HasMethod(add))")

(deftest clojure-structure-graph
  (let [graph (create-structure-graph file-version-original)
        nodes (.getIdentifiers graph)]
    (check-graph-nodes nodes)))

(deftest clojure-structure-graph-add-parameter
  (let [graph (create-structure-graph file-version-add-parameter)
        nodes (.getIdentifiers graph)]
    (check-graph-nodes nodes)
    (is (some #{add-parameter-id} nodes))))

(defn- check-modification-of-type-exists [old-graph new-graph key mod-type]
  (let[comparer (StructureGraphComparer.)
       result (.compare comparer old-graph new-graph)
       modifications (.getModifications result)]
    (is (not (empty? modifications)))
    (is (not (nil? (get modifications key))))
    (let [modification (get modifications key)
          type (.getType modification)]
      (is (= type mod-type)))))

(deftest clojure2clojure-comparison-add-parameter
  (let [graph-original (create-structure-graph file-version-original)
        graph-add-parameter (create-structure-graph file-version-add-parameter)]
    (check-modification-of-type-exists graph-original graph-add-parameter add-parameter-id Type/NodeAdded)))

(deftest clojure2clojure-comparison-remove-parameter
  (let [graph-original (create-structure-graph file-version-add-parameter)
        graph-remove-parameter (create-structure-graph file-version-original)]
    (check-modification-of-type-exists graph-original graph-remove-parameter add-parameter-id Type/NodeDeleted)))

(deftest clojure2clojure-comparison-rename-method
  (let [graph-original (create-structure-graph file-version-original)
        graph-rename-method (create-structure-graph file-version-rename-method)]
    (check-modification-of-type-exists graph-original graph-rename-method rename-method-id Type/NodeRenamed)))

(deftest clojure2clojure-comparison-move-method
  (let [graph-original (create-structure-graph file-version-original)
        graph-move-method (create-structure-graph file-version-move-method)]
    (check-modification-of-type-exists graph-original graph-move-method move-method-id Type/NodeMoved)))