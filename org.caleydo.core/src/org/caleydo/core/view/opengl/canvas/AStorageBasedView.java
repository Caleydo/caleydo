package org.caleydo.core.view.opengl.canvas;

import java.awt.Font;
import java.util.ArrayList;
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
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.picking.ESelectionMode;
import org.caleydo.core.manager.specialized.genome.IGenomeIdManager;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.view.opengl.canvas.parcoords.EStorageBasedVAType;
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
	 * Specify which type of selection is currently active for the content in
	 * the storages
	 */
	protected EStorageBasedVAType eWhichContentSelection = EStorageBasedVAType.COMPLETE_SELECTION;

	/**
	 * Specify which type of selection is currently active for the storages in
	 * the set
	 */
	protected EStorageBasedVAType eWhichStorageSelection = EStorageBasedVAType.STORAGE_SELECTION;

	/**
	 * map selection type to unique id for virtual array
	 */
	protected EnumMap<EStorageBasedVAType, Integer> mapSelections;

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

	protected boolean bIsDisplayListDirtyLocal = true;
	protected boolean bIsDisplayListDirtyRemote = true;

	protected int iGLDisplayListIndexLocal;
	protected int iGLDisplayListIndexRemote;

	protected int iGLDisplayListToCall = 0;

	protected IGenomeIdManager genomeIDManager;

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
	protected boolean bRenderSelection = true;

	protected TextRenderer textRenderer;

	/**
	 * flag whether to render only the contextual data, in particular expression
	 * data that maps to a pathway
	 */
	protected boolean bRenderOnlyContext = true;

	/**
	 * Constructor.
	 */
	public AStorageBasedView(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum, true);

		mapSelections = new EnumMap<EStorageBasedVAType, Integer>(EStorageBasedVAType.class);

		genomeIDManager = generalManager.getGenomeIdManager();

		connectedElementRepresentationManager = generalManager.getViewGLCanvasManager()
				.getConnectedElementRepresentationManager();

		textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 16), false);

	}

	/**
	 * Set wheter to render only contextual information (eg only elements that
	 * are contained in currently loaded pathways) or all available data
	 * 
	 * @param bRenderOnlyContext true if only context, else false
	 */
	public void renderOnlyContext(boolean bRenderOnlyContext)
	{
		this.bRenderOnlyContext = bRenderOnlyContext;
	}

	/**
	 * Initialize data
	 */
	public void initData()
	{
		if (alSetData == null)
			throw new CaleydoRuntimeException("Set in View is null",
					CaleydoRuntimeExceptionType.VIEW);

		if (!mapSelections.isEmpty())
		{
			for (EStorageBasedVAType eSelectionType : EStorageBasedVAType.values())
			{
				set.removeVirtualArray(mapSelections.get(eSelectionType));
			}
			mapSelections.clear();
		}

		for (ISet currentSet : alSetData)
		{
			if (currentSet.getSetType() == ESetType.GENE_EXPRESSION_DATA)
				set = currentSet;
		}

		ArrayList<Integer> alTempList = new ArrayList<Integer>();

		int iVAID = set.createStorageVA(alTempList);
		mapSelections.put(EStorageBasedVAType.EXTERNAL_SELECTION, iVAID);

		int iStorageLength = set.depth();
		// FIXME hack
		if (!bRenderOnlyContext)
			iStorageLength = 200;

		// initialize full list
		alTempList = new ArrayList<Integer>(set.depth());
		for (int iCount = 0; iCount < iStorageLength; iCount++)
		{
			if (bRenderOnlyContext)
			{
				// FIXME: not general, only for genes
				int iDavidID = getDavidIDFromStorageIndex(iCount);

				if (iDavidID == -1)
				{
					generalManager.getLogger().log(Level.FINE,
							"Cannot resolve gene to DAVID ID!");
					continue;
				}
				else
				{
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

		iVAID = set.createStorageVA(alTempList);
		mapSelections.put(EStorageBasedVAType.COMPLETE_SELECTION, iVAID);

		alTempList = new ArrayList<Integer>();

		for (int iCount = 0; iCount < set.size(); iCount++)
		{
			alTempList.add(iCount);
		}

		iVAID = set.createSetVA(alTempList);
		mapSelections.put(EStorageBasedVAType.STORAGE_SELECTION, iVAID);
	}

	/**
	 * View specific data initialization
	 */
	protected abstract void initLists();

	// protected ArrayList<Integer> convertDavidIdToExpressionIndices(
	// ArrayList<Integer> iAlSelection)
	// {
	//
	// ArrayList<Integer> iAlSelectionStorageIndices = new ArrayList<Integer>();
	// for (int iCount = 0; iCount < iAlSelection.size(); iCount++)
	// {
	// int iTmp = generalManager.getGenomeIdManager().getIdIntFromIntByMapping(
	// iAlSelection.get(iCount), EMappingType.DAVID_2_EXPRESSION_STORAGE_ID);
	//
	// // if (iTmp == -1)
	// // continue;
	//
	// iAlSelectionStorageIndices.add(iTmp);
	// }
	//
	// return iAlSelectionStorageIndices;
	// }

	// protected void cleanSelection(ArrayList<Integer> iAlSelection,
	// ArrayList<Integer> iAlGroup)
	// {
	//
	// ArrayList<Integer> alDelete = new ArrayList<Integer>(1);
	// for (int iCount = 0; iCount < iAlSelection.size(); iCount++)
	// {
	// // TODO remove elements if -1
	// if (iAlSelection.get(iCount) == -1)
	// {
	// alDelete.add(iCount);
	// continue;
	// }
	// // iAlSelection.set(iCount, iAlSelection.get(iCount) / 1000);
	// // System.out.println("Storageindexalex: " + iAlSelection[iCount]);
	// }
	//
	// for (int iCount = alDelete.size() - 1; iCount >= 0; iCount--)
	// {
	// iAlSelection.remove(alDelete.get(iCount).intValue());
	// iAlGroup.remove(alDelete.get(iCount).intValue());
	// }
	// }

	// protected void mergeSelection(ArrayList<Integer> iAlSelection,
	// ArrayList<Integer> iAlGroup, ArrayList<Integer> iAlOptional)
	// {
	//
	// alSelection.get(0).mergeSelection(iAlSelection, iAlGroup, iAlOptional);
	//
	// initData();
	// }

	/**
	 * Create 0:n {@link SelectedElementRep} for the selectionDelta
	 * 
	 * @param selectionDelta the selection delta which should be represented
	 * @throws InvalidAttributeValueException when the selectionDelta does not
	 *             contain a valid type for this view
	 */
	protected abstract SelectedElementRep createElementRep(int iStorageIndex)
			throws InvalidAttributeValueException;

	protected int getDavidIDFromStorageIndex(int index)
	{

		int iDavidId = genomeIDManager.getIdIntFromIntByMapping(index,
				EMappingType.EXPRESSION_INDEX_2_DAVID);
		return iDavidId;
	}

	protected String getRefSeqFromStorageIndex(int index)
	{

		// Convert expression storage ID to RefSeq
		int iDavidId = getDavidIDFromStorageIndex(index);
		String sRefSeq = genomeIDManager.getIdStringFromIntByMapping(iDavidId,
				EMappingType.DAVID_2_REFSEQ_MRNA);
		if (sRefSeq == "")
			return "Unkonwn Gene";
		else
			return sRefSeq;
	}

	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{

		generalManager.getLogger().log(Level.INFO,
				"Update called by " + eventTrigger.getClass().getSimpleName());

		contentSelectionManager.clearSelections();
		ISelectionDelta internalDelta = contentSelectionManager.setDelta(selectionDelta);
		handleConnectedElementRep(internalDelta);

		// // contains all genes in center pathway (not yet)
		// ArrayList<Integer> iAlSelection = setSelection.getSelectionIdArray();
		// // contains type - 0 for not selected 1 for selected
		// ArrayList<Integer> iAlGroup = setSelection.getGroupArray();
		// ArrayList<Integer> iAlOptional = setSelection.getOptionalDataArray();
		// // iterate here
		// ArrayList<Integer> iAlSelectionStorageIndices =
		// convertDavidIdToExpressionIndices(iAlSelection);

		// cleanSelection(iAlSelectionStorageIndices, iAlGroup);
		// mergeSelection(iAlSelectionStorageIndices, iAlGroup, iAlOptional);

		// int iSelectedDavidId = 0;
		// int iSelectedStorageIndex = 0;
		//
		// for (int iSelectionCount = 0; iSelectionCount <
		// iAlSelectionStorageIndices.size(); iSelectionCount++)
		// {
		// // TODO: same for click and mouse over atm
		// if (iAlGroup.get(iSelectionCount) == 1 ||
		// iAlGroup.get(iSelectionCount) == 2)
		// {
		// iSelectedDavidId = iAlSelection.get(iSelectionCount);
		// iSelectedStorageIndex =
		// iAlSelectionStorageIndices.get(iSelectionCount);
		//
		// // System.out.println("Accession ID: " + iSelectedAccessionID);
		// // System.out.println("Accession Code: " +sAccessionCode);
		// // System.out.println("Expression storage index: "
		// // +iSelectedStorageIndex);
		//
		// if (iSelectedStorageIndex >= 0)
		// {
		// if (!bRenderStorageHorizontally)
		// {
		// // handle local selection
		// contentSelectionManager
		// .clearSelection(EViewInternalSelectionType.MOUSE_OVER);
		// contentSelectionManager.addToType(
		// EViewInternalSelectionType.MOUSE_OVER, iSelectedStorageIndex);
		//
		// // handle external selection

		// TODO
		//connectedElementRepresentationManager.modifySelection(iSelectedDavidId
		// ,
		// createElementRep(iSelectedStorageIndex),
		// ESelectionMode.AddPick);
		// }
		// else
		// {
		// storageSelectionManager
		// .clearSelection(EViewInternalSelectionType.MOUSE_OVER);
		// storageSelectionManager.addToType(
		// EViewInternalSelectionType.MOUSE_OVER, iSelectedStorageIndex);
		// rePosition(iSelectedStorageIndex);
		//connectedElementRepresentationManager.modifySelection(iSelectedDavidId
		// ,
		// createElementRep(iSelectedStorageIndex),
		// ESelectionMode.AddPick);
		// }
		// }
		// }
		// }

		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger)
	{

		// generalManager.logMsg(
		// this.getClass().getSimpleName()
		// + ": updateReceiver(Object eventTrigger): Update called by "
		// + eventTrigger.getClass().getSimpleName(),
		// LoggerType.VERBOSE);
	}

	// protected void propagateGeneSelection(int iExternalID, int iNewGroupID,
	// ArrayList<Integer> iAlOldSelection)
	// {
	//
	// int iDavidId = getDavidIDFromStorageIndex(iExternalID);
	//
	// generalManager.getViewGLCanvasManager().getInfoAreaManager().setData(
	// iUniqueID,
	// iDavidId, EInputDataType.GENE, getInfo());
	//
	// // Write currently selected vertex to selection set
	// // and trigger update event
	// ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>(2);
	// // iAlTmpSelectionId.add(1);
	// ArrayList<Integer> iAlTmpGroup = new ArrayList<Integer>(2);
	//
	// if (iDavidId != -1)
	// {
	// iAlTmpSelectionId.add(iDavidId);
	// iAlTmpGroup.add(iNewGroupID);
	// connectedElementRepresentationManager.modifySelection(iDavidId,
	// createElementRep(iExternalID), ESelectionMode.ReplacePick);
	// }
	//
	// for (Integer iCurrent : iAlOldSelection)
	// {
	// iDavidId = getDavidIDFromStorageIndex(iCurrent);
	//
	// if (iDavidId != -1)
	// {
	// iAlTmpSelectionId.add(iDavidId);
	// iAlTmpGroup.add(0);
	// }
	// }
	//
	// // alSelection.get(1).updateSelectionSet(iUniqueID, iAlTmpSelectionId,
	// // iAlTmpGroup, null);
	//
	// // propagateGeneSet(iAlTmpSelectionId, iAlTmpGroup);
	// }

	// protected void propagateGeneSet()// ArrayList<Integer> iAlSelection,
	// // ArrayList<Integer> iAlGroup)
	// {
	//
	// ArrayList<Integer> iAlGroup = alSelection.get(0).getGroupArray();
	// ArrayList<Integer> iAlSelection =
	// alSelection.get(0).getSelectionIdArray();
	//
	// propagateGenes(iAlSelection, iAlGroup);
	//
	// }

	// protected void propagateGenes(ArrayList<Integer> iAlSelection,
	// ArrayList<Integer> iAlGroup)
	// {
	//
	// ArrayList<Integer> iAlGeneSelection = new
	// ArrayList<Integer>(iAlSelection.size());
	//
	// for (Integer iCurrent : iAlSelection)
	// {
	// iAlGeneSelection.add(getDavidIDFromStorageIndex(iCurrent));
	// }
	//
	// alSelection.get(1).updateSelectionSet(iUniqueID, iAlGeneSelection,
	// iAlGroup, null);
	// }

	// protected ArrayList<Integer> prepareSelection(GenericSelectionManager
	// connectedElementRepManager,
	// EViewInternalSelectionType selectionType)
	// {
	//
	// Set<Integer> selectedSet;
	// ArrayList<Integer> iAlOldSelection;
	// selectedSet = connectedElementRepManager.getElements(selectionType);
	// iAlOldSelection = new ArrayList<Integer>();
	// for (Integer iCurrent : selectedSet)
	// {
	// iAlOldSelection.add(iCurrent);
	// }
	// return iAlOldSelection;
	// }

	protected abstract void rePosition(int iElementID);

	public void clearAllSelections()
	{
		connectedElementRepresentationManager.clear();
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;

		// initData();

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
						throw new CaleydoRuntimeException("No internal id in selection delta", CaleydoRuntimeExceptionType.VIEW);
				
					
					System.out.println("StorageBased with ID: " + iUniqueID + " David: " + iDavidID);
					
					SelectedElementRep rep = createElementRep(iStorageIndex);
					connectedElementRepresentationManager.modifySelection(iDavidID, rep,
							ESelectionMode.AddPick);
				}

			}
		}
		catch (InvalidAttributeValueException e)
		{
			generalManager.getLogger().log(Level.WARNING,
					"Can not handle data type of update in selectionDelta");
		}
	}

}
