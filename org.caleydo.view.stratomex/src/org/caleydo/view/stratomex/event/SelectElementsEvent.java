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

import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.event.AEvent;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectElementsEvent extends AEvent {

	private RecordPerspective source;
	private RecordPerspective target;

	public SelectElementsEvent() {

	}

	public SelectElementsEvent(Object sender, RecordPerspective source, RecordPerspective target) {
		this.setSender(sender);
		this.source = source;
		this.target = target;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.event.AEvent#checkIntegrity()
	 */
	@Override
	public boolean checkIntegrity() {
		return source != null && target != null;
	}

	/**
	 * @return
	 */
	public RecordPerspective getRecordPerspective1() {
		return source;
	}

	/**
	 * @return
	 */
	public RecordPerspective getRecordPerspective2() {
		return target;
	}

}
