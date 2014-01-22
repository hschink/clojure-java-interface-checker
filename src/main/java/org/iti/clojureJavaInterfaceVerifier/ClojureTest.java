package org.iti.clojureJavaInterfaceVerifier;

import java.io.IOException;

import clojure.lang.RT;

public class ClojureTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
//		RT.loadResourceScript("src/main/clojure/org/iti/clojureJavaInterfaceVerifier/test.clj");
		new clojure.lang.RT();
		clojure.lang.Compiler.loadFile("src/main/clojure/org/iti/clojureJavaInterfaceVerifier/test.clj");
		System.out.println(RT.var("org.iti.clojureJavaInterfaceVerifier", "add2").invoke(2));
		Object edn = RT.var("org.iti.clojureJavaInterfaceVerifier", "get-ast").invoke("(defn add2 [x] (+ x 2))");
		System.out.println(edn);
	}

}
