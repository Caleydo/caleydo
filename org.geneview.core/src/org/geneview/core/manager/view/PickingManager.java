package org.geneview.core.manager.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.base.AAbstractManager;
import org.geneview.core.manager.type.ManagerType;



/**
 * 
 * @author Alexander Lex
 * 
 * Manage Picking IDs in a system-wide unique way
 * 
 * Syntax for Picking IDs
 * 
 * 2 last digits: type
 * digits 3-8: ViewID
 * rest: counter
 * 
 * C*VVVVVTT
 *
 */

public class PickingManager extends AAbstractManager 
{
	
	private HashMap<Integer, Integer> hashSignatureToCounter;
	
	private HashMap<Integer, ArrayList<Integer>> hashSignatureToHitList;

	public PickingManager(IGeneralManager setGeneralManager) 
	{

		super(setGeneralManager, IGeneralManager.iUniqueID_TypeOffset_PickingID, ManagerType.PICKING_MANAGER);
		
		hashSignatureToCounter = new HashMap<Integer, Integer>();
		hashSignatureToHitList = new HashMap<Integer, ArrayList<Integer>>();
		
	}
	
	/**
	 * Returns a unique picking ID based on the ViewID and a special type
	 *  
	 * @param iViewID the ID of the calling view, has to have 5 digits max
	 * @param iType a type which is part of the picking ID, 
	 * has to be between 0 and 99 
	 * @return 
	 */
	public int getPickingID(int iViewID, int iType)
	{
		int iSignature = iViewID * 100 + iType; 
		
		if(hashSignatureToCounter.get(iSignature) == null)
		{
			hashSignatureToCounter.put(iSignature, 1);
			return calculateID(iViewID, iType, 1);
		}
		else
		{
			hashSignatureToCounter.put(iSignature, (hashSignatureToCounter.get(iSignature) + 1));
			return calculateID(iViewID, iType, hashSignatureToCounter.get(iSignature));
		}
		
	}
	
	/**
	 * Extracts the nearest hit from the provided iArPickingBuffer
	 * Stores it internally
	 * 
	 * @param iHitCount
	 * @param iArPickingBuffer
	 */
	public void calculateNearestHit(int iHitCount, int[] iArPickingBuffer)
	{
		int iPickingBufferCounter = 0;
	
		
		int iPickedObjectId =0;
		
		// Only pick object that is nearest
		int iMinimumZValue = Integer.MAX_VALUE;
		for (int iCount = 0; iCount < iHitCount; iCount++)
		{
			//iPickingBufferCounter++;
			// Check if object is nearer than previous objects
			if (iArPickingBuffer[iPickingBufferCounter+1] < iMinimumZValue)
			{
				// first element is number of names on name stack				
				// second element is min Z Value
				iMinimumZValue = iArPickingBuffer[iPickingBufferCounter+1];
				// third element is max Z Value
				// fourth element is name of lowest name on stack
				iPickedObjectId = iArPickingBuffer[iPickingBufferCounter+3];
			}
			iPickingBufferCounter += 4 ;
		}		
		if(iPickedObjectId != 0)
			addPick(iPickedObjectId);
		
		//return iPickedObjectId;		
	}
	
	/**
	 * Returns the hit for a particular view and type
	 * 
	 * @param iViewID
	 * @param iType
	 * @return null if no Hits, else the ArrayList<Integer> with the hits
	 */	
	public ArrayList<Integer> getHits(int iViewID, int iType)
	{
		int iSignature = getSignature(iViewID, iType);		
		
		if (hashSignatureToHitList.get(iSignature) == null)
			return null;
		else
			return hashSignatureToHitList.get(iSignature);
	}
	
	/**
	 * Flush a particular hit list
	 * 
	 * @param iViewID
	 * @param iType
	 */
	public void flushHits(int iViewID, int iType)
	{
		if (hashSignatureToHitList.get(getSignature(iViewID, iType)) != null)
		{
			hashSignatureToHitList.get(getSignature(iViewID, iType)).clear();
		}
			
	}
			
	private int calculateID(int iViewID, int iType, int iCount)
	{		
		return (iCount * 10000000 + iViewID * 100 + iType);		
	}
	
	private void addPick(int iPickingID)
	{
		int iSignature = getSignature(iPickingID);
		
		if (hashSignatureToHitList.get(iSignature) == null)
		{
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			tempList.add(iPickingID);
			hashSignatureToHitList.put(iSignature, tempList);
		}
		else
		{
			//ArrayList<Integer> listTemp =
			hashSignatureToHitList.get(iSignature).add(iPickingID);
			//listTemp.add(iSignature);
		}		
	}
	
	private int getSignature(int iPickingID)	
	{
		int iCounter = iPickingID / 10000000;

		int iSignature = iPickingID - iCounter * 10000000;
		
		return iSignature;
	}
	
	private int getSignature(int iViewID, int iType)
	{
		return (iViewID * 100 + iType);
	}
	
}
