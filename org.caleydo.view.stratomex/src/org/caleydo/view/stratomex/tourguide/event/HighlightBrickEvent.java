/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.tourguide.event;



import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.util.color.Color;

/**
 * triggers that the
 *
 * @author Samuel Gratzl
 *
 */
public class HighlightBrickEvent extends ADirectedEvent {
	private TablePerspective stratification;
	private Color color;
	private Group group;

	public HighlightBrickEvent(TablePerspective stratification, Group group, Color color) {
		this.stratification = stratification;
		this.group = group;
		this.color = color;
	}

	/**
	 * @return true if the specified element should be highlighted or stopping highlighting it
	 */
	public boolean isHighlight() {
		return color != null;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the stratification, see {@link #stratification}
	 */
	public TablePerspective getStratification() {
		return stratification;
	}

	/**
	 * @return the group, see {@link #group}
	 */
	public Group getGroup() {
		return group;
	}
	@Override
	public boolean checkIntegrity() {
		return stratification != null;
	}
}
