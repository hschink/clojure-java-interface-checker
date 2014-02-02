package org.iti.clojureJavaInterfaceVerifier;

import java.util.Arrays;
import java.util.List;

import mikera.cljunit.ClojureTest;

public class ClojureTests extends ClojureTest {

	private final String NAMESPACE_PREFIX = "org.iti.clojureJavaInterfaceVerifier.test.";

	@Override
	public List<String> namespaces() {

		return Arrays.asList(new String[] {
				NAMESPACE_PREFIX + "utils.Clojure",
	        });
	}
}
