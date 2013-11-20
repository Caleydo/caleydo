/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.parcoords.v2.test;

import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.datadomain.mock.MockDataDomain;
import org.caleydo.view.parcoords.v2.ParallelCoordinateElement;

/**
 * @author Samuel Gratzl
 *
 */
public class Test {

	public static void main(String[] args) {
		MockDataDomain d = MockDataDomain.createNumerical(10, 100, MockDataDomain.RANDOM);

		ParallelCoordinateElement root = new ParallelCoordinateElement(d.getDefaultTablePerspective(), EDetailLevel.HIGH);
		GLSandBox.main(args, root);
	}
}
