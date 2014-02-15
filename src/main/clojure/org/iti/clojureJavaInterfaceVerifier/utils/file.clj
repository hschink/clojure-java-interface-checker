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

(ns org.iti.clojureJavaInterfaceVerifier.utils.File
  (:use [clojure.java.io :only [reader]]))

(def ^:private directory-filter
  (reify java.io.FileFilter (accept [_ file] (.isDirectory file))))

(defn- source-file-filter [file-ending]
  (reify java.io.FileFilter
    (accept [_ file] (and (.isFile file) (.endsWith (.getName file) file-ending)))))

(defn get-source-files [path file-ending]
  (concat (.listFiles path (source-file-filter file-ending))
          (mapcat #(get-source-files % file-ending) (.listFiles path directory-filter))))

(defn get-lines [file]
  (with-open [r (reader file)]
    (let [lines (doall (line-seq r))]
      {file lines})))