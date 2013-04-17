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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.swt.ASingleTablePerspectiveSWTView;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
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
import org.eclipse.nebula.widgets.nattable.group.model.RowGroup;
import org.eclipse.nebula.widgets.nattable.group.model.RowGroupModel;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.RowHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.ISelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.DiscreteDomains;
import com.google.common.collect.Ranges;
import com.google.common.primitives.Ints;

/**
 * the main part of this plugin, where a {@link NatTable} is used to present a {@link TablePerspective}
 *
 * @author Samuel Gratzl
 *
 */
public class TableView extends ASingleTablePerspectiveSWTView implements ILayerListener {
	public static final String VIEW_TYPE = "org.caleydo.view.table";
	public static final String VIEW_NAME = "Table";

	private final CaleydoRCPViewPart view;
	private boolean selectionOnly;

	private DataProvider data;
	private NatTable table;
	private SelectionLayer selectionLayer;
	private CustomDisplayConverter converter;
	private ColumnHideShowLayer columnHidder;
	private RowHideShowLayer rowHidder;

	public TableView(Composite parentComposite, CaleydoRCPViewPart view) {
		super(parentComposite, VIEW_TYPE, VIEW_NAME);
		this.view = view;
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		if (selectionLayer == null)
			return;

		Collection<Integer> records = toIndices(tablePerspective.getRecordPerspective(),  selection.getRecordSelectionManager().getElements(SelectionType.SELECTION));
		Collection<Integer> dimensions = toIndices(tablePerspective.getDimensionPerspective(),  selection.getDimensionSelectionManager().getElements(SelectionType.SELECTION));

		ISelectionModel model = selectionLayer.getSelectionModel();
		selectionLayer.removeLayerListener(this); // remove me to avoid that I get my updates
		model.clearSelection();

		if (records.isEmpty() && dimensions.isEmpty()) {
			return;
		}
		if (records.isEmpty()) { // just dimensions, select columns
			for (Integer id : dimensions) {
				model.addSelection(new Rectangle(id, 0, 1, Integer.MAX_VALUE));
			}
		} else if (dimensions.isEmpty()) { // just records, select rows
			for (Integer id : records) {
				model.addSelection(new Rectangle(0, id, Integer.MAX_VALUE, 1));
			}
		} else { // both select cells
			// select all cells in the cross product
			for (Integer row : records) {
				for (Integer dim : dimensions) {
					model.addSelection(dim, row);
				}
			}
		}
		if (selectionOnly)
			showJustSelection();
	}

	/**
	 * @param dimensionPerspective
	 * @param dimensions
	 * @return
	 */
	private static Collection<Integer> toIndices(Perspective per, Set<Integer> ids) {
		Collection<Integer> indices = new ArrayList<>(ids.size());
		for (Integer id : ids) {
			int index = per.getVirtualArray().indexOf(id);
			if (index < 0)
				continue;
			indices.add(index);
		}
		return indices;
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		if (table != null)
			table.refresh();
	}

