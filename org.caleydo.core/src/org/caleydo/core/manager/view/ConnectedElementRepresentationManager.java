package org.caleydo.core.manager.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.caleydo.core.data.view.rep.selection.SelectedElementRep;
import org.caleydo.core.manager.picking.ESelectionMode;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;

/**
 * <p>
 * Selection manager that manages selections and their
 * {@link SelectedElementRep}.
 * </p>
 * <p>
 * The manager is able to identify identical selections in different views.
 * Selections have selection representations. Selection representations store
 * their containing view and the x/y position in the view area.
 * </p>
 * <p>
 * The purpose of this manager is to make selections available to an external
 * instance that connects them, for example the
 * {@link AGLConnectionLineRenderer}
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ConnectedElementRepresentationManager
{

	HashMap<Integer, ArrayList<SelectedElementRep>> hashSelectedElementID2SelectedElementReps;

	/**
	 * Constructor.
	 * 
	 */
	protected ConnectedElementRepresentationManager()
	{
		hashSelectedElementID2SelectedElementReps = new HashMap<Integer, ArrayList<SelectedElementRep>>();
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
			case ADD_PICK:
				addSelection(iElementID, selectedElementRep);
				break;
			case REMOVE_PICK:
				removeSelection(iElementID, selectedElementRep);
				break;
			case REPLACE_PICK:
				clear();
				addSelection(iElementID, selectedElementRep);
				break;
			default:
				throw new IllegalArgumentException("No selection mode specified");
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
			hashSelectedElementID2SelectedElementReps.put(iElementID,
					new ArrayList<SelectedElementRep>());
		}

		hashSelectedElementID2SelectedElementReps.get(iElementID).add(selectedElementRep);
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
			hashSelectedElementID2SelectedElementReps.get(iElementID).remove(
					selectedElementRep);
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
	 * 
	 * @return a Set of IDs
	 */
	public Set<Integer> getAllSelectedElements()
	{

		return hashSelectedElementID2SelectedElementReps.keySet();
	}

	/**
	 * Get a representation of a particular element
	 * 
	 * @param iElementID
	 * @return
	 */
	public ArrayList<SelectedElementRep> getSelectedElementRepsByElementID(final int iElementID)
	{

		ArrayList<SelectedElementRep> tempList = hashSelectedElementID2SelectedElementReps
				.get(iElementID);

		if (tempList == null)
			throw new IllegalArgumentException(
					"SelectionManager: No representations for this element ID");
		return tempList;
	}

	/**
	 * Clear all selections and representations
	 */
	public void clear()
	{

		hashSelectedElementID2SelectedElementReps.clear();
	}

}
