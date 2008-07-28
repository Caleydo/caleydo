package org.caleydo.core.data.selection;

import java.util.ArrayList;

import org.caleydo.core.data.AUniqueManagedObject;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.type.ManagerObjectType;

/**
 * Selection SET that gives access to
 * the selection storage arrays.
 * The access methods are thread safe.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 * 
 * 
 * FIXME: DO NOT ASSIGN REFERENCES TO INTERNAL DATA
 */
public class Selection 
extends AUniqueManagedObject 
implements ISelection
{
	// TODO: replace this with a storage that takes ArrayLists
	
	private ArrayList<Integer> iAlSelectionID = null;
	private ArrayList<Integer> iAlSelectionGroup = null;
	private ArrayList<Integer> iAlSelectionOptionalData = null;

	/**
	 * Constructor.
	 * 
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 */
	public Selection(int iSetCollectionId, 
			IGeneralManager generalManager) 
	{

	super(iSetCollectionId, 
				generalManager);
		
//		/** add missing objects for optional data */
//		vecRefSelection_Array.add(2, new Vector<IVirtualArray> (2));		
//		vecRefStorage_Array.add(2, new Vector<IStorage> (2));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.selection.ISetSelection#setSelectionIdArray(int[])
	 */
	public void setSelectionIdArray(ArrayList<Integer> iAlSelectionId) 
	{
		this.iAlSelectionID = iAlSelectionId;
//		this.getWriteToken();
//		this.getStorageByDimAndIndex(0, 0).setArrayInt(iArSelectionId);
//		
//		//TODO: fix this after CHI!
////		IVirtualArray bufferVA= this.getVirtualArrayByDimAndIndex(0, 0);
////		bufferVA.setOffset(0);
////		bufferVA.setLength(iArSelectionId.length-1);
//		
//		this.returnWriteToken();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.selection.ISetSelection#setGroupArray(int[])
	 */
	public void setGroupArray(ArrayList<Integer> iAlSelectionGroup) {
		
		this.iAlSelectionGroup = iAlSelectionGroup;
//		this.getWriteToken();
//		this.getStorageByDimAndIndex(0, 1).setArrayInt(iArSelectionGroup);
		
		//TODO: fix this after CHI!
//		IVirtualArray bufferVA = this.getVirtualArrayByDimAndIndex(0, 1);
//		bufferVA.setOffset(0);
//		bufferVA.setLength(iArSelectionGroup.length-1);
		
		//this.returnWriteToken();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.selection.ISetSelection#setOptionalDataArray(int[])
	 */
	public void setOptionalDataArray(ArrayList<Integer> iAlSelectionOptionalData)
	{
		this.iAlSelectionOptionalData = iAlSelectionOptionalData;
		
//		this.getWriteToken();
//		this.getStorageByDimAndIndex(0, 2).setArrayInt(iArSelectionOptionalData);
		
		//TODO: fix this after CHI!
//		IVirtualArray bufferVA = this.getVirtualArrayByDimAndIndex(0, 2);
//		bufferVA.setOffset(0);
//		bufferVA.setLength(iArSelectionOptionalData.length-1);
		
		//this.returnWriteToken();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.selection.ISetSelection#setAllSelectionDataArrays(int[], int[], int[])
	 */
	public void setAllSelectionDataArrays(ArrayList<Integer> iAlSelectionId, 
			ArrayList<Integer> iAlSelectionGroup, 
			ArrayList<Integer> iAlSelectionOptionalData) 
	{
		this.iAlSelectionID = iAlSelectionId;
		this.iAlSelectionGroup = iAlSelectionGroup;
		this.iAlSelectionOptionalData = iAlSelectionOptionalData;
//		if (( iArSelectionId.length != iArSelectionGroup.length )&&
//				(iArSelectionGroup.length != iArSelectionOptionalData.length))
//		{
//			throw new CaleydoRuntimeException("Try to set a SetSelection wiht array of different length!");		
//		}
//		
//		this.getWriteToken();
//		this.getStorageByDimAndIndex(0, 0).setArrayInt(iArSelectionId);
//		this.getStorageByDimAndIndex(0, 1).setArrayInt(iArSelectionGroup);
//		this.getStorageByDimAndIndex(0, 2).setArrayInt(iArSelectionOptionalData);
//		
//		//TODO: fix this after CHI!
////		for ( int i=0; i<3; i++)
////		{
////			IVirtualArray bufferVA = this.getVirtualArrayByDimAndIndex(0, i);
////			bufferVA.setOffset(0);
////			bufferVA.setLength(iArSelectionId.length);
////		}
//		
//		this.returnWriteToken();
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getDimensions()
	 */
	public final int getDimensions() {
		return 3;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.selection.ISetSelection#getSelectionIdArray()
	 */
	public ArrayList<Integer> getSelectionIdArray() {
		

//		int[] tmp = this.getStorageByDimAndIndex(0, 0).getArrayInt();

//		
//		return tmp;
		if (iAlSelectionID ==  null)
			return null;
		
		ArrayList<Integer> alTmp = new ArrayList<Integer>();
		for (Integer iCurrent : iAlSelectionID)
		{
			alTmp.add(iCurrent.intValue());
		}
		
		return alTmp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.selection.ISetSelection#getGroupArray()
	 */
	public ArrayList<Integer> getGroupArray() {	


//		int[] tmp = this.getStorageByDimAndIndex(0, 1).getArrayInt();

//		
//		return tmp;
		if (iAlSelectionGroup ==  null)
			return null;
		
		ArrayList<Integer> alTmp = new ArrayList<Integer>();
		for (Integer iCurrent : iAlSelectionGroup)
		{
			alTmp.add(iCurrent.intValue());
		}
		return alTmp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.selection.ISetSelection#getOptionalDataArray()
	 */
	public ArrayList<Integer> getOptionalDataArray() {

//		this.getReadToken();
//		int[] tmp = this.getStorageByDimAndIndex(0, 2).getArrayInt();
//		this.returnReadToken();
//		
//		return tmp;	
		// FIXME: copy this
		
		return iAlSelectionOptionalData;
	}
	
	
	/**
	 * The selection set will be filled 
	 * with arrays that are given as parameter.
	 * The selection event will be triggered
	 * with the unique ID parameter.
	 * 
	 * @param setSelection
	 * @param iUniqueTriggerID
	 * @param iArSelectionVertexId
	 * @param iArSelectionGroup
	 * @param iArNeighborVertices
	 */
	public void updateSelectionSet(int iUniqueTriggerID, 
			ArrayList<Integer> iAlSelectionVertexId,
			ArrayList<Integer> iAlSelectionGroup,
			ArrayList<Integer> iAlNeighborVertices) {
	
			// Update selection SET data.
			this.setAllSelectionDataArrays(
					iAlSelectionVertexId, iAlSelectionGroup, iAlNeighborVertices);
			
			updateSelectionSet(iUniqueTriggerID);
	}
	
	public void updateSelectionSet(int iUniqueTriggerID) {
		
		try {	
//			generalManager.logMsg(
//					this.getClass().getSimpleName() + 
//					": updateSelectionSet(): Set selection data and trigger update.",
//					LoggerType.VERBOSE );
			
	 		// Calls update with the ID of the view
	 		((EventPublisher)generalManager.
				getEventPublisher()).updateReceiver(generalManager.getViewGLCanvasManager().
							getItem(iUniqueTriggerID), this);
	 		
		} catch (Exception e)
		{
//			generalManager.logMsg(
//					this.getClass().getSimpleName() + 
//					": updateSelectionSet(): Problem during selection update triggering.",
//					LoggerType.MINOR_ERROR );
	
			e.printStackTrace();
		}
	}
	

	
	public void mergeSelection(ArrayList<Integer> iAlNewSelectionID,
			ArrayList<Integer> iAlNewSelectionGroup,
			ArrayList<Integer> iAlNewOptional)
	{
		
		if(iAlSelectionID == null)
		{
			iAlSelectionID = new ArrayList<Integer>();
			
			if (iAlNewSelectionGroup != null)
			{
				iAlSelectionGroup = new ArrayList<Integer>();
			}
			
			if (iAlNewOptional != null)
			{
				iAlSelectionGroup = new ArrayList<Integer>();
			}
				
			//setAllSelectionDataArrays(iAlNewSelectionID, iAlNewSelectionGroup, iAlSelectionOptionalData);
		}
		
		int iCount = 0;
		for(Integer iCurrent : iAlNewSelectionID)
		{
			
			if(iAlSelectionID.contains(iCurrent))
			{
				int iIndex = iAlSelectionID.indexOf(iCurrent);
				if(iAlNewSelectionGroup.get(iCount) == -1)
				{
					iAlSelectionID.remove(iIndex);
					if(iAlSelectionGroup != null)
						iAlSelectionGroup.remove(iIndex);
					if(iAlSelectionOptionalData != null)
						iAlSelectionOptionalData.remove(iIndex);
				}
				else
				{	
					if(iAlSelectionGroup != null)
					{
						int iBiggerSelection = Math.max(iAlNewSelectionGroup.get(iCount),
								iAlSelectionGroup.get(iIndex));
						
						iAlSelectionGroup.set(iIndex, iBiggerSelection);
					}
					if(iAlSelectionOptionalData != null)
						iAlSelectionOptionalData.set(iIndex, iAlNewOptional.get(iCount));
				}
			}
			else
			{
				iAlSelectionID.add(iCurrent);
				if(iAlSelectionGroup != null)
					iAlSelectionGroup.add(iAlNewSelectionGroup.get(iCount));
				if(iAlSelectionOptionalData != null)
					iAlSelectionOptionalData.add(iAlNewOptional.get(iCount));
			}
			iCount++;
		}
	}
	
	public void clearAllSelectionArrays() 
	{
		iAlSelectionID = null;
		iAlSelectionGroup = null;
		iAlSelectionOptionalData = null;
	}

	@Override
	public ManagerObjectType getBaseType() {

		// TODO Auto-generated method stub
		return null;
	}
}
