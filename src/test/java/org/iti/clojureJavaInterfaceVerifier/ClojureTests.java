/*
 *  Copyright 2014 Hagen Schink <hagen.schink@gmail.com>
 *
 *  This file is part of sql-schema-comparer.
 *
 *  sql-schema-comparer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  sql-schema-comparer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with sql-schema-comparer.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

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
				NAMESPACE_PREFIX + "utils.Graph",
	        });
	}
}
