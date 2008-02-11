package org.geneview.core.manager.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.geneview.core.data.view.rep.selection.SelectedElementRep;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.base.AAbstractManager;
import org.geneview.core.manager.type.ManagerType;
import org.geneview.util.graph.IGraph;

/**
 * 
 * Selection manager that manages selections of views.
 * The selection data itself is still stored in each view.
 * The manager is able to identify identical selections in different
 * views. The selections are held in a graph structure.
 * Selections have selection representations. Selection representations 
 * store their containing view and the x/y position in the view area.
 * 
 * @author Marc Streit
 * 
 * TODO: add method for removing elements and selectedElementReps.
 *
 */
public class SelectionManager  
extends AAbstractManager  {

	IGraph selectionGraph;
	
	HashMap<Integer, ArrayList<SelectedElementRep>> hashSelectedElementID2SelectedElementReps;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	protected SelectionManager(IGeneralManager generalManager) {

		super(generalManager,				
				IGeneralManager.iUniqueID_TypeOffset_Selection, 
				ManagerType.SELECTION_MANAGER);
		
		hashSelectedElementID2SelectedElementReps = 
			new HashMap<Integer, ArrayList<SelectedElementRep>>();
	}
	
	private void addSelection(final int iElementID) {
		
		hashSelectedElementID2SelectedElementReps.put(
				iElementID, new ArrayList<SelectedElementRep>());
	}
	
	public void addSelectionRep(final int iElementID, 
			final SelectedElementRep selectedElementRep) {
		
		if (!hashSelectedElementID2SelectedElementReps.containsKey(iElementID))
			addSelection(iElementID);
		
		hashSelectedElementID2SelectedElementReps.get(
				iElementID).add(selectedElementRep);
	}
	
	public Set<Integer> getAllSelectedElements() {
		
		return hashSelectedElementID2SelectedElementReps.keySet();
	}
	
	public ArrayList<SelectedElementRep> getSelectedElementRepsByElementID(
			final int iElementID) {
		
		return hashSelectedElementID2SelectedElementReps.get(iElementID);
	}
	
	public void clear() {
		
		hashSelectedElementID2SelectedElementReps.clear();
	}
}
