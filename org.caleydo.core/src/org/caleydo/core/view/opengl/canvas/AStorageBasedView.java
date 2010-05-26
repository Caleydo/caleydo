package org.caleydo.core.view.opengl.canvas;

import java.awt.Font;
import java.util.ArrayList;

import javax.management.InvalidAttributeValueException;

import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.delta.StorageVADelta;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.datadomain.EDataDomain;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.StorageVAUpdateEvent;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
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
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Base class for OpenGL views that heavily use storages.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public abstract class AStorageBasedView
	extends AGLView
	implements ISelectionUpdateHandler, IContentVAUpdateHandler, IStorageVAUpdateHandler,
	ISelectionCommandHandler, IViewCommandHandler {

	// protected ArrayList<Boolean> alUseInRandomSampling;

	protected ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * This manager is responsible for the content in the storages (the indices). The contentSelectionManager
	 * is initialized when the useCase is set ({@link #setUseCase(IDataDomain)}).
	 */
	protected ContentSelectionManager contentSelectionManager;

	/**
	 * This manager is responsible for the management of the storages in the set. The storageSelectionManager
	 * is initialized when the useCase is set ( {@link #setUseCase(IDataDomain)}).
	 */
	protected StorageSelectionManager storageSelectionManager;

	/**
	 * flag whether the whole data or the selection should be rendered
	 */
	protected boolean bRenderOnlyContext;

	protected CaleydoTextRenderer textRenderer;

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

	/**
	 * Constructor for storage based views
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	protected AStorageBasedView(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		connectedElementRepresentationManager =
			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager();

		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 24), false);
		// registerEventListeners();

	}

	@Override
	public void setUseCase(IDataDomain useCase) {
		this.useCase = useCase;
		this.dataDomain = useCase.getDataDomain();

		contentSelectionManager = useCase.getContentSelectionManager();
		storageSelectionManager = useCase.getStorageSelectionManager();

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

		super.initData();

		bRenderOnlyContext =
			(glRemoteRenderingView != null && glRemoteRenderingView.getViewType().equals(
				"org.caleydo.view.bucket"));

		// TODO: do we need this here?
		// if (set == null) {
		// contentSelectionManager.resetSelectionManager();
		// storageSelectionManager.resetSelectionManager();
		// connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
		// return;
		// }

		initLists();
	}

	/**
	 * Initializes a virtual array with all elements, according to the data filters, as defined in
	 * {@link EDataFilterLevel}.
	 */
	// protected final void initCompleteList() {
	// // initialize virtual array that contains all (filtered) information
	// ArrayList<Integer> alTempList = new ArrayList<Integer>(set.depth());
	//
	// for (int iCount = 0; iCount < set.depth(); iCount++) {
	// if (dataFilterLevel != EDataFilterLevel.COMPLETE
	// && set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
	//
	// // Here we get mapping data for all values
	// int iDavidID =
	// GeneticIDMappingHelper.get().getDavidIDFromStorageIndex(iCount);
	//
	// if (iDavidID == -1) {
	// // generalManager.getLogger().log(new Status(Status.WARNING,
	// GeneralManager.PLUGIN_ID,
	// // "Cannot resolve gene to DAVID ID!"));
	// continue;
	// }
	//
	// if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
	// // Here all values are contained within pathways as well
	// PathwayVertexGraphItem tmpPathwayVertexGraphItem =
	// generalManager.getPathwayItemManager().getPathwayVertexGraphItemByDavidId(iDavidID);
	//
	// if (tmpPathwayVertexGraphItem == null) {
	// continue;
	// }
	// }
	// }
	//
	// alTempList.add(iCount);
	// }
	//
	//
	// // TODO: remove possible old virtual array
	// int iVAID = set.createStorageVA(alTempList);
	// mapVAIDs.put(EStorageBasedVAType.COMPLETE_SELECTION, iVAID);
	//
	// setDisplayListDirty();
	// }
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
	protected abstract ArrayList<SelectedElementRep> createElementRep(EIDType idType, int iStorageIndex)
		throws InvalidAttributeValueException;

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		// generalManager.getLogger().log(
		// Level.INFO,
		// "Update called by " + eventTrigger.getClass().getSimpleName()
		// + ", received in: " + this.getClass().getSimpleName());

		// Check for type that can be handled
		if (selectionDelta.getIDType().getCategory() == EIDCategory.GENE
			&& dataDomain == EDataDomain.GENETIC_DATA) {
			contentSelectionManager.setDelta(selectionDelta);
			ISelectionDelta internalDelta = contentSelectionManager.getCompleteDelta();
			initForAddedElements();
			handleConnectedElementRep(internalDelta);
			reactOnExternalSelection(scrollToSelection);
			setDisplayListDirty();
		}

		else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX
			&& (dataDomain == EDataDomain.GENETIC_DATA)) {
			// generalManager.getIDMappingManager().getID(EMappingType.EXPERIMENT_2_EXPERIMENT_INDEX,
			// key)(type)

			storageSelectionManager.setDelta(selectionDelta);
			handleConnectedElementRep(storageSelectionManager.getCompleteDelta());
			reactOnExternalSelection(scrollToSelection);
			setDisplayListDirty();
		}

		else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX
			&& dataDomain == EDataDomain.CLINICAL_DATA) {

			contentSelectionManager.setDelta(selectionDelta);

			handleConnectedElementRep(contentSelectionManager.getCompleteDelta());
			reactOnExternalSelection(scrollToSelection);
			setDisplayListDirty();
		}

		// FIXME: this is not nice since we use expression index for unspecified
		// data
		else if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX
			&& dataDomain == EDataDomain.UNSPECIFIED) {

			contentSelectionManager.setDelta(selectionDelta);
			handleConnectedElementRep(contentSelectionManager.getCompleteDelta());
			reactOnExternalSelection(scrollToSelection);
			setDisplayListDirty();
		}

		else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX
			&& dataDomain == EDataDomain.UNSPECIFIED) {

			storageSelectionManager.setDelta(selectionDelta);
			handleConnectedElementRep(contentSelectionManager.getCompleteDelta());
			reactOnExternalSelection(scrollToSelection);
			setDisplayListDirty();
		}

	}

	@Override
	public void handleContentVAUpdate(ContentVADelta delta, String info) {
		// generalManager.getLogger().log(
		// Level.INFO,
		// "VA Update called by " + eventTrigger.getClass().getSimpleName()
		// + ", received in: " + this.getClass().getSimpleName());

		contentVA.setGroupList(null);

		if (delta.getIDType() == EIDType.REFSEQ_MRNA_INT)
			delta = DeltaConverter.convertDelta(EIDType.EXPRESSION_INDEX, delta);

		reactOnContentVAChanges(delta);
		contentSelectionManager.setVADelta(delta);

		// reactOnExternalSelection();
		setDisplayListDirty();
	}

	public void handleStorageVAUpdate(StorageVADelta delta, String info) {
		storageVA.setGroupList(null);
		reactOnStorageVAChanges(delta);
		storageSelectionManager.setVADelta(delta);

		// reactOnExternalSelection();
		setDisplayListDirty();
	}

	/**
	 * Is called any time a update is triggered externally. Should be implemented by inheriting views.
	 */
	protected void reactOnExternalSelection(boolean scrollToSelection) {

	}

	/**
	 * Is called any time a virtual array is changed. Can be implemented by inheriting views if some action is
	 * necessary
	 * 
	 * @param delta
	 */
	protected void reactOnContentVAChanges(ContentVADelta delta) {

	}

	protected void reactOnStorageVAChanges(StorageVADelta delta) {

	}

	/**
	 * This method is called when new elements are added from external - if you need to react to it do it
	 * here, if not don't do anything.
	 */
	protected void initForAddedElements() {
	}

	@Override
	public void clearAllSelections() {
		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		setDisplayListDirty();
	}

	// @Override
	// public void setSet(ISet set) {
	// super.setSet(set);
	//
	// // resetView();
	// }

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
	public void handleSelectionCommand(EIDCategory category, SelectionCommand selectionCommand) {
		if (category == EIDCategory.GENE)
			contentSelectionManager.executeSelectionCommand(selectionCommand);
		else
			storageSelectionManager.executeSelectionCommand(selectionCommand);
		setDisplayListDirty();
	}

	/**
	 * Handles the creation of {@link SelectedElementRep} according to the data in a selectionDelta
	 * 
	 * @param selectionDelta
	 *            the selection data that should be handled
	 */
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta) {
		try {
			int iStorageIndex = -1;

			int iID = -1;
			EIDType idType;

			if (selectionDelta.size() > 0) {
				for (SelectionDeltaItem item : selectionDelta) {
					// if (!(item.getSelectionType() ==
					// SelectionType.MOUSE_OVER
					// || item.getSelectionType() == SelectionType.SELECTION))
					if (!(item.getSelectionType() == SelectionType.MOUSE_OVER)) {
						continue;
					}

					if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX) {
						iStorageIndex = item.getPrimaryID();

						iID = item.getSecondaryID();
						idType = EIDType.EXPRESSION_INDEX;

					}
					else if (selectionDelta.getSecondaryIDType() == EIDType.EXPRESSION_INDEX) {
						iStorageIndex = item.getSecondaryID();

						iID = item.getPrimaryID();
						idType = EIDType.EXPRESSION_INDEX;
					}
					else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX) {
						iID = item.getPrimaryID();
						iStorageIndex = iID;
						idType = EIDType.EXPERIMENT_INDEX;
					}
					else if (selectionDelta.getIDType() == EIDType.UNSPECIFIED) {
						iStorageIndex = item.getPrimaryID();

						iID = item.getPrimaryID();
						idType = EIDType.UNSPECIFIED;
					}
					else
						throw new InvalidAttributeValueException("Can not handle data type: "
							+ selectionDelta.getIDType());

					if (iStorageIndex == -1)
						throw new IllegalArgumentException("No internal ID in selection delta");

					ArrayList<SelectedElementRep> alRep = createElementRep(idType, iStorageIndex);
					if (alRep == null) {
						continue;
					}
					for (SelectedElementRep rep : alRep) {
						if (rep == null) {
							continue;
						}

						for (Integer iConnectionID : item.getConnectionIDs()) {
							connectedElementRepresentationManager.addSelection(iConnectionID, rep);
						}
					}
				}
			}
		}
		catch (InvalidAttributeValueException e) {
			generalManager.getLogger().log(
				new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
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
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
		eventPublisher.addListener(ContentVAUpdateEvent.class, contentVAUpdateListener);

		storageVAUpdateListener = new StorageVAUpdateListener();
		storageVAUpdateListener.setHandler(this);
		eventPublisher.addListener(StorageVAUpdateEvent.class, storageVAUpdateListener);

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

		replaceStorageVAListener = new ReplaceStorageVAListener();
		replaceStorageVAListener.setHandler(this);
		eventPublisher.addListener(ReplaceContentVAEvent.class, replaceStorageVAListener);
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
	public void replaceContentVA(int setID, EIDCategory idCategory, ContentVAType vaType) {
		// String primaryVAType = useCase.getVATypeForIDCategory(idCategory);
		// if (primaryVAType == null)
		// return;

		// ContentVAType suggestedVAType = ContentVAType.getVATypeForPrimaryVAType(primaryVAType);

		if (this.contentVAType != vaType)
			return;

		contentVA = set.getContentVA(vaType);
		// contentSelectionManager.setVA(contentVA);

		initData();
	}

	@Override
	public void replaceStorageVA(EIDCategory idCategory, StorageVAType vaType) {
		if (vaType != storageVAType)
			return;

		storageVA = set.getStorageVA(vaType);

		initData();
	}

	/**
	 * Manually set the vaType if you want to override the automatic setting triggeret in
	 * {@link #init(javax.media.opengl.GL)}
	 * 
	 * @param vaType
	 */
	public void setContentVAType(ContentVAType vaType) {
		this.contentVAType = vaType;
	}

	/**
	 * Manually set the vaType if you want to override the automatic setting triggeret in
	 * {@link #init(javax.media.opengl.GL)}
	 * 
	 * @param vaType
	 */
	public void setStorageVAType(StorageVAType vaType) {
		this.storageVAType = vaType;
	}

}
