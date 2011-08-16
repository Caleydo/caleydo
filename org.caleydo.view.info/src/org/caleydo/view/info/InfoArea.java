package org.caleydo.view.info;

import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ReplaceDimensionVAEvent;
import org.caleydo.core.manager.event.data.ReplaceRecordVAEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.manager.event.view.tablebased.DimensionVAUpdateEvent;
import org.caleydo.core.manager.event.view.tablebased.RecordVAUpdateEvent;
import org.caleydo.core.manager.event.view.tablebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.DimensionVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IDimensionVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IRecordVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RecordVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceDimensionVAListener;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceRecordVAListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.view.info.listener.InfoAreaUpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

/**
 * Info area that is located in the side-bar. It shows the current view and the
 * current selection (in a tree).
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class InfoArea implements IDataDomainBasedView<ATableBasedDataDomain>,
		ISelectionUpdateHandler, IRecordVAUpdateHandler, IDimensionVAUpdateHandler,
		ISelectionCommandHandler, IViewCommandHandler {

	GeneralManager generalManager = null;
	EventPublisher eventPublisher = null;

	private Label lblViewInfoContent;

	private Tree selectionTree;

	private TreeItem contentTree;
	private TreeItem dimensionTree;

	private AGLView updateTriggeringView;
	private Composite parentComposite;

	protected SelectionUpdateListener selectionUpdateListener;
	protected RecordVAUpdateListener recordVAUpdateListener;
	protected DimensionVAUpdateListener dimensionVAUpdateListener;
	protected ReplaceRecordVAListener replaceRecordVAListener;
	protected ReplaceDimensionVAListener replaceDimensionVAListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;
	protected InfoAreaUpdateListener infoAreaUpdateListener;

	protected ATableBasedDataDomain dataDomain;

	RecordSelectionManager contentSelectionManager;
	DimensionSelectionManager dimensionSelectionManager;

	/**
	 * Constructor.
	 */
	public InfoArea() {

		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
	}

	public Control createControl(final Composite parent) {

		contentSelectionManager = dataDomain.getRecordSelectionManager();
		dimensionSelectionManager = dataDomain.getDimensionSelectionManager();

		parentComposite = parent;

		selectionTree = new Tree(parent, SWT.NULL);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 150;
		selectionTree.setLayoutData(gridData);

		contentTree = new TreeItem(selectionTree, SWT.NONE);
		contentTree.setExpanded(true);
		contentTree.setData(-1);

		contentTree.setText(dataDomain.getRecordName(true, true));

		dimensionTree = new TreeItem(selectionTree, SWT.NONE);
		dimensionTree.setExpanded(true);
		dimensionTree.setData(-1);
		dimensionTree.setText("Experiments");

		// pathwayTree = new TreeItem(selectionTree, SWT.NONE);
		// pathwayTree.setText("Pathways");
		// pathwayTree.setExpanded(false);
		// pathwayTree.setData(-1);

		lblViewInfoContent = new Label(parent, SWT.WRAP);
		lblViewInfoContent.setText("");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 100;
		lblViewInfoContent.setLayoutData(gridData);

		return parent;
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			final boolean scrollToSelection, final String info) {
		
		IDType recordIDType = dataDomain.getRecordIDType();
		if (selectionDelta.getIDType().getIDCategory().equals(recordIDType.getIDCategory())) {
			// Check for type that can be handled
			if (selectionDelta.getIDType() != recordIDType) {
				selectionDelta = DeltaConverter.convertDelta(recordIDType, selectionDelta);
			}
			
			contentSelectionManager.setDelta(selectionDelta);
			updateTree(true, contentSelectionManager, contentTree, info);
		}
		
		if (selectionDelta.getIDType() == contentSelectionManager.getIDType()) {

		} else if (selectionDelta.getIDType() == dimensionSelectionManager.getIDType()) {
			dimensionSelectionManager.setDelta(selectionDelta);
			updateTree(false, dimensionSelectionManager, dimensionTree, info);
		} else
			throw new IllegalStateException(
					"Mapping does not match, no selection manager can handle: "
							+ selectionDelta.getIDType());

	}

	private void updateTree(final boolean isContent,
			final SelectionManager selectionManager, final TreeItem tree,
			final String info) {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {

				if (info != null) {
					lblViewInfoContent.setText(info);
				}

				// Flush old items from this selection type
				for (TreeItem item : tree.getItems()) {
					item.dispose();
				}

				Set<Integer> mouseOverIDs = selectionManager
						.getElements(SelectionType.MOUSE_OVER);
				createItems(isContent, tree, SelectionType.MOUSE_OVER, mouseOverIDs);

				Set<Integer> selectedIDs = selectionManager
						.getElements(SelectionType.SELECTION);
				createItems(isContent, tree, SelectionType.SELECTION, selectedIDs);

			}
		});
	}

	private void createItems(boolean isContent, TreeItem tree,
			SelectionType selectionType, Set<Integer> ids) {
		Color color;
		int[] intColor = selectionType.getIntColor();

		color = new Color(parentComposite.getDisplay(), intColor[0], intColor[1],
				intColor[2]);

		for (Integer id : ids) {
			String name;
			if (isContent)
				name = dataDomain.getRecordLabel(id);
			else
				name = dataDomain.getDimensionLabel(id);

			TreeItem item = new TreeItem(tree, SWT.NONE);

			item.setText(name);
			item.setBackground(color);
			item.setData(id);
			item.setData("selection_type", selectionType);
		}

		tree.setExpanded(true);
	}

	@Override
	public void handleSelectionCommand(IDCategory category,
			final SelectionCommand selectionCommand) {

		if (parentComposite.isDisposed())
			return;

		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				ESelectionCommandType cmdType;

				cmdType = selectionCommand.getSelectionCommandType();
				if (cmdType == ESelectionCommandType.RESET
						|| cmdType == ESelectionCommandType.CLEAR_ALL) {
					selectionTree.removeAll();
				} else if (cmdType == ESelectionCommandType.CLEAR) {
					// Flush old items that become
					// deselected/normal
					for (TreeItem tmpItem : selectionTree.getItems()) {
						if (tmpItem.getData("selection_type") == selectionCommand
								.getSelectionType()) {
							tmpItem.dispose();
						}
					}

				}
			}
		});
	}

	protected AGLView getUpdateTriggeringView() {
		return updateTriggeringView;
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
		contentTree.removeAll();
		dimensionTree.removeAll();
		contentSelectionManager.clearSelections();
		dimensionSelectionManager.clearSelections();
	}

	/**
	 * handling method for updates about the info text displayed in the this
	 * info-area
	 * 
	 * @param info
	 *            short-info of the sender to display
	 */
	public void handleInfoAreaUpdate(final String info) {
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				lblViewInfoContent.setText(info);
			}
		});
	}

	/**
	 * Registers the listeners for this view to the event system. To release the
	 * allocated resources unregisterEventListeners() has to be called.
	 */
	@Override
	public void registerEventListeners() {
		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		selectionUpdateListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		recordVAUpdateListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);

		replaceRecordVAListener = new ReplaceRecordVAListener();
		replaceRecordVAListener.setHandler(this);
		replaceRecordVAListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReplaceRecordVAEvent.class, replaceRecordVAListener);

		dimensionVAUpdateListener = new DimensionVAUpdateListener();
		dimensionVAUpdateListener.setHandler(this);
		dimensionVAUpdateListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(DimensionVAUpdateEvent.class, dimensionVAUpdateListener);

		replaceDimensionVAListener = new ReplaceDimensionVAListener();
		replaceDimensionVAListener.setHandler(this);
		replaceDimensionVAListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReplaceDimensionVAEvent.class, replaceDimensionVAListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		infoAreaUpdateListener = new InfoAreaUpdateListener();
		infoAreaUpdateListener.setHandler(this);
		eventPublisher.addListener(InfoAreaUpdateEvent.class, infoAreaUpdateListener);
	}

	/**
	 * Unregisters the listeners for this view from the event system. To release
	 * the allocated resources unregisterEventListenrs() has to be called.
	 */
	@Override
	public void unregisterEventListeners() {
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (recordVAUpdateListener != null) {
			eventPublisher.removeListener(recordVAUpdateListener);
			recordVAUpdateListener = null;
		}
		if (replaceRecordVAListener != null) {
			eventPublisher.removeListener(replaceRecordVAListener);
			replaceRecordVAListener = null;
		}
		if (dimensionVAUpdateListener != null) {
			eventPublisher.removeListener(dimensionVAUpdateListener);
			dimensionVAUpdateListener = null;
		}
		if (replaceDimensionVAListener != null) {
			eventPublisher.removeListener(replaceDimensionVAListener);
			replaceDimensionVAListener = null;
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
		if (infoAreaUpdateListener != null) {
			eventPublisher.removeListener(infoAreaUpdateListener);
			infoAreaUpdateListener = null;
		}
	}

	public void dispose() {
		unregisterEventListeners();
	}

	@Override
	public synchronized void queueEvent(
			final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);

			}
		});
	}

	@Override
	public void handleVAUpdate(RecordVADelta vaDelta, final String info) {
		if (vaDelta.getIDType() != dataDomain.getRecordIDType())
			return;
		if (parentComposite.isDisposed())
			return;

		contentSelectionManager.setVADelta(vaDelta);
		updateTree(true, contentSelectionManager, contentTree, info);
	}

	@Override
	public void replaceRecordVA(int tableID, String dataDomain, String vaType) {
		contentSelectionManager.setVA(this.dataDomain.getRecordVA(vaType));
		updateTree(true, contentSelectionManager, contentTree, "");
	}

	@Override
	public void handleVAUpdate(DimensionVADelta vaDelta, String info) {
		if (vaDelta.getIDType() != dataDomain.getDimensionIDType())
			return;
		if (parentComposite.isDisposed())
			return;
		dimensionSelectionManager.setVADelta(vaDelta);
		updateTree(false, dimensionSelectionManager, dimensionTree, info);
	}

	@Override
	public void replaceDimensionVA(String dataDomain, String vaType) {
		if (parentComposite.isDisposed())
			return;
		dimensionSelectionManager.setVA(this.dataDomain.getDimensionVA(vaType));
		updateTree(false, dimensionSelectionManager, dimensionTree, "");
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

}
