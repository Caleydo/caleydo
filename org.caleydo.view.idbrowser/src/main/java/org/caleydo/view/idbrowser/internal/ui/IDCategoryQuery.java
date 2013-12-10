/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.ui;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
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
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.idbrowser.internal.model.PrimaryIDRow;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.data.DoubleInferrers;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.ARow;
import org.caleydo.vis.lineup.model.DoubleRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.IntegerRankColumnModel;
import org.caleydo.vis.lineup.model.MultiCategoricalRankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

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
			final Table data = d.getTable();
			final IDType idType = d.getRecordIDType();
			Color color = d.getColor();
			Color bgColor = d.getColor().brighter().brighter();
			for (TablePerspective t : d.getAllTablePerspectives()) {
				VirtualArray dims = t.getDimensionPerspective().getVirtualArray();
				if (dims.size() != 1)
					continue;
				IGLRenderer header = GLRenderers.drawText(t.getLabel(), VAlign.CENTER);
				final Integer dimId = dims.get(0);
				EDataClass clazz = data.getDataClass(dimId, 0);
				switch (clazz) {
				case NATURAL_NUMBER:
					new_.add(new IntegerRankColumnModel(header, new Function<IRow, Integer>() {
						@Override
						public Integer apply(IRow input) {
							assert input instanceof PrimaryIDRow;
							Set<Object> r = ((PrimaryIDRow) input).get(idType);
							if (r == null || r.isEmpty())
								return null;
							Object ri = r.iterator().next();
							if (!(ri instanceof Integer))
								return null;
							Integer id = (Integer)ri;
							ri = data.getRaw(dimId, id);
							if (ri instanceof Integer)
								return (Integer)ri;
							return null;
						}
					}, color, bgColor, NumberFormat.getInstance(Locale.ENGLISH)));
					break;
				case REAL_NUMBER:
					new_.add(new DoubleRankColumnModel(new ADoubleFunction<IRow>() {
						@Override
						public double applyPrimitive(IRow input) {
							assert input instanceof PrimaryIDRow;
							Set<Object> r = ((PrimaryIDRow) input).get(idType);
							if (r == null || r.isEmpty())
								return Double.NaN;
							Object ri = r.iterator().next();
							if (!(ri instanceof Integer))
								return Double.NaN;
							Integer id = (Integer)ri;
							ri = data.getRaw(dimId, id);
							if (ri instanceof Number)
								return ((Number)ri).doubleValue();
							return Double.NaN;
						}
					}, header, color, bgColor, new PiecewiseMapping(Float.NaN, Float.NaN), DoubleInferrers
							.fix(Double.NaN)));
					break;
				case CATEGORICAL:
					List<CategoryProperty<?>> categories = resolveCategories(dimId, d, EDimension.DIMENSION);
					Map<String, String> catMeta = new HashMap<>();
					for (CategoryProperty<?> p : categories) {
						catMeta.put(p.getCategory().toString(), p.getCategoryName());
					}
					new_.add(new MultiCategoricalRankColumnModel<String>(header, new Function<IRow, Set<String>>() {
						@Override
						public Set<String> apply(IRow input) {
							assert input instanceof PrimaryIDRow;
							Set<Object> r = ((PrimaryIDRow) input).get(idType);
							if (r == null || r.isEmpty())
								return Collections.emptySet();
							Set<String> s = new TreeSet<>();
							for (Object ri : r) {
								if (!(ri instanceof Integer))
									continue;
								ri = data.getRaw(dimId, (Integer) ri);
								s.add(ri.toString());
							}
							return s;
						}
					}, catMeta, color, bgColor, ""));
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

	private void addIDTypeColumns(RankTableModel table) {
		for (final IDType idType : this.idTypes) {
			// not interesting to show
			if (idType == category.getHumanReadableIDType() || idType == category.getPrimaryMappingType()
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

	@SuppressWarnings("unchecked")
	static List<CategoryProperty<?>> resolveCategories(Integer singleID, ATableBasedDataDomain dataDomain,
			EDimension dim) {
		final Table table = dataDomain.getTable();

		Object spec = table.getDataClassSpecificDescription(dim.select(singleID.intValue(), 0),
				dim.select(0, singleID.intValue()));
		if (spec instanceof CategoricalClassDescription<?>) {
			List<?> tmp = ((CategoricalClassDescription<?>) spec).getCategoryProperties();
			return ImmutableList.copyOf((List<CategoryProperty<?>>) tmp);
		}
		return Collections.emptyList();
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

	@Override
	public int compareTo(IDCategoryQuery o) {
		return String.CASE_INSENSITIVE_ORDER.compare(category.getCategoryName(), o.category.getCategoryName());
	}
}
