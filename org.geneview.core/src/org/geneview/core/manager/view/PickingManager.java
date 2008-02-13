package org.geneview.core.manager.view;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.HTMLReader.HiddenAction;

import org.geneview.core.data.AUniqueManagedObject;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.base.AAbstractManager;
import org.geneview.core.manager.type.ManagerType;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;

/**
 * 
 * @author Alexander Lex
 * 
 * Manages Picking IDs in a system-wide unique way and stores them locally
 * 
 * Do NOT store picking ids in classes that use this class
 * 
 * Syntax for Picking IDs
 * 
 * 2 last digits: type
 * rest: counter
 * 
 * C*TT
 *
 */

public class PickingManager extends AAbstractManager 
{	
	private HashMap<Integer, HashMap<Integer, Integer>> hashSignatureToPickingIDHashMap;
	private HashMap<Integer, HashMap<Integer, Integer>> hashSignatureToExternalIDHashMap;
	private int iIDCounter;
	private HashMap<Integer, ArrayList<Integer>> hashSignatureToHitList;
	
	
	/**
	 * Constructor. 
	 * 
	 * @param setGeneralManager
	 */
	public PickingManager(IGeneralManager refGeneralManager) 
	{

		super(refGeneralManager, 
				IGeneralManager.iUniqueID_TypeOffset_PickingID, 
				ManagerType.PICKING_MANAGER);
		
		iIDCounter = 0;
		hashSignatureToHitList = new HashMap<Integer, ArrayList<Integer>>();
		hashSignatureToPickingIDHashMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		hashSignatureToExternalIDHashMap = new HashMap<Integer, HashMap<Integer, Integer>>();
	}
	
	/**
	 * Returns a unique picking ID based on the ViewID and a special type
	 * and stores it in a hash map with the external id
	 * 
	 * DO NOT store picking id's locally
	 *  
	 * @param iViewID the ID of the calling view, has to have 5 digits max
	 * @param iType a type which is part of the picking ID, 
	 * has to be between 0 and 99 
	 * @return 
	 */
	public int getPickingID(AUniqueManagedObject uniqueManagedObject, 
							int iType, 
							int iExternalID)
	{
		checkType(iType);		
		
		int iViewID = uniqueManagedObject.getId();
		
		checkViewID(iViewID);
		
		int iSignature = getSignature(uniqueManagedObject, iType);
				
		
		if(hashSignatureToPickingIDHashMap.get(iSignature) == null)
		{
			hashSignatureToPickingIDHashMap.put(iSignature, new HashMap<Integer, Integer>());			
			hashSignatureToExternalIDHashMap.put(iSignature, new HashMap<Integer, Integer>());
			
		}		
		else if(hashSignatureToExternalIDHashMap.get(iSignature).get(iExternalID) != null)
		{
			return hashSignatureToExternalIDHashMap.get(iSignature).get(iExternalID);
		}	
		
		int iPickingID = calculateID(iViewID, iType);
		hashSignatureToPickingIDHashMap.get(iSignature).put(iPickingID, iExternalID);
		hashSignatureToExternalIDHashMap.get(iSignature).put(iExternalID, iPickingID);
			
		return iPickingID;		
	}
	
	public void processHits(AUniqueManagedObject uniqueManagedObject, int iHitCount, int[] iArPickingBuffer, ESelectionMode myMode)
	{	
		processHits(uniqueManagedObject, iHitCount, iArPickingBuffer, myMode, false);
	}
	
