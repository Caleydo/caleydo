package org.caleydo.core.manager.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectedElementRep;
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
	 * Add a selection to a specific tree. The data type is determined by the
	 * selectedElementRep, the connection id has to be specified manually
	 * 
	 * @param iConnectionID the connection ID - one connection id per connection
	 *            line tree
	 * @param selectedElementRep the selected element rep associated with the
	 *            tree specified
	 */
	public void addSelection(int iConnectionID, final SelectedElementRep selectedElementRep)
	{
		HashMap<Integer, ArrayList<SelectedElementRep>> tmpHash = hashIDTypes
				.get(selectedElementRep.getIDType());
		if (tmpHash == null)
		{
			tmpHash = new HashMap<Integer, ArrayList<SelectedElementRep>>();
			hashIDTypes.put(selectedElementRep.getIDType(), tmpHash);
		}

		if (!tmpHash.containsKey(iConnectionID))
		{
			tmpHash.put(iConnectionID, new ArrayList<SelectedElementRep>());
		}

		tmpHash.get(iConnectionID).add(selectedElementRep);
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
		clear(selectedElementRep.getIDType());
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
	 * @param iDType the type of the object to be connected (e.g. gene
	 *            expression, clinical)
	 * @param iElementID the id of the object to be connected
	 * @return a list of the representations of the points
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
	public void clearAll()
	{
		hashIDTypes.clear();
	}

	/**
	 * Clear all selections of a given type
	 */
	public void clear(EIDType eIDType)
	{
		if (!hashIDTypes.containsKey(eIDType))
			return;

		HashMap<Integer, ArrayList<SelectedElementRep>> tmp = hashIDTypes.get(eIDType);
		tmp.clear();
	}

	@Deprecated 
	public void clearByView(EIDType idType, int iViewID)
	{
		HashMap<Integer, ArrayList<SelectedElementRep>> hashReps = hashIDTypes.get(idType);
		if (hashReps == null)
			return;
		for (int iElementID : hashReps.keySet())
		{
			ArrayList<SelectedElementRep> alRep = hashReps.get(iElementID);

			Iterator<SelectedElementRep> iterator = alRep.iterator();
			while (iterator.hasNext())
			{
				if (iterator.next().getContainingViewID() == iViewID)
					iterator.remove();
			}
		}
	}

	public void clearByConnectionID(EIDType idType, int iConnectionID)
	{
		hashIDTypes.get(idType).remove(iConnectionID);
	}

}
