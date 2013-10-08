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
package org.caleydo.view.entourage;

import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.datadomain.pathway.toolbar.ClearPathAction;
import org.caleydo.datadomain.pathway.toolbar.SelectPathAction;
import org.caleydo.view.entourage.datamapping.DataMappers;
import org.caleydo.view.entourage.toolbar.ClearWorkspaceAction;
import org.caleydo.view.entourage.toolbar.ShowDataMapperAction;
import org.caleydo.view.entourage.toolbar.ShowPortalsAction;
import org.eclipse.jface.action.IToolBarManager;
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
		super(SerializedSubGraphView.class);

		// try {
		// viewContext = JAXBContext.newInstance(SerializedSubGraphView.class);
		// } catch (JAXBException ex) {
		// throw new RuntimeException("Could not create JAXBContext", ex);
		// }
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		GLEntourage subgraph = new GLEntourage(glCanvas, serializedView.getViewFrustum());
		view = subgraph;
		initializeView();

		createPartControlGL();

		DataMappers.getDataMapper().show();
	}

	@Override
	public GLEntourage getView() {
		return (GLEntourage) super.getView();
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {

		GLEntourage entourage = (GLEntourage) view;

		SelectPathAction selectPathAction = new SelectPathAction(false, entourage.getPathEventSpace());
		entourage.setSelectPathAction(selectPathAction);
		// SelectFreePathAction selectFreePathAction = new SelectFreePathAction(false, entourage.getPathEventSpace());
		// selectPathAction.setSelectFreePathAction(selectFreePathAction);
		// selectFreePathAction.setSelectPathAction(selectPathAction);
		ShowPortalsAction showPortalsAction = new ShowPortalsAction(entourage.getPathEventSpace());
		// HighlightAllPortalsAction highlightAllPortalsAction = new HighlightAllPortalsAction(subgraph);
		// subgraph.setHighlightAllPortalsButton(highlightAllPortalsAction);
		entourage.setShowPortalsButton(showPortalsAction);

		// if (view instanceof GLSubGraph)
		// ((GLSubGraph) view).setSelectPathAction(selectPathAction);
		toolBarManager.add(selectPathAction);
		// toolBarManager.add(selectFreePathAction);
		toolBarManager.add(new ClearPathAction(entourage.getPathEventSpace()));
		toolBarManager.add(showPortalsAction);
		toolBarManager.add(new ShowDataMapperAction());
		toolBarManager.add(new ClearWorkspaceAction(entourage));
		// toolBarManager.add(highlightAllPortalsAction);

		toolBarManager.add(new OpenOnlineHelpAction(GeneralManager.HELP_URL + "/views/enroute.md#Pathway_View", false));

	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedSubGraphView();
		determineDataConfiguration(serializedView);
	}

}