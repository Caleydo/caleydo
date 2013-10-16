/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.model;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;

/**
 * @author Samuel Gratzl
 *
 */
public class GroupInfo implements ILabeled {
	private final String label;
	private final int size;
	private final Color color;

	/**
	 * @param label
	 * @param size
	 */
	public GroupInfo(String label, int size, Color color) {
		super();
		this.label = label;
		this.size = size;
		this.color = color;
	}

	/**
	 * @return the color, see {@link #color}
	 */
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

	/**
	 * @return the size, see {@link #size}
	 */
	public int getSize() {
		return size;
	}
}
