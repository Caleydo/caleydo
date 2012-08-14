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
package org.caleydo.util.r;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class RTest {
	public static void main(String[] args) {
		// just making sure we have the right version of everything
		if (!Rengine.versionCheck()) {
			System.err
					.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Creating Rengine (with arguments)");
		// 1) we pass the arguments from the command line
		// 2) we won't use the main loop at first, we'll start it later
		// (that's the "false" as second argument)
		// 3) the callbacks are implemented by the TextConsole class above
		Rengine re = new Rengine(args, false, new RConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's
		// ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}

		/*
		 * High-level API - do not use RNI methods unless there is no other way
		 * to accomplish what you want
		 */
		try {
			REXP test;
			int[] array = new int[] { 5, 6, 7 };
			int[] array_2 = new int[] { 1, 2, 3 };
			re.assign("my_array", array);
			re.assign("my_array_2", array_2);

			System.out.println("Array: " + re.eval("my_array"));
			System.out.println("Array 2: " + re.eval("my_array_2"));
			test = re.eval("t.test(my_array,my_array_2)");
			System.out.println("T-Test result: " + test);

			// REXP x;
			// re.eval("data(iris)",false);
			// System.out.println(x=re.eval("iris"));
			//
			// // generic vectors are RVector to accomodate names
			// RVector v = x.asVector();
			// if (v.getNames()!=null) {
			// System.out.println("has names:");
			// for (Enumeration e = v.getNames().elements() ;
			// e.hasMoreElements() ;) {
			// System.out.println(e.nextElement());
			// }
			// }
			// // for compatibility with Rserve we allow casting of vectors to
			// lists
			// RList vl = x.asList();
			// String[] k = vl.keys();
			// if (k!=null) {
			// System.out.println("and once again from the list:");
			// int i=0; while (i<k.length) System.out.println(k[i++]);
			// }
			//
			// // get boolean array
			// System.out.println(x=re.eval("iris[[1]]>mean(iris[[1]])"));
			// // R knows about TRUE/FALSE/NA, so we cannot use boolean[] this
			// way
			// // instead, we use int[] which is more convenient (and what R
			// uses internally anyway)
			// int[] bi = x.asIntArray();
			// {
			// int i = 0; while (i<bi.length) {
			// System.out.print(bi[i]==0?"F ":(bi[i]==1?"T ":"NA ")); i++; }
			// System.out.println("");
			// }
			//
			// // push a boolean array
			// boolean by[] = { true, false, false };
			// re.assign("bool", by);
			// System.out.println(x=re.eval("bool"));
			// // asBool returns the first element of the array as RBool
			// // (mostly useful for boolean arrays of the length 1). is should
			// return true
			// System.out.println("isTRUE? "+x.asBool().isTRUE());
			//
			// // now for a real dotted-pair list:
			// System.out.println(x=re.eval("pairlist(a=1,b='foo',c=1:5)"));
			// RList l = x.asList();
			// if (l!=null) {
			// int i=0;
			// String [] a = l.keys();
			// System.out.println("Keys:");
			// while (i<a.length) System.out.println(a[i++]);
			// System.out.println("Contents:");
			// i=0;
			// while (i<a.length) System.out.println(l.at(i++));
			// }
			// System.out.println(re.eval("sqrt(36)"));
		} catch (Exception e) {
			System.out.println("EX:" + e);
			e.printStackTrace();
		}
		{
			REXP x = re.eval("1:10");
			System.out.println("REXP result = " + x);
			int d[] = x.asIntArray();
			if (d != null) {
				int i = 0;
				while (i < d.length) {
					System.out.print(((i == 0) ? "" : ", ") + d[i]);
					i++;
				}
				System.out.println("");
			}
		}

		re.eval("print(1:10/3)");

		if (true) {
			// so far we used R as a computational slave without REPL
			// now we start the loop, so the user can use the console
			System.out.println("Now the console is yours ... have fun");
			re.startMainLoop();
		}
	}
}