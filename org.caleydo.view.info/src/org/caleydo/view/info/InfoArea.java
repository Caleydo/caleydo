package org.caleydo.view.info;

import java.util.Set;

import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.StorageVAUpdateEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IStorageVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceContentVAListener;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceStorageVAListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.StorageVAUpdateListener;
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
public class InfoArea implements IDataDomainBasedView<ASetBasedDataDomain>,
		ISelectionUpdateHandler, IContentVAUpdateHandler, IStorageVAUpdateHandler,
		ISelectionCommandHandler, IViewCommandHandler {

	GeneralManager generalManager = null;
	EventPublisher eventPublisher = null;

	private Label lblViewInfoContent;

	private Tree selectionTree;

	private TreeItem contentTree;
	private TreeItem storageTree;

	private AGLView updateTriggeringView;
	private Composite parentComposite;

	protected SelectionUpdateListener selectionUpdateListener;
	protected ContentVAUpdateListener contentVAUpdateListener;
	protected StorageVAUpdateListener storageVAUpdateListener;
	protected ReplaceContentVAListener replaceContentVAListener;
	protected ReplaceStorageVAListener replaceStorageVAListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;
	protected InfoAreaUpdateListener infoAreaUpdateListener;

	protected ASetBasedDataDomain dataDomain;

	ContentSelectionManager contentSelectionManager;
	StorageSelectionManager storageSelectionManager;

	/**
	 * Constructor.
	 */
	public InfoArea() {

		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();

	}

	public Control createControl(final Composite parent) {

		contentSelectionManager = dataDomain.getContentSelectionManager();
		storageSelectionManager = dataDomain.getStorageSelectionManager();

		parentComposite = parent;

		selectionTree = new Tree(parent, SWT.NULL);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 150;
		selectionTree.setLayoutData(gridData);

		contentTree = new TreeItem(selectionTree, SWT.NONE);
		contentTree.setExpanded(true);
		contentTree.setData(-1);

		contentTree.setText(dataDomain.getContentName(true, true));

		storageTree = new TreeItem(selectionTree, SWT.NONE);
		storageTree.setExpanded(true);
		storageTree.setData(-1);
		storageTree.setText("Experiments");

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
	public void handleSelectionUpdate(final ISelectionDelta selectionDelta,
			final boolean scrollToSelection, final String info) {
		if (selectionDelta.getIDType() == contentSelectionManager.getIDType()) {
			contentSelectionManager.setDelta(selectionDelta);
			updateTree(true, contentSelectionManager, contentTree, info);
		} else if (selectionDelta.getIDType() == storageSelectionManager.getIDType()) {
			storageSelectionManager.setDelta(selectionDelta);
			updateTree(false, storageSelectionManager, storageTree, info);
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
				name = dataDomain.getContentLabel(id);
			else
				name = dataDomain.getStorageLabel(id);

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
		storageTree.removeAll();
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();
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
		selectionUpdateListener.setDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
		contentVAUpdateListener.setDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(ContentVAUpdateEvent.class, contentVAUpdateListener);

		replaceContentVAListener = new ReplaceContentVAListener();
		replaceContentVAListener.setHandler(this);
		replaceContentVAListener.setDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(ReplaceContentVAEvent.class, replaceContentVAListener);

		storageVAUpdateListener = new StorageVAUpdateListener();
		storageVAUpdateListener.setHandler(this);
		storageVAUpdateListener.setDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(StorageVAUpdateEvent.class, storageVAUpdateListener);

		replaceStorageVAListener = new ReplaceStorageVAListener();
		replaceStorageVAListener.setHandler(this);
		replaceStorageVAListener.setDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(ReplaceStorageVAEvent.class, replaceStorageVAListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setDataDomainType(dataDomain.getDataDomainType());
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
		if (contentVAUpdateListener != null) {
			eventPublisher.removeListener(contentVAUpdateListener);
			contentVAUpdateListener = null;
		}
		if (replaceContentVAListener != null) {
			eventPublisher.removeListener(replaceContentVAListener);
			replaceContentVAListener = null;
		}
		if (storageVAUpdateListener != null) {
			eventPublisher.removeListener(storageVAUpdateListener);
			storageVAUpdateListener = null;
		}
		if (replaceStorageVAListener != null) {
			eventPublisher.removeListener(replaceStorageVAListener);
			replaceStorageVAListener = null;
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
	public void handleVAUpdate(ContentVADelta vaDelta, final String info) {
		if (vaDelta.getIDType() != dataDomain.getContentIDType())
			return;
		if (parentComposite.isDisposed())
			return;

		contentSelectionManager.setVADelta(vaDelta);
		updateTree(true, contentSelectionManager, contentTree, info);
	}

	@Override
	public void replaceContentVA(int setID, String dataDomain, String vaType) {
		contentSelectionManager.setVA(this.dataDomain.getContentVA(vaType));
		updateTree(true, contentSelectionManager, contentTree, "");
	}

	@Override
	public void handleVAUpdate(StorageVADelta vaDelta, String info) {
		if (vaDelta.getIDType() != dataDomain.getStorageIDType())
			return;
		if (parentComposite.isDisposed())
			return;
		storageSelectionManager.setVADelta(vaDelta);
		updateTree(false, storageSelectionManager, storageTree, info);
	}

	@Override
	public void replaceStorageVA(String dataDomain, String vaType) {
		if (parentComposite.isDisposed())
			return;
		storageSelectionManager.setVA(this.dataDomain.getStorageVA(vaType));
		updateTree(false, storageSelectionManager, storageTree, "");
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

}
