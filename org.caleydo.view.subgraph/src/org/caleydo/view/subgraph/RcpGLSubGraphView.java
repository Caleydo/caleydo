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
package org.caleydo.view.subgraph;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.datadomain.pathway.toolbar.ClearPathAction;
import org.caleydo.datadomain.pathway.toolbar.SelectPathAction;
import org.caleydo.view.subgraph.toolbar.ShowPortalsAction;
import org.eclipse.swt.widgets.Composite;

/**
 * RCP view container for {@link GLStratomex}
 *
 * @author <Alexander Lex
 */
public class RcpGLSubGraphView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLSubGraphView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedSubGraphView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		GLSubGraph subgraph = new GLSubGraph(glCanvas, serializedView.getViewFrustum());
		view = subgraph;
		initializeView();

		createPartControlGL();
	}

	@Override
	public void addToolBarContent() {

		GLSubGraph subgraph = (GLSubGraph) view;

		SelectPathAction selectPathAction = new SelectPathAction(false, subgraph.getPathEventSpace());
		ShowPortalsAction showPortalsAction = new ShowPortalsAction(subgraph.getPathEventSpace());
		// HighlightAllPortalsAction highlightAllPortalsAction = new HighlightAllPortalsAction(subgraph);
		// subgraph.setHighlightAllPortalsButton(highlightAllPortalsAction);
		subgraph.setShowPortalsButton(showPortalsAction);

		// if (view instanceof GLSubGraph)
		// ((GLSubGraph) view).setSelectPathAction(selectPathAction);
		toolBarManager.add(selectPathAction);
		toolBarManager.add(new ClearPathAction(subgraph.getPathEventSpace()));
		toolBarManager.add(showPortalsAction);
		// toolBarManager.add(highlightAllPortalsAction);

		toolBarManager.add(new OpenOnlineHelpAction(
				"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/pathways", false));

	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedSubGraphView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLSubGraph.VIEW_TYPE;
	}

}