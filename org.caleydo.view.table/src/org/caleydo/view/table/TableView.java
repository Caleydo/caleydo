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
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.swt.ASingleTablePerspectiveSWTView;
import org.caleydo.view.table.NatTableBuilder.CustomDisplayConverter;
import org.caleydo.view.table.NatTableBuilder.NatTableSettings;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.ISelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.google.common.primitives.Ints;

/**
 * @author Samuel Gratzl
 *
 */
public class TableView extends ASingleTablePerspectiveSWTView implements ILayerListener {
	public static final String VIEW_TYPE = "org.caleydo.view.table";
	public static final String VIEW_NAME = "Table";

	private DataProvider data;
	private NatTable table;
	private SelectionLayer selectionLayer;
	private CustomDisplayConverter converter;

	public TableView(Composite parentComposite) {
		super(parentComposite, VIEW_TYPE, VIEW_NAME);
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
			// nothing to be done
		} else if (records.isEmpty()) { // just dimensions, select columns
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
		selectionLayer.addLayerListener(this);
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
			int index = row ? table.getRowIndexByPosition(pos) : table.getColumnIndexByPosition(pos);
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
			NatTableSettings settings = NatTableBuilder.create(parentComposite, tablePerspective);
			this.table = settings.natTable;
			this.table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			this.data = settings.dataProvider;
			this.selectionLayer = settings.selectionLayer;
			this.converter = settings.converter;

			onSelectionUpdate(null); // apply current selection

			selectionLayer.addLayerListener(this);
		}
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
}
