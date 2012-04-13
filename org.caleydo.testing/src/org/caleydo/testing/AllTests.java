/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.testing;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.caleydo.testing.collection.VirtualArrayTester;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.caleydo.testing.command.data.filter");
		// $JUnit-BEGIN$
		// suite.addTestSuite(CmdDataFiterMinMaxTest.class);
		// suite.addTestSuite(CmdDataFilterMathTest.class);
		// suite.addTestSuite(NominalStringCCollectionTest.class);
		// $JUnit-END$
		suite.addTestSuite(VirtualArrayTester.class);
		return suite;
	}

}
