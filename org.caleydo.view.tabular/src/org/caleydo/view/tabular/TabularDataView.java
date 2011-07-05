package org.caleydo.view.tabular;

import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.gui.util.LabelEditorDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IStorageVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceContentVAListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
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
import org.eclipse.swt.widgets.Shell;
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
		IDataDomainBasedView<ASetBasedDataDomain>, ISelectionUpdateHandler,
		IContentVAUpdateHandler, IStorageVAUpdateHandler, ISelectionCommandHandler,
		IViewCommandHandler {

	public final static String VIEW_ID = "org.caleydo.view.tabular";
	private final static int COLUMN_OFFSET = 3;

	/**
	 * This manager is responsible for the content in the storages (the indices)
	 */
	protected ContentSelectionManager contentSelectionManager;

	/**
	 * This manager is responsible for the management of the storages in the set
	 */
	protected StorageSelectionManager storageSelectionManager;

	/**
	 * The virtual array that manages the contents (the indices) in the storages
	 */
	protected ContentVirtualArray contentVA;

	/**
	 * The virtual array that manages the storage references in the set
	 */
	protected StorageVirtualArray storageVA;
	/**
	 * The type of the content VA
	 */
	protected String contentVAType = ISet.CONTENT;

	/**
	 * The type of the storage VA
	 */
	protected String storageVAType = ISet.STORAGE;

	/**
	 * Define what level of filtering on the data should be applied
	 */
	protected EDataFilterLevel dataFilterLevel;

	private Table contentTable;

	private TableCursor contentTableCursor;

	protected SelectionUpdateListener selectionUpdateListener;
	protected ContentVAUpdateListener virtualArrayUpdateListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;
	protected ReplaceContentVAListener replaceContentVAListener;

	protected ASetBasedDataDomain dataDomain;

	protected ISet set;

	/**
	 * Constructor.
	 */
	public TabularDataView(Composite parentComposite) {
		super(GeneralManager.get().getIDCreator()
				.createID(EManagedObjectType.VIEW_SWT_TABULAR_DATA_VIEWER),
				parentComposite);

		this.viewType = VIEW_ID;
	}

	@Override
	public void draw() {

		createTable();
	}

	public void initData() {

		set = dataDomain.getSet();

		contentSelectionManager = dataDomain.getContentSelectionManager();
		storageSelectionManager = dataDomain.getStorageSelectionManager();

		if (set == null) {
			contentSelectionManager.resetSelectionManager();
			storageSelectionManager.resetSelectionManager();
			return;
		}

		contentVA = dataDomain.getContentVA(contentVAType);
		storageVA = dataDomain.getStorageVA(storageVAType);

		contentSelectionManager.resetSelectionManager();
		storageSelectionManager.resetSelectionManager();

		contentSelectionManager.setVA(contentVA);
		storageSelectionManager.setVA(storageVA);

		// int iNumberOfColumns = contentVA.size();
		// int iNumberOfRows = storageVA.size();

		// for (int iRowCount = 0; iRowCount < iNumberOfRows; iRowCount++) {
		// storageSelectionManager.initialAdd(storageVA.get(iRowCount));
		//
		// }
		//
		// // this for loop executes one per axis
		// for (int iColumnCount = 0; iColumnCount < iNumberOfColumns;
		// iColumnCount++) {
		// contentSelectionManager.initialAdd(contentVA.get(iColumnCount));
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
		// storageVA.remove(0);
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

		if (dataDomain.getDataDomainType().equals("org.caleydo.datadomain.genetic")) {

			column = new TableColumn(contentTable, SWT.NONE);
			column.setText("RefSeq ID");
			column.setWidth(110);

			column = new TableColumn(contentTable, SWT.NONE);
			column.setText("Gene Symbol");
			column.setWidth(110);
		} else if (dataDomain.getDataDomainType()
				.equals("org.caleydo.datadomain.generic")) {

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

		for (final Integer iStorageIndex : storageVA) {
			final TableColumn col = new TableColumn(contentTable, SWT.NONE);
			col.setText(set.get(iStorageIndex).getLabel());
			col.setWidth(120);
			col.setMoveable(true);

			col.addSelectionListener(new SelectionAdapter() {
				// Label changer
				@Override
				public void widgetSelected(SelectionEvent e) {
					LabelEditorDialog dialog = new LabelEditorDialog(new Shell());
					String sLabel = dialog.open(set.get(iStorageIndex).getLabel());

					if (sLabel != null && !sLabel.isEmpty()) {
						set.get(iStorageIndex).setLabel(sLabel);
						contentTable.getColumn(iStorageIndex + 3).setText(sLabel);
						RedrawViewEvent event = new RedrawViewEvent();
						event.setSender(this);
						eventPublisher.triggerEvent(event);
					}
				}
			});
		}

		int index = 0;
		for (Integer iContentIndex : contentVA) {
			// line number
			item = new TableItem(contentTable, SWT.NONE);
			// item.setData(iContentIndex);
			item.setText(0, Integer.toString(index));

			item.setText(1, dataDomain.getContentLabel(iContentIndex));

			int i = 3;
			for (Integer iStorageIndex : storageVA) {
				fValue = set.get(iStorageIndex).getFloat(EDataRepresentation.RAW,
						iContentIndex);

				item.setText(i++, Float.toString(fValue));
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

				int iRefSeqID = contentVA.get(iRowIndex);
				int iStorageIndex = storageVA.get(iColIndex);

				triggerStorageSelectionEvent(iStorageIndex, SelectionType.SELECTION);
				triggerContentSelectionEvent(iRefSeqID, SelectionType.SELECTION);

				// addContentRemoveIcon(iRowIndex);
				// addStorageRemoveIcon(iStorageIndex);
			}
		});

		parentComposite.layout();
	}

	@Override
	public void handleRedrawView() {
		// nothing to do here
	}

	@Override
	public void handleUpdateView() {
		// nothing to do here
	}

	@Override
	public void handleClearSelections() {
		clearAllSelections();
	}

	@Override
	public void handleSelectionCommand(IDCategory category,
			SelectionCommand selectionCommand) {
		if (dataDomain.getContentIDCategory() == category)
			contentSelectionManager.executeSelectionCommand(selectionCommand);
		else if (dataDomain.getStorageIDCategory() == category)
			storageSelectionManager.executeSelectionCommand(selectionCommand);
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scroolToSelection, String info) {
		// Check for type that can be handled
		if (selectionDelta.getIDType().getIDCategory() == dataDomain
				.getContentIDCategory()) {
			contentSelectionManager.setDelta(selectionDelta);
		} else if (selectionDelta.getIDType().getIDCategory() == dataDomain
				.getStorageIDCategory()) {
			storageSelectionManager.setDelta(selectionDelta);
		}

		reactOnExternalSelection();
	}

	private void addColumn(final int index, final int storageNumber) {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TableColumn column = new TableColumn(contentTable, SWT.NONE, index);
				AStorage storage = set.get(storageNumber);
				column.setText(storage.getLabel());
				TableItem[] items = contentTable.getItems();
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					float value = set.get(storageNumber).getFloat(
							EDataRepresentation.RAW, contentVA.get(i));
					item.setText(index, Float.toString(value));

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

				Iterator<Integer> iterContentIndex = contentSelectionManager.getElements(
						SelectionType.SELECTION).iterator();

				// FIXME: currently we do not handle multiple selections (->
				// replace if with while)
				while (iterContentIndex.hasNext()) {
					iRowIndex = contentVA.indexOf(iterContentIndex.next());
					contentTable.select(iRowIndex);
				}

				// FIXME: currently we do not handle multiple selections (->
				// replace if with while)
				Iterator<Integer> iterStorageIndex = storageSelectionManager.getElements(
						SelectionType.SELECTION).iterator();
				while (iterStorageIndex.hasNext()) {
					iColIndex = storageVA.indexOf(iterStorageIndex.next()) + 3;
				}

				contentTableCursor.setSelection(iRowIndex,
						contentTable.getColumnOrder()[iColIndex]);
			}
		});
	}

	private void triggerContentSelectionEvent(int iContentIndex,
			SelectionType SelectionType) {
		if (contentSelectionManager.checkStatus(SelectionType, iContentIndex))
			return;

		contentSelectionManager.clearSelection(SelectionType);
		contentSelectionManager.addToType(SelectionType, iContentIndex);

		// if (dataDomain.equals("org.caleydo.datadomain.genetic")) {
		// // Resolve multiple spotting on chip and add all to the
		// // selection manager.
		// Integer iRefSeqID = null;
		// // FIXME: Due to new mapping system, a mapping involving expression
		// // index can return a Set of
		// // values, depending on the IDType that has been specified when
		// // loading expression data.
		// // Possibly a different handling of the Set is required.
		// Set<Integer> setRefSeqIDs = idMappingManager.getIDAsSet(
		// EIDType.EXPRESSION_INDEX, EIDType.REFSEQ_MRNA_INT, iContentIndex);
		//
		// if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
		// iRefSeqID = (Integer) setRefSeqIDs.toArray()[0];
		// }
		// if (iRefSeqID != null) {
		// for (Object iExpressionIndex : idMappingManager
		// .<Integer, Object> getIDAsSet(EIDType.REFSEQ_MRNA_INT,
		// EIDType.EXPRESSION_INDEX, iRefSeqID)) {
		// contentSelectionManager.addToType(SelectionType,
		// (Integer) iExpressionIndex);
		// }
		// }
		// }

		ISelectionDelta selectionDelta = contentSelectionManager.getDelta();

		// SelectionCommand command = new
		// SelectionCommand(ESelectionCommandType.CLEAR,
		// SelectionType);
		// sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

		SelectionUpdateEvent event = new SelectionUpdateEvent();

		event.setSender(this);

		event.setSelectionDelta((SelectionDelta) selectionDelta);
		eventPublisher.triggerEvent(event);
	}

	private void triggerStorageSelectionEvent(int iStorageIndex,
			SelectionType SelectionType) {
		if (storageSelectionManager.checkStatus(SelectionType, iStorageIndex))
			return;

		storageSelectionManager.clearSelection(SelectionType);
		storageSelectionManager.addToType(SelectionType, iStorageIndex);

		SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR,
				SelectionType);
		sendSelectionCommandEvent(dataDomain.getContentIDType(), command);

		ISelectionDelta selectionDelta = storageSelectionManager.getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		eventPublisher.triggerEvent(event);
	}

	private void clearAllSelections() {
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();
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

		virtualArrayUpdateListener = new ContentVAUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class,
				virtualArrayUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		replaceContentVAListener = new ReplaceContentVAListener();
		replaceContentVAListener.setHandler(this);
		eventPublisher.addListener(ReplaceContentVAEvent.class, replaceContentVAListener);
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

		if (replaceContentVAListener != null) {
			eventPublisher.removeListener(replaceContentVAListener);
			replaceContentVAListener = null;
		}
	}

	@Override
	public void handleVAUpdate(ContentVADelta vaDelta, String info) {
		if (vaDelta.getIDType() != dataDomain.getContentIDType())
			vaDelta = DeltaConverter.convertDelta(dataDomain.getContentIDType(), vaDelta);
		contentSelectionManager.setVADelta(vaDelta);
	}

	@Override
	public void replaceContentVA(int setID, String dataDomainType, String vaType) {

		contentVA = dataDomain.getContentVA(vaType);

		initData();
		createTable();

	}

	@Override
	public void handleVAUpdate(StorageVADelta vaDelta, String info) {
		if (vaDelta.getIDType() == dataDomain.getStorageIDType()) {
			storageSelectionManager.setVADelta(vaDelta);

			for (VADeltaItem deltaItem : vaDelta.getAllItems()) {
				final int iVAIndex = deltaItem.getIndex();

				switch (deltaItem.getType()) {
				case REMOVE:
					parentComposite.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							contentTable.getColumn(iVAIndex + 3).dispose();
						}
					});
					break;
				case ADD:
					addColumn(deltaItem.getIndex() + COLUMN_OFFSET,
							deltaItem.getPrimaryID());
					break;
				case COPY:
					addColumn(deltaItem.getIndex() + 1 + COLUMN_OFFSET,
							storageVA.get(deltaItem.getIndex()));

					break;
				case MOVE:
					// case MOVE_LEFT:
					// case MOVE_RIGHT:
					int[] orig = contentTable.getColumnOrder();

					ArrayList<Integer> ordered = new ArrayList<Integer>(orig.length);

					for (int index : orig) {
						ordered.add(index);
					}

					Integer item = ordered.remove(deltaItem.getIndex() + COLUMN_OFFSET);
					ordered.add(deltaItem.getTargetIndex() + COLUMN_OFFSET, item);
					for (int count = 0; count < ordered.size(); count++) {
						orig[count] = ordered.get(count);
					}

					contentTable.setColumnOrder(orig);
					break;
				default:
					throw new IllegalStateException("EVAOperation not implemented");
				}
			}

		}
	}

	@Override
	public void replaceStorageVA(String dataDomainType, String vaType) {
		storageVA = dataDomain.getStorageVA(vaType);
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;

		initData();
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

}
