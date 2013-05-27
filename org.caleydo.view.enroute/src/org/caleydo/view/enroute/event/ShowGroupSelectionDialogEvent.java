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
package org.caleydo.view.enroute.event;

import java.util.List;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.AEvent;

/**
 * @author Alexander Lex
 *
 */
public class ShowGroupSelectionDialogEvent extends AEvent {

	List<Perspective> perspectives;

	/**
	 *
	 */
	public ShowGroupSelectionDialogEvent(List<Perspective> perspectives) {
		this.perspectives = perspectives;
	}

	/**
	 * @return the perspective, see {@link #perspective}
	 */
	public List<Perspective> getPerspectives() {
		return perspectives;
	}

	/**
	 * @param perspective
	 *            setter, see {@link perspective}
	 */
	public void setPerspective(List<Perspective> perspectives) {
		this.perspectives = perspectives;
	}

	@Override
	public boolean checkIntegrity() {
		if (perspectives != null)
			return true;

		return false;
	}

}
