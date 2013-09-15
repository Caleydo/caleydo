/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.vis.lineup.model.RankTableModel;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author Samuel Gratzl
 *
 */
public class InhomogenousDataDomainQuery extends ADataDomainQuery {
	private final Set<EDataClass> dataClass;
	private final ImmutableSet<EDataType> possible;

	private Set<EDataType> selectedDataTypes;

	// snapshot when creating the data for fast comparison
	private Set<String> snapshot;


	public InhomogenousDataDomainQuery(ATableBasedDataDomain dataDomain, Set<EDataClass> dataClasses) {
		super(dataDomain);
		this.dataClass = dataClasses;
		Builder<EDataType> builder = ImmutableSet.builder();
		for (EDataClass dataClass : dataClasses)
			builder.addAll(dataClass.getSupportedDataTypes());
		this.possible = builder.build();
		this.selectedDataTypes = new HashSet<>(possible);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return (ATableBasedDataDomain) super.getDataDomain();
	}

	@Override
	public boolean apply(AScoreRow row) {
		assert row.getDataDomain() == dataDomain;
		InhomogenousPerspectiveRow r = (InhomogenousPerspectiveRow) row;
		Perspective clinical = r.asTablePerspective().getDimensionPerspective();
		Integer dimensionID = clinical.getVirtualArray().get(0);
		EDataType type = getDataDomain().getTable().getRawDataType(dimensionID, 0);
		return selectedDataTypes.contains(type);
	}

	public static boolean hasOne(IDataDomain dataDomain, Set<EDataClass> clazzes) {
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;
		for (String dimPerspectiveID : d.getDimensionPerspectiveIDs()) {
			Perspective p = d.getTable().getDimensionPerspective(dimPerspectiveID);
			if (p.isDefault() || p.isPrivate())
				continue;
			Integer dimensionID = p.getVirtualArray().get(0);
			if (!clazzes.contains(d.getTable().getDataClass(dimensionID, 0)))
				continue;
			return true;
		}
		return false;
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
			if (!dataClass.contains(d.getTable().getDataClass(dimensionID, 0)))
				continue;
			r.add(new InhomogenousPerspectiveRow(asTablePerspective(p)));
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
			added.add(new InhomogenousPerspectiveRow(asTablePerspective(p)));
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

		String rowPerspectiveID = d.getTable().getDefaultRecordPerspective(false).getPerspectiveID();
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
	public Set<EDataClass> getDataClass() {
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
		return this.selectedDataTypes.size() < possible.size();
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

	@Override
	public void updateSpecificColumns(RankTableModel table) {

	}

	/**
	 * @return the possible, see {@link #possible}
	 */
	public ImmutableSet<EDataType> getPossibleDataTypes() {
		return possible;
	}
}
