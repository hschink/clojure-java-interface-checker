;;
;;  Copyright 2014 Hagen Schink <hagen.schink@gmail.com>
;;
;;  This file is part of sql-schema-comparer.
;;
;;  sql-schema-comparer is free software: you can redistribute it and/or modify
;;  it under the terms of the GNU Lesser General Public License as published by
;;  the Free Software Foundation, either version 3 of the License, or
;;  (at your option) any later version.
;;
;;  sql-schema-comparer is distributed in the hope that it will be useful,
;;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;  GNU Lesser General Public License for more details.
;;
;;   You should have received a copy of the GNU Lesser General Public License
;;  along with sql-schema-comparer.  If not, see <http://www.gnu.org/licenses/>.

(ns org.iti.clojureJavaInterfaceVerifier.test.utils.Java
  (:use clojure.test)
  (:require [org.iti.clojureJavaInterfaceVerifier.utils.Java :as oicj])) 

(def java-test-file-name "ClojureAccess.java")

(def java-test-file-path "src/test/clojure/files/ClojureAccess.java")

(def java-test-file (clojure.java.io/as-file java-test-file-path))

(deftest read-clojure-function-invocations-calls-from-java-file
 (let [result (oicj/clojure-calls [java-test-file])
       namespace (first result)]
   (is (= (count result) 1))
   (is (= (.getName namespace) "org.iti.clojureJavaInterfaceVerifier.Test"))
   (is (= (count (.getFunctions namespace)) 1))
   (let [func (first (.getFunctions namespace))]
     (is (= (.getName func) "func"))
     (is (= (count (.getParameters func)) 0)))))