/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;

/**
 * different spacing layouts for a heatmap, i.e. how to place within one dimension
 *
 * @author Samuel Gratzl
 *
 */
public interface ISpacingStrategy {
	/**
	 * apply the strategy and compute a {@link ISpacingLayout} given the arguments
	 *
	 * @param perspective
	 *            the data to show
	 * @param manager
	 *            the selection manager of this data to test, whether some items are selected
	 * @param hideHidden
	 *            should hidden selected items really be hidden
	 * @param size
	 *            available dip size
	 * @return
	 */
	ISpacingLayout apply(Perspective perspective, SelectionManager manager, boolean hideHidden, float size);

	/**
	 * returns the minimum size of this dimension according to the given information
	 *
	 * @param count
	 * @param isShowingLabels
	 * @return
	 */
	float minSize(int count, boolean isShowingLabels);

	public interface ISpacingLayout {
		/**
		 * return the position of the given index
		 *
		 * @param index
		 * @return
		 */
		float getPosition(int index);

		/**
		 * return the cell sice of the given index
		 *
		 * @param index
		 * @return
		 */
		float getSize(int index);

		/**
		 * return the index at the given position or -1 for none
		 *
		 * @param position
		 * @return
		 */
		int getIndex(float position);
	}
}
