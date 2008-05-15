package org.caleydo.core.view.opengl.canvas;

import java.awt.Font;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.data.collection.set.selection.ISetSelection;
import org.caleydo.core.data.collection.set.selection.SetSelection;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.selection.SelectedElementRep;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IGenomeIdManager;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.view.ESelectionMode;
import org.caleydo.core.manager.view.SelectionManager;
import org.caleydo.core.view.opengl.canvas.parcoords.EInputDataType;
import org.caleydo.core.view.opengl.canvas.parcoords.ESelectionType;
import org.caleydo.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.caleydo.core.view.opengl.util.selection.GenericSelectionManager;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Base class for OpenGL views that heavily use storages.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 *
 */
public abstract class AGLCanvasStorageBasedView 
extends AGLCanvasUser 
implements IMediatorReceiver, IMediatorSender 
{
	protected ArrayList<IStorage> alDataStorages;
	
	// Specify which type of selection is currently active
	protected ESelectionType eWhichContentSelection = ESelectionType.EXTERNAL_SELECTION;
	protected ESelectionType eWhichStorageSelection = ESelectionType.STORAGE_SELECTION;
	
	// the list of all selection arrays
	protected EnumMap<ESelectionType, ArrayList<Integer>> mapSelections;
	
	// the currently active selection arrays for content and storage 
	// (references to mapSelection entries)
	protected ArrayList<Integer> alContentSelection;
	protected ArrayList<Integer> alStorageSelection;
	
	protected boolean bIsDisplayListDirtyLocal = true;
	protected boolean bIsDisplayListDirtyRemote = true;
	
	protected int iGLDisplayListIndexLocal;
	protected int iGLDisplayListIndexRemote;
	protected int iGLDisplayListToCall = 0;
	
	protected IGenomeIdManager IDManager;	
	
	protected SelectionManager extSelectionManager;
	
	// internal management of polyline selections, use 
	// EPolylineSelectionType for types 
	protected GenericSelectionManager horizontalSelectionManager;	

	// internal management of axis selections, use
	// EAxisSelectionTypes for types
	protected GenericSelectionManager verticalSelectionManager;
	
	// flag whether one array should be a polyline or an axis
	protected boolean bRenderStorageHorizontally = false;
	
	// flag whether the whole data or the selection should be rendered
	protected boolean bRenderSelection = true;
	
	protected TextRenderer textRenderer;
	
	protected boolean bRenderOnlyContext = true;
	
	public void renderOnlyContext(boolean bRenderOnlyContext)
	{
		this.bRenderOnlyContext = bRenderOnlyContext;
	}
	
	public AGLCanvasStorageBasedView(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
		
				
		alDataStorages = new ArrayList<IStorage>();
		mapSelections = new EnumMap<ESelectionType, ArrayList<Integer>>(ESelectionType.class);	
		
		IDManager = generalManager.getGenomeIdManager();
		
		extSelectionManager = generalManager.getViewGLCanvasManager().getSelectionManager();
		
		textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 16), false);
		
	}
	
	protected void initData()
	{
		// TODO: check if I only get in here once
		alDataStorages.clear();
		
		
		if (alSetData == null)
			return;
		
		if (alSetSelection == null)
			return;				
			
		// Extract data
		for (ISet tmpSet : alSetData)
		{
			if (tmpSet.getSetType().equals(SetType.SET_GENE_EXPRESSION_DATA))
			{
				for (IStorage tmpStorage : tmpSet.getStorageByDim(0))
					alDataStorages.add(tmpStorage);
			}
		}			
		
		ArrayList<Integer> alTempList = alSetSelection.get(0).getSelectionIdArray();
//		A iArTemp = ;
//		for(int iCount = 0; iCount < iArTemp.length; iCount++)
//		{
//			alTempList.add(iArTemp[iCount]);
//		}
		if(alTempList == null)
		{
			alTempList = new ArrayList<Integer>();
		}
		mapSelections.put(ESelectionType.EXTERNAL_SELECTION, alTempList);

		int iStorageLength = alDataStorages.get(0).getArrayFloat().length;
//		int iStorageLength = 2000;
		alTempList = new ArrayList<Integer>(iStorageLength);
		// initialize full list
		
		for(int iCount = 0; iCount < iStorageLength; iCount++)
		{
			if (bRenderOnlyContext)
			{
				// FIXME: not general, only for genes
				int iDavidId = getDavidIDFromStorageIndex(iCount);
				
				if(iDavidId == -1)
					continue;
				else
				{								
					PathwayVertexGraphItem tmpPathwayVertexGraphItem = 
						((PathwayVertexGraphItem)generalManager.getPathwayItemManager().getItem(
							generalManager.getPathwayItemManager().getPathwayVertexGraphItemIdByDavidId(iDavidId)));
		
					if(tmpPathwayVertexGraphItem == null)
					{
	//					generalManager.getSingelton().logMsg(
	//							this.getClass().getSimpleName()
	//									+ " ("+iUniqueId+"): Irgendwas mit graph vertex item das eigentlich net passiern sullt ",
	//							LoggerType.VERBOSE);
						continue;					
					}
				}
			}
			alTempList.add(iCount);
		}
		
		mapSelections.put(ESelectionType.COMPLETE_SELECTION, alTempList);
		
		alTempList = new ArrayList<Integer>();
		
		for(int iCount = 0; iCount < alDataStorages.size(); iCount++)
		{
			alTempList.add(iCount);
		}
		
		mapSelections.put(ESelectionType.STORAGE_SELECTION, alTempList);
		initLists();
	}
	
	
	
	protected ArrayList<Integer> convertDavidIdToExpressionIndices(ArrayList<Integer> iAlSelection)
	{
		ArrayList<Integer> iAlSelectionStorageIndices = new ArrayList<Integer>();
		for(int iCount = 0; iCount < iAlSelection.size(); iCount++)
		{
			int iTmp = generalManager.getGenomeIdManager().getIdIntFromIntByMapping(
					iAlSelection.get(iCount), EGenomeMappingType.DAVID_2_EXPRESSION_STORAGE_ID);
			
//			if (iTmp == -1)
//				continue;
			
			iAlSelectionStorageIndices.add(iTmp);
		}
		
		return iAlSelectionStorageIndices;
	}
	
	
	protected void cleanSelection(ArrayList<Integer> iAlSelection, ArrayList<Integer> iAlGroup)
	{
		ArrayList<Integer> alDelete = new ArrayList<Integer>(1);
		for (int iCount = 0; iCount < iAlSelection.size(); iCount++)
		{
			// TODO remove elements if -1
			if(iAlSelection.get(iCount) == -1)
			{
				alDelete.add(iCount);
				continue;		
			}
//			iAlSelection.set(iCount, iAlSelection.get(iCount) / 1000);	
//			System.out.println("Storageindexalex: " + iAlSelection[iCount]);
		}		
		
		for(int iCount = alDelete.size()-1; iCount >= 0; iCount--)
		{			
			iAlSelection.remove(alDelete.get(iCount).intValue());
			iAlGroup.remove(alDelete.get(iCount).intValue());
		}
	}
	
	protected void mergeSelection(ArrayList<Integer> iAlSelection, 
			ArrayList<Integer> iAlGroup,
			ArrayList<Integer> iAlOptional)
	{	
		alSetSelection.get(0).mergeSelection(iAlSelection, iAlGroup, iAlOptional);
		
		initData();
	}
	
	protected abstract SelectedElementRep createElementRep(int iStorageIndex);
	
	protected abstract void initLists();
	
	protected int getDavidIDFromStorageIndex(int index)
	{
		int iDavidId = IDManager.getIdIntFromIntByMapping(index, 
				EGenomeMappingType.EXPRESSION_STORAGE_ID_2_DAVID);
		return iDavidId;
	}
	
	protected String getRefSeqFromStorageIndex(int index)
	{
		// Convert expression storage ID to RefSeq
		int iDavidId = getDavidIDFromStorageIndex(index);
		String sRefSeq = IDManager.getIdStringFromIntByMapping(iDavidId, 
				EGenomeMappingType.DAVID_2_REFSEQ_MRNA);
		if(sRefSeq == "")
			return "Unkonwn Gene";
		else
			return sRefSeq;		
	}
	
	public void updateReceiver(Object eventTrigger, ISet updatedSet) 
	{		
		generalManager.getLogger().log(Level.INFO, "Update called by "
				+eventTrigger.getClass().getSimpleName());
		
		ISetSelection refSetSelection = (ISetSelection) updatedSet;

		refSetSelection.getReadToken();
		// contains all genes in center pathway (not yet)
		ArrayList<Integer> iAlSelection = refSetSelection.getSelectionIdArray();
		// contains type - 0 for not selected 1 for selected
		ArrayList<Integer> iAlGroup = refSetSelection.getGroupArray();
		ArrayList<Integer> iAlOptional = refSetSelection.getOptionalDataArray();
		// iterate here		
		ArrayList<Integer> iAlSelectionStorageIndices = convertDavidIdToExpressionIndices(iAlSelection);
		cleanSelection(iAlSelectionStorageIndices, iAlGroup);
		mergeSelection(iAlSelectionStorageIndices, iAlGroup, iAlOptional);
		
		int iSelectedDavidId = 0;
		int iSelectedStorageIndex = 0;
		
		for(int iSelectionCount = 0; iSelectionCount < iAlSelectionStorageIndices.size();  iSelectionCount++)
		{
			// TODO: same for click and mouse over atm
			if(iAlGroup.get(iSelectionCount) == 1 || iAlGroup.get(iSelectionCount) == 2)
			{
				iSelectedDavidId = iAlSelection.get(iSelectionCount);
				iSelectedStorageIndex = iAlSelectionStorageIndices.get(iSelectionCount);
			
//				System.out.println("Accession ID: " + iSelectedAccessionID);
//				System.out.println("Accession Code: " +sAccessionCode);			
//				System.out.println("Expression storage index: " +iSelectedStorageIndex);
				
				if (iSelectedStorageIndex >= 0)
				{						
					if(!bRenderStorageHorizontally)
					{				
						// handle local selection
						horizontalSelectionManager.clearSelection(EViewInternalSelectionType.MOUSE_OVER);
						horizontalSelectionManager.addToType(EViewInternalSelectionType.MOUSE_OVER, iSelectedStorageIndex);
						
						// handle external selection
						extSelectionManager.modifySelection(iSelectedDavidId, 
								createElementRep(iSelectedStorageIndex), ESelectionMode.AddPick);
					}
					else
					{
						verticalSelectionManager.clearSelection(EViewInternalSelectionType.MOUSE_OVER);
						verticalSelectionManager.addToType(EViewInternalSelectionType.MOUSE_OVER, iSelectedStorageIndex);
						rePosition(iSelectedStorageIndex);
						extSelectionManager.modifySelection(iSelectedDavidId, 
								createElementRep(iSelectedStorageIndex), ESelectionMode.AddPick);
					}
				}
			}
		}
		
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#updateReceiver(java.lang.Object)
	 */
	public void updateReceiver(Object eventTrigger) {

//		generalManager.logMsg(
//				this.getClass().getSimpleName()
//						+ ": updateReceiver(Object eventTrigger): Update called by "
//						+ eventTrigger.getClass().getSimpleName(),
//				LoggerType.VERBOSE);
	}
	
	
	protected void propagateGeneSelection(int iExternalID, int iNewGroupID, ArrayList<Integer> iAlOldSelection)
	{
		int iDavidId = getDavidIDFromStorageIndex(iExternalID);	
		
		generalManager.getViewGLCanvasManager().getInfoAreaManager()
			.setData(iUniqueId, iDavidId, EInputDataType.GENE, getInfo());					
		
		// Write currently selected vertex to selection set
		// and trigger update event
		ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>(2);
		//iAlTmpSelectionId.add(1);
		ArrayList<Integer> iAlTmpGroup = new ArrayList<Integer>(2);
		
		if (iDavidId != -1)
		{			
			iAlTmpSelectionId.add(iDavidId);
			iAlTmpGroup.add(iNewGroupID);
			extSelectionManager.modifySelection(iDavidId, 
					createElementRep(iExternalID), ESelectionMode.ReplacePick);
		}							
			
		for(Integer iCurrent : iAlOldSelection)
		{
			iDavidId = getDavidIDFromStorageIndex(iCurrent);
			
			if(iDavidId != -1)
			{			
				iAlTmpSelectionId.add(iDavidId);
				iAlTmpGroup.add(0);
			}
		}

		alSetSelection.get(1).getWriteToken();
		alSetSelection.get(1).updateSelectionSet(iUniqueId, 
				iAlTmpSelectionId, iAlTmpGroup, null);
		alSetSelection.get(1).returnWriteToken();
		
		//propagateGeneSet(iAlTmpSelectionId, iAlTmpGroup);
	}
	
	protected void propagateGeneSet()//ArrayList<Integer> iAlSelection, ArrayList<Integer> iAlGroup)
	{
		
		ArrayList<Integer> iAlGroup = alSetSelection.get(0).getGroupArray();
		ArrayList<Integer> iAlSelection = alSetSelection.get(0).getSelectionIdArray();	
		
		propagateGenes(iAlSelection, iAlGroup);
		
	}
	
	protected void propagateGenes(ArrayList<Integer> iAlSelection, 
			ArrayList<Integer> iAlGroup)
	{
		
		ArrayList<Integer> iAlGeneSelection = new ArrayList<Integer>(iAlSelection.size());
		
		for(Integer iCurrent : iAlSelection)
		{
			iAlGeneSelection.add(getDavidIDFromStorageIndex(iCurrent));
		}
		
		alSetSelection.get(1).getWriteToken();
		alSetSelection.get(1).updateSelectionSet(iUniqueId, 
				iAlGeneSelection, iAlGroup, null);
		alSetSelection.get(1).returnWriteToken();	
	}
	
	protected ArrayList<Integer> prepareSelection(GenericSelectionManager selectionManager, 
			EViewInternalSelectionType selectionType)
	{
		Set<Integer> selectedSet;
		ArrayList<Integer> iAlOldSelection;
		selectedSet = selectionManager.getElements(selectionType);
		iAlOldSelection = new ArrayList<Integer>();
		for(Integer iCurrent : selectedSet)
		{
			iAlOldSelection.add(iCurrent);
		}
		return iAlOldSelection;
	}
	
	protected abstract void rePosition(int iElementID);

	
	public void clearAllSelections() 
	{
//		extSelectionManager.clear();
//		horizontalSelectionManager.clearSelections();
//		verticalSelectionManager.clearSelections();

		if (alSetSelection == null)
			return;				
				
		Iterator<SetSelection> iterSetSelection = alSetSelection.iterator();
		while (iterSetSelection.hasNext())
		{
			SetSelection tmpSet = iterSetSelection.next();
			tmpSet.getWriteToken();
			tmpSet.updateSelectionSet(iUniqueId, null, null, null);
			tmpSet.returnWriteToken();
		}	
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		
		initData();
	}
}
