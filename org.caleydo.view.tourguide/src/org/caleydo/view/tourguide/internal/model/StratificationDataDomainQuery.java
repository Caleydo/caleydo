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
import org.caleydo.vis.rank.model.RankTableModel;

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

	private String getDimensionPerspectiveID() {
		String dimensionPerspectiveID = null;
		if (dimensionSelection != null)
			dimensionPerspectiveID = dimensionSelection.getPerspectiveID();
		else {
			for (Perspective p : getDimensionPerspectives()) {
				if (!p.isDefault())
					return p.getPerspectiveID();
			}
			return getDimensionPerspectives().iterator().next().getPerspectiveID();
		}
		return dimensionPerspectiveID;
	}

	/**
	 * @param dimensionPerspectiveID
	 * @param p
	 * @return
	 */
	public TablePerspective asTablePerspective(Perspective p) {
		ATableBasedDataDomain d = getDataDomain();

		String dimensionPerspectiveID = getDimensionPerspectiveID();

		boolean existsAlready = d.hasTablePerspective(p.getPerspectiveID(), dimensionPerspectiveID);

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
		return dimensionSelection;
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

}
