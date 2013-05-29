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
import org.caleydo.view.tourguide.internal.view.col.CategoricalPercentageRankColumnModel;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalDataDomainQuery extends ADataDomainQuery {
	private Set<CategoryProperty<?>> selected = new HashSet<>();
	private final IDType categoryIDType;
	private final VirtualArray categories;

	public CategoricalDataDomainQuery(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
		assert (DataDomainOracle.isCategoricalDataDomain(dataDomain));
		this.selected.addAll(getCategories());

		if (dataDomain.isColumnDimension()) {
			this.categoryIDType = dataDomain.getRecordIDType();
			this.categories = dataDomain.getTable().getDefaultRecordPerspective().getVirtualArray();
		} else {
			this.categoryIDType = dataDomain.getDimensionIDType();
			this.categories = dataDomain.getTable().getDefaultDimensionPerspective().getVirtualArray();
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

	public int getGroupSize() {
		return selected.size();
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
		this.selected.clear();
		this.selected.addAll(selected);
		updateFilter();
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

	/**
	 * @param id
	 * @return
	 */
	public VirtualArray createVirtualArray(String label, Integer category) {
		ATableBasedDataDomain dataDomain2 = getDataDomain();

		// reuse existing
		TablePerspective tablePerspective = findExistingTablePerspective(label);
		if (tablePerspective != null) {
			if (dataDomain2.isColumnDimension()) {
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


	public TablePerspective createTablePerspective(String label, Integer id, VirtualArray va) {
		ATableBasedDataDomain d = getDataDomain();
		final Table table = d.getTable();

		TablePerspective tablePerspective = findExistingTablePerspective(label);
		if (tablePerspective != null)
			return tablePerspective;

		Perspective cat = new Perspective(dataDomain, categoryIDType);
		cat.setPrivate(true);
		cat.setLabel(label, false);
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(Lists.newArrayList(id));
		cat.init(data);

		Perspective items = new Perspective(dataDomain, d.getOppositeIDType(categoryIDType));
		data = new PerspectiveInitializationData();
		data.setData(va);
		items.init(data);
		items.setLabel(label, false);
		items.setPrivate(true);

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
		tablePerspective.setPrivate(true);
		tablePerspective.getContainerStatistics().setNumberOfBucketsForHistogram(
				cat.getVirtualArray().getGroupList().size());

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

		// I know that String might be wrong but who cares
		final CategoricalTable<?> ctable = (CategoricalTable<?>) getDataDomain().getTable();
		for (CategoryProperty<?> p : ctable.getCategoryDescriptions().getCategoryProperties()) {
			table.add(CategoricalPercentageRankColumnModel.create(p.getCategory(), ctable));
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

	private void flat(Iterator<ARankColumnModel> cols, List<ARankColumnModel> toDestroy) {
		while (cols.hasNext()) {
			ARankColumnModel col = cols.next();
			if (col instanceof ACompositeRankColumnModel) {
				flat(((ACompositeRankColumnModel) col).iterator(), toDestroy);
			}
			if (col instanceof CategoricalPercentageRankColumnModel
					&& ((CategoricalPercentageRankColumnModel) col).getDataDomain() == dataDomain)
				toDestroy.add(col);
		}
	}
}
