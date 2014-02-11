package org.iti.clojureJavaInterfaceVerifier.utils;

import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class RtVisitor extends VoidVisitorAdapter<List<ClojureNamespace>> {

	private List<ClojureNamespace> namespaces;

	public RtVisitor(List<ClojureNamespace> namespaces) {
		this.namespaces = namespaces;
	}

	@Override
	public void visit(MethodCallExpr n, List<ClojureNamespace> arg) {
		super.visit(n, arg);
	}
}
