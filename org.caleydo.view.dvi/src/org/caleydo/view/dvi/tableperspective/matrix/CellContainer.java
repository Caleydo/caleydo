/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.tableperspective.matrix;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.util.base.ILabelProvider;

/**
 * Specifies a row or a column of the matrix representation of a node.
 * 
 * @author Christian Partl
 * 
 */
class CellContainer implements Comparable<CellContainer> {
	protected String id;
	protected int numSubdivisions;
	protected float position;
	protected boolean isVisible;
	protected boolean isCollapsed;
	protected ILabelProvider labelProvider;
	protected List<CellContainer> childContainers = new ArrayList<CellContainer>();
	protected CellContainer parentContainer;

	public CellContainer() {
	}

	@Override
	public int compareTo(CellContainer o) {
		return labelProvider.getLabel().compareTo(o.labelProvider.getLabel());
	}
}
