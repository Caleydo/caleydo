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
package org.caleydo.core.event.view;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * This event should be triggered when the {@link TablePerspective}(s) of a view change(s).
 * 
 * @author Partl
 * @author Alexander Lex
 */
public class TablePerspectivesChangedEvent
	extends AEvent {

	private AGLView view;

	public TablePerspectivesChangedEvent(AGLView view) {
		this.setView(view);
	}

	@Override
	public boolean checkIntegrity() {
		if (view == null)
			return false;
		return true;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

	public AGLView getView() {
		return view;
	}

}
