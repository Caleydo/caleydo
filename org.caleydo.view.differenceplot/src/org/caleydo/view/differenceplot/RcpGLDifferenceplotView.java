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
package org.caleydo.view.differenceplot;

import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.differenceplot.toolbar.DataSelectionBarGUI;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 *
 * @author CagatayTurkay
 */
public class RcpGLDifferenceplotView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDifferenceplotView() {
		super(SerializedDifferenceplotView.class);
	}

	@Override
	public void createPartControl(Composite parent) {		
		super.createPartControl(parent);

		view = new GLDifferenceplot(glCanvas);
		initializeView();
		createPartControlGL();
		
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedDifferenceplotView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLDifferenceplot.VIEW_TYPE;
	}
	
	@Override
	public void addToolBarContent() {

		DataSelectionBarGUI toolBarToAdd = new DataSelectionBarGUI((GLDifferenceplot) this.getView()); 	
		toolBarManager.add(toolBarToAdd);
		//toolBarManager.add(new OpenOnlineHelpAction(
		//		"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/stratomex", true));

		toolBarManager.update(true);
	}

}