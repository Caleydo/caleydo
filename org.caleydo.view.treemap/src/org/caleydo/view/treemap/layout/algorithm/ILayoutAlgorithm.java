/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.layout.algorithm;

import org.caleydo.view.treemap.layout.ATreeMapNode;

/**
 * Interface for layout algorithm.
 * 
 * @author Michael Lafer
 * 
 */

public interface ILayoutAlgorithm {

	public static final int SIMPLE_LAYOUT_ALGORITHM = 0;
	public static final int SQUARIFIED_LAYOUT_ALGORITHM = 1;

	/**
	 * Apply layout on given data.
	 * 
	 * @param tree
	 *            Treemap model without display coordinates (see
	 *            <code>ATreeMapNode</code>).
	 */
	public void layout(ATreeMapNode tree);

}
