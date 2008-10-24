package org.caleydo.core.manager.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectedElementRep;
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

	HashMap<EIDType, HashMap<Integer, ArrayList<SelectedElementRep>>> hashIDTypes;

	/**
	 * Constructor.
	 * 
	 */
	protected ConnectedElementRepresentationManager()
	{
		hashIDTypes = new HashMap<EIDType, HashMap<Integer, ArrayList<SelectedElementRep>>>();
	}

	/**
	 * Modify a selection in a way specified by selectionMode
	 * 
	 * @param iElementID
	 * @param selectedElementRep
	 * @param selectionMode
	 */

	public void modifySelection(int iElementID, final SelectedElementRep selectedElementRep,
			final ESelectionMode selectionMode)
	{
		iElementID = 0;
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
	public void addSelection(int iElementID, final SelectedElementRep selectedElementRep)
	{
		HashMap<Integer, ArrayList<SelectedElementRep>> tmpHash = hashIDTypes
				.get(selectedElementRep.getIDType());
		if (tmpHash == null)
		{
			tmpHash = new HashMap<Integer, ArrayList<SelectedElementRep>>();
			hashIDTypes.put(selectedElementRep.getIDType(), tmpHash);
		}
		// FIXME temp hack
		iElementID = 0;
		if (!tmpHash.containsKey(iElementID))
		{
			tmpHash.put(iElementID, new ArrayList<SelectedElementRep>());
		}

		tmpHash.get(iElementID).add(selectedElementRep);
	}

	/**
	 * Remove a particular selection
	 * 
	 * @param iElementID
	 * @param selectedElementRep
	 */
	public void removeSelection(final int iElementID, SelectedElementRep selectedElementRep)
	{

		if (hashIDTypes.containsKey(iElementID))
		{
			hashIDTypes.get(iElementID).remove(selectedElementRep);
			hashIDTypes.remove(iElementID);
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
	 * Get a list of all occurring {@link EIDTypes}
	 * 
	 * @return a Set of EIDType
	 */
	public Set<EIDType> getOccuringIDTypes()
	{
		return hashIDTypes.keySet();
	}

	/**
	 * Get a list or IDs of all selected elements of a type
	 * 
	 * @return a Set of IDs
	 */
	public Set<Integer> getIDList(EIDType idType)
	{

		return hashIDTypes.get(idType).keySet();
	}

	/**
	 * Get a representation of a particular element
	 * 
	 * @param idType the type of the object to be connected (eg. gene
	 *            expression, clinical)
	 * @param iElementID the id of the object to be connected
	 * @return a list of the representations of the poings
	 */
	public ArrayList<SelectedElementRep> getSelectedElementRepsByElementID(EIDType idType,
			final int iElementID)
	{

		ArrayList<SelectedElementRep> tempList = hashIDTypes.get(idType).get(iElementID);

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
		hashIDTypes.clear();
	}

}
