/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.layout.IHasGLLayoutData;
import org.caleydo.core.view.opengl.util.gleem.IColored;

/**
 * @author Samuel Gratzl
 *
 */
public interface IDistributionData extends IHasGLLayoutData {
	DistributionEntry get(int entry);

	List<DistributionEntry> getEntries();

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
	DistributionEntry getOf(int dataIndex);

	public static class DistributionEntry implements ILabeled, IColored {
		private final String label;
		private final Color color;
		private final float value;
		private final Set<Integer> ids;

		public DistributionEntry(String label, Color color, float value, Set<Integer> ids) {
			this.label = label;
			this.color = color;
			this.value = value;
			this.ids = ids;
		}

		/**
		 * @return the value, see {@link #value}
		 */
		public float getValue() {
			return value;
		}

		/**
		 * @return the ids, see {@link #ids}
		 */
		public Set<Integer> getIDs() {
			return ids;
		}

		/**
		 * @return the color, see {@link #color}
		 */
		@Override
		public Color getColor() {
			return color;
		}

		/**
		 * @return the label, see {@link #label}
		 */
		@Override
		public String getLabel() {
			return label;
		}
	}

	/**
	 * @return
	 */
	boolean hasIds();
}
