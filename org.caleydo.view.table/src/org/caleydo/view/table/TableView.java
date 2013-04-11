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
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

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
		Set<Integer> records = selection.getRecordSelectionManager().getElements(SelectionType.SELECTION);
		Set<Integer> dimensions = selection.getDimensionSelectionManager().getElements(SelectionType.SELECTION);
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		if (table != null)
			table.refresh();
	}

	// Default selection behavior selects cells by default.
	@Override
	public void handleLayerEvent(ILayerEvent event) {
		if (event instanceof CellSelectionEvent) {
			CellSelectionEvent cellEvent = (CellSelectionEvent) event;
			int rowIndex = table.getRowIndexByPosition(cellEvent.getRowPosition());
			int colIndex = table.getColumnIndexByPosition(cellEvent.getColumnPosition());

		} else if (event instanceof RowSelectionEvent) {
			RowSelectionEvent rowEvent = (RowSelectionEvent) event;
			for (Range range : rowEvent.getRowPositionRanges()) {
			}
			// rowDataProvider.getRowObject(natTable.getRowIndexByPosition(selectedRowPosition));
		} else if (event instanceof ColumnSelectionEvent) {
			ColumnSelectionEvent columnEvent = (ColumnSelectionEvent) event;
			for (Range range : columnEvent.getColumnPositionRanges()) {

			}
		}
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
			table.removeLayerListener(this);
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

			table.addLayerListener(this);
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
}
