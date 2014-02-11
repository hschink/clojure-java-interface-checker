package org.iti.clojureJavaInterfaceVerifier.utils;

import java.util.ArrayList;
import java.util.List;

class ClojureNamespace {

	private String name = "";

	public String getName() {
		return name;
	}

	private List<ClojureFunction> functions = new ArrayList<>();

	public List<ClojureFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<ClojureFunction> functions) {
		this.functions = functions;
	}

	public ClojureNamespace(String name) {
		this.name = name;
	}
}
