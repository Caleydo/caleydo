package org.caleydo.core.view.opengl.canvas.storagebased;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Set;
import java.util.logging.Level;
import javax.management.InvalidAttributeValueException;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionItem;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
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
	implements IMediatorReceiver, IMediatorSender
{

	protected ISet set;
	protected ESetType setType;

	/**
	 * map selection type to unique id for virtual array
	 */
	protected EnumMap<EStorageBasedVAType, Integer> mapVAIDs;

	/**
	 * The id of the virtual array that manages the contents (the indices) in
	 * the storages
	 */
	protected int iContentVAID = 0;

	/**
	 * The id of the virtual array that manages the storage references in the
	 * set
	 */
	protected int iStorageVAID = 0;

	protected IIDMappingManager genomeIDManager;

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

	protected int iNumberOfRandomElements = 100;

	protected int iNumberOfSamplesPerTexture = 100;

	protected int iNumberOfSamplesPerHeatmap = 100;

	/**
	 * Constructor for storage based views
	 * 
	 * @param setType from the type of set the kind of visualization is derived
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	protected AStorageBasedView(ESetType setType, final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum, true);

		this.setType = setType;

		mapVAIDs = new EnumMap<EStorageBasedVAType, Integer>(EStorageBasedVAType.class);

		genomeIDManager = generalManager.getIDMappingManager();

		connectedElementRepresentationManager = generalManager.getViewGLCanvasManager()
				.getConnectedElementRepresentationManager();

		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 16), false);

	}

	/**
	 * Toggle whether to render the complete dataset (with regards to the
	 * filters though) or only contextual data
	 * 
	 * This effectively means switching between the
	 * {@link EStorageBasedVAType#COMPLETE_SELECTION} and
	 * {@link EStorageBasedVAType#EXTERNAL_SELECTION}
	 * 
	 */
	public abstract void renderContext(boolean bRenderContext);

	/**
	 * Check whether only context is beeing rendered
	 * 
	 * @return
	 */
	public boolean isRenderingOnlyContext()
	{
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

	public synchronized final void initData()
	{
		set = null;

		for (ISet currentSet : alSets)
		{
			if (currentSet.getSetType() == setType)
				set = currentSet;
		}

		if (!mapVAIDs.isEmpty())
		{

			// This should be done once we get some thread safety, memory leak,
			// and a big one

			for (EStorageBasedVAType eSelectionType : EStorageBasedVAType.values())
			{
				if (mapVAIDs.containsKey(eSelectionType))
					set.removeVirtualArray(mapVAIDs.get(eSelectionType));
			}
			iContentVAID = -1;
			iStorageVAID = -1;
			mapVAIDs.clear();
		}

		if (set == null)
		{
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

		for (int iCount = 0; iCount < set.size(); iCount++)
		{
			alTempList.add(iCount);
		}

		iVAID = set.createSetVA(alTempList);
		mapVAIDs.put(EStorageBasedVAType.STORAGE_SELECTION, iVAID);

		initLists();
	}

	/**
	 * Initializes a virtual array with all elements, according to the data
	 * filters, as defined in {@link EDataFilterLevel}.
	 */
	protected final void initCompleteList()
	{
		// initialize virtual array that contains all (filtered) information
		ArrayList<Integer> alTempList = new ArrayList<Integer>(set.depth());

		for (int iCount = 0; iCount < set.depth(); iCount++)
		{
			if (dataFilterLevel != EDataFilterLevel.COMPLETE)
			{
				// Here we get mapping data for all values
				// FIXME: not general, only for genes
				int iDavidID = getDavidIDFromStorageIndex(iCount);

				if (iDavidID == -1)
				{
					generalManager.getLogger().log(Level.FINE,
							"Cannot resolve gene to DAVID ID!");
					continue;
				}

				if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT)
				{

					// Here all values are contained within pathways as well
					int iGraphItemID = generalManager.getPathwayItemManager()
							.getPathwayVertexGraphItemIdByDavidId(iDavidID);

					if (iGraphItemID == -1)
						continue;

					PathwayVertexGraphItem tmpPathwayVertexGraphItem = ((PathwayVertexGraphItem) generalManager
							.getPathwayItemManager().getItem(iGraphItemID));

					if (tmpPathwayVertexGraphItem == null)
						continue;
				}
			}
			alTempList.add(iCount);
		}

		if (bUseRandomSampling)
		{
			Collections.shuffle(alTempList);
			if (alTempList.size() > iNumberOfRandomElements)
			{
				ArrayList<Integer> alNewList = new ArrayList<Integer>();
				alNewList.addAll(alTempList.subList(0, iNumberOfRandomElements));
				alTempList = alNewList;
			}

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
	 * @param idType TODO
	 * @param selectionDelta the selection delta which should be represented
	 * 
	 * @throws InvalidAttributeValueException when the selectionDelta does not
	 *             contain a valid type for this view
	 */
	protected abstract SelectedElementRep createElementRep(EIDType idType, int iStorageIndex)
			throws InvalidAttributeValueException;

	@Deprecated
	protected int getDavidIDFromStorageIndex(int index)
	{
		Integer iDavidId = genomeIDManager.getID(EMappingType.EXPRESSION_INDEX_2_DAVID, index);

		if (iDavidId == null)
			return -1;

		return iDavidId;
	}

	@Deprecated
	protected String getRefSeqFromStorageIndex(int index)
	{
		// Convert expression storage ID to RefSeq
		Integer iDavidId = getDavidIDFromStorageIndex(index);

		// if (iDavidId == null)
		// return "Unknown Gene";

		Set<String> sSetRefSeqID = genomeIDManager.getMultiID(
				EMappingType.DAVID_2_REFSEQ_MRNA, iDavidId);
		String sOutput = "";
		for (String sRefSeqID : sSetRefSeqID)
		{
			// if (sRefSeqID == "")
			// continue;

			sOutput += sRefSeqID;
			sOutput += " | ";
		}

		return sOutput;
	}

	@Deprecated
	protected String getShortNameFromDavid(int index)
	{
		// Convert expression storage ID to RefSeq
		Integer iDavidID = getDavidIDFromStorageIndex(index);

		if (iDavidID == null)
			return "Unknown Gene";

		String sGeneSymbol = genomeIDManager.getID(EMappingType.DAVID_2_GENE_SYMBOL, iDavidID);
		if (sGeneSymbol == "")
			return "Unkonwn Gene";
		else
			return sGeneSymbol;
	}

	@Override
	public synchronized final void handleUpdate(IUniqueObject eventTrigger,
			ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand,
			EMediatorType eMediatorType)

	{

		generalManager.getLogger().log(
				Level.INFO,
				"Update called by " + eventTrigger.getClass().getSimpleName()
						+ ", received in: " + this.getClass().getSimpleName());

		// Check for type that can be handled
		if (selectionDelta.getIDType() == EIDType.DAVID
				|| selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX)
		{
			contentSelectionManager.executeSelectionCommands(colSelectionCommand);

			generalManager.getLogger().log(
					Level.INFO,
					"Update called by " + eventTrigger.getClass().getSimpleName()
							+ ", received in: " + this.getClass().getSimpleName());

			contentSelectionManager.setDelta(selectionDelta);
			ISelectionDelta internalDelta = contentSelectionManager.getCompleteDelta();
			initForAddedElements();
			handleConnectedElementRep(internalDelta);
			reactOnExternalSelection();
			setDisplayListDirty();
		}

		else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX)
		{
			// generalManager.getIDMappingManager().getID(EMappingType.EXPERIMENT_2_EXPERIMENT_INDEX,
			// key)(type)

			storageSelectionManager.setDelta(selectionDelta);
			handleConnectedElementRep(storageSelectionManager.getCompleteDelta());
			setDisplayListDirty();
		}

	}

	/**
	 * Is called any time a update is triggered externally. Should be implemented by inheriting views.
	 */
	protected void reactOnExternalSelection()
	{
		
	}
	
	
	/**
	 * This method is called when new elements are added from external - if you
	 * need to react to it do it here, if not don't do anything.
	 */
	protected void initForAddedElements()
	{
	}

	/**
	 * Clears all selections, meaning that no element is selected or deselected
	 * after this method was called. Everything returns to "normal". Note that
	 * virtual array manipulations are not considered selections and are
	 * therefore not reset.
	 */
	public synchronized final void clearAllSelections()
	{
		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		setDisplayListDirty();
	}

	/**
	 * Reset the view to its initial state, synchronized
	 */
	public synchronized final void resetView()
	{
		initData();
		setDisplayListDirty();
	}

	@Override
	public final synchronized void triggerUpdate(EMediatorType eMediatorType,
			ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand)
	{
		// TODO connects to one element only here
		handleConnectedElementRep(selectionDelta);
		generalManager.getEventPublisher().triggerUpdate(eMediatorType, this, selectionDelta,
				colSelectionCommand);
	}

	/**
	 * Handles the creation of {@link SelectedElementRep} according to the data
	 * in a selectionDelta
	 * 
	 * @param selectionDelta the selection data that should be handled
	 */
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta)
	{
		try
		{
			int iStorageIndex = -1;

			int iID = -1;
			EIDType idType;

			if (selectionDelta.size() > 0)
			{
				for (SelectionItem item : selectionDelta)
				{
					if (!(item.getSelectionType() == ESelectionType.MOUSE_OVER || item
							.getSelectionType() == ESelectionType.SELECTION))
						// if (!(item.getSelectionType() ==
						// ESelectionType.MOUSE_OVER))
						continue;

					if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX)
					{
						iStorageIndex = item.getSelectionID();

						iID = item.getInternalID();
						idType = EIDType.EXPRESSION_INDEX;

					}
					else if (selectionDelta.getInternalIDType() == EIDType.EXPRESSION_INDEX)
					{
						iStorageIndex = item.getInternalID();

						iID = item.getSelectionID();
						idType = EIDType.EXPRESSION_INDEX;
					}
					else if (selectionDelta.getIDType() == EIDType.EXPERIMENT_INDEX)
					{
						iID = item.getSelectionID();
						iStorageIndex = iID;
						idType = EIDType.EXPERIMENT_INDEX;

					}
					else
						throw new InvalidAttributeValueException("Can not handle data type");

					if (iStorageIndex == -1)
						throw new IllegalArgumentException("No internal ID in selection delta");

					SelectedElementRep rep = createElementRep(idType, iStorageIndex);
					if (rep == null)
					{
						continue;
					}

					for (Integer iConnectionID : item.getConnectionID())
					{
						connectedElementRepresentationManager.addSelection(iConnectionID, rep);
					}
				}

			}
		}
		catch (InvalidAttributeValueException e)
		{
			generalManager.getLogger().log(Level.WARNING,
					"Can not handle data type of update in selectionDelta");
		}
	}

	/**
	 * Re-position a view centered on a element, specified by the element ID
	 * 
	 * @param iElementID the ID of the element that should be in the center
	 */
	protected abstract void rePosition(int iElementID);

	/**
	 * Broadcast all elements independent of their type.
	 */
	public abstract void broadcastElements();

	@Override
	public synchronized void broadcastElements(ESelectionType type)
	{
		// nothing to do
	}

	/**
	 * Set whether to use random sampling or not, synchronized
	 * 
	 * @param bUseRandomSampling
	 */
	public synchronized final void useRandomSampling(boolean bUseRandomSampling)
	{
		if (this.bUseRandomSampling != bUseRandomSampling)
		{
			this.bUseRandomSampling = bUseRandomSampling;
		}
		// TODO, probably do this with initCompleteList, take care of selection
		// manager though
		initData();
		initCompleteList();
	}

	/**
	 * Set the number of samples which are shown in the view. The distribution
	 * is purely random
	 * 
	 * @param iNumberOfRandomElements the number
	 */
	public synchronized final void setNumberOfSamplesToShow(int iNumberOfRandomElements)
	{
		if (iNumberOfRandomElements != this.iNumberOfRandomElements && bUseRandomSampling)
		{
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
	 * @param iNumberOfSamplesPerTexture the number
	 */
	public synchronized final void setNumberOfSamplesPerTexture(int iNumberOfSamplesPerTexture)
	{
		this.iNumberOfSamplesPerTexture = iNumberOfSamplesPerTexture;
	}

	/**
	 * Set the number of samples which are shown in one heat map
	 * 
	 * @param iNumberOfSamplesPerHeatmap the number
	 */
	public synchronized final void setNumberOfSamplesPerHeatmap(int iNumberOfSamplesPerHeatmap)
	{
		this.iNumberOfSamplesPerHeatmap = iNumberOfSamplesPerHeatmap;
	}

	/**
	 * Set the level of data filtering, according to the parameters defined in
	 * {@link EDataFilterLevel}
	 * 
	 * @param dataFilterLevel the level of filtering
	 */
	public synchronized void setDataFilterLevel(EDataFilterLevel dataFilterLevel)
	{
		this.dataFilterLevel = dataFilterLevel;
	}

	public abstract void resetSelections();

	public abstract void changeOrientation(boolean bDefaultOrientation);

	public abstract boolean isInDefaultOrientation();

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType)
	{
		return contentSelectionManager.getElements(eSelectionType).size();
	}
}
