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
package org.caleydo.view.table;

import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupExpandCollapseLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.RowGroupExpandCollapseLayer;
import org.eclipse.nebula.widgets.nattable.group.RowGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.model.RowGroupModel;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.RowHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO
 *
 * @author Marc Streit
 *
 */
public class NatTableBuilder {
	private final Composite parent;
	private final TablePerspective tablePerspective;

	private NatTableBuilder(Composite parent, TablePerspective tablePerspective) {
		this.parent = parent;
		this.tablePerspective = tablePerspective;
	}

	public static Pair<NatTable, DataProvider> create(Composite parent, TablePerspective tablePerspective) {
		return new NatTableBuilder(parent, tablePerspective).build();
	}

	public Pair<NatTable, DataProvider> build() {

		final GroupList dimensionGroups = tablePerspective.getDimensionPerspective().getVirtualArray().getGroupList();
		final boolean hasDimensionGroups = dimensionGroups != null && dimensionGroups.size() > 1;
		final GroupList recordGroups = tablePerspective.getRecordPerspective().getVirtualArray().getGroupList();
		final boolean hasRecordGroups = false; // recordGroups != null && recordGroups.size() > 1;

		IUniqueIndexLayer bodyLayer;

		DataProvider data = new DataProvider(tablePerspective);
		bodyLayer = new DataLayer(data);
		bodyLayer = new ColumnReorderLayer(bodyLayer);
		bodyLayer = new ColumnHideShowLayer(bodyLayer);

		ColumnGroupModel columnGroupModel = null;
		if (hasDimensionGroups) {
			columnGroupModel = new ColumnGroupModel();
			bodyLayer = new ColumnGroupExpandCollapseLayer(bodyLayer, columnGroupModel);
		}

		bodyLayer = new RowHideShowLayer(bodyLayer);

		RowGroupModel rowGroupModel = null;
		if (hasRecordGroups) {
			rowGroupModel = new RowGroupModel();
			// FIXME
			// rowGroupModel.setDataProvider(bodyDataProvider);
			bodyLayer = new RowGroupExpandCollapseLayer(bodyLayer, rowGroupModel);
		}

		SelectionLayer selectionLayer = new SelectionLayer(bodyLayer);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		IDataProvider colHeaderDataProvider = new TablePerspectiveHeaderDataProvider(tablePerspective, true);
		DataLayer columnHeaderDataLayer = new DataLayer(colHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

		if (hasDimensionGroups) {
			ColumnGroupHeaderLayer columnGroupHeaderLayer = new ColumnGroupHeaderLayer(columnHeaderLayer,
					selectionLayer, columnGroupModel);
			columnHeaderLayer = columnGroupHeaderLayer;
			// Create a group of rows for the model.
			int j = 0;
			for (Group group : dimensionGroups) {
				int[] indices = new int[group.getSize()];
				for (int i = 0; i < indices.length; ++i)
					// as order by groups
					indices[i] = j++;
				columnGroupHeaderLayer.addColumnsIndexesToGroup(group.getLabel(), indices);
			}
		}

		IDataProvider rowHeaderDataProvider = new TablePerspectiveHeaderDataProvider(tablePerspective, false);
		DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider);
		rowHeaderDataLayer.setDefaultColumnWidth(100);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);

		if (hasRecordGroups) {
			RowGroupHeaderLayer rowGroupHeaderLayer = new RowGroupHeaderLayer(rowHeaderLayer, selectionLayer,
					rowGroupModel);
			rowGroupHeaderLayer.setColumnWidth(15);
			rowHeaderLayer = rowGroupHeaderLayer;
			// FIXME
			// Create a group of rows for the model.
			for (Group group : recordGroups) {
				// RowGroup rowGroup = new RowGroup(rowGroupModel, group.getLabel(), false);
				// rowGroup.add
				// rowGroup.addMemberRow(bodyDataProvider.getRowObject(1));
				// rowGroup.addStaticMemberRow(bodyDataProvider.getRowObject(2));
				// rowGroupModel.addRowGroup(rowGroup);
			}
		}

		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider);
		CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer,
				columnHeaderLayer);

		GridLayer gridLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		NatTable natTable = new NatTable(parent, gridLayer, true);
		// natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		// natTable.addConfiguration(new HeaderMenuConfiguration(natTable) {
		// @Override
		// protected PopupMenuBuilder createColumnHeaderMenu(NatTable natTable) {
		// return super.createColumnHeaderMenu(natTable).withColumnChooserMenuItem();
		// }
		// });

		// FIXME
		// ISelectionProvider selectionProvider = new RowSelectionProvider(gridLayer.getBodyLayer().getSelectionLayer(),
		// bodyDataProvider, false); // Provides rows where any cell in the row is selected
		//
		// selectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {
		//
		// public void selectionChanged(SelectionChangedEvent event) {
		// System.out.println("Selection changed:");
		//
		// IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		// @SuppressWarnings("rawtypes")
		// Iterator it = selection.iterator();
		// while (it.hasNext()) {
		// System.out.println("  " + it.next());
		// }
		// }
		//
		// });
		//
		// // Programmatically select a few rows
		// selectionProvider.setSelection(new StructuredSelection(new Person[] { homer, smithers, nelson }));
		//
		// // I changed my mind. Select a few other rows
		// selectionProvider.setSelection(new StructuredSelection(new Person[] { bart, frink }));

		// Column chooser
		// DisplayColumnChooserCommandHandler columnChooserCommandHandler = new DisplayColumnChooserCommandHandler(
		// selectionLayer, bodyLayer.getColumnHideShowLayer(),
		// columnHeaderLayer, columnHeaderDataLayer,
		// columnHeaderLayer.getColumnGroupHeaderLayer(), columnGroupModel);
		// bodyLayer.registerCommandHandler(columnChooserCommandHandler);

		natTable.configure();


		return Pair.make(natTable, data);
	}

	static class TablePerspectiveHeaderDataProvider implements IDataProvider {
		private final VirtualArray va;
		private final boolean isDimension;
		private final IIDTypeMapper<Integer, String> mapper;

		public TablePerspectiveHeaderDataProvider(TablePerspective tablePerspective, boolean isDimension) {
			this.isDimension = isDimension;
			Perspective p = isDimension ? tablePerspective.getDimensionPerspective() : tablePerspective
					.getRecordPerspective();
			va = p.getVirtualArray();
			final IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(va.getIdType());
			mapper = idMappingManager.getIDTypeMapper(va.getIdType(), va.getIdType().getIDCategory()
					.getHumanReadableIDType());
		}

		@Override
		public int getColumnCount() {
			return isDimension ? va.size() : 1;
		}

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			int index = isDimension ? columnIndex : rowIndex;
			int lookup = va.get(index);
			Set<String> s = mapper.apply(lookup);
			if (s == null || s.isEmpty())
				return "" + index;
			return s.iterator().next();
		}

		@Override
		public int getRowCount() {
			return isDimension ? 1 : va.size();
		}

		@Override
		public void setDataValue(int arg0, int arg1, Object arg2) {
			throw new UnsupportedOperationException();
		}

	}
}

