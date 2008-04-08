package org.caleydo.testing.command.data.filter;


import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.caleydo.testing.command.data.filter");
		//$JUnit-BEGIN$
		suite.addTestSuite(CmdDataFiterMinMaxTest.class);
		suite.addTestSuite(CmdDataFilterMathTest.class);
		//$JUnit-END$
		return suite;
	}

}
