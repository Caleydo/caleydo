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
package org.caleydo.view.scatterplot;

import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.scatterplot.toolbar.DataSelectionBarGUI;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 *
 * @author CagatayTurkay
 */
public class RcpGLScatterplotView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLScatterplotView() {
		super(SerializedScatterplotView.class);
	}

	@Override
	public void createPartControl(Composite parent) {		
		super.createPartControl(parent);

		view = new GLScatterplot(glCanvas);
		initializeView();
		createPartControlGL();
		
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedScatterplotView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLScatterplot.VIEW_TYPE;
	}
	
	@Override
	public void addToolBarContent() {

		// ((GLScatterplot) this.getView()).getRootElement().getTablePerspective()
		DataSelectionBarGUI toolBarToAdd = new DataSelectionBarGUI((GLScatterplot) this.getView()); 		
		//((GLScatterplot) this.getView()).setToolbar(toolBarToAdd);
		toolBarManager.add(toolBarToAdd);
		//toolBarManager.add(new OpenOnlineHelpAction(
		//		"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/stratomex", true));

		toolBarManager.update(true);
	}

}