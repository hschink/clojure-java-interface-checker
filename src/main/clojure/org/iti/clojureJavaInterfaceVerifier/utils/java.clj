(ns org.iti.clojureJavaInterfaceVerifier.utils.Java
  (:use [org.iti.clojureJavaInterfaceVerifier.utils.File :only [get-source-files get-lines]]))

(defn java-source-files [path]
  (get-source-files path "java"))