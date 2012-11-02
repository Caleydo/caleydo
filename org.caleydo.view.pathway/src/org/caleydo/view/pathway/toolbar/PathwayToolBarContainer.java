/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.pathway.toolbar;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.OpenOnlineHelpAction;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.SerializedPathwayView;
import org.caleydo.view.pathway.toolbar.actions.ClearPathAction;
import org.caleydo.view.pathway.toolbar.actions.SelectPathAction;

/**
 * Widget based toolbar container to display pathway related toolbar content.
 * 
 * @author Marc Streit
 */
public class PathwayToolBarContainer
	extends ToolBarContainer {

	/** Mediator to handle actions triggered by the contributed elements */
	PathwayToolBarMediator pathwayToolBarMediator;

	/** serialized remote rendering view to read the configuration from */
	ASerializedView targetViewData;

	/**
	 * Creates a the pathway selection box and add the pathway toolbar items.
	 */
	@Override
	public List<IToolBarItem> getToolBarItems() {

		List<IToolBarItem> elements = new ArrayList<IToolBarItem>();
		SerializedPathwayView serializedView = (SerializedPathwayView) getTargetViewData();

		SelectPathAction selectPathAction = new SelectPathAction(serializedView.isPathSelectionMode());
		AGLView view = GeneralManager.get().getViewManager().getGLView(this.getTargetViewData().getViewID());
		if (view instanceof GLPathway)
			((GLPathway) view).setSelectPathAction(selectPathAction);
		elements.add(selectPathAction);

		elements.add(new ClearPathAction());

		SampleSelectionMode sampleSelectionMode = new SampleSelectionMode("Select which Samples to map",
				serializedView.getMappingMode());
		elements.add(sampleSelectionMode);
		
		IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(serializedView.getDataDomainID());

		DatasetSelectionBox dataSelectionBox = new DatasetSelectionBox("Select Data", dataDomain);
		elements.add(dataSelectionBox);

		PathwaySearchBox pathwaySearchBox = new PathwaySearchBox("", serializedView.getPathwayID());
		pathwaySearchBox.setPathwayToolBarMediator(pathwayToolBarMediator);
		elements.add(pathwaySearchBox);

		elements.add(new OpenOnlineHelpAction("http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/pathways",
				false));

		// AGLView view = GeneralManager.get().getViewManager()
		// .getGLView(targetViewData.getViewID());
		// elements.add(new TakeSnapshotAction(view.getParentComposite()));

		return elements;
	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}

	public ASerializedView getTargetViewData() {
		return targetViewData;
	}

	public void setTargetViewData(ASerializedView targetViewData) {
		this.targetViewData = targetViewData;
	}
}
