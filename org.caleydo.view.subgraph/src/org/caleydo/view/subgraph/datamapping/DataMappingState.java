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
package org.caleydo.view.subgraph.datamapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.events.ClearGroupSelectionEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;

/**
 * Holds the currently selected data sets and stratifications.
 *
 * @author Marc Streit
 *
 */
public class DataMappingState {

	/** All considered table perspectives */
	private List<TablePerspective> mappedTablePerspectives = new ArrayList<TablePerspective>();

	/** The selected grouping */
	private Perspective selectedPerspective;

	private TablePerspective pathwayMappedTablePerspective;

	private HashMap<ATableBasedDataDomain, TablePerspective> hashDDToTablePerspective = new HashMap<ATableBasedDataDomain, TablePerspective>();

	private final String eventSpace;

	public DataMappingState(String eventSpace) {
		this.eventSpace = eventSpace;
	}

	public List<TablePerspective> getTablePerspectives() {
		return mappedTablePerspectives;
	}

	public void addDataDomain(ATableBasedDataDomain dd) {

		addTablePerspective(dd, selectedPerspective);
	}

	public void addTablePerspective(ATableBasedDataDomain dd, Perspective recordPerspective) {

		Perspective convertedPerspective = dd.convertForeignPerspective(recordPerspective);
		TablePerspective tablePerspective = new TablePerspective(dd, convertedPerspective, dd.getTable()
				.getDefaultDimensionPerspective(false));
		tablePerspective.setLabel(dd.getLabel() + " - " + recordPerspective.getLabel());

		mappedTablePerspectives.add(tablePerspective);
		hashDDToTablePerspective.put(dd, tablePerspective);

		AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(tablePerspective);
		event.setEventSpace(eventSpace);
		event.setSender(this);
		EventPublisher.trigger(event);
	}

	public void removeDataDomain(ATableBasedDataDomain dd) {

		if (!hashDDToTablePerspective.containsKey(dd))
			return;

		AEvent event = new RemoveTablePerspectiveEvent(hashDDToTablePerspective.get(dd));
		event.setEventSpace(eventSpace);
		event.setSender(this);
		EventPublisher.trigger(event);

		mappedTablePerspectives.remove(hashDDToTablePerspective.get(dd));
		hashDDToTablePerspective.remove(dd);
	}

	/** Removes a previous and sets the new perspective on the event space */
	public void setPerspective(Perspective perspective) {
		selectedPerspective = perspective;

		ClearGroupSelectionEvent clearEvent = new ClearGroupSelectionEvent();
		EventPublisher.trigger(clearEvent);

		for (TablePerspective tablePerspective : mappedTablePerspectives) {
			AEvent event = new RemoveTablePerspectiveEvent(tablePerspective);
			event.setEventSpace(eventSpace);
			event.setSender(this);
			EventPublisher.trigger(event);
		}

		mappedTablePerspectives.clear();
		for (ATableBasedDataDomain dd : hashDDToTablePerspective.keySet()) {
			addTablePerspective(dd, selectedPerspective);
		}
	}

	/** Returns all data domains that are currently mapped */
	public Set<ATableBasedDataDomain> getDataDomains() {
		return hashDDToTablePerspective.keySet();
	}

	public TablePerspective getMatchingTablePerspective(ATableBasedDataDomain dd) {
		return hashDDToTablePerspective.get(dd);
	}

	/**
	 * @param pathwayMappedTablePerspective
	 *            setter, see {@link pathwayMappedTablePerspective}
	 */
	public void setPathwayMappedTablePerspective(TablePerspective pathwayMappedTablePerspective) {
		this.pathwayMappedTablePerspective = pathwayMappedTablePerspective;
	}

	/**
	 * @return the pathwayMappedTablePerspective, see {@link #pathwayMappedTablePerspective}
	 */
	public TablePerspective getPathwayMappedTablePerspective() {
		return pathwayMappedTablePerspective;
	}
}
