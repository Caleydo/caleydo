/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.vis.lineup.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class StratificationDataDomainQuery extends ADataDomainQuery {
	public static final String PROP_DIMENSION_SELECTION = "dimensionSelection";

	private Perspective dimensionSelection = null;

	// snapshot when creating the data for fast comparison
	private Set<String> snapshot;

	public StratificationDataDomainQuery(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return (ATableBasedDataDomain) super.getDataDomain();
	}

	@Override
	public boolean apply(AScoreRow row) {
		return true;
	}

	@Override
	protected List<AScoreRow> getAll() {
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;

		List<AScoreRow> r = new ArrayList<>();
		this.snapshot = new HashSet<>(d.getRecordPerspectiveIDs());
		for (String rowPerspectiveID : d.getRecordPerspectiveIDs()) {
			Perspective p = d.getTable().getRecordPerspective(rowPerspectiveID);
			if (p.isPrivate())
				continue;
			// include the default stratificatition see #1227
			r.add(new StratificationPerspectiveRow(p, this));
		}
		return r;
	}

	@Override
	public List<AScoreRow> onDataDomainUpdated() {
		if (!isInitialized()) // not yet used
			return null;
		ATableBasedDataDomain d = getDataDomain();

		Set<String> current = new TreeSet<>(d.getRecordPerspectiveIDs());

		if (snapshot.equals(d.getRecordPerspectiveIDs()))
			return null;

		// black list and remove existing

		BitSet blackList = new BitSet();
		{
			int i = 0;
			for (AScoreRow row : data) {
				StratificationPerspectiveRow r = (StratificationPerspectiveRow) row;
				Perspective perspective = r.getStratification();
				blackList.set(i++, !current.remove(perspective.getPerspectiveID()));
			}
		}
		for (int i = blackList.nextSetBit(0); i >= 0; i = blackList.nextSetBit(i + 1)) {
			data.set(i, null); // clear out
		}

		// add new stuff
		List<AScoreRow> added = new ArrayList<>(1);
		for (String rowPerspectiveID : current) {
			Perspective p = d.getTable().getRecordPerspective(rowPerspectiveID);
			if (p.isDefault() || p.isPrivate())
				continue;
			// try to reuse old entries
			// we have add some stuff
			added.add(new StratificationPerspectiveRow(p, this));
		}
		if (added.isEmpty())
			updateFilter();

		snapshot = new TreeSet<>(d.getRecordPerspectiveIDs());
		return added;
	}

	/**
	 * @param dimensionPerspectiveID
	 * @param p
	 * @return
	 */
	public TablePerspective asTablePerspective(Perspective p) {
		ATableBasedDataDomain d = getDataDomain();

		String dimensionPerspectiveID = getDimensionSelection().getPerspectiveID();

		// boolean existsAlready = d.hasTablePerspective(p.getPerspectiveID(), dimensionPerspectiveID);

		TablePerspective per = d.getTablePerspective(p.getPerspectiveID(), dimensionPerspectiveID);

		// We do not want to overwrite the state of already existing
		// public table perspectives.
		// if (!existsAlready)
		// per.setPrivate(true);

		return per;
	}

	/**
	 * @return
	 */
	public Collection<Perspective> getDimensionPerspectives() {
		Collection<Perspective> r = new ArrayList<>();
		Table table = getDataDomain().getTable();
		for (String id : table.getDimensionPerspectiveIDs()) {
			r.add(table.getDimensionPerspective(id));
		}
		return r;
	}

	/**
	 * @return the dimensionSelection, see {@link #dimensionSelection}
	 */
	public Perspective getDimensionSelection() {
		if (dimensionSelection != null)
			return dimensionSelection;
		for (Perspective p : getDimensionPerspectives()) {
			if (p.isDefault())
				continue;
			return p;
		}
		return getDimensionPerspectives().iterator().next();
	}


	@Override
	public boolean hasFilter() {
		return false;
	}

	@Override
	public boolean isFilteringPossible() {
		return false;
	}

	/**
	 * @param d
	 */
	public void setDimensionSelection(Perspective d) {
		if (Objects.equals(dimensionSelection, d))
			d = null;
		if (Objects.equals(dimensionSelection, d))
			return;
		propertySupport.firePropertyChange(PROP_DIMENSION_SELECTION, dimensionSelection, dimensionSelection = d);
	}

	@Override
	public void createSpecificColumns(RankTableModel table) {

	}

	@Override
	public void removeSpecificColumns(RankTableModel table) {

	}

	@Override
	public void updateSpecificColumns(RankTableModel table) {

	}

}
