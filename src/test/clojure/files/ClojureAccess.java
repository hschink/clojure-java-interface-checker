/*
 *  Copyright 2014 Hagen Schink <hagen.schink@gmail.com>
 *
 *  This file is part of clojure-java-interface-verifier.
 *
 *  clojure-java-interface-verifier is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  clojure-java-interface-verifier is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with clojure-java-interface-verifier.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package files;

import java.io.IOException;

import clojure.lang.RT;
import clojure.lang.Var;

public class ClojureAccess {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
//		RT.loadResourceScript("src/main/clojure/org/iti/clojureJavaInterfaceVerifier/test.clj");
		new clojure.lang.RT();
		clojure.lang.Compiler.loadFile("src/main/clojure/org/iti/clojureJavaInterfaceVerifier/test.clj");
		Var func = RT.var("org.iti.clojureJavaInterfaceVerifier.Test", "func-add");
		Var getAst = RT.var("org.iti.clojureJavaInterfaceVerifier.Test", "get-ast");

		func.invoke();

		getAst.invoke("x");
	}

}
