/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

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

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.base.Labels;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalDataDomainQuery extends ADataDomainQuery {
	public static final String PROP_GROUP_SELECTION = "selection";

	private Set<CategoryProperty<?>> selected = new HashSet<>();
	private final IDType categoryIDType;
	private final VirtualArray categories;

	public CategoricalDataDomainQuery(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
		assert (DataDomainOracle.isCategoricalDataDomain(dataDomain));
		this.selected.addAll(getCategories());

		if (dataDomain.isColumnDimension()) {
			this.categoryIDType = dataDomain.getRecordIDType();
			this.categories = dataDomain.getTable().getDefaultRecordPerspective(false).getVirtualArray();
		} else {
			this.categoryIDType = dataDomain.getDimensionIDType();
			this.categories = dataDomain.getTable().getDefaultDimensionPerspective(false).getVirtualArray();
		}
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return (ATableBasedDataDomain) super.getDataDomain();
	}

	/**
	 * @return the categoryIDType, see {@link #categoryIDType}
	 */
	public IDType getCategoryIDType() {
		return categoryIDType;
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

	public boolean apply(Group group) {
		for (CategoryProperty<?> s : selected) {
			if (Objects.equals(s.getCategoryName(), group.getLabel()))
				return true;
		}
		return false;
	}

	@Override
	protected List<AScoreRow> getAll() {
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(categoryIDType);

		IIDTypeMapper<Integer, String> toLabel = idMappingManager.getIDTypeMapper(categoryIDType, categoryIDType
				.getIDCategory().getHumanReadableIDType());

		List<AScoreRow> r = new ArrayList<>(); // just stratifications
		for (int category : categories) {
			String label = toLabel.apply(category).iterator().next();
			r.add(new CategoricalPerspectiveRow(label, category, this));
		}

		return r;
	}

	@Override
	public List<AScoreRow> onDataDomainUpdated() {
		// up to now not possible to create new categories
		return null;
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
	public boolean hasFilter() {
		return selected.size() < getCategories().size();
	}

	@Override
	public boolean isFilteringPossible() {
		return true;
	}

	/**
	 * return the expected group count for a given dimension
	 *
	 * @param category
	 * @return
	 */
	public int getGroupSize(Integer category) {
		ATableBasedDataDomain dataDomain2 = getDataDomain();
		CategoricalTable<?> table = (CategoricalTable<?>)dataDomain2.getTable();
		CategoricalClassDescription<?> categoryDescriptions = (CategoricalClassDescription<?>) table
				.getDataClassSpecificDescription(category, 0);
		int count = 0;
		for (CategoryProperty<?> property : categoryDescriptions) {
			int cp = table.getNumberOfMatches(property.getCategory(), getCategoryIDType(), category);
			if (cp > 0)
				count++;
		}
		return count;
	}

	public Collection<GroupInfo> getGroupInfos(Integer category) {
		ATableBasedDataDomain dataDomain2 = getDataDomain();
		CategoricalTable<?> table = (CategoricalTable<?>) dataDomain2.getTable();
		CategoricalClassDescription<?> categoryDescriptions = (CategoricalClassDescription<?>) table
				.getDataClassSpecificDescription(category, 0);
		Collection<GroupInfo> infos = new ArrayList<>();
		for (CategoryProperty<?> property : categoryDescriptions) {
			int cp = table.getNumberOfMatches(property.getCategory(), getCategoryIDType(), category);
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
	public VirtualArray createVirtualArray(String label, Integer category) {
		ATableBasedDataDomain dataDomain2 = getDataDomain();

		// reuse existing
		TablePerspective tablePerspective = findExistingTablePerspective(label);
		if (tablePerspective != null) {
			if (dataDomain2.getRecordIDType() != getCategoryIDType()) {
				return tablePerspective.getRecordPerspective().getVirtualArray();
			} else {
				return tablePerspective.getDimensionPerspective().getVirtualArray();
			}
		}

		Table table = dataDomain2.getTable();
		CategoricalClassDescription<?> categoryDescriptions = (CategoricalClassDescription<?>) table
				.getDataClassSpecificDescription(category, 0);
		int nrBins = categoryDescriptions.size();

		Map<CategoryProperty<?>, List<Integer>> bins = new HashMap<>(nrBins * 2);
		for (CategoryProperty<?> property : categoryDescriptions) {
			bins.put(property, new ArrayList<Integer>());
		}

		List<Integer> records;
		boolean swap = table.getDataDomain().isColumnDimension();
		if (swap) {
			records = table.getRowIDList();
		} else {
			records = table.getColumnIDList();
		}

		for (Integer recordID : records) {
			CategoryProperty<?> property = categoryDescriptions
.getCategoryProperty(table.getRaw(category, recordID));
			if (property == null) {
				System.out.println("recordID: " + recordID + " dimensionID " + category + " raw: "
						+ table.getRaw(category, recordID));
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

		VirtualArray va = new VirtualArray(table.getDataDomain().getOppositeIDType(this.categoryIDType), groupIds);
		va.setGroupList(groupList);

		return va;
	}

	/**
	 * creates a {@link TablePerspective} out of the given data
	 *
	 * @param label
	 * @param id
	 *            the category
	 * @param va
	 *            the record dimension to use
	 * @return
	 */
	public TablePerspective createTablePerspective(String label, Integer id, VirtualArray va) {
		ATableBasedDataDomain d = getDataDomain();
		final Table table = d.getTable();

		TablePerspective tablePerspective = findExistingTablePerspective(label);
		if (tablePerspective != null)
			return tablePerspective;

		Perspective cat = new Perspective(dataDomain, categoryIDType);
		cat.setPrivate(false);
		cat.setLabel(label, false);
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(Lists.newArrayList(id));
		cat.init(data);

		Perspective items = new Perspective(dataDomain, d.getOppositeIDType(categoryIDType));
		data = new PerspectiveInitializationData();
		data.setData(va);
		items.init(data);
		items.setLabel(label, false);
		items.setPrivate(false);

		if (d.isColumnDimension()) {
			table.registerRecordPerspective(cat, false);
			table.registerDimensionPerspective(items, false);
			tablePerspective = d.getTablePerspective(cat.getPerspectiveID(), items.getPerspectiveID());
		} else {
			table.registerDimensionPerspective(cat, false);
			table.registerRecordPerspective(items, false);
			tablePerspective = d.getTablePerspective(items.getPerspectiveID(), cat.getPerspectiveID());
		}
		tablePerspective.setLabel(label, false);
		tablePerspective.setPrivate(false);

		return tablePerspective;
	}

	private TablePerspective findExistingTablePerspective(String label) {
		// use existing by name
		for (TablePerspective p : getDataDomain().getAllTablePerspectives()) {
			if (p.getLabel().equals(label))
				return p;
		}
		return null;
	}

	@Override
	public void createSpecificColumns(RankTableModel table) {
		final CategoricalTable<?> ctable = (CategoricalTable<?>) getDataDomain().getTable();
		ATableBasedDataDomain d = getDataDomain();
		Color color = d.getColor();
		GroupRankColumnModel group = new GroupRankColumnModel(d.getLabel() + " Metrics", color, color.brighter());
		table.add(group);
		for (CategoryProperty<?> p : ctable.getCategoryDescriptions().getCategoryProperties()) {
			group.add(CategoricalPercentageRankColumnModel.create(p.getCategory(), ctable, selected.contains(p)));
		}
		group = new GroupRankColumnModel(d.getLabel() + " Groupings", color, color.brighter());
		for (String id : d.getDimensionPerspectiveIDs()) {
			Perspective p = ctable.getDimensionPerspective(id);
			if (p.isDefault() || p.getVirtualArray().size() <= 1 || p.getVirtualArray().getGroupList().size() <= 1)
				continue;
			//use the complex perspective over multiple genes with a group list as a categorical column
			if (group.size() == 0)
				table.add(group);
			group.add(createCategoricalFromGroupList(p.getLabel(), p.getVirtualArray()));
		}
	}

	/**
	 * @param va
	 * @return
	 */
	private ARankColumnModel createCategoricalFromGroupList(String label, final VirtualArray va) {
		Collection<String> items = new ArrayList<>();
		for (Group g : va.getGroupList()) {
			items.add(g.getLabel());
		}
		Function<IRow, Set<String>> toGroup = new Function<IRow, Set<String>>() {
			@Override
			public Set<String> apply(IRow in) {
				if (!(in instanceof CategoricalPerspectiveRow))
					return null;
				CategoricalPerspectiveRow r = (CategoricalPerspectiveRow) in;
				if (r.getCategoryIDType() != va.getIdType())
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

	@Override
	public void removeSpecificColumns(RankTableModel table) {
		List<ARankColumnModel> toDestroy = new ArrayList<>();
		flat(table.getColumns().iterator(), toDestroy);
		for (ARankColumnModel r : toDestroy) {
			r.hide();
			r.destroy();
		}
	}

	private void flat(Iterator<ARankColumnModel> cols, List<ARankColumnModel> toDestroy) {
		while (cols.hasNext()) {
			ARankColumnModel col = cols.next();
			if (col instanceof GroupRankColumnModel
					&& ((GroupRankColumnModel) col).getTitle().startsWith(getDataDomain().getLabel() + " ")) {
				toDestroy.add(col);
			} else if (col instanceof ACompositeRankColumnModel) {
				flat(((ACompositeRankColumnModel) col).iterator(), toDestroy);
			} else if (col instanceof CategoricalPercentageRankColumnModel
					&& ((CategoricalPercentageRankColumnModel) col).getDataDomain() == dataDomain)
				toDestroy.add(col);
		}
	}
}
