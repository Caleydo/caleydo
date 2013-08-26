/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.vis.lineup.layout.IRowLayoutInstance.IRowSetter;
import org.caleydo.vis.lineup.model.ARankColumnModel;

public interface IColumModelLayout {
	int getNumVisibleRows(ARankColumnModel model);
	void layoutRows(ARankColumnModel model, IRowSetter setter, float w, float h);

	/**
	 * @param tableColumnUI
	 * @return
	 */
	boolean hasFreeSpace(ITableColumnUI tableColumnUI);

	/**
	 * @param tableColumnUI
	 * @return
	 */
	VAlign getAlignment(ITableColumnUI tableColumnUI);

	OrderColumnUI getRanker(ARankColumnModel model);

	/**
	 * @return
	 */
	boolean causesReorderingLayouting();

	/**
	 * @return
	 */
	Color getBarOutlineColor();

}
