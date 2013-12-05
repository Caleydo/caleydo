/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.api.model;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.perspective.variable.Perspective;

/**
 * @author Samuel Gratzl
 *
 */
public interface IPerspectiveScoreRow {
	Perspective asPerspective();

	/**
	 * return the dimension of the {@link Perspective}
	 * 
	 * @return
	 */
	EDimension getDimension();
}
