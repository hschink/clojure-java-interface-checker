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