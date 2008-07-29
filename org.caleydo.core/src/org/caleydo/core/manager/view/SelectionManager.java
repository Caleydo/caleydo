package org.caleydo.core.manager.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.view.rep.selection.SelectedElementRep;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.EManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * 
 * Selection manager that manages selections of views.
 * The selection data itself is still stored in each view.
 * The manager is able to identify identical selections in different
 * views. 
 * 
 * Selections have selection representations. Selection representations 
 * store their containing view and the x/y position in the view area.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 *
 */
public class SelectionManager  
extends AManager  {

	HashMap<Integer, ArrayList<SelectedElementRep>> hashSelectedElementID2SelectedElementReps;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	protected SelectionManager(IGeneralManager generalManager) {

		super(generalManager,				
				IGeneralManager.iUniqueID_TypeOffset_Selection, 
				EManagerType.SELECTION_MANAGER);
		
		hashSelectedElementID2SelectedElementReps = 
			new HashMap<Integer, ArrayList<SelectedElementRep>>();
	}
	
	/**
	 * Modify a selection in a way specified by selectionMode
	 * 
	 * @param iElementID
	 * @param selectedElementRep
	 * @param selectionMode
	 */
	
	public void modifySelection(final int iElementID, 
			final SelectedElementRep selectedElementRep, final ESelectionMode selectionMode) 
	{
		switch (selectionMode)
		{
		case AddPick:			
			addSelection(iElementID, selectedElementRep);
			break;
		case RemovePick:			
			removeSelection(iElementID, selectedElementRep);			
			break;
		case ReplacePick:
			clear();
			addSelection(iElementID, selectedElementRep);			
			break;			
		default:
			throw new CaleydoRuntimeException("No selection mode specified",  CaleydoRuntimeExceptionType.MANAGER);			
		}		
	}
	
	/**
	 * Add a selection
	 * 
	 * @param iElementID
	 * @param selectedElementRep
	 */
	public void addSelection(final int iElementID, final SelectedElementRep selectedElementRep) 
	{
		if (!hashSelectedElementID2SelectedElementReps.containsKey(iElementID))
		{	
			hashSelectedElementID2SelectedElementReps.put(
				iElementID, new ArrayList<SelectedElementRep>());
		}
		
		hashSelectedElementID2SelectedElementReps.get(
			iElementID).add(selectedElementRep);		
	}
	
	/** 
	 * Remove a particular selection
	 * 
	 * @param iElementID
	 * @param selectedElementRep
	 */
	public void removeSelection(final int iElementID, SelectedElementRep selectedElementRep)
	{
		if (hashSelectedElementID2SelectedElementReps.containsKey(iElementID))
		{
			hashSelectedElementID2SelectedElementReps.get(
				iElementID).remove(selectedElementRep);
			hashSelectedElementID2SelectedElementReps.remove(iElementID);
		}
	}
	
	/**
	 * Replace all selections with new selection
	 * 
	 * @param iElementID
	 * @param selectedElementRep
	 */
	public void replaceSelection(final int iElementID, SelectedElementRep selectedElementRep)
	{
		clear();
		addSelection(iElementID, selectedElementRep);
	}
	
	/**
	 * Get all selected elements
	 * @return a Set of IDs
	 */
	public Set<Integer> getAllSelectedElements() 
	{
		
		return hashSelectedElementID2SelectedElementReps.keySet();
	}
	
	/**
	 * Get a representation of a particular element
	 * @param iElementID
	 * @return
	 */
	public ArrayList<SelectedElementRep> getSelectedElementRepsByElementID(
			final int iElementID) 
	{
		ArrayList<SelectedElementRep> tempList = hashSelectedElementID2SelectedElementReps.get(iElementID);
		
		if(tempList == null)
			throw new CaleydoRuntimeException(
					"SelectionManager: No representations for this element ID", CaleydoRuntimeExceptionType.MANAGER);		
		return tempList;
	}
	
	/**
	 * Clear all selections and representations
	 */
	public void clear() 
	{		
		hashSelectedElementID2SelectedElementReps.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#getItem(int)
	 */
	public Object getItem(int itemId) {

		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#hasItem(int)
	 */
	public boolean hasItem(int itemId) {

		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#registerItem(java.lang.Object, int, org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public boolean registerItem(Object registerItem, int itemId) {

		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#size()
	 */
	public int size() {

		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#unregisterItem(int, org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public boolean unregisterItem(int itemId) {

		// TODO Auto-generated method stub
		return false;
	}	
}
