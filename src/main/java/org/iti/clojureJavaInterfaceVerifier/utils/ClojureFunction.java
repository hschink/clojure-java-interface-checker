package org.iti.clojureJavaInterfaceVerifier.utils;

import java.util.HashSet;
import java.util.Set;

public class ClojureFunction {

	private String name = "";

	public String getName() {
		return name;
	}

	private Set<String> parameters = new HashSet<>();

	public Set<String> getParameters() {
		return parameters;
	}

	public void addParameters(String parameter) {
		this.parameters.add(parameter);
	}

	public ClojureFunction(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		ClojureFunction other = (ClojureFunction) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}
}
