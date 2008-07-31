package org.caleydo.core.view.opengl.util.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Manages selections generically. You have to define one "normal" type in the
 * constructor.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GenericSelectionManager
{

	// EnumMap<Enum, V>
	// HashMap<SelectionEnumeration, Integer> hash;
	private HashMap<EViewInternalSelectionType, HashMap<Integer, Boolean>> hashSelectionTypes;

	private EViewInternalSelectionType eNormalType;

	private int iNumberOfElements = 0;

	private ArrayList<EViewInternalSelectionType> selectionTypes;

	/**
	 * Constructor.
	 * 
	 * @param selectionTypes
	 * @param normalType
	 */
	public GenericSelectionManager(final ArrayList<EViewInternalSelectionType> selectionTypes,
			final EViewInternalSelectionType normalType)// SelectionEnumeration
	// selectionEnumeration)
	{

		this.selectionTypes = selectionTypes;
		this.eNormalType = normalType;

		hashSelectionTypes = new HashMap<EViewInternalSelectionType, HashMap<Integer, Boolean>>();

		for (EViewInternalSelectionType eType : selectionTypes)
		{
			hashSelectionTypes.put(eType, new HashMap<Integer, Boolean>());
		}
	}

	public void initialAdd(int iElementID)
	{

		iNumberOfElements++;
		hashSelectionTypes.get(eNormalType).put(iElementID, true);
	}

	/**
	 * Clears all elements and sets the element counter to 0
	 */
	public void resetSelectionManager()
	{

		hashSelectionTypes.clear();
		for (EViewInternalSelectionType eType : selectionTypes)
		{
			hashSelectionTypes.put(eType, new HashMap<Integer, Boolean>());
		}
		iNumberOfElements = 0;
	}

	/**
	 * Clears all selections all selections are written into the "normal" type
	 */
	public void clearSelections()
	{

		for (EViewInternalSelectionType eType : selectionTypes)
		{
			if (eType == eNormalType)
				continue;
			clearSelection(eType);
		}
	}

	/**
	 * Clear one specific selection type The elements are added to the "normal"
	 * type
	 * 
	 * @param sSelectionType the selection type to be cleared
	 */
	public void clearSelection(EViewInternalSelectionType eSelectionType)
	{

		if (eSelectionType == eNormalType)
			throw new CaleydoRuntimeException(
					"SelectionManager: cannot reset selections of normal selection",
					CaleydoRuntimeExceptionType.VIEW);

		hashSelectionTypes.get(eNormalType).putAll(hashSelectionTypes.get(eSelectionType));
		hashSelectionTypes.get(eSelectionType).clear();
	}

	/**
	 * Returns all elements that are in a specific selection type
	 * 
	 * @param sSelectionType
	 * @return
	 */
	public Set<Integer> getElements(EViewInternalSelectionType eSelectionType)
	{

		return hashSelectionTypes.get(eSelectionType).keySet();
	}

	public void addToType(EViewInternalSelectionType eSelectionType, int iElementID)
	{

		if (hashSelectionTypes.get(eSelectionType).get(iElementID) != null)
			return;

		for (EViewInternalSelectionType eType : selectionTypes)
		{
			if (eType == eSelectionType)
				continue;

			if (hashSelectionTypes.get(eType).get(iElementID) != null)
			{
				hashSelectionTypes.get(eType).remove(iElementID);
				hashSelectionTypes.get(eSelectionType).put(iElementID, true);
				// return;
			}
		}

		// throw new CaleydoRuntimeException(
		// "SelectionManager: element to be removed does not exist",
		// CaleydoRuntimeExceptionType.VIEW);
		//		
	}

	public void removeFromType(EViewInternalSelectionType eSelectionType, int iElementID)
	{

		if (eSelectionType == eNormalType)
			throw new CaleydoRuntimeException(
					"SelectionManager: cannot remove from normal selection",
					CaleydoRuntimeExceptionType.VIEW);

		if (hashSelectionTypes.get(eSelectionType).remove(iElementID) != null)
			hashSelectionTypes.get(eNormalType).put(iElementID, true);
	}

	public int getNumberOfElements()
	{

		return iNumberOfElements;
	}

	public boolean checkStatus(EViewInternalSelectionType eSelectionType, int iElementID)
	{

		if (hashSelectionTypes.get(eSelectionType).get(iElementID) != null)
			return true;
		else
			return false;
	}
}
