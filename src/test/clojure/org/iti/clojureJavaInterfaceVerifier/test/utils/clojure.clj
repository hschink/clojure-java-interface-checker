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

(ns org.iti.clojureJavaInterfaceVerifier.test.utils.Clojure
  (:use clojure.test)
  (:require [org.iti.clojureJavaInterfaceVerifier.utils.Clojure :as oicc])) 

; The tests expect a File record with the following content
;
; ({:name "test.clj",
;  :namespaces
;  ({:name "org.iti.clojureJavaInterfaceVerifier.Test",
;    :functions
;    [{:name "add2", :parameters ["x"]}
;     {:name "get-ast", :parameters ["x"]}]}
;   {:name "org.iti.clojureJavaInterfaceVerifier.eeek",
;    :functions
;    [{:name "add2", :parameters ["x"]}
;     {:name "get-ast", :parameters ["x"]}]})})

(def clojure-test-file-name "test.clj")

(def clojure-test-file-path "src/test/clojure/files/test.clj")

(def clojure-test-file (clojure.java.io/as-file clojure-test-file-path))

(defn- get-ns-with-name [name namespaces]
  (filter #(= (:name %) name) namespaces))

(defn- get-funcs-from-namespace-with-name [name namespaces]
  (mapcat :functions (get-ns-with-name name namespaces)))

(deftest read-clojure-methods-by-namespace-file
 (let [result (oicc/read-clojure-methods-by-namespace [clojure-test-file])
       file (first result)]
   (is (= (count result) 1))
   (is (= (:name file) clojure-test-file-name))))

(deftest read-clojure-methods-by-namespace-namespaces
 (let [result (oicc/read-clojure-methods-by-namespace [clojure-test-file])
       namespaces (:namespaces (first result))
       default-ns (get-ns-with-name :default namespaces)
       test-ns (get-ns-with-name "org.iti.clojureJavaInterfaceVerifier.Test" namespaces)
       eeek-ns (get-ns-with-name "org.iti.clojureJavaInterfaceVerifier.eeek" namespaces)]
   (is (= (count namespaces) 2))
   (is (= (count default-ns) 0))
   (is (= (count test-ns) 1))
   (is (= (count eeek-ns) 1))))

(deftest read-clojure-methods-by-namespace-functions
 (let [result (oicc/read-clojure-methods-by-namespace [clojure-test-file])
       namespaces (:namespaces (first result))
       default-funcs (get-funcs-from-namespace-with-name :default namespaces)
       test-funcs (get-funcs-from-namespace-with-name "org.iti.clojureJavaInterfaceVerifier.Test" namespaces)
       eeek-funcs (get-funcs-from-namespace-with-name "org.iti.clojureJavaInterfaceVerifier.eeek" namespaces)]
   (is (= (count default-funcs) 0))
   (is (= (count test-funcs) 2))
   (is (= (count eeek-funcs) 2))))