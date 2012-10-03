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
package org.caleydo.view.tabular;

import java.util.Iterator;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.dimension.RawDataType;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.EDataFilterLevel;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.data.selection.events.ISelectionCommandHandler;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.events.IDimensionVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.IRecordVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateListener;
import org.caleydo.core.data.virtualarray.events.VADeltaEvent;
import org.caleydo.core.event.data.ClearSelectionsEvent;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.view.RedrawViewEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.swt.ASWTView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * View shows data from a set in a tabular format.
 * 
 * @author Marc Streit
 */
public class TabularDataView extends ASWTView implements
		IDataDomainBasedView<ATableBasedDataDomain>, ISelectionUpdateHandler,
		IRecordVAUpdateHandler, IDimensionVAUpdateHandler, ISelectionCommandHandler,
		IViewCommandHandler {
	public static String VIEW_TYPE = "org.caleydo.view.tabular";
	
	public static String VIEW_NAME = "Tabular View";

	private final static int COLUMN_OFFSET = 3;

	/**
	 * This manager is responsible for the content in the dimensions (the
	 * indices)
	 */
	protected RecordSelectionManager recordSelectionManager;

	/**
	 * This manager is responsible for the management of the dimensions in the
	 * set
	 */
	protected DimensionSelectionManager dimensionSelectionManager;

	/**
	 * The virtual array that manages the contents (the indices) in the
	 * dimensions
	 */
	protected RecordVirtualArray recordVA;

	/**
	 * The virtual array that manages the dimension references in the set
	 */
	protected DimensionVirtualArray dimensionVA;
	/**
	 * The type of the content VA
	 */
	protected String recordPerspectiveID;

	/**
	 * The type of the dimension VA
	 */
	protected String dimensionPerspectiveID;
	/**
	 * Define what level of filtering on the data should be applied
	 */
	protected EDataFilterLevel dataFilterLevel;

	private Table contentTable;

	private TableCursor contentTableCursor;

	protected SelectionUpdateListener selectionUpdateListener;
	protected RecordVAUpdateListener virtualArrayUpdateListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;

	protected ATableBasedDataDomain dataDomain;

	protected DataTable table;

	/**
	 * Constructor.
	 */
	public TabularDataView(Composite parentComposite) {
		super(GeneralManager.get().getIDCreator()
				.createID(ManagedObjectType.VIEW_SWT_TABULAR_DATA_VIEWER),
				parentComposite, VIEW_TYPE, VIEW_NAME);

	}

	@Override
	public void draw() {

		createTable();
	}

	public void initData() {

		table = dataDomain.getTable();

		recordSelectionManager = dataDomain.getRecordSelectionManager();
		dimensionSelectionManager = dataDomain.getDimensionSelectionManager();

		if (table == null) {
			recordSelectionManager.resetSelectionManager();
			dimensionSelectionManager.resetSelectionManager();
			return;
		}

		recordVA = dataDomain.getRecordVA(recordPerspectiveID);
		dimensionVA = dataDomain.getDimensionVA(dimensionPerspectiveID);

		recordSelectionManager.resetSelectionManager();
		dimensionSelectionManager.resetSelectionManager();

		// int iNumberOfColumns = recordVA.size();
		// int iNumberOfRows = dimensionVA.size();

		// for (int iRowCount = 0; iRowCount < iNumberOfRows; iRowCount++) {
		// dimensionSelectionManager.initialAdd(dimensionVA.get(iRowCount));
		//
		// }
		//
		// // this for loop executes one per axis
		// for (int iColumnCount = 0; iColumnCount < iNumberOfColumns;
		// iColumnCount++) {
		// contentSelectionManager.initialAdd(recordVA.get(iColumnCount));
		// }
	}

	private void createTable() {

		if (contentTable != null) {
			contentTable.removeAll();
			contentTable.dispose();
		}

		contentTable = new Table(parentComposite, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.VIRTUAL);
		contentTable.setLinesVisible(true);
		contentTable.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 300;
		data.widthHint = 700;
		contentTable.setLayoutData(data);

		contentTable.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Rectangle clientArea = contentTable.getClientArea();
				Point pt = new Point(event.x, event.y);

				int index = 0; // only make caption line editable

				boolean visible = false;
				final TableItem item = contentTable.getItem(index);
				for (int iColIndex = 1; iColIndex < contentTable.getColumnCount(); iColIndex++) {
					Rectangle rect = item.getBounds(iColIndex);
					if (rect.contains(pt)) {
						final int column = iColIndex;
						final Text text = new Text(contentTable, SWT.NONE);
						Listener textListener = new Listener() {
							@Override
							public void handleEvent(final Event e) {
								switch (e.type) {
								case SWT.FocusOut:
									item.setText(column, text.getText());
									text.dispose();
									break;
								case SWT.Traverse:
									switch (e.detail) {
									case SWT.TRAVERSE_RETURN:
										item.setText(column, text.getText());

										// FALL THROUGH
									case SWT.TRAVERSE_ESCAPE:
										text.dispose();
										e.doit = false;
									}
									break;
								}
							}
						};

						text.addListener(SWT.FocusOut, textListener);
						text.addListener(SWT.Traverse, textListener);
						// editor.setEditor(text, item, iColIndex);
						text.setText(item.getText(iColIndex));
						text.selectAll();
						text.setFocus();
						return;
					}

					if (!visible && rect.intersects(clientArea)) {
						visible = true;
					}
				}

				if (!visible)
					return;
				index++;
			}
		});

		TableColumn column;
		TableItem item;

		// // Remove experiment context menu
		// final Menu headerMenu = new Menu(composite.getShell(), SWT.POP_UP);
		// MenuItem itemName = new MenuItem(headerMenu, SWT.NONE);
		// itemName.setText("Remove experiment (column)");
		// itemName.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// dimensionVA.remove(0);
		// // axisSelectionManager.remove(externalID, false);
		// // IVirtualArrayDelta vaDelta = new
		// // VirtualArrayDelta(axisVAType,
		// // EIDType.EXPERIMENT_INDEX);
		// // vaDelta.add(VADeltaItem.remove(externalID));
		// // sendVirtualArrayUpdateEvent(vaDelta);
		// }
		// });
		// contentTable.addListener(SWT.MenuDetect, new Listener() {
		// public void handleEvent(Event event) {
		// // Point pt = Display.getCurrent().map(null, contentTable,
		// // new Point(event.x, event.y));
		// // Rectangle clientArea = contentTable.getClientArea();
		// // boolean header = clientArea.y <= pt.y && pt.y < (clientArea.y
		// // + contentTable.getHeaderHeight());
		// // if (header)
		// contentTable.setMenu(headerMenu);
		// }
		// });

		float fValue;

		column = new TableColumn(contentTable, SWT.NONE);
		column.setText("#");
		column.setWidth(50);

		if (dataDomain.getDataDomainID().equals("org.caleydo.datadomain.genetic")) {

			column = new TableColumn(contentTable, SWT.NONE);
			column.setText("RefSeq ID");
			column.setWidth(110);

			column = new TableColumn(contentTable, SWT.NONE);
			column.setText("Gene Symbol");
			column.setWidth(110);
		} else if (dataDomain.getDataDomainID().equals("org.caleydo.datadomain.generic")) {

			column = new TableColumn(contentTable, SWT.NONE);
			column.setText("ID");
			column.setWidth(200);

			column = new TableColumn(contentTable, SWT.NONE);
			column.setText("");
			column.setWidth(1);
		} else {
			throw new IllegalStateException("The data domain " + dataDomain
					+ " is not implemented in the tabular data viewer.");
		}

		for (final Integer iDimensionIndex : dimensionVA) {
			final TableColumn col = new TableColumn(contentTable, SWT.NONE);
			col.setText(dataDomain.getDimensionLabel(iDimensionIndex));
			col.setWidth(120);
			col.setMoveable(true);

			col.addSelectionListener(new SelectionAdapter() {
				// Label changer
				@Override
				public void widgetSelected(SelectionEvent e) {
					// re-activate
					// LabelEditorDialog dialog = new LabelEditorDialog(new
					// Shell());
					// String sLabel =
					// dialog.open(dataDomain.getDimensionLabel(iDimensionIndex));
					//
					// if (sLabel != null && !sLabel.isEmpty()) {
					// table.get(iDimensionIndex).setLabel(sLabel);
					// contentTable.getColumn(iDimensionIndex +
					// 3).setText(sLabel);
					// RedrawViewEvent event = new RedrawViewEvent();
					// event.setSender(this);
					// eventPublisher.triggerEvent(event);
					// }
				}
			});
		}

		int index = 0;
		for (Integer recordIndex : recordVA) {
			// line number
			item = new TableItem(contentTable, SWT.NONE);
			// item.setData(recordIndex);
			item.setText(0, Integer.toString(index));

			item.setText(1, dataDomain.getRecordLabel(recordIndex));

			int i = 3;
			for (Integer dimensionID : dimensionVA) {
				RawDataType rawDataType = table.getRawDataType(dimensionID, recordIndex);
				if (rawDataType == RawDataType.STRING) {
					String text = table.getRaw(dimensionID, recordIndex);
					item.setText(i++, text);
				} else if (rawDataType == RawDataType.FLOAT) {

					fValue = table.getFloat(DataRepresentation.RAW, recordIndex,
							dimensionID);

					item.setText(i++, Float.toString(fValue));
				} else {
					throw new IllegalStateException("Data Type " + rawDataType
							+ " not implemented.");
				}
			}

			index++;
		}
		for (TableColumn tempColumn : contentTable.getColumns()) {
			tempColumn.pack();
		}

		contentTableCursor = new TableCursor(contentTable, SWT.NONE);
		contentTableCursor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int iColIndex = contentTableCursor.getColumn() - 3;
				int iRowIndex = contentTable.indexOf(contentTableCursor.getRow());
				contentTable.setSelection(iRowIndex);

				int iRefSeqID = recordVA.get(iRowIndex);
				int iDimensionIndex = dimensionVA.get(iColIndex);

				triggerDimensionSelectionEvent(iDimensionIndex, SelectionType.SELECTION);
				triggerContentSelectionEvent(iRefSeqID, SelectionType.SELECTION);

				// addContentRemoveIcon(iRowIndex);
				// addDimensionRemoveIcon(iDimensionIndex);
			}
		});

		parentComposite.layout();
	}

	@Override
	public void handleRedrawView() {
		// nothing to do here
	}

	@Override
	public void handleClearSelections() {
		clearAllSelections();
	}

	@Override
	public void handleSelectionCommand(IDCategory category,
			SelectionCommand selectionCommand) {
		if (dataDomain.getRecordIDCategory() == category)
			recordSelectionManager.executeSelectionCommand(selectionCommand);
		else if (dataDomain.getDimensionIDCategory() == category)
			dimensionSelectionManager.executeSelectionCommand(selectionCommand);
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		// Check for type that can be handled
		if (selectionDelta.getIDType().getIDCategory() == dataDomain
				.getRecordIDCategory()) {
			recordSelectionManager.setDelta(selectionDelta);
		} else if (selectionDelta.getIDType().getIDCategory() == dataDomain
				.getDimensionIDCategory()) {
			dimensionSelectionManager.setDelta(selectionDelta);
		}

		reactOnExternalSelection();
	}

	private void addColumn(final int index, final int dimensionNumber) {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TableColumn column = new TableColumn(contentTable, SWT.NONE, index);
				column.setText(dataDomain.getDimensionLabel(dimensionNumber));
				TableItem[] items = contentTable.getItems();
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					RawDataType rawDataType = table.getRawDataType(dimensionNumber,
							recordVA.get(i));
					String value;
					if (rawDataType == RawDataType.FLOAT) {
						value = Float.toString(table.getFloat(DataRepresentation.RAW,
								recordVA.get(i), dimensionNumber));

					} else if (rawDataType == RawDataType.STRING)
						value = table.getRaw(dimensionNumber, recordVA.get(i));
					else
						throw new IllegalStateException(rawDataType + " not implemented");

					item.setText(index, value);

				}
				column.pack();

			}
		});
	}

	/**
	 * Highlight the selected cell in the table. Only the first element is
	 * taken, since we cannot handle multiple selections ATM.
	 */
	private void reactOnExternalSelection() {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (contentTable.isDisposed())
					return;

				int iRowIndex = 0;
				int iColIndex = 0;
				contentTable.deselectAll();

				Iterator<Integer> iterContentIndex = recordSelectionManager.getElements(
						SelectionType.SELECTION).iterator();

				// FIXME: currently we do not handle multiple selections (->
				// replace if with while)
				while (iterContentIndex.hasNext()) {
					iRowIndex = recordVA.indexOf(iterContentIndex.next());
					contentTable.select(iRowIndex);
				}

				// FIXME: currently we do not handle multiple selections (->
				// replace if with while)
				Iterator<Integer> iterDimensionIndex = dimensionSelectionManager
						.getElements(SelectionType.SELECTION).iterator();
				while (iterDimensionIndex.hasNext()) {
					iColIndex = dimensionVA.indexOf(iterDimensionIndex.next()) + 3;
				}

				contentTableCursor.setSelection(iRowIndex,
						contentTable.getColumnOrder()[iColIndex]);
			}
		});
	}

	private void triggerContentSelectionEvent(int recordIndex, SelectionType SelectionType) {
		if (recordSelectionManager.checkStatus(SelectionType, recordIndex))
			return;

		recordSelectionManager.clearSelection(SelectionType);
		recordSelectionManager.addToType(SelectionType, recordIndex);

		SelectionDelta selectionDelta = recordSelectionManager.getDelta();

		// SelectionCommand command = new
		// SelectionCommand(ESelectionCommandType.CLEAR,
		// SelectionType);
		// sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

		SelectionUpdateEvent event = new SelectionUpdateEvent();

		event.setSender(this);

		event.setSelectionDelta((SelectionDelta) selectionDelta);
		eventPublisher.triggerEvent(event);
	}

	private void triggerDimensionSelectionEvent(int iDimensionIndex,
			SelectionType SelectionType) {
		if (dimensionSelectionManager.checkStatus(SelectionType, iDimensionIndex))
			return;

		dimensionSelectionManager.clearSelection(SelectionType);
		dimensionSelectionManager.addToType(SelectionType, iDimensionIndex);

		SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR,
				SelectionType);
		sendSelectionCommandEvent(dataDomain.getRecordIDType(), command);

		SelectionDelta selectionDelta = dimensionSelectionManager.getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		eventPublisher.triggerEvent(event);
	}

	private void clearAllSelections() {
		recordSelectionManager.clearSelections();
		dimensionSelectionManager.clearSelections();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedTabularDataView serializedForm = new SerializedTabularDataView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// this implementation does not initialize anything yet
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		virtualArrayUpdateListener = new RecordVAUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VADeltaEvent.class, virtualArrayUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (virtualArrayUpdateListener != null) {
			eventPublisher.removeListener(virtualArrayUpdateListener);
			virtualArrayUpdateListener = null;
		}
		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}
		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
		}
		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}

	}

	@Override
	public void handleRecordVAUpdate(String recordPerspectiveID) {
		if (!this.recordPerspectiveID.equals(recordPerspectiveID))
			return;
		recordVA = dataDomain.getRecordVA(recordPerspectiveID);
		initData();
		createTable();
	}

	@Override
	public void handleDimensionVAUpdate(String dimensionPerspectiveID) {
		if (!this.dimensionPerspectiveID.equals(dimensionPerspectiveID))
			return;
		dimensionVA = dataDomain.getDimensionVA(dimensionPerspectiveID);
		initData();
		createTable();

		// if (vaDelta.getIDType() == dataDomain.getDimensionIDType()) {
		// dimensionSelectionManager.virtualArrayUpdated(vaDelta);
		//
		// for (VADeltaItem deltaItem : vaDelta.getAllItems()) {
		// final int iVAIndex = deltaItem.getIndex();
		//
		// switch (deltaItem.getType()) {
		// case REMOVE:
		// parentComposite.getDisplay().asyncExec(new Runnable() {
		// @Override
		// public void run() {
		// contentTable.getColumn(iVAIndex + 3).dispose();
		// }
		// });
		// break;
		// case ADD:
		// addColumn(deltaItem.getIndex() + COLUMN_OFFSET,
		// deltaItem.getID());
		// break;
		// case COPY:
		// addColumn(deltaItem.getIndex() + 1 + COLUMN_OFFSET,
		// dimensionVA.get(deltaItem.getIndex()));
		//
		// break;
		// case MOVE:
		// // case MOVE_LEFT:
		// // case MOVE_RIGHT:
		// int[] orig = contentTable.getColumnOrder();
		//
		// ArrayList<Integer> ordered = new ArrayList<Integer>(orig.length);
		//
		// for (int index : orig) {
		// ordered.add(index);
		// }
		//
		// Integer item = ordered.remove(deltaItem.getIndex() + COLUMN_OFFSET);
		// ordered.add(deltaItem.getTargetIndex() + COLUMN_OFFSET, item);
		// for (int count = 0; count < ordered.size(); count++) {
		// orig[count] = ordered.get(count);
		// }
		//
		// contentTable.setColumnOrder(orig);
		// break;
		// default:
		// throw new IllegalStateException("EVAOperation not implemented");
		// }
		// }
		//
		// }
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		initData();
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public boolean isDataView() {
		return true;
	}

}
