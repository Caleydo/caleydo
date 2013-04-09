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
package org.caleydo.view.table.menu;

import org.caleydo.view.table.RcpGLTableView;
import org.eclipse.jface.action.Action;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectDataRepresentationAction extends Action {
	private final boolean raw;
	private final RcpGLTableView view;

	public SelectDataRepresentationAction(RcpGLTableView view, boolean raw) {
		super(raw ? "Raw Values" : "Normalized Values");
		if (raw)
			setToolTipText("Show the raw values of the table");
		else
			setToolTipText("Show the normalized values of the table");
		this.raw = raw;
		this.view = view;
	}

	@Override
	public void run() {
		view.setDataRepresentation(raw);
	}

}
