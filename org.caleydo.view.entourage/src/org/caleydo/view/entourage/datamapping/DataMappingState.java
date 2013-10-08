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
package org.caleydo.view.entourage.datamapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.events.ClearGroupSelectionEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;
import org.caleydo.view.entourage.GLEntourage;

/**
 * Holds the currently selected data sets and stratifications.
 *
 * @author Marc Streit
 *
 */
public class DataMappingState {

	/** All considered table perspectives */
	private List<TablePerspective> mappedTablePerspectives = new ArrayList<TablePerspective>();

	/** The the perspective that gets displayed. It represents a subsets of the groups in {@link #sourcePerspective}. */
	private Perspective selectedPerspective;

	/**
	 * The perspective {@link #selectedPerspective} is based on.
	 */
	private Perspective sourcePerspective;

	private TablePerspective pathwayMappedTablePerspective;

	private HashMap<ATableBasedDataDomain, TablePerspective> hashDDToTablePerspective = new HashMap<ATableBasedDataDomain, TablePerspective>();

	private final String eventSpace;

	private final GLEntourage entourage;

	public DataMappingState(GLEntourage entourage) {
		this.entourage = entourage;
		this.eventSpace = entourage.getPathEventSpace();
		applyDefaultState();
	}

	private void applyDefaultState() {

		List<ATableBasedDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class);
		if (dataDomains == null || dataDomains.isEmpty())
			return;

		Collections.sort(dataDomains, new Comparator<ATableBasedDataDomain>() {

			@Override
			public int compare(ATableBasedDataDomain dd1, ATableBasedDataDomain dd2) {
				return dd1.getLabel().compareTo(dd2.getLabel());
			}
		});
		GeneticDataDomain geneticDataDomain = null;
		for (ATableBasedDataDomain dd : dataDomains) {
			if (geneticDataDomain == null && dd instanceof GeneticDataDomain) {
				geneticDataDomain = (GeneticDataDomain) dd;
				break;
			}
		}

		if (geneticDataDomain != null) {
			IDCategory geneIDCategory = geneticDataDomain.getGeneIDType().getIDCategory();
			if (geneticDataDomain.getRecordIDCategory() == geneIDCategory) {
				sourcePerspective = geneticDataDomain.getTable().getDefaultDimensionPerspective(false);
				setSelectedPerspective(sourcePerspective);
			} else {
				sourcePerspective = geneticDataDomain.getTable().getDefaultRecordPerspective(false);
				setSelectedPerspective(sourcePerspective);
			}
			addDataDomain(geneticDataDomain);
		} else {
			ATableBasedDataDomain dd = dataDomains.get(0);
			sourcePerspective = dd.getTable().getDefaultRecordPerspective(false);
			setSelectedPerspective(sourcePerspective);
			addDataDomain(dd);
		}
	}

	public List<TablePerspective> getTablePerspectives() {
		return mappedTablePerspectives;
	}

	public void addDataDomain(ATableBasedDataDomain dd) {
		if (hashDDToTablePerspective.containsKey(dd))
			return;

		addTablePerspective(dd, selectedPerspective);
	}

	public void addTablePerspective(ATableBasedDataDomain dd, Perspective recordPerspective) {

		TablePerspective tablePerspective = createTablePerspective(dd, recordPerspective);

		mappedTablePerspectives.add(tablePerspective);
		hashDDToTablePerspective.put(dd, tablePerspective);

		AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(tablePerspective);
		event.setEventSpace(eventSpace);
		event.setSender(this);
		EventPublisher.trigger(event);
		TablePerspectivesChangedEvent e = new TablePerspectivesChangedEvent(entourage);
		e.setSender(this);
		EventPublisher.trigger(e);
	}

	private TablePerspective createTablePerspective(ATableBasedDataDomain dd, Perspective foreignPerspective) {
		if (dd == null || foreignPerspective == null)
			return null;
		Perspective convertedPerspective = dd.convertForeignPerspective(foreignPerspective);
		TablePerspective tablePerspective = new TablePerspective(dd, convertedPerspective, dd.getTable()
				.getDefaultDimensionPerspective(false));
		tablePerspective.setLabel(dd.getLabel() + " - " + foreignPerspective.getLabel());
		return tablePerspective;
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

		TablePerspectivesChangedEvent e = new TablePerspectivesChangedEvent(entourage);
		e.setSender(this);
		EventPublisher.trigger(e);
	}

	/** Removes a previous and sets the new perspective on the event space */
	public void setSelectedPerspective(Perspective perspective) {
		if (this.selectedPerspective == perspective)
			return;
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
		if (selectedPerspective != null) {
			for (ATableBasedDataDomain dd : hashDDToTablePerspective.keySet()) {
				addTablePerspective(dd, selectedPerspective);
			}
		}

		// also update pathway mapping
		if (pathwayMappedTablePerspective != null) {
			setPathwayMappedDataDomain(pathwayMappedTablePerspective.getDataDomain());
		}
		TablePerspectivesChangedEvent e = new TablePerspectivesChangedEvent(entourage);
		e.setSender(this);
		EventPublisher.trigger(e);
	}

	/** Returns all data domains that are currently mapped */
	public Set<ATableBasedDataDomain> getDataDomains() {
		return hashDDToTablePerspective.keySet();
	}

	public TablePerspective getMatchingTablePerspective(ATableBasedDataDomain dd) {
		return hashDDToTablePerspective.get(dd);
	}

	/**
	 * Creates and sets a table perspective based on the provided data domain.
	 *
	 * @param dd
	 */
	public void setPathwayMappedDataDomain(ATableBasedDataDomain dd) {
		if (dd == null)
			setPathwayMappedTablePerspective(null);
		TablePerspective tablePerspective = createTablePerspective(dd, selectedPerspective);
		setPathwayMappedTablePerspective(tablePerspective);
	}

	/**
	 * @param pathwayMappedTablePerspective
	 *            setter, see {@link pathwayMappedTablePerspective}
	 */
	public void setPathwayMappedTablePerspective(TablePerspective pathwayMappedTablePerspective) {
		this.pathwayMappedTablePerspective = pathwayMappedTablePerspective;
		PathwayMappingEvent event = new PathwayMappingEvent(pathwayMappedTablePerspective);
		event.setEventSpace(eventSpace);
		EventPublisher.trigger(event);
	}

	/**
	 * @return the pathwayMappedTablePerspective, see {@link #pathwayMappedTablePerspective}
	 */
	public TablePerspective getPathwayMappedTablePerspective() {
		return pathwayMappedTablePerspective;
	}

	/**
	 * @return the selectedPerspective, see {@link #selectedPerspective}
	 */
	public Perspective getSelectedPerspective() {
		return selectedPerspective;
	}

	/**
	 * @param sourcePerspective
	 *            setter, see {@link sourcePerspective}
	 */
	public void setSourcePerspective(Perspective sourcePerspective) {
		this.sourcePerspective = sourcePerspective;
	}

	/**
	 * @return the sourcePerspective, see {@link #sourcePerspective}
	 */
	public Perspective getSourcePerspective() {
		return sourcePerspective;
	}
}
