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

package org.iti.clojureJavaInterfaceVerifier.utils;

import japa.parser.ast.Node;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.Map;

public class RtVisitor extends VoidVisitorAdapter<Object> {

	private Map<String, ClojureNamespace> nsByName = new HashMap<>();

	public Map<String, ClojureNamespace> getNsByName() {
		return nsByName;
	}

	private Map<String, ClojureFunction> declarationIds = new HashMap<>();

	@Override
	public void visit(MethodCallExpr n, Object arg) {
		String methodName = n.getName();
		String scope = (n.getScope() == null) ? "this" : n.getScope().toString();

		if (methodName.equals("var") && scope.equals("RT")) {
			processClojureFunctionInit(n);
		} else if (methodName.equals("invoke") && declarationIds.containsKey(scope)) {
			processClojureFunctionInvocation(n, declarationIds.get(scope));
		}

		super.visit(n, arg);
	}

	private void processClojureFunctionInit(MethodCallExpr n) {
		ClojureNamespace ns = getNamespace(n.getArgs().get(0));
		ClojureFunction func = new ClojureFunction(n.getArgs().get(1).toString().replaceAll("\"", ""));

		prepareClojureMethodClassProcessing(n, func);

		ns.addFunctions(func);
	}

	private void processClojureFunctionInvocation(MethodCallExpr n,
			ClojureFunction clojureFunction) {
		int numberOfArgs = (n.getArgs() == null) ? 0 : n.getArgs().size();
		
		for (int x = 0; x < numberOfArgs; x++) {
			clojureFunction.addParameters(x + "");
		}
	}

	private ClojureNamespace getNamespace(Expression expression) {
		String nsName = expression.toString();

		if (nsByName.containsKey(nsName)) {
			return nsByName.get(nsName);
		}

		nsByName.put(nsName, new ClojureNamespace(nsName.replaceAll("\"", "")));

        return nsByName.get(nsName);
	}

	private void prepareClojureMethodClassProcessing(MethodCallExpr n, ClojureFunction func) {
		Node parent = n.getParentNode();
		
		if (parent instanceof VariableDeclarator) {
			rememberDeclarationId(parent, func);
		}
	}

	private void rememberDeclarationId(Node parent, ClojureFunction func) {
		VariableDeclarator declaration = (VariableDeclarator)parent;
        VariableDeclaratorId id = declaration.getId();

        declarationIds.put(id.toString(), func);
	}

}
