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
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.util.clusterer.gui.ClusterDialog;
import org.eclipse.swt.widgets.Shell;

public class StartClusteringAction extends SimpleAction {

	public static final String LABEL = "Clustering";
	public static final String ICON = "resources/icons/view/tablebased/clustering.png";

	/**
	 * Constructor.
	 */
	public StartClusteringAction() {
		super(LABEL, ICON);
	}

	@Override
	public void run() {
		super.run();

		ClusterDialog dialog = new ClusterDialog(new Shell());

		dialog.open();
	}
}