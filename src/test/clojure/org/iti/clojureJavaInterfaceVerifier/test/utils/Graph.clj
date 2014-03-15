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

(ns org.iti.clojureJavaInterfaceVerifier.test.utils.Graph
  (:use [clojure.pprint :only [pprint]]
        [org.iti.clojureJavaInterfaceVerifier.utils.Graph :only [create-structure-graph]]
        clojure.test)
  (:require [org.iti.clojureJavaInterfaceVerifier.utils.Graph :as oicg])
  (:import (org.iti.structureGraph.comparison StructureGraphComparer)
           (org.iti.structureGraph.comparison.result Type)
           (org.iti.clojureJavaInterfaceVerifier.utils.Graph File Namespace Function)))

(def ^:private file-version-original
  (let [func-add (Function. "add" ["x"])
        func-get-ast (Function. "get-ast" ["x"])
        func-add2 (Function. "add2" ["x"])
        ns-test (Namespace. "org.iti.clojureJavaInterfaceVerifier.Test" [func-add func-get-ast])
        ns-eeek (Namespace. "org.iti.clojureJavaInterfaceVerifier.eeek" [func-add2 func-get-ast])
        file-test (File. "test.clj" [ns-test ns-eeek])]
    file-test))

(def ^:private file-version-add-parameter
  (let [func-add (Function. "add" ["x" "y"])
        func-get-ast (Function. "get-ast" ["x"])
        func-add2 (Function. "add2" ["x"])
        ns-test (Namespace. "org.iti.clojureJavaInterfaceVerifier.Test" [func-add func-get-ast])
        ns-eeek (Namespace. "org.iti.clojureJavaInterfaceVerifier.eeek" [func-add2 func-get-ast])
        file-test (File. "test.clj" [ns-test ns-eeek])]
    file-test))

(def ^:private file-version-rename-method
  (let [func-add (Function. "add" ["x"])
        func-get-ast (Function. "get-ast" ["x"])
        func-add2 (Function. "add2" ["x"])
        func-get-ast2 (Function. "get-ast2" ["x"])
        ns-test (Namespace. "org.iti.clojureJavaInterfaceVerifier.Test" [func-add func-get-ast])
        ns-eeek (Namespace. "org.iti.clojureJavaInterfaceVerifier.eeek" [func-add2 func-get-ast2])
        file-test (File. "test.clj" [ns-test ns-eeek])]
    file-test))

(def ^:private file-version-move-method
  (let [func-add (Function. "add" ["x"])
        func-get-ast (Function. "get-ast" ["x"])
        func-add2 (Function. "add2" ["x"])
        ns-test (Namespace. "org.iti.clojureJavaInterfaceVerifier.Test" [func-get-ast])
        ns-eeek (Namespace. "org.iti.clojureJavaInterfaceVerifier.eeek" [func-add func-add2 func-get-ast])
        file-test (File. "test.clj" [ns-test ns-eeek])]
    file-test))

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
  (let [graph (create-structure-graph [file-version-original])
        nodes (.getIdentifiers graph)]
    (check-graph-nodes nodes)))

(deftest clojure-structure-graph-add-parameter
  (let [graph (create-structure-graph [file-version-add-parameter])
        nodes (.getIdentifiers graph)]
    (check-graph-nodes nodes)
    (is (some #{add-parameter-id} nodes))))

(defn- modifications [old-graph new-graph]
  (let [comparer (StructureGraphComparer.)
        result (.compare comparer old-graph new-graph)
        modifications (.getModifications result)]
    modifications))

(defn- check-modification-of-type-exists [modifications key mod-type]
  (is (not (empty? modifications)))
  (is (not (nil? (get modifications key))))
  (let [modification (get modifications key)
        type (.getType modification)]
    (is (= type mod-type))))

(deftest clojure2clojure-comparison-add-parameter
  (let [graph-original (create-structure-graph [file-version-original])
        graph-add-parameter (create-structure-graph [file-version-add-parameter])
        modifications (modifications graph-original graph-add-parameter)]
    (check-modification-of-type-exists modifications add-parameter-id Type/NodeAdded)))

(deftest clojure2clojure-comparison-remove-parameter
  (let [graph-original (create-structure-graph [file-version-add-parameter])
        graph-remove-parameter (create-structure-graph [file-version-original])
        modifications (modifications graph-original graph-remove-parameter)]
    (check-modification-of-type-exists modifications add-parameter-id Type/NodeDeleted)))

(deftest clojure2clojure-comparison-rename-method
  (let [graph-original (create-structure-graph [file-version-original])
        graph-rename-method (create-structure-graph [file-version-rename-method])
        modifications (modifications graph-original graph-rename-method)]
    (check-modification-of-type-exists modifications rename-method-id Type/NodeRenamed)))

(deftest clojure2clojure-comparison-move-method
  (let [graph-original (create-structure-graph [file-version-original])
        graph-move-method (create-structure-graph [file-version-move-method])
        modifications (modifications graph-original graph-move-method)]
    (check-modification-of-type-exists modifications move-method-id Type/NodeMoved)))

(def ^:private clojure-calls-in-java
  (let [func-add (Function. "add" ["0"])
        func-get-ast (Function. "get-ast" ["0"])
        ns-test (Namespace. "org.iti.clojureJavaInterfaceVerifier.Test" [func-add func-get-ast])]
    ns-test))

(deftest check-valid-clojure2java-function-mapping
  (let [result (oicg/check-clojure2java-function-mapping [file-version-original] [clojure-calls-in-java])]
    (is (empty? result))))

(deftest check-missing-parameter-in-clojure2java-function-mapping
  (let [result (oicg/check-clojure2java-function-mapping [file-version-add-parameter] [clojure-calls-in-java])
        expected "org.iti.clojureJavaInterfaceVerifier.Test.HasMethod(add.HasParameter(1))"]
    (is (= (count result) 1))
    (check-modification-of-type-exists result expected Type/NodeDeleted)))
