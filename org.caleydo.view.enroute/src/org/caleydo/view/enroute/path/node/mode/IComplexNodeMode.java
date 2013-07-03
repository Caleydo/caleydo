/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path.node.mode;


/**
 * Abstract base class for complex node modes.
 *
 * @author Christian Partl
 *
 */
public interface IComplexNodeMode {

	

	/**
	 * Updates the positions of all nodes within a complex node. This method should be called whenever the position of a
	 * complex node is changed.
	 */
	public void updateSubNodePositions();


}
