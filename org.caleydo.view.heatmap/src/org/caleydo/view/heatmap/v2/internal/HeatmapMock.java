/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.util.Random;

import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.datadomain.mock.MockDataDomain;
import org.caleydo.view.heatmap.v2.HeatMapElement;

/**
 * @author Samuel Gratzl
 *
 */
public class HeatmapMock {

	public static void main(String[] args) {
		MockDataDomain d = MockDataDomain.createNumerical(100, 100, new Random());
		GLSandBox.main(args, new HeatMapElement(d.getDefaultTablePerspective()));
	}
}
