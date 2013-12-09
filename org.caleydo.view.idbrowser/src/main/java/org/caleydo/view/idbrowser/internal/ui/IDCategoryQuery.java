/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.ui;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.base.Labels;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.idbrowser.internal.model.PrimaryIDRow;
import org.caleydo.vis.lineup.model.ARow;
import org.caleydo.vis.lineup.model.CategoricalRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @author Samuel Gratzl
 *
 */
public class IDCategoryQuery extends GLButton implements Comparable<IDCategoryQuery> {

	private BitSet mask;

	private final IDCategory category;
	private final Set<IDType> idTypes;

	public IDCategoryQuery(IDCategory category) {
		super(EButtonMode.CHECKBOX);
		setRenderer(createRadioRenderer(category.getCategoryName()));
		this.category = category;
		setLayoutData(category);

		Set<IDType> idTypes = new HashSet<>();
		idTypes.addAll(category.getPublicIdTypes());
		idTypes.add(category.getHumanReadableIDType());
		idTypes.addAll(findReleveantDataIDTypes());
		this.idTypes = ImmutableSortedSet.orderedBy(new Comparator<IDType>() {
			@Override
			public int compare(IDType o1, IDType o2) {
				return String.CASE_INSENSITIVE_ORDER.compare(o1.getTypeName(), o2.getTypeName());
			}
		}).addAll(idTypes).build();
	}

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
		IDType primary = category.getPrimaryMappingType();
		Set<?> ids = mappingManager.getAllMappedIDs(primary);

		final Map<IDType, IIDTypeMapper<Object, Object>> mappings = new HashMap<>();

		for (IDType idType : idTypes) {
			IIDTypeMapper<Object, Object> m = mappingManager.getIDTypeMapper(primary, idType);
			if (m != null)
				mappings.put(idType, m);
		}

		return Collections2.transform(ids, new Function<Object, ARow>() {
			@Override
			public ARow apply(Object input) {
				assert input != null;
				ImmutableMap.Builder<IDType, Set<Object>> b = ImmutableMap.builder();
				for (Map.Entry<IDType, IIDTypeMapper<Object, Object>> entry : mappings.entrySet()) {
					Set<Object> m = entry.getValue().apply(input);
					if (m != null)
						b.put(entry.getKey(), m);
				}
				return new PrimaryIDRow(category, input, b.build());
			}
		});
	}


	public void addColumns(RankTableModel table) {
		for (final IDType idType : this.idTypes) {
			if (idType == category.getHumanReadableIDType() || idType == category.getPrimaryMappingType()
					|| idType.isInternalType())
				continue;
			IGLRenderer header = GLRenderers.drawText(idType.getTypeName(), VAlign.CENTER);
			table.add(new StringRankColumnModel(header, new Function<IRow, String>() {
				@Override
				public String apply(IRow input) {
					assert input instanceof PrimaryIDRow;
					return ((PrimaryIDRow) input).getAsString(idType, null);
				}
			}));
		}
		// List<ATableBasedDataDomain> dataDomains = new ArrayList<>(DataDomainManager.get().getDataDomainsByType(
		// ATableBasedDataDomain.class));
		// for(ATableBasedDataDomain d : Iterables.filter(dataDomains, DataSupportDefinitions.categoricalTables)) {
		//
		// }
		List<Perspective> perspectives = findRelevantPerspectives();
		Map<Boolean,String> metaData = ImmutableMap.of(Boolean.TRUE, "Found",Boolean.FALSE,"Not Found");

		for (final Perspective perspective : perspectives) {
			IGLRenderer header = GLRenderers.drawText(perspective.getDataDomain().getLabel(), VAlign.CENTER);
			table.add(new CategoricalRankColumnModel<Boolean>(header, new Function<IRow, Boolean>() {
				@Override
				public Boolean apply(IRow input) {
					assert input instanceof PrimaryIDRow;
					return ((PrimaryIDRow) input).get(perspective.getIdType()) != null;
				}
			}, metaData));
		}
	}

	/**
	 * @return
	 */
	private Collection<IDType> findReleveantDataIDTypes() {
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

	/**
	 * find the relevant perspectives that have an {@link IDType} of the given {@link IDCategory}
	 *
	 * @param category
	 * @return
	 */
	private List<Perspective> findRelevantPerspectives() {
		List<ATableBasedDataDomain> dataDomains = new ArrayList<>(DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class));

		List<Perspective> dataDomainPerspectives = new ArrayList<>(dataDomains.size());
		for (ATableBasedDataDomain dd : dataDomains) {
			if (dd.getRecordIDCategory() == category)
				dataDomainPerspectives.add(dd.getTable().getDefaultRecordPerspective(false));

			if (dd.getDimensionIDCategory() == category)
				dataDomainPerspectives.add(dd.getTable().getDefaultDimensionPerspective(false));
		}
		Collections.sort(dataDomainPerspectives, Labels.BY_LABEL);
		return dataDomainPerspectives;
	}

	@Override
	public int compareTo(IDCategoryQuery o) {
		return String.CASE_INSENSITIVE_ORDER.compare(category.getCategoryName(), o.category.getCategoryName());
	}
}
