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
package org.caleydo.view.search;

import java.util.ArrayList;
import java.util.List;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.browser.ChangeURLEvent;
import org.caleydo.core.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

public class SearchViewMediator {

	EventPublisher eventPublisher;

	public SearchViewMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public void loadPathway(int pathwayID) {
		LoadPathwayEvent event = new LoadPathwayEvent();
		event.setSender(this);
		event.setPathwayID(pathwayID);
		eventPublisher.triggerEvent(event);
	}

	public void loadURLInBrowser(String url) {
		ChangeURLEvent event = new ChangeURLEvent();
		event.setSender(this);
		event.setUrl(url);
		eventPublisher.triggerEvent(event);
	}

	public void loadPathwayByGene(int davidID) {
		LoadPathwaysByGeneEvent loadPathwaysByGeneEvent = new LoadPathwaysByGeneEvent();
		loadPathwaysByGeneEvent.setSender(this);
		loadPathwaysByGeneEvent.setGeneID((davidID));
		loadPathwaysByGeneEvent.setTableIDType(IDType.getIDType("DAVID"));
		eventPublisher.triggerEvent(loadPathwaysByGeneEvent);
	}

	public void selectGeneSystemWide(int davidID) {

		IDType davidIDType = IDType.getIDType("DAVID");
		// First the current selections need to be cleared
		ClearSelectionsEvent clearSelectionsEvent = new ClearSelectionsEvent();
		clearSelectionsEvent.setSender(this);
		eventPublisher.triggerEvent(clearSelectionsEvent);

		// Create new selection with the selected david ID
		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSender(this);
		SelectionDelta delta = new SelectionDelta(davidIDType);
		// Set<Integer> setExpIndex = GeneralManager.get().getIDMappingManager()
		// .getIDAsSet(IDType.getIDType("DAVID"), recordIDType, davidID);

		// for (Integer expressionIndex : setExpIndex) {
		delta.addSelection(davidID, SelectionType.SELECTION);
		// }

		selectionUpdateEvent.setSelectionDelta((SelectionDelta) delta);
		eventPublisher.triggerEvent(selectionUpdateEvent);
	}
}
