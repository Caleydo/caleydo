package org.geneview.core.view.opengl.canvas;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.SetType;
import org.geneview.core.data.collection.set.selection.ISetSelection;
import org.geneview.core.data.mapping.EGenomeMappingType;
import org.geneview.core.data.view.camera.IViewFrustum;
import org.geneview.core.data.view.rep.selection.SelectedElementRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.IGenomeIdManager;
import org.geneview.core.manager.event.mediator.IMediatorReceiver;
import org.geneview.core.manager.event.mediator.IMediatorSender;
import org.geneview.core.manager.view.ESelectionMode;
import org.geneview.core.manager.view.SelectionManager;
import org.geneview.core.view.opengl.canvas.parcoords.ESelectionType;
import org.geneview.core.view.opengl.util.selection.EViewInternalSelectionType;
import org.geneview.core.view.opengl.util.selection.GenericSelectionManager;


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
	
	public AGLCanvasStorageBasedView(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
		
				
		alDataStorages = new ArrayList<IStorage>();
		mapSelections = new EnumMap<ESelectionType, ArrayList<Integer>>(ESelectionType.class);	
		
		IDManager = generalManager.getSingelton().getGenomeIdManager();
		
		extSelectionManager = generalManager.
		getSingelton().getViewGLCanvasManager().getSelectionManager();
		
	}
	
	protected void initData()
	{
		// TODO: check if I only get in here once
		alDataStorages.clear();
		
		
		if (alSetData == null)
			return;
		
		if (alSetSelection == null)
			return;				
				
		
		Iterator<ISet> iterSetData = alSetData.iterator();
		while (iterSetData.hasNext())
		{
			ISet tmpSet = iterSetData.next();
						
			if (tmpSet.getSetType().equals(SetType.SET_GENE_EXPRESSION_DATA))
			{
				alDataStorages.add(tmpSet.getStorageByDimAndIndex(0, 0));
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

		//int iStorageLength = alDataStorages.get(0).getArrayFloat().length;
		int iStorageLength = 1000;
		alTempList = new ArrayList<Integer>(iStorageLength);
		// initialize full list
		for(int iCount = 0; iCount < iStorageLength; iCount++)
		{
			alTempList.add(iCount);
		}
		
		mapSelections.put(ESelectionType.COMPLETE_SELECTION, alTempList);
		
		alTempList = new ArrayList<Integer>();
		
		for(int iCount = 0; iCount < alDataStorages.size(); iCount++)
		{
			alTempList.add(iCount);
		}
		
		mapSelections.put(ESelectionType.STORAGE_SELECTION, alTempList);
	}
	
	protected void initLists()
	{		
		int iNumberOfColumns;
		
		horizontalSelectionManager.resetSelectionManager();
		
		int iNumberOfEntriesToRender = 0;		

		alContentSelection = mapSelections.get(eWhichContentSelection);
		alStorageSelection = mapSelections.get(eWhichStorageSelection);
		iNumberOfEntriesToRender = alContentSelection.size();
	
		int iNumberOfRowsToRender = 0;		
		
		// if true one array corresponds to one polyline, number of arrays is number of polylines
		if (bRenderStorageHorizontally)
		{			
			iNumberOfRowsToRender = alStorageSelection.size();
			iNumberOfColumns = iNumberOfEntriesToRender;			
		}
		// render polylines across storages - first element of storage 1 to n makes up polyline
		else
		{						
			iNumberOfRowsToRender = iNumberOfEntriesToRender;
			iNumberOfColumns = alStorageSelection.size();
		}		
				
			
		// this for loop executes once per polyline
		for (int iRowCount = 0; iRowCount < iNumberOfRowsToRender; iRowCount++)
		{	
			if(bRenderStorageHorizontally)
				horizontalSelectionManager.initialAdd(alStorageSelection.get(iRowCount));
			else
				horizontalSelectionManager.initialAdd(alContentSelection.get(iRowCount));
		}
		
		// this for loop executes one per axis
		for (int iColumnCount = 0; iColumnCount < iNumberOfColumns; iColumnCount++)
		{
			if(bRenderStorageHorizontally)
				verticalSelectionManager.initialAdd(alContentSelection.get(iColumnCount));
			else
				verticalSelectionManager.initialAdd(alStorageSelection.get(iColumnCount));
		}		
		
//		fXTranslation = viewFrustum.getLeft() + renderStyle.getXSpacing();
//		fYTranslation = viewFrustum.getBottom() + renderStyle.getBottomSpacing();
//		
//		fXTranslation = renderStyle.getXSpacing();
//		fYTranslation = renderStyle.getBottomSpacing();
//	
//		fAxisSpacing = renderStyle.getAxisSpacing(iNumberOfAxis);
		
	}
	
	protected ArrayList<Integer> convertAccessionToExpressionIndices(ArrayList<Integer> iAlSelection)
	{
		ArrayList<Integer> iAlSelectionStorageIndices = new ArrayList<Integer>();
		for(int iCount = 0; iCount < iAlSelection.size(); iCount++)
		{
			int iTmp = generalManager.getSingelton().getGenomeIdManager()
				.getIdIntFromIntByMapping(iAlSelection.get(iCount), EGenomeMappingType.ACCESSION_2_MICROARRAY_EXPRESSION);
			
			if (iTmp == -1)
				continue;
			
			iAlSelectionStorageIndices.add(iTmp);
		}
		
		return iAlSelectionStorageIndices;
	}
	
	
	protected ArrayList<Integer>  cleanSelection(ArrayList<Integer> iAlSelection, ArrayList<Integer> iAlGroup)
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
			iAlSelection.set(iCount, iAlSelection.get(iCount) / 1000);	
//			System.out.println("Storageindexalex: " + iAlSelection[iCount]);
		}		
		
		for(int iCount = alDelete.size()-1; iCount >= 0; iCount--)
		{
			iAlSelection.remove(iCount);
			iAlGroup.remove(iCount);
		}
		
		return iAlSelection;
	}
	
	protected void setSelection(ArrayList<Integer> iAlSelection, 
			ArrayList<Integer> iAlGroup,
			ArrayList<Integer> iAlOptional)
	{	
		alSetSelection.get(0).mergeSelection(iAlSelection, iAlGroup, iAlOptional);
		
		initData();
		initLists();
	}
	
	protected abstract SelectedElementRep createElementRep(int iStorageIndex);
	
	
	protected int getAccesionIDFromStorageIndex(int index)
	{
		int iAccessionID = IDManager.getIdIntFromIntByMapping(index*1000+770, 
				EGenomeMappingType.MICROARRAY_EXPRESSION_2_ACCESSION);
		return iAccessionID;
	}
	
	protected String getAccessionNumberFromStorageIndex(int index)
	{
			
		// Convert expression storage ID to accession ID
		int iAccessionID = getAccesionIDFromStorageIndex(index);
		String sAccessionNumber = IDManager.getIdStringFromIntByMapping(iAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);
		if(sAccessionNumber == "")
			return "Unkonwn Gene";
		else
			return sAccessionNumber;		
	}
	
	public void updateReceiver(Object eventTrigger, ISet updatedSet) 
	{		
		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName()
						+ " ("+iUniqueId+"): updateReceiver(Object eventTrigger, ISet updatedSet): Update called by "
						+ eventTrigger.getClass().getSimpleName()+" ("+((AGLCanvasUser)eventTrigger).getId(),
				LoggerType.VERBOSE);
		
		ISetSelection refSetSelection = (ISetSelection) updatedSet;

		refSetSelection.getReadToken();
		// contains all genes in center pathway (not yet)
		ArrayList<Integer> iAlSelection = refSetSelection.getSelectionIdArray();

		// contains type - 0 for not selected 1 for selected
		ArrayList<Integer> iAlGroup = refSetSelection.getGroupArray();
		ArrayList<Integer> iAlOptional = refSetSelection.getOptionalDataArray();
		// iterate here		
		ArrayList<Integer> iAlSelectionStorageIndices = convertAccessionToExpressionIndices(iAlSelection);
		iAlSelectionStorageIndices = cleanSelection(iAlSelectionStorageIndices, iAlGroup);
		setSelection(iAlSelectionStorageIndices, iAlGroup, iAlOptional);
		
		int iSelectedAccessionID = 0;
		int iSelectedStorageIndex = 0;
		
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		
		for(int iSelectionCount = 0; iSelectionCount < iAlSelectionStorageIndices.size();  iSelectionCount++)
		{
			// TODO: set this to 1 resp. later to a enum as soon as I get real data
			if(iAlGroup.get(iSelectionCount) == 1)
			{
				iSelectedAccessionID = iAlSelection.get(iSelectionCount);
				iSelectedStorageIndex = iAlSelectionStorageIndices.get(iSelectionCount);
				
				String sAccessionCode = generalManager.getSingelton().getGenomeIdManager()
					.getIdStringFromIntByMapping(iSelectedAccessionID, EGenomeMappingType.ACCESSION_2_ACCESSION_CODE);
			
				System.out.println("Accession ID: " + iSelectedAccessionID);
				System.out.println("Accession Code: " +sAccessionCode);			
				System.out.println("Expression stroage index: " +iSelectedStorageIndex);
				
				if (iSelectedStorageIndex >= 0)
				{						
					if(!bRenderStorageHorizontally)
					{				
						// handle local selection
						horizontalSelectionManager.clearSelection(EViewInternalSelectionType.MOUSE_OVER);
						horizontalSelectionManager.addToType(EViewInternalSelectionType.MOUSE_OVER, iSelectedStorageIndex);
						
						// handle external selection
						extSelectionManager.modifySelection(iSelectedAccessionID, 
								createElementRep(iSelectedStorageIndex), ESelectionMode.AddPick);
					}
					else
					{
						verticalSelectionManager.clearSelection(EViewInternalSelectionType.MOUSE_OVER);
						verticalSelectionManager.addToType(EViewInternalSelectionType.MOUSE_OVER, iSelectedStorageIndex);
						
						extSelectionManager.modifySelection(iSelectedAccessionID, createElementRep(iSelectedStorageIndex), ESelectionMode.AddPick);
					}
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.view.opengl.canvas.AGLCanvasUser#updateReceiver(java.lang.Object)
	 */
	public void updateReceiver(Object eventTrigger) {

		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName()
						+ ": updateReceiver(Object eventTrigger): Update called by "
						+ eventTrigger.getClass().getSimpleName(),
				LoggerType.VERBOSE);
	}

}
