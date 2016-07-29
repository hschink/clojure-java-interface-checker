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

(ns org.iti.clojureJavaInterfaceVerifier.utils.Java
  (:use [org.iti.clojureJavaInterfaceVerifier.utils.File :only [get-source-files]])
  (:import (java.io FileInputStream)
           (com.github.javaparser JavaParser)
           (com.github.javaparser.ast.visitor VoidVisitorAdapter)
           (com.github.javaparser.ast.expr MethodCallExpr)
           (org.iti.clojureJavaInterfaceVerifier.utils RtVisitor)
           (org.iti.clojureJavaInterfaceVerifier.utils.Graph File Namespace Function Parameter)))

(defn java-source-files [path]
  (get-source-files path "java"))

(defn- parse-java-file [file]
  (with-open [in (FileInputStream. (.getAbsolutePath file))]
    (JavaParser/parse in)))

(defn- parse-java-files [files]
  (map parse-java-file files))

(defn- convert [element]
  (let [type-of-element (type element)]
    (cond
      (= type-of-element org.iti.clojureJavaInterfaceVerifier.utils.ClojureFunction) (Function. (.getName element) (map #(Parameter. % false) (.getParameters element)))
      (= type-of-element org.iti.clojureJavaInterfaceVerifier.utils.ClojureNamespace) (Namespace. (.getName element) (map convert (.getFunctions element))))))

(defn- parse-clojure-calls [java-files]
  (let [cus (parse-java-files java-files)
        visitor (RtVisitor.)]
    (do
      (doall (map #(.visit visitor % (java.util.HashMap.)) cus))
      (map convert (vals (.getNsByName visitor))))))

(defn clojure-calls [files]
  (let [java-files (filter #(-> % (.getName) (.endsWith "java")) files)]
    (parse-clojure-calls java-files)))