/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.virtualarray.group.Group;

/**
 * @author Samuel Gratzl
 *
 */
public interface IHeatMapDataProvider {
	String getLabel(EDimension dim, Integer id);

	List<Integer> getData(EDimension dim);

	SelectionManager getManager(EDimension dim);

	void fireSelectionChanged(SelectionManager manager);

	void setCallback(IDataChangedCallback callback);

	public interface IDataChangedCallback {
		void onDataUpdate();

		void onSelectionUpdate();
	}

	List<Group> getGroups(EDimension dim);

	int indexOf(EDimension dim, Integer id);
}
