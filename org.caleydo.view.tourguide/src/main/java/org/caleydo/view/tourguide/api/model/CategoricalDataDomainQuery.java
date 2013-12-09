/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.model;

import static org.caleydo.view.tourguide.internal.view.col.CategoricalPercentageRankColumnModel.create;
import static org.caleydo.view.tourguide.internal.view.col.CategoricalPercentageRankColumnModel.isConsideredForCalculation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.util.base.Labels;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.api.prefs.MyPreferences;
import org.caleydo.view.tourguide.internal.view.col.CategoricalPercentageRankColumnModel;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.GroupRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.MultiCategoricalRankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalDataDomainQuery extends ASingleIDDataDomainQuery {
	public static final String PROP_GROUP_SELECTION = "selection";

	private Set<CategoryProperty<?>> selected = new HashSet<>();

	public CategoricalDataDomainQuery(ATableBasedDataDomain dataDomain, EDimension dim) {
		super(dataDomain, dim);
		assert (DataDomainOracle.isCategoricalDataDomain(dataDomain));
		this.selected.addAll(getCategories());
		setMinSize(MyPreferences.getMinClusterSize());
	}

	@SuppressWarnings("unchecked")
	public List<CategoryProperty<?>> getCategories() {
		final CategoricalTable<?> table = (CategoricalTable<?>) getDataDomain().getTable();

		CategoricalClassDescription<?> cats = table.getCategoryDescriptions();
		List<?> tmp = cats.getCategoryProperties();
		return (List<CategoryProperty<?>>) tmp;
	}

	@Override
	public boolean apply(AScoreRow row) {
		assert row.getDataDomain() == dataDomain;
		return !selected.isEmpty();
	}

	@Override
	public boolean apply(Group group) {
		if (!super.apply(group))
			return false;
		for (CategoryProperty<?> s : selected) {
			if (Objects.equals(s.getCategoryName(), group.getLabel()))
				return true;
		}
		return false;
	}

	public void setSelection(Set<CategoryProperty<?>> selected) {
		if (Objects.equals(selected, this.selected))
			return;
		Set<CategoryProperty<?>> bak = ImmutableSet.copyOf(this.selected);
		this.selected.clear();
		this.selected.addAll(selected);
		if (bak.isEmpty() || this.selected.isEmpty())
			updateFilter();
		else
			propertySupport.firePropertyChange(PROP_GROUP_SELECTION, bak, this.selected);
	}

	/**
	 * @return the selected, see {@link #selected}
	 */
	public Set<CategoryProperty<?>> getSelected() {
		return selected;
	}

	@Override
	protected boolean hasFilterImpl() {
		return selected.size() < getCategories().size();
	}

	/**
	 * return the expected group count for a given dimension
	 *
	 * @param category
	 * @return
	 */
	@Override
	public int getGroupSize(Integer category) {
		ATableBasedDataDomain dataDomain2 = getDataDomain();
		CategoricalTable<?> table = (CategoricalTable<?>)dataDomain2.getTable();
		CategoricalClassDescription<?> categoryDescriptions = (CategoricalClassDescription<?>) table
				.getDataClassSpecificDescription(category, 0);
		int count = 0;
		for (CategoryProperty<?> property : categoryDescriptions) {
			int cp = table.getNumberOfMatches(property.getCategory(), getSingleIDType(), category);
			if (cp > 0)
				count++;
		}
		return count;
	}

	@Override
	public Collection<GroupInfo> getGroupInfos(Integer category) {
		ATableBasedDataDomain dataDomain2 = getDataDomain();
		CategoricalTable<?> table = (CategoricalTable<?>) dataDomain2.getTable();
		CategoricalClassDescription<?> categoryDescriptions = (CategoricalClassDescription<?>) table
				.getDataClassSpecificDescription(category, 0);
		Collection<GroupInfo> infos = new ArrayList<>();
		for (CategoryProperty<?> property : categoryDescriptions) {
			int cp = table.getNumberOfMatches(property.getCategory(), getSingleIDType(), category);
			infos.add(new GroupInfo(property.getCategoryName(), cp, property.getColor()));
		}
		return infos;
	}

	/**
	 * builds a virtual array given a category and a label for it
	 *
	 * @param label
	 * @param category
	 * @return
	 */
	@Override
	public VirtualArray createVirtualArrayImpl(String label, Integer category) {
		ATableBasedDataDomain dataDomain2 = getDataDomain();

		Table table = dataDomain2.getTable();
		CategoricalClassDescription<?> categoryDescriptions = (CategoricalClassDescription<?>) table
				.getDataClassSpecificDescription(category, 0);
		int nrBins = categoryDescriptions.size();

		Map<CategoryProperty<?>, List<Integer>> bins = new HashMap<>(nrBins * 2);
		for (CategoryProperty<?> property : categoryDescriptions) {
			bins.put(property, new ArrayList<Integer>());
		}

		List<Integer> records;
		if (dim.isDimension()) {
			records = table.getDefaultRecordPerspective(false).getVirtualArray().getIDs();
		} else {
			records = table.getDefaultDimensionPerspective(false).getVirtualArray().getIDs();
		}

		for (Integer recordID : records) {
			Object raw = dim.isDimension() ? table.getRaw(category, recordID) : table.getRaw(recordID, category);
			CategoryProperty<?> property = categoryDescriptions.getCategoryProperty(raw);
			if (property == null) {
				System.out.println("recordID: " + dim.select(recordID, category) + " dimensionID "
						+ dim.select(category, recordID) + " raw: " + raw);
			} else {
				// System.out.println(" raw: " + table.getRaw(dimensionID, recordID));
				List<Integer> bin = bins.get(property);
				bin.add(recordID);
			}
		}

		List<Integer> groupIds = new ArrayList<>(records.size());
		GroupList groupList = new GroupList();
		int from = 0;
		int to = 0;

		for (CategoryProperty<?> property : categoryDescriptions) {
			List<Integer> bin = bins.get(property);

			int size = bin.size();

			if (size == 0) // skip empty groups
				continue;

			Group g = new Group(size, size > 0 ? bin.get(0) : 0);
			g.setLabel(property.getCategoryName(), false);
			g.setStartIndex(from);
			to += size;
			from = to;

			groupList.append(g);
			groupIds.addAll(bin);
		}

		VirtualArray va = new VirtualArray(table.getDataDomain().getOppositeIDType(this.getSingleIDType()), groupIds);
		va.setGroupList(groupList);

		return va;
	}

	@Override
	public void createSpecificColumns(RankTableModel table) {
		final CategoricalTable<?> ctable = (CategoricalTable<?>) getDataDomain().getTable();
		ATableBasedDataDomain d = getDataDomain();
		Color color = d.getColor();
		GroupRankColumnModel group = new GroupRankColumnModel(d.getLabel() + " Metrics", color, color.brighter());
		table.add(group);
		for (CategoryProperty<?> p : ctable.getCategoryDescriptions().getCategoryProperties()) {
			group.add(create(p.getCategory(), ctable, selected.contains(p), getDim()));
		}
		group = new GroupRankColumnModel(d.getLabel() + " Groupings", color, color.brighter());
		for (String id : dim.isDimension() ? d.getDimensionPerspectiveIDs() : d.getRecordPerspectiveIDs()) {
			Perspective p = dim.isDimension() ? ctable.getDimensionPerspective(id) : ctable.getRecordPerspective(id);
			if (p.isDefault() || p.getVirtualArray().size() <= 1 || p.getVirtualArray().getGroupList().size() <= 1)
				continue;
			//use the complex perspective over multiple genes with a group list as a categorical column
			if (group.size() == 0)
				table.add(group);
			group.add(createCategoricalFromGroupList(p.getLabel(), p.getVirtualArray()));
		}
	}

	@Override
	public void removeSpecificColumns(RankTableModel table) {
		List<ARankColumnModel> toDestroy = new ArrayList<>();
		flat(table.getColumns().iterator(), toDestroy);
		for (ARankColumnModel r : toDestroy) {
			r.hide();
			r.destroy();
		}
	}

	@Override
	public void updateSpecificColumns(RankTableModel table) {
		final CategoricalTable<?> ctable = (CategoricalTable<?>) getDataDomain().getTable();
		for (CategoricalPercentageRankColumnModel c : Iterables.filter(table.getFlatColumns(),
				CategoricalPercentageRankColumnModel.class)) {
			if (c.getDataDomain() != dataDomain)
				continue;
			final CategoryProperty<?> category = c.getCategory();
			boolean active = selected.contains(category);
			boolean wasActive = isConsideredForCalculation(c);

			if (active != wasActive) {
				final CategoricalPercentageRankColumnModel new_ = create(category.getCategory(), ctable, active,
						getDim());
				new_.setWidth(c.getWidth());
				new_.setCollapsed(c.isCollapsed());
				new_.setFilter(c.isFilterNotMappedEntries(), c.isFilterMissingEntries(), c.isGlobalFilter(),
						c.isRankIndependentFilter());
				c.getParent().replace(c, new_);
			}
		}
	}

	private static ARankColumnModel createCategoricalFromGroupList(String label, final VirtualArray va) {
		Collection<String> items = new ArrayList<>();
		for (Group g : va.getGroupList()) {
			items.add(g.getLabel());
		}
		Function<IRow, Set<String>> toGroup = new Function<IRow, Set<String>>() {
			@Override
			public Set<String> apply(IRow in) {
				if (!(in instanceof SingleIDPerspectiveRow))
					return null;
				SingleIDPerspectiveRow r = (SingleIDPerspectiveRow) in;
				if (r.getSingleIDType() != va.getIdType())
					return null;
				List<Group> groups = va.getGroupOf(r.getDimensionID());
				if (groups.isEmpty())
					return null;
				if (groups.size() == 1)
					return Collections.singleton(groups.get(0).getLabel());
				return Sets.newTreeSet(Iterables.transform(groups, Labels.TO_LABEL));
			}
		};
		return MultiCategoricalRankColumnModel.createSimple(GLRenderers.drawText(label, VAlign.CENTER), toGroup, items,
				"");
	}
	private void flat(Iterator<ARankColumnModel> cols, List<ARankColumnModel> toDestroy) {
		while (cols.hasNext()) {
			ARankColumnModel col = cols.next();
			if (col instanceof GroupRankColumnModel
					&& ((GroupRankColumnModel) col).getLabel().startsWith(getDataDomain().getLabel() + " ")) {
				toDestroy.add(col);
			} else if (col instanceof ACompositeRankColumnModel) {
				flat(((ACompositeRankColumnModel) col).iterator(), toDestroy);
			} else if (col instanceof CategoricalPercentageRankColumnModel
					&& ((CategoricalPercentageRankColumnModel) col).getDataDomain() == dataDomain)
				toDestroy.add(col);
		}
	}
}
