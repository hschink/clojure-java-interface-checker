package org.iti.clojureJavaInterfaceVerifier.utils;

import java.util.HashSet;
import java.util.Set;

public class ClojureNamespace {

	private String name = "";

	public String getName() {
		return name;
	}

	private Set<ClojureFunction> functions = new HashSet<>();

	public Set<ClojureFunction> getFunctions() {
		return functions;
	}

	public void addFunctions(ClojureFunction function) {
		functions.add(function);
	}

	public ClojureNamespace(String name) {
		this.name = name;
	}
}
