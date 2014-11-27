/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import org.caleydo.core.util.color.Color;

/**
 * @author Christian
 *
 */
public class SimpleCategory {

	public final String name;
	public final Color color;

	/**
	 * @param name
	 * @param color
	 */
	public SimpleCategory(String name, Color color) {
		this.name = name;
		this.color = color;
	}

}