	// Default selection behavior selects cells by default.
	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if (event instanceof CellSelectionEvent || event instanceof RowSelectionEvent
				|| event instanceof ColumnSelectionEvent) {
			ISelectionModel model = selectionLayer.getSelectionModel();
			Collection<Integer> columns = Ints.asList(model.getSelectedColumnPositions());
			Collection<Integer> rows = new TreeSet<>();
			for (Range r : model.getSelectedRowPositions())
				rows.addAll(r.getMembers());
			int columnCount = data.getColumnCount();
			int rowCount = data.getRowCount();
			boolean allColumnsSelected = columns.size() == columnCount;
			boolean allRowsSelected = rows.size() == rowCount;
			if (allColumnsSelected == allRowsSelected) { // select all or mixed
				select(selection.getDimensionSelectionManager(), columns, false);
				select(selection.getRecordSelectionManager(), rows, true);
			} else if (allColumnsSelected) { // selected rows
				select(selection.getRecordSelectionManager(), rows, true);
			} else if (allRowsSelected) { // just columns
				select(selection.getDimensionSelectionManager(), columns, false);
			}
		}
	}

	private void select(SelectionManager manager, Collection<Integer> positions, boolean row) {
		manager.clearSelection(SelectionType.SELECTION);
		for (Integer pos : positions) {
			int index = row ? selectionLayer.getRowIndexByPosition(pos) : selectionLayer.getColumnIndexByPosition(pos);
			if (index < 0)
				continue;
			int id = row ? data.getRowObject(index) : data.getColumnObject(index);
			manager.addToType(SelectionType.SELECTION, id);
		}
		selection.fireSelectionDelta(manager.getIDType());
	}

	/**
	 * @param raw
	 */
	public void setDataRepresentation(EDataRepresentation mode) {
		if (table == null)
			return;
		this.data.setDataRepresentation(mode);
		table.refresh();
	}

	public EDataRepresentation getDataRepresentation() {
		if (table == null)
			return EDataRepresentation.RAW;
		return data.getDataRepresentation();
	}

	@Override
	protected void onSetTablePerspective() {
		super.onSetTablePerspective();
		if (table != null) { // cleanup old
			selectionLayer.removeLayerListener(this);
			this.table.dispose();
			this.table = null;
			this.data = null;
			this.selectionLayer = null;
			this.converter = null;
		}

		if (tablePerspective != null) {
			build();
			onSelectionUpdate(null); // apply current selection

			selectionLayer.addLayerListener(this);

			view.setPartName(tablePerspective.getLabel());
		}
	}

	private void build() {
		final GroupList dimensionGroups = tablePerspective.getDimensionPerspective().getVirtualArray().getGroupList();
		final boolean hasDimensionGroups = dimensionGroups != null && dimensionGroups.size() > 1;
		final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		final GroupList recordGroups = recordVA.getGroupList();
		final boolean hasRecordGroups = recordGroups != null && recordGroups.size() > 1;

		IUniqueIndexLayer bodyLayer;

		this.data = new DataProvider(tablePerspective);
		bodyLayer = new DataLayer(data);
		bodyLayer = new ColumnReorderLayer(bodyLayer);

		bodyLayer = this.columnHidder = new ColumnHideShowLayer(bodyLayer);

		ColumnGroupModel columnGroupModel = null;
		if (hasDimensionGroups) {
			columnGroupModel = new ColumnGroupModel();
			bodyLayer = new ColumnGroupExpandCollapseLayer(bodyLayer, columnGroupModel);
		}

		bodyLayer = this.rowHidder = new RowHideShowLayer(bodyLayer);

		RowGroupModel<Integer> rowGroupModel = null;
		if (hasRecordGroups && recordGroups != null) {
			rowGroupModel = new RowGroupModel<Integer>();
			rowGroupModel.setDataProvider(data);
			bodyLayer = new RowGroupExpandCollapseLayer<>(bodyLayer, rowGroupModel);
		}

		this.selectionLayer = new SelectionLayer(bodyLayer);
		ViewportLayer viewportLayerBase = new ViewportLayer(selectionLayer);
		final FreezeLayer freezeLayer = new FreezeLayer(selectionLayer);
		ILayer viewportLayer = new CompositeFreezeLayer(freezeLayer, viewportLayerBase, selectionLayer);

		IDataProvider colHeaderDataProvider = new TablePerspectiveHeaderDataProvider(tablePerspective, true);
		DataLayer columnHeaderDataLayer = new DataLayer(colHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

		if (hasDimensionGroups && dimensionGroups != null) {
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

		if (hasRecordGroups && recordGroups != null && rowGroupModel != null) {
			RowGroupHeaderLayer<Integer> rowGroupHeaderLayer = new RowGroupHeaderLayer<>(rowHeaderLayer,
					selectionLayer, rowGroupModel);
			rowGroupHeaderLayer.setColumnWidth(15);
			rowHeaderLayer = rowGroupHeaderLayer;
			// Create a group of rows for the model.
			for (Group group : recordGroups) {
				RowGroup<Integer> rowGroup = new RowGroup<>(rowGroupModel, group.getLabel(), false);
				for (Integer id : recordVA.getIDsOfGroup(group.getGroupIndex())) {
					rowGroup.addMemberRow(id);
				}
				int rep = (group.getRepresentativeElementIndex() >= 0) ? group.getRepresentativeElementIndex() : group
						.getStartIndex();
				Integer id = recordVA.get(rep);
				rowGroup.addStaticMemberRow(id);
				rowGroupModel.addRowGroup(rowGroup);
			}
		}

		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(colHeaderDataProvider,
				rowHeaderDataProvider);
		CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

		GridLayer gridLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		this.table = new NatTable(parentComposite, gridLayer, false);
		this.table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.converter = new CustomDisplayConverter();
		configureStyle(this.table, converter);

		// natTable.getConfigRegistry().registerConfigAttribute(configAttribute, attributeValue)
		// natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		// natTable.addConfiguration(new HeaderMenuConfiguration(natTable) {
		// @Override
		// protected PopupMenuBuilder createColumnHeaderMenu(NatTable natTable) {
		// return super.createColumnHeaderMenu(natTable).withColumnChooserMenuItem();
		// }
		// });

		// Column chooser
		// DisplayColumnChooserCommandHandler columnChooserCommandHandler = new DisplayColumnChooserCommandHandler(
		// selectionLayer, bodyLayer.getColumnHideShowLayer(),
		// columnHeaderLayer, columnHeaderDataLayer,
		// columnHeaderLayer.getColumnGroupHeaderLayer(), columnGroupModel);
		// bodyLayer.registerCommandHandler(columnChooserCommandHandler);

		this.table.configure();
	}

	protected static void configureStyle(NatTable natTable, final IDisplayConverter converter) {
		DefaultNatTableStyleConfiguration natTableConfiguration = new DefaultNatTableStyleConfiguration();
		natTableConfiguration.hAlign = HorizontalAlignmentEnum.RIGHT;
		natTable.addConfiguration(natTableConfiguration);

		DefaultSelectionStyleConfiguration selectionStyle = new DefaultSelectionStyleConfiguration();
		selectionStyle.selectionFont = natTableConfiguration.font;
		natTable.addConfiguration(selectionStyle);

		natTable.addConfiguration(new IConfiguration() {
			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {

			}

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, converter);
			}

			@Override
			public void configureLayer(ILayer layer) {

			}
		});
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView serializedView) {

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedTableView serializedForm = new SerializedTableView(this);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "Table";
	}

	public void changeFormattingPrecision(int delta) {
		if (converter == null)
			return;
		converter.changeMinFractionDigits(delta);
		table.refresh();
	}

	/**
	 * @param checked
	 */
	public void setSelectionOnly(boolean checked) {
		if (this.selectionOnly == checked)
			return;
		this.selectionOnly = checked;
		if (this.selectionOnly) {
			showJustSelection();
		} else {
			showAll();
		}
	}

	private void showJustSelection() {
		rowHidder.showAllRows();
		Collection<Integer> toKeep = new ArrayList<>();
		for(Integer id : selection.getRecordSelectionManager().getElements(SelectionType.SELECTION)) {
			int index = data.indexOfRowObject(id);
			if (index < 0)
				continue;
			int position = rowHidder.getRowPositionByIndex(index);
			if (position < 0)
				continue;
			toKeep.add(position);
		}
		if (!toKeep.isEmpty()) {
			Set<Integer> all = new HashSet<>(Ranges.closed(rowHidder.getRowPositionByIndex(0),
					rowHidder.getRowPositionByIndex(data.getRowCount() - 1)).asSet(DiscreteDomains.integers()));
			all.removeAll(toKeep);
			rowHidder.hideRowPositions(all);
		}

		columnHidder.showAllColumns();
		toKeep.clear();
		for (Integer id : selection.getDimensionSelectionManager().getElements(SelectionType.SELECTION)) {
			int index = data.indexOfColumnObject(id);
			if (index < 0)
				continue;
			int position = columnHidder.getColumnPositionByIndex(index);
			if (position < 0)
				continue;
			toKeep.add(position);
		}
		if (!toKeep.isEmpty()) {
			Set<Integer> all = new HashSet<>(Ranges.closed(columnHidder.getColumnPositionByIndex(0),
					columnHidder.getColumnPositionByIndex(data.getColumnCount() - 1)).asSet(DiscreteDomains.integers()));
			all.removeAll(toKeep);
			columnHidder.hideColumnPositions(all);
		}
	}

	private void showAll() {
		rowHidder.showAllRows();
		columnHidder.showAllColumns();
	}
}
