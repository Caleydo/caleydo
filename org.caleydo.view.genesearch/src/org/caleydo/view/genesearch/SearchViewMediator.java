package org.caleydo.view.genesearch;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;

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

	public void selectGeneSystemWide(ATableBasedDataDomain dataDomain, int davidID) {

		IDType recordIDType = dataDomain.getPrimaryRecordMappingType();
		
		// First the current selections need to be cleared
		ClearSelectionsEvent clearSelectionsEvent = new ClearSelectionsEvent();
		clearSelectionsEvent.setSender(this);
		eventPublisher.triggerEvent(clearSelectionsEvent);

		// Create new selection with the selected david ID
		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSender(this);
		selectionUpdateEvent.setDataDomainID(dataDomain.getDataDomainID());
		SelectionDelta delta = new SelectionDelta(recordIDType);
//		Set<Integer> setExpIndex = GeneralManager.get().getIDMappingManager()
//				.getIDAsSet(IDType.getIDType("DAVID"), recordIDType, davidID);

//		for (Integer expressionIndex : setExpIndex) {
			delta.addSelection(davidID, SelectionType.SELECTION);
//		}

		selectionUpdateEvent.setSelectionDelta((SelectionDelta) delta);
		eventPublisher.triggerEvent(selectionUpdateEvent);
	}
}
