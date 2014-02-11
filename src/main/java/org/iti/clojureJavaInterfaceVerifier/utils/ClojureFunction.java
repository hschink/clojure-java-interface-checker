package org.iti.clojureJavaInterfaceVerifier.utils;

import java.util.ArrayList;
import java.util.List;

class ClojureFunction {

	private String name = "";

	public String getName() {
		return name;
	}

	private List<String> parameters = new ArrayList<>();

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public ClojureFunction(String name) {
		this.name = name;
	}
}
