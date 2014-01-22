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