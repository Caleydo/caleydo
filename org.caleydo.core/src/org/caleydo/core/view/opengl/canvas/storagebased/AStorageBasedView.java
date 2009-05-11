package org.caleydo.core.view.opengl.canvas.storagebased;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import javax.management.InvalidAttributeValueException;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.manager.event.view.TriggerSelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticIDMappingHelper;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ITriggerSelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.IVirtualArrayUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.TriggerSelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.VirtualArrayUpdateListener;
import org.eclipse.core.runtime.Status;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Base class for OpenGL views that heavily use storages.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public abstract class AStorageBasedView
	extends AGLEventListener
	implements ISelectionUpdateHandler, IVirtualArrayUpdateHandler, ITriggerSelectionCommandHandler,
	IViewCommandHandler {

	/**
	 * map selection type to unique id for virtual array
	 */
	protected EnumMap<EStorageBasedVAType, Integer> mapVAIDs;

	protected ArrayList<Boolean> alUseInRandomSampling;

	protected ConnectedElementRepresentationManager connectedElementRepresentationManager;

	/**
	 * This manager is responsible for the content in the storages (the indices)
	 */
	protected GenericSelectionManager contentSelectionManager;

	/**
	 * This manager is responsible for the management of the storages in the set
	 */
	protected GenericSelectionManager storageSelectionManager;

	/**
	 * flag whether one array should be a polyline or an axis
	 */
	protected boolean bRenderStorageHorizontally = false;

	/**
	 * flag whether the whole data or the selection should be rendered
	 */
	protected boolean bRenderOnlyContext;

	protected TextRenderer textRenderer;

	/**
	 * Define what level of filtering on the data should be applied
	 */
	protected EDataFilterLevel dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;

	protected boolean bUseRandomSampling = true;

	// clustering stuff
	protected boolean bUseClusteredVA = false;

	protected int iNumberOfRandomElements = 100;

	protected int iNumberOfSamplesPerTexture = 100;

	protected int iNumberOfSamplesPerHeatmap = 100;

	protected SelectionUpdateListener selectionUpdateListener = null;
	protected VirtualArrayUpdateListener virtualArrayUpdateListener = null;
	protected TriggerSelectionCommandListener triggerSelectionCommandListener = null;
	protected RedrawViewListener redrawViewListener = null;
	protected ClearSelectionsListener clearSelectionsListener = null;

	/**
	 * Constructor for storage based views
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	protected AStorageBasedView(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);

		mapVAIDs = new EnumMap<EStorageBasedVAType, Integer>(EStorageBasedVAType.class);

		connectedElementRepresentationManager =
			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager();

		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), false);
		registerEventListeners();
	}

	/**
	 * Toggle whether to render the complete dataset (with regards to the filters though) or only contextual
	 * data This effectively means switching between the {@link EStorageBasedVAType#COMPLETE_SELECTION} and
	 * {@link EStorageBasedVAType#EXTERNAL_SELECTION}
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

	public synchronized void initData() {

		super.initData();

		bRenderOnlyContext = bIsRenderedRemote;

		String sLevel =
			GeneralManager.get().getPreferenceStore().getString(PreferenceConstants.DATA_FILTER_LEVEL);
		if (sLevel.equals("complete")) {
			dataFilterLevel = EDataFilterLevel.COMPLETE;
		}
		else if (sLevel.equals("only_mapping")) {
			dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
		}
		else if (sLevel.equals("only_context")) {
			// Only apply only_context when pathways are loaded
			if (GeneralManager.get().getPathwayManager().size() > 100) {
				dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;
			}
			else {
				dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
			}
		}
		else
			throw new IllegalStateException("Unknown data filter level");

		if (!mapVAIDs.isEmpty()) {

			for (EStorageBasedVAType eSelectionType : EStorageBasedVAType.values()) {
				if (mapVAIDs.containsKey(eSelectionType)) {
					set.removeVirtualArray(mapVAIDs.get(eSelectionType));
				}
			}
			iContentVAID = -1;
			iStorageVAID = -1;
			mapVAIDs.clear();
		}

		if (set == null) {
			mapVAIDs.clear();
			contentSelectionManager.resetSelectionManager();
			storageSelectionManager.resetSelectionManager();
			connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
			return;
		}

		ArrayList<Integer> alTempList = new ArrayList<Integer>();
		// create VA with empty list
		int iVAID = set.createStorageVA(alTempList);
		mapVAIDs.put(EStorageBasedVAType.EXTERNAL_SELECTION, iVAID);

		alTempList = new ArrayList<Integer>();

		for (int iCount = 0; iCount < set.size(); iCount++) {
			alTempList.add(iCount);
		}

		iVAID = set.createSetVA(alTempList);
		mapVAIDs.put(EStorageBasedVAType.STORAGE_SELECTION, iVAID);

		initLists();
	}

	/**
	 * Initializes a virtual array with all elements, according to the data filters, as defined in
	 * {@link EDataFilterLevel}.
	 */
	protected final void initCompleteList() {
		// initialize virtual array that contains all (filtered) information
		ArrayList<Integer> alTempList = new ArrayList<Integer>(set.depth());

		for (int iCount = 0; iCount < set.depth(); iCount++) {
			if (dataFilterLevel != EDataFilterLevel.COMPLETE
				&& set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {

				// Here we get mapping data for all values
				int iDavidID = GeneticIDMappingHelper.get().getDavidIDFromStorageIndex(iCount);

				if (iDavidID == -1) {
//					generalManager.getLogger().log(new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
//						"Cannot resolve gene to DAVID ID!"));
					continue;
				}

				if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
					// Here all values are contained within pathways as well
					int iGraphItemID =
						generalManager.getPathwayItemManager().getPathwayVertexGraphItemIdByDavidId(iDavidID);

					if (iGraphItemID == -1) {
						continue;
					}

					PathwayVertexGraphItem tmpPathwayVertexGraphItem =
						(PathwayVertexGraphItem) generalManager.getPathwayItemManager().getItem(iGraphItemID);

					if (tmpPathwayVertexGraphItem == null) {
						continue;
					}
				}
			}

			alTempList.add(iCount);
		}

		if (bUseRandomSampling) {
			alUseInRandomSampling = new ArrayList<Boolean>();
			int iCount = 0;
			for (; iCount < iNumberOfRandomElements; iCount++) {
				alUseInRandomSampling.add(true);
			}
			for (; iCount < alTempList.size(); iCount++) {
				alUseInRandomSampling.add(false);
			}
			Collections.shuffle(alUseInRandomSampling);
			// if (alTempList.size() > iNumberOfRandomElements)
			// {
			// ArrayList<Integer> alNewList = new ArrayList<Integer>();
			// alNewList.addAll(alTempList.subList(0, iNumberOfRandomElements));
			// alTempList = alNewList;
			// }

		}

		// TODO: remove possible old virtual array
		int iVAID = set.createStorageVA(alTempList);
		mapVAIDs.put(EStorageBasedVAType.COMPLETE_SELECTION, iVAID);

		setDisplayListDirty();
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
	protected abstract ArrayList<SelectedElementRep> createElementRep(EIDType idType, int iStorageIndex)
		throws InvalidAttributeValueException;

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		// generalManager.getLogger().log(
		// Level.INFO,
		// "Update called by " + eventTrigger.getClass().getSimpleName()
		// + ", received in: " + this.getClass().getSimpleName());

		// Check for type that can be handled
		if (selectionDelta.getIDType() == EIDType.REFSEQ_MRNA_INT
			|| selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX) {
			contentSelectionManager.setDelta(selectionDelta);
			ISelectionDelta internalDelta = contentSelectionManager.getCompleteDelta();
			initForAddedElements();
			handleConnectedElementRep(internalDelta);
			reactOnExternalSelection(scrollToSelection);
			setDisplayListDirty();
		}

		else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX) {
			// generalManager.getIDMappingManager().getID(EMappingType.EXPERIMENT_2_EXPERIMENT_INDEX,
			// key)(type)

			storageSelectionManager.setDelta(selectionDelta);
			handleConnectedElementRep(storageSelectionManager.getCompleteDelta());
			reactOnExternalSelection(scrollToSelection);
			setDisplayListDirty();
		}

	}

	@Override
	public void handleVirtualArrayUpdate(IVirtualArrayDelta delta, String info) {
		// generalManager.getLogger().log(
		// Level.INFO,
		// "VA Update called by " + eventTrigger.getClass().getSimpleName()
		// + ", received in: " + this.getClass().getSimpleName());

		GenericSelectionManager selectionManager;
		if (delta.getIDType() == EIDType.EXPERIMENT_INDEX) {
			selectionManager = storageSelectionManager;
		}
		else if (delta.getIDType() == EIDType.REFSEQ_MRNA_INT) {
			delta = DeltaConverter.convertDelta(EIDType.EXPRESSION_INDEX, delta);
			selectionManager = contentSelectionManager;
		}
		else if (delta.getIDType() == EIDType.EXPRESSION_INDEX) {
			selectionManager = contentSelectionManager;
		}
		else
			return;

		reactOnVAChanges(delta);
		selectionManager.setVADelta(delta);

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
	protected void reactOnVAChanges(IVirtualArrayDelta delta) {

	}

	/**
	 * This method is called when new elements are added from external - if you need to react to it do it
	 * here, if not don't do anything.
	 */
	protected void initForAddedElements() {
	}

	@Override
	public synchronized void clearAllSelections() {
		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		setDisplayListDirty();
	}

	@Override
	public synchronized void setSet(ISet set) {
		super.setSet(set);

		resetView();
	}

	// @Override
	// public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer) {
	// generalManager.getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);
	// }

	// @Override
	// public void handleExternalEvent(IMediatorSender eventTrigger, IEventContainer eventContainer,
	// EMediatorType eMediatorType) {
	// switch (eventContainer.getEventType()) {
	// case VIEW_COMMAND:
	// ViewCommandEventContainer viewCommandEventContainer =
	// (ViewCommandEventContainer) eventContainer;
	//
	// if (viewCommandEventContainer.getViewCommand() == EViewCommand.REDRAW) {
	// setDisplayListDirty();
	// }
	// else if (viewCommandEventContainer.getViewCommand() == EViewCommand.CLEAR_SELECTIONS) {
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
	public void handleContentTriggerSelectionCommand(EIDType type, List<SelectionCommand> selectionCommands) {
		contentSelectionManager.executeSelectionCommands(selectionCommands);
		setDisplayListDirty();
	}

	@Override
	public void handleStorageTriggerSelectionCommand(EIDType type, List<SelectionCommand> selectionCommands) {
		storageSelectionManager.executeSelectionCommands(selectionCommands);
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
					// ESelectionType.MOUSE_OVER
					// || item.getSelectionType() == ESelectionType.SELECTION))
					if (!(item.getSelectionType() == ESelectionType.MOUSE_OVER)) {
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

						for (Integer iConnectionID : item.getConnectionID()) {
							connectedElementRepresentationManager.addSelection(iConnectionID, rep);
						}
					}
				}
			}
		}
		catch (InvalidAttributeValueException e) {
			generalManager.getLogger().log(new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
				"Can not handle data type of update in selectionDelta", e));
		}
	}

	/**
	 * Broadcast all elements independent of their type.
	 */
	public abstract void broadcastElements();

	@Override
	public synchronized void broadcastElements(EVAOperation type) {
		// nothing to do
	}

	/**
	 * Set whether to use random sampling or not, synchronized
	 * 
	 * @param bUseRandomSampling
	 */
	public synchronized final void useRandomSampling(boolean bUseRandomSampling) {
		if (this.bUseRandomSampling != bUseRandomSampling) {
			this.bUseRandomSampling = bUseRandomSampling;
		}
		// TODO, probably do this with initCompleteList, take care of selection
		// manager though
		initData();
		initCompleteList();
	}

	/**
	 * Set the number of samples which are shown in the view. The distribution is purely random
	 * 
	 * @param iNumberOfRandomElements
	 *            the number
	 */
	public synchronized final void setNumberOfSamplesToShow(int iNumberOfRandomElements) {
		if (iNumberOfRandomElements != this.iNumberOfRandomElements && bUseRandomSampling) {
			this.iNumberOfRandomElements = iNumberOfRandomElements;
			initData();
			return;
		}
		// TODO, probably do this with initCompleteList, take care of selection
		// manager though
		this.iNumberOfRandomElements = iNumberOfRandomElements;
	}

	/**
	 * Set the number of samples which are shown in one texture
	 * 
	 * @param iNumberOfSamplesPerTexture
	 *            the number
	 */
	public synchronized final void setNumberOfSamplesPerTexture(int iNumberOfSamplesPerTexture) {
		this.iNumberOfSamplesPerTexture = iNumberOfSamplesPerTexture;
	}

	/**
	 * Set the number of samples which are shown in one heat map
	 * 
	 * @param iNumberOfSamplesPerHeatmap
	 *            the number
	 */
	public synchronized final void setNumberOfSamplesPerHeatmap(int iNumberOfSamplesPerHeatmap) {
		this.iNumberOfSamplesPerHeatmap = iNumberOfSamplesPerHeatmap;
	}

	// public abstract void resetSelections();

	public abstract void changeOrientation(boolean bDefaultOrientation);

	public abstract boolean isInDefaultOrientation();

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType) {
		return contentSelectionManager.getElements(eSelectionType).size();
	}

	@Override
	public void registerEventListeners() {
		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		virtualArrayUpdateListener = new VirtualArrayUpdateListener();
		virtualArrayUpdateListener.setHandler(this);
		eventPublisher.addListener(VirtualArrayUpdateEvent.class, virtualArrayUpdateListener);

		triggerSelectionCommandListener = new TriggerSelectionCommandListener();
		triggerSelectionCommandListener.setHandler(this);
		eventPublisher.addListener(TriggerSelectionCommandEvent.class, triggerSelectionCommandListener);

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (virtualArrayUpdateListener != null) {
			eventPublisher.removeListener(virtualArrayUpdateListener);
			virtualArrayUpdateListener = null;
		}
		if (triggerSelectionCommandListener != null) {
			eventPublisher.removeListener(triggerSelectionCommandListener);
			triggerSelectionCommandListener = null;
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

}
