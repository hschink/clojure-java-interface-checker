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

(ns org.iti.clojureJavaInterfaceVerifier.Test
  (:require [clojure.tools.reader :as r]
            [clojure.tools.reader.edn :as edn])
  (:use [clojure.jvm.tools.analyzer :only [ast]]
        [clojure.pprint :only [pprint]]))

(defn add2 [x]
  (+ x 2))

(defn get-ast [x]
  (pprint x)
  (edn/read-string x))

(ns org.iti.clojureJavaInterfaceVerifier.eeek)

(defn add2 [x]
  (+ x 2))

(defn get-ast [x]
  (pprint x)
  (edn/read-string x))