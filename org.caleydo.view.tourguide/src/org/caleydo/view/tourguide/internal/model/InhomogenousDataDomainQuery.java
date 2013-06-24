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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.vis.rank.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class InhomogenousDataDomainQuery extends ADataDomainQuery {
	private Set<EDataType> selectedDataTypes;

	// snapshot when creating the data for fast comparison
	private Set<String> snapshot;

	private final EDataClass dataClass;

	public InhomogenousDataDomainQuery(ATableBasedDataDomain dataDomain, EDataClass dataClass) {
		super(dataDomain);
		this.dataClass = dataClass;
		this.selectedDataTypes = new HashSet<>(dataClass.getSupportedDataTypes());
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return (ATableBasedDataDomain) super.getDataDomain();
	}

	@Override
	public boolean apply(AScoreRow row) {
		assert row.getDataDomain() == dataDomain;
		InhomogenousPerspectiveRow r = (InhomogenousPerspectiveRow) row;
		Perspective clinical = r.getStratification();
		Integer dimensionID = clinical.getVirtualArray().get(0);
		EDataType type = getDataDomain().getTable().getRawDataType(dimensionID, 0);
		return selectedDataTypes.contains(type) && dataClass.supports(type);
	}

	@Override
	protected List<AScoreRow> getAll() {
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;

		List<AScoreRow> r = new ArrayList<>();

		this.snapshot = new HashSet<>(d.getDimensionPerspectiveIDs());
		for (String dimPerspectiveID : d.getDimensionPerspectiveIDs()) {
			Perspective p = d.getTable().getDimensionPerspective(dimPerspectiveID);
			if (p.isDefault() || p.isPrivate())
				continue;
			Integer dimensionID = p.getVirtualArray().get(0);
			if (dataClass != d.getTable().getDataClass(dimensionID, 0))
				continue;
			r.add(new InhomogenousPerspectiveRow(asTablePerspective(p), this));
		}
		return r;
	}

	@Override
	public List<AScoreRow> onDataDomainUpdated() {
		if (!isInitialized()) // not yet used
			return null;
		ATableBasedDataDomain d = getDataDomain();

		Set<String> current = new TreeSet<>(d.getDimensionPerspectiveIDs());

		if (snapshot.equals(d.getDimensionPerspectiveIDs()))
			return null;

		// black list and remove existing

		BitSet blackList = new BitSet();
		{
			int i = 0;
			for (AScoreRow row : data) {
				InhomogenousPerspectiveRow r = (InhomogenousPerspectiveRow) row;
				Perspective perspective = r.getStratification();
				blackList.set(i++, !current.remove(perspective.getPerspectiveID()));
			}
		}
		for (int i = blackList.nextSetBit(0); i >= 0; i = blackList.nextSetBit(i + 1)) {
			data.set(i, null); // clear out
		}

		// add new stuff
		List<AScoreRow> added = new ArrayList<>(1);
		for (String dimPerspectiveID : current) {
			Perspective p = d.getTable().getDimensionPerspective(dimPerspectiveID);
			if (p.isDefault() || p.isPrivate())
				continue;
			// try to reuse old entries
			// we have add some stuff
			added.add(new InhomogenousPerspectiveRow(asTablePerspective(p), this));
		}
		updateFilter();

		snapshot = new TreeSet<>(d.getDimensionPerspectiveIDs());
		return added;
	}

	/**
	 * @param dimensionPerspectiveID
	 * @param p
	 * @return
	 */
	public TablePerspective asTablePerspective(Perspective p) {
		ATableBasedDataDomain d = getDataDomain();

		String rowPerspectiveID = d.getTable().getDefaultRecordPerspective().getPerspectiveID();
		for (String recId : d.getTable().getRecordPerspectiveIDs()) {
			Perspective recordPerspective = d.getTable().getRecordPerspective(recId);
			if (recordPerspective.getLabel().equals(p.getLabel())) {
				rowPerspectiveID = recId;
				break;
			}
		}
		boolean existsAlready = d.hasTablePerspective(rowPerspectiveID, p.getPerspectiveID());

		TablePerspective per = d.getTablePerspective(rowPerspectiveID, p.getPerspectiveID());

		// We do not want to overwrite the state of already existing
		// public table perspectives.
		if (!existsAlready)
			per.setPrivate(true);

		return per;
	}

	public void setMatches(Set<EDataType> selected) {
		if (Objects.equals(selectedDataTypes, selected))
			return;
		this.selectedDataTypes = selected;
		updateFilter();
	}

	/**
	 * @return the dataClass, see {@link #dataClass}
	 */
	public EDataClass getDataClass() {
		return dataClass;
	}
		/**
	 * @return the selectedDataTypes, see {@link #selectedDataTypes}
	 */
	public Set<EDataType> getSelectedDataTypes() {
		return selectedDataTypes;
	}

	@Override
	public boolean hasFilter() {
		return this.selectedDataTypes.size() < dataClass.getSupportedDataTypes().size();
	}

	@Override
	public boolean isFilteringPossible() {
		return true;
	}

	@Override
	public void createSpecificColumns(RankTableModel table) {

	}

	@Override
	public void removeSpecificColumns(RankTableModel table) {

	}
}
