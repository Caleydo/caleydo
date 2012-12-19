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
package org.caleydo.view.stratomex.event;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.color.IColor;
import org.caleydo.view.stratomex.GLStratomex;

/**
 * triggers that the
 *
 * @author Samuel Gratzl
 *
 */
public class HighlightBrickEvent extends AEvent {
	private TablePerspective stratification;
	private GLStratomex receiver;
	private IColor color;
	private Group group;

	public HighlightBrickEvent(TablePerspective stratification, GLStratomex receiver, Object sender, IColor color) {
		this.setSender(sender);
		this.receiver = receiver;
		this.stratification = stratification;
		this.color = color;
	}

	public HighlightBrickEvent(TablePerspective stratification, Group group, GLStratomex receiver, Object sender,
			IColor color) {
		this.setSender(sender);
		this.stratification = stratification;
		this.group = group;
		this.receiver = receiver;
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
	public IColor getColor() {
		return color;
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	public GLStratomex getReceiver() {
		return receiver;
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
	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.event.AEvent#checkIntegrity()
	 */
	@Override
	public boolean checkIntegrity() {
		return stratification != null && receiver != null;
	}
}
