/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.util.gleem;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;

/**
 * similar to {@link ILabeled} an object which has a color
 *
 * @author Samuel Gratzl
 *
 */
public interface IColored {
	/**
	 * return the color of this object
	 * 
	 * @return
	 */
	Color getColor();
}
