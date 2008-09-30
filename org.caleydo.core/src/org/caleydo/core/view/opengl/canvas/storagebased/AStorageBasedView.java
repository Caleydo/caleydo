package org.caleydo.core.view.opengl.canvas.storagebased;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
import org.caleydo.core.data.selection.SelectionItem;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.selection.SelectedElementRep;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.picking.ESelectionMode;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
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

	protected int iGLDisplayListIndexLocal;
	protected int iGLDisplayListIndexRemote;

	protected int iGLDisplayListToCall = 0;

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
	protected boolean bRenderOnlyContext = true;

	protected TextRenderer textRenderer;

	/**
	 * Define what level of filtering on the data should be applied
	 */
	protected EDataFilterLevel dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;

	protected boolean bUseRandomSampling = true;

	protected int iNumberOfRandomElements = 100;

	/**
	 * Constructor.
	 */
	protected AStorageBasedView(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum, true);

		mapVAIDs = new EnumMap<EStorageBasedVAType, Integer>(EStorageBasedVAType.class);

		genomeIDManager = generalManager.getGenomeIdManager();

		connectedElementRepresentationManager = generalManager.getViewGLCanvasManager()
				.getConnectedElementRepresentationManager();

		textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 16), false);

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

	public void initData()
	{
		set = null;

		for (ISet currentSet : alSets)
		{
			if (currentSet.getSetType() == ESetType.GENE_EXPRESSION_DATA)
				set = currentSet;
		}

		if (!mapVAIDs.isEmpty())
		{
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
			connectedElementRepresentationManager.clear();
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
	protected void initCompleteList()
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
	 * @param selectionDelta the selection delta which should be represented
	 * @throws InvalidAttributeValueException when the selectionDelta does not
	 *             contain a valid type for this view
	 */
	protected abstract SelectedElementRep createElementRep(int iStorageIndex)
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
		
		if (iDavidId == null)
			return "Unknown Gene";
		
		String sRefSeq = genomeIDManager.getID(EMappingType.DAVID_2_REFSEQ_MRNA, iDavidId);
		if (sRefSeq == "")
			return "Unkonwn Gene";
		else
			return sRefSeq;
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
	public void handleUpdate(IUniqueObject eventTrigger)
	{

	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{
		// Check for type that can be handled
		if (selectionDelta.getIDType() != EIDType.DAVID)
			return;

		generalManager.getLogger().log(
				Level.INFO,
				"Update called by " + eventTrigger.getClass().getSimpleName()
						+ ", received in: " + this.getClass().getName());

		contentSelectionManager.clearSelections();
		ISelectionDelta internalDelta = contentSelectionManager.setDelta(selectionDelta);
		//handleConnectedElementRep(internalDelta);
		handleConnectedElementRep(internalDelta);
		checkUnselection();
		setDisplayListDirty();
	}

	/**
	 * Clears all selections, meaning that no element is selected or deselected
	 * after this method was called. Everything returns to "normal". Note that
	 * virtual array manipulations are not considered selections and are
	 * therefore not reset.
	 */
	public void clearAllSelections()
	{
		connectedElementRepresentationManager.clear();
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		setDisplayListDirty();
	}

	public void resetView()
	{
//		contentSelectionManager.resetSelectionManager();
//		storageSelectionManager.resetSelectionManager();
//		if (bRenderOnlyContext == true)
//			set.getVA(iContentVAID).clear();
//		else
//			initCompleteList();
//		
//		set.getVA(iStorageVAID).reset();
//		
//
//		contentSelectionManager.setVA(set.getVA(iContentVAID));
//		storageSelectionManager.setVA(set.getVA(iStorageVAID));
		
		initData();
		
		//resetSelections();
		setDisplayListDirty();
	}

	@Override
	public void triggerUpdate()
	{
		generalManager.getEventPublisher().handleUpdate(this);
	}

	@Override
	public void triggerUpdate(ISelectionDelta selectionDelta)
	{
		// TODO connects to one element only here

		handleConnectedElementRep(selectionDelta);
		generalManager.getEventPublisher().handleUpdate(this, selectionDelta);
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
			int iDavidID = -1;
			if (selectionDelta.size() > 0)
			{
				for (SelectionItem item : selectionDelta)
				{
					if (item.getSelectionType() != ESelectionType.MOUSE_OVER)
						continue;

					if (selectionDelta.getIDType() == EIDType.EXPRESSION_INDEX)
					{
						iStorageIndex = item.getSelectionID();
						iDavidID = item.getInternalID();

					}
					else if (selectionDelta.getInternalIDType() == EIDType.EXPRESSION_INDEX)
					{
						iStorageIndex = item.getInternalID();
						iDavidID = item.getSelectionID();
					}
					else
						throw new InvalidAttributeValueException("Can not handle data type");

					if (iStorageIndex == -1)
						throw new IllegalArgumentException("No internal ID in selection delta");

					SelectedElementRep rep = createElementRep(iStorageIndex);
					if (rep == null)
					{
						continue;
					}
					connectedElementRepresentationManager.modifySelection(iDavidID, rep,
							ESelectionMode.ADD_PICK);
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
	 * Check wheter an element is selected or not
	 */
	protected abstract void checkUnselection();

	/**
	 * Broadcast all elements independent of their type.
	 */
	public abstract void broadcastElements();

	@Override
	public void broadcastElements(ESelectionType type)
	{
		// TODO: implement
	}

	public final void useRandomSampling(boolean bUseRandomSampling)
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
	 * Set the number of samples which are shown in the view. The distribution is purely random
	 * 
	 * @param iNumberOfRandomElements the number
	 */
	public final void setNumberOfSamplesToShow(int iNumberOfRandomElements)
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

	public abstract void resetSelections();
	
}
