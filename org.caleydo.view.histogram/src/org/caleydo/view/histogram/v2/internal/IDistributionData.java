/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import java.util.Collection;
import java.util.Set;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.layout.IHasGLLayoutData;

/**
 * @author Samuel Gratzl
 *
 */
public interface IDistributionData extends IHasGLLayoutData {
	Histogram getHist();

	String getBinName(int bin);

	Color getBinColor(int bin);

	int size();

	Set<Integer> getElements(SelectionType type);

	void select(Collection<Integer> ids, SelectionType selectionType, boolean clear);

	/**
	 * @param callback
	 */
	void onChange(GLElement callback);

	/**
	 * @param dataIndex
	 */
	int getBinOf(int dataIndex);
}