	/**
	 * Extracts the nearest hit from the provided iArPickingBuffer
	 * Stores it internally
	 * Can process only one hit at at time at the moment
	 * 
	 * @param iHitCount
	 * @param iArPickingBuffer
	 */
	public void processHits(AUniqueManagedObject uniqueManagedObject, 
							int iHitCount, 
							int[] iArPickingBuffer, 
							ESelectionMode myMode, 
							boolean bIsMaster)
	{			
		 int iPickingBufferCounter = 0;	
		
		 ArrayList<Integer> iAlPickedObjectId = new ArrayList<Integer>(2);
		
		// Only pick object that is nearest
		int iMinimumZValue = Integer.MAX_VALUE;
		int iNumberOfNames = 0;
		int iNearestObjectIndex = 0;
		for (int iCount = 0; iCount < iHitCount; iCount++)
		{			
			//iPickingBufferCounter++;
			// Check if object is nearer than previous objects
			if (iArPickingBuffer[iPickingBufferCounter+1] < iMinimumZValue)
			{
				// first element is number of names on name stack				
				// second element is min Z Value
				iMinimumZValue = iArPickingBuffer[iPickingBufferCounter+1];
				iNearestObjectIndex = iPickingBufferCounter;
				// third element is max Z Value
				// fourth element is name of lowest name on stack
				//iAlPickedObjectId.add(iArPickingBuffer[iPickingBufferCounter+3]);
			}
			iPickingBufferCounter = iPickingBufferCounter + 3 + iArPickingBuffer[iPickingBufferCounter];
			
		}		
		
		iNumberOfNames = iArPickingBuffer[iNearestObjectIndex];
		
		for (int iNameCount = 0; iNameCount < iNumberOfNames; iNameCount++)
		{
			iAlPickedObjectId.add(iArPickingBuffer[iNearestObjectIndex + 3 + iNameCount]);
		}
		
		//iPickingBufferCounter += iNumberOfNames;
		if(iAlPickedObjectId.size() > 0)
		{
			processPicks(iAlPickedObjectId, uniqueManagedObject, myMode, bIsMaster);
		}
		//return iPickedObjectId;		
	}
	
	/**
	 * Returns the hit for a particular view and type
	 * 
	 * @param iViewID
	 * @param iType
	 * @return null if no Hits, else the ArrayList<Integer> with the hits
	 */	
	public ArrayList<Integer> getHits(AUniqueManagedObject uniqueManagedObject, int iType)
	{
		checkType(iType);
		int iViewID = uniqueManagedObject.getId();
		checkViewID(iViewID);
		
		int iSignature = getSignature(iViewID, iType);		
		
		if (hashSignatureToHitList.get(iSignature) == null)
			return null;
		else
			return hashSignatureToHitList.get(iSignature);
	}
	
	/**
	 * Returns the external ID (the id with which you initialized getPickingID()) when you provide the picking ID
	 * 
	 * @param uniqueManagedObject
	 * @param iPickingID the picking ID
	 * @return the ID, null if no entry for that pickingID
	 */
	public int getExternalIDFromPickingID(AUniqueManagedObject uniqueManagedObject, int iPickingID)
	{
		//TODO: exceptions
		int iSignature = getSignatureFromPickingID(iPickingID, uniqueManagedObject);
		return hashSignatureToPickingIDHashMap.get(iSignature).get(iPickingID);
	}
	
	/**
	 * Returns the external ID (the id with which you initialized getPickingID()) when you provide the hit count, 
	 * meaning the n-th element in the hit list. 
	 * @param uniqueManagedObject
	 * @param iType the type, >= 0, <100
	 * @param iHitCount
	 * @return the ID, null if no entry for that hit count
	 */
	public int getExternalIDFromHitCount(AUniqueManagedObject uniqueManagedObject, int iType, int iHitCount)
	{
		//TODO: exceptions
		int iSignature = getSignature(uniqueManagedObject, iType);		
		int iPickingID = hashSignatureToHitList.get(iSignature).get(iHitCount);		
		return  hashSignatureToPickingIDHashMap.get(iSignature).get(iPickingID);
		 
	}
	
	/**
	 * Removes the picking IDs form internal storage and from the hit list
	 * You should do that when you close a view, remember to do it for all types
	 * 
	 * @param uniqueManagedObject
	 * @param iType
	 */
	public void flushPickingIDs(AUniqueManagedObject uniqueManagedObject, int iType)
	{
		int iSignature = getSignature(uniqueManagedObject, iType);
		hashSignatureToExternalIDHashMap.remove(iSignature);
		hashSignatureToHitList.remove(iSignature);
		hashSignatureToPickingIDHashMap.remove(iSignature);
	}
	
