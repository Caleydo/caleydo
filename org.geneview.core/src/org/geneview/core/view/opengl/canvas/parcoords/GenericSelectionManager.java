package org.geneview.core.view.opengl.canvas.parcoords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;


/**
 * Manages selections generically. You have to define one "normal" type in the constructor.
 * 
 * @author Alexander Lex
 *
 */
public class GenericSelectionManager 
{

	//EnumMap<Enum, V>
	//HashMap<SelectionEnumeration, Integer> hash;
	private HashMap<String, HashMap<Integer, Boolean>> hashSelectionTypes;
	private String sNormalType;
	private int iNumberOfElements = 0;
	private ArrayList<String> selectionTypes;
	
	public GenericSelectionManager(ArrayList<String> selectionTypes, String normalType)//SelectionEnumeration selectionEnumeration)
	{
		this.selectionTypes = selectionTypes;
		this.sNormalType = normalType;
		
		hashSelectionTypes = new HashMap<String, HashMap<Integer,Boolean>>(); 
		
		for (String sType : selectionTypes)
		{
			hashSelectionTypes.put(sType, new HashMap<Integer, Boolean>());
		}		
		
	}
	
	public void initialAdd(int iElementID)
	{
		iNumberOfElements++;
		hashSelectionTypes.get(sNormalType).put(iElementID, true);
	}
	
	/**
	 * Clears all elements and sets the element counter to 0
	 */
	public void resetSelectionManager()
	{
		hashSelectionTypes.clear();
		for (String sType : selectionTypes)
		{
			hashSelectionTypes.put(sType, new HashMap<Integer, Boolean>());
		}	
		iNumberOfElements = 0;
	}
	
	/**
	 * Clears all selections
	 * all selections are written into the "normal" type 
	 */
	public void clearSelections()
	{
		for (String sType : selectionTypes)
		{
			if (sType == sNormalType)
				continue;
			clearSelection(sType);
					
		}
	
	}
	
	/**
	 * Clear one specific selction type
	 * The elements are added to the "normal" type
	 * 
	 * @param sSelectionType the selection type to be cleared
	 */
	public void clearSelection(String sSelectionType)
	{	
		if(sSelectionType == sNormalType)
			throw new GeneViewRuntimeException(
					"SelectionManager: cannot reset selections of normal selection",
					GeneViewRuntimeExceptionType.VIEW);
		
		hashSelectionTypes.get(sNormalType).putAll(hashSelectionTypes.get(sSelectionType));
		hashSelectionTypes.get(sSelectionType).clear();
	}
	
	/**
	 * Returns all elements that are in a specific selection type
	 * 
	 * @param sSelectionType
	 * @return
	 */
	public Set<Integer> getElements(String sSelectionType)
	{
		return hashSelectionTypes.get(sSelectionType).keySet();
	}
	
	public void addToType(String sSelectionType, int iElementID)
	{
		if(hashSelectionTypes.get(sSelectionType).get(iElementID) != null)
			return;
		
		for (String sType : selectionTypes)
		{
			if (sType == sSelectionType)
				continue;
			
			if (hashSelectionTypes.get(sType).get(iElementID) != null)
			{
				hashSelectionTypes.get(sType).remove(iElementID);
				hashSelectionTypes.get(sSelectionType).put(iElementID, true);
				//return;
			}		
		}
		
//		throw new GeneViewRuntimeException(
//				"SelectionManager: element to be removed does not exist",
//				GeneViewRuntimeExceptionType.VIEW);
//		
	}
	
	public void removeFromType(String sSelectionType, int iElementID)
	{
		if (sSelectionType == sNormalType)
			throw new GeneViewRuntimeException(
					"SelectionManager: cannot remove from normal selection",
					GeneViewRuntimeExceptionType.VIEW);
		
		if (hashSelectionTypes.get(sSelectionType).remove(iElementID) != null)
			hashSelectionTypes.get(sNormalType).put(iElementID, true);		
	}
	
	public int getNumberOfElements()
	{
		return iNumberOfElements;
	}
	
	public boolean checkStatus(String sSelectionType, int iElementID)
	{
		if (hashSelectionTypes.get(sSelectionType).get(iElementID) != null)
			return true;
		else
			return false;
	}
	

	

	
	
}
