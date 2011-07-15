package org.caleydo.core.view.opengl.canvas;

import java.util.ArrayList;

import javax.management.InvalidAttributeValueException;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.DataDomainsChangedEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.StorageVAUpdateEvent;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Base class for OpenGL2 views that heavily use storages.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public abstract class AStorageBasedView
	extends AGLView
	implements IDataDomainSetBasedView, ISelectionUpdateHandler, IContentVAUpdateHandler,
	IStorageVAUpdateHandler, ISelectionCommandHandler, IViewCommandHandler {

	protected ISet set;

	protected ASetBasedDataDomain dataDomain;

	// protected ArrayList<Boolean> alUseInRandomSampling;

	protected ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * This manager is responsible for the content in the storages (the indices). The contentSelectionManager
	 * is initialized when the useCase is set ({@link #setDataDomain(IDataDomain)}).
	 */
	protected ContentSelectionManager contentSelectionManager;

	/**
	 * This manager is responsible for the management of the storages in the set. The storageSelectionManager
	 * is initialized when the useCase is set ( {@link #setDataDomain(IDataDomain)}).
	 */
	protected StorageSelectionManager storageSelectionManager;

	/**
	 * flag whether the whole data or the selection should be rendered
	 */
	protected boolean bRenderOnlyContext;

	/**
	 * Define what level of filtering on the data should be applied
	 */
	protected EDataFilterLevel dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;

	protected boolean bUseRandomSampling = true;

	protected int iNumberOfRandomElements = 100;

	protected int iNumberOfSamplesPerTexture = 100;

	protected int iNumberOfSamplesPerHeatmap = 100;

	protected SelectionUpdateListener selectionUpdateListener;
	protected SelectionCommandListener selectionCommandListener;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;

	protected ContentVAUpdateListener contentVAUpdateListener;
	protected StorageVAUpdateListener storageVAUpdateListener;
	protected ReplaceContentVAListener replaceContentVAListener;
	protected ReplaceStorageVAListener replaceStorageVAListener;

	protected IDType contentIDType;
	protected IDType storageIDType;

	/**
	 * Constructor for storage based views
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	protected AStorageBasedView(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		connectedElementRepresentationManager =
			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager();
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = (ASetBasedDataDomain) dataDomain;

		contentSelectionManager = this.dataDomain.getContentSelectionManager();
		storageSelectionManager = this.dataDomain.getStorageSelectionManager();

		contentIDType = dataDomain.getContentIDType();
		storageIDType = dataDomain.getStorageIDType();

		initData();
		
		DataDomainsChangedEvent event = new DataDomainsChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * Toggle whether to render the complete dataset (with regards to the filters though) or only contextual
	 * data This effectively means switching between the {@link VAType#CONTENT} and
	 * {@link VAType#CONTENT_CONTEXT}
	 */
	public abstract void renderContext(boolean bRenderContext);

	/**
	 * Check whether only context is beeing rendered
	 * 
	 * @return
	 */
	public boolean isRenderingOnlyContext() {
		return bRenderOnlyContext;
	}

	@Override
	public void initData() {
		if (set == null)
			set = dataDomain.getSet();

		super.initData();

		bRenderOnlyContext =
			(glRemoteRenderingView != null && glRemoteRenderingView.getViewType().equals(
				"org.caleydo.view.bucket"));

		initLists();
	}

	/**
	 * View specific data initialization
	 */
	protected abstract void initLists();

	/**
	 * Create 0:n {@link SelectedElementRep} for the selectionDelta
	 * 
	 * @param iDType
	 *            TODO
	 * @param selectionDelta
	 *            the selection delta which should be represented
	 * @throws InvalidAttributeValueException
	 *             when the selectionDelta does not contain a valid type for this view
	 */
	protected abstract ArrayList<SelectedElementRep> createElementRep(IDType idType, int id)
		throws InvalidAttributeValueException;

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {

		if (selectionDelta.getIDType().getIDCategory().equals(contentIDType.getIDCategory())) {
			// Check for type that can be handled
			if (selectionDelta.getIDType() != contentIDType) {
				selectionDelta = DeltaConverter.convertDelta(contentIDType, selectionDelta);
			}

			contentSelectionManager.setDelta(selectionDelta);
			// ISelectionDelta internalDelta = contentSelectionManager.getCompleteDelta();
			initForAddedElements();
			handleConnectedElementReps(selectionDelta);
			reactOnExternalSelection(selectionDelta, scrollToSelection);
			setDisplayListDirty();
		}
		else if (selectionDelta.getIDType() == storageIDType) {

			storageSelectionManager.setDelta(selectionDelta);
			handleConnectedElementReps(selectionDelta);
			reactOnExternalSelection(selectionDelta, scrollToSelection);
			setDisplayListDirty();
		}

		// else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX
		// && dataDomain.getDataDomainType().equals("org.caleydo.datadomain.clinical")) {
		//
		// contentSelectionManager.setDelta(selectionDelta);
		// //
		// handleConnectedElementRep(selectionDelta);
		// reactOnExternalSelection(scrollToSelection);
		// setDisplayListDirty();
		// }
		//
		// // FIXME: this is not nice since we use expression index for unspecified
		// // data
		// else if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX
		// && dataDomain.getDataDomainType().equals("org.caleydo.datadomain.generic")) {
		//
		// contentSelectionManager.setDelta(selectionDelta);
		// // handleConnectedElementRep(contentSelectionManager.getCompleteDelta());
		// handleConnectedElementRep(selectionDelta);
		// reactOnExternalSelection(scrollToSelection);
		// setDisplayListDirty();
		// }
		//
		// else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX
		// && dataDomain.getDataDomainType().equals("org.caleydo.datadomain.generic")) {
		//
		// storageSelectionManager.setDelta(selectionDelta);
		// // handleConnectedElementRep(contentSelectionManager.getCompleteDelta());
		// handleConnectedElementRep(selectionDelta);
		// reactOnExternalSelection(scrollToSelection);
		// setDisplayListDirty();
		// }

	}

	@Override
	public void handleVAUpdate(ContentVADelta delta, String info) {
		if (!delta.getVAType().equals(contentVAType))
			return;

		contentVA.setGroupList(null);
		contentSelectionManager.setVADelta(delta);

		reactOnContentVAChanges(delta);

		// reactOnExternalSelection();
		setDisplayListDirty();
	}

	@Override
	public void handleVAUpdate(StorageVADelta delta, String info) {
		storageVA.setGroupList(null);
		// reactOnStorageVAChanges(delta);
		storageSelectionManager.setVADelta(delta);
		setDisplayListDirty();
	}

	/**
	 * Is called any time a update is triggered externally. Should be implemented by inheriting views.
	 */
	protected void reactOnExternalSelection(ISelectionDelta selectionDelta, boolean scrollToSelection) {

	}

	/**
	 * Is called any time a virtual array is changed. Can be implemented by inheriting views if some action is
	 * necessary
	 * 
	 * @param delta
	 */
	protected void reactOnContentVAChanges(ContentVADelta delta) {

	}

	/**
	 * This method is called when new elements are added from external - if you need to react to it do it
	 * here, if not don't do anything.
	 */
	protected void initForAddedElements() {
	}

	@Override
	public void clearAllSelections() {
		connectedElementRepresentationManager.clear(contentIDType);
		connectedElementRepresentationManager.clear(storageIDType);
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		setDisplayListDirty();
	}

	// @Override
	// public void triggerEvent(EMediatorType eMediatorType, IEventContainer
	// eventContainer) {
	// generalManager.getEventPublisher().triggerEvent(eMediatorType, this,
	// eventContainer);
	// }

	// @Override
	// public void handleExternalEvent(IMediatorSender eventTrigger,
	// IEventContainer eventContainer,
	// EMediatorType eMediatorType) {
	// switch (eventContainer.getEventType()) {
	// case VIEW_COMMAND:
	// ViewCommandEventContainer viewCommandEventContainer =
	// (ViewCommandEventContainer) eventContainer;
	//
	// if (viewCommandEventContainer.getViewCommand() == EViewCommand.REDRAW) {
	// setDisplayListDirty();
	// }
	// else if (viewCommandEventContainer.getViewCommand() ==
	// EViewCommand.CLEAR_SELECTIONS) {
	// clearAllSelections();
	// setDisplayListDirty();
	// }
	// break;
	// }
	// }

	@Override
	public void handleRedrawView() {
		setDisplayListDirty();
	}

	@Override
	public void handleClearSelections() {
		clearAllSelections();
		setDisplayListDirty();
	}

	@Override
	public void handleSelectionCommand(IDCategory idCategory, SelectionCommand selectionCommand) {
		if (idCategory == dataDomain.getContentIDCategory())
			contentSelectionManager.executeSelectionCommand(selectionCommand);
		else if (idCategory == dataDomain.getStorageIDCategory())
			storageSelectionManager.executeSelectionCommand(selectionCommand);
		else
			return;
		setDisplayListDirty();
	}

	/**
	 * Handles the creation of {@link SelectedElementRep} according to the data in a selectionDelta
	 * 
	 * @param selectionDelta
	 *            the selection data that should be handled
	 */
	protected void handleConnectedElementReps(ISelectionDelta selectionDelta) {
		try {
			int id = -1;

			int iID = -1;
			IDType idType;

			if (selectionDelta.size() > 0) {
				for (SelectionDeltaItem item : selectionDelta) {
					if (!connectedElementRepresentationManager.isSelectionTypeRenderedWithVisuaLinks(item
						.getSelectionType()) || item.isRemove())
						continue;
					if (selectionDelta.getIDType() == contentIDType) {
						id = item.getPrimaryID();

						iID = item.getSecondaryID();
						idType = contentIDType;

					}
					else if (selectionDelta.getIDType() == storageIDType) {
						iID = item.getPrimaryID();
						id = iID;
						idType = storageIDType;
					}
					else
						throw new InvalidAttributeValueException("Can not handle data type: "
							+ selectionDelta.getIDType());

					if (id == -1)
						throw new IllegalArgumentException("No internal ID in selection delta");

					ArrayList<SelectedElementRep> alRep = createElementRep(idType, id);
					if (alRep == null) {
						continue;
					}
					for (SelectedElementRep rep : alRep) {
						if (rep == null) {
							continue;
						}

						for (Integer iConnectionID : item.getConnectionIDs()) {
							connectedElementRepresentationManager.addSelection(iConnectionID, rep,
								item.getSelectionType());
						}
					}
				}
			}
		}
		catch (InvalidAttributeValueException e) {
			Logger.log(new Status(IStatus.WARNING, this.toString(),
				"Can not handle data type of update in selectionDelta", e));
		}
	}

	/**
	 * Broadcast all elements independent of their type.
	 */
	// public abstract void broadcastElements();
	@Override
	public void broadcastElements(EVAOperation type) {
		// nothing to do
	}

	/**
	 * Set whether to use random sampling or not, synchronized
	 * 
	 * @param bUseRandomSampling
	 */
	public final void useRandomSampling(boolean bUseRandomSampling) {
		if (this.bUseRandomSampling != bUseRandomSampling) {
			this.bUseRandomSampling = bUseRandomSampling;
		}
		// TODO, probably do this with initCompleteList, take care of selection
		// manager though
		initData();
	}

	/**
	 * Set the number of samples which are shown in the view. The distribution is purely random
	 * 
	 * @param iNumberOfRandomElements
	 *            the number
	 */
	public final void setNumberOfSamplesToShow(int iNumberOfRandomElements) {
		if (iNumberOfRandomElements != this.iNumberOfRandomElements && bUseRandomSampling) {
			this.iNumberOfRandomElements = iNumberOfRandomElements;
			initData();
			return;
		}
		// TODO, probably do this with initCompleteList, take care of selection
		// manager though
		this.iNumberOfRandomElements = iNumberOfRandomElements;
	}

	// public abstract void resetSelections();

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		return contentSelectionManager.getElements(SelectionType).size();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		selectionUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
		contentVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(ContentVAUpdateEvent.class, contentVAUpdateListener);

		replaceContentVAListener = new ReplaceContentVAListener();
		replaceContentVAListener.setHandler(this);
		replaceContentVAListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReplaceContentVAEvent.class, replaceContentVAListener);

		storageVAUpdateListener = new StorageVAUpdateListener();
		storageVAUpdateListener.setHandler(this);
		storageVAUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(StorageVAUpdateEvent.class, storageVAUpdateListener);

		replaceStorageVAListener = new ReplaceStorageVAListener();
		replaceStorageVAListener.setHandler(this);
		replaceStorageVAListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(ReplaceStorageVAEvent.class, replaceStorageVAListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setExclusiveDataDomainType(dataDomain.getDataDomainID());
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		redrawViewListener.setDataDomainType(dataDomain.getDataDomainID());
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

		if (contentVAUpdateListener != null) {
			eventPublisher.removeListener(contentVAUpdateListener);
			contentVAUpdateListener = null;
		}

		if (storageVAUpdateListener != null) {
			eventPublisher.removeListener(storageVAUpdateListener);
			storageVAUpdateListener = null;
		}
		if (replaceContentVAListener != null) {
			eventPublisher.removeListener(replaceContentVAListener);
			replaceContentVAListener = null;
		}

		if (replaceStorageVAListener != null) {
			eventPublisher.removeListener(replaceStorageVAListener);
			replaceStorageVAListener = null;
		}

	}

	@Override
	public void replaceContentVA(int setID, String dataDomainType, String vaType) {

		if (set.getID() != setID || this.contentVAType != vaType)
			return;

		contentVA = set.getContentData(vaType).getContentVA();
		contentSelectionManager.setVA(contentVA);

		initData();
	}

	@Override
	public void replaceStorageVA(String dataDomain, String vaType) {
		if (vaType != storageVAType)
			return;

		storageVA = set.getStorageData(vaType).getStorageVA();

		initData();
	}

	/**
	 * Manually set the vaType if you want to override the automatic setting triggeret in
	 * {@link #init(javax.media.opengl.GL)}
	 * 
	 * @param vaType
	 */
	public void setContentVAType(String vaType) {
		this.contentVAType = vaType;
	}

	/**
	 * Manually set the vaType if you want to override the automatic setting triggeret in
	 * {@link #init(javax.media.opengl.GL)}
	 * 
	 * @param vaType
	 */
	public void setStorageVAType(String vaType) {
		this.storageVAType = vaType;
	}
	
	@Override
	public boolean isDataView() {
		return true;
	}
}