	/**
	 * Flush a particular hit list
	 * 
	 * @param iViewID
	 * @param iType
	 */
	public void flushHits(AUniqueManagedObject uniqueManagedObject, int iType)
	{
		checkType(iType);
		int iViewID = uniqueManagedObject.getId();
		checkViewID(iViewID);
		
		if (hashSignatureToHitList.get(getSignature(iViewID, iType)) != null)
		{
			hashSignatureToHitList.get(getSignature(iViewID, iType)).clear();
		}			
	}
			
	private int calculateID(int iViewID, int iType)
	{		
		iIDCounter++;
		return (iIDCounter * 100 + iType);		
	}
	
	private void processPicks(ArrayList<Integer> alPickingIDs, AUniqueManagedObject uniqueManagedObject, ESelectionMode myMode, boolean bIsMaster)
	{
		
		int iPickingID = 0;
		int iSignature = 0;
		int iOrigianlPickingID = 0;
		for (int iResultCounter = 0; iResultCounter < alPickingIDs.size(); iResultCounter++)
		{			
			iPickingID = alPickingIDs.get(iResultCounter);
		
			if (iResultCounter == 0)
			{
				iSignature = getSignatureFromPickingID(iPickingID,
					uniqueManagedObject);
				iOrigianlPickingID = iPickingID;
			}
			if (iResultCounter == 1 && bIsMaster)
			{				
				int iViewUnderInteractionID = hashSignatureToPickingIDHashMap.get(iSignature).get(iOrigianlPickingID);
				iSignature = getSignatureFromPickingID(iPickingID, iViewUnderInteractionID);
				System.out.println("Should be the name of a view: " + iViewUnderInteractionID);
				
			}
			
			
			if (hashSignatureToHitList.get(iSignature) == null)
			{
				if (myMode == ESelectionMode.RemovePick)
				{
					return;
				} else
				{
					ArrayList<Integer> tempList = new ArrayList<Integer>();
					tempList.add(iPickingID);
					hashSignatureToHitList.put(iSignature, tempList);
				}
			} else
			{
				if (myMode == ESelectionMode.AddPick)
				{
					// ArrayList<Integer> listTemp =
					hashSignatureToHitList.get(iSignature).add(iPickingID);
					// listTemp.add(iSignature);
				}
				if (myMode == ESelectionMode.ReplacePick)
				{
					hashSignatureToHitList.get(iSignature).clear();
					hashSignatureToHitList.get(iSignature).add(iPickingID);
				}
				if (myMode == ESelectionMode.RemovePick)
				{
					ArrayList<Integer> alTempList = hashSignatureToHitList
							.get(iSignature);
					alTempList.remove(iSignature);
				}
			}
		
			
		}
	}
		
	private int getSignature(int iViewID, int iType)
	{
		return (iViewID * 100 + iType);
	}
	
	private int getSignature(AUniqueManagedObject uniqueManagedObject, int iType)
	{
		return getSignature(uniqueManagedObject.getId(), iType);
	}
	
	private int getSignatureFromPickingID(int iPickingID, AUniqueManagedObject uniqueManagedObject)
	{
		int iViewID = uniqueManagedObject.getId();
		return getSignatureFromPickingID(iPickingID, iViewID);
		
	}
	
	private int getSignatureFromPickingID(int iPickingID, int iViewID)
	{
		int iTemp = iPickingID / 100;
		int iType = iPickingID - iTemp * 100;
		
		return (getSignature(iViewID, iType));
	}
	
	private void checkViewID(int iViewID)
	{
		if (iViewID > 99999 || iViewID < 10000)
		{
			throw new GeneViewRuntimeException(
					"PickingManager: The view id has to have exactly 5 digits",
					GeneViewRuntimeExceptionType.VIEW);
		}
	}
	
	private void checkType(int iType)
	{
		if (iType > 99 || iType < 0)
		{
			throw new GeneViewRuntimeException(
					"PickingManager: Type has to be larger then or exactly 0 and less than 100",
					GeneViewRuntimeExceptionType.VIEW);
		}
	}
	
}
