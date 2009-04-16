package org.caleydo.core.view.opengl.canvas.storagebased;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.logging.Level;

import javax.management.InvalidAttributeValueException;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.DeltaConverter;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;
import org.caleydo.core.data.selection.SelectionDeltaItem;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.EViewCommand;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.event.ViewCommandEventContainer;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.mapping.IDMappingHelper;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Base class for OpenGL views that heavily use storages.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public abstract class AStorageBasedView
	extends AGLEventListener
	implements IMediatorReceiver, IMediatorSender {

	protected ISet set;
	protected ESetType setType;

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

	/**
	 * Constructor for storage based views
	 * 
	 * @param setType
	 *            from the type of set the kind of visualization is derived
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	protected AStorageBasedView(ESetType setType, final int iGLCanvasID, final String sLabel,
		final IViewFrustum viewFrustum) {
		super(iGLCanvasID, sLabel, viewFrustum, true);

		this.setType = setType;

		mapVAIDs = new EnumMap<EStorageBasedVAType, Integer>(EStorageBasedVAType.class);

		connectedElementRepresentationManager =
			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager();

		// textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 16),
		// false);
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), false);

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

	// /**
	// * Set which level of data filtering should be applied.
	// *
	// * @param bRenderOnlyContext true if only context, else false
	// */
	// @Override
	// public void setDataFilterLevel(EDataFilterLevel dataFilterLevel)
	// {
	// this.dataFilterLevel = dataFilterLevel;
	// }

	public synchronized void initData() {
		set = null;

		for (ISet currentSet : alSets) {
			if (currentSet.getSetType() == setType) {
				set = currentSet;
			}
		}

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

			// This should be done once we get some thread safety, memory leak,
			// and a big one

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
			if (dataFilterLevel != EDataFilterLevel.COMPLETE) {
				// Here we get mapping data for all values
				// FIXME: not general, only for genes
				int iDavidID = IDMappingHelper.get().getDavidIDFromStorageIndex(iCount);

				if (iDavidID == -1) {
					generalManager.getLogger().log(Level.FINE, "Cannot resolve gene to DAVID ID!");
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

	private void handleSelectionUpdate(IMediatorSender eventTrigger, ISelectionDelta selectionDelta) {
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
			reactOnExternalSelection(eventTrigger.getClass().getSimpleName());
			setDisplayListDirty();
		}

		else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX) {
			// generalManager.getIDMappingManager().getID(EMappingType.EXPERIMENT_2_EXPERIMENT_INDEX,
			// key)(type)

			storageSelectionManager.setDelta(selectionDelta);
			handleConnectedElementRep(storageSelectionManager.getCompleteDelta());
			reactOnExternalSelection(eventTrigger.getClass().getSimpleName());
			setDisplayListDirty();
		}

	}

	private void handleVAUpdate(IMediatorSender eventTrigger, IVirtualArrayDelta delta) {
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
	protected void reactOnExternalSelection(String trigger) {

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

	/**
	 * Reset the view to its initial state, synchronized
	 */
	public synchronized final void resetView() {
		initData();
		setDisplayListDirty();
	}

	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer) {
		generalManager.getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleExternalEvent(IMediatorSender eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {
		switch (eventContainer.getEventType()) {
			case SELECTION_UPDATE:
				DeltaEventContainer<ISelectionDelta> selectionDeltaEventContainer =
					(DeltaEventContainer<ISelectionDelta>) eventContainer;
				handleSelectionUpdate(eventTrigger, selectionDeltaEventContainer.getSelectionDelta());
				break;
			case VA_UPDATE:
				if (eMediatorType != null && this instanceof GLHeatMap && ((GLHeatMap) this).bIsInListMode
					&& eMediatorType != EMediatorType.PROPAGATION_MEDIATOR) {
					break;
				}
				DeltaEventContainer<IVirtualArrayDelta> vaDeltaEventContainer =
					(DeltaEventContainer<IVirtualArrayDelta>) eventContainer;
				handleVAUpdate(eventTrigger, vaDeltaEventContainer.getSelectionDelta());
				break;
			case TRIGGER_SELECTION_COMMAND:
				SelectionCommandEventContainer commandEventContainer =
					(SelectionCommandEventContainer) eventContainer;
				switch (commandEventContainer.getIDType()) {
					case DAVID:
					case REFSEQ_MRNA_INT:
					case EXPRESSION_INDEX:
						contentSelectionManager.executeSelectionCommands(commandEventContainer
							.getSelectionCommands());
						break;
					case EXPERIMENT_INDEX:
						storageSelectionManager.executeSelectionCommands(commandEventContainer
							.getSelectionCommands());
						break;
				}
				break;
			case VIEW_COMMAND:
				ViewCommandEventContainer viewCommandEventContainer =
					(ViewCommandEventContainer) eventContainer;
				if (viewCommandEventContainer.getViewCommand() == EViewCommand.REDRAW) {
					setDisplayListDirty();
				}
				break;
		}
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
			generalManager.getLogger().log(Level.WARNING,
				"Can not handle data type of update in selectionDelta");
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

}
