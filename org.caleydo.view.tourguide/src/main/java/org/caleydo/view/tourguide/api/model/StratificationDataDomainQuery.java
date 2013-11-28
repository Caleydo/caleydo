/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.view.tourguide.api.prefs.MyPreferences;
import org.caleydo.vis.lineup.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class StratificationDataDomainQuery extends ADataDomainQuery {
	public static final String PROP_DIMENSION_SELECTION = "dimensionSelection";

	private final EDimension dim;

	private Perspective dimensionSelection = null;

	// snapshot when creating the data for fast comparison
	private Set<String> snapshot;


	public StratificationDataDomainQuery(ATableBasedDataDomain dataDomain, EDimension dim) {
		super(dataDomain);
		this.dim = dim;
		setMinSize(MyPreferences.getMinClusterSize());
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
		Set<String> ids = dim.isRecord() ? d.getRecordPerspectiveIDs() : d.getDimensionPerspectiveIDs();
		this.snapshot = new HashSet<>(ids);
		for (String rowPerspectiveID : ids) {
			Perspective p = dim.isRecord() ? d.getTable().getRecordPerspective(rowPerspectiveID) : d.getTable()
					.getDimensionPerspective(rowPerspectiveID);
			if (p.isPrivate())
				continue;
			// include the default stratification see #1227
			r.add(new StratificationPerspectiveRow(p, dim, this));
		}
		return r;
	}

	@Override
	public List<AScoreRow> onDataDomainUpdated() {
		if (!isInitialized()) // not yet used
			return null;
		ATableBasedDataDomain d = getDataDomain();

		Set<String> ids = dim.isRecord() ? d.getRecordPerspectiveIDs() : d.getDimensionPerspectiveIDs();
		Set<String> current = new TreeSet<>(ids);

		if (snapshot.equals(ids))
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
			Perspective p = dim.isRecord() ? d.getTable().getRecordPerspective(rowPerspectiveID) : d.getTable()
					.getDimensionPerspective(rowPerspectiveID);
			if (p.isDefault() || p.isPrivate())
				continue;
			// try to reuse old entries
			// we have add some stuff
			added.add(new StratificationPerspectiveRow(p, dim, this));
		}
		if (added.isEmpty())
			updateFilter();

		snapshot = new TreeSet<>(ids);
		return added;
	}

	/**
	 * @param dimensionPerspectiveID
	 * @param p
	 * @return
	 */
	public TablePerspective asTablePerspective(Perspective p) {
		ATableBasedDataDomain d = getDataDomain();

		String dimensionPerspectiveID = getOppositeSelection().getPerspectiveID();

		// boolean existsAlready = d.hasTablePerspective(p.getPerspectiveID(), dimensionPerspectiveID);

		TablePerspective per = d.getTablePerspective(dim.select(dimensionPerspectiveID, p.getPerspectiveID()),
				dim.select(p.getPerspectiveID(), dimensionPerspectiveID));

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
		for (String id : dim.select(table.getRecordPerspectiveIDs(), table.getDimensionPerspectiveIDs())) {
			r.add(dim.isRecord() ? table.getDimensionPerspective(id) : table.getRecordPerspective(id));
		}
		return r;
	}

	/**
	 * @return the dimensionSelection, see {@link #dimensionSelection}
	 */
	public Perspective getOppositeSelection() {
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
	protected boolean hasFilterImpl() {
		return false;
	}

	/**
	 * @param d
	 */
	public void setOppositeSelection(Perspective d) {
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

	/**
	 * @return
	 */
	public String getOppositeIDType() {
		return dim.select(getDataDomain().getRecordIDCategory(), getDataDomain().getDimensionIDCategory())
				.getCategoryName();
	}

	/**
	 * @return
	 */
	public IDType getIDType() {
		return dim.select(getDataDomain().getDimensionIDType(), getDataDomain().getRecordIDType());
	}

}
