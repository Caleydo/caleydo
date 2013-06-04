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
package org.caleydo.view.info.selection.external;

import org.caleydo.view.info.selection.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * @author Samuel Gratzl
 *
 */
public class OpenExternalWrappingAction extends Action {
	private IAction wrappee = null;

	public OpenExternalWrappingAction() {
		super("Search In ...", Activator.getImageDescriptor("resources/icons/external.png"));
		setEnabled(false);
	}

	/**
	 * @param wrappee
	 *            setter, see {@link wrappee}
	 */
	public void setWrappee(IAction wrappee) {
		this.wrappee = wrappee;
		setEnabled(wrappee != null);
		setText(wrappee == null ? "Search In ..." : wrappee.getText());
	}

	/**
	 * @return the wrappee, see {@link #wrappee}
	 */
	public IAction getWrappee() {
		return wrappee;
	}

	@Override
	public void run() {
		if (wrappee != null)
			wrappee.run();
	}
}

