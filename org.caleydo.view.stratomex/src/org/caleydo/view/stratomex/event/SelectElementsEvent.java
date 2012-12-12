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
import org.caleydo.view.stratomex.GLStratomex;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectElementsEvent extends AEvent {

	private GLStratomex receiver;

	private TablePerspective aStrat;
	private Group aGroup;
	private TablePerspective bStrat;
	private Group bGroup;

	public SelectElementsEvent() {

	}

	public SelectElementsEvent(TablePerspective aStrat, Group aGroup, TablePerspective bStrat, Group bGroup,
			GLStratomex receiver, Object sender) {
		this.setSender(sender);
		this.receiver = receiver;
		this.aStrat = aStrat;
		this.aGroup = aGroup;
		this.bStrat = bStrat;
		this.bGroup = bGroup;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.event.AEvent#checkIntegrity()
	 */
	@Override
	public boolean checkIntegrity() {
		return aStrat != null && aGroup != null && bStrat != null && bGroup != null;
	}

	/**
	 * @return the aGroup, see {@link #aGroup}
	 */
	public Group getaGroup() {
		return aGroup;
	}

	/**
	 * @return the aStrat, see {@link #aStrat}
	 */
	public TablePerspective getaStrat() {
		return aStrat;
	}

	/**
	 * @return the bGroup, see {@link #bGroup}
	 */
	public Group getbGroup() {
		return bGroup;
	}

	/**
	 * @return the bStrat, see {@link #bStrat}
	 */
	public TablePerspective getbStrat() {
		return bStrat;
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	public GLStratomex getReceiver() {
		return receiver;
	}

}
