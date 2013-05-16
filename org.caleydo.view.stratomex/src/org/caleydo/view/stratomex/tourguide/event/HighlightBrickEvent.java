/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.tourguide.event;

import java.awt.Color;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.ADirectedEvent;

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
