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

(ns org.iti.clojureJavaInterfaceVerifier.test.utils.Clojure
  (:use clojure.test
        [org.iti.clojureJavaInterfaceVerifier.test.utils.TestUtils :only [get-fn-by-name]])
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
;     {:name "get-ast", :parameters ["x"]}
;     {:name "variadic", :parameters ["x" "y" "args"]}]})})

(def clojure-test-file-name "test.clj")

(def clojure-test-file-path "src/test/clojure/files/test.clj")

(def clojure-test-file (clojure.java.io/as-file clojure-test-file-path))

(defn- get-ns-by-name [name namespaces]
  (filter #(= (:name %) name) namespaces))

(defn- get-fns-from-namespace-by-name [name namespaces]
  (mapcat :functions (get-ns-by-name name namespaces)))

(defn- check-fn-parameters [fn optional-list-flags]
  (let [parameters (:parameters fn)
        c (count optional-list-flags)]
    (is (= c (count parameters)))
    (is (= optional-list-flags (map #(:is-optional %) parameters)))))

(deftest reads-clojure-source-file
 (let [result (oicc/read-clojure-methods-by-namespace [clojure-test-file])
       file (first result)]
   (is (= (count result) 1))
   (is (= (:name file) clojure-test-file-name))))

(deftest reads-clojure-namespaces
 (let [result (oicc/read-clojure-methods-by-namespace [clojure-test-file])
       namespaces (:namespaces (first result))
       default-ns (get-ns-by-name :default namespaces)
       test-ns (get-ns-by-name "org.iti.clojureJavaInterfaceVerifier.Test" namespaces)
       eeek-ns (get-ns-by-name "org.iti.clojureJavaInterfaceVerifier.eeek" namespaces)]
   (is (= (count namespaces) 2))
   (is (= (count default-ns) 0))
   (is (= (count test-ns) 1))
   (is (= (count eeek-ns) 1))))

(deftest reads-clojure-functions
 (let [result (oicc/read-clojure-methods-by-namespace [clojure-test-file])
       namespaces (:namespaces (first result))
       default-fns (get-fns-from-namespace-by-name :default namespaces)
       test-fns (get-fns-from-namespace-by-name "org.iti.clojureJavaInterfaceVerifier.Test" namespaces)
       eeek-fns (get-fns-from-namespace-by-name "org.iti.clojureJavaInterfaceVerifier.eeek" namespaces)]
   (is (= (count default-fns) 0))
   (is (= (count test-fns) 2))
   (is (= (count eeek-fns) 3))
   (check-fn-parameters (get-fn-by-name "add2" test-fns) '(false))
   (check-fn-parameters (get-fn-by-name "get-ast" test-fns) '(false))
   (check-fn-parameters (get-fn-by-name "add2" eeek-fns) '(false))
   (check-fn-parameters (get-fn-by-name "get-ast" eeek-fns) '(false))
   (check-fn-parameters (get-fn-by-name "variadic" eeek-fns) '(false false true))))