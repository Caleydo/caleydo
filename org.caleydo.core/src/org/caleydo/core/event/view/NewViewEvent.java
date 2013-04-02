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

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewManager;

/**
 * This Event is triggered when a new view is registered at the {@link ViewManager}.
 *
 * @author Partl
 */
public class NewViewEvent
	extends AEvent {

	private IView view;

	public NewViewEvent(IView view) {
		this.setView(view);
	}

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setView(IView view) {
		this.view = view;
	}

	public IView getView() {
		return view;
	}

}
