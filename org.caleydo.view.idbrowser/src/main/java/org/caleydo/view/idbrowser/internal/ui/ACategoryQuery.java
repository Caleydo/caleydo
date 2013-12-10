/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.ui;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.idbrowser.internal.model.BoxPlotRankTableModel;
import org.caleydo.view.idbrowser.internal.model.DistributionRankTableModel;
import org.caleydo.view.idbrowser.internal.model.PrimaryIDRow;
import org.caleydo.view.idbrowser.internal.model.SingleCategorical;
import org.caleydo.view.idbrowser.internal.model.SingleDouble;
import org.caleydo.view.idbrowser.internal.model.SingleInteger;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.ARow;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ACategoryQuery extends GLButton implements Comparable<ACategoryQuery> {

	private BitSet mask;

	protected final IDCategory category;
	private final Set<IDType> idTypes;

	protected final IDType primary;

	public ACategoryQuery(IDCategory category, String label, IDType primary) {
		super(EButtonMode.CHECKBOX);
		this.primary = primary;
		setRenderer(createRadioRenderer(label));
		this.category = category;
		setLayoutData(category);

		Set<IDType> idTypes = findIDTypes(category);
		this.idTypes = ImmutableSortedSet.orderedBy(new Comparator<IDType>() {
			@Override
			public int compare(IDType o1, IDType o2) {
				return String.CASE_INSENSITIVE_ORDER.compare(o1.getTypeName(), o2.getTypeName());
			}
		}).addAll(idTypes).build();
	}

	protected abstract Set<IDType> findIDTypes(IDCategory category);

	/**
	 * @return the mask, see {@link #mask}
	 */
	public BitSet getMask() {
		return mask;
	}

	public void init(int from, int to) {
		this.mask = new BitSet(to);
		this.mask.set(from, to);
	}

	public boolean inited() {
		return mask != null;
	}

	public Collection<ARow> create() {
		// find all ids and check the predicate
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(category);

		final Map<IDType, IIDTypeMapper<Object, Object>> mappings = new HashMap<>();

		for (IDType idType : idTypes) {
			IIDTypeMapper<Object, Object> m = mappingManager.getIDTypeMapper(primary, idType);
			if (m != null)
				mappings.put(idType, m);
		}

		return createEntries(mappingManager, mappings);
	}

	protected abstract Collection<ARow> createEntries(IDMappingManager mappingManager,
			final Map<IDType, IIDTypeMapper<Object, Object>> mappings);

	public void addColumns(RankTableModel table) {
		addIDTypeColumns(table);
		addDataDomainColumns(table);
	}

	private void addDataDomainColumns(RankTableModel table) {
		List<ATableBasedDataDomain> dataDomains = new ArrayList<>(DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class));
		List<ARankColumnModel> new_ = new ArrayList<>();
		for (ATableBasedDataDomain d : Iterables.filter(dataDomains, DataSupportDefinitions.categoricalTables)) {
			EDimension dim = select(d);
			if (dim == null)
				continue;
			new_.add(new DistributionRankTableModel(d, dim));
		}
		for (ATableBasedDataDomain d : Iterables.filter(dataDomains, DataSupportDefinitions.numericalTables)) {
			EDimension dim = select(d);
			if (dim == null)
				continue;
			new_.add(new BoxPlotRankTableModel(d, dim));
		}
		for (ATableBasedDataDomain d : Iterables.filter(dataDomains, DataSupportDefinitions.inhomogenousTables)) {
			if (d.getRecordIDCategory() != category)
				continue;
			boolean multi = expectMultiMapping(d);
			final Table data = d.getTable();
			for (TablePerspective t : d.getAllTablePerspectives()) {
				VirtualArray dims = t.getDimensionPerspective().getVirtualArray();
				if (dims.size() != 1)
					continue;

				final Integer dimId = dims.get(0);
				EDataClass clazz = data.getDataClass(dimId, 0);
				switch (clazz) {
				case NATURAL_NUMBER:
					if (multi)
						new_.add(new BoxPlotRankTableModel(t, EDimension.DIMENSION));
					else
						new_.add(new SingleInteger(t));
					break;
				case REAL_NUMBER:
					if (multi)
						new_.add(new BoxPlotRankTableModel(t, EDimension.DIMENSION));
					else
						new_.add(new SingleDouble(t));
					break;
				case CATEGORICAL:
					if (multi)
						new_.add(new DistributionRankTableModel(t, EDimension.DIMENSION));
					else
						new_.add(new SingleCategorical(t));
					break;
				default:
					break;
				}
			}
		}

		// Collections.sort(new_, Labels.BY_LABEL);
		for (ARankColumnModel r : new_)
			table.add(r);
	}

	/**
	 * @param d
	 * @return
	 */
	protected boolean expectMultiMapping(ATableBasedDataDomain d) {
		return false;
	}

	private void addIDTypeColumns(RankTableModel table) {
		for (final IDType idType : this.idTypes) {
			// not interesting to show
			if (idType == category.getHumanReadableIDType() || idType == primary
					|| idType.isInternalType())
				continue;
			IGLRenderer header = GLRenderers.drawText(idType.getTypeName(), VAlign.CENTER);
			// render as string
			table.add(new StringRankColumnModel(header, new Function<IRow, String>() {
				@Override
				public String apply(IRow input) {
					assert input instanceof PrimaryIDRow;
					return ((PrimaryIDRow) input).getAsString(idType, null);
				}
			}));
		}
	}

	/**
	 * @param d
	 * @return
	 */
	private EDimension select(ATableBasedDataDomain d) {
		if (d.getRecordIDCategory() == category)
			return EDimension.RECORD;
		if (d.getDimensionIDCategory() == category)
			return EDimension.DIMENSION;
		return null;
	}

	/**
	 * @return
	 */
	protected Collection<IDType> findReleveantDataIDTypes() {
		List<ATableBasedDataDomain> dataDomains = new ArrayList<>(DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class));
		List<IDType> r = new ArrayList<>();
		for (ATableBasedDataDomain d : dataDomains) {
			if (d.getDimensionIDCategory() == category)
				r.add(d.getDimensionIDType());
			if (d.getRecordIDCategory() == category)
				r.add(d.getRecordIDType());
		}
		return r;
	}

	@Override
	public int compareTo(ACategoryQuery o) {
		return String.CASE_INSENSITIVE_ORDER.compare(category.getCategoryName(), o.category.getCategoryName());
	}
}
